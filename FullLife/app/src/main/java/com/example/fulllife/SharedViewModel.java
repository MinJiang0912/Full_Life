package com.example.fulllife;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.fulllife.ui.CompletedTask;
import com.example.fulllife.ui.Task;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.example.fulllife.enums.UserType;

public class SharedViewModel extends ViewModel {
    /**
     * The shared view model represents the memory for all the fragments
     * Fragments are able to retrieve values from here and stay synced to each other
     * <p>
     * Code has two lists that it stores
     * One that is from firebase that will grab the information using Wi-Fi to a cloud database
     * Two is the Tasks list that will grab information from firebase and will be used by the application
     */
    private MutableLiveData<CharSequence> text = new MutableLiveData<>("Edit this Task");
    private List<Task> taskList = new ArrayList<>();
    private List<CompletedTask> completedTaskList = new ArrayList<>();
    private int editIndex = 0;
    private int DEFAULT_HOURS = 8;
    private int DEFAULT_MINUTES = 0;
    private CharSequence DEFAULT_TEXT = "Please edit this task";
    private DatabaseReference database;
    private FirebaseStorage storage;
    //private StorageReference storage;
    private Uri filePath;
    private String picUrl="https://firebasestorage.googleapis.com/v0/b/dementiaapp-3efd1.appspot.com/o/-N8bhCS2Flo1p-2sa-wT?alt=media&token=4423b269-5889-4714-b48a-4b9b9421e4dd";
    private Bitmap image;
    private String editUrl;
    private firebaseTasks tasks;
    private CompletedTask completedTask;

    // Could potentially modularize this into their own User class
    private Bitmap caretakerImage;
    private Bitmap patientImage;

    private UserType user;
    private String audioUrl = "https://firebasestorage.googleapis.com/v0/b/fulllife-cd5fc.appspot.com/o/tunes%2FtestRecordingFIle0.3gp?alt=media&token=83b8280d-3a6a-409f-9d8e-dde6b948df0e";

    public String getAudioUrl() {
        return audioUrl;
    }

    public void sort() {
        // Done after adding or editing any task time.
        // We sort by shortest duration task at the top
        Collections.sort(taskList, new Comparator<Task>() {
            public int compare(Task c1, Task c2) {
                if (c1.getTotalDurationMillisecond() > c2.getTotalDurationMillisecond()) return 1;
                if (c1.getTotalDurationMillisecond() < c2.getTotalDurationMillisecond()) return -1;
                return 0;
            }
        });
    }

    public void sortCompleted() {
        // Done after adding or editing any task time.
        // We sort by shortest duration task at the top
        Collections.sort(completedTaskList, new Comparator<CompletedTask>() {
            public int compare(CompletedTask c1, CompletedTask c2) {
                if (c1.getTimeMillisecondsCompleted() > c2.getTimeMillisecondsCompleted()) return -1;
                if (c1.getTimeMillisecondsCompleted() < c2.getTimeMillisecondsCompleted()) return 1;
                //return 0;
                return 0;
            }
        });
    }

    public LiveData<CharSequence> getText() {
        return text;
    }

    public Integer getTotalTaskCount() {
        return taskList.size();
    }

    public Integer getTotalCompletedTaskCount() {
        return completedTaskList.size();
    }

    public List<CompletedTask> getCompletedTaskList() {
        return completedTaskList;
    }

    public int getEditIndex() {
        return editIndex;
    }

    public void editTaskText(String editedText) {
        // First it edits the task in the app
        // Then it sends the edited task to Firebase, which it can identify by id
        if (editedText == "") {
            return;
        }
        taskList.get(editIndex).setTaskText(editedText);
        String id = taskList.get(editIndex).getId();
        database.child("tasks").child("task").child(id).child("name").setValue(editedText);
    }

    public CharSequence getTaskText() {
        return (taskList.get(editIndex)).getTaskText();
    }

    public void createTask(Bitmap bitmap) {
        // Creates a task
        // First it creates a task in firebase, then it will grab the id of that task from firebase
        // Then it will send it to the in-app tasks list so it can be identified
        firebaseTasks ab = writeNewUser(DEFAULT_TEXT.toString(), Integer.toString(DEFAULT_HOURS), Integer.toString(DEFAULT_MINUTES),bitmap);

        database = FirebaseDatabase.getInstance().getReference();
        Task newTask = new Task(DEFAULT_TEXT, DEFAULT_HOURS, DEFAULT_MINUTES,ab.id,ab.url, bitmap, ab.audioUrl);
        Log.d("MyApplook",ab.getAudioUrl());
        taskList.add(newTask);
    }

