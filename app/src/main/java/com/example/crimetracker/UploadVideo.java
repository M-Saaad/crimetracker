package com.example.crimetracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadVideo extends AppCompatActivity {

    Button recordAgain;
    Button backBtn;
    TextView statusText;

    StorageReference storageReference;
    DatabaseReference databaseReference;
    UploadTask uploadTask;

    private  static int CAMERA_PERMISSION_CODE = 100;
    private Uri videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);

        storageReference = FirebaseStorage.getInstance().getReference("Video");
        databaseReference = FirebaseDatabase.getInstance().getReference("video");

        recordAgain = findViewById(R.id.recordButton);
        statusText = findViewById(R.id.statusTextView);
        backBtn = findViewById(R.id.backButton);

        recordAgain.setEnabled(false);
        backBtn.setEnabled(false);

        checkPermissionAndRecord();

        recordAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionAndRecord();
                statusText.setText("Uploading ...");
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchActivities();
            }
        });
    }

    private void switchActivities() {
        Intent switchActivityIntent = new Intent(this, Menu.class);
        startActivity(switchActivityIntent);
    }

    private void checkPermissionAndRecord(){

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this , new String[]{Manifest.permission.CAMERA} ,CAMERA_PERMISSION_CODE);
        }


        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        if (videoIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(videoIntent , CAMERA_PERMISSION_CODE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_PERMISSION_CODE);{
            if(resultCode == RESULT_OK){
                videoPath = data.getData();

                if(videoPath != null){

                    final StorageReference reference = storageReference.child("1");
                    uploadTask = reference.putFile(videoPath);
                    Log.i("Videofile" , videoPath.toString());

                    Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful()){
                                throw task.getException();
                            }
                            return reference.getDownloadUrl();
                        }
                    })
                            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if(task.isSuccessful()){
                                        Uri downloadUrl = task.getResult();

                                        Toast.makeText(UploadVideo.this , "Data uploaded to firebase" , Toast.LENGTH_LONG);
                                        statusText.setText("Uploaded Successfully");
                                    }
                                    else{
                                        statusText.setText("Uploading Failed!");
                                    }
                                    recordAgain.setEnabled(true);
                                    backBtn.setEnabled(true);
                                }
                            });
                }else{
                    Toast.makeText(UploadVideo.this , "VideoPath is Null " , Toast.LENGTH_LONG);
                }
            }
        }
    }
}
