package com.sheridan.jobpill.JobApplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sheridan.jobpill.Auth.LoginActivity;
import com.sheridan.jobpill.Job.JobDetailsActivity;
import com.sheridan.jobpill.Job.MyJobsActivity;
import com.sheridan.jobpill.MainActivity;
import com.sheridan.jobpill.Messaging.MessagesActivity;
import com.sheridan.jobpill.Models.JobApplication;
import com.sheridan.jobpill.Profile.ProfileActivity;
import com.sheridan.jobpill.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class JobApplicantProfile extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

   private CircleImageView applicantProfileImg;
   private TextView txtApplicantName;
   private TextView txtApplicantCity;
   private TextView txtApplicantIntro;
   private  TextView txtApplicantRating;
   private TextView txtApplicantJobsCompleted;

   private ImageView backBtn;

   private ChipGroup interestChipGroup;

    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;

    private JobApplication currentJobApplication;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_applicant_profile);

        toolbar = findViewById(R.id.top_toolbar_jobapplicant_profile);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        setupWidgets();

        bottomNavigationView.setSelectedItemId(R.id.bottom_action_home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.bottom_action_jobs:
                        sendToMyJobs();
                        return true;
                    case R.id.bottom_action_account:
                        sendToAccount();
                    case R.id.bottom_action_messages:
                        sendToMessages();

                    default: return false;
                }
            }
        });


        if(getIntent().hasExtra("jobApplicant")){
            currentJobApplication = getIntent().getParcelableExtra("jobApplicant");
            Log.d("JOB_APPLICATION_DETAILS", "Job Application Details: " + currentJobApplication.toString());

            txtApplicantName.setText(currentJobApplication.getApplicantName());
            txtApplicantCity.setText(currentJobApplication.getApplicantCity());
            txtApplicantIntro.setText(currentJobApplication.getApplicantIntro());


            RequestOptions placeholderRequest = new RequestOptions();
            placeholderRequest.placeholder(R.drawable.profile_default);

            Glide.with(JobApplicantProfile.this).setDefaultRequestOptions(placeholderRequest).load(currentJobApplication.getApplicantPhoto()).into(applicantProfileImg);
        }

    }

    private void setupWidgets() {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        applicantProfileImg = findViewById(R.id.applicant_profile_img);
        txtApplicantName = findViewById(R.id.txt_applicant_name);
        txtApplicantCity = findViewById(R.id.txt_applicant_city);
        txtApplicantIntro = findViewById(R.id.txt_applicant_intro);
        interestChipGroup = findViewById(R.id.interest_chipGroup);

        backBtn = findViewById(R.id.applicant_profile_back_btn);

        bottomNavigationView = findViewById(R.id.applicantProfileBottomNav);

    }

    @Override
    protected void onStart() {
        super.onStart();


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void sendToLogin() {
        Intent intent = new Intent(JobApplicantProfile.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToMain() {
        Intent intent = new Intent(JobApplicantProfile.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void sendToMyJobs() {
        Intent intent = new Intent(JobApplicantProfile.this, MyJobsActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToMessages() {
        Intent intent = new Intent(JobApplicantProfile.this, MessagesActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToAccount() {
        Intent intent = new Intent(JobApplicantProfile.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }
}