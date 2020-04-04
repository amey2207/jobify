package com.sheridan.jobpill.Job;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sheridan.jobpill.Auth.LoginActivity;
import com.sheridan.jobpill.FilterAlertDialog;
import com.sheridan.jobpill.MainActivity;
import com.sheridan.jobpill.Models.Job;
import com.sheridan.jobpill.Profile.EditProfileActivity;
import com.sheridan.jobpill.Profile.ProfileActivity;
import com.sheridan.jobpill.R;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

public class JobsActivity extends AppCompatActivity implements JobsListFirestoreAdapter.OnListItemClick, FilterAlertDialog.FilterDialogListener {

    private RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private JobsListFirestoreAdapter adapter;
    private Toolbar toolbar;
    private LinearLayoutManager linearLayoutManager;
    private BottomNavigationView bottomNavigationView;
    private boolean isScrolling;
    private boolean isLastItemReached;


    private List<Job> list;
    private DocumentSnapshot lastVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        currentUser = firebaseAuth.getCurrentUser();

        recyclerView = findViewById(R.id.jobsList);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        bottomNavigationView = findViewById(R.id.jobsBottomNav);
        bottomNavigationView.setSelectedItemId(R.id.bottom_action_jobs);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.bottom_action_jobs:
                        //                       sendToMyJobs();
                        return true;
                    case R.id.bottom_action_schedule:
//                        sendToSchedule();
                        return true;
                    case R.id.bottom_action_account:
                        sendToProfile();
                        return true;
                    case R.id.bottom_action_home:
                        sendToHome();
                    default:
                        return false;
                }
            }
        });
        list = new ArrayList<>();

        Query query = firebaseFirestore
                .collection("jobs")
                .whereEqualTo("createdBy", currentUser.getEmail());

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(3)
                .build();

        FirestorePagingOptions<Job> options = new FirestorePagingOptions.Builder<Job>()
                .setLifecycleOwner(this)
                .setQuery(query, config, Job.class)
                .build();

        adapter = new JobsListFirestoreAdapter(options, this);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {


    }

    @Override
    public void onDialogNeutralClick(DialogFragment dialog) {

    }

    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {
        Log.d("ITEM_CLICK", "Clicked the item: " + position + "and ID: " + snapshot.getId());

        Job job = snapshot.toObject(Job.class);
        job.setItemId(snapshot.getId());


        if (currentUser.getEmail().equals(job.getCreatedBy())) {
            Intent intent = new Intent(this, JobDetailsPoster.class);
            intent.putExtra("JobSnapshot", job);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, JobDetailsActivity.class);
            intent.putExtra("JobSnapshot", job);
            startActivity(intent);
        }

    }

    private void sendToProfile() {
        Intent intent = new Intent(JobsActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToHome() {
        Intent intent = new Intent(JobsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}