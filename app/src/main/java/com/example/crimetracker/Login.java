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

public class Login extends AppCompatActivity {

    Button continueBtn, signupBtn;
    TextInputEditText enteredEmail , enteredPassword;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        continueBtn = (Button) findViewById(R.id.continueButton);
        signupBtn = (Button) findViewById(R.id.signUpButton);
        enteredEmail = (TextInputEditText) findViewById(R.id.editTextEmail2);
        enteredPassword = (TextInputEditText) findViewById(R.id.editTextPassword2);

        fAuth = FirebaseAuth.getInstance();

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { switchActivities1(); }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email  = enteredEmail.getText().toString().trim();
                String password = enteredPassword.getText().toString().trim();


                if(email.isEmpty()){
                    enteredEmail.setError("Please Enter an Email");
                    enteredEmail.requestFocus();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    enteredEmail.setError("Please Enter a valid Email");
                    enteredEmail.requestFocus();
                    return;
                }

                if(password.isEmpty()){
                    enteredPassword.setError("Please Enter A Password");
                    enteredPassword.requestFocus();
                    return;
                }

                fAuth.signInWithEmailAndPassword(email , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this , "Login Successfull" , Toast.LENGTH_LONG).show();
                            startActivity(new Intent(Login.this  , Menu.class));

                        }else{
                            Toast.makeText(Login.this , "Failed to login. Please Check Your credentials" , Toast.LENGTH_LONG).show();

                        }
                    }
                });

            }
        });

    }

    private void switchActivities1() {
        Intent switchActivityIntent = new Intent(this, Signup.class);
        startActivity(switchActivityIntent);
    }

}