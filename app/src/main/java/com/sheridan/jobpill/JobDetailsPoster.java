package com.sheridan.jobpill;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sheridan.jobpill.Models.Job;

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
    }

    public void setupWidgets() {
        txtJobTitle = findViewById(R.id.jdp_jobTitle);
        txtJobEstimatedPay = findViewById(R.id.jdp_jobEstimatedPay);
        txtJobDescription = findViewById(R.id.jdp_jobDescription);
        txtJobLocation = findViewById(R.id.jdp_jobLocation);
        jobImage = findViewById(R.id.jdp_img);



        //firestore initialize
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        jobsRef = firebaseFirestore.collection("jobs");
    }



}
