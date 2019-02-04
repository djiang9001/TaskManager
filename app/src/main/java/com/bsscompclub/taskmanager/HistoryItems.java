package com.bsscompclub.taskmanager;

/**
 * Created by U0006008 on 12/04/2018.
 */

public class HistoryItems {

    private String name;
    private String date;
    private String timeSpent;

    public HistoryItems(String name, String date, String timeSpent) {
        this.name = name;
        this.date = date;
        this.timeSpent = timeSpent;
    }

    public String getNameHistory() {
        return name;
    }

    public void setNameHistory(String name) {
        this.name = name;
    }

    public String getDateHistory() {
        return date;
    }

    public void setDateHistory(String date) {
        this.date = date;
    }

    public String getTimeSpent() { return timeSpent;}

    public void setTimeSpent() { this.timeSpent = timeSpent;}
}
