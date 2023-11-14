package com.example.fulllife.ui.settings.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.fulllife.MainActivity;
import com.example.fulllife.R;
import com.example.fulllife.databinding.FragmentSettingsProfileBinding;

public class ProfileFragment extends Fragment {
    /**
     * Profile fragment that allows user to change to change caretaker or patient information
     */
    private FragmentSettingsProfileBinding binding;
    Button caretakerButton;
    Button patientButton;
    Button backButton;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel settingsViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentSettingsProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        backButton = (Button) root.findViewById(binding.editBackProfile.getId());
        backButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Navigation.findNavController(view)
                        .navigate(R.id.action_navigation_settings_profile_to_navigation_settings);
                }
            });

        caretakerButton = (Button) root.findViewById(binding.caretakerButton.getId());
        caretakerButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Navigation.findNavController(view)
                        .navigate(R.id.action_navigation_settings_profile_to_navigation_settings_profile_caretaker_edit);
                }
            });

        patientButton = (Button) root.findViewById(binding.patientButton.getId());
        patientButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Navigation.findNavController(view)
                        .navigate(R.id.action_navigation_settings_profile_to_navigation_settings_profile_patient_edit);
                }
            });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
