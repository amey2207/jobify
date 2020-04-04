package com.sheridan.jobpill.Job;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_jobs);

        setupWidgets();

        cardJobsPosted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToMyPostedJobs();
            }
        });

        cardJobsApplied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        cardJobsInProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        cardJobsCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

    private void sendToMyPostedJobs(){
        Intent intent = new Intent(MyJobsActivity.this, MyPostedJobsActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToMessages() {
        Intent intent = new Intent(MyJobsActivity.this, MessagesActivity.class);
        startActivity(intent);
        finish();
    }
}
