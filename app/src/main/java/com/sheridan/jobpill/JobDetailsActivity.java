package com.sheridan.jobpill;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.sheridan.jobpill.Models.Job;

public class JobDetailsActivity extends AppCompatActivity {

    private TextView txtJobID;
    private TextView txtJobTitle;
    private Job currentJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        txtJobID = findViewById(R.id.details_txtJobID);
        txtJobTitle = findViewById(R.id.details_txtJobTitle);

        if(getIntent().hasExtra("JobSnapshot")){
             currentJob = getIntent().getParcelableExtra("JobSnapshot");

            Log.d("JOB_DETAILS","Job Details: " + currentJob.toString());
        }

        txtJobTitle.setText(currentJob.getJobTitle());
        txtJobID.setText(currentJob.getItemId());
    }
}
