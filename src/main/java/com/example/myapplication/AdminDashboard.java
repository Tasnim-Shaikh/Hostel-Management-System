package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityDashboardBinding;

public class AdminDashboard extends DrawerBaseActivity {

    Button btnCheckRegistrations, btnRemoveRegistrations, btnPutMenu;
    ActivityDashboardBinding activityDashboardBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDashboardBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(activityDashboardBinding.getRoot());

        getLayoutInflater().inflate(R.layout.activity_admin_dashboard, activityDashboardBinding.contentFrame, true);
        btnCheckRegistrations = findViewById(R.id.btnCheckRegistrations);
        btnRemoveRegistrations = findViewById(R.id.btnRemoveRegistrations);
        btnPutMenu = findViewById(R.id.btnPutMenu);

        btnCheckRegistrations.setOnClickListener(v -> {
            // TODO: Start CheckRegistrationsActivity
            startActivity(new Intent(AdminDashboard.this, CheckRegistrations.class));
        });

        btnRemoveRegistrations.setOnClickListener(v -> {
            // TODO: Start RemoveRegistrationsActivity
            startActivity(new Intent(AdminDashboard.this, RemoveRegistrations.class));
        });

        btnPutMenu.setOnClickListener(v -> {
            // TODO: Start PutMenuActivity
            startActivity(new Intent(AdminDashboard.this, PutMenuActivity.class));
        });
    }
}
