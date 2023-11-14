package com.example.fulllife.ui.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.navigation.Navigation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ui.NavigationUI;

import com.example.fulllife.R;
import com.example.fulllife.SharedViewModel;
import com.example.fulllife.databinding.FragmentHomeBinding;
import com.example.fulllife.enums.UserType;
import com.example.fulllife.ui.DataHolder;

public class HomeFragment extends Fragment {
    // This home page fragment will provide navigation for the users
    private FragmentHomeBinding binding;
    SharedViewModel sharedViewModel;
    ImageView profileImage;
    TextView welcomeText;
    Button taskNav;
    Button settingNav;
    Button completedNav;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        grabSharedViewModel();
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Profile picture and welcome text logic
        profileImage = (ImageView) root.findViewById(binding.homeProfileImage.getId());
        welcomeText = (TextView) root.findViewById(binding.textHomeIntro.getId());

        if (sharedViewModel.getUserType() == UserType.CARETAKER) {
          profileImage.setImageBitmap(sharedViewModel.getPatientImage());
          welcomeText.setText("Managing Anne");
        }
        else {
          profileImage.setImageBitmap(sharedViewModel.getPatientImage());
          welcomeText.setText("Welcome Anne");
        }

        taskNav = (Button) root.findViewById(binding.pwdtasks.getId());
        taskNav.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Navigation.findNavController(view)
                                .navigate(R.id.action_navigation_home_to_navigation_dashboard);
                    }
                });

        settingNav = (Button) root.findViewById(binding.settingNav.getId());
        settingNav.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Navigation.findNavController(view)
                        .navigate(R.id.action_navigation_home_to_navigation_settings);
                }
            });

      completedNav = (Button) root.findViewById(binding.reminders.getId());
      completedNav.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Navigation.findNavController(view)
                  .navigate(R.id.action_navigation_home_to_navigation_completedtasks);
            }
          });

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    public void grabSharedViewModel()
    {
      if (DataHolder.getInstance().getSharedViewModel() != null) {
        sharedViewModel = DataHolder.getInstance().getSharedViewModel();
      }
      else {
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.setUserType(UserType.DEBUG);
        initUsers();
        sharedViewModel.getFirebaseAndStore(getContext());
      }
    }

  private void initUsers() {
    Bitmap icon = BitmapFactory.decodeResource(this.getResources(),
        R.drawable.abstract_user_flat_4);
    if (sharedViewModel.getCaretakerImage() == null) {
      sharedViewModel.setCaretakerImage(icon);
    }
    if (sharedViewModel.getPatientImage() == null) {
      sharedViewModel.setPatientImage(icon);
    }
  }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
