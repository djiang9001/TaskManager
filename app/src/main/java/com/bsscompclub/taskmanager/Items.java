package com.bsscompclub.taskmanager;

/**
 * Created by danieljng on 2018-03-18.
 *
 *
 * 2018-03-28 Need to add a countdown object to Items, so that it can be displayed
 */

public class Items {

    private String name;
    private String date;
    private String countDownStrings;
    private boolean dueDateOn;

    public Items(String name, String date, String countDownStrings, boolean dueDateOn) {
        this.name = name;
        this.date = date;
        this.countDownStrings = countDownStrings;
        this.dueDateOn = dueDateOn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCount() { return countDownStrings;}

    public void setCount() { this.countDownStrings = countDownStrings;}

    public boolean getDueDateOn() { return dueDateOn;}

    public void setDueDateOn() { this.dueDateOn = dueDateOn;}

}
