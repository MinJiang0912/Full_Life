package com.example.fulllife.ui.completedTasks.graph;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CompletedTaskGraphViewModel extends ViewModel {
    // Dashboard view model for the dashboard fragment
    private final MutableLiveData<String> mText;

    public CompletedTaskGraphViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
