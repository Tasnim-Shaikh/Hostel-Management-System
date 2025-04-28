package com.example.myapplication;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityDashboardBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewMenuActivity extends DrawerBaseForPatient {

    private LinearLayout menuContainer;
    private DatabaseReference menuRef;
    ActivityDashboardBinding activityDashboardBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDashboardBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(activityDashboardBinding.getRoot());

        getLayoutInflater().inflate(R.layout.viewmenu, activityDashboardBinding.contentFrame, true);

        menuContainer = findViewById(R.id.menuContainer);
        menuRef = FirebaseDatabase.getInstance().getReference("Menu");

        // Fetch menu data and update UI
        fetchMenuData();
    }

    private void fetchMenuData() {
        menuRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    // Loop through each day and display its menu
                    for (DataSnapshot daySnapshot : dataSnapshot.getChildren()) {
                        String day = daySnapshot.getKey();
                        String lunch = daySnapshot.child("lunch").getValue(String.class);
                        String dinner = daySnapshot.child("dinner").getValue(String.class);

                        // Create and display TextViews for each day's menu
                        TextView dayTextView = new TextView(ViewMenuActivity.this);
                        dayTextView.setText(day.toUpperCase());
                        dayTextView.setTextSize(18);
                        dayTextView.setPadding(0, 20, 0, 5);
                        menuContainer.addView(dayTextView);

                        if (lunch != null) {
                            TextView lunchTextView = new TextView(ViewMenuActivity.this);
                            lunchTextView.setText("Lunch: " + lunch);
                            menuContainer.addView(lunchTextView);
                        }

                        if (dinner != null) {
                            TextView dinnerTextView = new TextView(ViewMenuActivity.this);
                            dinnerTextView.setText("Dinner: " + dinner);
                            menuContainer.addView(dinnerTextView);
                        }
                    }
                } else {
                    Toast.makeText(ViewMenuActivity.this, "No menu data available.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ViewMenuActivity.this, "Failed to load menu.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
