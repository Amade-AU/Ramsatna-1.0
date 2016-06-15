package amada.ramsatna.util.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import amada.ramsatna.R;
import amada.ramsatna.model.WordModel;

/**
 * Adapter class for the Dictionary ListView which implements the fast scroll and section indexing capabilities.
 * Also includes a search filter for filtering results.
 */
public class DictionaryAdaptor extends BaseAdapter implements SectionIndexer {

    private static final String TAG = "DictionaryAdaptor";
    private final Context context;
    private ArrayList<WordModel> originalData;
    private ArrayList<WordModel> wordListOrig;
    private Map<String, Integer> mapIndex;
    private String[] sections;
    private ArrayList<String> tempw = new ArrayList<>();



    public DictionaryAdaptor(ArrayList<WordModel> words, Context context) {

        this.context = context;
        this.originalData = words;
        this.wordListOrig = new ArrayList<>(originalData);

        mapIndex = new LinkedHashMap<>();

        for (WordModel wrd : words) {
            tempw.add(wrd.getWord());
        }
        String[] w = tempw.toArray(new String[tempw.size()]);

        for (int i = 0; i < w.length; i++) {
            String word = w[i].trim();
            String index = word.substring(0, 1);
            if (mapIndex.get(index) == null)
                mapIndex.put(index, i);
        }

        Set<String> sectionLetters = mapIndex.keySet();
        ArrayList<String> sectionList = new ArrayList<>(sectionLetters);
        Collections.sort(sectionList);
        sections = new String[sectionList.size()];
        for (int i = 0; i < sectionList.size(); i++)
            sections[i] = sectionList.get(i);


    }


    @Override
    public int getCount() {
        return originalData.size();
    }

    @Override
    public Object getItem(int position) {
        return originalData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return originalData.get(position).hashCode();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row, parent, false);

        TextView word = (TextView) rowView.findViewById(R.id.word);
        TextView meaning = (TextView) rowView.findViewById(R.id.meaning);

        word.setText(originalData.get(position).getWord());
        meaning.setText(originalData.get(position).getMeaning());

        return rowView;

    }


    public void filter(String text) {
        text = text.toLowerCase(Locale.getDefault());

        if (text.contains("\u0622")) {
            text = text.replace("\u0622", "\u0627");
        }

        if (text.contains("\u0623")) {
            text = text.replace("\u0623", "\u0627");
        }

        if (text.contains("\u0625")) {
            text = text.replace("\u0625", "\u0627");
        }

        if (text.contains("\u064E")) {
            text = text.replace("\u064E", "");
        }

        if (text.contains("\u064F")) {
            text = text.replace("\u064F", "");
        }

        if (text.contains("\u0650")) {
            text = text.replace("\u0650", "");
        }

        if (text.contains("\u0652")) {
            text = text.replace("\u0652", "");
        }

        if (text.contains("\u0651")) {
            text = text.replace("\u0651", "");
        }

        if (text.contains("\u064B")) {
            text = text.replace("\u064B", "");
        }

        if (text.contains("\u064C")) {
            text = text.replace("\u064C", "");
        }

        if (text.contains("\u064D")) {
            text = text.replace("\u064D", "");
        }

        if (text.contains("\u0653")) {
            text = text.replace("\u0653", "");
        }

        if (text.contains(" ")) {
            text = text.replace(" ", "");
        }

        if (text.contains("\u0629")) {
            text = text.replace("\u0629", "\u0647");
        }

        if (text.contains("-")) {
            text = text.replace("-", "");
        }

        text.trim();

        Log.d(TAG, "filter: " + text);

        originalData.clear();

        if (text.length() == 0) {
            originalData.addAll(wordListOrig);
        } else {
            for (WordModel wp : wordListOrig) {
                if (wp.getSearch_word().toLowerCase().contains(text)) {
                    originalData.add(wp);
                }
            }
            if(originalData.size() == 0){

               Button btn = (Button) ((Activity) context).findViewById(R.id.new_word);
                btn.setText("هل تريد أن نضيف معنى "  + text + "؟");
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public Object[] getSections() {
        return sections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return mapIndex.get(sections[sectionIndex]);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 1;
    }
}

