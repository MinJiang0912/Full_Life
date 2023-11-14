package com.example.fulllife.ui.settings.profile.caretaker;

import static android.graphics.ImageDecoder.decodeBitmap;

import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.fulllife.MainActivity;
import com.example.fulllife.R;
import com.example.fulllife.SharedViewModel;
import com.example.fulllife.databinding.FragmentSettingsProfileBinding;
import com.example.fulllife.databinding.FragmentSettingsProfileCaretakerEditBinding;
import com.example.fulllife.ui.DataHolder;

import java.io.IOException;

public class ProfileCaretakerFragment extends Fragment {
    /**
     * Settings fragment that allows user to log out or change caretaker information
     * Currently can change image
     */
    private FragmentSettingsProfileCaretakerEditBinding binding;
    private SharedViewModel sharedViewModel;
    Button backButton;
    ImageView editProfileImage;
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
                sharedViewModel.setCaretakerImage(profileBitmap);
                editProfileImage.setImageBitmap(profileBitmap);
            }
        });

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ProfileCaretakerViewModel settingsViewModel =
                new ViewModelProvider(this).get(ProfileCaretakerViewModel.class);
        grabSharedViewModel();

        binding = FragmentSettingsProfileCaretakerEditBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        backButton = (Button) root.findViewById(binding.editBackProfilePatientEdit.getId());
        backButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Navigation.findNavController(view)
                        .navigate(R.id.action_navigation_settings_profile_caretaker_edit_to_navigation_settings_profile);
                }
            });

        editProfileImage = (ImageView) root.findViewById(binding.editProfileImage.getId());
        editProfileImage.setImageBitmap(sharedViewModel.getCaretakerImage());
        // Image button logic
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
