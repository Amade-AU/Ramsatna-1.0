package amada.ramsatna.views;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import amada.ramsatna.R;
import amada.ramsatna.services.ApiService;
import amada.ramsatna.util.FileHelper;

public class AddWordActivity extends AppCompatActivity {

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

        mAddButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                if (mWord.getText().toString().equals("") || mMeaning.getText().toString().equals("")) {
                    Snackbar snackbar = Snackbar.make(mLayout, "Cannot be left blank", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else if (isValid(mWord.getText().toString(), mMeaning.getText().toString())) {

                    ApiService service = new ApiService();
                    service.addWord(mWord.getText().toString(), mMeaning.getText().toString());

                } else {
                    Snackbar snackbar = Snackbar.make(mLayout, "Not a valid word", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

            }
        });

    }

    public boolean isValid(String word, String meaning) {
        FileHelper fHelper = new FileHelper();
        return fHelper.validateWord(word) && word.length() <= 200 && meaning.length() <= 5000;

    }


}
