package com.bsscompclub.taskmanager;

import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.apache.commons.io.FileUtils;

import java.io.File;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    public ArrayList<String> names;
    public ArrayList<String> dates;
    public ArrayList<String> countDownStrings;

    public ListView lvItems;

    public ArrayAdapter<Items> customAdapter;
    public ArrayList<Items> itemsList;

    public ArrayList<Date> rawDates;
    public ArrayList<Double> loggedHours;

    public boolean dueDateOn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Current Tasks");
    }

    //onResume runs every time app starts or another activity ends
    @Override
    public void onResume(){
        super.onResume();
        lvItems = (ListView) findViewById(R.id.lvItems);

        itemsList = new ArrayList<Items>();
        names = new ArrayList<String>();
        dates = new ArrayList<String>();
        rawDates = new ArrayList<Date>();
        countDownStrings = new ArrayList<String>();

        loggedHours = new ArrayList<Double>();

        readItems();

        //setup listview
        customAdapter = new CustomListAdapter(this, R.layout.list_view_items, itemsList);
        lvItems.setAdapter(customAdapter);

        //setup view listeners
        setupListViewListener();

        //setup toggle button
        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dueDateOn=true;
                    countDownStrings = new ArrayList<String>();
                    //things to be done every second
                    //set all countDownString to match the current countdowns, using millisToString
                    for (int i = 0; i < names.size(); i++) {
                        Date d = new Date();
                        long millis = rawDates.get(i).getTime() - d.getTime();

                        countDownStrings.add(millisToString(millis));//used modular arithmetic

                        Items temp = new Items(itemsList.get(i).getName(), itemsList.get(i).getDate(), countDownStrings.get(i),dueDateOn);
                        itemsList.set(i, temp);
                    }

                    customAdapter.notifyDataSetChanged();

                    TextView textViewHead1 = (TextView) findViewById(R.id.textViewHead1);
                    textViewHead1.setText("Due Date");

                } else {
                    dueDateOn=false;
                    countDownStrings = new ArrayList<String>();
                    //things to be done every second
                    //set all countDownString to match the current countdowns, using millisToString
                    for (int i = 0; i < names.size(); i++) {
                        Date d = new Date();
                        long millis = rawDates.get(i).getTime() - d.getTime();

                        countDownStrings.add(millisToString(millis));//used modular arithmetic

                        Items temp = new Items(itemsList.get(i).getName(), itemsList.get(i).getDate(), countDownStrings.get(i),dueDateOn);
                        itemsList.set(i, temp);
                    }

                    customAdapter.notifyDataSetChanged();

                    TextView textViewHead1 = (TextView) findViewById(R.id.textViewHead1);
                    textViewHead1.setText("Time Remaining");

                }
            }
        });



        //handler for updating the listview every second

        //first instance is outside of handler so that there is no delay
        countDownStrings = new ArrayList<String>();
        //things to be done every second
        //set all countDownString to match the current countdowns, using millisToString
        for (int i = 0; i < names.size(); i++) {
            Date d = new Date();
            long millis = rawDates.get(i).getTime() - d.getTime();

            countDownStrings.add(millisToString(millis));//used modular arithmetic

            Items temp = new Items(itemsList.get(i).getName(), itemsList.get(i).getDate(), countDownStrings.get(i),dueDateOn);
            itemsList.set(i, temp);
        }
        customAdapter.notifyDataSetChanged();
        final Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                countDownStrings = new ArrayList<String>();
                //things to be done every second
                //set all countDownString to match the current countdowns, using millisToString
                for (int i = 0; i < names.size(); i++) {
                    Date d = new Date();
                    long millis = rawDates.get(i).getTime() - d.getTime();

                    countDownStrings.add(millisToString(millis));//used modular arithmetic

                    Items temp = new Items(itemsList.get(i).getName(), itemsList.get(i).getDate(), countDownStrings.get(i),dueDateOn);
                    itemsList.set(i, temp);
                }
                customAdapter.notifyDataSetChanged();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    // ...end of onCreate and onResume methods


    public void onAddItem(View v) {
        Intent intent = new Intent(MainActivity.this, EditList.class);
        intent.putExtra("names", names);
        intent.putExtra("dates", dates);
        intent.putExtra("loggedHours", loggedHours);
        startActivity(intent);

    }

    public void onHistory(View v) {
        Intent intent = new Intent(MainActivity.this, History.class);

        startActivity(intent);
    }

    private void setupListViewListener() {
        //allows for editing existing items in list
        lvItems.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View item, int pos, long id) {
                        //Open up a new page for details on the individual item
                        Intent intent = new Intent(MainActivity.this, EditList2.class);

                        intent.putExtra("names", names);
                        intent.putExtra("dates", dates);
                        intent.putExtra("pos", pos);
                        intent.putExtra("rawDates", rawDates);
                        intent.putExtra("loggedHours", loggedHours);

                        startActivity(intent);

                        return;
                    }
                }
        );
    }

    private void readItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        File todoFileDates = new File(filesDir, "tododates.txt");
        File todoFileHours = new File(filesDir, "todohours.txt");

        try {
            itemsList = new ArrayList<Items>();
            names = new ArrayList<String>(FileUtils.readLines(todoFile));
            dates = new ArrayList<String>(FileUtils.readLines(todoFileDates));

            loggedHours = new ArrayList<Double>();

            ArrayList<String> temp = new ArrayList<String>(FileUtils.readLines(todoFileHours));
            loggedHours = new ArrayList<Double>();
            for (String s : temp) loggedHours.add(Double.valueOf(s));

            rawDates = new ArrayList<Date>();
            countDownStrings = new ArrayList<String>();

            //for loop to parse dates into rawDates from strings in ArrayList<String> dates, using the method stringToDate
            for (int i = 0; i < names.size(); i++) {
                rawDates.add(stringToDate(dates.get(i)));
            }
            for (int i = 0; i < names.size(); i++) {
                Date d = new Date();
                itemsList.add(new Items(names.get(i), dates.get(i), millisToString(rawDates.get(i).getTime() - d.getTime()),dueDateOn));
            }
        } catch (IOException e) {
            itemsList = new ArrayList<Items>();
            names = new ArrayList<String>();
            dates = new ArrayList<String>();
            rawDates = new ArrayList<Date>();
            countDownStrings = new ArrayList<String>();
            loggedHours = new ArrayList<Double>();

        }


    }

    private void writeItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        File todoFileDates = new File(filesDir, "tododates.txt");
        File todoFileHours = new File(filesDir, "todohours.txt");

        try {
            FileUtils.writeLines(todoFile, names);
            FileUtils.writeLines(todoFileDates, dates);
            FileUtils.writeLines(todoFileHours, loggedHours);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //method to parse date in milliseconds into formatted string
    private String millisToString(long milliseconds) {
        long millis;
        String s;
        millis = milliseconds;
        if (millis > 0) {
            long seconds = (millis / 1000) % 60;
            long minutes = (millis / (1000 * 60)) % 60;
            long hours = (millis / (1000 * 60 * 60)) % 24;
            long days = (millis / (1000 * 60 * 60 * 24));
            s = String.format("%d days \n%d:%02d:%02d", days, hours, minutes, seconds);
        } else {
            long seconds = (-millis / 1000) % 60;
            long minutes = (-millis / (1000 * 60)) % 60;
            long hours = (-millis / (1000 * 60 * 60)) % 24;
            long days = (-millis / (1000 * 60 * 60 * 24));
            if (days == 1)
                s = String.format("Overdue by \n%d day \n%d:%02d:%02d", days, hours, minutes, seconds);
            else
                s = String.format("Overdue by \n%d days \n%d:%02d:%02d", days, hours, minutes, seconds);
        }


        return s;
    }

    //method to parse string into date object
    private Date stringToDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy kk:mm", Locale.US);

        Date d = null;
        try {
            d = format.parse(date);
            return d;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }


}
