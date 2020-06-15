package com.sheridan.jobpill.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.sheridan.jobpill.R;

import java.util.regex.Pattern;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText userEmail;
    Button sendEmailBtn;

    FirebaseAuth fb;

    //declare regex pattern for email format
    final Pattern EMAIL_FORMAT = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        //initialize widgets and firebase instance
        userEmail = findViewById(R.id.email_edttxt);
        sendEmailBtn = findViewById(R.id.send_email_btn);
        fb = FirebaseAuth.getInstance();

        sendEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //declare system variables
                String email = userEmail.getText().toString().trim();

                if (!validateEmail(email) || TextUtils.isEmpty(email)) {
                    //display error message
                    Toast.makeText(ResetPasswordActivity.this, "The email address entered is invalid.", Toast.LENGTH_LONG).show();
                } else {
                    fb.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //display success message to user and redirect to login screen
                                Toast.makeText(ResetPasswordActivity.this, "Email has been sent. Please check your email to reset your password.", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                //display error message to user
                                Toast.makeText(ResetPasswordActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    //checks if the email is valid (matches against regex pattern)
    private boolean validateEmail(String email) {
        return EMAIL_FORMAT.matcher(email).matches();
    }
}