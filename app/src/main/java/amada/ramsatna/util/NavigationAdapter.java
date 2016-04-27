package amada.ramsatna.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import amada.ramsatna.R;
import amada.ramsatna.model.NavigationRowItem;

/**
 * Created by Hamza on 19/04/2016.
 */
public class NavigationAdapter extends BaseAdapter {

    Context context;
    List<NavigationRowItem> rowItem;

    public NavigationAdapter(Context context, List<NavigationRowItem> rowItem) {
        this.context = context;
        this.rowItem = rowItem;
    }


    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.navigation, null);
        }

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        NavigationRowItem row_pos = rowItem.get(position);

        imgIcon.setImageResource(row_pos.getIcon());
        txtTitle.setText(row_pos.getTitle());
        return convertView;


    }
}
