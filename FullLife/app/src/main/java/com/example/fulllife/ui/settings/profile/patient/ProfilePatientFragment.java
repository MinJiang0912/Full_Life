package com.example.fulllife.ui.settings.profile.patient;

import static android.graphics.ImageDecoder.decodeBitmap;

import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.fulllife.MainActivity;
import com.example.fulllife.R;
import com.example.fulllife.SharedViewModel;
import com.example.fulllife.databinding.FragmentSettingsProfileBinding;
import com.example.fulllife.databinding.FragmentSettingsProfilePatientEditBinding;
import com.example.fulllife.ui.DataHolder;

import java.io.IOException;

public class ProfilePatientFragment extends Fragment {
    /**
     * Settings fragment that allows user to log out or change patient information
     * Currently can change image
     */
    private FragmentSettingsProfilePatientEditBinding binding;
    private SharedViewModel sharedViewModel;
    Button backButton;
    AppCompatImageView editProfileImage;
    Bitmap profileBitmap;

    /**
     * When the image is edited, this section of code will be able to detect and make the changes
     * in the app
     */
        ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @RequiresApi(api = Build.VERSION_CODES.P)
                @Override
                public void onActivityResult(Uri result) {
                    ImageDecoder.Source SourceImage = null;
                    profileBitmap = null;
                    SourceImage = ImageDecoder.createSource(getContext().getApplicationContext().getContentResolver(), result);
                    try {
                        profileBitmap = decodeBitmap(SourceImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sharedViewModel.setPatientImage(profileBitmap);
                    editProfileImage.setImageBitmap(profileBitmap);
                }
            });

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ProfilePatientViewModel settingsViewModel =
                new ViewModelProvider(this).get(ProfilePatientViewModel.class);
        grabSharedViewModel();

        binding = FragmentSettingsProfilePatientEditBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        backButton = (Button) root.findViewById(binding.editBackProfilePatientEdit.getId());
        backButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Navigation.findNavController(view)
                        .navigate(R.id.action_navigation_settings_profile_patient_edit_to_navigation_settings_profile);
                }
            });

        editProfileImage = (AppCompatImageView) root.findViewById(binding.editProfileImage.getId());
        editProfileImage.setImageBitmap(sharedViewModel.getPatientImage());
        //Image button logic
        editProfileImage.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("Image clicked");
                    mGetContent.launch("image/*");
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
