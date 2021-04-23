package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdatePetition extends AppCompatActivity {
    private final String TAG = "UpdatePetition";
    public static String id, activity;
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final CollectionReference userRef = firebaseFirestore.collection("users");
    public EditText petitionTitle, enterDescp;
    public String title, description, signature, fullName, date;
    public Button updateBtn;
    public TextView petitionAuthor, petitionDate, petitionSignatures;
    public Toolbar toolbar;
    public ActionBar actionBar;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_petition);

        setToolbar();
        setPage();
        updatePetition();
        setNavigation();

    }

    public void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void setNavigation() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PetitionPage.class);
                intent.putExtra("id", id);
                intent.putExtra("editable", true);
                intent.putExtra("activity", activity);
                startActivity(intent);
            }
        });
    }


    public void setPage() {
        RecyclerView recyclerView = findViewById(R.id.recview);
        petitionTitle = findViewById(R.id.petitionTitle);
        enterDescp = findViewById(R.id.enterDescp);
        updateBtn = findViewById(R.id.updateBtn);
        petitionAuthor = findViewById(R.id.petitionAuthor);
        petitionDate = findViewById(R.id.petitionDate);
        petitionSignatures = findViewById(R.id.petitionSignatures);


        Intent intent = getIntent();
        id = intent.getStringExtra("id").trim();
        activity = intent.getStringExtra("activity");
        userRef.document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                title = snapshot.getString("title");
                petitionTitle.setText(title);

                description = snapshot.getString("description");
                enterDescp.setText(description);

                fullName = snapshot.getString("fullName");

                date = snapshot.getString("date");
                petitionDate.setText("Date: " + date);

                petitionAuthor.setText("Posted by: " + fullName + " " + "on " + date);

                signature = String.valueOf(snapshot.get("signature"));
                petitionSignatures.setText("Signatures: " + signature);
            }
        });

    }

    public void updatePetition() {
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = petitionTitle.getText().toString().trim();
                final String descp = enterDescp.getText().toString().trim();
                final String nDate = date;
                final String nSignatures = signature;

                Model model = new Model(fAuth.getCurrentUser().getDisplayName(), fAuth.getCurrentUser().getEmail(), title, descp, nDate, Integer.valueOf(nSignatures));
                firebaseFirestore.collection("users").document(id).update(
                        "fullName", model.getFullName(),
                        "email", model.getEmail(),
                        "title", model.getTitle(),
                        "description", model.getDescription(),
                        "date", model.getDate(),
                        "signature", model.getSignature()
                );
                Toast.makeText(getApplicationContext(), "Changes saved", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), PetitionPage.class);
                intent.putExtra("id", id);
                intent.putExtra("editable", true);
                intent.putExtra("activity", activity);
                startActivity(intent);
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
        MenuItem edit = menu.findItem(R.id.editBtn);
        MenuItem myPosts = menu.findItem(R.id.myPosts);
        MenuItem searchView = menu.findItem(R.id.search);
        searchView.setVisible(false);
        edit.setVisible(false);
        myPosts.setVisible(false);
        return true;
    }

    //Do what needs to be done when the item is clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.myPosts:
//                Toast.makeText(this, "Showing your Posts", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(getApplicationContext(), UpdatePetition.class));
//                break;
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