package com.example.fulllife;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import com.example.fulllife.SharedViewModel;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.fulllife.R;
import com.example.fulllife.SharedViewModel;
import com.example.fulllife.ui.DataHolder;
import com.example.fulllife.ui.Task;
import com.example.fulllife.ui.notifications.NotificationsFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AlarmReceiver extends BroadcastReceiver {
    private SharedViewModel sharedViewModel;
    private DatabaseReference database;
    private DataSnapshot snapshot;
    private Task taskfinished;
    /**
     * This alarm receiver will ring a tone and send a notification to the user that a task is required to be done
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "Reached AlarmReciever");
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        sharedViewModel= DataHolder.getInstance().getSharedViewModel();
        if (sharedViewModel.getImmediateTask() == null) {
          // If no immediate task, don't try to set an alarm
          return;
        }
        taskfinished  = sharedViewModel.getImmediateTask();
        Uri newUri = Uri.parse(taskfinished.getAudioUrl());
         Log.d("child", String.valueOf(newUri));

       Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(),newUri);

        Log.d("ChildRingtone", String.valueOf(r));
        r.play();

        Intent i = new Intent(context, NotificationsFragment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "fulllife")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Fulllife Alarm Manager")
                .setContentText("Have you done it")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(123, builder.build());


    }
}