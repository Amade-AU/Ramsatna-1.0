package amada.ramsatna.model;

import java.util.ArrayList;

/**
 * Created by Hamza on 6/04/2016.
 */
public class Dictionary {

    /**
     * Takes an List of WordModels and a search word as parameters.
     * Finds the word from the list and returns back its meaning.
     *
     * @param words_list
     * @param word
     * @return
     */

    public String getMeaning(ArrayList<WordModel> words_list, String word) {
        for (WordModel list : words_list) {
            if (list.getWord().equals(word)) {
                return list.getMeaning();
            }
        }

        return null;
    }

    public WordModel getWord(ArrayList<WordModel> words_list, String word) {

        for (WordModel list : words_list) {
            if (list.getWord().equals(word)) {
                return list;
            }
        }
        return null;
    }
}
