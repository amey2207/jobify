package com.sheridan.jobpill.Job;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sheridan.jobpill.Auth.LoginActivity;
import com.sheridan.jobpill.MainActivity;
import com.sheridan.jobpill.Models.Job;
import com.sheridan.jobpill.Models.JobApplication;
import com.sheridan.jobpill.Profile.ProfileActivity;
import com.sheridan.jobpill.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JobDetailsInProgress extends AppCompatActivity {

    private TextView txtJobTitle;
    private TextView txtJobLocation;
    private TextView txtJobEstimatedPay;
    private TextView txtJobDescription;

    private ImageView backButton;

    private Button btn_complete;

    private ImageView jobImage;

    private Job currentJob;

    FirebaseAuth firebaseAuth;

    FirebaseFirestore firebaseFirestore;

    String current_user_id;

    CollectionReference jobsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details_inprogress);
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

        txtJobTitle.setText(currentJob.getJobTitle());
        txtJobEstimatedPay.setText("$" + String.valueOf(currentJob.getEstimatedPay()));
        txtJobLocation.setText(currentJob.getLocation());
        txtJobDescription.setText(currentJob.getJobDescription());

        RequestOptions placeholderRequest = new RequestOptions();
        placeholderRequest.placeholder(R.drawable.profile_default);

        Glide.with(JobDetailsInProgress.this).setDefaultRequestOptions(placeholderRequest).load(currentJob.getPhotoURL()).into(jobImage);
        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JobDetailsInProgress.this, JobCompletionFormActivity.class);
                intent.putExtra("JobId", currentJob.getItemId());
                startActivity(intent);
                finish();
            }
        });
    }

    public void setupWidgets() {
        txtJobTitle = findViewById(R.id.details_txtJobTitle);
        txtJobEstimatedPay = findViewById(R.id.details_txtJobEstimatedPay);
        txtJobDescription = findViewById(R.id.details_txtJobDescription);
        txtJobLocation = findViewById(R.id.details_txtJobLocation);
        jobImage = findViewById(R.id.img_jd);

        btn_complete = findViewById(R.id.btn_complete);
        backButton = findViewById(R.id.jobDetails_back_button);
        //FireStore initialize
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        jobsRef = firebaseFirestore.collection("jobs");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            sendToLogin();
        } else {
            current_user_id = currentUser.getUid();
        }
    }

    private void sendToLogin() {
        Intent intent = new Intent(JobDetailsInProgress.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToHome() {
        Intent intent = new Intent(JobDetailsInProgress.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
