package com.sheridan.jobpill;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.List;

public class JobPostingActivity extends AppCompatActivity {

    Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_posting);
        nextBtn = findViewById(R.id.Next_btn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent jobIntent = new Intent(JobPostingActivity.this, InstructionsActivity.class);
                startActivity(jobIntent);
                finish();
            }
        });
        addItemsOnSpinner();
    }

    public void Instructions_page(View view) {
        startActivity(new Intent(this, InstructionsActivity.class));
    }
    public void addItemsOnSpinner() {

        Spinner spinner2 = findViewById(R.id.cat_spinner);
        List<String> list = new ArrayList<>();
        list.add("list 1");
        list.add("list 2");
        list.add("list 3");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapter);
    }
}
