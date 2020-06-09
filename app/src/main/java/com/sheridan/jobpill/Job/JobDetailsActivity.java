package com.sheridan.jobpill.Job;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.WriteResult;
import com.sheridan.jobpill.Auth.LoginActivity;
import com.sheridan.jobpill.MainActivity;
import com.sheridan.jobpill.Models.Job;
import com.sheridan.jobpill.Models.JobApplication;
import com.sheridan.jobpill.Profile.ProfileActivity;
import com.sheridan.jobpill.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JobDetailsActivity extends AppCompatActivity {

    private TextView txtJobTitle;
    private TextView txtJobLocation;
    private TextView txtJobEstimatedPay;
    private TextView txtJobDescription;

    private ImageView backButton;

    private Button btn_apply;
    private Button btn_contact;

    private ImageView jobImage;

    private Job currentJob;

    FirebaseAuth firebaseAuth;

    FirebaseFirestore firebaseFirestore;

    String current_user_id;

    CollectionReference jobsRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

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

        Glide.with(JobDetailsActivity.this).setDefaultRequestOptions(placeholderRequest).load(currentJob.getPhotoURL()).into(jobImage);

        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JobApplication jobApplication = new JobApplication();
                jobApplication.setApplicantId(current_user_id);
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                jobApplication.setApplicationDate(date);
                jobApplication.setStatus("pending approval");

                SharedPreferences settings = getSharedPreferences("myprofile",
                        Context.MODE_PRIVATE);

                String name = settings.getString("name","");
                String intro = settings.getString("intro","");
                String phone = settings.getString("phone","");
                String city = settings.getString("city","");
                String image = settings.getString("image","");

                //set user details in job_application
                jobApplication.setApplicantName(name);
                jobApplication.setApplicantIntro(intro);
                jobApplication.setApplicantPhone(phone);
                jobApplication.setApplicantCity(city);
                jobApplication.setApplicantPhoto(image);

                jobsRef.document(currentJob.getItemId()).collection("jobApplications").add(jobApplication).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(JobDetailsActivity.this, "Application Successful", Toast.LENGTH_LONG).show();

//                            Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
//                            startActivity(intent);
//                            finish();

                            btn_apply.setEnabled(false);
                            btn_apply.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.colorButtonDisabled));
                        }
                    }
                });
            }
        });
    }

    public void setupWidgets() {
        txtJobTitle = findViewById(R.id.details_txtJobTitle);
        txtJobEstimatedPay = findViewById(R.id.details_txtJobEstimatedPay);
        txtJobDescription = findViewById(R.id.details_txtJobDescription);
        txtJobLocation = findViewById(R.id.details_txtJobLocation);
        jobImage = findViewById(R.id.img_jd);
        btn_apply = findViewById(R.id.btn_apply);
        btn_contact = findViewById(R.id.btn_contact);

        backButton = findViewById(R.id.jobDetails_back_button);

        //firestore initialize
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

           jobsRef.document(currentJob.getItemId()).collection("jobApplications").whereEqualTo("applicantId",current_user_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
               @Override
               public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){

                        if(task.getResult().size() > 0){
                            Log.d("FOUND_APPLICATION", "User has already applied for the job");
                            btn_apply.setEnabled(false);
                            btn_apply.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.colorButtonDisabled));
                        }
                    }
               }
           });
        }
    }

    private void sendToLogin() {
        Intent intent = new Intent(JobDetailsActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToHome() {
        Intent intent = new Intent(JobDetailsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
