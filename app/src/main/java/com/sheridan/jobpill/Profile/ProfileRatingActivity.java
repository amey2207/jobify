package com.sheridan.jobpill.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
import com.google.android.material.appbar.CollapsingToolbarLayout;
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

    private RecyclerView reviewsListView;
    private CollapsingToolbarLayout collapsingToolbar;


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

        //setup click listener for back button
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //get the data passed from the profile activity
        String ratingScore = getIntent().getStringExtra("RATING_SCORE");
        String numRatings = getIntent().getStringExtra("NUMBER_OF_RATINGS");
        String name = getIntent().getStringExtra("NAME");

        //get the data passed from the job applicant profile
        String applicantId = getIntent().getStringExtra("APPLICANT_ID");

        //check if there are any ratings and populate layout accordingly
        if(Float.parseFloat(ratingScore) == 0 && numRatings.equals("0")){

            rating_bar.setRating(Float.parseFloat(ratingScore));
            num_rating_lbl.setText("No Ratings");
        }
        else{
            rating_bar.setRating(Float.parseFloat(ratingScore));
            rating_score.setText(ratingScore);
            num_rating_lbl.setText("Based on " + numRatings + " ratings");
        }

        //set typography of toolbar title when expanded and collapsed
        collapsingToolbar.setTitle(name + "'s Ratings" + " (" + numRatings + ")");
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);


        //create firestore query
        Query query;

        //build query based on which activity preceded this one (ProfileActivity or JobApplicantProfile)
        if(applicantId != null){

            //create query to retrieve all the ratings for the applicant
            query = firebaseFirestore
                    .collection("Users")
                    .document(applicantId)
                    .collection("ratings")
                    .orderBy("postedDate", Query.Direction.DESCENDING);
        }
        else{

            //create query to retrieve all the ratings for the current user
            query = firebaseFirestore
                    .collection("Users")
                    .document(currentUser.getUid())
                    .collection("ratings")
                    .orderBy("postedDate", Query.Direction.DESCENDING);
        }

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
        reviewsListView = findViewById(R.id.reviews_list);

        collapsingToolbar = findViewById(R.id.collapsing_toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

}