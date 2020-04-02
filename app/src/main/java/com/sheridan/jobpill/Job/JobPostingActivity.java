package com.sheridan.jobpill.Job;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sheridan.jobpill.MainActivity;
import com.sheridan.jobpill.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class JobPostingActivity extends AppCompatActivity {

    Button postBtn;
    Button cancelBtn;
    ImageButton jobImageBtn;
    EditText titleEdt;
    EditText paymentEdt;
    EditText locationEdt;
    EditText descriptionEdt;
    Spinner categorySpn;
    private Uri jobImageURI = null;
    EditText Instructions;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    String user_id;
    Map<String, Object> jobMap;
    DatabaseReference reference;
    private FirebaseFirestore db;
    CollectionReference dbJobs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_posting);
        cancelBtn = findViewById(R.id.cancel_post_btn);
        jobImageBtn = findViewById(R.id.job_imagebtn);
        titleEdt = findViewById(R.id.Job_title_editText);
        paymentEdt = findViewById(R.id.payment_editText);
        locationEdt = findViewById(R.id.Location_editText);
        descriptionEdt = findViewById(R.id.description_editText);
        categorySpn = findViewById(R.id.cat_spinner);
        postBtn = findViewById(R.id.pst_btn);
        Instructions = findViewById(R.id.instructions_edt);
        jobImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(JobPostingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(JobPostingActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(JobPostingActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        BringImagePicker();
                    }

                } else {
                    BringImagePicker();
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent jobIntent = new Intent(JobPostingActivity.this, MainActivity.class);
                startActivity(jobIntent);
                finish();
            }
        });
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseAuth = FirebaseAuth.getInstance();
                user_id = firebaseAuth.getCurrentUser().getUid();
                currentUser = firebaseAuth.getCurrentUser();

                String jobTitle = titleEdt.getText().toString();
                Long jobPayment = Long.parseLong(paymentEdt.getText().toString());
                String jobLocation = locationEdt.getText().toString();
                String jobDescription = descriptionEdt.getText().toString();
                String jobCategory = categorySpn.getSelectedItem().toString();
                String jobImage = jobImageURI.toString();
                String jobInstructions = Instructions.getText().toString();
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

                if (jobTitle.isEmpty() || jobPayment == null || jobLocation.isEmpty() || jobCategory.isEmpty() || jobDescription.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_LONG).show();
                }
                else {
                    dbJobs.add(jobMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getApplicationContext(), "Job posted successfully", Toast.LENGTH_LONG).show();
                            Intent jobIntent = new Intent(JobPostingActivity.this, MainActivity.class);
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
            }
        });
        addItemsOnSpinner();
    }

    public void Instructions_page(View view) {
        startActivity(new Intent(this, InstructionsActivity.class));
    }

    public void addItemsOnSpinner() {

        Spinner spinner2 = findViewById(R.id.cat_spinner);
        List<String> list = new ArrayList<>();
        list.add("Paint");
        list.add("DIY");
        list.add("Cleaning");
        list.add("Gardening");
        list.add("Babysitting");
        list.add("Grocery Pick-up");
        list.add("Other");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapter);
    }

    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(JobPostingActivity.this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                jobImageURI = result.getUri();
                jobImageBtn.setImageURI(jobImageURI);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
