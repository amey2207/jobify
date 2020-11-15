package com.sheridan.jobpill.Messaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.sheridan.jobpill.Job.JobDetailsActivity;
import com.sheridan.jobpill.Job.MyJobsActivity;
import com.sheridan.jobpill.MainActivity;
import com.sheridan.jobpill.Models.Messaging;
import com.sheridan.jobpill.Profile.ProfileActivity;
import com.sheridan.jobpill.R;

public class MessagesActivity extends AppCompatActivity implements MessagingListFirestoreAdapter.OnListItemClick {

    private RecyclerView messageListView;
    private MessagingListFirestoreAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser currentUser;
    private String current_user_id;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        toolbar = findViewById(R.id.top_toolbar_messages);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        setupWidgets();
        currentUser = firebaseAuth.getCurrentUser();
        current_user_id = currentUser.getUid();

        Query query = firebaseFirestore
                .collection("Users")
                .document(current_user_id)
                .collection("Contacts");

        //RecyclerOptions
        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(3)
                .build();

        FirestorePagingOptions<Messaging> options = new FirestorePagingOptions.Builder<Messaging>()
                .setLifecycleOwner(this)
                .setQuery(query, config, Messaging.class)
                .build();

        adapter = new MessagingListFirestoreAdapter(options, this);

        messageListView.setHasFixedSize(true);
        messageListView.setLayoutManager(new LinearLayoutManager(this));
        messageListView.setAdapter(adapter);

        bottomNavigationView.setSelectedItemId(R.id.bottom_action_messages);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.bottom_action_jobs:
                        sendToMyJobs();
                        return true;
                    case R.id.bottom_action_home:
                        sendToMain();
                        return true;
                    case R.id.bottom_action_account:
                        sendToProfile();
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    private void sendToProfile() {
        Intent intent = new Intent(MessagesActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupWidgets() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        messageListView = findViewById(R.id.chat_recent);
        toolbar = findViewById(R.id.top_toolbar_messages);
        bottomNavigationView = findViewById(R.id.messages_bottom_nav);

    }

    private void sendToMain() {
        Intent intent = new Intent(MessagesActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToMyJobs() {
        Intent intent = new Intent(MessagesActivity.this, MyJobsActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {
        Log.d("APPLICANT_CLICKED", "Clicked the item: " + position + "and ID: " + snapshot.getId());

        Messaging messaging = snapshot.toObject(Messaging.class);
        messaging.setItemId(snapshot.getId());

        Intent intent = new Intent(MessagesActivity.this, ChatActivity.class);
        intent.putExtra("contactID", messaging.getContactId());
        intent.putExtra("contactName", messaging.getContactName());
        if (messaging.getContactPhotoURL() != null) {
            intent.putExtra("contactPhoto", messaging.getContactPhotoURL());
        } else {
            intent.putExtra("contactPhoto", "profile_default");
        }
        startActivity(intent);
    }
}