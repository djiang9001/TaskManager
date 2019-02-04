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
 * Created by U0006008 on 12/04/2018.
 */

public class CustomListAdapter2 extends ArrayAdapter<HistoryItems> {

    ArrayList<HistoryItems> historyItemsList = new ArrayList<>();

    public CustomListAdapter2(@NonNull Context context, int textViewResourceId, ArrayList<HistoryItems> list) {
        super(context, textViewResourceId , list);
        historyItemsList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.list_view_items, null);
        TextView nameView = (TextView) v.findViewById(R.id.nameView);
        TextView dateView = (TextView) v.findViewById(R.id.dateView);
        nameView.setText(historyItemsList.get(position).getNameHistory());
        StringTokenizer st = new StringTokenizer(historyItemsList.get(position).getDateHistory());
        dateView.setText(st.nextToken() + "\n" + st.nextToken() + "\n" + historyItemsList.get(position).getTimeSpent()+"h spent");
        return v;

    }
}
