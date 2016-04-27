package amada.ramsatna.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import amada.ramsatna.R;
import amada.ramsatna.model.Favorites;
import amada.ramsatna.model.WordModel;
import amada.ramsatna.util.DatabaseHelper;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = "DetailsActivity";
    private TextView mWordMeaning;
    private ImageView mFav;
    private DatabaseHelper databaseHelper = null;
    private WordModel word;
    private Dao<WordModel, Integer> wordDao;
    private CoordinatorLayout layout;
    private TextView mWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(amada.ramsatna.R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(amada.ramsatna.R.id.toolbar);
        setSupportActionBar(toolbar);
        overridePendingTransition(amada.ramsatna.R.anim.anim_slide_in_left, amada.ramsatna.R.anim.anim_slide_out_left);

        mWordMeaning = (TextView) findViewById(amada.ramsatna.R.id.word_meaning);
        mFav = (ImageView) findViewById(R.id.favorites);
        layout = (CoordinatorLayout) findViewById(R.id.word_details_layout);
        mWord = (TextView) findViewById(R.id.word_text);

        Intent i = getIntent();
        final String recordId = i.getStringExtra("word");

        try {
            wordDao = getHelper().getWordsDao();
            word = wordDao.queryForId(Integer.parseInt(recordId));
        } catch (SQLException e) {
            e.printStackTrace();
        }


        if(word.isFavorite()){
            mFav.setBackgroundColor(getResources().getColor(R.color.yellow));
        }

        mWord.setText(word.getWord());
        mWordMeaning.setText(word.getMeaning());

        mFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dao<Favorites, Integer> favoritesDao = null;
                Favorites fav = new Favorites();
                fav.setRecord_id(recordId);

                try {
                    favoritesDao = getHelper().getFavoritesDao();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                if (!word.isFavorite()) {
                    mFav.setBackgroundColor(getResources().getColor(R.color.yellow));
                    word.setIsFavorite(true);
                    try {
                        wordDao.createOrUpdate(word);
                        favoritesDao.createOrUpdate(fav);
                        Snackbar snackbar = Snackbar.make(layout, "Added to favorites", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    mFav.setBackgroundColor(getResources().getColor(R.color.windowBackground));
                    word.setIsFavorite(false);
                    try {
                        favoritesDao.delete(fav);
                        wordDao.createOrUpdate(word);
                        Snackbar snackbar = Snackbar.make(layout, "Removes from favorites", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "onClick: " + word.isFavorite());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();



        if (id == R.id.action_share) {

            String shareText = "كلمة: " + word.getWord() + "\n" + "معنى: " + word.getMeaning();
            Intent intent=new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(intent,getString(R.string.share_title)));
        }

        return super.onOptionsItemSelected(item);
    }

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, amada.ramsatna.R.anim.anim_slide_out_right);
    }
}
