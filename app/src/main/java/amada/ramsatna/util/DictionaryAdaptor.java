package amada.ramsatna.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import amada.ramsatna.R;
import amada.ramsatna.model.WordModel;

/**
 * Created by Hamza on 18/04/2016.
 */
public class DictionaryAdaptor extends ArrayAdapter<WordModel> implements Filterable {

    private final Context context;
    private ArrayList<WordModel> originalData;
    private DictionaryFilter dictionaryFilter;
    private ArrayList<WordModel> wordListOrig;


    public DictionaryAdaptor(Context context, ArrayList<WordModel> words) {
        super(context, R.layout.row, words);

        this.context = context;
        this.originalData = words;
        this.wordListOrig = new ArrayList<>(originalData);

    }


    @Override
    public int getCount() {
      return originalData.size();
    }

    @Override
    public long getItemId(int position) {
        return originalData.get(position).hashCode();
    }



    public void resetData(){
        originalData = wordListOrig;
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

    @Override
    public Filter getFilter() {
        if (dictionaryFilter == null) {
            dictionaryFilter = new DictionaryFilter();
        }
        return dictionaryFilter;
    }

    private class DictionaryFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                results.values = originalData;
                results.count = originalData.size();
            } else {
                List<WordModel> tempWordList = new ArrayList<>();

                for (WordModel w : originalData) {
                    if (w.getWord().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        tempWordList.add(w);
                    }
                }

                results.values = tempWordList;
                results.count = tempWordList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count == 0) {
                notifyDataSetInvalidated();

            } else {
                originalData = (ArrayList<WordModel>) results.values;
                notifyDataSetChanged();
            }
        }
    }

}

