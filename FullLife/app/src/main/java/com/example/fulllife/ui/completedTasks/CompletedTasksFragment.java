package com.example.fulllife.ui.completedTasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.fulllife.R;
import com.example.fulllife.SharedViewModel;
import com.example.fulllife.databinding.FragmentCompletedtaskBinding;
import com.example.fulllife.ui.CompletedTask;
import com.example.fulllife.ui.DataHolder;

import java.util.List;


public class CompletedTasksFragment extends Fragment {
    /**
     * This completed task fragment will show all the history of completed tasks
     * Information is saved onto firebase
     */
    private CompletedTaskViewModel completedTaskViewModel;
    private SharedViewModel sharedViewModel;
    private FragmentCompletedtaskBinding binding;
    private LinearLayout completedTaskLayout;
    Button homeNav;
    Button graphNav;

    Integer completedTaskCount;

    private static final String TAG = "CompletedTaskFragment";

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        completedTaskViewModel = new ViewModelProvider(this).get(CompletedTaskViewModel.class);
        grabSharedViewModel();
        binding = FragmentCompletedtaskBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Button to go back to home
        homeNav = (Button) root.findViewById(binding.back.getId());
        homeNav.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Navigation.findNavController(view)
                                .navigate(R.id.action_navigation_completedtasks_to_navigation_home);
                    }
                });

        // Button to go to completed Tasks Graph
        graphNav = (Button) root.findViewById(binding.graphButton.getId());
        graphNav.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Navigation.findNavController(view)
                        .navigate(R.id.action_navigation_completedtasks_to_navigation_completedtasksgraph);
                }
            });


        completedTaskLayout = (LinearLayout) root.findViewById(binding.CompletedTaskListLayout.getId());
        buildCompletedTasksList();

        return root;
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

    private void buildCompletedTasksList()
    {
        completedTaskLayout.removeAllViewsInLayout();
        completedTaskCount = sharedViewModel.getTotalCompletedTaskCount();
        List<CompletedTask> completedTaskList = sharedViewModel.getCompletedTaskList();
        // Rebuild tasks based on memory from the shared View Model
        for (int i = 0; i < completedTaskCount; i++) {
            completedTaskLayout.addView(buildTask(completedTaskList.get(i)));
        }
    }

    private View buildTask(CompletedTask completedTask) {
        LayoutInflater inflater =
            (LayoutInflater)
                getActivity()
                    .getApplicationContext()
                    .getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        // Rebuild the task list from shared View Model memory
        View taskCompletedView = inflater.inflate(R.layout.task_completed, null);

        View root = binding.getRoot();

        // Task Title function
        TextView taskTitle = (TextView) taskCompletedView.findViewById(R.id.TaskTitle);
        TextView taskTime = (TextView) taskCompletedView.findViewById(R.id.completedTime);

        taskTitle.setText(completedTask.getTaskText());
        String timerText = "Completion Date: " + completedTask.getTimeYearOfTaskCompleted() + "-"
            + completedTask.getTimeMonthOfTaskCompleted() + "-" + completedTask.getTimeDayOfTaskCompleted() + ", "
            + completedTask.getTimeHourOfTaskCompleted() + ":" + completedTask.getTimeMinutesOfTaskCompleted();

        taskTime.setText(timerText);

        //Log.d("Link",my.get(i).getUrl().toString());
        return taskCompletedView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
