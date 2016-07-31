package ae.gov.dm.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ae.gov.dm.R;
import ae.gov.dm.model.Dictionary;
import ae.gov.dm.model.Favorites;
import ae.gov.dm.model.WordModel;
import ae.gov.dm.util.helpers.DatabaseHelper;
import ae.gov.dm.views.DetailsActivity;


public class FavoritesFragment extends Fragment {
    private static final String TAG = "Favorites Fragment";
    // TODO: Rename parameter arguments, choose names that match


    private OnFragmentInteractionListener mListener;
    private DatabaseHelper mDatabaseHelper = null;
    private ListView mFavList;
    private List<Favorites> mFavorites;
    private ArrayList<WordModel> mWords;
    private ArrayAdapter<String> adaptor;
    private String[] words;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    public void fetchData() {
        try {

            Dao<Favorites, Integer> favoritesDao = getHelper().getFavoritesDao();
            Dao<WordModel, Integer> wordDao = getHelper().getWordsDao();

            mFavorites = favoritesDao.queryForAll();

            for (int i = 0; i < mFavorites.size(); i++) {
                mWords.add(wordDao.queryForId(Integer.parseInt(mFavorites.get(i).getRecord_id())));
            }

            words = new String[mWords.size()];

            for (int i = 0; i < words.length; i++) {
                words[i] = mWords.get(i).getWord();
            }

            adaptor = new ArrayAdapter<>(getActivity(), R.layout.listview_text, words);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFavorites = new ArrayList<>();
        mWords = new ArrayList<>();
        adaptor = new ArrayAdapter<>(getActivity(), R.layout.listview_text);


    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        mFavList = (ListView) view.findViewById(R.id.favorites_list);


        return view;

    }


    private DatabaseHelper getHelper() {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
        }
        return mDatabaseHelper;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mWords.clear();
        fetchData();
        mFavList.setAdapter(adaptor);
        adaptor.notifyDataSetChanged();

        mFavList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String word = mFavList.getItemAtPosition(position).toString();
                Dictionary dict = new Dictionary();

                WordModel w = dict.getWord(mWords, word);
                Intent i = new Intent(getActivity(), DetailsActivity.class);
                i.putExtra("word", w.getRecord_id());
                startActivity(i);
            }
        });

        Log.d(TAG, "onResume: ON RESUME: " + adaptor.getCount());

    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop: ON STOPPING");
        super.onStop();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        try{
            if (isVisibleToUser) {
                hideKeyboard(getActivity());
            }
        }
        catch(Exception e){
            Log.e("FavoritesFragment: " , e.getMessage());
        }

    }

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


}
