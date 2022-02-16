package com.example.crimetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    Button continueBtn, signupBtn;
    TextInputEditText enteredEmail , enteredPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        continueBtn = (Button) findViewById(R.id.continueButton);
        signupBtn = (Button) findViewById(R.id.signUpButton);
        enteredEmail = (TextInputEditText) findViewById(R.id.editTextEmail2);
        enteredPassword = (TextInputEditText) findViewById(R.id.editTextPassword2);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { switchActivities1(); }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email  = enteredEmail.getText().toString().trim();
                String password = enteredPassword.getText().toString().trim();

                Log.d("test0 ", email);


                if(email.isEmpty()){
                    enteredEmail.setError("Please Enter an Email");
                    enteredEmail.requestFocus();
                    return;
                }

//                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
//                    enteredEmail.setError("Please Enter a valid Email");
//                    enteredEmail.requestFocus();
//                    return;
//                }

                if(password.isEmpty()){
                    enteredPassword.setError("Please Enter A Password");
                    enteredPassword.requestFocus();
                    return;
                }

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User Profile");
                Query checkUser = reference.orderByChild("email").equalTo(email);
                checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("test12 ", snapshot.toString());
                        if (snapshot.exists()) {

                            enteredEmail.setError(null);

                            String passwordFromDB = snapshot.child(email.replace(".", "")).child("password").getValue(String.class);

                            Log.d("test2 ", passwordFromDB);

                            if (passwordFromDB.equals(password)) {

                                enteredEmail.setError(null);

                                String nameFromDB = snapshot.child(email.replace(".", "")).child("name").getValue(String.class);
                                String emailFromDB = snapshot.child(email.replace(".", "")).child("email").getValue(String.class);
                                String contactNoFromDB = snapshot.child(email.replace(".", "")).child("contactNumber").getValue(String.class);
                                String EmergencyNo1FromDB = snapshot.child(email.replace(".", "")).child("emergencyNum1").getValue(String.class);
                                String EmergencyNo2FromDB = snapshot.child(email.replace(".", "")).child("emergencyNum2").getValue(String.class);

                                Intent intent = new Intent(getApplicationContext(), Menu.class);

                                Preferences.setUserProfileData(
                                        Login.this,
                                        nameFromDB,
                                        emailFromDB,
                                        contactNoFromDB,
                                        passwordFromDB,
                                        EmergencyNo1FromDB,
                                        EmergencyNo2FromDB
                                );

                                Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_LONG).show();

                                Preferences.setDataLogin(Login.this, true);

                                Preferences.setDataAs(Login.this, "user");

                                startActivity(intent);
                                finish();
                            }
                            else {
                                enteredPassword.setError("Wrong Password");
                                enteredPassword.requestFocus();
                            }
                        }
                        else {
                            enteredEmail.setError("No such Email exist");
                            enteredEmail.requestFocus();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        enteredEmail.setError("Database error please try again");
                    }
                });

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Preferences.getDataLogin(this)) {
            if (Preferences.getDataAs(this).equals("user")) {
                startActivity(new Intent(this, Menu.class));
                finish();
            }
        }
    }

    private void switchActivities1() {
        Intent switchActivityIntent = new Intent(this, Signup.class);
        startActivity(switchActivityIntent);
    }

//    private void switchActivities2() {
//        Intent switchActivityIntent = new Intent(this, Menu.class);
//        startActivity(switchActivityIntent);
//    }

}