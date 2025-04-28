package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import com.example.myapplication.databinding.ActivityDashboardBinding;

public class DoctorHome extends DrawerBaseActivity {

    ActivityDashboardBinding activityDashboardBinding;
    LinearLayout linearLayoutForProfile,myPatient,appoint,chat,logout,room;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main_for_doctor);

        activityDashboardBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(activityDashboardBinding.getRoot());

        // Inflate Doctor's layout inside the drawer
        getLayoutInflater().inflate(R.layout.activity_main_for_doctor, activityDashboardBinding.contentFrame, true);
        linearLayoutForProfile=findViewById(R.id.profileId);
        myPatient=findViewById(R.id.myPatient);
        appoint=findViewById(R.id.appoint);
        chat=findViewById(R.id.chat);
        room=findViewById(R.id.room);
        logout=findViewById(R.id.logout);
        linearLayoutForProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  Intent it=new Intent(DoctorHome.this,Profile.class);
                  startActivity(it);
                }
            });
        myPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it=new Intent(DoctorHome.this,AdminDashboard.class);
                startActivity(it);
            }
        });
        appoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it=new Intent(DoctorHome.this, AdminAnnouncementsActivity.class);
                startActivity(it);
            }
        });
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it=new Intent(DoctorHome.this, AdminActivity.class);
                startActivity(it);
            }
        });
        room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it=new Intent(DoctorHome.this,Rooms.class);
                startActivity(it);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it=new Intent(DoctorHome.this, LoginActivity.class);
                startActivity(it);
            }
        });
    }
}
