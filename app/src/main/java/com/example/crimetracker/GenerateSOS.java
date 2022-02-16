package com.example.crimetracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GenerateSOS extends AppCompatActivity {

    Button recordAgain;
    Button backBtn;
    Button sendMsgBtn;
    TextView statusText;

    StorageReference storageReference;
    DatabaseReference databaseReference;
    UploadTask uploadTask;

    String _NAME, _EMAIL, _CONTACTNO, _EMERGENCYNUM1, _EMERGENCYNUM2, _PASSWORD;

    protected Context context;

    private static int CAMERA_PERMISSION_CODE = 100;
    private Uri videoPath;
    String dateTime;

    private GpsTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        dateTime = sdf.format(new Date());

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        storageReference = FirebaseStorage.getInstance().getReference("Video");
        databaseReference = FirebaseDatabase.getInstance().getReference("video");

        recordAgain = findViewById(R.id.recordButton);
        statusText = findViewById(R.id.statusTextView);
        backBtn = findViewById(R.id.backButton);
        sendMsgBtn = findViewById(R.id.messageButton);

        recordAgain.setEnabled(false);
        backBtn.setEnabled(false);

        checkPermissionAndRecord();

        recordAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionAndRecord();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchActivities();
            }
        });

        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(GenerateSOS.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    sendMessage();
                }
                else {
                    ActivityCompat.requestPermissions(GenerateSOS.this, new String[]{Manifest.permission.SEND_SMS}, 100);
                }
            }
        });
    }

    private void getLocation() {
        gpsTracker = new GpsTracker(GenerateSOS.this);
        if (gpsTracker.canGetLocation()) {
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();

            ItemDetail itemDetail = new ItemDetail(_NAME, "Mobile SOS", "SOS Emergency", getStringAddress(latitude, longitude), (latitude + ", " + longitude), dateTime);

            FirebaseDatabase.getInstance().getReference("Dashboard")
                    .child(dateTime)
                    .setValue(itemDetail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(GenerateSOS.this, "Successfully marked mugged activity", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(GenerateSOS.this, "Unsuccesfull, please try again!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    private void switchActivities() {
        Intent switchActivityIntent = new Intent(this, Menu.class);
        startActivity(switchActivityIntent);
    }

    private void checkPermissionAndRecord() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }

        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        if (videoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(videoIntent, CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (resultCode == RESULT_OK) {
                videoPath = data.getData();

                if (videoPath != null) {

                    final StorageReference reference = storageReference.child(dateTime);
                    uploadTask = reference.putFile(videoPath);
                    statusText.setText("Uploading...");
                    Log.i("Videofile", videoPath.toString());

                    Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return reference.getDownloadUrl();
                        }
                    })
                            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUrl = task.getResult();
                                        Toast.makeText(GenerateSOS.this, "Data uploaded to firebase", Toast.LENGTH_LONG);
                                        getLocation();
                                        statusText.setText("Uploaded Successfully");
                                    } else {
                                        statusText.setText("Uploading Failed!");
                                    }
                                    recordAgain.setEnabled(true);
                                    backBtn.setEnabled(true);
                                }
                            });
                } else {
                    Toast.makeText(GenerateSOS.this, "VideoPath is Null ", Toast.LENGTH_LONG);
                }
            }
        }
    }

    public String getStringAddress(Double lat, Double lng) {
        String address = "";
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            address = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    private void sendMessage() {

        String[] user_data = Preferences.getUserProfileData(GenerateSOS.this);

        _NAME = user_data[0];
        _EMAIL = user_data[1];
        _CONTACTNO = user_data[3];
        _EMERGENCYNUM1 = user_data[4];
        _EMERGENCYNUM2 = user_data[5];
        _PASSWORD = user_data[2];

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(_EMERGENCYNUM1, null, "SOS Emergency", null, null);
        smsManager.sendTextMessage(_EMERGENCYNUM2, null, "SOS Emergency", null, null);

        Toast.makeText(GenerateSOS.this, "Message Sent Succesfully..!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            sendMessage();
        }
        else {
            Toast.makeText(GenerateSOS.this, "Message Permission Denied..!", Toast.LENGTH_LONG).show();
        }
    }
}
