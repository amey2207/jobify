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
import com.sheridan.jobpill.MainActivity;
import com.sheridan.jobpill.Models.Job;
import com.sheridan.jobpill.Models.JobApplication;
import com.sheridan.jobpill.Profile.ProfileActivity;
import com.sheridan.jobpill.R;

public class JobsInProgressActivity extends AppCompatActivity implements JobsListFirestoreAdapter.OnListItemClick, FilterAlertDialog.FilterDialogListener {

    private RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private JobsListFirestoreAdapter adapter;
    private Toolbar toolbar;
    private LinearLayoutManager linearLayoutManager;
    private ImageView imageView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs_in_progress);
        toolbar = (Toolbar) findViewById(R.id.top_toolbar_job_inprogress);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        currentUser = firebaseAuth.getCurrentUser();

        imageView = findViewById(R.id.jobsinprogress_back_button);

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
                .collection("jobs")
                //.whereEqualTo("hiredApplicant", currentUser.getUid())
                .whereEqualTo("createdBy", currentUser.getEmail())
                .whereEqualTo("jobStatus", "In-Progress")
                .orderBy("createdDate",Query.Direction.DESCENDING);

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(3)
                .build();

        FirestorePagingOptions<Job> options = new FirestorePagingOptions.Builder<Job>()
                .setLifecycleOwner(this)
                .setQuery(query, config, Job.class)
                .build();

        adapter = new

                JobsListFirestoreAdapter(options, this, this);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new

                LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    protected void onLoa(Bundle savedInstanceState) {
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


        Intent intent = new Intent(this, JobDetailsInProgress.class);
        intent.putExtra("JobSnapshot", job);
        intent.putExtra("Role", "JobPoster");
        startActivity(intent);
    }

    private void sendToProfile() {
        Intent intent = new Intent(JobsInProgressActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToHome() {
        Intent intent = new Intent(JobsInProgressActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToMyJobs() {
        Intent intent = new Intent(JobsInProgressActivity.this, JobsInProgressOptionActivity.class);
        startActivity(intent);
        finish();
    }
}
