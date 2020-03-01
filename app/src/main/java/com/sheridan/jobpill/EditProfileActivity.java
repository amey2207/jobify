package com.sheridan.jobpill;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView imgCancelIcon;
    private ImageView imgSaveIcon;

    private TextView txtChangeProfileImg;
    private CircleImageView imgChangeProfile;

    private TextView txtInterests;
    private Button btnInterests;
    String[] listInterests;
    boolean[] checkedInterests;
    ArrayList<Integer> selectedInterests = new ArrayList<>();

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
    private FirebaseUser currentUser;

    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        setUpWidgets();

        listInterests = getResources().getStringArray(R.array.interest_categories);
        checkedInterests = new boolean[listInterests.length];

        btnInterests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(EditProfileActivity.this);
                mBuilder.setTitle(R.string.dialog_title);
                mBuilder.setMultiChoiceItems(listInterests, checkedInterests, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if(isChecked){
                            // if current item is not in the selectedItems arraylist add it in the arraylist
                            if(!selectedInterests.contains(position)){
                                selectedInterests.add(position);
                                //else remove item from the list
                            }
                        } else {
                            if(selectedInterests.contains(position)){
                                selectedInterests.remove(selectedInterests.indexOf(position));
                            }
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(R.string.lbl_dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String items = "";
                        for(int i = 0; i < selectedInterests.size(); i++){
                            items = items + listInterests[selectedInterests.get(i)];
                            if(i != selectedInterests.size()-1){
                                items = items + ",";
                            }

                            Log.d("interests: ", "value:" + selectedInterests.get(i).toString());
                        }

                        txtInterests.setText(items);
                    }
                });

                mBuilder.setNegativeButton(R.string.lbl_dialog_dismiss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton(R.string.lbl_dialog_clearall, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for(int i = 0; i < checkedInterests.length; i++){
                            checkedInterests[i] = false;
                        }
                        selectedInterests.clear();
                        txtInterests.setText("");
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();

            }
        });

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
                        ArrayList<Integer> interests = (ArrayList<Integer>)task.getResult().get("interests");

                        if(!interests.isEmpty()){
                            for(int i = 0; i < interests.size();i++){
                                Log.d("interests", "value " + Integer.parseInt(String.valueOf(interests.get(i))));
                                int temp =  Integer.parseInt(String.valueOf(interests.get(i)));
                                checkedInterests[temp] = true;
                                selectedInterests.add(temp);
                                Log.d("selected interests: ", "value: " + selectedInterests.toString());
                            }

                            String items = "";
                            for(int i = 0; i < selectedInterests.size(); i++){
                                items = items + listInterests[selectedInterests.get(i)];
                                if(i != selectedInterests.size()-1){
                                    items = items + ",";
                                }

                                Log.d("interests: ", "value:" + selectedInterests.get(i).toString());
                            }

                            //set default selected interests
                            txtInterests.setText(items);


                        }

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

                // If a new profile picture is selected by the user

                if (isChanged) {

                    if (!TextUtils.isEmpty(name) && profileImageURI != null) {

                        image_path = storageReference.child("profile_images").child(user_id + ".jpg");


                        image_path.putFile(profileImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()) {


                                    storeFirestore(task, name, intro, phone, city, selectedInterests);

                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(EditProfileActivity.this, "Image error: " + error, Toast.LENGTH_LONG).show();

                                    progressBar.setVisibility(View.INVISIBLE);

                                }

                            }
                        });

                        // If no new picture is selected by the user
                        // check if the name field is empty or if the profileimage is not selected

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
                        storeFirestore(null, name, intro, phone, city, selectedInterests);
                    }
                }

            }
        });




    }

    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, final String name, final String intro, final String phone, final String city, final ArrayList interests) {


        if (task != null) {

            image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    downloadUri = uri;

                    final Map<String, Object> userMap = new HashMap<>();
                    userMap.put("name", name);
                    userMap.put("intro", intro);
                    userMap.put("phone", phone);
                    userMap.put("city", city);
                    userMap.put("photoURL", downloadUri.toString());
                    userMap.put("email",currentUser.getEmail());
                    userMap.put("interests", selectedInterests);


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

            final Map<String, Object> userMap = new HashMap<>();
            userMap.put("name", name);
            userMap.put("intro", intro);
            userMap.put("phone", phone);
            userMap.put("city", city);
            userMap.put("photoURL", downloadUri.toString());
            userMap.put("email",currentUser.getEmail());
            userMap.put("interests", selectedInterests);



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

        txtInterests = findViewById(R.id.txt_interests);
        btnInterests = findViewById(R.id.btn_interestsdialog);

        progressBar = findViewById(R.id.edit_profile_progress);
        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        currentUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

    }
}
