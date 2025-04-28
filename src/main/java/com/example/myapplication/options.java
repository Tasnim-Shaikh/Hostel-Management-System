package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityDashboardBinding;

public class options extends DrawerBaseForPatient {
    Button btnRegisterMess, btnViewMenu;
    String username; // To store the username passed from login
    ActivityDashboardBinding activityDashboardBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDashboardBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(activityDashboardBinding.getRoot());

        getLayoutInflater().inflate(R.layout.options, activityDashboardBinding.contentFrame, true);

        // Get the username from the Intent
        username = getIntent().getStringExtra("username");

        btnRegisterMess = findViewById(R.id.btnRegisterMess);
        btnViewMenu = findViewById(R.id.btnViewMenu);

        btnRegisterMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to MessRegistrationActivity with username
                Intent intent = new Intent(options.this, mess_registration.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        btnViewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to ViewMenuActivity with username
                Intent intent = new Intent(options.this, ViewMenuActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }
}
