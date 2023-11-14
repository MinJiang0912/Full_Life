package com.example.fulllife.ui.login;

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
import com.example.fulllife.SharedViewModel;
import com.example.fulllife.databinding.FragmentLoginBinding;
import com.example.fulllife.enums.UserType;
import com.example.fulllife.ui.DataHolder;

public class LoginFragment extends Fragment {
  // Dashboard fragment which shows all the tasks and their timers
  // Tasks can be deleted from this page
  // The edit button puts you to another fragment to edit.
  private LoginViewModel loginViewModel;
  private SharedViewModel sharedViewModel;
  private FragmentLoginBinding binding;

  Button patientButton;
  Button caregiverButton;
  Button debugButton;

  private static final String TAG = "LoginFragment";

  public View onCreateView(
          @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    grabSharedViewModel();
    binding = FragmentLoginBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

    patientButton = (Button) root.findViewById(binding.patientButton.getId());
    patientButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            sharedViewModel.setUserType(UserType.PATIENT);
            Navigation.findNavController(view)
                .navigate(R.id.action_navigation_login_to_navigation_home);
          }
        });

    caregiverButton = (Button) root.findViewById(binding.caretakerButton.getId());
    caregiverButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            sharedViewModel.setUserType(UserType.CARETAKER);
            Navigation.findNavController(view)
                .navigate(R.id.action_navigation_login_to_navigation_userlist);
          }
        });

    debugButton = (Button) root.findViewById(binding.debuggerButton.getId());
    debugButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            sharedViewModel.setUserType(UserType.DEBUG);
            Navigation.findNavController(view)
                .navigate(R.id.action_navigation_login_to_navigation_home);
          }
        });

    return root;
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
