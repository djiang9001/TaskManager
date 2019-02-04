package com.bsscompclub.taskmanager;

import android.content.Context;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by danieljng on 2018-03-17.
 */

public class CustomListAdapter extends ArrayAdapter<Items> {
    ArrayList<Items> itemsList = new ArrayList<>();

    public CustomListAdapter(@NonNull Context context, int textViewResourceId, ArrayList<Items> list) {
        super(context, textViewResourceId , list);
        itemsList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.list_view_items, null);
        TextView nameView = (TextView) v.findViewById(R.id.nameView);
        TextView dateView = (TextView) v.findViewById(R.id.dateView);
        nameView.setText(itemsList.get(position).getName());
        if(itemsList.get(position).getDueDateOn()) {
            StringTokenizer st = new StringTokenizer(itemsList.get(position).getDate());
            String date = st.nextToken();
            String time = st.nextToken();
            dateView.setText(date + "\n" + time);
        }else{
            dateView.setText(itemsList.get(position).getCount());
        }
        return v;

    }
}
