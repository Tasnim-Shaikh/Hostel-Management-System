package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityDashboardBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PutMenuActivity extends DrawerBaseActivity {
    EditText mealInput;
    TextView promptText;
    Button nextButton;
    DatabaseReference menuRef;

    String[] days = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
    String[] meals = {"lunch", "dinner"};
    int dayIndex = 0, mealIndex = 0;
    ActivityDashboardBinding activityDashboardBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDashboardBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(activityDashboardBinding.getRoot());

        getLayoutInflater().inflate(R.layout.activity_put_menu, activityDashboardBinding.contentFrame, true);

        mealInput = findViewById(R.id.mealInput);
        promptText = findViewById(R.id.promptText);
        nextButton = findViewById(R.id.nextButton);
        menuRef = FirebaseDatabase.getInstance().getReference("Menu");

        updatePrompt();

        nextButton.setOnClickListener(v -> {
            String mealValue = mealInput.getText().toString().trim();
            if (mealValue.isEmpty()) {
                Toast.makeText(this, "Enter a meal", Toast.LENGTH_SHORT).show();
                return;
            }

            String currentDay = days[dayIndex];
            String currentMeal = meals[mealIndex];

            // Push to Firebase
            menuRef.child(currentDay).child(currentMeal).setValue(mealValue);

            // Move to next meal/day
            mealIndex++;
            if (mealIndex >= meals.length) {
                mealIndex = 0;
                dayIndex++;
            }

            mealInput.setText("");

            if (dayIndex >= days.length) {
                Toast.makeText(this, "Menu entry complete!", Toast.LENGTH_LONG).show();
                nextButton.setEnabled(false);
                return;
            }

            updatePrompt();
        });
    }

    void updatePrompt() {
        promptText.setText("Enter " + meals[mealIndex] + " for " + days[dayIndex]);
    }
}
