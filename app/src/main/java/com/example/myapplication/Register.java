package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;


public class Register extends AppCompatActivity {
    private static final String TAG = "Register";
// initializers for vars
    EditText mEmailField, mPasswordField, mFirstName, mLastName, mConfirmPassword;
    Button mRegisterButton;
    TextView mLoginButton;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    String fullName, userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //       initializes the vars
        mLastName = findViewById(R.id.lName);
        mFirstName = findViewById(R.id.fName);
        mEmailField = findViewById(R.id.Email);
        mPasswordField = findViewById(R.id.password);
        mRegisterButton = findViewById(R.id.register_button);
        mLoginButton = findViewById(R.id.loginText);
        progressBar = findViewById(R.id.progressBar);
        mConfirmPassword = findViewById(R.id.confirmPassword);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        //checks if the user is logged in or not
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), PetitionList.class));
            finish();
        }
        //sets listener on register button
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmailField.getText().toString();
                String password = mPasswordField.getText().toString();
                String confirmPassword = mConfirmPassword.getText().toString();
                String fName = mFirstName.getText().toString();
                String lName = mLastName.getText().toString();
                fullName = fName + " " + lName;

                //Error checking
                if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password) && TextUtils.isEmpty(confirmPassword) && TextUtils.isEmpty(fName) && TextUtils.isEmpty(lName)) {
                    mEmailField.setError("Email is Required.");
                    mPasswordField.setError("Password Required.");
                    mConfirmPassword.setError("Confirm Password Required.");
                    mFirstName.setError("Enter First Name");
                    mLastName.setError("Enter Last Name");
                    Toast.makeText(Register.this, "Please Enter a Valid Email and Password", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(email)) {
                    mEmailField.setError("Email is Required.");
                    Toast.makeText(Register.this, "Please Enter a Valid Email", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    mPasswordField.setError("Password is Required.");
                    Toast.makeText(Register.this, "Please Enter a Valid Password", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(confirmPassword)) {
                    mPasswordField.setError("Password do not match.");
                    Toast.makeText(Register.this, "Please Enter Confirm Password", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!confirmPassword.equals(password)) {
                    mConfirmPassword.setError("Confirm Password does not match Password");
                    Toast.makeText(Register.this, "Passwords Do Not Match", Toast.LENGTH_SHORT).show();
                    return;
                } else if (password.length() < 6) {
                    mPasswordField.setError("Password Must be >= 6 Characters.");
                    Toast.makeText(Register.this, "Please Enter a Valid Password", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    mEmailField.setError(null);
                    mPasswordField.setError(null);
                    mConfirmPassword.setError(null);
                }


                progressBar.setVisibility(View.VISIBLE);
                //Creates a account
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, start the main activity
                            Log.d(TAG, "createUserWithEmail:success" + mAuth.getCurrentUser().getUid());
                            Toast.makeText(Register.this, "User Created." + fullName, Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            //Sets the firebase display name to full user name
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(fullName).build();
                            user.updateProfile(profileUpdates);
                            //staterts the petitino list activity-
                            startActivity(new Intent((getApplicationContext()), PetitionList.class));


                        } else {
                            // If registration fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Register.this, "Registration Failed !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);

                        }

                    }
                });
            }
        });
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }
}