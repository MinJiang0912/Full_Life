package com.example.fulllife.ui;

import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;

import com.example.fulllife.firebaseTasks;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class Task {
    /**
     * Task class that stores information and calculates everything needed in a task
     * Title, time hour, minutes, bitmap image, id is expected to be provided
     * Everything else can be calculated inside the task from inputs and be given outside of class
     * if required.
     */
    Calendar currentTime;
    CharSequence taskText;
    Integer timeHour;
    Integer timeMinutes;
    Integer durationHour;
    Integer durationMinute;
    Integer durationSecond;
    // Useful to have the total duration in millisecond calculated from duration hour + Minute + Second
    long totalDurationMillisecond;
    Bitmap bitmapImage;
    String id;
    String url;
    String audioUrl;


    public Task(CharSequence taskText, Integer timeHour, Integer timeMinutes, Bitmap bitmapImage, String id, String url, String audioUrl) {
        this.taskText = taskText;
        this.timeHour = timeHour;
        this.timeMinutes = timeMinutes;
        this.bitmapImage = bitmapImage;
        this.id = id;
        this.url = url;
        this.audioUrl = audioUrl;
        calculateDuration();
    }



    public String getUrl() {
        return url;
    }

    public Task(CharSequence taskText, Integer timeHour, Integer timeMinutes, String id, String url, Bitmap bitmapImage) {
        this.taskText = taskText;
        this.timeHour = timeHour;
        this.timeMinutes = timeMinutes;
        this.bitmapImage = bitmapImage;
        this.id = id;
        this.url = url;
        calculateDuration();
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Task(CharSequence taskText, Integer timeHour, Integer timeMinutes, String id, String url) {
        this.taskText = taskText;
        this.timeHour = timeHour;
        this.timeMinutes = timeMinutes;
        this.id = id;
        this.url = url;
        calculateDuration();
    }



    public Task(CharSequence taskText, Integer timeHour, Integer timeMinutes, Bitmap bitmap, String id) {
        // In-app Task where bitmap is available
        // used when a task is generated in-app
        this.taskText = taskText;
        this.timeHour = timeHour;
        this.timeMinutes = timeMinutes;
        this.bitmapImage = bitmap;
        this.id = id;
        calculateDuration();
    }

//    public Task(CharSequence taskText, Integer timeHour, Integer timeMinutes, String id) {
//        // Currently firebase does not store our bitmap images, so this is used when it comes to
//        // reading tasks from firebase, and then creating an in-app task.
//        this.taskText = taskText;
//        this.timeHour = timeHour;
//        this.timeMinutes = timeMinutes;
//        this.id = id;
//        calculateDuration();
//    }

    public Task(CharSequence taskText, Integer timeHour, Integer timeMinutes, String id, String url,Bitmap bitmap,String audioUrl) {
        this.taskText = taskText;
        this.timeHour = timeHour;
        this.timeMinutes = timeMinutes;
        this.id = id;
        this.url = url;
        this.bitmapImage= bitmap;
        this.audioUrl = audioUrl;
        calculateDuration();
    }

    public Task(CharSequence taskText, Integer timeHour, Integer timeMinutes) {
        // Used in debug alarm
        // Will be depreciated
        this.taskText = taskText;
        this.timeHour = timeHour;
        this.timeMinutes = timeMinutes;
        calculateDuration();
    }

    public CharSequence getTaskText() {
        return taskText;
    }

    public long getTotalDurationMillisecond() {
        calculateDuration();
        return totalDurationMillisecond;
    }

    public Bitmap getBitmapImage() {
        return bitmapImage;
    }

    public void setTimeText(int defaultHours, int defaultMinutes) {
        // Default function during addTask
        timeHour = defaultHours;
        timeMinutes = defaultMinutes;
        // Find out duration
        calculateDuration();
    }

    public void setTimeHour(int hour) {
        // Default function during addTask
        timeHour = hour;
        calculateDuration();
    }

    public void setTimeMinute(int minute) {
        // Default function during addTask
        timeMinutes = minute;
        calculateDuration();
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public Integer getTimeHourOfTask() {
        return timeHour;
    }

    public Integer getTimeMinutesOfTask() {
        return timeMinutes;
    }

    public String getTaskId() {
        return id;
    }

    public void setTaskText(CharSequence string) {
        taskText = string;
    }

    public void setBitmapImage(Bitmap bitmap) {
        bitmapImage = bitmap;
    }

    private void calculateDuration() {
        // Calculate duration of everything
        // The main thing is to get the duration of milliseconds till next task to make it
        // compatible with java timing functions
        currentTime = Calendar.getInstance();
        // Done after adding or editing any task time
        if (timeHour * 60 * 60 + timeMinutes * 60
                < currentTime.get(Calendar.HOUR_OF_DAY) * 60 * 60 + currentTime.get(Calendar.MINUTE) * 60) {
            durationHour =
                    (timeHour) - currentTime.get(Calendar.HOUR_OF_DAY) + 23;
        } else {
            durationHour = (timeHour) - currentTime.get(Calendar.HOUR_OF_DAY);
        }
        if (timeMinutes < currentTime.get(Calendar.MINUTE)) {
            durationMinute =
                    (timeMinutes) - currentTime.get(Calendar.MINUTE) + 59;
        } else {
            durationMinute =
                    (timeMinutes) - currentTime.get(Calendar.MINUTE) - 1;
        }
        durationSecond = 60 - currentTime.get(Calendar.SECOND);
        // Useful to have durations in milliseconds as Java timing functions depends on it
        totalDurationMillisecond = TimeUnit.HOURS.toMillis(durationHour) + TimeUnit.MINUTES.toMillis(durationMinute) + TimeUnit.SECONDS.toMillis(durationSecond);
    }

    public String getId() {
        return id;
    }
}
