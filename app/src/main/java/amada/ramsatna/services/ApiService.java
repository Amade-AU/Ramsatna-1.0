package amada.ramsatna.services;


import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Handler;

import amada.ramsatna.model.WordModel;
import amada.ramsatna.util.FileHelper;
import amada.ramsatna.views.DictionaryFragment;
import amada.ramsatna.views.MainActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Hamza Tariq on 7/04/2016.
 * This class handles all the calls made to the API
 */


public class ApiService {
    private static String res;
    private static final String ADD_WORD_ENDPOINT = "http://3on.ae/clients/DM/API/add.php";
    private static final String DOWNLOAD_FILE_ENDPOINT = "http://3on.ae/clients/DM/publish/dictionary.txt";
    private String TAG = getClass().getSimpleName();
    private final OkHttpClient client = new OkHttpClient();
    private static ProgressDialog mProgress;


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

        RequestBody params = new MultipartBody.Builder()
                .addFormDataPart("word", word)
                .addFormDataPart("meaning", meaning)
                .build();

        Log.d(TAG, "addWord: " + params);

        Request request = new Request.Builder()
                .url(ADD_WORD_ENDPOINT)
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, parameters))
                .header("content-type", "application/x-www-form-urlencoded")
                .build();


        Log.d(TAG, "addWord: " + request.body().toString());


        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {


                    JSONObject obj = new JSONObject(response.body().string());
                    res = obj.getString("res");
                    final String error = obj.getString("error");
                    Log.d(TAG, "onResponse: " + obj.toString());

                    android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (("0").equals(res)) {
                                Toast.makeText(MainActivity.getContext(), "Successful", Toast.LENGTH_LONG).show();
                            } else if (("1").equals(res)) {
                                Toast.makeText(MainActivity.getContext(), "Word already exists", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(MainActivity.getContext(), "Operation is not successful " + error, Toast.LENGTH_LONG).show();
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
     * Downloads and stores the dictionary data from the API and stores it on the device.
     * It is performed in a separate thread for better performance.
     */
    public static class DownloadData extends AsyncTask<Void, Void, ArrayList<WordModel>> {

        private ReturnData returnData = null;
        DictionaryFragment context;


        public interface ReturnData {
            void handleReturnData(ArrayList<WordModel> words_list);
        }


        public DownloadData(DictionaryFragment ct, ReturnData delegate) {
            context = ct;
            this.returnData = delegate;
        }

        @Override
        protected void onPreExecute() {
            mProgress = new ProgressDialog(MainActivity.getContext());
            mProgress.setTitle("Downloading Data");
            mProgress.show();
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
            mProgress.dismiss();
            returnData.handleReturnData(word_list);
        }
    }


}
