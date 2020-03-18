package com.sheridan.jobpill;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sheridan.jobpill.Models.Job;

public class JobDetailsActivity extends AppCompatActivity {

    private TextView txtJobTitle;
    private TextView txtJobLocation;
    private TextView txtJobEstimatedPay;
    private TextView txtJobDescription;

    private Button btn_apply;
    private Button btn_contact;

    private ImageView jobImage;

    private Job currentJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        txtJobTitle = findViewById(R.id.details_txtJobTitle);
        txtJobEstimatedPay = findViewById(R.id.details_txtJobEstimatedPay);
        txtJobDescription = findViewById(R.id.details_txtJobDescription);
        txtJobLocation = findViewById(R.id.details_txtJobLocation);
        jobImage = findViewById(R.id.img_jd);

        if(getIntent().hasExtra("JobSnapshot")){
             currentJob = getIntent().getParcelableExtra("JobSnapshot");

            Log.d("JOB_DETAILS","Job Details: " + currentJob.toString());
        }

        txtJobTitle.setText(currentJob.getJobTitle());
        txtJobEstimatedPay.setText("$"+String.valueOf(currentJob.getEstimatedPay()));
        txtJobLocation.setText(currentJob.getLocation());
        txtJobDescription.setText(currentJob.getJobDescription());

        RequestOptions placeholderRequest = new RequestOptions();
        placeholderRequest.placeholder(R.drawable.profile_default);

        Glide.with(JobDetailsActivity.this).setDefaultRequestOptions(placeholderRequest).load(currentJob.getPhotoURL()).into(jobImage);

    }
}
