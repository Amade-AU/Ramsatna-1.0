package amada.ramsatna.views.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;

import amada.ramsatna.R;
import amada.ramsatna.model.configs.Config;
import amada.ramsatna.services.HomeApiService;
import amada.ramsatna.services.LoadImageTask;
import amada.ramsatna.views.AddWordActivity;
import amada.ramsatna.views.DetailsActivity;


public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private TextView mTotalDict;
    private View view;
    private Config mConfig;
    private Button mWordDay;
    private ImageView mImgRight;
    private ImageView mImgLeft;
    private TextView rightImageTxt;
    private TextView leftImageTxt;
    private Button mAddWordHome;
    private ImageView mBottomLogo;
    private ScrollView mScrollView;

    public HomeFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);

        mWordDay = (Button) view.findViewById(R.id.word_of_the_day);
        mTotalDict = (TextView) view.findViewById(R.id.total_in_dict_text);
        mImgRight = (ImageView) view.findViewById(R.id.image_right);
        mImgLeft = (ImageView) view.findViewById(R.id.image_left);
        rightImageTxt = (TextView) view.findViewById(R.id.rightImageViewText);
        leftImageTxt = (TextView) view.findViewById(R.id.leftImageViewText);
        mAddWordHome = (Button) view.findViewById(R.id.home_add_word_botton);
        mBottomLogo = (ImageView) view.findViewById(R.id.bottom_logo);
        mScrollView = (ScrollView) view.findViewById(R.id.home_scroll);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Gson gson = new Gson();
        String json = sp.getString("Config", "");
        String right_bitmap = sp.getString("bitmap_right", "");
        String left_bitmap = sp.getString("bitmap_left", "");

        Log.d(TAG, "onCreateView: " + json);

        if (json != "") {
            mConfig = gson.fromJson(json, Config.class);
            mTotalDict.setText(mConfig.getTotal_in_dictionary());
            mWordDay.setText(mConfig.getRandom().getWord());
            leftImageTxt.setText(mConfig.getParams().getNews_1_title());
            rightImageTxt.setText(mConfig.getParams().getNews_2_title());

        }

        mImgRight.setImageBitmap(StringToBitMap(right_bitmap));
        mImgLeft.setImageBitmap(StringToBitMap(left_bitmap));


        mAddWordHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddWordActivity.class));
            }
        });


        mBottomLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mScrollView.fullScroll(ScrollView.FOCUS_UP);
            }

        });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        new HomeApiService(new HomeApiService.ReturnData() {
            @Override
            public void handleReturnData(Config config) {
                mConfig = config;

                mWordDay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), DetailsActivity.class);
                        i.putExtra("word", mConfig.getRandom().getId());
                        startActivity(i);
                    }
                });

                populateData();
            }
        }).fetchHomeContent();
    }

    public void populateData() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTotalDict.setText(mConfig.getTotal_in_dictionary());
                mWordDay.setText(mConfig.getRandom().getWord());
                mWordDay.setTextColor(getResources().getColor(R.color.colorAccent));

                mImgLeft.setTag("left_image");
                mImgRight.setTag("right_image");

                String url = mConfig.getParams().getNews_1_url();

                Log.d(TAG, "run: " + mConfig.getParams().getNews_1_title());

                if (!(url.equals(""))) {

                    new LoadImageTask(mConfig.getParams().getNews_2_image_link(), mConfig.getParams().getNews_1_image_link(), mImgLeft, mImgRight).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    leftImageTxt.setText(mConfig.getParams().getNews_1_title());
                    rightImageTxt.setText(mConfig.getParams().getNews_2_title());
                }


                mImgLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(mConfig.getParams().getNews_2_url());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });

                mImgRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(mConfig.getParams().getNews_1_url());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });

            }
        });


    }

    // TODO: Rename method, update argument and hook method into UI event


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getActivity().findViewById(R.id.btn_refresh).setVisibility(View.INVISIBLE);
        }
    }


    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }


}
