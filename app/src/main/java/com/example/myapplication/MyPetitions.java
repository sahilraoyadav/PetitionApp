package com.example.myapplication;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

/* Not initialized */
public class MyPetitions extends AppCompatActivity { // This is under construction, no access to this yet
    private static final String TAG = "MyPetition";

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final CollectionReference userRef = firebaseFirestore.collection("users");

    private recviewAdapter adapter;
    RecyclerView recyclerView;
    FloatingActionButton newPost;

    //Swipe to delete
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if (direction == ItemTouchHelper.LEFT) {
                recviewAdapter.recviewHolder recviewHolder = (recviewAdapter.recviewHolder) viewHolder;
                recviewHolder.deleteItem();
            }

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(MyPetitions.this, R.color.colorAccent))
                    .addActionIcon(R.drawable.ic_baseline_delete_24)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_petitions);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recview);

        setUpRecyclerView();

    }
    //Sets the Recycler view
    private void setUpRecyclerView() {
        //.whereEqualTo("email",)
        //System.out.println((FirebaseAuth.getInstance().getCurrentUser().getEmail())+"===================================");
        final Query query = userRef
                .whereEqualTo("email", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
                //.orderBy("date", Query.Direction.DESCENDING);
        final FirestoreRecyclerOptions<Model> options = new FirestoreRecyclerOptions.Builder<Model>()
                .setQuery(query, Model.class)
                .build();
        //adapter = new recviewAdapter(options);

        adapter = new recviewAdapter(options, new recviewAdapter.OnItemClickListener() {
            @Override
            public void handleDeleteItem(DocumentSnapshot snapshot) {
                deleteItem(snapshot);
            }
            //Teal color Color.rgb(0,113,133)


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

        //Adds the itemtouch helper to recyler View for the delete button
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void deleteItem(DocumentSnapshot snapshot) {
        final DocumentReference documentReference = snapshot.getReference();
        final Model model = snapshot.toObject(Model.class);

        documentReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Item deleted");
                    }
                });

        Snackbar.make(recyclerView, "Petition deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        assert model != null;
                        documentReference.set(model);
                    }
                })
                .setActionTextColor(Color.RED)
                .show();
    }

    //Do what needs to be done when the item is clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            Toast.makeText(this, "Signout", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();//logout
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
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
        MenuItem myPetition = menu.findItem(R.id.myPosts);
        myPetition.setVisible(false);
        Snackbar deleteHint = Snackbar.make(recyclerView, "Swipe the Petition to Delete", Snackbar.LENGTH_LONG);
        deleteHint.show();

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

        return true;
    }

    public void processSearch(String s) {
        Log.d(TAG, "Searchbox has changed to: " + s);
        Query query;
        if (s.isEmpty()) {
            query = userRef.orderBy("date", Query.Direction.ASCENDING).whereEqualTo("email", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
        } else {
            query = userRef
                    .whereEqualTo("title", s)
                    .whereEqualTo("email", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail())
                    .orderBy("date", Query.Direction.ASCENDING);
        }
        FirestoreRecyclerOptions<Model> options = new FirestoreRecyclerOptions.Builder<Model>()
                .setQuery(query, Model.class)
                .build();
        //adapter.updateOptions(options);
        adapter = new recviewAdapter(options, new recviewAdapter.OnItemClickListener() {
            @Override
            public void handleDeleteItem(DocumentSnapshot snapshot) {
                deleteItem(snapshot);
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
        Model note = snapshot.toObject(Model.class);
        String id = snapshot.getId();
        assert note != null;
        note.setDocId(snapshot.getId());

        Intent intent = new Intent(getApplicationContext(), PetitionPage.class);
        intent.putExtra("id", id);
        intent.putExtra("editable", true);
        intent.putExtra("activity", "MyPetitions");

        startActivity(intent);
    }

    //Removes the backbutton functionality when signed in.
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        // Disables back button in current screen.
        Toast.makeText(this, "Sorry Back Button is Disabled until Logout.", Toast.LENGTH_SHORT).show();
    }


}