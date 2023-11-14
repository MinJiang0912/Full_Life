package com.example.fulllife.ui.userlist;

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
import com.example.fulllife.databinding.FragmentSettingsBinding;
import com.example.fulllife.databinding.FragmentUserlistBinding;

public class UserListFragment extends Fragment {
    // This notifications page fragment currently does nothing
    private FragmentUserlistBinding binding;
    Button PatientButton;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        UserListViewModel userListViewModel =
                new ViewModelProvider(this).get(UserListViewModel.class);

        binding = FragmentUserlistBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        PatientButton = (Button) root.findViewById(binding.AnnePatientButton.getId());
        PatientButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Navigation.findNavController(view)
                        .navigate(R.id.action_navigation_userlist_to_navigation_home);
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
