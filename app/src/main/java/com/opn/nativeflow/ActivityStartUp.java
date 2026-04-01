package com.opn.nativeflow;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


public class ActivityStartUp extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        openNextActivity();
    }

    private void openNextActivity() {
        Intent intent = new Intent(ActivityStartUp.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

}