    public void removeTask(int k) {
        // get the id of the task
        // remove the task from in-app List, with the given index
        // Then remove task from firebase with the given id
        Task toRemove = taskList.get(k);
        String id = toRemove.getId();
        taskList.remove(k);

        StorageReference b = storage.getInstance().getReference();
        StorageReference ImagesRef = b.child(id);
        ImagesRef.delete();

        database.child("tasks").child("task").child(id).removeValue();
    }

    public void completeTask(int k) {
        // Modified function that will remove task and put in complete list
        completedTask = new CompletedTask(taskList.get(k).getTaskText(), taskList.get(k).getTimeHourOfTask(), taskList.get(k).getTimeMinutesOfTask(), taskList.get(k).getTaskId());
        removeTask(k);

        database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference completedDatabaseTasks = database.child("completedTasks");
        addFirebaseCompletedTasks(completedTask);

        completedTaskList.add(completedTask);
        sortCompleted();
    }

    public void editTimeHourText(String editedText) {
        // Edit the hour of the time
        // Then it sends the edited task to Firebase, which it can identify by id
        String value = editedText.replaceAll("[^0-9]", "");
        if (value.isEmpty()) {
            return;
        }
        taskList.get(editIndex).setTimeHour(Integer.parseInt(value));
        String id = taskList.get(editIndex).getId();
        database = FirebaseDatabase.getInstance().getReference();
        database.child("tasks").child("task").child(id).child("hour").setValue(editedText);
    }

    public void editTimeMinuteText(String editedText) {
        // Edit the minute of the time
        // Then it sends the edited task to Firebase, which it can identify by id
        String value = editedText.replaceAll("[^0-9]", "");
        if (value.isEmpty()) {
            return;
        }
        taskList.get(editIndex).setTimeMinute(Integer.parseInt(value));
        String id = taskList.get(editIndex).getId();
        database = FirebaseDatabase.getInstance().getReference();
        database.child("tasks").child("task").child(id).child("minutes").setValue(editedText);
    }

