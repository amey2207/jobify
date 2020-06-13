package com.sheridan.jobpill.Job;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sheridan.jobpill.MainActivity;
import com.sheridan.jobpill.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class JobPostingEditActivity extends AppCompatActivity {

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

    private StorageReference image_path = null;
    private StorageReference storageReference;
    private String jobId;

    CollectionReference dbJobs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_posting_edit);
        cancelBtn = findViewById(R.id.cancel_post_btn);
        jobImageBtn = findViewById(R.id.job_imagebtn);
        titleEdt = findViewById(R.id.Job_title_editText);
        paymentEdt = findViewById(R.id.payment_editText);
        locationEdt = findViewById(R.id.Location_editText);
        descriptionEdt = findViewById(R.id.description_editText);
        categorySpn = findViewById(R.id.cat_spinner);
        postBtn = findViewById(R.id.pst_btn);
        Instructions = findViewById(R.id.instructions_edt);
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        jobImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(JobPostingEditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(JobPostingEditActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(JobPostingEditActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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
                Intent jobIntent = new Intent(JobPostingEditActivity.this, MainActivity.class);
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
                if (TextUtils.isEmpty(titleEdt.getText()) || TextUtils.isEmpty(paymentEdt.getText()) || TextUtils.isEmpty(locationEdt.getText()) || categorySpn.getSelectedItemPosition() < 1
                        || TextUtils.isEmpty(descriptionEdt.getText()) || jobImageURI == null) {
                    Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_LONG).show();
                } else {

                    final String jobTitle = titleEdt.getText().toString();
                    final Float jobPayment = Float.parseFloat(paymentEdt.getText().toString());
                    final String jobLocation = locationEdt.getText().toString();
                    final String jobDescription = descriptionEdt.getText().toString();
                    final String jobCategory = categorySpn.getSelectedItem().toString();
                    final String jobInstructions = Instructions.getText().toString();
                    reference = FirebaseDatabase.getInstance().getReference().child("jobs").child(user_id);
                    final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    final DocumentReference newJobRef = db.collection("jobs").document();
                    jobId = newJobRef.getId();
                    image_path = storageReference.child("job_images").child(jobId + ".jpg");
                    image_path.putFile(jobImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                storeFirestore(task, newJobRef, currentUser.getEmail(), date, jobPayment, jobInstructions, jobCategory, jobDescription, jobTitle, jobLocation);
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(JobPostingEditActivity.this, "Image upload error: " + error, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, final DocumentReference newJobRef, final String createdBy, final String createdDate, final Float jobPayment,
                                final String instructions, final String jobCategory, final String jobDescription, final String jobTitle,
                                final String jobLocation) {

        if (task != null) {

            image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Uri downloadUri = uri;
                    jobMap = new HashMap<>();
                    jobMap.put("createdBy", createdBy);
                    jobMap.put("createdDate", createdDate);
                    jobMap.put("estimatedPay", jobPayment);
                    jobMap.put("photoURL", downloadUri.toString());
                    jobMap.put("instructions", instructions);
                    jobMap.put("jobCategory", jobCategory);
                    jobMap.put("jobDescription", jobDescription);
                    jobMap.put("jobStatus", "available");
                    jobMap.put("jobTitle", jobTitle);
                    jobMap.put("location", jobLocation);

                    newJobRef.set(jobMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(JobPostingEditActivity.this, "Job Posted Successfully", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(JobPostingEditActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(JobPostingEditActivity.this, "FireStore error: " + error, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });
        }
    }

    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(JobPostingEditActivity.this);
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