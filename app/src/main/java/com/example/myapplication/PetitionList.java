package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class PetitionList extends AppCompatActivity {
    private static final String TAG = "PetitionList";
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final CollectionReference userRef = firebaseFirestore.collection("users");
    private recviewAdapter adapter;
    RecyclerView recyclerView;
    FloatingActionButton newPost;
    public EditText searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petition_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recview);

        //Creates a floating action button for adding a post
        newPost = findViewById(R.id.newPost);
        newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MakePetition.class));
            }
        });
        setUpRecyclerView();
        Snackbar appHint = Snackbar.make(recyclerView, "Deletion is available in my petitions", Snackbar.LENGTH_LONG);
        appHint.show();
    }
    //Sets the Recycler view
    private void setUpRecyclerView() {
        final Query query = userRef.orderBy("date", Query.Direction.DESCENDING);
        final FirestoreRecyclerOptions<Model> options = new FirestoreRecyclerOptions.Builder<Model>()
                .setQuery(query, Model.class)
                .build();
        adapter = new recviewAdapter(options, new recviewAdapter.OnItemClickListener() {
            @Override
            public void handleDeleteItem(DocumentSnapshot snapshot) {
                //Implemented in MyPetition
            }

            @Override
            public void onItemClick(DocumentSnapshot snapshot, int position) {
                itemClick(snapshot, position);

            }
        });
        RecyclerView recyclerView = findViewById(R.id.recview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Makes the recycler view horizontal
        // recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

    }

    //Do what needs to be done when the item is clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.myPosts:
                Toast.makeText(this, "Showing your Posts", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MyPetitions.class));
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

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    //Creates the menu for the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflates the menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.petition_list_menu, menu);
        //sets the edit menu item to be hidden since it is not needed
        MenuItem edit = menu.findItem(R.id.editBtn);
        edit.setVisible(false);
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setQueryHint("Search by Title");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                processSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                processSearch(s);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void processSearch(String s) {
        Log.d(TAG, "Searchbox has changed to: " + s);
        Query query;
        if (s.isEmpty()) {
            query = userRef.orderBy("date", Query.Direction.ASCENDING);
        } else {
            query = userRef
                    .whereEqualTo("title", s)
                    .orderBy("date", Query.Direction.ASCENDING);
        }
        FirestoreRecyclerOptions<Model> options = new FirestoreRecyclerOptions.Builder<Model>()
                .setQuery(query, Model.class)
                .build();
        //adapter.updateOptions(options);
        adapter = new recviewAdapter(options, new recviewAdapter.OnItemClickListener() {
            @Override
            public void handleDeleteItem(DocumentSnapshot snapshot) {

            }

            @Override
            public void onItemClick(DocumentSnapshot snapshot, int position) {
                itemClick(snapshot, position);
            }
        });
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    public void itemClick(DocumentSnapshot snapshot, int position) {
        if (snapshot.getString("email").equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail())) {
            Model note = snapshot.toObject(Model.class);
            String id = snapshot.getId();
            assert note != null;
            note.setDocId(snapshot.getId());
                /* Used for testing the snapshot by getting the id of item you clicked.
                String path = snapshot.getReference().getPath();
                Toast.makeText(getApplicationContext(),"Position: " + position + " ID: " + id, Toast.LENGTH_SHORT).show();
                 */
            Intent intent = new Intent(getApplicationContext(), PetitionPage.class);
            intent.putExtra("id", id);
            intent.putExtra("editable", true);
            intent.putExtra("activity", "PetitionList");
            startActivity(intent);
        } else {
            Model note = snapshot.toObject(Model.class);
            String id = snapshot.getId();
            assert note != null;
            note.setDocId(snapshot.getId());
                /* Used for testing the snapshot by getting the id of item you clicked.
                String path = snapshot.getReference().getPath();
                Toast.makeText(getApplicationContext(),"Position: " + position + " ID: " + id, Toast.LENGTH_SHORT).show();
                 */
            Intent intent = new Intent(getApplicationContext(), PetitionPage.class);
            intent.putExtra("id", id);
            intent.putExtra("editable", false);
            intent.putExtra("activity", "PetitionList");
            startActivity(intent);
        }
    }


    //Removes the backbutton functionality when signed in.
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        // Disables back button in current screen.
        Toast.makeText(this, "Sorry Back Button is Disabled until Logout.", Toast.LENGTH_SHORT).show();
    }


}