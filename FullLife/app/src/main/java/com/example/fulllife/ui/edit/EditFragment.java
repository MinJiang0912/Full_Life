package com.example.fulllife.ui.edit;

import static android.graphics.ImageDecoder.decodeBitmap;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.fulllife.SharedViewModel;
import com.example.fulllife.ui.DataHolder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.example.fulllife.R;
import com.example.fulllife.databinding.FragmentEditBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

public class EditFragment extends Fragment {
    // Fragment for the edit page which will edit a specific task
    private FragmentEditBinding binding;
    private SharedViewModel sharedViewModel;
    Button editBack;
    Button record;
    Button setAudio;
    TextInputLayout taskTitleInput;
    TextInputLayout taskHourInput;
    TextInputLayout taskMinuteInput;
    TextInputEditText taskTitleInputText;
    TextInputEditText taskHourInputText;
    TextInputEditText taskMinuteInputText;
    AppCompatImageView taskImageView;
    private FirebaseStorage storage;
    private DataSnapshot snapshot;
    Bitmap taskBitmap;
    private String picUrl;
    private DatabaseReference database;
    private Bitmap Bitmap;
    public static EditFragment newInstance() {
        return new EditFragment();
    }

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
                    //taskImageView = (AppCompatImageView) taskImageView.findViewById(R.id.taskImage);


                    taskBitmap = null;
                    SourceImage = ImageDecoder.createSource(getContext().getApplicationContext().getContentResolver(), result);
                    try {
                        taskBitmap = decodeBitmap(SourceImage);
                        picUrl = sharedViewModel.getTaskList().get(sharedViewModel.getEditIndex()).getUrl();
                        taskImageView.setImageBitmap(taskBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //taskImageView.setImageBitmap(taskBitmap);
                    sharedViewModel.editImage(taskBitmap);
                }
            });

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        EditViewModel editViewModel = new ViewModelProvider(this).get(EditViewModel.class);
        grabSharedViewModel();

        binding = FragmentEditBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Back button functionality
        editBack = (Button) root.findViewById(binding.editBack.getId());
        editBack.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Navigation.findNavController(view)
                                .navigate(R.id.action_navigation_edit_to_navigation_dashboard);
                        taskHourInputText.clearFocus();
                        taskMinuteInputText.clearFocus();
                        taskTitleInputText.clearFocus();
                    }
                });
        //Record button functionality
        record = (Button)root.findViewById(binding.record.getId());
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view)
                        .navigate(R.id.action_navigation_edit_to_voiceFragment2);

            }
        });

        // Edit text functionality
        taskHourInput = (TextInputLayout) root.findViewById(binding.taskTimerHourEdit.getId());
        taskHourInputText =
                (TextInputEditText) root.findViewById(binding.taskTimerHourEditText.getId());

        taskMinuteInput = (TextInputLayout) root.findViewById(binding.taskTimerMinuteEdit.getId());
        taskMinuteInputText =
                (TextInputEditText) root.findViewById(binding.taskTimerMinuteEditText.getId());

        taskTitleInput = (TextInputLayout) root.findViewById(binding.taskTitleEdit.getId());
        taskTitleInputText = (TextInputEditText) root.findViewById(binding.taskTitleEditText.getId());
        taskTitleInputText.setHint(sharedViewModel.getText().getValue());


        // When page is exited, this line will trigger as an unfocus() is called
        // in the back button and whatever changes were made in the text fields will be reflected
        // in the new task
        taskHourInputText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    sharedViewModel.editTimeHourText(taskHourInputText.getText().toString());
                }
            }
        });

        taskMinuteInputText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    sharedViewModel.editTimeMinuteText(taskMinuteInputText.getText().toString());
                }
            }
        });

        taskTitleInputText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    sharedViewModel.editTaskText(taskTitleInputText.getText().toString());
                }
            }
        });

        //Image
        taskImageView = (AppCompatImageView) root.findViewById(binding.editTaskImage.getId());
        taskImageView.setImageBitmap(sharedViewModel.getTaskBitmap());
        taskImageView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        System.out.println("Image clicked");
                        mGetContent.launch("image/*");
                    }
                });



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
}
