package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/* Not working bcz i was not able to figure out how to create posts*/
//Will be using email+title as unique id to find post from database
public class PetitionPage extends AppCompatActivity {
    private static final String TAG = "PetitionPage";
    public static String id, activity;
    public static String title, description, signature, fullName, date, uEmail;
    public static boolean editable;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final CollectionReference userRef = firebaseFirestore.collection("users");
    TextView petitionTitle, petitionDescp, signatures, numberSignature, petitionAuthor;
    FloatingActionButton shareBtn;
    ConstraintLayout constraintPetition;
    Button signBtn;
    Integer nsignature;
    public Toolbar toolbar;
    public ActionBar actionBar;
    DocumentReference doc;
    Model model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petition_page);
        setToolbar();
        Intent intent = getIntent();
        id = intent.getStringExtra("id").trim();
        editable = intent.getExtras().getBoolean("editable");
        activity = intent.getStringExtra("activity");

        petitionTitle = findViewById(R.id.petitionTitle);
        petitionAuthor = findViewById(R.id.petitionAuthor);
        petitionDescp = findViewById(R.id.petitionDescp);
        signatures = findViewById(R.id.signatures);
        numberSignature = findViewById(R.id.numberSignature);
        constraintPetition = findViewById(R.id.constraintPetition);
        userRef.document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot snapshot) {
                title = snapshot.getString("title");
                petitionTitle.setText(title);
                description = snapshot.getString("description");
                petitionDescp.setText(description);
                uEmail = snapshot.getString("email");
                fullName = snapshot.getString("fullName");
                date = snapshot.getString("date");
                petitionAuthor.setText("Posted by: " + fullName + " " + "on " + date);
                signature = String.valueOf(snapshot.get("signature"));
                nsignature = Integer.parseInt(signature);
                numberSignature.setText(signature);
                // Adds user's email to array in Model object when users signs petition
                // Incremets number of signatures when sign button clicked
                signBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        Model model = snapshot.toObject(Model.class);
                        ArrayList<String> signs = model.getSigns();
                        if (!signs.contains(email)){
                            nsignature ++;
                            numberSignature.setText(nsignature.toString());
                            firebaseFirestore.collection("users").document(id).update(
                                    "signature", nsignature,
                                    "signs", FieldValue.arrayUnion(email));
                            signBtn.setEnabled(false);
                            Snackbar.make(constraintPetition, "Petition Signed", Snackbar.LENGTH_LONG)
                                    .setAction("Unsign", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            nsignature --;
                                            numberSignature.setText(nsignature.toString());
                                            firebaseFirestore.collection("users").document(id).update(
                                                    "signature", nsignature,
                                                    "signs", FieldValue.arrayRemove(email)
                                            );
                                            signBtn.setEnabled(true);
                                        }
                                    })
                                    .setActionTextColor(Color.RED)
                                    .show();
                        } else {
                            Snackbar.make(constraintPetition, "Petition Signed Already", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        signBtn = findViewById(R.id.signBtn);
        shareBtn = findViewById(R.id.shareBtn);
        sharePetition();
        setNavigation();

    }

    public void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void setNavigation() {
        if (activity.equals("MyPetitions")) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MyPetitions.class);
                    intent.putExtra("id", id);
                    intent.putExtra("editable", true);
                    intent.putExtra("activity", activity);
                    startActivity(intent);
                }
            });
        }
        if (activity.equals("PetitionList")) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), PetitionList.class);
                    intent.putExtra("id", id);
                    intent.putExtra("editable", false);
                    intent.putExtra("activity", activity);
                    startActivity(intent);
                }
            });
        }
    }

    public void sharePetition() {
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uriText =
                        "mailto:".concat(uEmail) +
                                "?subject=" + Uri.encode(title);
                Uri uri = Uri.parse(uriText);
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(uri);
                startActivity(Intent.createChooser(sendIntent, "Send email"));
            }
        });
    }
    //Creates the menu for the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflates the menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.petition_list_menu, menu);
        //sets the edit menu item to be hidden since it is not needed
        MenuItem myPost = menu.findItem(R.id.myPosts);
        myPost.setVisible(false);
        MenuItem edit = menu.findItem(R.id.editBtn);
        MenuItem searchView = menu.findItem(R.id.search);
        searchView.setVisible(false);
        //edit menu will show if user coming from MyPetitions
        if(!editable){
            edit.setVisible(false);
        }
        return true;
    }

    //Do what needs to be done when the item is clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Either back to myPetition if user came from myPetition, or back to PetitionList otherwise
                if (!editable){
                    startActivity( new Intent(this, PetitionList.class));
                }
                break;
            case R.id.editBtn:
                Toast.makeText(this, "Editing", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), UpdatePetition.class);
                intent.putExtra("id", id);
                intent.putExtra("editable", editable); //type (editable,viewOnly)//depends on where user is coming from
                intent.putExtra("activity", activity);
                startActivity(intent);
                break;
            case R.id.logout:
                Toast.makeText(this, "Signout", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();//logout
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}