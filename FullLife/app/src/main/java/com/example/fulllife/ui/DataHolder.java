package com.example.fulllife.ui;

import android.graphics.Bitmap;

import com.example.fulllife.SharedViewModel;

import android.util.Log;

import com.example.fulllife.SharedViewModel;

public class DataHolder {
    // For transferring one task to the Alarm activity
    private Task data;
    private Bitmap image;
    private Boolean complete;
    private SharedViewModel sharedViewModel;

    public Task getData() {
        return data;
    }

    public SharedViewModel getSharedViewModel() { return sharedViewModel; }

    public Bitmap getImage() { return image; }

    public Boolean getComplete() { return complete; }

    public void setData(Task data) {
        this.data = data;
    }

    public void setSharedViewModel(SharedViewModel sharedViewModel) { this.sharedViewModel = sharedViewModel; }

    public void setImage(Bitmap image) { this.image = image; }

    public void setComplete(Boolean complete) {this.complete = complete; }

    private static final DataHolder holder = new DataHolder();

    public static DataHolder getInstance() {
        return holder;
    }
}
