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
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sheridan.jobpill.MainActivity;
import com.sheridan.jobpill.Messaging.MessagesActivity;
import com.sheridan.jobpill.Profile.ProfileActivity;
import com.sheridan.jobpill.R;

public class MyJobsActivity extends AppCompatActivity {

    private CardView cardJobsPosted;
    private CardView cardJobsApplied;
    private CardView cardJobsInProgress;
    private CardView cardJobsCompleted;
    private BottomNavigationView bottomNavigationView;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_jobs);
        setupWidgets();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToHome();
            }
        });
        cardJobsPosted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToMyPostedJobs();
            }
        });

        cardJobsApplied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToMyAppliedJobs();
            }
        });

        cardJobsInProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToJobsInProgress();
            }
        });

        cardJobsCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendtoJobsCompleted();
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
        cardJobsPosted = findViewById(R.id.card_posted_jobs);
        cardJobsApplied = findViewById(R.id.card_jobs_applied);
        cardJobsInProgress = findViewById(R.id.card_jobs_inprogress);
        cardJobsCompleted = findViewById(R.id.card_jobs_completed);
        bottomNavigationView = findViewById(R.id.myjobs_bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.bottom_action_jobs);
        backButton = findViewById(R.id.My_Jobs_back_button);
    }

    private void sendToProfile() {
        Intent intent = new Intent(MyJobsActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToHome() {
        Intent intent = new Intent(MyJobsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToMyPostedJobs() {
        Intent intent = new Intent(MyJobsActivity.this, MyPostedJobsActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToMyAppliedJobs() {
        Intent intent = new Intent(MyJobsActivity.this, MyAppliedJobsActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToMessages() {
        Intent intent = new Intent(MyJobsActivity.this, MessagesActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToJobsInProgress() {
        Intent intent = new Intent(MyJobsActivity.this, JobsInProgressOptionActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendtoJobsCompleted() {
        Intent intent = new Intent(MyJobsActivity.this, JobCompletedActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE){
            bottomNavigationView.setVisibility(View.GONE);
        }
        else {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }
}