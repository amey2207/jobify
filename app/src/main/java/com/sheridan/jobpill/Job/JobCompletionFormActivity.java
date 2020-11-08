package com.sheridan.jobpill.Job;

import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
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

public class JobCompletionFormActivity extends AppCompatActivity {

    Button completionBtn;
    Button cancelBtn;
    ImageButton jobImageBtn;
    EditText notesEdt;
    RadioGroup radio;
    private Uri jobImageURI = null;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    String user_id;
    Map<String, Object> jobMap;
    DatabaseReference reference;
    private FirebaseFirestore db;
    private StorageReference image_path = null;
    private StorageReference storageReference;
    private String jobCompletionId;
    private String createdByName = "";
    private ImageView backButton;

    private String test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_completion_form);
        backButton = findViewById(R.id.jobsinprogress_back_button);
        completionBtn = findViewById(R.id.pst_btn);
        cancelBtn = findViewById(R.id.cancel_post_btn);
        jobImageBtn = findViewById(R.id.job_imagebtn);
        notesEdt = findViewById(R.id.instructions_edt);
        radio = findViewById(R.id.radioGroup2);
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        // retrieve job image and job notes
        if(savedInstanceState != null){

            // retrieve saved job image and job notes from the saved state
            jobImageURI = savedInstanceState.getParcelable("jobImageURI");
            String jobNotes = savedInstanceState.getString("jobNotes");

            //check if there is a saved jobImage
            if(jobImageURI != null){
                jobImageBtn.setImageURI(jobImageURI);
            }

            //check if there are saved job notes
            if(jobNotes != null){
                notesEdt.setText(jobNotes);
            }
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToHome();
            }
        });

        jobImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(JobCompletionFormActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(JobCompletionFormActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(JobCompletionFormActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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
                Intent jobIntent = new Intent(JobCompletionFormActivity.this, MainActivity.class);
                startActivity(jobIntent);
                finish();
            }
        });

        completionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseAuth = FirebaseAuth.getInstance();
                user_id = firebaseAuth.getCurrentUser().getUid();
                currentUser = firebaseAuth.getCurrentUser();

                //check if user has added a job image and chosen a option for the payment
                if (radio.getCheckedRadioButtonId() == 0 && jobImageURI == null) {
                    Toast.makeText(getApplicationContext(), "Please fill the payment field and add an image of your work", Toast.LENGTH_LONG).show();
                }
                else if(jobImageURI == null){
                    Toast.makeText(getApplicationContext(), "Please add an image of your work", Toast.LENGTH_LONG).show();
                }
                else if(radio.getCheckedRadioButtonId() == 0){
                    Toast.makeText(getApplicationContext(), "Please fill the payment field", Toast.LENGTH_LONG).show();
                }
                else {
                    final String jobNotes = notesEdt.getText().toString();
                    final int paymentChoice = radio.getCheckedRadioButtonId();
                    final RadioButton radiochoice = findViewById(paymentChoice);

                    final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                    //check if job completion document already exists
                    if(jobCompletionId != null){

                        //retrieve existing job application document for update
                        final DocumentReference newJobRef = db.collection("JobCompletion").document(jobCompletionId);
                        storeFirestorage(newJobRef, date, jobNotes, radiochoice);
                    }
                    else{

                        //create new job application document
                        final DocumentReference newJobRef = db.collection("JobCompletion").document();
                        jobCompletionId = newJobRef.getId();

                        storeFirestorage(newJobRef, date, jobNotes, radiochoice);
                    }
                }

            }
        });
    }

    private void storeFirestorage(final DocumentReference newJobRef, final String date, final String jobNotes, final RadioButton radiochoice){

        image_path = storageReference.child("job_images").child(jobCompletionId + ".jpg");
        image_path.putFile(jobImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {

                    storeFirestore(task, newJobRef, currentUser.getUid(), date, jobNotes, radiochoice.getText().toString());

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(JobCompletionFormActivity.this, "Image upload error: " + error, Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, final DocumentReference newJobRef, final String createdByUID, final String createdDate,
                                final String notes, final String paymentChoice) {

        if (task != null) {

            image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Uri downloadUri = uri;
                    jobMap = new HashMap<>();
                    jobMap.put("Date", createdDate);
                    jobMap.put("createdByUID", createdByUID);
                    jobMap.put("Notes", notes);
                    jobMap.put("photoURL", downloadUri.toString());
                    jobMap.put("Payment", paymentChoice);
                    jobMap.put("jobId", getIntent().getStringExtra("JobId"));
                    jobMap.put("posterID", getIntent().getStringExtra("posterID"));
                    jobMap.put("employeeID", getIntent().getStringExtra("employeeID"));

                    newJobRef.set(jobMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(JobCompletionFormActivity.this, "Job Updated Successfully", Toast.LENGTH_LONG).show();

                                      //  .set(jobStatus, SetOptions.merge());
                                sendToPosterRating();
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(JobCompletionFormActivity.this, "Firestore error: " + error, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });
        } else {
        }
    }

    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(JobCompletionFormActivity.this);
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        //save job image and job notes for retrieval when navigating back to activity
        outState.putParcelable("jobImageURI", jobImageURI);
        outState.putString("jobNotes", notesEdt.getText().toString());
    }

    private void sendToHome() {
        Intent intent = new Intent(JobCompletionFormActivity.this, MyJobsActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToPosterRating(){
        Intent intent = new Intent(JobCompletionFormActivity.this, JobRatingActivity.class);
        intent.putExtra("Role", getIntent().getStringExtra("Role"));
        intent.putExtra("JobId", getIntent().getStringExtra("JobId"));
        startActivity(intent);
    }

}
