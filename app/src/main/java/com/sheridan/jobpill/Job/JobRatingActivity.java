package com.sheridan.jobpill.Job;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sheridan.jobpill.R;

import java.util.HashMap;
import java.util.Map;

public class JobRatingActivity extends AppCompatActivity {

    private ImageView backBtn;
    private Button finishBtn;
    private CircleImageView profileImg;
    private RatingBar ratingBar;
    private TextView ratingTxt;
    private EditText reviewEdtTxt;
    private TextView title;

    Map<String, Object> jobStatus = new HashMap<>();

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_rating);
        setupWidgets();

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

                sendToMyJobs();
            }
        });

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