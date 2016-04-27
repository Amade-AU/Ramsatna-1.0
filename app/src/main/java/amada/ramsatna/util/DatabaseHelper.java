package amada.ramsatna.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import amada.ramsatna.R;
import amada.ramsatna.model.Favorites;
import amada.ramsatna.model.WordModel;

/**
 * Created by Hamza on 19/04/2016.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "dictionary";
    private static final int DATABASE_VERSION = 1;

    private Dao<WordModel, Integer> wordDao;
    private Dao<Favorites, Integer> favoritesDao;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);

    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {

        try {
            TableUtils.createTableIfNotExists(connectionSource, WordModel.class);
            TableUtils.createTableIfNotExists(connectionSource, Favorites.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

        try {
            TableUtils.dropTable(connectionSource, WordModel.class, false);
            TableUtils.dropTable(connectionSource, Favorites.class, false);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao<WordModel, Integer> getWordsDao() throws SQLException {

        if (wordDao == null) {
            wordDao = getDao(WordModel.class);
        }
        return wordDao;

    }

    public Dao<Favorites, Integer> getFavoritesDao() throws SQLException {

        if (favoritesDao == null) {
            favoritesDao = getDao(Favorites.class);
        }
        return favoritesDao;
    }
}
