
package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityDashboardBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

public class mess_registration extends DrawerBaseForPatient {

    Button mess1, mess2, mess3;
    DatabaseReference dbRef, regRef;
    String loggedInUsername;
    ActivityDashboardBinding activityDashboardBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDashboardBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(activityDashboardBinding.getRoot());

        getLayoutInflater().inflate(R.layout.activity_mess_registration, activityDashboardBinding.contentFrame, true);

        mess1 = findViewById(R.id.btnMess1);
        mess2 = findViewById(R.id.btnMess2);
        mess3 = findViewById(R.id.btnMess3);

        // Get username from intent
        loggedInUsername = getIntent().getStringExtra("username");

        if (loggedInUsername == null || loggedInUsername.trim().isEmpty()) {
            Toast.makeText(this, "Username not found. Please login again.", Toast.LENGTH_LONG).show();
            finish(); // Stop this activity
            return;
        }

        dbRef = FirebaseDatabase.getInstance().getReference("Menu");
        regRef = FirebaseDatabase.getInstance().getReference("registrations");

        checkIfAlreadyRegistered();

        // Display available slots
        displayAvailableSlots();

        mess1.setOnClickListener(view -> registerForMess("Mess1"));
        mess2.setOnClickListener(view -> registerForMess("Mess2"));
        mess3.setOnClickListener(view -> registerForMess("Mess3"));
    }

    private void checkIfAlreadyRegistered() {
        regRef.child(loggedInUsername).child("mess").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String mess = snapshot.getValue(String.class);
                    Toast.makeText(mess_registration.this, "Already registered for " + mess, Toast.LENGTH_SHORT).show();
                    mess1.setEnabled(false);
                    mess2.setEnabled(false);
                    mess3.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mess_registration.this, "Firebase error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayAvailableSlots() {
        // For each mess, fetch the count of registrations and display available slots
        displaySlotsForMess("Mess1", R.id.m1);
        displaySlotsForMess("Mess2", R.id.m2);
        displaySlotsForMess("Mess3", R.id.m3);
    }

    private void displaySlotsForMess(String messName, int textViewId) {
        DatabaseReference messRef = dbRef.child(messName);
        messRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                int maxCapacity = 5;
                long remainingSlots = maxCapacity - count;
                TextView textView = findViewById(textViewId);
                textView.setText("Slots: " + remainingSlots+"/5");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mess_registration.this, "Firebase Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerForMess(String messName) {
        DatabaseReference messRef = dbRef.child(messName);

        messRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                int maxCapacity = 5;
                long remainingSlots = maxCapacity - count;

                if (remainingSlots > 0) {
                    // Save registration for this user
                    regRef.child(loggedInUsername).child("mess").setValue(messName);

                    // Also add user to mess list (optional for admin to view)
                    String id = messRef.push().getKey();
                    messRef.child(id).setValue(loggedInUsername);
                    Toast.makeText(mess_registration.this, "Registered to " + messName + ". Slots left: " + (remainingSlots - 1), Toast.LENGTH_SHORT).show();
                    mess1.setEnabled(false);
                    mess2.setEnabled(false);
                    mess3.setEnabled(false);
                } else {
                    Toast.makeText(mess_registration.this, messName + " is full! Please try another.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mess_registration.this, "Firebase Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}