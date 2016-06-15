package amada.ramsatna.services;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import amada.ramsatna.R;
import amada.ramsatna.model.WordModel;
import amada.ramsatna.util.helpers.FileHelper;
import amada.ramsatna.views.MainActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * This class handles all the calls made to the API regarding dictionary downloading and adding words.
 */


public class ApiService {
    private static String res;
    private static String version;
    private static final String ADD_WORD_ENDPOINT = "http://3on.ae/clients/DM/API/add.php";
    private static final String DOWNLOAD_FILE_ENDPOINT = "http://3on.ae/clients/DM/publish/dictionary.txt";
    private static final String DATABASE_VERSION_ENDPOINT_ = "http://3on.ae/clients/DM/dictionary/version.txt";
    private String TAG = getClass().getSimpleName();
    private final OkHttpClient client = new OkHttpClient();
    private Context mCtx;

    public ApiService() {
    }

    public ApiService(Context ctx) {
        mCtx = ctx;
    }


    /**
     * Calls the add word endpoint on the API with the new word and its meaning.
     * The response is handled and appropriate messages are displayed depending on the response code.
     *
     * @param word
     * @param meaning
     * @return
     */
    public String addWord(String word, String meaning) {

        final MediaType MEDIA_TYPE_MARKDOWN
                = MediaType.parse("application/x-www-form-urlencoded");

        String parameters = "word=" + word + "&meaning=" + meaning;



        Request request = new Request.Builder()
                .url(ADD_WORD_ENDPOINT)
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, parameters))
                .header("content-type", "application/x-www-form-urlencoded")
                .build();


        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.getContext(), R.string.connection_error, Toast.LENGTH_LONG).
                                show();
                    }

                });
                Log.d(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    JSONObject obj = new JSONObject(response.body().string());
                    res = obj.getString("res");
                    android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (("0").equals(res)) {
                                Toast.makeText(MainActivity.getContext(), R.string.message_succesfull, Toast.LENGTH_LONG).show();
                                ((Activity) mCtx).finish();
                            } else if (("1").equals(res)) {
                                Toast.makeText(MainActivity.getContext(), R.string.word_already_exists_text, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(MainActivity.getContext(), R.string.operation_failed_message, Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                } catch (JSONException js) {
                    Log.d(TAG, "onResponse: " + js.getMessage());
                }
            }

        });

        return res;
    }

    /**
     * Calls the version API endpoint and returns the current database version.
     */
    public static class getDatabaseVerion extends AsyncTask<Void, Void, String> {

        private ReturnVersion returnVersion = null;

        public getDatabaseVerion(ReturnVersion returnVersion) {
            this.returnVersion = returnVersion;
        }

        public interface ReturnVersion {
            void handleReturnVersion(String version);
        }

        @Override
        protected String doInBackground(Void... params) {

            URL url;
            try {
                url = new URL(DATABASE_VERSION_ENDPOINT_);
                InputStream is = url.openStream();
                Scanner scan = new Scanner(is);
                while (scan.hasNext()) {
                    version = scan.next();
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return version;
        }

        @Override
        protected void onPostExecute(String s) {
            returnVersion.handleReturnVersion(s);
        }
    }


    /**
     * Downloads and stores the dictionary data from the API and stores it on the device.
     * It is performed in a separate thread for better performance.
     */
    public static class DownloadData extends AsyncTask<Void, Void, ArrayList<WordModel>> {

        private boolean locked;
        private ReturnData returnData = null;
        private String TAG = "DownloadData";

        public interface ReturnData {
            void handleReturnData(ArrayList<WordModel> words_list, boolean locked);
        }


        public DownloadData(ReturnData delegate) {
            this.returnData = delegate;
        }

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute: Starting Downloading...");
            locked = true;
        }

        @Override
        protected ArrayList<WordModel> doInBackground(Void... params) {

            ArrayList<WordModel> words_list = new ArrayList<>();

            try {
                URL url = new URL(DOWNLOAD_FILE_ENDPOINT);
                FileHelper fileHelper = new FileHelper();
                words_list = fileHelper.importData(url.openStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return words_list;
        }

        @Override
        protected void onPostExecute(ArrayList<WordModel> word_list) {
            locked = false;
            returnData.handleReturnData(word_list, locked);
            Log.d(TAG, "onPostExecute: DOWNLOADING COMPLETE");
        }
    }


    public String addWord(String word, String meaning, String has_audio) {

        final MediaType MEDIA_TYPE_MARKDOWN
                = MediaType.parse("application/x-www-form-urlencoded");

        String parameters = "word=" + word + "&meaning=" + meaning + "$has_audio=" + has_audio;


        Request request = new Request.Builder()
                .url(ADD_WORD_ENDPOINT)
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, parameters))
                .header("content-type", "application/x-www-form-urlencoded")
                .build();


        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.getContext(), R.string.connection_error, Toast.LENGTH_LONG).
                                show();
                    }

                });
                Log.d(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    JSONObject obj = new JSONObject(response.body().string());
                    res = obj.getString("res");
                    android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());

                    Log.d(TAG, "onResponse: " + res);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.getContext(), R.string.message_succesfull, Toast.LENGTH_LONG).show();
                        }
                    });


                } catch (JSONException js) {
                    Log.d(TAG, "onResponse: " + js.getMessage());
                }
            }

        });

        return res;
    }


}
