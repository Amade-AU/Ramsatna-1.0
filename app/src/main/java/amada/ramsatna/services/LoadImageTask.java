package amada.ramsatna.services;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import amada.ramsatna.views.MainActivity;

/**
 * Async Task class for loading images from the API.
 * Converts the Image stream into a bitmap object and populates the image view.
 * The bitmap pictures are persisted as well in shared preferences for caching purposes.
 */
public class LoadImageTask extends AsyncTask<Void, Void, List<Bitmap>> {

    private String urlLeft;
    private String urlRight;
    private ImageView imgViewLeft;
    private ImageView imgViewRight;


    public LoadImageTask(String urlLeft, String urlRight, ImageView viewLeft, ImageView viewRight) {
        this.urlLeft = urlLeft;
        this.urlRight = urlRight;
        imgViewLeft = viewLeft;
        imgViewRight = viewRight;
    }

    @Override
    protected List<Bitmap> doInBackground(Void... params) {

        Bitmap imgLeft;
        Bitmap imgRight;
        InputStream is;
        List<Bitmap> pictures = new ArrayList<>();

        try {
            URL imageUrlLeft = new URL(urlLeft);
            is = imageUrlLeft.openStream();
            imgLeft = BitmapFactory.decodeStream(is);

            URL imageUrlRight = new URL(urlRight);
            is = imageUrlRight.openStream();
            imgRight = BitmapFactory.decodeStream(is);

            pictures.add(imgLeft);
            pictures.add(imgRight);

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }


        return pictures;

    }


    @Override
    protected void onPostExecute(List<Bitmap> bitmap) {

        Bitmap resizedLeft = Bitmap.createScaledBitmap(bitmap.get(0), 330, 300, true);
        Bitmap resizedRight = Bitmap.createScaledBitmap(bitmap.get(1), 330, 300, true);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContext());
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("bitmap_right", BitMapToString(resizedRight));

        editor.putString("bitmap_left", BitMapToString(resizedLeft));
        editor.commit();

        imgViewLeft.setImageBitmap(resizedLeft);
        imgViewRight.setImageBitmap(resizedRight);

    }


    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
}
