package com.example.crimetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {

    TextInputEditText Name, Email, ContactNo, EmergencyNum1, EmergencyNum2, Password;
    Button continueBtn;
    Button loginBtn;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Name = (TextInputEditText) findViewById(R.id.editTextName2);
        Email = (TextInputEditText) findViewById(R.id.editTextEmail2);
        ContactNo = (TextInputEditText) findViewById(R.id.editTextContactNumber2);
        EmergencyNum1 = (TextInputEditText) findViewById(R.id.editTextEmergencyNumber12);
        EmergencyNum2 = (TextInputEditText) findViewById(R.id.editTextEmergencyNumber22);
        Password = (TextInputEditText) findViewById(R.id.editTextPassword2);
        continueBtn = (Button) findViewById(R.id.continueButton);
        loginBtn = (Button) findViewById(R.id.loginButton);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Signup.this  , Login.class));
            }
        });

        fAuth = FirebaseAuth.getInstance();


        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredName = Name.getText().toString().trim();
                String enteredEmail = Email.getText().toString().trim();
                String enteredContactNumber = ContactNo.getText().toString().trim();
                String enteredEmergencyNo1 = EmergencyNum1.getText().toString().trim();
                String enteredEmergencyNo2 = EmergencyNum2.getText().toString().trim();
                String enteredPassword = Password.getText().toString().trim();

                if (enteredName.isEmpty()) {
                    Name.setError("Name is Required");
                    Name.requestFocus();
                    return;
                }

                if (enteredEmail.isEmpty()) {
                    Email.setError("Email is Required!");
                    Email.requestFocus();
                    return;
                }

                if (enteredContactNumber.isEmpty()) {
                    ContactNo.setError("Contact Number is Required!");
                    ContactNo.requestFocus();
                    return;
                }

                if (enteredEmergencyNo1.isEmpty()) {
                    EmergencyNum1.setError("Emergency Number Required!");
                    EmergencyNum1.requestFocus();
                    return;
                }

                if (enteredEmergencyNo2.isEmpty()) {
                    EmergencyNum2.setError("Emergency Number Required!");
                    EmergencyNum2.requestFocus();
                    return;
                }

                if (enteredPassword.isEmpty()) {
                    Password.setError("Password is Required!");
                    Password.requestFocus();
                    return;
                }

                UserDetails userDetail = new UserDetails(enteredName, enteredEmail, enteredContactNumber, enteredEmergencyNo1, enteredEmergencyNo2, enteredPassword);

                FirebaseDatabase.getInstance().getReference("User Profile")
                        .child(enteredEmail.replace(".", ""))
                        .setValue(userDetail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Signup.this, "Successfully Signup", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Signup.this, "Unsuccesfull, please try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

//                fAuth.createUserWithEmailAndPassword(enteredEmail, enteredPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            User user = new User(enteredName, enteredEmail);
//
//                            FirebaseDatabase.getInstance().getReference("Users")
//                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        Toast.makeText(Signup.this, "User is Succesfully Created", Toast.LENGTH_LONG).show();
//
//                                        startActivity(new Intent(Signup.this  , Login.class));
//                                    } else {
//                                        Toast.makeText(Signup.this, "Signup Unsuccesfull, please try again!", Toast.LENGTH_LONG).show();
//                                    }
//                                }
//                            });
//                        } else {
//                            Toast.makeText(Signup.this, "Signup Unsuccesfull, please try again!", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });

            }
        });
    }
}





