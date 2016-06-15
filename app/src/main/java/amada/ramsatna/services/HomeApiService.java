package amada.ramsatna.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import amada.ramsatna.model.WordModel;
import amada.ramsatna.model.configs.Config;
import amada.ramsatna.views.MainActivity;

/**
 * Service for calling endpoints related to the Home screen.
 */
public class HomeApiService {

    private static final String CONFINGS_ENDPOINT = "http://3on.ae/clients/DM/configs.json";
    private static final String TAG = "HomeApiService";

    private URL url;
    private Config config;
    private ReturnData returnData = null;

    public HomeApiService(ReturnData delegate) {
        this.returnData = delegate;
    }

    public interface ReturnData {
        void handleReturnData(Config config);
    }

    /**
     * Calls the homepage endpoint and retuns the JSON data.
     * The data is converted into a Config POJO and saved in the shared preferences (for cashing purposes)
     * @return
     */
    public Config fetchHomeContent() {


        Log.d(TAG, "fetchHomeContent: Inside HomeApiService");
        new Thread() {

            @Override
            public void run() {
                try {

                    url = new URL(CONFINGS_ENDPOINT);
                    InputStream is = url.openStream();
                    String json = "";
                    Scanner scan = new Scanner(is);
                    while (scan.hasNext()) {
                        json = json + " " + scan.next();
                    }



                    config = new Gson().fromJson(json, Config.class);

                    Log.d(TAG, "run: " + config.getParams().getNews_1_title());

                    if (!config.getRes().equals("0")) {
                        System.exit(0);
                    }

                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContext());
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("Config", json);
                    editor.commit();
                    

                    returnData.handleReturnData(config);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();


        return config;
    }


}
