//package com.example.room;
//
//
//import android.os.Bundle;
//import android.widget.*;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.database.*;
//
//import java.util.ArrayList;
//
//public class AdminActivity extends AppCompatActivity {
//
//    EditText etRoomNumber, etStudentName;
//    Button btnAddStudent;
//    TextView tvRoomDetails;
//
//    DatabaseReference dbRef;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_admin);
//
//        // Initialize views
//        etRoomNumber = findViewById(R.id.etRoomNumber);
//        etStudentName = findViewById(R.id.etStudentName);
//        btnAddStudent = findViewById(R.id.btnAddStudent);
//        tvRoomDetails = findViewById(R.id.tvRoomDetails);
//
//        // Firebase reference
//        dbRef = FirebaseDatabase.getInstance().getReference();
//
//        btnAddStudent.setOnClickListener(v -> {
//            String roomNumber = etRoomNumber.getText().toString().trim();
//            String studentName = etStudentName.getText().toString().trim();
//
//            if (roomNumber.isEmpty() || studentName.isEmpty()) {
//                Toast.makeText(this, "Please enter both room number and student name", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            addStudentToRoom(roomNumber, studentName);
//        });
//    }
//
//    private void addStudentToRoom(String roomNumber, String studentName) {
//        // Check if student exists in users table
//        DatabaseReference userRef = dbRef.child("users").child(studentName);
//
//        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (!snapshot.exists()) {
//                    Toast.makeText(AdminActivity.this, "Student does not exist in users table!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                // Proceed to add student to room
//                DatabaseReference roomRef = dbRef.child("rooms").child(roomNumber);
//                roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot roomSnap) {
//                        ArrayList<String> currentStudents = new ArrayList<>();
//                        int studentCount = 0;
//
//                        if (roomSnap.exists()) {
//                            Long count = roomSnap.child("noOfStudents").getValue(Long.class);
//                            if (count != null) {
//                                studentCount = count.intValue();
//                            }
//
//                            for (DataSnapshot child : roomSnap.child("studentNames").getChildren()) {
//                                currentStudents.add(child.getValue(String.class));
//                            }
//                        }
//
//                        if (currentStudents.contains(studentName)) {
//                            Toast.makeText(AdminActivity.this, "Student already in this room!", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                        if (studentCount >= 4) {
//                            Toast.makeText(AdminActivity.this, "Room is already full!", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                        currentStudents.add(studentName);
//                        roomRef.child("noOfStudents").setValue(currentStudents.size());
//                        roomRef.child("studentNames").setValue(currentStudents);
//
//                        Toast.makeText(AdminActivity.this, "Student assigned to room successfully!", Toast.LENGTH_SHORT).show();
//                        displayRoomDetails(roomNumber, currentStudents.size(), currentStudents);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(AdminActivity.this, "Failed to access room: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(AdminActivity.this, "Failed to access user: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void displayRoomDetails(String roomNumber, int count, ArrayList<String> students) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("Room ").append(roomNumber).append(" has ").append(count).append(" student(s):\n\n");
//        for (String name : students) {
//            sb.append("• ").append(name).append("\n");
//        }
//        tvRoomDetails.setText(sb.toString());
//    }
//}
//package com.example.room;
//
//import android.os.Bundle;
//import android.widget.*;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.database.*;
//
//import java.util.ArrayList;
//
//public class AdminActivity extends AppCompatActivity {
//
//    EditText etRoomNumber, etStudentName;
//    Button btnAddStudent;
//    TextView tvRoomDetails;
//
//    DatabaseReference dbRef;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_admin);
//
//        etRoomNumber = findViewById(R.id.etRoomNumber);
//        etStudentName = findViewById(R.id.etStudentName);
//        btnAddStudent = findViewById(R.id.btnAddStudent);
//        tvRoomDetails = findViewById(R.id.tvRoomDetails);
//
//        dbRef = FirebaseDatabase.getInstance().getReference();
//
//        btnAddStudent.setOnClickListener(v -> {
//            String roomNumber = etRoomNumber.getText().toString().trim();
//            String studentName = etStudentName.getText().toString().trim();
//
//            if (roomNumber.isEmpty() || studentName.isEmpty()) {
//                Toast.makeText(this, "Please enter both room number and student name", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            checkIfStudentAlreadyAssigned(studentName, roomNumber);
//        });
//
//        // Load all room info on start
//        displayAllRooms();
//    }
//
//    private void checkIfStudentAlreadyAssigned(String studentName, String roomNumber) {
//        DatabaseReference mapRef = dbRef.child("studentRoomMap").child(studentName);
//
//        mapRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    Toast.makeText(AdminActivity.this, "Student already assigned to Room " + snapshot.getValue(String.class), Toast.LENGTH_SHORT).show();
//                } else {
//                    addStudentToRoom(studentName, roomNumber);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {}
//        });
//    }
//
//    private void addStudentToRoom(String studentName, String roomNumber) {
//        // Step 1: Check if user exists
//        dbRef.child("users").child(studentName).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot userSnap) {
//                if (!userSnap.exists()) {
//                    Toast.makeText(AdminActivity.this, "Student not found in users!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                // Step 2: Load current students
//                DatabaseReference roomRef = dbRef.child("rooms").child(roomNumber);
//                roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot roomSnap) {
//                        ArrayList<String> students = new ArrayList<>();
//                        int count = 0;
//
//                        if (roomSnap.exists()) {
//                            Long current = roomSnap.child("noOfStudents").getValue(Long.class);
//                            if (current != null) count = current.intValue();
//
//                            for (DataSnapshot s : roomSnap.child("studentNames").getChildren()) {
//                                students.add(s.getValue(String.class));
//                            }
//                        }
//
//                        if (students.contains(studentName)) {
//                            Toast.makeText(AdminActivity.this, "Student already in this room", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                        if (count >= 4) {
//                            Toast.makeText(AdminActivity.this, "Room is full", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                        students.add(studentName);
//                        roomRef.child("noOfStudents").setValue(students.size());
//                        roomRef.child("studentNames").setValue(students);
//
//                        // Save mapping
//                        dbRef.child("studentRoomMap").child(studentName).setValue(roomNumber);
//
//                        Toast.makeText(AdminActivity.this, "Student assigned to room!", Toast.LENGTH_SHORT).show();
//                        displayAllRooms();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {}
//                });
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {}
//        });
//    }
//
//    private void displayAllRooms() {
//        dbRef.child("rooms").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                StringBuilder sb = new StringBuilder();
//
//                for (DataSnapshot room : snapshot.getChildren()) {
//                    String roomNum = room.getKey();
//                    long count = room.child("noOfStudents").getValue(Long.class) != null
//                            ? room.child("noOfStudents").getValue(Long.class)
//                            : 0;
//
//                    sb.append("Room ").append(roomNum).append(" - ").append(count).append(" student(s):\n");
//
//                    for (DataSnapshot nameSnap : room.child("studentNames").getChildren()) {
//                        sb.append("• ").append(nameSnap.getValue(String.class)).append("\n");
//                    }
//
//                    sb.append("\n");
//                }
//
//                tvRoomDetails.setText(sb.toString());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {}
//        });
//    }
//}


