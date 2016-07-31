package ae.gov.dm.views;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ae.gov.dm.R;
import uk.co.senab.photoview.PhotoViewAttacher;

public class FullScreenActivity extends AppCompatActivity {

    private ImageView mBackIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_screen);

        mBackIcon = (ImageView) findViewById(R.id.back_navigate);
        Intent i = getIntent();
        String url = i.getExtras().getString("url");

        ImageView iv = (ImageView) findViewById(R.id.image_fill_screen);
        PhotoViewAttacher mAttacher = new PhotoViewAttacher(iv);
        Picasso.with(FullScreenActivity.this).load(url).into(iv);


        mBackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
