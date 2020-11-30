package com.sheridan.jobpill.Job;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sheridan.jobpill.FilterAlertDialog;
import com.sheridan.jobpill.JobApplication.AppliedJobApplicationListFirestoreAdapter;
import com.sheridan.jobpill.JobApplication.JobApplicationListFirestoreAdapter;
import com.sheridan.jobpill.MainActivity;
import com.sheridan.jobpill.Models.Job;
import com.sheridan.jobpill.Models.JobApplication;
import com.sheridan.jobpill.Profile.ProfileActivity;
import com.sheridan.jobpill.R;

public class MyAppliedJobsActivity extends AppCompatActivity implements AppliedJobApplicationListFirestoreAdapter.OnListItemClick, FilterAlertDialog.FilterDialogListener {

    private RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private AppliedJobApplicationListFirestoreAdapter adapter;
    private Toolbar toolbar;
    private LinearLayoutManager linearLayoutManager;
    private ImageView imageView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_applied_jobs);

        toolbar = (Toolbar) findViewById(R.id.top_toolbar_myappliedjobs);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        currentUser = firebaseAuth.getCurrentUser();

        imageView = findViewById(R.id.myappliedjobs_back_button);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToMyJobs();
            }
        });

        recyclerView = findViewById(R.id.jobsList);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        Query query = firebaseFirestore
                .collectionGroup("jobApplications")
                .whereEqualTo("applicantId", currentUser.getUid())
                .orderBy("applicationDate",Query.Direction.DESCENDING);


        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(3)
                .build();

        FirestorePagingOptions<JobApplication> options = new FirestorePagingOptions.Builder<JobApplication>()
                .setLifecycleOwner(this)
                .setQuery(query, config, JobApplication.class)
                .build();

        adapter = new AppliedJobApplicationListFirestoreAdapter(options, this);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new

                LinearLayoutManager(this));
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


    /*
     * Get the JobApplication Snapshot from the ListAdapter
     * Create a local JobApplication object
     * Use the jobID from the local JobApplication Document to query for
     * the specific job that the application is for
     * Use the snapshot from the query result to pass it to the jobDetails Activity
     * */
    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {

        Log.d("ITEM_CLICK", "Clicked the item: " + position + "and ID: " + snapshot.getId());

        final JobApplication jobApplication = snapshot.toObject(JobApplication.class);



        firebaseFirestore.collection("jobs").document(jobApplication.getJobId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                   Job job = task.getResult().toObject(Job.class);
                   job.setItemId(task.getResult().getId());

                    Intent intent = new Intent(MyAppliedJobsActivity.this, JobDetailsActivity.class);
                    intent.putExtra("JobSnapshot", job);
                    startActivity(intent);

                    Log.d("JOB", "JOB: " + job.toString());

                }else{
                    Log.d("NO_JOB", "Firestore Query Failed - No Job Found with ID: " + jobApplication.getJobId());
                }
            }
        });


    }

    private void sendToProfile() {
        Intent intent = new Intent(MyAppliedJobsActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToHome() {
        Intent intent = new Intent(MyAppliedJobsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToMyJobs() {
        Intent intent = new Intent(MyAppliedJobsActivity.this, MyJobsActivity.class);
        startActivity(intent);
        finish();
    }
}
