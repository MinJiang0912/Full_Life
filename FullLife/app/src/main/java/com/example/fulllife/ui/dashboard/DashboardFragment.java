package com.example.fulllife.ui.dashboard;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.fulllife.MainActivity;
import com.example.fulllife.R;
import com.example.fulllife.SharedViewModel;
import com.example.fulllife.databinding.FragmentDashboardBinding;
import com.example.fulllife.enums.UserType;
import com.example.fulllife.ui.DataHolder;

import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.Calendar;

import static android.Manifest.permission.CALL_PHONE;

import javax.sql.DataSource;

public class DashboardFragment extends Fragment {
    // Dashboard fragment which shows all the tasks and their timers
    // Tasks can be deleted from this page
    // The edit button puts you to another fragment to edit.
    private DashboardViewModel dashboardViewModel;
    private SharedViewModel sharedViewModel;
    private FragmentDashboardBinding binding;
    Button homeNav;
    Button addTask;
    Button edit;
    Button delete;
    Button callButton;
    int taskCount;
    TextView taskTitle;
    LinearLayout taskViewLayout;
    Calendar currentTime;
    int indexOfTaskView;
    AppCompatImageView imageView;
    private final String DEFAULT_PHONE = "09130000000";
    private String url = "https://www.budgetbytes.com/wp-content/uploads/2021/12/Chicken-Breast-Pan.jpg";

    private static final String TAG = "DashboardFragment";
    private Bitmap Bitmap;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        grabSharedViewModel();
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        // Button to go back to home
        homeNav = (Button) root.findViewById(binding.back.getId());
        homeNav.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Navigation.findNavController(view)
                                .navigate(R.id.action_navigation_dashboard_to_navigation_home2);
                    }
                });

        // Button functionality to add a task to the task list
        addTask = (Button) root.findViewById(binding.addTask.getId());
        taskViewLayout = (LinearLayout) root.findViewById(binding.TaskListLayout.getId());
        rebuildTaskList();

        // Add a task when button is clicked
        addTask.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        taskViewLayout.addView(createTask());
                        rebuildTaskList();
                    }
                });

        // Call button functionality
        callButton = (Button) root.findViewById(R.id.callButton);
        callButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View buttonTaskView) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + DEFAULT_PHONE));
                        if (ContextCompat.checkSelfPermission(getContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            startActivity(intent);
                        } else {
                            requestPermissions(new String[]{CALL_PHONE}, 1);
                        }
                    }
                });
        final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        if (sharedViewModel.getUserType() == UserType.PATIENT) {
            addTask.setVisibility(View.GONE);
        }
        return root;
    }

    public View createTask() {
        LayoutInflater inflater =
                (LayoutInflater)
                        getActivity()
                                .getApplicationContext()
                                .getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        View taskView = inflater.inflate(R.layout.task_menu, null);

        currentTime = Calendar.getInstance();
        TextView countDownText = (TextView) taskView.findViewById(R.id.completedTime);

        // Image Function
        imageView = (AppCompatImageView) taskView.findViewById(R.id.taskImage);
        imageView.invalidate();

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        sharedViewModel.createTask(bitmap);


        // Task Title Function
        taskTitle = (TextView) taskView.findViewById(R.id.TaskTitle);
        // This is to show in the UI
        taskTitle.setText("Please Edit this Task");
        return taskView;
    }

    public View rebuildTask(int i) {
        LayoutInflater inflater =
                (LayoutInflater)
                        getActivity()
                                .getApplicationContext()
                                .getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        // Rebuild the task list from shared View Model memory
        View taskView = inflater.inflate(R.layout.task_menu, null);
        sharedViewModel.setEditIndex(i);


        // Image Function
        imageView = (AppCompatImageView) taskView.findViewById(R.id.taskImage);
        imageView.setImageBitmap(sharedViewModel.getTaskBitmap());

        // Edit Button Function
        edit = (Button) taskView.findViewById(R.id.EditButton);
        edit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View buttonTaskView) {
                        indexOfTaskView =
                                ((LinearLayout) buttonTaskView.getParent().getParent().getParent())
                                        .indexOfChild((View) buttonTaskView.getParent().getParent());
                        sharedViewModel.setEditIndex(indexOfTaskView);
                        Navigation.findNavController(taskView)
                                .navigate(R.id.action_navigation_dashboard_to_navigation_edit);
                    }
                });
        // Delete button
        View root = binding.getRoot();
        taskViewLayout = (LinearLayout) root.findViewById(binding.TaskListLayout.getId());
        taskCount = sharedViewModel.getTotalTaskCount();
        delete = (Button) taskView.findViewById(R.id.DeleteButton);
        delete.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View taskView) {
                        indexOfTaskView =
                                ((LinearLayout) taskView.getParent().getParent().getParent())
                                        .indexOfChild((View) taskView.getParent().getParent());
                        Log.d("DashboardFragment", "Terminated");
                        sharedViewModel.removeTask(indexOfTaskView);
                        // sharedViewModel.setEditIndex(indexOfTaskView);
                        ((LinearLayout) taskView.getParent().getParent().getParent())
                                .removeView((View) taskView.getParent().getParent());
                    }
                });

        // Timer Function
        TextView countDownText = (TextView) taskView.findViewById(R.id.completedTime);

        new CountDownTimer(
                sharedViewModel.getTotalDurationMillisecond(), 100) {
            @Override
            public void onTick(long l) {
                // suppose to count from 1 minute
                long timeHours = TimeUnit.MILLISECONDS.toHours(l) % 24;
                long timeMinutes = TimeUnit.MILLISECONDS.toMinutes(l) % 60;
                long timeSeconds = TimeUnit.MILLISECONDS.toSeconds(l) % 60;
                String sDuration =
                        String.format(
                                Locale.ENGLISH, "%2d : %2d : %2d until task", timeHours, timeMinutes, timeSeconds);
                countDownText.setText(sDuration);
            }

            @Override
            public void onFinish() {
                // When finished, hide the textview
                countDownText.setVisibility(View.GONE);
            }
        }.start();

        // Task Title function
        taskTitle = (TextView) taskView.findViewById(R.id.TaskTitle);
        taskTitle.setText(sharedViewModel.getTaskText());
        //Log.d("Link",my.get(i).getUrl().toString());
        setVisibilities();
        return taskView;
    }

    public void rebuildTaskList() {
        //Sort before we build
        sharedViewModel.sort();
        taskViewLayout.removeAllViewsInLayout();
        taskCount = sharedViewModel.getTotalTaskCount();
        // Rebuild tasks based on memory from the shared View Model
        for (int i = 0; i < taskCount; i++) {
            taskViewLayout.addView(rebuildTask(i));
        }
        if (sharedViewModel.getUserType() == UserType.PATIENT || sharedViewModel.getUserType() == UserType.DEBUG) {
            ((MainActivity) getActivity()).setAlarm();
        }
    }

    private void setVisibilities() {
        if (sharedViewModel.getUserType() == UserType.PATIENT){
            edit.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
        }
    }

    private void grabSharedViewModel()
    {
        if (DataHolder.getInstance().getSharedViewModel() != null) {
            sharedViewModel = DataHolder.getInstance().getSharedViewModel();
        }
        else {
            sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
