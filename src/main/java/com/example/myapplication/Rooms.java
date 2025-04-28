package com.example.myapplication;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.databinding.ActivityDashboardBinding;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;
public class Rooms extends DrawerBaseActivity {
    private RecyclerView recyclerView;
    private RoomStudentAdapter adapter;
    private List<RoomStudent> roomStudentList;
    private DatabaseReference roomsRef;
    ActivityDashboardBinding activityDashboardBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDashboardBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(activityDashboardBinding.getRoot());

        getLayoutInflater().inflate(R.layout.activity_room, activityDashboardBinding.contentFrame, true);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        roomStudentList = new ArrayList<>();
        adapter = new RoomStudentAdapter(roomStudentList);
        recyclerView.setAdapter(adapter);

        roomsRef = FirebaseDatabase.getInstance().getReference("rooms");

        fetchRoomStudents();

        // Swipe-to-delete setup
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                RoomStudent student = roomStudentList.get(position);

                // Confirmation dialog
                new AlertDialog.Builder(Rooms.this)
                        .setTitle("Delete Student")
                        .setMessage("Are you sure you want to delete \"" + student.getStudentName() + "\"?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            deleteStudentFromFirebase(student.getRoomNumber(), student.getStudentName());
                            roomStudentList.remove(position);
                            adapter.notifyItemRemoved(position);
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            adapter.notifyItemChanged(position); // reset swipe
                        })
                        .show();
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void fetchRoomStudents() {
        roomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomStudentList.clear();
                for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                    String roomNumber = roomSnapshot.getKey();
                    DataSnapshot studentNamesSnap = roomSnapshot.child("studentNames");
                    for (DataSnapshot studentSnap : studentNamesSnap.getChildren()) {
                        String studentName = studentSnap.getValue(String.class);
                        roomStudentList.add(new RoomStudent(roomNumber, studentName));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void deleteStudentFromFirebase(String roomNumber, String studentName) {
        DatabaseReference studentNamesRef = FirebaseDatabase.getInstance()
                .getReference("rooms")
                .child(roomNumber)
                .child("studentNames");

        studentNamesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot studentSnap : snapshot.getChildren()) {
                    String name = studentSnap.getValue(String.class);
                    if (name != null && name.equals(studentName)) {
                        // Remove student from room
                        studentSnap.getRef().removeValue();
                        break;
                    }
                }
                // Remove from users table
                FirebaseDatabase.getInstance().getReference("users")
                        .child(studentName).removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}
