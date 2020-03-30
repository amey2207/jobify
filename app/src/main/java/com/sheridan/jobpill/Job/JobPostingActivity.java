package com.sheridan.jobpill.Job;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

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

import com.sheridan.jobpill.MainActivity;
import com.sheridan.jobpill.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

public class JobPostingActivity extends AppCompatActivity {

    Button nextBtn;
    Button cancelBtn;
    ImageButton jobImageBtn;
    EditText titleEdt;
    EditText paymentEdt;
    EditText locationEdt;
    EditText descriptionEdt;
    Spinner categorySpn;


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
        nextBtn = findViewById(R.id.Next_btn);
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
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String jobTitle = titleEdt.getText().toString();
                String jobPayment = paymentEdt.getText().toString();
                String jobLocation = locationEdt.getText().toString();
                String jobDescription = descriptionEdt.getText().toString();
                String jobCategory = categorySpn.getSelectedItem().toString();
                if (jobTitle.isEmpty() || jobPayment.isEmpty() || jobLocation.isEmpty() || jobCategory.isEmpty() || jobDescription.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent jobIntent = new Intent(JobPostingActivity.this, InstructionsActivity.class);
                    jobIntent.putExtra("Job_Title", jobTitle);
                    jobIntent.putExtra("Job_Payment", jobPayment);
                    jobIntent.putExtra("Job_Location", jobLocation);
                    jobIntent.putExtra("Job_Description", jobDescription);
                    jobIntent.putExtra("Job_Category", jobCategory);
                    startActivity(jobIntent);
                    finish();
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
}
