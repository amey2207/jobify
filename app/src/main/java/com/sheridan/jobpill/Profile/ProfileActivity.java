package com.sheridan.jobpill.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sheridan.jobpill.Auth.LoginActivity;
import com.sheridan.jobpill.Job.MyJobsActivity;
import com.sheridan.jobpill.MainActivity;
import com.sheridan.jobpill.Messaging.MessagesActivity;
import com.sheridan.jobpill.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    TextView txtProfileName;
    TextView txtProfileIntro;
    TextView txtProfilePhone;
    TextView txtProfileCity;
    TextView txtNumRating;
    CircleImageView imgProfile;
    View ratingsView;

    private ChipGroup interestChipGroup;

    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;

    String current_user_id;
    String[] listInterests;
    double ratingScore;
    int numratings;

    private Uri profileImageURI = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        listInterests = getResources().getStringArray(R.array.interest_categories);
        toolbar = (Toolbar)findViewById(R.id.top_toolbar_myprofile);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        setupWidgets();

        ratingsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToProfileRating(ratingScore, numratings);
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.bottom_action_account);
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

                    default:
                        return false;
                }
            }
       });


    }



    public void setupWidgets(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        ratingsView = findViewById(R.id.view_rating);
        txtProfileName = findViewById(R.id.txt_profile_name);
        txtProfileIntro = findViewById(R.id.txt_profile_intro);
        txtNumRating = findViewById(R.id.txt_num_rating);
        //txtProfilePhone = findViewById(R.id.txt_profile_phone);
        txtProfileCity = findViewById(R.id.txt_profile_location);

        interestChipGroup = findViewById(R.id.profile_interestChipGroup);
        imgProfile = findViewById(R.id.img_profile);
        bottomNavigationView = findViewById(R.id.profileBottomNav);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if(currentUser == null){
            sendToLogin();
        }else{

            current_user_id = currentUser.getUid();

            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        if(!task.getResult().exists()){
                            Intent intent = new Intent(ProfileActivity.this,EditProfileActivity.class);
                            startActivity(intent);
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

                            //populate profile layout
                            profileImageURI = Uri.parse(image);
                            txtProfileName.setText(name);
                            txtProfileCity.setText(city);
                            txtProfileIntro.setText(intro);

                            if(!interests.isEmpty()) {
                                for (int i = 0; i < interests.size(); i++) {
                                    Log.d("interests", "value " + Integer.parseInt(String.valueOf(interests.get(i))));
                                    int temp = Integer.parseInt(String.valueOf(interests.get(i)));

                                    Log.d("interest list: " , "value: " + listInterests[temp]);
                                }
                            }

                            RequestOptions placeholderRequest = new RequestOptions();
                            placeholderRequest.placeholder(R.drawable.profile_default);

                            Glide.with(ProfileActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(imgProfile);

                        }
                    }else{
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(ProfileActivity.this, "Firestore Retrieve Error " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            });

            //get number of rating  for current user to calculate current rating
            firebaseFirestore.collection("Users").document(current_user_id).collection("ratings").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

                        txtNumRating.setText(Double.toString(ratingScore));
                    }
                }
            });
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.myprofile_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Get the menu item and determine what action to take.
        switch (item.getItemId()) {
            // In the case of logging out, finish the activity.
            case R.id.settings:
                break;
            case R.id.logout:
                logout();
                return true;
            case R.id.edit_profile:
                Intent intent = new Intent(ProfileActivity.this,EditProfileActivity.class);
                startActivity(intent);
            default:
                // Otherwise, do nothing.
                break;
        }

        // Call the super version of this method.
        return super.onOptionsItemSelected(item);
    }

    private void logout() {

        /*
        * Delete deviceToken from the subcollection of the current logged in user
        * so that they don't receive push notifications for any other user
        * */

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(!task.isSuccessful()){
                    Log.d("FCM:","Fetching FCM registration token failed", task.getException());
                    return;
                }

                final String token = task.getResult();
                Log.d("FCM INSTANCE:", token);

                firebaseFirestore.collection("Users").document(current_user_id).collection("deviceTokens").document(token).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d("FCM:","DELETING TOKEN SUCCESSFUL: " + token);

                            //Logout User and Send to Login Page
                            firebaseAuth.signOut();
                            sendToLogin();


                        }else{
                            Log.d("FCM:","DELETING TOKEN FAILED: " + token, task.getException());
                        }
                    }
                });
            }
        });

    }

    private void sendToLogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToMain() {
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void sendToMyJobs() {
        Intent intent = new Intent(ProfileActivity.this, MyJobsActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToProfileRating(double ratingScore, int numratings){
        Intent intent = new Intent(ProfileActivity.this, ProfileRatingActivity.class);

        intent.putExtra("RATING_SCORE", String.valueOf(ratingScore));
        intent.putExtra("NUMBER_OF_RATINGS", String.valueOf(numratings));

        startActivity(intent);
        finish();
    }

    private void sendToMessages() {
        Intent intent = new Intent(ProfileActivity.this, MessagesActivity.class);
        startActivity(intent);
        finish();
    }
}

