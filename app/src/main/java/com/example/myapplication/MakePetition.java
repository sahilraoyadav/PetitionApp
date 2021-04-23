package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/* Not able to work our because i was not able to fully implement creating a post*/
public class MakePetition extends AppCompatActivity {
    private final String TAG = "MakePetition";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_FULLNAME = "fullname";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_DATE = "date";
    private static final String KEY_SIGNATURE = "signatures";
    private static final String KEY_SIGNS = "signs";
    public EditText petitionTitle, enterDescp;
    public Button postBtn;
    public TextView petitionAuthor, petitionDate, petitionSignatures;
    public FirebaseFirestore firebaseFirestore;
    RecyclerView recyclerView;
    DocumentReference createdID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_petition);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.recview);
        petitionTitle = findViewById(R.id.petitionTitle);
        enterDescp = findViewById(R.id.enterDescp);
        postBtn = findViewById(R.id.postBtn);
        petitionAuthor = findViewById(R.id.petitionAuthor);
        petitionDate = findViewById(R.id.petitionDate);

        petitionSignatures = findViewById(R.id.petitionSignatures);
        petitionAuthor.setText("Name: " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        petitionDate.setText("Date: " + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        petitionSignatures.setText("Signatures: 0");

        postBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.postBtn:
                        //Error Checking for no title and description
                        if (petitionTitle.getText().toString().isEmpty() && enterDescp.getText().toString().isEmpty()) {
                            petitionTitle.setError("Please insert a title.");
                            enterDescp.setError("Please insert a description");
                        }
                        //Error checking for no title
                        else if (petitionTitle.getText().toString().isEmpty() && !enterDescp.getText().toString().isEmpty()) {
                            petitionTitle.setError("Please insert a title.");
                        }
                        //Error checking for no description
                        else if (!petitionTitle.getText().toString().isEmpty() && enterDescp.getText().toString().isEmpty()) {
                            enterDescp.setError("Please insert a valid description.");
                        }
                        //Error checking to check if only numbers are in the description or title.
                        else if (TextUtils.isDigitsOnly(petitionTitle.getText()) && TextUtils.isDigitsOnly(enterDescp.getText()) || TextUtils.isDigitsOnly(enterDescp.getText()) || TextUtils.isDigitsOnly(petitionTitle.getText())) {
                            if (TextUtils.isDigitsOnly(petitionTitle.getText())) {
                                petitionTitle.setError("Please insert a valid title with letters and words.");
                            }
                            if (TextUtils.isDigitsOnly(enterDescp.getText())) {
                                enterDescp.setError("Please enter a valid description with letters and words.");
                            }
                            if (TextUtils.isDigitsOnly(petitionTitle.getText()) && TextUtils.isDigitsOnly(enterDescp.getText())) {
                                petitionTitle.setError("Please insert a valid title with letters and words.");
                                enterDescp.setError("Please enter a valid description with letters and words.");
                            }
                        }
                        //If everything is okay then create the post!
                        else {
                            savePetition();
                            startActivity(new Intent((getApplicationContext()), PetitionList.class));
                        }
                }
            }
        });
    }


    private void savePetition() {
        String fullName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String title = petitionTitle.getText().toString();
        String description = enterDescp.getText().toString();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(new Date());
        Integer signature = 1;
        ArrayList<String> signs = new ArrayList<String>();
        signs.add(email);

        firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection("users").document();
        Map<String, Object> users = new HashMap<>();


        users.put(KEY_TITLE, title);
        users.put(KEY_DESCRIPTION, description);
        users.put(KEY_FULLNAME, fullName);
        users.put(KEY_EMAIL, email);
        users.put(KEY_DATE, date);
        users.put(KEY_SIGNATURE, signature);

        CollectionReference userRef = FirebaseFirestore.getInstance().collection("users");

        userRef.add(new Model(fullName, email, title, description, date, signature)).
                addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Petition is added to the database.");
                        createdID = documentReference;
                        //System.out.println(documentReference + " This is the one ===========================");
                        Toast.makeText(MakePetition.this, "Your Petition is Created and Added to the Database!", Toast.LENGTH_SHORT).show();
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Petition is not added to the database.");
                        Toast.makeText(MakePetition.this, "Your Petition is nor Created or Added to the Database!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}