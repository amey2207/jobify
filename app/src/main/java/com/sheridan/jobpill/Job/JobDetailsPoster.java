package com.sheridan.jobpill.Job;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sheridan.jobpill.MainActivity;
import com.sheridan.jobpill.Models.Job;
import com.sheridan.jobpill.R;

public class JobDetailsPoster extends AppCompatActivity {

    private TextView txtJobTitle;
    private TextView txtJobLocation;
    private TextView txtJobEstimatedPay;
    private TextView txtJobDescription;
    private ImageView jobImage;
    private Button btnViewApplicants;
    private Toolbar toolbar;
    private ImageView backButton;
    private String CurrentJob;
    private Job currentJob;

    String current_user_id;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    CollectionReference jobsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details_poster);
        toolbar = findViewById(R.id.jdp_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        setupWidgets();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToHome();
            }
        });

        if (getIntent().hasExtra("JobSnapshot")) {
            currentJob = getIntent().getParcelableExtra("JobSnapshot");
            Log.d("JOB_DETAILS", "Job Details: " + currentJob.toString());
        }

        CurrentJob = currentJob.getItemId();
        txtJobTitle.setText(currentJob.getJobTitle());
        txtJobEstimatedPay.setText("$" + String.valueOf(currentJob.getEstimatedPay()));
        txtJobLocation.setText(currentJob.getLocation());
        txtJobDescription.setText(currentJob.getJobDescription());

        RequestOptions placeholderRequest = new RequestOptions();
        placeholderRequest.placeholder(R.drawable.profile_default);

        Glide.with(JobDetailsPoster.this).setDefaultRequestOptions(placeholderRequest).load(currentJob.getPhotoURL()).into(jobImage);

        btnViewApplicants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewApplicants();
            }
        });
    }

    public void setupWidgets() {
        txtJobTitle = findViewById(R.id.jdp_jobTitle);
        txtJobEstimatedPay = findViewById(R.id.jdp_jobEstimatedPay);
        txtJobDescription = findViewById(R.id.jdp_jobDescription);
        txtJobLocation = findViewById(R.id.jdp_jobLocation);
        jobImage = findViewById(R.id.jdp_img);

        btnViewApplicants = findViewById(R.id.btn_viewApplicants);
        backButton = findViewById(R.id.jobDetailsPoster_back_button);

        //firesStore initialize
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        jobsRef = firebaseFirestore.collection("jobs");
    }

    private void viewApplicants() {
        Log.d("VIEW_APPLICANTS", "Clicked View Applicants");

        Intent intent = new Intent(this, JobApplications.class);
        intent.putExtra("JobID", currentJob.getItemId());
        startActivity(intent);

    }

    private void sendToHome() {
        Intent intent = new Intent(JobDetailsPoster.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.job_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Get the menu item and determine what action to take.
        switch (item.getItemId()) {
            case R.id.remove:
                new AlertDialog.Builder(JobDetailsPoster.this)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                firebaseAuth = FirebaseAuth.getInstance();
                                firebaseFirestore = FirebaseFirestore.getInstance();
                                firebaseFirestore.collection("jobs").document(CurrentJob).delete();
                                Intent intent = new Intent(JobDetailsPoster.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                //case R.id.edit:
                //  Intent intent = new Intent(JobDetailsPoster.this, JobPostingEditActivity.class);
                //startActivity(intent);
                //finish();
            default:
                // Otherwise, do nothing.
                break;
        }
        // Call the super version of this method.
        return super.onOptionsItemSelected(item);
    }
}
