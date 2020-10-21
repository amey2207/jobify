package com.sheridan.jobpill.Job;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sheridan.jobpill.MainActivity;
import com.sheridan.jobpill.Messaging.MessagesActivity;
import com.sheridan.jobpill.Profile.ProfileActivity;
import com.sheridan.jobpill.R;

public class JobsCompletedOptionActivity extends AppCompatActivity {
    private CardView cardInProgressApplied;
    private CardView cardInProgressPosted;
    private BottomNavigationView bottomNavigationView;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_completed_option);
        setupWidgets();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToMyJobs();
            }
        });

        cardInProgressPosted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToMyCompletedJobs();
            }
        });

        cardInProgressApplied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToAppliedJobsCompleted();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.bottom_action_messages:
                        sendToMessages();
                        return true;
                    case R.id.bottom_action_account:
                        sendToProfile();
                        return true;
                    case R.id.bottom_action_home:
                        sendToHome();
                    default:
                        return false;
                }
            }
        });
    }

    public void setupWidgets() {
        cardInProgressPosted = findViewById(R.id.card_in_progress_jobs_posted);
        cardInProgressApplied = findViewById(R.id.card_in_progress_jobs_applied);
        bottomNavigationView = findViewById(R.id.myjobs_bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.bottom_action_jobs);
        backButton = findViewById(R.id.jobs_in_progress_option_back_button);
    }

    private void sendToProfile() {
        Intent intent = new Intent(JobsCompletedOptionActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToHome() {
        Intent intent = new Intent(JobsCompletedOptionActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToMyCompletedJobs() {
        Intent intent = new Intent(JobsCompletedOptionActivity.this, JobCompletedActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToMyJobs() {
        Intent intent = new Intent(JobsCompletedOptionActivity.this, MyJobsActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToMessages() {
        Intent intent = new Intent(JobsCompletedOptionActivity.this, MessagesActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToAppliedJobsCompleted() {
        Intent intent = new Intent(JobsCompletedOptionActivity.this, AppliedJobsCompletedActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            bottomNavigationView.setVisibility(View.GONE);
        } else {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }
}
