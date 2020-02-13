package com.sheridan.jobpill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class SignUpActivity extends AppCompatActivity {

    private EditText name;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private Button signUpBtn;
    private ProgressBar signUpProgress;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setupWidgets();

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailID = email.getText().toString();
                String pass = password.getText().toString();
                String confPass = confirmPassword.getText().toString();

               if(!TextUtils.isEmpty(emailID) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(confPass)){
                   if(pass.equals(confPass)){
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
                   } else {
                       Toast.makeText(SignUpActivity.this, "Confirm Password and Password don't match.", Toast.LENGTH_LONG).show();

                   }
               }
            }
        });

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
        name = findViewById(R.id.name_txt);
        email = findViewById(R.id.email_txt);
        password = findViewById(R.id.pass_txt);
        confirmPassword = findViewById(R.id.confPass_txt);
        signUpBtn = findViewById(R.id.signUp_btn);
        signUpProgress = findViewById(R.id.signup_progress);

        name.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();


    }
}
