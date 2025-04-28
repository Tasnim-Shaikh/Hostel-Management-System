package com.example.myapplication;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityDashboardBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CheckRegistrations extends DrawerBaseActivity {

    LinearLayout layoutMess1, layoutMess2, layoutMess3;
    DatabaseReference dbRef;
    ActivityDashboardBinding activityDashboardBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDashboardBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(activityDashboardBinding.getRoot());
        getLayoutInflater().inflate(R.layout.activity_check_registrations, activityDashboardBinding.contentFrame, true);
        layoutMess1 = findViewById(R.id.layoutMess1);
        layoutMess2 = findViewById(R.id.layoutMess2);
        layoutMess3 = findViewById(R.id.layoutMess3);

        dbRef = FirebaseDatabase.getInstance().getReference("Menu");

        loadRegistrations("Mess1", layoutMess1);
        loadRegistrations("Mess2", layoutMess2);
        loadRegistrations("Mess3", layoutMess3);
    }

    private void loadRegistrations(String messName, LinearLayout layout) {
        dbRef.child(messName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                layout.removeAllViews();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    String user = snap.getValue(String.class);
                    TextView tv = new TextView(CheckRegistrations.this);
                    tv.setText(user);
                    layout.addView(tv);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                TextView tv = new TextView(CheckRegistrations.this);
                tv.setText("Error loading: " + error.getMessage());
                layout.addView(tv);
            }
        });
    }
}
