package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.myapplication.databinding.ActivityDashboardBinding;

public class PatientHome extends DrawerBaseForPatient {

    ActivityDashboardBinding activityDashboardBinding;
    LinearLayout linearLayoutForProfile,myPatient,appoint,chat;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main_for_doctor);

        activityDashboardBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(activityDashboardBinding.getRoot());

        // Inflate Doctor's layout inside the drawer
        getLayoutInflater().inflate(R.layout.activity_main_for_user, activityDashboardBinding.contentFrame, true);
      String  loggedInUsername = getIntent().getStringExtra("username");

        linearLayoutForProfile=findViewById(R.id.profileId);
        myPatient=findViewById(R.id.myPatient);
        appoint=findViewById(R.id.appoint);
        chat=findViewById(R.id.chat);
        linearLayoutForProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  Intent it=new Intent(PatientHome.this,mess_registration.class);
                    it.putExtra("username",loggedInUsername);
                  startActivity(it);
                }
            });
        myPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it=new Intent(PatientHome.this,AttendanceActivity.class);
                startActivity(it);
            }
        });
        appoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it=new Intent(PatientHome.this, ShowAnnoncements.class);
                startActivity(it);
            }
        });
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it=new Intent(PatientHome.this, ViewMenuActivity.class);

                startActivity(it);
            }
        });
    }
}
