package com.sheridan.jobpill;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class InstructionsActivity extends AppCompatActivity {

    Button CancelBtn;
    Button PostBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_posting_instructions);
        CancelBtn = findViewById(R.id.cancel_btn);
        PostBtn = findViewById(R.id.post_btn);
        CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent jobIntent = new Intent(InstructionsActivity.this, MainActivity.class);
                startActivity(jobIntent);
                finish();
            }
        });
        PostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent jobIntent = new Intent(InstructionsActivity.this, MainActivity.class);
                startActivity(jobIntent);
                finish();
            }
        });
    }
}
