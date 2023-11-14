package com.example.fulllife;

import android.graphics.Bitmap;

import java.util.Calendar;

public class firebaseCompletedTasks {
    /**
     * Simplified Completed Task class
     * intended to be used directly with firebase
     */
    public String name;
    public String id;

    Integer timeHourOfTask;
    Integer timeMinutesOfTask;

    Integer timeYearCompleted;
    Integer timeMonthCompleted;
    Integer timeDayCompleted;
    Integer timeHourCompleted;
    Integer timeMinutesCompleted;

    public firebaseCompletedTasks(String name, String id, Integer timeHourOfTask,
                                  Integer timeMinutesOfTask, Integer timeYearCompleted,
                                  Integer timeMonthCompleted, Integer timeDayCompleted,
                                  Integer timeHourCompleted, Integer timeMinutesCompleted) {
        this.name = name;
        this.id = id;

        this.timeHourOfTask = timeHourOfTask;
        this.timeMinutesOfTask = timeMinutesOfTask;

        this.timeYearCompleted = timeYearCompleted;
        this.timeMonthCompleted = timeMonthCompleted;
        this.timeDayCompleted = timeDayCompleted;
        this.timeHourCompleted = timeHourCompleted;
        this.timeMinutesCompleted = timeMinutesCompleted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
