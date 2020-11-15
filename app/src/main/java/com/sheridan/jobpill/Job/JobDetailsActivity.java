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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sheridan.jobpill.Auth.LoginActivity;
import com.sheridan.jobpill.MainActivity;
import com.sheridan.jobpill.Messaging.ChatActivity;
import com.sheridan.jobpill.Messaging.MessagesActivity;
import com.sheridan.jobpill.Models.Job;
import com.sheridan.jobpill.Models.JobApplication;
import com.sheridan.jobpill.Models.Messaging;
import com.sheridan.jobpill.Models.User;
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
    private FirebaseUser currentUser;
    CollectionReference jobsRef;
    CollectionReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        User user = new User();
        DocumentReference docRef = firebaseFirestore.collection("Users").document(currentUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        user.setName(document.getString("name"));
                        user.setPhotoURL(document.getString("photoURL"));
                        Log.d("", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

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

        btn_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sender
                Messaging messaging = new Messaging();
                messaging.setContactName(currentJob.getCreatedByName());
                messaging.setContactPhotoURL(currentJob.getCreatedByPhotoURL());
                messaging.setContactId(currentJob.getCreatedByUID());
                messaging.setChatJobId(currentJob.getItemId());
                messaging.setChatJobName(currentJob.getJobTitle());
                DocumentReference ref = userRef.document(current_user_id)
                        .collection("Contacts").document();
                messaging.setItemId(ref.getId());
                ref.set(messaging);
                //Receiver
                Messaging messagingReceiver = new Messaging();
                messagingReceiver.setContactName(user.getName());
                messagingReceiver.setContactPhotoURL(user.getPhotoURL());
                messagingReceiver.setContactId(current_user_id);
                messagingReceiver.setChatJobId(currentJob.getItemId());
                messagingReceiver.setChatJobName(currentJob.getJobTitle());
                DocumentReference refReceiver = userRef.document(currentJob.getCreatedByUID())
                        .collection("Contacts").document();
                messagingReceiver.setItemId(refReceiver.getId());
                refReceiver.set(messagingReceiver);

                Intent intent = new Intent(JobDetailsActivity.this, ChatActivity.class);
                intent.putExtra("contactID", currentJob.getCreatedByUID());
                intent.putExtra("contactName", currentJob.getCreatedByName());
                if (currentJob.getCreatedByPhotoURL() != null) {
                    intent.putExtra("contactPhoto", currentJob.getCreatedByPhotoURL());
                } else {
                    intent.putExtra("contactPhoto", "profile_default");
                }
                startActivity(intent);
            }
        });

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

                String name = settings.getString("name", "");
                String intro = settings.getString("intro", "");
                String phone = settings.getString("phone", "");
                String city = settings.getString("city", "");
                String image = settings.getString("image", "");
                String dateOfBirth = settings.getString("dateOfBirth", "");

                //set user details in job_application
                jobApplication.setApplicantName(name);
                jobApplication.setApplicantIntro(intro);
                jobApplication.setApplicantPhone(phone);
                jobApplication.setApplicantCity(city);
                jobApplication.setApplicantPhoto(image);
                jobApplication.setJobId(currentJob.getItemId());
                jobApplication.setJobTitle(currentJob.getJobTitle());
                jobApplication.setJobDescription(currentJob.getJobDescription());
                jobApplication.setJobLocation(currentJob.getLocation());
                jobApplication.setJobPhotoURL(currentJob.getPhotoURL());
                jobApplication.setApplicantDateOfBirth(dateOfBirth);

                DocumentReference ref = jobsRef.document(currentJob.getItemId()).collection("jobApplications").document();

                jobApplication.setItemId(ref.getId());

                ref.set(jobApplication).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(JobDetailsActivity.this, "Application Successful", Toast.LENGTH_LONG).show();
                            btn_apply.setEnabled(false);
                            btn_apply.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.colorButtonDisabled));
                        } else {
                            Toast.makeText(JobDetailsActivity.this, "Application Failed: Database Error", Toast.LENGTH_LONG).show();
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
        //FireStore initialize
        jobsRef = firebaseFirestore.collection("jobs");
        userRef = firebaseFirestore.collection("Users");
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!currentJob.getHiredApplicant().isEmpty()) {
            Log.d("FOUND_APPLICATION", "User has already applied for the job");
            btn_apply.setEnabled(false);
            btn_apply.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.colorButtonDisabled));
        }

        if (currentUser == null) {
            sendToLogin();
        } else {
            current_user_id = currentUser.getUid();

            jobsRef.document(currentJob.getItemId()).collection("jobApplications")
                    .whereEqualTo("applicantId", current_user_id)
                    .whereEqualTo("jobId", currentJob.getItemId())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() > 0) {
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