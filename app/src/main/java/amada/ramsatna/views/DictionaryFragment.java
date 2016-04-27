package amada.ramsatna.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import amada.ramsatna.R;
import amada.ramsatna.model.Dictionary;
import amada.ramsatna.model.WordModel;
import amada.ramsatna.services.ApiService;
import amada.ramsatna.util.DatabaseHelper;
import amada.ramsatna.util.DictionaryAdaptor;


public class DictionaryFragment extends Fragment implements ApiService.DownloadData.ReturnData {

    private static ArrayList<WordModel> words_list;
    private static Context context;
    private final String TAG = getClass().getSimpleName();
    private ArrayList<String> words = new ArrayList<>();
    private ListView dictionary;
    private EditText searchBox;
    private DictionaryAdaptor adaptor;
    private ScrollView scroll;
    private TextView letters;
    private Drawable img;
    private Map<String, Integer> mapIndex;
    private DatabaseHelper databaseHelper = null;
    private OnFragmentInteractionListener mListener;

    public DictionaryFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_dictionary, container, false);

        dictionary = (ListView) view.findViewById(amada.ramsatna.R.id.list_view);
        Log.d(TAG, "onCreateView: " + dictionary.toString());
        searchBox = (EditText) view.findViewById(amada.ramsatna.R.id.inputSearch);
        scroll = (ScrollView) view.findViewById(amada.ramsatna.R.id.scrollView);

        words_list = new ArrayList<>();
        context = container.getContext();


        new ApiService.DownloadData(this, new ApiService.DownloadData.ReturnData() {
            @Override
            public void handleReturnData(ArrayList<WordModel> words_list) {
                RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.root);
                Snackbar snackbar = Snackbar.make(layout, "Data imported successfully", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }).execute();

        try {

            final Dao<WordModel, Integer> wordDao = getHelper().getWordsDao();
            words_list = (ArrayList) wordDao.queryForAll();

            for (WordModel w : words_list) {
                words.add(w.getWord());
            }

            adaptor = new DictionaryAdaptor(getActivity(), words_list);
            dictionary.setAdapter(adaptor);
            adaptor.notifyDataSetChanged();
            getIndexList(words.toArray(new String[words.size()]));
            populateScrollView();

        } catch (SQLException e) {
            e.printStackTrace();
        }


        searchBox.addTextChangedListener(searchFilter);

        /**
         * Adds an empty view for ListView
         */
        View empty = getActivity().getLayoutInflater().inflate(amada.ramsatna.R.layout.empty_list_item, null, false);
        getActivity().addContentView(empty, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        dictionary.setEmptyView(empty);

        /**
         * hides the search icon once it gains focus.
         */
        searchBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    img = getView().getResources().getDrawable(amada.ramsatna.R.drawable.ic_search);
                    searchBox.setCompoundDrawables(img, null, null, null);
                } else {
                    searchBox.setCompoundDrawables(null, null, null, null);
                }
            }
        });

        /**
         * On click of an item in the list, the liste ner finds the meaning of the word and start
         * the DetailsActivity view where it displays the meaning.
         */
        dictionary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                //String word = words_list.get(position).getWord();
                //String word = dictionary.getItemAtPosition(position).toString();
                TextView wText = (TextView) view.findViewById(R.id.word);
                String word = wText.getText().toString();

                Dictionary dict = new Dictionary();

                WordModel w = dict.getWord(words_list, word);

                Intent i = new Intent(container.getContext(), DetailsActivity.class);
                i.putExtra("word", w.getRecord_id());
                startActivity(i);
                // overridePendingTransition(0, amada.ramsatna.R.anim.anim_slide_out_left);
            }
        });

        return view;

    }

    /**
     * Search Filter for List View
     */
    private TextWatcher searchFilter = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (count < before) {
                adaptor.resetData();
            }

            adaptor.getFilter().filter(s.toString());

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    /**
     * Populates the scroll view with alphabets.
     */
    public void populateScrollView() {
        LinearLayout alpha = new LinearLayout(getActivity());
        alpha.setOrientation(LinearLayout.VERTICAL);

        List<String> indexList = new ArrayList<>(mapIndex.keySet());
        letters = new TextView(getActivity());

        for (String index : indexList) {
            letters = (TextView) getActivity().getLayoutInflater().inflate(
                    amada.ramsatna.R.layout.side_index_item, null);
            letters.setText(index);
            letters.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView selectedIndex = (TextView) v;
                    dictionary.setSelection(mapIndex.get(selectedIndex.getText()));
                }
            });
            alpha.addView(letters);
        }

        scroll.addView(alpha);
    }

    private void getIndexList(String[] words) {
        mapIndex = new LinkedHashMap<>();
        for (int i = 0; i < words.length; i++) {
            String word = words[i].trim();
            String index = word.substring(0, 1);
            if (mapIndex.get(index) == null)
                mapIndex.put(index, i);
        }
    }


    @Override
    public void handleReturnData(ArrayList<WordModel> words_list) {

    }

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
        }
        return databaseHelper;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
