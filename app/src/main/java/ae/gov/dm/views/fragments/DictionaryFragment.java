package ae.gov.dm.views.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;

import ae.gov.dm.R;
import ae.gov.dm.model.Dictionary;
import ae.gov.dm.model.WordModel;
import ae.gov.dm.services.ApiService;
import ae.gov.dm.util.adapters.DictionaryAdaptor;
import ae.gov.dm.util.helpers.DatabaseHelper;
import ae.gov.dm.views.AddWordActivity;
import ae.gov.dm.views.DetailsActivity;


public class DictionaryFragment extends Fragment implements ApiService.DownloadData.ReturnData {

    private static ArrayList<WordModel> words_list;
    private static Context context;
    private final String TAG = getClass().getSimpleName();
    private ArrayList<String> words = new ArrayList<>();
    private ListView dictionary;
    private SearchView searchBox;
    private DictionaryAdaptor adaptor;
    private DatabaseHelper databaseHelper = null;
    private ImageButton mBttnRefresh;
    private FloatingActionButton mAddWord;
    private Button mAddbutton;
    private LinearLayout empty_view;
    private ProgressDialog progressDialog;
    Snackbar snackbar;


    public DictionaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_dictionary, container, false);

        dictionary = (ListView) view.findViewById(R.id.list_view);
        dictionary.setFastScrollAlwaysVisible(true);
        dictionary.setFastScrollEnabled(true);
        searchBox = (SearchView) view.findViewById(R.id.inputSearch);
        mAddbutton = (Button) view.findViewById(R.id.new_word);
        empty_view = (LinearLayout) view.findViewById(R.id.empty_view);
        words_list = new ArrayList<>();
        context = container.getContext();
        databaseHelper = getHelper();
        searchBox.requestFocus();
        adaptor = new DictionaryAdaptor(words_list, getActivity());
        mAddWord = (FloatingActionButton) view.findViewById(R.id.add);


        mAddbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(getActivity())
                        .setMessage("هل تريد أن نضيف معنى " + searchBox.getQuery().toString() + "؟")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("نعم ", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                ApiService apiService = new ApiService(getActivity());
                                apiService.addWord(searchBox.getQuery().toString(), "???", "0");
                                searchBox.setQuery("", true);
                            }
                        })
                        .setNegativeButton("لا", null).show();
            }
        });


        if (databaseHelper.getDictionarySize() == 0) {
            if (isConnectedToInternet()) {

                new ApiService.getDatabaseVerion(new ApiService.getDatabaseVerion.ReturnVersion() {
                    @Override
                    public void handleReturnVersion(String version) {
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("version", version);
                        editor.commit();
                    }
                }).execute();

                new ApiService.DownloadData(new ApiService.DownloadData.ReturnData() {
                    @Override
                    public void handleReturnData(ArrayList<WordModel> words_list, boolean locked) {
                        new Populate().execute();
                    }
                }, getActivity()).execute();
            } else {
                Toast.makeText(getContext(), "No Data Found. Please connect to the internet and restart.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
            }

        } else {
            new Populate().execute();
        }

        mAddWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AddWordActivity.class);
                startActivity(i);
            }
        });


        searchBox.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adaptor.filter(newText.toString());
                return false;
            }
        });


        /**
         * On click of an item in the list, the liste ner finds the meaning of the word and start
         * the DetailsActivity view where it displays the meaning.
         */
        dictionary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                              @Override
                                              public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                                                  TextView wText = (TextView) view.findViewById(R.id.word);
                                                  String word = wText.getText().toString();

                                                  Dictionary dict = new Dictionary();

                                                  WordModel w = dict.getWord(words_list, word);

                                                  Intent i = new Intent(container.getContext(), DetailsActivity.class);
                                                  i.putExtra("word", w.getRecord_id());
                                                  startActivity(i);

                                              }
                                          }

        );

        setupUI(view.findViewById(R.id.dictionary_container));


        new Populate().execute();
        adaptor.notifyDataSetChanged();

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            try {
                checkForUpdates();
            } catch (Exception e) {
                Log.d(TAG, "setUserVisibleHint: " + e.getMessage());
            }

            Log.d(TAG, "setUserVisibleHint: ");
        }

    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public class Populate extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                final Dao<WordModel, Integer> wordDao = getHelper().getWordsDao();
                words_list = (ArrayList) wordDao.queryForAll();

                for (WordModel w : words_list) {
                    if(w.getRecord_id().equals("132") || w.getRecord_id().equals("2778")) Log.d("Image Words: " , w.getWord());
                    words.add(w.getWord());
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adaptor = new DictionaryAdaptor(words_list, getActivity());
            dictionary.setAdapter(adaptor);
            adaptor.notifyDataSetChanged();
            empty_view.setVisibility(View.VISIBLE);
            dictionary.setEmptyView(empty_view);
            Log.d(TAG, "onPostExecute: Finished Populating");
        }
    }


    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
        }
        return databaseHelper;
    }


    @Override
    public void handleReturnData(ArrayList<WordModel> words_list, boolean locked) {
    }

    public void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    try {
                        hideSoftKeyboard(getActivity());
                    } catch (Exception e) {
                        Log.d(TAG, "onTouch: " + e.getMessage());
                    }

                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }


    public boolean isConnectedToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    public void checkForUpdates() {

        new ApiService.getDatabaseVerion(new ApiService.getDatabaseVerion.ReturnVersion() {
            @Override
            public void handleReturnVersion(String databaseVersion) {

                try {
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String version = sp.getString("version", "");
                    boolean isDownloading = sp.getBoolean("isDownloading" , false);
                    final String apiVersion = databaseVersion;


                    progressDialog = new ProgressDialog(getActivity());

                    if (version == null && !isDownloading) {
                       final  SharedPreferences.Editor editor = sp.edit();
                        editor.putString("version", apiVersion);
                        editor.putBoolean("isDownloading", true);
                        editor.commit();
                        new ApiService.DownloadData(new ApiService.DownloadData.ReturnData() {
                            @Override
                            public void handleReturnData(ArrayList<WordModel> words_list, boolean locked) {
                                new Populate().execute();
                                editor.putBoolean("isDownloading", false);
                                editor.commit();
                            }
                        }, getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {

                        if (!version.equals(apiVersion) && isConnectedToInternet() && !isDownloading) {
                            final SharedPreferences.Editor editor = sp.edit();
                            editor.clear();
                            editor.putBoolean("isDownloading", true);
                            editor.commit();
                            Toast.makeText(getContext(), R.string.new_version_available_text, Toast.LENGTH_LONG).show();

                            snackbar = Snackbar.make(mAddbutton, getResources().getString(R.string.new_version_available_text), Snackbar.LENGTH_INDEFINITE);
                            snackbar.show();
                            snackbar.getView().getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                                @Override
                                public boolean onPreDraw() {
                                    snackbar.getView().getViewTreeObserver().removeOnPreDrawListener(this);
                                    ((CoordinatorLayout.LayoutParams) snackbar.getView().getLayoutParams()).setBehavior(null);
                                    return true;
                                }
                            });


                         /*   progressDialog.setTitle("تحديث");
                            progressDialog.setMessage(getResources().getString(R.string.new_version_available_text));
                            progressDialog.setCancelable(false);
                            progressDialog.setIndeterminate(true);
                            progressDialog.show(); */

                            new ApiService.DownloadData(new ApiService.DownloadData.ReturnData() {
                                @Override
                                public void handleReturnData(ArrayList<WordModel> words_list, boolean locked) {
                                    new Populate().execute();
                                    editor.putString("version", apiVersion);
                                    editor.putBoolean("isDownloading", false);
                                    editor.commit();
                                    snackbar.dismiss();
                                    //progressDialog.hide();
                                }
                            }, getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    }

                } catch (Exception e) {
                    Log.d(TAG, "handleReturnVersion: " + e.getMessage());
                }


            }
        }).execute();
    }
}