    public void editImage(Bitmap bitmap) {
        // For editing a task at edit index
        taskList.get(editIndex).setBitmapImage(bitmap);
        String id = taskList.get(editIndex).getId();
        Task toChangeTask = taskList.get(editIndex);
        StorageReference b = storage.getInstance().getReference();
        StorageReference ImagesRef = b.child(id);
        DatabaseReference a = database.child("tasks").child("task");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        // Have to generate a URL, store image in there and send url to firebase
        byte[] data = baos.toByteArray();
        try {
            ImagesRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            editUrl = url;
                            Log.d("MyApppinside", editUrl);
                            a.child(id).child("url").setValue(url);
                            toChangeTask.setUrl(url);
                            Log.d("MyApppLogoinside", toChangeTask.getUrl());

                        }
                    });
                };
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Bitmap getTaskBitmap() {
        return (taskList.get(editIndex)).getBitmapImage();
    }

    public long getTotalDurationMillisecond() {
        return (taskList.get(editIndex)).getTotalDurationMillisecond();
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public long getTotalDurationMillisecond(int e) {
        return (taskList.get(e)).getTotalDurationMillisecond();
    }

    public void setEditIndex(int edit) {
        editIndex = edit;
    }

    public Task getImmediateTask() {
        // Gets the most imminent task to do
        if (taskList.size() == 0)
        {
            return null;
        }
        else {
            return taskList.get(0);
        }
    }

    public void getFirebaseAndStore(Context context) {
        // Initiated when the app gets opened to connect to the firebase
        // Will read the firebase and store the information into the in-app Tasks List

        database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference tasks = database.child("tasks").child("task");
        DatabaseReference tasksCompleted =  database.child("completedTasks");
        Log.d("firebase", tasks.getKey());
        tasks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Listener will check for if firebase has changed and will grab the values from the new data
                // and add it into the software Task list.
                Integer taskCountInt = getTotalTaskCount();

                Log.d("MyApp", "taskCOuntInt:" + String.valueOf(taskCountInt));
                for (DataSnapshot dsp : snapshot.getChildren()) {

                    firebaseTasks t = new firebaseTasks((String) dsp.child("name").getValue(), (String) dsp.getKey(),
                            (String) dsp.child("hour").getValue(String.class),
                            (String) dsp.child("minutes").getValue(String.class),dsp.child("url").getValue(String.class),dsp.child("audioUrl").getValue(String.class));

                    // Grab essential information of Task class
                    String a = dsp.child("name").getValue(String.class);
                    String b = dsp.child("hour").getValue(String.class);
                    String c = dsp.child("minutes").getValue(String.class);
                    String d = dsp.getKey();
                    String e = dsp.child("url").getValue(String.class);
                    String f = dsp.child("audioUrl").getValue(String.class);
                    Log.d("lol",e+" LINK");
                    URL url = null;
                    // Read image from URL and then make a task based on that image
                    Glide.with(context).asBitmap().load(e).into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            // Once image can be loaded, we reload the task into the task list
                            image = resource;
                            Task task = new Task(a, Integer.valueOf(b), Integer.valueOf(c), d,e,image,f);
                            taskList.add(task);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Check for tasks in task Completed database
        tasksCompleted.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Listener will check for if firebase has changed and will grab the values from the new data
                // and add it into the software Task list.
                Integer taskCountInt = getTotalCompletedTaskCount();

                for (DataSnapshot dsp : snapshot.getChildren()) {

                    String name = dsp.child("name").getValue(String.class);
                    String id = dsp.child("id").getValue(String.class);

                    Integer timeHourSet = dsp.child("hourset").getValue(Integer.class);
                    Integer timeMinuteSet = dsp.child("minuteset").getValue(Integer.class);

                    Integer yearCompleted = dsp.child("yearcompleted").getValue(Integer.class);
                    Integer monthCompleted = dsp.child("monthcompleted").getValue(Integer.class);
                    Integer dayCompleted = dsp.child("daycompleted").getValue(Integer.class);
                    Integer hourCompleted = dsp.child("hourcompleted").getValue(Integer.class);
                    Integer minuteCompleted = dsp.child("minutecompleted").getValue(Integer.class);

                    long millisecondCompleted = dsp.child("millisecondcompleted").getValue(long.class);

                    CompletedTask completedTask = new CompletedTask(name,timeHourSet,timeMinuteSet,id,yearCompleted,monthCompleted,dayCompleted,hourCompleted,minuteCompleted, millisecondCompleted);
                    completedTaskList.add(completedTask);
                }
                sortCompleted();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addFirebaseCompletedTasks(CompletedTask completedTask) {
        // For storing essential information of a completed task
        DatabaseReference a = database.child("completedTasks").push();
        a.child("name").setValue(completedTask.getTaskText());
        a.child("id").setValue(completedTask.getId());
        a.child("yearcompleted").setValue(completedTask.getTimeYearOfTaskCompleted());
        a.child("monthcompleted").setValue(completedTask.getTimeMonthOfTaskCompleted());
        a.child("daycompleted").setValue(completedTask.getTimeDayOfTaskCompleted());
        a.child("hourcompleted").setValue(completedTask.getTimeHourOfTaskCompleted());
        a.child("minutecompleted").setValue(completedTask.getTimeMinutesOfTaskCompleted());
        a.child("millisecondcompleted").setValue(completedTask.getTimeMillisecondsCompleted());
        a.child("hourset").setValue(completedTask.getTimeHourOfTaskSet());
        a.child("minuteset").setValue(completedTask.getTimeMinutesOfTaskSet());
    }

    public firebaseTasks writeNewUser(String name, String hour, String minute, Bitmap bitmap) {
        // adds a Task into the Firebase
        DatabaseReference a = database.child("tasks").child("task").push();
        //FirebaseStorage storage= FirebaseStorage.getInstance();
        String key = a.getKey();

        Log.d("MyApp",key);

        StorageReference b = storage.getInstance().getReference();
        StorageReference ImagesRef = b.child(key);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        //    ImagesRef.putBytes(data);
        try {
            ImagesRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            picUrl = url;
                            Log.d("MyApppinside", picUrl);
                            a.child("name").setValue(name);
                            a.child("hour").setValue(hour);
                            a.child("minutes").setValue(minute);
                            a.child("url").setValue(url);
                            a.child("audioUrl").setValue(audioUrl);
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        tasks = new firebaseTasks(name, a.getKey(), hour, minute,picUrl,audioUrl);
        return tasks;
    }

    public void setUserType(UserType permissions) {
        user = permissions;
    }

    public UserType getUserType() {
        return user;
    }

    public void setCaretakerImage(Bitmap image) {caretakerImage = image;}

    public Bitmap getCaretakerImage() {return caretakerImage;}

    public void setPatientImage(Bitmap image) {patientImage = image;}

    public Bitmap getPatientImage() {return patientImage;}

}
