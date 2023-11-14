package com.example.fulllife.ui;

import android.graphics.Bitmap;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CompletedTask {
    /**
     * Completed Task class that stores information of a completed task
     * Title, time hour, minute, id is expected to be provided
     * Everything else can be calculated inside the task from inputs and be given outside of class
     * if required.
     */
    Calendar currentTime;
    CharSequence taskText;
    Integer timeHourOfTask;
    Integer timeMinutesOfTask;
    Integer timeYearCompleted;
    Integer timeMonthCompleted;
    Integer timeDayCompleted;
    Integer timeHourCompleted;
    Integer timeMinutesCompleted;

    // for Sorting purposes
    long timeMillisecondsCompleted;

    // Useful to have the total duration in millisecond calculated from duration hour + Minute + Second
    String id;
    String dateCompleted;

    public CompletedTask(CharSequence taskText, Integer timeHour, Integer timeMinutes, String id) {
        this.taskText = taskText;
        this.timeHourOfTask = timeHour;
        this.timeMinutesOfTask = timeMinutes;
        this.id = id;

        currentTime = Calendar.getInstance();
        timeYearCompleted = currentTime.get(Calendar.YEAR);
        timeMonthCompleted = currentTime.get(Calendar.MONTH) + 1;
        timeDayCompleted = currentTime.get(Calendar.DAY_OF_MONTH);
        timeHourCompleted = currentTime.get(Calendar.HOUR_OF_DAY);
        timeMinutesCompleted = currentTime.get(Calendar.MINUTE);
        // for sorting purposes, should be in descending order
        timeMillisecondsCompleted = currentTime.getTimeInMillis();

        dateCompleted = timeMonthCompleted + "-" + timeDayCompleted;
    }

    public CompletedTask(CharSequence taskText, Integer timeHour, Integer timeMinutes, String id,
                         Integer timeYearCompleted, Integer timeMonthCompleted,
                         Integer timeDayCompleted, Integer timeHourCompleted,
                         Integer timeMinutesCompleted, long timeMillisecondsCompleted) {
        // Used in retrieval from firebase
        this.taskText = taskText;
        this.timeHourOfTask = timeHour;
        this.timeMinutesOfTask = timeMinutes;
        this.id = id;

        this.timeYearCompleted = timeYearCompleted;
        this.timeMonthCompleted = timeMonthCompleted;
        this.timeDayCompleted = timeDayCompleted;
        this.timeHourCompleted = timeHourCompleted;
        this.timeMinutesCompleted = timeMinutesCompleted;

        this.timeMillisecondsCompleted = timeMillisecondsCompleted;
        dateCompleted = timeMonthCompleted + "-" + timeDayCompleted;
    }

    public Integer getTimeYearOfTaskCompleted() {
        return timeYearCompleted;
    }

    public Integer getTimeMonthOfTaskCompleted() {
        return timeMonthCompleted;
    }

    public Integer getTimeDayOfTaskCompleted() {
        return timeDayCompleted;
    }

    public Integer getTimeHourOfTaskCompleted() {
        return timeHourCompleted;
    }

    public Integer getTimeMinutesOfTaskCompleted() {
        return timeMinutesCompleted;
    }

    public Integer getTimeHourOfTaskSet() {
        return timeHourOfTask;
    }

    public Integer getTimeMinutesOfTaskSet() {
        return timeMinutesOfTask;
    }

    public long getTimeMillisecondsCompleted() {return timeMillisecondsCompleted; }

    public String getDateCompleted() {
        return dateCompleted;
    }

    public CharSequence getTaskText() {
        return taskText;
    }

    public CharSequence getId() {
        return id;
    }

}
