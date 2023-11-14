package com.example.fulllife;

import android.graphics.Bitmap;

public class firebaseTasks {
    /**
     * Simplified Task class
     * intended to be used directly with firebase
     */
    public String name;
    public String id;
    public String timeHour;
    public String timeMinute;
    Bitmap bitmapImage;
    public String url;
    public String audioUrl;

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public firebaseTasks(String name, String id, String timeHour, String timeMinute, Bitmap bitmapImage, String url, String audioUrl) {
        this.name = name;
        this.id = id;
        this.timeHour = timeHour;
        this.timeMinute = timeMinute;
        this.bitmapImage = bitmapImage;
        this.url = url;
        this.audioUrl = audioUrl;
    }

    public firebaseTasks(String name, String id, String timeHour, String timeMinute, String url,String audioUrl) {
        this.name = name;
        this.id = id;
        this.timeHour = timeHour;
        this.timeMinute = timeMinute;
        this.url = url;
        this.audioUrl = audioUrl;
    }

    public String getUrl() {
        return url;
    }

    public Bitmap getBitmapImage() {
        return bitmapImage;
    }

    public void setBitmapImage(Bitmap bitmapImage) {
        this.bitmapImage = bitmapImage;
    }

    public firebaseTasks(String name, String id, String timeHour, String timeMinute, Bitmap bitmapImage) {
        this.name = name;
        this.id = id;
    }

    public firebaseTasks(String name, String id, String timeHour, String timeMinute) {
        this.name = name;
        this.timeHour = timeHour;
        this.timeMinute = timeMinute;
        this.id = id;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
