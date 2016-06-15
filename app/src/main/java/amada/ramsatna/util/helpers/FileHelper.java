package amada.ramsatna.util.helpers;


import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import com.j256.ormlite.dao.Dao;

import amada.ramsatna.model.Favorites;
import amada.ramsatna.model.Version;
import amada.ramsatna.model.WordModel;
import amada.ramsatna.util.Constants;
import amada.ramsatna.views.MainActivity;

/**
 * Created by Hamza on 3/04/2016.
 * A utility class for parsing the text file.
 */
public class FileHelper {


    private static final String ROW_DELIMITER = "<-r->";
    private static final String FIELDS_DELIMITER = "<-f->";
    private final String TAG = getClass().getSimpleName();
    private String[] arabicLetter = Constants.LETTERS;


    private DatabaseHelper databaseHelper = null;


    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(MainActivity.getContext(), DatabaseHelper.class);
        }
        return databaseHelper;
    }


    /**
     * Takes the input stream of the file downloaded from the API as an input and parses the data.
     * Splits the data into rows and fields using appropriate delimiters stores it in a Dictionary Object.
     * Returns a list of Dictionary objects with the all the data.
     *
     * @param is
     * @return
     */

    public ArrayList<WordModel> importData(InputStream is) {
        ArrayList<WordModel> words = new ArrayList<>();

        Scanner scan = new Scanner(is);
        scan.useDelimiter(ROW_DELIMITER);

        while (scan.hasNext()) {

            String logicLine = scan.next();
            String[] fields = logicLine.trim().split(FIELDS_DELIMITER);

            if (validateWord(fields[3])) {

                WordModel word = new WordModel();
                word.setLetter(fields[0].trim());
                word.setWord(fields[1].trim());
                word.setMeaning(fields[2]);
                word.setSearch_word(fields[3]);
                word.setRecord_id(fields[4]);
                word.setHas_audio(fields[5]);


                try {
                    final Dao<Favorites, Integer> favDao = getHelper().getFavoritesDao();
                    Favorites fav = favDao.queryForId(Integer.parseInt(word.getRecord_id()));
                    if (fav != null) {
                        word.setIsFavorite(true);
                    } else {
                        word.setIsFavorite(false);
                    }
                    final Dao<WordModel, Integer> wordDao = getHelper().getWordsDao();
                    Version version = new Version();
                    version.setSize(words.size());
                    words.add(word);
                    wordDao.createOrUpdate(word);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                if(word.getHas_audio().equals("1")){
                    Log.d(TAG, "importData: " + word.getWord() + " " + word.getRecord_id());
                }

            }

        }


        return words;
    }

    /**
     * Checks weather the word being parsed starts with a valid letter. Skipped if not valid.
     *
     * @param word
     * @return
     */
    public boolean validateWord(String word) {


        char firstLetter = word.trim().charAt(0);
        for (int i = 0; i < arabicLetter.length; i++) {
            if (arabicLetter[i].equals(Character.toString(firstLetter))) {
                return true;
            }
        }
        return false;
    }


}
