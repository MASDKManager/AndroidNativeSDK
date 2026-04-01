package com.opn.nativeflow;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class DashboardActivity extends AppCompatActivity  {

    AppCompatButton placesSateBtn;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        RelativeLayout mainContainer = findViewById(R.id.dashMapLyt);

        placesSateBtn = findViewById(R.id.btn_start_places);


        placesSateBtn.setOnClickListener(view -> {
            openBillingActivity();
        });

    }
    private void openBillingActivity() {
        Intent intent = new Intent(this, BillingActivity.class);
        startActivity(intent);
    }


}