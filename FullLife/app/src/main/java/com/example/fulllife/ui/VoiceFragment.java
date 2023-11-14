package com.example.fulllife.ui;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Dialog;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.fulllife.R;
import com.example.fulllife.SharedViewModel;
import com.example.fulllife.databinding.FragmentVoiceBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class VoiceFragment<Url> extends Fragment {

    private static final int PERMISSION_CODE =21;
    private static final String LOG_TAG = "VoiceFragment";
    private SharedViewModel sharedViewModel;
    private FragmentVoiceBinding binding;
    private String recordPremission = Manifest.permission.RECORD_AUDIO;
    private boolean isRecording = true;
    private boolean isPlaying = true;
    private String recordFile;
    Button back;
    Button record;
    Button play;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private FirebaseStorage storage;
    private MediaPlayer player;
    private String recordPath;
    private File file;
    private String filePath;
    Uri recordingPath = null;
    private Integer editIndex;
    private DatabaseReference database;


    public static VoiceFragment newInstance() {
        return new VoiceFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        binding = FragmentVoiceBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        editIndex = sharedViewModel.getEditIndex();
      //  List<Task> taskList = sharedViewModel.getTaskList();

        back = (Button) root.findViewById(binding.voiceBack.getId());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view)
                        .navigate(R.id.action_voiceFragment_to_navigation_edit);
            }
        });

        record = (Button) root.findViewById(binding.voicerecord.getId());
        record.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.S)
            @Override
            public void onClick(View view) {
                if(isRecording){
                    checkPermission();
                    startRecording();
                    record.setText("Stop");
                    isRecording = false;

                }
                else {
                    stopRecording();
                    record.setText("Record");
                    isRecording = true;

                }
            }
        });

        play = (Button) root.findViewById(binding.play.getId());
        play.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.S)
            @Override
            public void onClick(View view) {
                if(isPlaying){
                    startPlay();
                    play.setText("Stop");
                    isPlaying=false;
                }
                else{
                    stopPlaying();
                    play.setText("Play");
                    isPlaying=true;
                }

            }
        });
        return root;

    }
    private boolean checkPermission(){
        if(ActivityCompat.checkSelfPermission(getContext(),recordPremission)== PackageManager.PERMISSION_GRANTED){
            return true;

        }
        else{
            ActivityCompat.requestPermissions(getActivity(), new String[]{recordPremission},PERMISSION_CODE);
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void startRecording(){
        if(getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)){
            Log.d("Mic","yeah it does");
        }
         //recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();

        try {
            mediaRecorder=new MediaRecorder();
            recordFile = "Record"+".3gp";

            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(getRecordingPath());
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(getContext(),"Recording is started",Toast.LENGTH_LONG).show();
            Log.d("Audio",getRecordingPath());
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }


       // recordingPath = Uri.fromFile(new File(recordPath+"/"+recordFile));

    }
    @RequiresApi(api = Build.VERSION_CODES.S)
    private void stopRecording(){
        Uri uriAudio = Uri.fromFile(new File(getRecordingPath()).getAbsoluteFile());
        Uri myUri = Uri.parse(getRecordingPath());
        Uri uriAudio1 = Uri.fromFile(new File("/storage/emulated/0/Android/data/com.example.fulllife/files/Music/testRecordingFIle.mp3"));

        Log.d("Audio", "this is with file " +String.valueOf(uriAudio));
        Log.d("Audio","this is without file " + String.valueOf(myUri));
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/mpeg")
                .build();

        StorageReference b = storage.getInstance().getReference();
        StorageReference audioRef = b.child("tunes").child(uriAudio.getLastPathSegment());

        audioRef.putFile(uriAudio,metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("Audio","Uploading to firebase cloud");
                Log.d("Audio", String.valueOf(uriAudio));
                audioRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        Task toPutAudioIn =sharedViewModel.getTaskList().get(sharedViewModel.getEditIndex());
                        database = FirebaseDatabase.getInstance().getReference();
                        //storage = FirebaseStorage.getInstance().getReference();
                        DatabaseReference putAudio = database.child("tasks").child("task").child(toPutAudioIn.id).child("AudioUrl");
                      //  database.child("tasks").child("task").child(toPutAudioIn.id).child("AudioUrl").setValue(url);
                       putAudio.setValue(url);

                        Log.d("Audio", "this is the download url "+url);
                      //  a.child("url").setValue(url);

                    }
                });

            }
        });
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();

        mediaRecorder = null;
        Toast.makeText(getContext(),"Recording is stopped",Toast.LENGTH_LONG).show();
        //uploadAudio();

    }
    @RequiresApi(api = Build.VERSION_CODES.S)
    private void startPlay(){

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(getRecordingPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(getContext(),"Recording is playing",Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }
    private void stopPlaying() {
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private String getRecordingPath(){
        ContextWrapper contextWrapper = new ContextWrapper(getContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        editIndex = sharedViewModel.getEditIndex();
        file = new File(musicDirectory,"testRecordingFIle"+editIndex+".3gp");
        Log.d("Audio",file.getPath());
        filePath = file.getPath();
        return filePath;
    }



}