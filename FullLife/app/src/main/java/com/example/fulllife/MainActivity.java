package com.example.fulllife;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.fulllife.enums.UserType;
import com.example.fulllife.ui.DataHolder;
import com.example.fulllife.ui.Task;
import com.example.fulllife.ui.alarm.AlarmActivity;
import com.example.fulllife.ui.dashboard.DashboardViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.fulllife.SharedViewModel;

import com.example.fulllife.databinding.ActivityMainBinding;

import java.sql.Time;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    /**
     * This main activity currently provides an overlay and is present in every fragment
     * It is shown as the nav bar at the bottom of the screen
     * <p>
     * Nav bar will be depreciated
     * Alarm gets set in here when activity is created
     * Also grabs information from firebase
     */
    private static final String TAG = "MainActivity";
    // This main activity currently provides an overlay and is present in every fragment
    // It is shown as the nav bar at the bottom of the screen
    private ActivityMainBinding binding;
    private SharedViewModel sharedViewModel;
    Calendar alarm;
    long milisecondsTillNextAlarm;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    Handler alertPage;
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        grabSharedViewModel();
        initUsers();
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.d(TAG, "Oncreate sucessful");
        DataHolder.getInstance().setSharedViewModel(sharedViewModel);
        // This parts sets the alarm
        //sharedViewModel.alarmIndex = 0;
        createNotificationChannel();
        // Set up alarm variables and then set them
        // Firebase tasks are already retrieved at this point, so alarm will be set to the most
        // Immediate task
        setUpAlarm();
        setAlarm();
    }

    public void grabSharedViewModel()
    {
        if (DataHolder.getInstance().getSharedViewModel() != null) {
            sharedViewModel = DataHolder.getInstance().getSharedViewModel();
        }
        else {
            sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
            // Get data from Firebase
            sharedViewModel.getFirebaseAndStore(getApplicationContext());
            sharedViewModel.setUserType(UserType.DEBUG);
        }
    }

    private void setUpAlarm() {
        // Tasks sets up alarm variables
        Log.d("child","here3");
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this.getApplicationContext(), AlarmReceiver.class);

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        alertPage = new Handler();
    }

    public void setAlarm() {
        // Set the alarm to the most immediate task
        Log.d("child","here4");
        if (sharedViewModel.getTotalTaskCount() == 0) {
            // If no task then don't set an alarm
            return;
        }
        // Cancel latest alarm
        alarmManager.cancel(pendingIntent);

        alertPage.removeCallbacksAndMessages(null);
        // TODO: have an alarm set for every task
        // Will only ring for the closest task
        milisecondsTillNextAlarm = sharedViewModel.getTotalDurationMillisecond(0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + milisecondsTillNextAlarm, pendingIntent);
        alertPage.postDelayed(new Runnable() {
                @Override
                public void run() {
                    /* Create an Intent that will start the Menu-Activity. */
                    Intent mainIntent = new Intent(getApplicationContext(), AlarmActivity.class);
                    Log.d("child","here");
                    DataHolder.getInstance().setSharedViewModel(sharedViewModel);
                    DataHolder.getInstance().getSharedViewModel();
                    Log.d("child","here1");
                    DataHolder.getInstance().setData(sharedViewModel.getImmediateTask());
                    DataHolder.getInstance().setImage(sharedViewModel.getCaretakerImage());
                    DataHolder.getInstance().setComplete(Boolean.FALSE);
                    DataHolder.getInstance().setSharedViewModel(sharedViewModel);
                    startActivity(mainIntent);
                    finish();
                }
            }, milisecondsTillNextAlarm);
        Toast.makeText(this, "Alarm set Successfully", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Alarm got set called");
        if (DataHolder.getInstance().getComplete() == Boolean.TRUE){
            Log.d(TAG, "Complete is triggered");
        }
    }

    private void initUsers() {
        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
            R.drawable.abstract_user_flat_4);
        if (sharedViewModel.getCaretakerImage() == null) {
            sharedViewModel.setCaretakerImage(icon);
        }
        if (sharedViewModel.getPatientImage() == null) {
            sharedViewModel.setPatientImage(icon);
        }
    }

    public void cancelAlarm(){
        // Cancel Alarm
        alarmManager.cancel(pendingIntent);
    }

    private void createNotificationChannel() {
        Log.d(TAG, "CreatChannel got called");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "foxandroidReminderChannel";
            String description = "Channel For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("fulllife", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.d(TAG, "CreatChannel got created");

        }

    }

}

