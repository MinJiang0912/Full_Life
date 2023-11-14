package com.example.fulllife.ui.completedTasks.graph;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.fulllife.R;
import com.example.fulllife.SharedViewModel;
import com.example.fulllife.databinding.FragmentCompletedgraphBinding;
import com.example.fulllife.ui.CompletedTask;
import com.example.fulllife.ui.DataHolder;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class CompletedTasksGraphFragment extends Fragment {
    /**
     * This fragment shows a bar graph of the number of completed tasks, sorted into bucket
     * of date. Checks the last 5 recent dates.
     */
    private CompletedTaskGraphViewModel completedTaskViewGraphModel;
    private SharedViewModel sharedViewModel;
    private FragmentCompletedgraphBinding binding;
    Button homeNav;

    ArrayList<BarEntry> barArrayList;
    ArrayList<String> completedTasksDates;
    ArrayList<Integer> completedTaskNumbers;

    ValueFormatter formatter;
    private static final String TAG = "CompletedGraphTaskFragment";

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        completedTaskViewGraphModel = new ViewModelProvider(this).get(CompletedTaskGraphViewModel.class);
        grabSharedViewModel();
        binding = FragmentCompletedgraphBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Button to go back to home
        homeNav = (Button) root.findViewById(binding.back.getId());
        homeNav.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Navigation.findNavController(view)
                                .navigate(R.id.action_navigation_completedtasksgraph_to_navigation_completedtasks);
                    }
                });

        BarChart barChart = (BarChart) root.findViewById(R.id.completedTasksBarchart);
        getGraphData();
        BarDataSet barDataSet = new BarDataSet(barArrayList, "Completed Tasks History (Month-Day)");

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisRight().setEnabled(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setValueFormatter(formatter);
        barChart.getXAxis().setTextSize(32f);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        barChart.getDescription().setEnabled(true);

        return root;
    }

    private void getGraphData() {
        completedTasksDates = new ArrayList<String>();
        completedTaskNumbers = new ArrayList<Integer>();

        String dateCompleted;
        List<CompletedTask> completedTask = sharedViewModel.getCompletedTaskList();
        for (int i = 0; i < sharedViewModel.getTotalCompletedTaskCount(); i++) {
            dateCompleted = completedTask.get(i).getDateCompleted();
            if (completedTasksDates.contains(dateCompleted)) {
                int completedTaskIndex = completedTasksDates.indexOf(dateCompleted);
                Integer completedTaskNumber = completedTaskNumbers.get(completedTaskIndex);
                completedTaskNumber = completedTaskNumber + 1;
                completedTaskNumbers.set(completedTaskIndex, completedTaskNumber);
            }
            else
            {
                completedTasksDates.add(dateCompleted);
                completedTaskNumbers.add(1);
            }
        }
        barArrayList = new ArrayList<BarEntry>();
        // Reverse these two because we want the latest date to be on the right side.

        List<String> completedTasksDatesGraph;
        if (completedTasksDates.size() >= 5) {
            completedTasksDatesGraph = completedTasksDates.subList(0, 5);
        }
        else{
            completedTasksDatesGraph = completedTasksDates.subList(0, completedTasksDates.size());
        }
        // Reverse these two because we want the latest date to be on the right side.
        Collections.reverse(completedTasksDatesGraph);

        ArrayList<String> xAxisLabel = new ArrayList<>();
        for (int i = 0; i < completedTasksDatesGraph.size(); i++) {
            barArrayList.add(new BarEntry(i, completedTaskNumbers.get(i)));
            xAxisLabel.add(completedTasksDates.get(i));
        }

        formatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xAxisLabel.get((int) value);
            }
        };
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