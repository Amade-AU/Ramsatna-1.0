package amada.ramsatna.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import amada.ramsatna.R;
import amada.ramsatna.services.ApiService;
import amada.ramsatna.util.helpers.FileHelper;

/**
 * Add Word Activity implementation  class were users can add new words to the dictionary.
 */
public class AddWordActivity extends AppCompatActivity {

    private static final String TAG = "AddWordActivity";
    private Button mAddButton;
    private EditText mWord;
    private EditText mMeaning;
    private RelativeLayout mLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);
        mAddButton = (Button) findViewById(R.id.add_word_button);
        mWord = (EditText) findViewById(R.id.editText_word);
        mMeaning = (EditText) findViewById(R.id.editText_word_meaing);
        mLayout = (RelativeLayout) findViewById(R.id.root);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.add_word_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        mAddButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                if (mWord.getText().toString().equals("") || mMeaning.getText().toString().equals("")) {
                    Snackbar snackbar = Snackbar.make(mLayout, R.string.blank_validation, Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else if (isValid(mWord.getText().toString(), mMeaning.getText().toString())) {

                    ApiService service = new ApiService(AddWordActivity.this);
                    service.addWord(mWord.getText().toString(), mMeaning.getText().toString());

                } else {
                    Snackbar snackbar = Snackbar.make(mLayout, R.string.not_valid_word, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

    }

    public boolean isValid(String word, String meaning) {
        FileHelper fHelper = new FileHelper();
        return fHelper.validateWord(word) && word.length() <= 200 && meaning.length() <= 5000;

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

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
}
