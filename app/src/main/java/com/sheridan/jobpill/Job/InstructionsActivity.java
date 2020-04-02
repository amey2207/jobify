package com.sheridan.jobpill.Job;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.DatabaseReference;

import com.sheridan.jobpill.MainActivity;
import com.sheridan.jobpill.Models.Job;
import com.sheridan.jobpill.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class InstructionsActivity extends AppCompatActivity {

    Button CancelBtn;
    Button PostBtn;
    EditText InstructionsEdt;
    DatabaseReference reference;
    Job job;
    String user_id;
    private FirebaseFirestore db;
    CollectionReference dbJobs;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    Map<String, Object> jobMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_posting_instructions);
        CancelBtn = findViewById(R.id.cancel_btn);
        PostBtn = findViewById(R.id.post_btn);
        InstructionsEdt = findViewById(R.id.instructions_editText);
        Intent intent = getIntent();
        String jobTitle = intent.getStringExtra("Job_Title");
        long jobPayment = Long.parseLong(intent.getStringExtra("Job_Payment"));
        String jobLocation = intent.getStringExtra("Job_Location");
        String jobDescription = intent.getStringExtra("Job_Description");
        String jobCategory = intent.getStringExtra("Job_Category");
        String jobImage = intent.getStringExtra("Job_Image");
        String jobInstructions = InstructionsEdt.getText().toString();
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        currentUser = firebaseAuth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference().child("jobs").child(user_id);
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        jobMap = new HashMap<>();
        jobMap.put("createdBy", currentUser.getEmail());
        jobMap.put("createdDate", date);
        jobMap.put("estimatedPay", jobPayment);
        jobMap.put("photoURL", jobImage);
        jobMap.put("instructions", jobInstructions);
        jobMap.put("jobCategory", jobCategory);
        jobMap.put("jobDescription", jobDescription);
        jobMap.put("jobStatus", "available");
        jobMap.put("jobTitle", jobTitle);
        jobMap.put("location", jobLocation);
        db = FirebaseFirestore.getInstance();
        dbJobs = db.collection("jobs");
        CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent jobIntent = new Intent(InstructionsActivity.this, MainActivity.class);
                startActivity(jobIntent);
                finish();
            }
        });

        PostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbJobs.add(jobMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), "Job posted successfully", Toast.LENGTH_LONG).show();
                        Intent jobIntent = new Intent(InstructionsActivity.this, MainActivity.class);
                        startActivity(jobIntent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
