package com.sheridan.jobpill.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sheridan.jobpill.MainActivity;
import com.sheridan.jobpill.Profile.EditProfileActivity;
import com.sheridan.jobpill.R;

public class SignUpActivity extends AppCompatActivity {

    //declare UI variables
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private TextView clickHere;
    private Button signUpBtn;
    private ProgressBar signUpProgress;

    //declare firebase authentication
    private FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //declare spannable string variables
        String text = "Already a user? click here";
        SpannableString spannableString = new SpannableString(text);

        setupWidgets();

        //sign up the user
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //declare system variables
                String emailID = email.getText().toString().trim();
                String pass = password.getText().toString();
                String confPass = confirmPassword.getText().toString();
                String emailFormat = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                //check if fields are empty
               if(!TextUtils.isEmpty(emailID) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(confPass)){

                   //check if email is valid and passwords match
                   if(pass.equals(confPass) && emailID.matches(emailFormat) && emailID.length() > 0){
                       signUpProgress.setVisibility(View.VISIBLE);

                       firebaseAuth.createUserWithEmailAndPassword(emailID,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {
                               if(task.isSuccessful()){

                                   Intent setupIntent = new Intent(SignUpActivity.this, EditProfileActivity.class);
                                   startActivity(setupIntent);
                                   finish();

                               }else{

                                   String errorMessage = task.getException().getMessage();
                                   Toast.makeText(SignUpActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();

                               }

                               signUpProgress.setVisibility(View.INVISIBLE);
                           }
                       });
                   }
                   else if(!emailID.matches(emailFormat)){
                       Toast.makeText(SignUpActivity.this, "Please enter a valid email address", Toast.LENGTH_LONG).show();
                   }
                   else {
                       Toast.makeText(SignUpActivity.this, "Passwords don't match.", Toast.LENGTH_LONG).show();
                   }
               }
               else{
                   if(TextUtils.isEmpty(emailID)){
                       Toast.makeText(SignUpActivity.this, "Please provide a valid email address", Toast.LENGTH_LONG).show();
                   }else if(TextUtils.isEmpty(pass)){
                       Toast.makeText(SignUpActivity.this, "Please provide a valid password", Toast.LENGTH_LONG).show();
                   }else if(TextUtils.isEmpty(confPass)){
                       Toast.makeText(SignUpActivity.this, "Please confirm your password", Toast.LENGTH_LONG).show();
                   }else if(!pass.equals(confPass)){
                       Toast.makeText(SignUpActivity.this, "Confirm Password and Password don't match.", Toast.LENGTH_LONG).show();
                   }
               }
            }
        });

        //redirect to login page when clicking the text link "click me"
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE); //change link color to blue
            }
        };

        //set part of the string to be clickable
        spannableString.setSpan(clickableSpan, 16, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //set UI text to the spannable string and make the link in the textView element clickable
        clickHere.setText(spannableString);
        clickHere.setMovementMethod(LinkMovementMethod.getInstance());

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            sendToMain();
        }
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    public void setupWidgets(){
        email = findViewById(R.id.email_txt);
        password = findViewById(R.id.pass_txt);
        confirmPassword = findViewById(R.id.confPass_txt);
        clickHere = findViewById(R.id.clickHere_txtview);
        signUpBtn = findViewById(R.id.signUp_btn);
        signUpProgress = findViewById(R.id.signup_progress);

        firebaseAuth = FirebaseAuth.getInstance();

    }
}
