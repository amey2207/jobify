package com.sheridan.jobpill.Job;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sheridan.jobpill.Models.Job;
import com.sheridan.jobpill.R;

public class JobDetailsPoster extends AppCompatActivity {

    private TextView txtJobTitle;
    private TextView txtJobLocation;
    private TextView txtJobEstimatedPay;
    private TextView txtJobDescription;
    private ImageView jobImage;
    private Button btnViewApplicants;

    private Job currentJob;

    String current_user_id;


    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    CollectionReference jobsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details_poster);

        setupWidgets();

        if(getIntent().hasExtra("JobSnapshot")){
            currentJob = getIntent().getParcelableExtra("JobSnapshot");
            Log.d("JOB_DETAILS", "Job Details: " + currentJob.toString());

        }

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



        //firestore initialize
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        jobsRef = firebaseFirestore.collection("jobs");
    }

    private  void viewApplicants(){
        Log.d("VIEW_APPLICANTS", "Clicked View Applicants");

        Intent intent = new Intent(this, JobApplicants.class);
        intent.putExtra("JobID", currentJob.getItemId());
        startActivity(intent);

    }



}
