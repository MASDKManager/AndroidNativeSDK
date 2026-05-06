package com.mynative.sdkdemo;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityStartUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Go straight to BillingActivity — no delay, no double loading
        startActivity(new Intent(this, BillingActivity.class));
        overridePendingTransition(0, 0);
        finish();
    }
}
