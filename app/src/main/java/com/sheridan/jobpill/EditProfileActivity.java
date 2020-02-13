package com.sheridan.jobpill;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firestore.v1beta1.FirestoreGrpc;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView imgCancelIcon;
    private ImageView imgSaveIcon;

    private TextView txtChangeProfileImg;
    private CircleImageView imgChangeProfile;

    private String user_id;

    private boolean isChanged = false;

    private EditText txtProfileName;
    private EditText txtProfileIntro;
    private EditText txtProfilePhone;
    private EditText txtProfileCity;

    private ProgressBar progressBar;

    private Uri profileImageURI = null;

    private Uri downloadUri;

    private StorageReference image_path = null;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;

    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        setUpWidgets();

        imgChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(EditProfileActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        BringImagePicker();
                    }

                } else {
                    BringImagePicker();
                }
            }
        });


        progressBar.setVisibility(View.VISIBLE);

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    if (task.getResult().exists()) {

                        String name = task.getResult().getString("name");
                        String intro = task.getResult().getString("intro");
                        String phone = task.getResult().getString("phone");
                        String city = task.getResult().getString("city");
                        String image = task.getResult().getString("photoURL");

                        profileImageURI = Uri.parse(image);

                        txtProfileName.setText(name);
                        txtProfileIntro.setText(intro);
                        txtProfilePhone.setText(phone);
                        txtProfileCity.setText(city);


                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.profile_default);

                        Glide.with(EditProfileActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(imgChangeProfile);


                    }

                } else {
                    String error = task.getException().getMessage();

                    Toast.makeText(EditProfileActivity.this, "Firestore Retrieve error: " + error, Toast.LENGTH_LONG).show();
                }

                progressBar.setVisibility(View.INVISIBLE);

            }
        });


        imgSaveIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String name = txtProfileName.getText().toString();
                final String intro = txtProfileIntro.getText().toString();
                final String phone = txtProfilePhone.getText().toString();
                final String city = txtProfileCity.getText().toString();

                progressBar.setVisibility(View.VISIBLE);


                if (isChanged) {

                    if (!TextUtils.isEmpty(name) && profileImageURI != null) {

                        image_path = storageReference.child("profile_images").child(user_id + ".jpg");


                        image_path.putFile(profileImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()) {


                                    storeFirestore(task, name, intro, phone, city);

                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(EditProfileActivity.this, "Image error: " + error, Toast.LENGTH_LONG).show();

                                    progressBar.setVisibility(View.INVISIBLE);

                                }

                            }
                        });

                    } else {

                        if (TextUtils.isEmpty(name)) {
                            Toast.makeText(EditProfileActivity.this, "Please provide a name", Toast.LENGTH_LONG).show();

                        } else if (profileImageURI == null) {
                            Toast.makeText(EditProfileActivity.this, "Please select a profile image", Toast.LENGTH_LONG).show();

                        }


                        progressBar.setVisibility(View.INVISIBLE);

                    }

                } else {

                    if (profileImageURI == null) {
                        Toast.makeText(EditProfileActivity.this, "Please select a profile image", Toast.LENGTH_LONG).show();

                        progressBar.setVisibility(View.INVISIBLE);

                    } else if(TextUtils.isEmpty(name)) {

                        Toast.makeText(EditProfileActivity.this, "Please provide a name", Toast.LENGTH_LONG).show();

                        progressBar.setVisibility(View.INVISIBLE);


                    }else{
                        storeFirestore(null, name, intro, phone, city);
                    }
                }

            }
        });


    }

    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, final String name, final String intro, final String phone, final String city) {


        if (task != null) {

            image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    downloadUri = uri;

                    final Map<String, String> userMap = new HashMap<>();
                    userMap.put("name", name);
                    userMap.put("intro", intro);
                    userMap.put("phone", phone);
                    userMap.put("city", city);
                    userMap.put("photoURL", downloadUri.toString());


                    firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {


                                Toast.makeText(EditProfileActivity.this, "User Profile Updated", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                                startActivity(intent);
                                finish();

                            } else {

                                String error = task.getException().getMessage();

                                Toast.makeText(EditProfileActivity.this, "Firestore error: " + error, Toast.LENGTH_LONG).show();


                            }

                            progressBar.setVisibility(View.INVISIBLE);


                        }
                    });
                }
            });
        } else {
            downloadUri = profileImageURI;

            final Map<String, String> userMap = new HashMap<>();
            userMap.put("name", name);
            userMap.put("intro", intro);
            userMap.put("phone", phone);
            userMap.put("city", city);
            userMap.put("photoURL", downloadUri.toString());


            firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {


                        Toast.makeText(EditProfileActivity.this, "User Profile Updated", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        finish();

                    } else {

                        String error = task.getException().getMessage();

                        Toast.makeText(EditProfileActivity.this, "Firestore error: " + error, Toast.LENGTH_LONG).show();


                    }

                    progressBar.setVisibility(View.INVISIBLE);


                }
            });

        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                profileImageURI = result.getUri();
                imgChangeProfile.setImageURI(profileImageURI);

                isChanged = true;


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(EditProfileActivity.this);
    }

    public void setUpWidgets() {
        imgCancelIcon = findViewById(R.id.img_cancel_editprofile);
        imgSaveIcon = findViewById(R.id.img_save_editprofile);

        txtChangeProfileImg = findViewById(R.id.txt_change_imgprofile);
        imgChangeProfile = findViewById(R.id.img_change_profile);

        txtProfileName = findViewById(R.id.txt_profile_name);
        txtProfileIntro = findViewById(R.id.txt_profile_intro);
        txtProfilePhone = findViewById(R.id.txt_profile_phone);
        txtProfileCity = findViewById(R.id.txt_profile_city);

        progressBar = findViewById(R.id.edit_profile_progress);
        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

    }
}
