package com.sheridan.jobpill.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.sheridan.jobpill.Job.MyJobsActivity;
import com.sheridan.jobpill.JobApplication.JobApplicantProfile;
import com.sheridan.jobpill.MainActivity;
import com.sheridan.jobpill.Messaging.MessagesActivity;
import com.sheridan.jobpill.Models.Rating;
import com.sheridan.jobpill.R;

public class ProfileRatingActivity extends AppCompatActivity {

    private ImageView back_btn;
    private RatingBar rating_bar;
    private TextView rating_score;
    private TextView num_rating_lbl;
    private TextView number_of_ratings;
    private RecyclerView reviewsListView;
    private BottomNavigationView bottomNavigationView;
    private RatingsListFirestoreAdapter adapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_rating);

        setupWidgets();

        //get current logged in user
        currentUser = firebaseAuth.getCurrentUser();

        //highlight profile button of bottom navigation
        bottomNavigationView.setSelectedItemId(R.id.bottom_action_account);

        //setup click listener for back button
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToAccount();
            }
        });

        //get the data passed from the profile activity
        String ratingScore = getIntent().getStringExtra("RATING_SCORE");
        String numRatings = getIntent().getStringExtra("NUMBER_OF_RATINGS");

        Log.d("RATINGSCORE", ratingScore);
        Log.d("NUMBEROFRATINGS", numRatings);

        //pupulate layout
        rating_bar.setRating(Float.parseFloat(ratingScore));
        rating_score.setText(ratingScore);
        num_rating_lbl.setText("Based on " + numRatings + " ratings");
        number_of_ratings.setText("(" + numRatings + ")");

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

        //create firestore query to retrieve all the ratings for the current user
        Query query = firebaseFirestore
                .collection("Users")
                .document(currentUser.getUid())
                .collection("ratings")
                .orderBy("postedDate", Query.Direction.DESCENDING);


        //configure page settings
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(5)
                .build();

        //set the firestore paging options with query and page configurations
        FirestorePagingOptions options = new FirestorePagingOptions.Builder<Rating>()
                .setLifecycleOwner(this)
                .setQuery(query, config, Rating.class)
                .build();

        //set the firestore adapter and assign it to the recyclerview
        adapter = new RatingsListFirestoreAdapter(options, this);

        //set fixed size and the layout manager for the recyclerview to display items as a linear vertical list
        reviewsListView.setHasFixedSize(true);
        reviewsListView.setLayoutManager(new LinearLayoutManager(this));
        reviewsListView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void setupWidgets(){
        back_btn = findViewById(R.id.profile_rating_back_button);
        rating_bar = findViewById(R.id.profile_rating_bar);
        rating_score = findViewById(R.id.rating_score);
        num_rating_lbl = findViewById(R.id.num_ratings_lbl);
        number_of_ratings = findViewById(R.id.review_title_number_ratings);
        reviewsListView = findViewById(R.id.reviews_list);
        bottomNavigationView = findViewById(R.id.profileRatingBottomNav);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
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