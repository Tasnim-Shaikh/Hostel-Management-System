package com.example.myapplication;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ActivityDashboardBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Profile extends DrawerBaseActivity {

    ActivityDashboardBinding activityDashboardBinding;
    EditText etDate;
    Button btnShowPresent, btnShowAbsent, btnExportPDF;
    RecyclerView recyclerView;
    List<String> students = new ArrayList<>();
    AttendanceAdapter adapter;

    DatabaseReference attendanceRef, usersRef;
    String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDashboardBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(activityDashboardBinding.getRoot());

        getLayoutInflater().inflate(R.layout.add_info_of_patient, activityDashboardBinding.contentFrame, true);

        etDate = findViewById(R.id.etDate);
        btnShowPresent = findViewById(R.id.btnShowPresent);
        btnShowAbsent = findViewById(R.id.btnShowAbsent);
     //   btnExportPDF = findViewById(R.id.btnExportPDF);
        recyclerView = findViewById(R.id.recyclerViewAttendance);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AttendanceAdapter(students);
        recyclerView.setAdapter(adapter);

        attendanceRef = FirebaseDatabase.getInstance().getReference("attendance");
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        btnShowPresent.setOnClickListener(v -> fetchAttendance(true));
        btnShowAbsent.setOnClickListener(v -> fetchAttendance(false));

       // btnExportPDF.setOnClickListener(v -> exportToPdf());

        checkPermissions();
    }

    private void fetchAttendance(boolean showPresent) {
        selectedDate = etDate.getText().toString().trim();
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Please enter a date.", Toast.LENGTH_SHORT).show();
            return;
        }

        attendanceRef.child(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot attendanceSnapshot) {
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot usersSnapshot) {
                        students.clear();
                        for (DataSnapshot userSnap : usersSnapshot.getChildren()) {
                            String usernameKey = userSnap.getKey(); // matches attendance key
                            String displayName = userSnap.child("username").getValue(String.class);
                            String phoneNumber = userSnap.child("phone").getValue(String.class);  // Get phone number

                            String status = attendanceSnapshot.child(usernameKey).getValue(String.class);

                            // Show present students
                            if (showPresent && "P".equals(status)) {
                                students.add(displayName + " - " + phoneNumber);  // Add phone number with name
                            }
                            // Show absent students
                            else if (!showPresent && (status == null || "A".equals(status))) {
                                students.add(displayName + " - " + phoneNumber);  // Add phone number with name
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Profile.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Profile.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void exportToPdf() {
        if (students.isEmpty()) {
            Toast.makeText(this, "No data to export", Toast.LENGTH_SHORT).show();
            return;
        }

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        int y = 25;
        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setTextSize(12);

        page.getCanvas().drawText("Attendance - " + selectedDate, 10, y, paint);
        y += 25;

        for (String student : students) {
            page.getCanvas().drawText(student, 10, y, paint);
            y += 20;
        }

        document.finishPage(page);

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + "/attendance_" + selectedDate + ".pdf";
        try {
            File file = new File(path);
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            Toast.makeText(this, "PDF saved to Downloads", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "PDF creation failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }
}
