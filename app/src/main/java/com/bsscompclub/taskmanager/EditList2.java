package com.bsscompclub.taskmanager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;


public class EditList2 extends AppCompatActivity {
    public ArrayList<String> names;
    public ArrayList<String> dates;
    public int pos;
    public ArrayList<Date> rawDates;
    public ArrayList<Double> loggedHours;
    public ArrayList<String> notes;
    public ArrayList<Integer> notesLines;

    //for history
    public ArrayList<String> namesHistory;
    public ArrayList<String> datesHistory;
    public ArrayList<String> timeSpentHistory;

    public Calendar myCalendar = Calendar.getInstance();

    public String currentName;
    public int currentPos;

    //for countdown
    public final Handler handler = new Handler();
    public final int delay = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list2);
        setTitle("Task Details");
        names =  (ArrayList<String>)getIntent().getSerializableExtra("names");
        dates =  (ArrayList<String>)getIntent().getSerializableExtra("dates");
        pos = (Integer)getIntent().getSerializableExtra("pos");
        rawDates = (ArrayList<Date>)getIntent().getSerializableExtra("rawDates");
        loggedHours = (ArrayList<Double>)getIntent().getSerializableExtra("loggedHours");

        notes = new ArrayList<String>();
        notesLines = new ArrayList<Integer>();
        readNotes();

        EditText editTextName = (EditText) findViewById(R.id.editTextName);
        editTextName.setText(names.get(pos));

        StringTokenizer st = new StringTokenizer(dates.get(pos));
        EditText editTextDate = (EditText) findViewById(R.id.editTextDate);
        editTextDate.setText(st.nextToken());
        EditText editTextTime = (EditText) findViewById(R.id.editTextTime);
        editTextTime.setText(st.nextToken());
        EditText editTextNotes = (EditText) findViewById(R.id.editTextNotes);
        editTextNotes.setText(notes.get(pos));

        //countdown first instance
        Date d = new Date();
        long millis = rawDates.get(pos).getTime()-d.getTime();

        TextView timeRemaining = (TextView) findViewById(R.id.textViewTimeRemaining);

        timeRemaining.setText(millisToStringOneLine(millis));//used modular arithmetic

        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                //things to be done every second
                //set all countDownString to match the current countdowns, using millisToString
                Date d = new Date();
                long millis = rawDates.get(pos).getTime()-d.getTime();

                TextView timeRemaining = (TextView) findViewById(R.id.textViewTimeRemaining);

                timeRemaining.setText(millisToStringOneLine(millis));//used modular arithmetic

                handler.postDelayed(this, delay);
            }
        }, delay);

        TextView hoursSpent = (TextView) findViewById(R.id.textViewHoursSpent);
        hoursSpent.setText(""+loggedHours.get(pos));

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelTime();
            }

        };

        editTextDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(EditList2.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                EditText editTextDate = (EditText) findViewById(R.id.editTextDate);

            }
        });

        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hour);
                myCalendar.set(Calendar.MINUTE, minute);
                updateLabelDate();
            }

        };


        editTextTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new TimePickerDialog(EditList2.this, time, myCalendar
                        .get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE),
                        true).show();

            }
        });

    }

    public void updateLabelTime() {
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        EditText editTextDate = (EditText) findViewById(R.id.editTextDate);

        editTextDate.setText(sdf.format(myCalendar.getTime()));
    }

    public void updateLabelDate() {
        String myFormat = "kk:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        EditText editTextTime = (EditText) findViewById(R.id.editTextTime);

        editTextTime.setText(sdf.format(myCalendar.getTime()));
    }
    //method to parse date in milliseconds into formatted string of one line only
    private String millisToStringOneLine (long milliseconds){
        long millis;
        String s;
        millis = milliseconds;
        if (millis>0) {
            long seconds = (millis / 1000) % 60;
            long minutes = (millis / (1000 * 60)) % 60;
            long hours = (millis / (1000 * 60 * 60)) % 24;
            long days = (millis / (1000 * 60 * 60 * 24));
            s = String.format("%d days %d:%02d:%02d", days, hours, minutes, seconds);
        } else {
            long seconds = (-millis / 1000) % 60;
            long minutes = (-millis / (1000 * 60)) % 60;
            long hours = (-millis / (1000 * 60 * 60)) % 24;
            long days = (-millis / (1000 * 60 * 60 * 24));
            if (days==1)
                s = String.format("Overdue by %d day %d:%02d:%02d", days, hours, minutes, seconds);
            else
                s = String.format("Overdue by %d days %d:%02d:%02d", days, hours, minutes, seconds);
        }


        return s;
    }

    private void writeItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        File todoFileDates = new File (filesDir, "tododates.txt");
        File todoFileHours = new File (filesDir, "todohours.txt");
        File todoFileNotes = new File (filesDir, "todonotes.txt");
        File todoFileNotesLines = new File (filesDir, "todonoteslines.txt");
        try {
            FileUtils.writeLines(todoFile, names);
            FileUtils.writeLines(todoFileDates, dates);
            FileUtils.writeLines(todoFileHours, loggedHours);
            FileUtils.writeLines(todoFileNotes, notes);
            FileUtils.writeLines(todoFileNotesLines, notesLines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void readNotes(){
        File filesDir = getFilesDir();
        File todoFileNotes = new File (filesDir, "todonotes.txt");
        File todoFileNotesLines = new File (filesDir, "todonoteslines.txt");

        try{
            //to read notes from file to arraylist
            ArrayList<String> tempNotesLines = new ArrayList<String>(FileUtils.readLines(todoFileNotesLines));
            notesLines = new ArrayList<Integer>();
            for (String s : tempNotesLines) notesLines.add(Integer.valueOf(s));
            //one line per element in tempNotes
            ArrayList<String> tempNotes = new ArrayList<String>(FileUtils.readLines(todoFileNotes));
            notes = new ArrayList<String>();
            //putting multi-line strings into notes
            int counter = 0;
            for(int i=0;i<notesLines.size();i++){
                notes.add("");
                for(int j=0;j<notesLines.get(i);j++){
                    if(j==notesLines.get(i)-1){
                        notes.set(i,notes.get(i)+tempNotes.get(counter));
                    }else{
                        notes.set(i,notes.get(i)+tempNotes.get(counter)+"\n");
                    }
                    counter++;
                }
            }
        }catch (IOException e){
            notes = new ArrayList<String>();
            notesLines = new ArrayList<Integer>();
        }
    }

    public void onPlus (View v){
        loggedHours.set(pos, loggedHours.get(pos)+0.5);
        //update total logged hours
        TextView hoursSpent = (TextView) findViewById(R.id.textViewHoursSpent);
        hoursSpent.setText(""+loggedHours.get(pos));
        writeItems();
    }

    public void onMinus (View v){
        if(loggedHours.get(pos)>0){
            loggedHours.set(pos, loggedHours.get(pos)-0.5);
            //update total logged hours
            TextView hoursSpent = (TextView) findViewById(R.id.textViewHoursSpent);
            hoursSpent.setText(""+loggedHours.get(pos));
            writeItems();
        }

    }

    public void onSave (View v){

        EditText editTextName = (EditText) findViewById(R.id.editTextName);
        EditText editTextDate = (EditText) findViewById(R.id.editTextDate);
        EditText editTextTime = (EditText) findViewById(R.id.editTextTime);
        EditText editTextNotes = (EditText) findViewById(R.id.editTextNotes);


        if(!editTextName.getText().toString().isEmpty()) {
            //storing temporary info so it can be reinserted in the correct place
            String tempDateString = editTextDate.getText().toString() + " " + editTextTime.getText().toString();
            Date tempDate = stringToDate(tempDateString);
            String tempName = editTextName.getText().toString();
            Double tempLoggedHours = loggedHours.get(pos);
            String tempNotes = editTextNotes.getText().toString();
            int numLines = tempNotes.length() - tempNotes.replace("\n", "").length()+1;


            //removing from lists
            handler.removeCallbacksAndMessages(null);
            names.remove(pos);
            dates.remove(pos);
            loggedHours.remove(pos);
            rawDates.remove(pos);
            notes.remove(pos);
            notesLines.remove(pos);

            //iterate through remaining list
            int tempSize = names.size();
            if(tempSize==0){
                names.add(tempName);
                dates.add(tempDateString);
                loggedHours.add(tempLoggedHours);
                rawDates.add(tempDate);
                notes.add(tempNotes);
                notesLines.add(numLines);
                pos=names.size()-1;
            }else {
                for (int i = 0; i < tempSize; i++) {
                    if (tempDate.getTime() < stringToDate(dates.get(i)).getTime()) {
                        names.add(i, tempName);
                        dates.add(i, tempDateString);
                        loggedHours.add(i, tempLoggedHours);
                        rawDates.add(i, tempDate);
                        notes.add(i, tempNotes);
                        notesLines.add(i, numLines);
                        pos = i;
                        break;
                    } else if (i == tempSize - 1) {
                        names.add(tempName);
                        dates.add(tempDateString);
                        loggedHours.add(tempLoggedHours);
                        rawDates.add(tempDate);
                        notes.add(tempNotes);
                        notesLines.add(numLines);
                        pos = i + 1;
                        break;
                    }
                }
            }
            writeItems();

            //set all countDownString to match the current countdowns, using millisToString
            Date d = new Date();
            long millis = rawDates.get(pos).getTime() - d.getTime();

            TextView timeRemaining = (TextView) findViewById(R.id.textViewTimeRemaining);


            timeRemaining.setText(millisToStringOneLine(millis));//used modular arithmetic
            Toast.makeText(getApplicationContext(), "Changes saved.", Toast.LENGTH_SHORT).show();

            //restart handler
            handler.postDelayed(new Runnable(){
                @Override
                public void run(){
                    //things to be done every second
                    //set all countDownString to match the current countdowns, using millisToString
                    Date d = new Date();
                    long millis = rawDates.get(pos).getTime()-d.getTime();

                    TextView timeRemaining = (TextView) findViewById(R.id.textViewTimeRemaining);


                    timeRemaining.setText(millisToStringOneLine(millis));//used modular arithmetic


                    handler.postDelayed(this, delay);
                }
            }, delay);

        }else{
            Toast.makeText(getApplicationContext(), "Cannot leave name, date, or time empty.", Toast.LENGTH_SHORT).show();

        }

    }

    public void onDone (View v){

        confirmDone(pos);


    }

    private void readHistory (){
        File filesDir = getFilesDir();

        File todoFileHistory = new File(filesDir, "todohistory.txt");
        File todoFileDatesHistory = new File(filesDir, "tododateshistory.txt");
        File todoFileHoursHistory = new File(filesDir, "todohourshistory.txt");

        try{
            namesHistory = new ArrayList<String>(FileUtils.readLines(todoFileHistory));
            datesHistory = new ArrayList<String>(FileUtils.readLines(todoFileDatesHistory));
            timeSpentHistory = new ArrayList<String>(FileUtils.readLines(todoFileHoursHistory));
        }catch (IOException e){
            namesHistory = new ArrayList<String>();
            datesHistory = new ArrayList<String>();
            timeSpentHistory = new ArrayList<String>();
        }
    }

    private void writeHistory(){
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

    public void onDelete (View v){
        confirmDelete(pos);

    }
    private void confirmDelete(int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        currentName = names.get(pos);
        currentPos = pos;
        builder.setTitle("Confirm deletion");
        builder.setMessage("Are you sure you want to delete " + currentName + "? It will not be saved to history.");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), currentName + " deleted.", Toast.LENGTH_SHORT).show();
                handler.removeCallbacksAndMessages(null);
                names.remove(currentPos);
                dates.remove(currentPos);
                loggedHours.remove(currentPos);
                notes.remove(currentPos);
                notesLines.remove(currentPos);
                rawDates.remove(currentPos);
                writeItems();

                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.show();
    }
    private void confirmDone(int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        currentName = names.get(pos);
        currentPos = pos;
        builder.setTitle("Confirm task completion");
        builder.setMessage("Are you sure you want to mark " + currentName + " as complete? It will be saved to history.");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), currentName + " completed and saved to history", Toast.LENGTH_SHORT).show();
                readHistory();


                namesHistory.add(0,names.get(currentPos));
                datesHistory.add(0,dates.get(currentPos));
                timeSpentHistory.add(0,loggedHours.get(currentPos).toString());

                writeHistory();

                //delete
                handler.removeCallbacksAndMessages(null);
                names.remove(currentPos);
                dates.remove(currentPos);
                loggedHours.remove(currentPos);
                notes.remove(currentPos);
                notesLines.remove(currentPos);
                rawDates.remove(currentPos);
                writeItems();

                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.show();
    }

    public void onBack (View v){
        finish();
    }

    private Date stringToDate (String date) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy kk:mm");

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
