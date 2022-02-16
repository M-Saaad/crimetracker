package com.example.crimetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UserProfile extends AppCompatActivity {

    TextInputEditText name, email, contactNo, emergencyNum1, emergencyNum2, password;
    Button updateBtn;
    String _NAME, _EMAIL, _CONTACTNO, _EMERGENCYNUM1, _EMERGENCYNUM2, _PASSWORD;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        reference = FirebaseDatabase.getInstance().getReference("User Profile");

        name = findViewById(R.id.editTextName2);
        email = findViewById(R.id.editTextEmail2);
        contactNo = findViewById(R.id.editTextContactNo2);
        password = findViewById(R.id.editTextPassword2);
        updateBtn = findViewById(R.id.updateButton);
        emergencyNum1 = findViewById(R.id.editTextEmergencyNumber12);
        emergencyNum2 = findViewById(R.id.editTextEmergancyNumber22);

        showAllUserData();

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(v);
            }
        });
    }

    private void showAllUserData() {

        String[] user_data = Preferences.getUserProfileData(UserProfile.this);

        _NAME = user_data[0];
        _EMAIL = user_data[1];
        _CONTACTNO = user_data[3];
        _EMERGENCYNUM1 = user_data[4];
        _EMERGENCYNUM2 = user_data[5];
        _PASSWORD = user_data[2];

        name.setText(_NAME);
        email.setText(_EMAIL);
        email.setEnabled(false);
        contactNo.setText(_CONTACTNO);
        emergencyNum1.setText(_EMERGENCYNUM1);
        emergencyNum2.setText(_EMERGENCYNUM2);
        password.setText(_PASSWORD);
    }

    public void update(View view) {

        if (!isNameChanged() && !isPasswordChanged() && !isContactNoChanged() && !isEmergencyNum1Changed() && !isEmergencyNum2Changed()) {
            Toast.makeText(this, "Data is same and cannot be updated", Toast.LENGTH_LONG).show();
        }

        if (isNameChanged()) {
            Toast.makeText(this, "Name is updated", Toast.LENGTH_SHORT).show();
        }

        if (isPasswordChanged()) {
            Toast.makeText(this, "Password is updated", Toast.LENGTH_SHORT).show();
        }

        if (isContactNoChanged()) {
            Toast.makeText(this, "Contact Number is updated", Toast.LENGTH_SHORT).show();
        }

        if (isEmergencyNum1Changed()) {
            Toast.makeText(this, "Emergency Number 1 is updated", Toast.LENGTH_SHORT).show();
        }

        if (isEmergencyNum2Changed()) {
            Toast.makeText(this, "Emergency Number 2 is updated", Toast.LENGTH_SHORT).show();
        }

        Preferences.setUserProfileData(
                UserProfile.this,
                _NAME,
                _EMAIL,
                _CONTACTNO,
                _PASSWORD,
                _EMERGENCYNUM1,
                _EMERGENCYNUM2
        );
    }

    private boolean isNameChanged() {

        if (!_NAME.equals(name.getText().toString())) {
            reference.child(_EMAIL.replace(".", "")).child("name").setValue(name.getText().toString());
            _NAME = name.getText().toString();
            return true;
        }
        else {
            return false;
        }

    }

//    private boolean isEmailChanged() {
//
//        if (!_EMAIL.equals(email.getText().toString())) {
//            Toast.makeText(this, "Email Cannot be changed!", Toast.LENGTH_LONG).show();
//            return false;
//        }
//        else {
//            return false;
//        }
//
//    }

    private boolean isContactNoChanged() {

        if (!_CONTACTNO.equals(contactNo.getText().toString())) {
            reference.child(_EMAIL.replace(".", "")).child("contactNumber").setValue(contactNo.getText().toString());
            _CONTACTNO = contactNo.getText().toString();
            return true;
        }
        else {
            return false;
        }

    }

    private boolean isEmergencyNum1Changed() {

        if (!_EMERGENCYNUM1.equals(emergencyNum1.getText().toString())) {
            reference.child(_EMAIL.replace(".", "")).child("emergencyNum1").setValue(emergencyNum1.getText().toString());
            _EMERGENCYNUM1 = emergencyNum1.getText().toString();
            return true;
        }
        else {
            return false;
        }

    }

    private boolean isEmergencyNum2Changed() {

        if (!_EMERGENCYNUM2.equals(emergencyNum2.getText().toString())) {
            reference.child(_EMAIL.replace(".", "")).child("emergencyNum2").setValue(emergencyNum2.getText().toString());
            _EMERGENCYNUM2 = emergencyNum2.getText().toString();
            return true;
        }
        else {
            return false;
        }

    }

    private boolean isPasswordChanged() {
        if (!_PASSWORD.equals(password.getText().toString())) {
            reference.child(_EMAIL.replace(".", "")).child("password").setValue(password.getText().toString());
            _PASSWORD = password.getText().toString();
            return true;
        }
        else {
            return false;
        }
    }

}