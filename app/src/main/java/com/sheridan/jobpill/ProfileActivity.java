package com.sheridan.jobpill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends AppCompatActivity {

    Toolbar toolbar;

    FirebaseAuth firebaseAuth;

    FirebaseFirestore firebaseFirestore;

    String current_user_id;

    TextView txtProfileName;
    TextView txtProfileIntro;
    TextView txtProfilePhone;
    TextView txtProfileCity;

    CircleImageView imgProfile;

    BottomNavigationView bottomNavigationView;

    private Uri profileImageURI = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

       toolbar = (Toolbar)findViewById(R.id.top_toolbar_myprofile);
       toolbar.setTitle("");
       setSupportActionBar(toolbar);

       setupWidgets();


       bottomNavigationView.setSelectedItemId(R.id.bottom_action_account);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.bottom_action_jobs:
//                        sentToJobs();
                        return true;
                    case R.id.bottom_action_schedule:
//                        sendToSchedule();
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

        txtProfileName = findViewById(R.id.txt_profile_name);
        txtProfileIntro = findViewById(R.id.txt_profile_intro);
//        txtProfilePhone = findViewById(R.id.txt_profile_phone);
        txtProfileCity = findViewById(R.id.txt_profile_location);

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

                            profileImageURI = Uri.parse(image);

                            txtProfileName.setText(name);
                            txtProfileCity.setText(city);
                            txtProfileIntro.setText(intro);


                            RequestOptions placeholderRequest = new RequestOptions();
                            placeholderRequest.placeholder(R.drawable.profile_default);

                            Glide.with(ProfileActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(imgProfile);




                        }
                    }else{
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(ProfileActivity.this, "Firesstore Retrieve Error " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            });

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
        firebaseAuth.signOut();
        sendToLogin();
    }

    private void sendToLogin() {
        Intent intent = new Intent(ProfileActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToMain() {
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

