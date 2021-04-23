package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    //initializer for the variables
    TextView signText, createText;
    EditText mEmailField, mPasswordField;
    Button login_button;
    ProgressBar progressBar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //initializes the variables.
        mAuth = FirebaseAuth.getInstance();
        signText = findViewById(R.id.signText);
        mEmailField = findViewById(R.id.userEmail);
        mPasswordField = findViewById(R.id.password);
        login_button = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progressBar);
        createText = findViewById(R.id.createText);
        //creates a on click listener for login button
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailField.getText().toString();
                String password = mPasswordField.getText().toString();
                //Error checking
                if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                    mEmailField.setError("Email is Required.");
                    mPasswordField.setError("Password Required.");
                    Toast.makeText(Login.this, "Please Enter a Valid Email and Password", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(email)) {
                    mEmailField.setError("Email is Required.");
                    Toast.makeText(Login.this, "Please Enter a Valid Email", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    mEmailField.setError("Password is Required.");
                    Toast.makeText(Login.this, "Please Enter a Valid Password", Toast.LENGTH_SHORT).show();
                    return;
                } else if (password.length() < 6) {
                    mPasswordField.setError("Password Must be >= 6 Characters.");
                    Toast.makeText(Login.this, "Please Enter a Valid Password", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    mEmailField.setError(null);
                    mPasswordField.setError(null);
                }
                //sets progressBar to be visible while processing
                progressBar.setVisibility(View.VISIBLE);

                //logs in user
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Logged in", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent((getApplicationContext()), PetitionList.class));
                        } else {
                            Toast.makeText(Login.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

            }

        });
        //Creates on click listener for register user.
        createText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });
    }
}