package com.sheridan.jobpill.FCM;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sheridan.jobpill.Auth.LoginActivity;
import com.sheridan.jobpill.MainActivity;

import java.util.HashMap;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM";
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    @Override
    public void onCreate() {
        super.onCreate();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        currentUser = firebaseAuth.getCurrentUser();

    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("FCM:", "The Refreshed token: " + token);

        SharedPreferences sharedPreferences = getSharedPreferences("tokenSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("deviceToken",token);

        editor.commit();

        Log.d("SHARED_PREFERENCES", "TOKEN SAVED AS: " + token);




    }




}
