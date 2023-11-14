package com.example.fulllife.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.fulllife.MainActivity;
import com.example.fulllife.R;
import com.example.fulllife.SharedViewModel;
import com.example.fulllife.databinding.FragmentSettingsBinding;
import com.example.fulllife.enums.UserType;
import com.example.fulllife.ui.DataHolder;

public class SettingsFragment extends Fragment {
  /**
   * Settings fragment that allows user to log out or change profile information
   */
    private FragmentSettingsBinding binding;
    private SharedViewModel sharedViewModel;
    Button logoutButton;
    Button editBack;
    Button profileButton;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);
        grabSharedViewModel();
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        logoutButton = (Button) root.findViewById(binding.logoutButton.getId());
        logoutButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) getActivity()).cancelAlarm();
                    Navigation.findNavController(view)
                        .navigate(R.id.action_navigation_settings_to_navigation_login);
                }
            });

        // Back button functionality
        editBack = (Button) root.findViewById(binding.editBackSettings.getId());
        editBack.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Navigation.findNavController(view)
                        .navigate(R.id.action_navigation_settings_to_navigation_home);
                }
            });

        // Profile Button functionality
        profileButton = (Button) root.findViewById(binding.profileButton.getId());
        profileButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Navigation.findNavController(view)
                        .navigate(R.id.action_navigation_settings_to_navigation_settings_profile);
                }
            });

        setVisibilities();

        return root;
    }

    private void setVisibilities() {
      if (sharedViewModel.getUserType() == UserType.PATIENT){
        profileButton.setVisibility(View.GONE);
      }
    }

    public void grabSharedViewModel()
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
