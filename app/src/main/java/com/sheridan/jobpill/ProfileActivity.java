package com.sheridan.jobpill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class ProfileActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

       toolbar = (Toolbar)findViewById(R.id.top_toolbar_myprofile);
       toolbar.setTitle("");
       setSupportActionBar(toolbar);


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
                break;
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
}

