package com.bsscompclub.taskmanager;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class History extends AppCompatActivity {

    public ListView lvHistory;

    public ArrayAdapter<HistoryItems> customAdapter2;
    public ArrayList<HistoryItems> historyItemsList;

    public ArrayList<String> namesHistory;
    public ArrayList<String> datesHistory;
    public ArrayList<String> timeSpentHistory;

    public String currentName;
    public int currentPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setTitle("History");

        readHistory();

        historyItemsList = new ArrayList<HistoryItems>();


        for (int i = 0; i < namesHistory.size(); i++) {
            historyItemsList.add(new HistoryItems(namesHistory.get(i), datesHistory.get(i), timeSpentHistory.get(i)));
        }


        lvHistory = (ListView) findViewById(R.id.lvHistory);


        customAdapter2 = new CustomListAdapter2(this, R.layout.list_view_items, historyItemsList);
        lvHistory.setAdapter(customAdapter2);

        setupListViewListener();


    }

    private void setupListViewListener() {
        lvHistory.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, int pos, long id) {
                        // Remove the item within array at position
                        confirmDelete(pos);

                        // Return true consumes the long click event (marks it handled)
                        return true;
                    }

                });
    }

    private void confirmDelete(int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        currentName = namesHistory.get(pos);
        currentPos = pos;
        builder.setTitle("Confirm deletion");
        builder.setMessage("Are you sure you want to delete " + currentName + " from history?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), currentName + " deleted from history.", Toast.LENGTH_SHORT).show();
                namesHistory.remove(currentPos);
                datesHistory.remove(currentPos);
                timeSpentHistory.remove(currentPos);
                historyItemsList.remove(currentPos);
                // Refresh the adapter
                customAdapter2.notifyDataSetChanged();
                writeHistory();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.show();
    }

    public void onWipeAllData(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Wipe all data");
        builder.setMessage("Are you sure you want to delete all data, including all current tasks and history?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "All data has been deleted", Toast.LENGTH_SHORT).show();
                //deleting all history and current tasks
                namesHistory = new ArrayList<String>();
                datesHistory = new ArrayList<String>();
                timeSpentHistory = new ArrayList<String>();
                historyItemsList = new ArrayList<HistoryItems>();
                File filesDir = getFilesDir();
                File todoFileHistory = new File(filesDir, "todohistory.txt");
                File todoFileDatesHistory = new File(filesDir, "tododateshistory.txt");
                File todoFileHoursHistory = new File(filesDir, "todohourshistory.txt");
                File todoFile = new File(filesDir, "todo.txt");
                File todoFileDates = new File (filesDir, "tododates.txt");
                File todoFileHours = new File (filesDir, "todohours.txt");
                File todoFileNotes = new File (filesDir, "todonotes.txt");
                File todoFileNotesLines = new File (filesDir, "todonoteslines.txt");

                try {
                    FileUtils.writeLines(todoFileHistory, namesHistory);
                    FileUtils.writeLines(todoFileDatesHistory, datesHistory);
                    FileUtils.writeLines(todoFileHoursHistory, timeSpentHistory);
                    FileUtils.writeLines(todoFile, namesHistory);
                    FileUtils.writeLines(todoFileDates, namesHistory);
                    FileUtils.writeLines(todoFileHours, namesHistory);
                    FileUtils.writeLines(todoFileNotes, namesHistory);
                    FileUtils.writeLines(todoFileNotesLines, namesHistory);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Refresh the adapter by reinitializing it, notifydatasetchanged doesnt work

                customAdapter2 = new CustomListAdapter2(History.this, R.layout.list_view_items, historyItemsList);
                lvHistory.setAdapter(customAdapter2);

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.show();
    }

    public void onBack(View v) {
        finish();

    }

    private void readHistory() {
        File filesDir = getFilesDir();

        File todoFileHistory = new File(filesDir, "todohistory.txt");
        File todoFileDatesHistory = new File(filesDir, "tododateshistory.txt");
        File todoFileHoursHistory = new File(filesDir, "todohourshistory.txt");

        try {
            namesHistory = new ArrayList<String>(FileUtils.readLines(todoFileHistory));
            datesHistory = new ArrayList<String>(FileUtils.readLines(todoFileDatesHistory));
            timeSpentHistory = new ArrayList<String>(FileUtils.readLines(todoFileHoursHistory));
        } catch (IOException e) {
            namesHistory = new ArrayList<String>();
            datesHistory = new ArrayList<String>();
            timeSpentHistory = new ArrayList<String>();
        }
    }

    private void writeHistory() {
        File filesDir = getFilesDir();

        File todoFileHistory = new File(filesDir, "todohistory.txt");
        File todoFileDatesHistory = new File(filesDir, "tododateshistory.txt");
        File todoFileHoursHistory = new File(filesDir, "todohourshistory.txt");
        try {
            FileUtils.writeLines(todoFileHistory, namesHistory);
            FileUtils.writeLines(todoFileDatesHistory, datesHistory);
            FileUtils.writeLines(todoFileHoursHistory, timeSpentHistory);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
