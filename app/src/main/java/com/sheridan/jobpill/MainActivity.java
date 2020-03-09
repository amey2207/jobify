package com.sheridan.jobpill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.sheridan.jobpill.Models.Job;

public class MainActivity extends AppCompatActivity implements JobsListFirestoreAdapter.OnListItemClick {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private String current_user_id;

    private Toolbar toolbar;

    private RecyclerView jobsListView;

    private JobsListFirestoreAdapter adapter;
    private ImageButton btn_plus;

    private TextView txtGreeting;


    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.top_toolbar_main);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        setupWidgets();
        btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent jobIntent = new Intent(MainActivity.this, JobPostingActivity.class);
                startActivity(jobIntent);
                finish();
            }
        });


        bottomNavigationView.setSelectedItemId(R.id.bottom_action_home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.bottom_action_jobs:
//                        sentToJobs();
                        return true;
                    case R.id.bottom_action_schedule:
//                        sendToSchedule();
                        return true;
                    case R.id.bottom_action_account:
                        sendToProfile();
                        return true;

                    default:
                        return false;
                }
            }
        });

        //Query
        Query query = firebaseFirestore.collection("jobs");
        //RecyclerOptions

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(3)
                .build();

        FirestorePagingOptions<Job> options = new FirestorePagingOptions.Builder<Job>()
                .setLifecycleOwner(this)
                .setQuery(query, config, Job.class)
                .build();

        adapter = new JobsListFirestoreAdapter(options,this );



        jobsListView.setHasFixedSize(true);
        jobsListView.setLayoutManager(new LinearLayoutManager(this));
        jobsListView.setAdapter(adapter);

        //View Holder


    }




    public void setupWidgets() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        btn_plus = findViewById(R.id.plus_btn);
        txtGreeting = findViewById(R.id.txt_greeting);
        bottomNavigationView = findViewById(R.id.mainBottomNav);
        jobsListView = findViewById(R.id.jobs_list);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            sendToLogin();
        } else {
            current_user_id = firebaseAuth.getCurrentUser().getUid();

            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (!task.getResult().exists()) {
                            Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                        }
                    } else {
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, "Error " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }


    private void sendToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    private void sendToProfile() {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {
        Log.d("ITEM_CLICK", "Clicked the item: " + position + "and ID: " + snapshot.getId());

        Job job = snapshot.toObject(Job.class);
        job.setItemId(snapshot.getId());

        Intent intent = new Intent(this,JobDetailsActivity.class);
        intent.putExtra("JobSnapshot",job);
        startActivity(intent);
    }
}
