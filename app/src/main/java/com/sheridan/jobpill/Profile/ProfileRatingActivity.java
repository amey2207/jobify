package com.sheridan.jobpill.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sheridan.jobpill.Job.MyJobsActivity;
import com.sheridan.jobpill.JobApplication.JobApplicantProfile;
import com.sheridan.jobpill.MainActivity;
import com.sheridan.jobpill.Messaging.MessagesActivity;
import com.sheridan.jobpill.R;

public class ProfileRatingActivity extends AppCompatActivity {

    ImageView back_btn;
    RatingBar rating_bar;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_rating);

        setupWidgets();

        //highlight profile button of bottom navigation
        bottomNavigationView.setSelectedItemId(R.id.bottom_action_account);

        //setup click listener for back button
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToAccount();
            }
        });

        rating_bar.setRating(0.0f);


        //setup click listener for bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.bottom_action_jobs:
                        sendToMyJobs();
                        return true;
                    case R.id.bottom_action_messages:
                        sendToMessages();
                        return true;
                    case R.id.bottom_action_home:
                        sendToMain();
                        return true;
                    case R.id.bottom_action_account:
                        sendToAccount();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }


    public void setupWidgets(){
        back_btn = findViewById(R.id.profile_rating_back_button);
        rating_bar = findViewById(R.id.profile_rating_bar);
        bottomNavigationView = findViewById(R.id.profileRatingBottomNav);
    }

    private void sendToMain() {
        Intent intent = new Intent(ProfileRatingActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToMyJobs() {
        Intent intent = new Intent(ProfileRatingActivity.this, MyJobsActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToMessages() {
        Intent intent = new Intent(ProfileRatingActivity.this, MessagesActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToAccount() {
        Intent intent = new Intent(ProfileRatingActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }
}