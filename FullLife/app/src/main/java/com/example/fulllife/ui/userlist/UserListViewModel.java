package com.example.fulllife.ui.userlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserListViewModel extends ViewModel {
    // This represents the Notifications View Model for the notifications fragment
    private final MutableLiveData<String> mText;

    public UserListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is settings fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
