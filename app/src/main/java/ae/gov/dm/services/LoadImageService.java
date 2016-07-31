package ae.gov.dm.services;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import ae.gov.dm.R;
import ae.gov.dm.views.DetailsActivity;
import ae.gov.dm.views.FullScreenActivity;

/**
 * Created by Zeeshan on 29/07/16.
 */
public class LoadImageService {

        private static final String IMAGE_ENDPOINT = "https://3on.ae/clients/DM/photos/";
        private static final String IMAGE_TYPE = ".png";


        public void getImageForWord(String id, final Context ctx, ImageView view){

            final String url = IMAGE_ENDPOINT + id + IMAGE_TYPE;

            Log.d("LoadImageService", "getImageForWord: " + url);

            try{
                view.setVisibility(View.VISIBLE);
                Picasso.with(ctx).load(url).into(view);
            }

            catch(Exception e){
                Log.e("LoadImageService", "Cannot load the image. Exception: " + e.getMessage());
            }

        }


}
