package amada.ramsatna.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
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
import amada.ramsatna.services.AudioService;
import amada.ramsatna.util.helpers.DatabaseHelper;

/**
 * Word details activity where the meaing of the word is displayed and and option for sharing the word.
 */
public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = "DetailsActivity";
    private TextView mWordMeaning;
    private ImageView mFav;
    private DatabaseHelper databaseHelper = null;
    private WordModel word;
    private Dao<WordModel, Integer> wordDao;
    private CoordinatorLayout layout;
    private TextView mWord;
    private FloatingActionButton mShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        final String recordId = i.getStringExtra("word");

        try {
            wordDao = getHelper().getWordsDao();
            word = wordDao.queryForId(Integer.parseInt(recordId));
        } catch (SQLException e) {
            e.printStackTrace();
        }


        if (word != null && word.getHas_audio().equals("1")) {
            setContentView(R.layout.content_details_audio);

            ImageView imgVoice = (ImageView) findViewById(R.id.speaker);

            imgVoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AudioService audioService = new AudioService();
                  //  audioService.getAudio(word.getRecord_id(), DetailsActivity.this);
                    //audioService.playAudio("http://3on.ae/clients/DM/audio/315.caf", DetailsActivity.this);
                }
            });

        } else {
            setContentView(amada.ramsatna.R.layout.activity_details);
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_dictionary);
        setSupportActionBar(toolbar);
        overridePendingTransition(amada.ramsatna.R.anim.anim_slide_in_left, amada.ramsatna.R.anim.anim_slide_out_left);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        mShare = (FloatingActionButton) findViewById(R.id.share);

        mWordMeaning = (TextView) findViewById(amada.ramsatna.R.id.word_meaning);
        mFav = (ImageView) findViewById(R.id.favorites);
        layout = (CoordinatorLayout) findViewById(R.id.word_details_layout);
        mWord = (TextView) findViewById(R.id.word_text);


        // Sets the favorites icon for the word if it is marked as favorite
        if (word != null) {
            if (word.isFavorite()) {
                mFav.setImageResource(R.drawable.favorites_yellow);
                // mFav.setBackgroundColor(getResources().getColor(R.color.yellow));
            }

            mWord.setText(word.getWord());
            mWordMeaning.setText(word.getMeaning());
        }

        // Checks the current status of the word and decides weather the user wants to add it to favorites or remove it from the list.
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
                    mFav.setImageResource(R.drawable.favorites_yellow);
                    word.setIsFavorite(true);
                    try {
                        wordDao.createOrUpdate(word);
                        favoritesDao.createOrUpdate(fav);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    mFav.setImageResource(R.drawable.favorites_white);
                    word.setIsFavorite(false);
                    try {
                        favoritesDao.delete(fav);
                        wordDao.createOrUpdate(word);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "onClick: " + word.isFavorite());
            }
        });

        // Allows to the share the word and its details
        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareText = "كلمة: " + word.getWord() + "\n" + "معنى: " + word.getMeaning();
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, shareText);
                startActivity(Intent.createChooser(intent, getString(R.string.share_title)));
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


        if (id == android.R.id.home) {
            overridePendingTransition(0, amada.ramsatna.R.anim.anim_slide_out_right);
            Intent parentIntent = NavUtils.getParentActivityIntent(this);
            parentIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(parentIntent);
            finish();
            return true;
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
        overridePendingTransition(0, amada.ramsatna.R.anim.anim_slide_out_right);
        super.onBackPressed();
    }
}
