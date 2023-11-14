package com.example.fulllife.ui.settings.profile.patient;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfilePatientViewModel extends ViewModel {
    // This represents the Notifications View Model for the notifications fragment
    private final MutableLiveData<String> mText;

    public ProfilePatientViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is settings fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
