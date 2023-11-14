package com.example.fulllife.ui.alarm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.ViewModelProvider;

import com.example.fulllife.MainActivity;
import com.example.fulllife.R;
import com.example.fulllife.SharedViewModel;
import com.example.fulllife.databinding.ActivityAlarmBinding;
import com.example.fulllife.ui.DataHolder;
import com.example.fulllife.ui.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class AlarmActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    // This main activity currently provides an overlay and is present in every fragment
    // It is shown as the nav bar at the bottom of the screen
    private ActivityAlarmBinding binding;
    SharedViewModel sharedViewModel;
    Button backNav;
    Button completeButton;
    TextView taskTitle;
    AppCompatImageView alarmImageView;
    AppCompatImageView caregiverImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlarmBinding.inflate(getLayoutInflater());
        //sharedViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(SharedViewModel.class);]
        //sharedViewModel = new ViewModelProvider(TAG.get).get(SharedViewModel.class);
        setContentView(binding.getRoot());
        // Get saved data thrown
        // It should be the most immediate task, which is the one that likely rang the alarm
        DataHolder data = DataHolder.getInstance();
        sharedViewModel =  data.getSharedViewModel();
        // Button to go back to home
        backNav = (Button) binding.getRoot().findViewById(R.id.backAlarm);
        backNav.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }
                });

        // Button to complete task
        completeButton = (Button) binding.getRoot().findViewById(R.id.completeButton);
        completeButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                    sharedViewModel.completeTask(0);
                    data.setSharedViewModel(sharedViewModel);
                    startActivity(mainIntent);
                    finish();
                }
            });


        // Put text
        taskTitle = (TextView) binding.getRoot().findViewById(R.id.alarmPageInfo);

        Log.d(TAG, "Oncreate sucessful");

        // Image
        alarmImageView = (AppCompatImageView) binding.getRoot().findViewById(R.id.alarmImage);
        caregiverImageView = (AppCompatImageView) binding.getRoot().findViewById(R.id.alarmCaregiverImage);

        // If task exists, set page attributes to the text
        taskTitle.setText(data.getData().getTaskText());
        alarmImageView.setImageBitmap(data.getData().getBitmapImage());
        caregiverImageView.setImageBitmap(data.getImage());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

}

