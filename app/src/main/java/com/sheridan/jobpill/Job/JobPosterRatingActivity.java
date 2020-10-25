package com.sheridan.jobpill.Job;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sheridan.jobpill.MainActivity;
import com.sheridan.jobpill.Messaging.MessagesActivity;
import com.sheridan.jobpill.Profile.ProfileActivity;
import com.sheridan.jobpill.Profile.ProfileRatingActivity;
import com.sheridan.jobpill.R;

public class JobPosterRatingActivity extends AppCompatActivity {

    private ImageView backBtn;
    private Button finishBtn;
    private CircleImageView profileImg;
    private RatingBar ratingBar;
    private TextView ratingTxt;
    private EditText reviewEdtTxt;
    private TextView title;

    private FirebaseFirestore fb;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_poster_rating);
        setupWidgets();


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(getIntent().getStringExtra("Role") != null)
                sendToMain();
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

    private void sendToMain() {
        Intent intent = new Intent(JobPosterRatingActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}