package com.example.fulllife.ui.edit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EditViewModel extends ViewModel {
    // This represents the Edit View Model for the edit fragment
    private final MutableLiveData<String> mText;

    public EditViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is edit fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
