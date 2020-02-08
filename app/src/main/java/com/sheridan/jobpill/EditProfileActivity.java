package com.sheridan.jobpill;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView imgCancelIcon;
    private ImageView imgSaveIcon;

    private TextView txtChangeProfileImg;
    private CircleImageView imgChangeProfile;

    private EditText txtProfileName;
    private EditText txtProfileIntro;
    private EditText txtProfilePhone;
    private EditText txtProfileCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
    }

    public void setUpWidgets(){
        imgCancelIcon = findViewById(R.id.img_cancel_editprofile);
        imgSaveIcon = findViewById(R.id.img_save_editprofile);

        txtChangeProfileImg = findViewById(R.id.txt_change_imgprofile);
        imgChangeProfile = findViewById(R.id.img_change_profile);

        txtProfileName = findViewById(R.id.txt_profile_name);
        txtProfileIntro = findViewById(R.id.txt_profile_intro);
        txtProfilePhone = findViewById(R.id.txt_profile_phone);
        txtProfileCity = findViewById(R.id.txt_profile_city);

    }
}
