package com.sheridan.jobpill.Job;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sheridan.jobpill.Profile.ProfileActivity;
import com.sheridan.jobpill.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class JobRatingActivity extends AppCompatActivity {

    private ImageView backBtn;
    private Button finishBtn;
    private CircleImageView profileImg;
    private RatingBar ratingBar;
    private TextView ratingTxt;
    private EditText reviewEdtTxt;
    private TextView title;

    String jobId;
    String revieweeId;
    String revieweesJobTitle;

    Map<String, Object> jobStatus = new HashMap<>();
    Map<String, Object> rating;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_rating);
        setupWidgets();

        //setup firebase attributes
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //setup job variables
        jobId = getIntent().getStringExtra("JobId");
        final String role = getIntent().getStringExtra("Role");

        //determine role of current user
        if(role.equals("JobSeeker")  || role.equals("JobPoster")){

            //retrieve data and populate the rating page with reviewee's data (name, profile img)
            populatePage(role);

            /*db.collection("jobs").document(jobId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        if(!task.getResult().exists()){
                            Log.d("Error", "Task does not exist");
                        }
                        else{
                            String userId = task.getResult().getString("createdByUID");
                            String jobTitle = task.getResult().getString("jobTitle");
                            String name = task.getResult().getString("createdByName");
                            String profileImgURL = task.getResult().getString("createdByPhotoURL");

                            revieweeId = userId;
                            revieweesJobTitle = jobTitle;

                            //set title
                            title.setText("Rate " + name);

                            //set profile image
                            RequestOptions placeholderRequest = new RequestOptions();
                            placeholderRequest.placeholder(R.drawable.profile_default);
                            Glide.with(JobRatingActivity.this).setDefaultRequestOptions(placeholderRequest).load(profileImgURL).into(profileImg);
                        }

                    }
                    else{
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(JobRatingActivity.this, "Firestore Retrieve Error " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            });*/

        }
        else{
            Log.d("Error", "Role is not defined");
            Toast.makeText(JobRatingActivity.this, "Error: Role is not defined", Toast.LENGTH_LONG).show();
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                if(rating <= 1){
                    ratingTxt.setText("Could be Better");
                }
                else if(rating <= 2){
                    ratingTxt.setText("So So");
                }
                else if(rating <= 3){
                    ratingTxt.setText("Good Job!");
                }
                else if(rating <= 4){
                    ratingTxt.setText("Fantastic!");
                }
                else{
                    ratingTxt.setText("Above and Beyond!");
                }
            }
        });

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //retrieve rating data from user
                final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                final float ratingScore = ratingBar.getRating();
                final String review = reviewEdtTxt.getText().toString();

                //save the rating
                setRating(date, ratingScore, review);
            }
        });

    }

    private void populatePage(final String role){

        db.collection("jobs").document(jobId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().exists()){
                        Log.d("Error", "Task does not exist");
                    }
                    else{
                        if(role.equals("JobSeeker")){
                            String userId = task.getResult().getString("createdByUID");
                            String jobTitle = task.getResult().getString("jobTitle");
                            String name = task.getResult().getString("createdByName");
                            String profileImgURL = task.getResult().getString("createdByPhotoURL");

                            revieweeId = userId;
                            revieweesJobTitle = jobTitle;

                            //set title
                            title.setText("Rate " + name);

                            //set profile image
                            RequestOptions placeholderRequest = new RequestOptions();
                            placeholderRequest.placeholder(R.drawable.profile_default);
                            Glide.with(JobRatingActivity.this).setDefaultRequestOptions(placeholderRequest).load(profileImgURL).into(profileImg);
                        }
                        else if(role.equals("JobPoster")){
                            String userId = task.getResult().getString("hiredApplicant");
                            String jobTitle = task.getResult().getString("jobTitle");

                            revieweeId = userId;
                            revieweesJobTitle = jobTitle;

                            db.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        if(!task.getResult().exists()){
                                            Log.d("Error", "Task does not exist");
                                        }
                                        else{
                                            String name = task.getResult().getString("name");
                                            String profileImgURL = task.getResult().getString("photoURL");

                                            //set title
                                            title.setText("Rate " + name);

                                            //set profile image
                                            RequestOptions placeholderRequest = new RequestOptions();
                                            placeholderRequest.placeholder(R.drawable.profile_default);
                                            Glide.with(JobRatingActivity.this).setDefaultRequestOptions(placeholderRequest).load(profileImgURL).into(profileImg);
                                        }
                                    }
                                }
                            });
                        }

                    }

                }
                else{
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(JobRatingActivity.this, "Firestore Retrieve Error " + errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setRating(final String date, final float ratingScore, final String review){

        db.collection("Users").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().exists()){
                        Log.d("Error", "Task does not exist");
                    }
                    else{
                        String name = task.getResult().getString("name");
                        String reviewerPhotoURL = task.getResult().getString("photoURL");

                        rating = new HashMap<>();
                        rating.put("jobTitle", revieweesJobTitle);
                        rating.put("postedDate", date);
                        rating.put("ratedBy", name);
                        rating.put("ratedByUID", currentUser.getUid());
                        rating.put("ratingScore", ratingScore);
                        rating.put("review", review);
                        rating.put("reviewerPhotoUrl", reviewerPhotoURL);

                        db.collection("Users").document(revieweeId).collection("ratings").document().set(rating).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    Toast.makeText(JobRatingActivity.this, "Rating Successful", Toast.LENGTH_LONG).show();

                                    markJobComplete();
                                    sendToMyJobs();
                                }
                                else{
                                    String error = task.getException().getMessage();
                                    Toast.makeText(JobRatingActivity.this, "Firestore error: " + error, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
                else{
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(JobRatingActivity.this, "Firestore Retrieve Error " + errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void markJobComplete(){

        //update job status as complete
        jobStatus.put("jobStatus", "complete");
        db.collection("jobs").document(jobId).update(jobStatus);
    }


    public void setupWidgets() {
        backBtn = findViewById(R.id.job_poster_rating_back_button);
        finishBtn = findViewById(R.id.btn_finish);
        profileImg = findViewById(R.id.user_img_profile);
        ratingBar = findViewById(R.id.rating_bar);
        ratingTxt = findViewById(R.id.rating_txt);
        reviewEdtTxt = findViewById(R.id.review_edt);
        title = findViewById(R.id.user_rating_title);
    }

    private void sendToMyJobs(){
        Intent intent = new Intent(JobRatingActivity.this, MyJobsActivity.class);
        startActivity(intent);
        finish();
    }


}