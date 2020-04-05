package com.sheridan.jobpill.Auth;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sheridan.jobpill.MainActivity;
import com.sheridan.jobpill.R;

public class  LoginActivity extends AppCompatActivity {

    EditText userEmail;
    EditText userPass;
    Button userLogin;
    TextView forgotPassword;

    FirebaseAuth fb;

    private FirebaseFirestore firebaseFirestore;
    private String current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //declare spannable string variables
        String text = "Forgot Password?";
        SpannableString spannableString = new SpannableString(text);

        userEmail = findViewById(R.id.editEmail);
        userPass = findViewById(R.id.editPass);
        userLogin = findViewById(R.id.btn_login);
        forgotPassword = findViewById(R.id.txt_view_forgot_password);
        fb = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();

        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userEmail.getText().toString().equals("") || userPass.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter and E-mail and Password", Toast.LENGTH_LONG).show();
                } else {
                    fb.signInWithEmailAndPassword(userEmail.getText().toString(), userPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            } else {
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        //redirect to reset password page when clicking the text link "Forgot Password?"
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE); //change link color to blue
            }
        };

        //set part of the string to be clickable
        spannableString.setSpan(clickableSpan, 0, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //set UI text to the spannable string and make the link in the textView element clickable
        forgotPassword.setText(spannableString);
        forgotPassword.setMovementMethod(LinkMovementMethod.getInstance());

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = fb.getCurrentUser();

        if (currentUser != null) {
            sendToMain();
        }
    }

    private void sendToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void Signup_page(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
    }
}
