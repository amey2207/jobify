package com.sheridan.jobpill.Job;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sheridan.jobpill.JobApplication.JobApplicantProfile;
import com.sheridan.jobpill.JobApplication.JobApplicationListFirestoreAdapter;
import com.sheridan.jobpill.Models.Job;
import com.sheridan.jobpill.Models.JobApplication;
import com.sheridan.jobpill.R;

public class JobApplicants extends AppCompatActivity implements JobApplicationListFirestoreAdapter.OnListItemClick {

    private RecyclerView jobApplicantsListView;
    private JobApplicationListFirestoreAdapter adapter;

    private Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser currentUser;
    private String current_user_id;
    private String jobID;

    private ImageView backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_applicants);

        if (getIntent().hasExtra("JobID")) {

            jobID = getIntent().getStringExtra("JobID");

            Log.d("JOB_ID", "Job Details: " + jobID);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar_job_applicants);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        setupWidgets();


        currentUser = firebaseAuth.getCurrentUser();

        firebaseFirestore.collection("jobApplications").whereEqualTo("jobId",jobID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document: task.getResult()){
                        Log.d("TEST", document.getId() + " => " + document.getData());
                    }
                }else{
                    Log.d("TEST", "Error getting documents: ", task.getException());
                }
            }
        });

        //Query
        Query query = firebaseFirestore
                .collection("jobApplications")
                .whereEqualTo("jobId",jobID)
                .orderBy("applicationDate", Query.Direction.DESCENDING);

        //RecyclerOptions

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(3)
                .build();

        FirestorePagingOptions<JobApplication> options = new FirestorePagingOptions.Builder<JobApplication>()
                .setLifecycleOwner(this)
                .setQuery(query, config, JobApplication.class)
                .build();

        adapter = new JobApplicationListFirestoreAdapter(options, this);


        jobApplicantsListView.setHasFixedSize(true);
        jobApplicantsListView.setLayoutManager(new LinearLayoutManager(this));
        jobApplicantsListView.setAdapter(adapter);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setupWidgets() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        jobApplicantsListView = findViewById(R.id.jobApplicants);
        backButton = findViewById(R.id.jobapplicants_backbutton);
    }

    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {
        Log.d("APPLICANT_CLICKED", "Clicked the item: " + position + "and ID: " + snapshot.getId());

        JobApplication jobApplication = snapshot.toObject(JobApplication.class);
        jobApplication.setItemId(snapshot.getId());

        Intent intent = new Intent(this, JobApplicantProfile.class);
        intent.putExtra("jobApplicant", jobApplication);
        startActivity(intent);



    }
}