package com.example.myapplication;

import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityDashboardBinding;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class AdminActivity extends DrawerBaseActivity {
    ActivityDashboardBinding activityDashboardBinding;
    EditText etRoomNumber, etStudentName;
    Button btnAddStudent;
    TextView tvRoomDetails;

    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDashboardBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(activityDashboardBinding.getRoot());

        getLayoutInflater().inflate(R.layout.admin_room, activityDashboardBinding.contentFrame, true);

        etRoomNumber = findViewById(R.id.etRoomNumber);
        etStudentName = findViewById(R.id.etStudentName);
        btnAddStudent = findViewById(R.id.btnAddStudent);
        tvRoomDetails = findViewById(R.id.tvRoomDetails);

        dbRef = FirebaseDatabase.getInstance().getReference();

        btnAddStudent.setOnClickListener(v -> {
            String roomNumber = etRoomNumber.getText().toString().trim();
            String studentName = etStudentName.getText().toString().trim();

            if (roomNumber.isEmpty() || studentName.isEmpty()) {
                Toast.makeText(this, "Please enter both room number and student name", Toast.LENGTH_SHORT).show();
                return;
            }

            checkIfStudentAlreadyAssigned(studentName, roomNumber);
        });

        displayAllRooms();
    }

    private void checkIfStudentAlreadyAssigned(String studentName, String roomNumber) {
        DatabaseReference mapRef = dbRef.child("studentRoomMap").child(studentName);

        mapRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String existingRoom = snapshot.getValue(String.class);
                    if (!existingRoom.equals(roomNumber)) {
                        Toast.makeText(AdminActivity.this, "Student already assigned to Room " + existingRoom, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Already in this room – allow to continue (e.g. to update UI)
                }

                addStudentToRoom(studentName, roomNumber);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void addStudentToRoom(String studentName, String roomNumber) {
        // Step 1: Check if user exists
        dbRef.child("users").child(studentName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnap) {
                if (!userSnap.exists()) {
                    Toast.makeText(AdminActivity.this, "Student not found in users!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Step 2: Load room students
                DatabaseReference roomRef = dbRef.child("rooms").child(roomNumber);
                roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot roomSnap) {
                        ArrayList<String> students = new ArrayList<>();
                        int count = 0;

                        if (roomSnap.exists()) {
                            Long current = roomSnap.child("noOfStudents").getValue(Long.class);
                            if (current != null) count = current.intValue();

                            for (DataSnapshot s : roomSnap.child("studentNames").getChildren()) {
                                students.add(s.getValue(String.class));
                            }
                        }

                        if (!students.contains(studentName) && count >= 4) {
                            Toast.makeText(AdminActivity.this, "Room is full", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!students.contains(studentName)) {
                            students.add(studentName);
                        }

                        // Update room info
                        roomRef.child("noOfStudents").setValue(students.size());
                        roomRef.child("studentNames").setValue(students);

                        // Map student to this room
                        dbRef.child("studentRoomMap").child(studentName).setValue(roomNumber);

                        Toast.makeText(AdminActivity.this, "Student assigned to room!", Toast.LENGTH_SHORT).show();
                        displayAllRooms();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void displayAllRooms() {
        dbRef.child("rooms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StringBuilder sb = new StringBuilder();

                for (DataSnapshot room : snapshot.getChildren()) {
                    String roomNum = room.getKey();
                    long count = room.child("noOfStudents").getValue(Long.class) != null
                            ? room.child("noOfStudents").getValue(Long.class)
                            : 0;

                    sb.append("Room ").append(roomNum).append(" - ").append(count).append(" student(s):\n");

                    for (DataSnapshot nameSnap : room.child("studentNames").getChildren()) {
                        sb.append("• ").append(nameSnap.getValue(String.class)).append("\n");
                    }

                    sb.append("\n");
                }

                tvRoomDetails.setText(sb.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
