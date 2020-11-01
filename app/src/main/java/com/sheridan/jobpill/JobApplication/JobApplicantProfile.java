package com.sheridan.jobpill.JobApplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.sheridan.jobpill.Auth.LoginActivity;
import com.sheridan.jobpill.Job.JobDetailsActivity;
import com.sheridan.jobpill.Job.MyJobsActivity;
import com.sheridan.jobpill.MainActivity;
import com.sheridan.jobpill.Messaging.MessagesActivity;
import com.sheridan.jobpill.Models.JobApplication;
import com.sheridan.jobpill.Profile.EditProfileActivity;
import com.sheridan.jobpill.Profile.ProfileActivity;
import com.sheridan.jobpill.Profile.ProfileRatingActivity;
import com.sheridan.jobpill.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class JobApplicantProfile extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

   private CircleImageView applicantProfileImg;
   private TextView txtApplicantName;
   private TextView txtApplicantCity;
   private TextView txtApplicantIntro;
   private TextView txtApplicantRating;
   private View ratingView;
   private TextView txtApplicantJobsCompleted;

   private Button btnHireApplicant;

    String[] listInterests;
    double ratingScore;
    int numratings;

    private ImageView backBtn;

   private ChipGroup interestChipGroup;

    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;

    private JobApplication currentJobApplication;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_applicant_profile);

        toolbar = findViewById(R.id.top_toolbar_jobapplicant_profile);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        setupWidgets();

        listInterests = getResources().getStringArray(R.array.interest_categories);





        if(getIntent().hasExtra("jobApplicant")){
            currentJobApplication = getIntent().getParcelableExtra("jobApplicant");
            Log.d("JOB_APPLICATION_DETAILS", "Job Application Details: " + currentJobApplication.toString());


            firebaseFirestore.collection("Users").document(currentJobApplication.getApplicantId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        if(!task.getResult().exists()){
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(JobApplicantProfile.this, "Applicant Retrieve Error " + errorMessage, Toast.LENGTH_LONG).show();
                            finish();
                        }else{
                            String name = task.getResult().getString("name");
                            String intro = task.getResult().getString("intro");
                            String phone = task.getResult().getString("phone");
                            String city = task.getResult().getString("city");
                            String image = task.getResult().getString("photoURL");
                            ArrayList<Integer> interests = (ArrayList<Integer>)task.getResult().get("interests");

                            String interestList = "";
                            ArrayList<String> savedInterests = new ArrayList<>();
                            if(!interests.isEmpty()){
                                for(int i = 0; i < interests.size();i++){
                                    interestList = interestList + listInterests[Integer.parseInt(String.valueOf(interests.get(i)))];

                                    if(i != interests.size()-1){
                                        interestList = interestList + ", ";
                                    }

                                    savedInterests.add(listInterests[Integer.parseInt(String.valueOf(interests.get(i)))]);
                                    Log.d("SAVED_INTERESTS", "Interests: " + savedInterests.toString());

                                }

                                setInterestChips(savedInterests);
                            }else{
                                interestList = "No Interests Selected!";
                            }

                            txtApplicantName.setText(name);
                            txtApplicantCity.setText(city);
                            txtApplicantIntro.setText(intro);


                            RequestOptions placeholderRequest = new RequestOptions();
                            placeholderRequest.placeholder(R.drawable.profile_default);

                            Glide.with(JobApplicantProfile.this).setDefaultRequestOptions(placeholderRequest).load(image).into(applicantProfileImg);

                            //get number of rating  for current user to calculate current rating
                            firebaseFirestore.collection("Users").document(currentJobApplication.getApplicantId()).collection("ratings").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        double ratingTotal = 0;
                                        double ratingAverage;

                                        numratings = task.getResult().size();

                                        //get rating value for every entry for average calculation
                                        for(QueryDocumentSnapshot doc : task.getResult()){
                                            double rating = doc.getDouble("ratingScore");
                                            ratingTotal += rating;
                                        }

                                        //calculate average rating of user and set the rating score view
                                        ratingAverage = ratingTotal / numratings;
                                        ratingScore = (ratingAverage <= 5 && ratingAverage >= 0) ? ratingAverage : 0;

                                        txtApplicantRating.setText(Double.toString(ratingScore));
                                    }
                                }
                            });



                        }
                    }else{
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(JobApplicantProfile.this, "Firestore Retrieve Error " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            });

        }

        firebaseFirestore.collection("jobs").document(currentJobApplication.getJobId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String hiredApplicant = task.getResult().getString("hiredApplicant");
                    if(!hiredApplicant.isEmpty()){
                        btnHireApplicant.setEnabled(false);
                    }
                }
            }
        });

        ratingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToProfileRating(ratingScore, numratings);
            }
        });

        btnHireApplicant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());


                Map<String, Object> data = new HashMap<>();
                data.put("hiredApplicant", currentJobApplication.getApplicantId());
                data.put("jobStatus","In-Progress");
                data.put("hiringDate",date);



                firebaseFirestore.collection("jobs").document(currentJobApplication.getJobId()).set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            final Map<String,Object> jobApplication = new HashMap<>();
                            jobApplication.put("status","assigned");

                            firebaseFirestore.collection("jobs").document(currentJobApplication.getJobId()).collection("jobApplications")
                                    .document(currentJobApplication.getItemId())
                                    .set(jobApplication,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });

                            Toast.makeText(JobApplicantProfile.this, "Applicant Hired Successfully ", Toast.LENGTH_LONG).show();
                            finish();
                        }else{
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(JobApplicantProfile.this, "Firestore Update Error " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });


            }
        });



        }


     public void setInterestChips(ArrayList<String> interests)   {

        for(String interest:interests){
            Chip mChip = (Chip) this.getLayoutInflater().inflate(R.layout.item_chip_interest,null,false );
            mChip.setText(interest);
            int paddingDp = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,10,getResources().getDisplayMetrics()
            );

            mChip.setPadding(paddingDp,0,paddingDp,0);
            mChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                }
            });
            interestChipGroup.addView(mChip);
        }


     }



    private void setupWidgets() {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        applicantProfileImg = findViewById(R.id.applicant_profile_img);
        txtApplicantName = findViewById(R.id.txt_applicant_name);
        txtApplicantCity = findViewById(R.id.txt_applicant_city);
        txtApplicantIntro = findViewById(R.id.txt_applicant_intro);
        interestChipGroup = findViewById(R.id.interest_chipGroup);

        ratingView = findViewById(R.id.view_applicant_rating);
        txtApplicantRating = findViewById(R.id.txt_applicant_rating);

        backBtn = findViewById(R.id.applicant_profile_back_btn);
        btnHireApplicant = findViewById(R.id.btn_hireApplicant);

    }

    @Override
    protected void onStart() {
        super.onStart();


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void sendToProfileRating(double ratingScore, int numratings){
        Intent intent = new Intent(JobApplicantProfile.this, ProfileRatingActivity.class);

        intent.putExtra("RATING_SCORE", String.valueOf(ratingScore));
        intent.putExtra("NUMBER_OF_RATINGS", String.valueOf(numratings));
        intent.putExtra("APPLICANT_ID", currentJobApplication.getApplicantId());

        startActivity(intent);
    }

    private void sendToLogin() {
        Intent intent = new Intent(JobApplicantProfile.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToMain() {
        Intent intent = new Intent(JobApplicantProfile.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void sendToMyJobs() {
        Intent intent = new Intent(JobApplicantProfile.this, MyJobsActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToMessages() {
        Intent intent = new Intent(JobApplicantProfile.this, MessagesActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToAccount() {
        Intent intent = new Intent(JobApplicantProfile.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }
}