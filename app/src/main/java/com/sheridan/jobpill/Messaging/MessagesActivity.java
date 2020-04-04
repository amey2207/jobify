package com.sheridan.jobpill.Messaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sheridan.jobpill.Job.MyJobsActivity;
import com.sheridan.jobpill.MainActivity;
import com.sheridan.jobpill.Profile.ProfileActivity;
import com.sheridan.jobpill.R;

public class MessagesActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);


        toolbar = findViewById(R.id.top_toolbar_messages);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        setupWidgets();

        bottomNavigationView.setSelectedItemId(R.id.bottom_action_messages);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.bottom_action_jobs:
                        sendToMyJobs();
                        return true;
                    case R.id.bottom_action_home:
                        sendToMain();
                        return true;
                    case R.id.bottom_action_account:
                        sendToProfile();
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    private void sendToProfile() {
        Intent intent = new Intent(MessagesActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupWidgets() {
        toolbar = findViewById(R.id.top_toolbar_messages);
        bottomNavigationView = findViewById(R.id.messages_bottom_nav);

    }

    private void sendToMain() {
        Intent intent = new Intent(MessagesActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void sendToMyJobs() {
        Intent intent = new Intent(MessagesActivity.this, MyJobsActivity.class);
        startActivity(intent);
        finish();
    }
}
