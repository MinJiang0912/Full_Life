package com.example.fulllife.ui.completedTasks;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CompletedTaskViewModel extends ViewModel {
    // Dashboard view model for the dashboard fragment
    private final MutableLiveData<String> mText;

    public CompletedTaskViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
