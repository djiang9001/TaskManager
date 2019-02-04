package com.bsscompclub.taskmanager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
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

public class EditList extends AppCompatActivity {

    public ArrayList<Items> itemsList;
    public ArrayList<String> names;
    public ArrayList<String> dates;
    public ArrayList<Double> loggedHours;
    public ArrayList<String> notes;
    public ArrayList<Integer> notesLines;
    public Calendar myCalendar = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);
        setTitle("Add New Task");
        names =  (ArrayList<String>)getIntent().getSerializableExtra("names");
        dates =  (ArrayList<String>)getIntent().getSerializableExtra("dates");
        loggedHours = (ArrayList<Double>)getIntent().getSerializableExtra("loggedHours");
        notes = new ArrayList<String>();
        notesLines = new ArrayList<Integer>();
        readNotes();

        //dialog box stuff
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
        EditText editTextDate = (EditText) findViewById(R.id.editTextDate);

        editTextDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(EditList.this, date, myCalendar
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

        EditText editTextTime = (EditText) findViewById(R.id.editTextTime);

        editTextTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new TimePickerDialog(EditList.this, time, myCalendar
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

    public void onSave (View v) {
        EditText editTextName = (EditText) findViewById(R.id.editTextName);
        String nameText = editTextName.getText().toString();
        EditText editTextDate = (EditText) findViewById(R.id.editTextDate);
        String dateText = editTextDate.getText().toString();
        EditText editTextTime = (EditText) findViewById(R.id.editTextTime);
        String timeText = editTextTime.getText().toString();
        EditText editTextNotes = (EditText) findViewById(R.id.editTextNotes);
        String noteText = editTextNotes.getText().toString();
        int numLines = noteText.length() - noteText.replace("\n", "").length() + 1;


        if(!nameText.isEmpty()&&!dateText.isEmpty()&&!timeText.isEmpty()) {
            //record data and insert into correct position in list
            if (names.size() > 0) {
                int temp = names.size();
                for (int i = 0; i < temp; i++) {
                    if (stringToDate(dateText + " " + timeText).getTime() < stringToDate(dates.get(i)).getTime()) {
                        names.add(i, nameText);
                        dates.add(i, dateText + " " + timeText);
                        loggedHours.add(i, (Double) 0.0);
                        notes.add(i, noteText);
                        notesLines.add(i, numLines);
                        break;
                    } else if (i == temp - 1) {
                        names.add(nameText);
                        dates.add(dateText + " " + timeText);
                        loggedHours.add((Double) 0.0);
                        notes.add(noteText);
                        notesLines.add(numLines);
                    }
                }
            } else {
                names.add(nameText);
                dates.add(dateText + " " + timeText);
                loggedHours.add((Double) 0.0);
                notesLines.add(numLines);
                notes.add(noteText);
            }


            //customAdapter.add(new Items (itemText, dateText));
            writeItems();
            finish();
        }else{
            Toast.makeText(getApplicationContext(), "Cannot leave name, date, or time empty", Toast.LENGTH_SHORT).show();

        }
}

    public void onCancel (View v){
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
}
