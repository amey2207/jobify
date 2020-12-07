package com.sheridan.jobpill.Job;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sheridan.jobpill.ML.ImageModerate;
import com.sheridan.jobpill.MainActivity;
import com.sheridan.jobpill.Models.Job;
import com.sheridan.jobpill.Profile.EditProfileActivity;
import com.sheridan.jobpill.Profile.ProfileActivity;
import com.sheridan.jobpill.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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

    private StorageReference image_path = null;
    private StorageReference storageReference;
    private String jobId;
    private String createdByName = "";
    private String createdByPhotoURL = "";

    //image moderation variables
    ImageModerate imageModerate;
    private Bitmap imageBitmap;

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
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


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



                if (TextUtils.isEmpty(titleEdt.getText()) || TextUtils.isEmpty(paymentEdt.getText()) || TextUtils.isEmpty(locationEdt.getText()) || categorySpn.getSelectedItemPosition() < 1
                        || TextUtils.isEmpty(descriptionEdt.getText()) || jobImageURI == null) {
                    Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_LONG).show();
                }
                else {

                    if(imageModerate.isImageClean()) {
                        final String jobTitle = titleEdt.getText().toString();
                        final Float jobPayment = Float.parseFloat(paymentEdt.getText().toString());
                        final String jobLocation = locationEdt.getText().toString();
                        final String jobDescription = descriptionEdt.getText().toString();
                        final String jobCategory = categorySpn.getSelectedItem().toString();
                        String jobImage = jobImageURI.toString();
                        final String jobInstructions = Instructions.getText().toString();
                        reference = FirebaseDatabase.getInstance().getReference().child("jobs").child(user_id);
                        final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                        db.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    createdByName = task.getResult().getString("name");
                                    createdByPhotoURL = task.getResult().getString("photoURL");
                                }else{
                                    createdByName = currentUser.getEmail();
                                }
                            }
                        });


                        final DocumentReference newJobRef = db.collection("jobs").document();
                        jobId = newJobRef.getId();
                        image_path = storageReference.child("job_images").child(jobId + ".jpg");

                        image_path.putFile(jobImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){

                                    storeFirestore(task,newJobRef,currentUser.getEmail(),currentUser.getUid(),date,jobPayment,jobInstructions,jobCategory,jobDescription,jobTitle,jobLocation);

                                }else{
                                    String error = task.getException().getMessage();
                                    Toast.makeText(JobPostingActivity.this, "Image upload error: " + error, Toast.LENGTH_LONG).show();

                                }
                            }
                        });
                    }
                    else{
                        Toast.makeText(JobPostingActivity.this, "Inappropriate content detected in image, please select another image.", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
    }

    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, final DocumentReference newJobRef, final String createdBy, final String createdByUID , final String createdDate, final Float jobPayment,
                                final String instructions, final String jobCategory, final String jobDescription, final String jobTitle,
                                final String jobLocation){

        if(task != null){

            image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Uri downloadUri = uri;
                    jobMap = new HashMap<>();
                    jobMap.put("createdBy", createdBy);
                    jobMap.put("createdByName",createdByName);
                    jobMap.put("createdByPhotoURL", createdByPhotoURL);
                    jobMap.put("createdByUID",createdByUID);
                    jobMap.put("createdDate", createdDate);
                    jobMap.put("estimatedPay", jobPayment);
                    jobMap.put("photoURL", downloadUri.toString());
                    jobMap.put("instructions", instructions);
                    jobMap.put("jobCategory", jobCategory);
                    jobMap.put("jobDescription", jobDescription);
                    jobMap.put("jobStatus", "available");
                    jobMap.put("jobTitle", jobTitle);
                    jobMap.put("location", jobLocation);
                    jobMap.put("hiringDate","");
                    jobMap.put("hiredApplicant","");

                    newJobRef.set(jobMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(JobPostingActivity.this, "Job Posted Successfully", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(JobPostingActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                String error = task.getException().getMessage();

                                Toast.makeText(JobPostingActivity.this, "Firestore error: " + error, Toast.LENGTH_LONG).show();

                            }
                        }
                    });

                }
            });

        }else{

        }

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

                //get bitmap of image
                try {
                    InputStream inputStream = getContentResolver().openInputStream(jobImageURI);
                    imageBitmap = BitmapFactory.decodeStream(inputStream);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                //moderate image content with Vision API
                imageModerate = new ImageModerate(JobPostingActivity.this, imageBitmap);
                imageModerate.callVisionAPI();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}
