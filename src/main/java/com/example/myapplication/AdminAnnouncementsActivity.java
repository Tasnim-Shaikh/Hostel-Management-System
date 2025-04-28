//package com.example.myapplication;
//
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.myapplication.databinding.ActivityDashboardBinding;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//
//public class AdminAnnouncementsActivity extends DrawerBaseActivity {
//
//    EditText etTitle, etMessage;
//    Button btnAddAnnouncement;
//    RecyclerView recyclerAdminAnnouncements;
//    AnnouncementAdapter adapter;
//    List<AnnouncementModel> announcementList;
//
//    DatabaseReference announcementRef;
//    ActivityDashboardBinding activityDashboardBinding;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        activityDashboardBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
//        setContentView(activityDashboardBinding.getRoot());
//        getLayoutInflater().inflate(R.layout.annoncement, activityDashboardBinding.contentFrame, true);
//
//        etTitle = findViewById(R.id.etTitle);
//        etMessage = findViewById(R.id.etMessage);
//        btnAddAnnouncement = findViewById(R.id.btnAddAnnouncement);
//        recyclerAdminAnnouncements = findViewById(R.id.recyclerAdminAnnouncements);
//
//        announcementList = new ArrayList<>();
//        adapter = new AnnouncementAdapter(this, announcementList);
//
//        recyclerAdminAnnouncements.setLayoutManager(new LinearLayoutManager(this));
//        recyclerAdminAnnouncements.setAdapter(adapter);
//
//        announcementRef = FirebaseDatabase.getInstance().getReference("Announcement");
//
//        btnAddAnnouncement.setOnClickListener(v -> {
//            String title = etTitle.getText().toString().trim();
//            String message = etMessage.getText().toString().trim();
//
//            if (title.isEmpty() || message.isEmpty()) {
//                Toast.makeText(this, "Please enter both title and message", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
//
//            AnnouncementModel announcement = new AnnouncementModel(title, message, currentDate);
//
//            String id = announcementRef.push().getKey();
//            if (id != null) {
//                announcementRef.child(id).setValue(announcement)
//                        .addOnSuccessListener(unused -> {
//                            Toast.makeText(this, "Announcement added", Toast.LENGTH_SHORT).show();
//                            etTitle.setText("");
//                            etMessage.setText("");
//                        })
//                        .addOnFailureListener(e -> {
//                            Toast.makeText(this, "Failed to add: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        });
//            }
//        });
//
//        fetchAnnouncements();
//    }
//
//    private void fetchAnnouncements() {
//        announcementRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                announcementList.clear();
//                for (DataSnapshot data : snapshot.getChildren()) {
//                    AnnouncementModel model = data.getValue(AnnouncementModel.class);
//                    if (model != null) {
//                        announcementList.add(model);
//                    }
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(AdminAnnouncementsActivity.this, "Failed to load announcements", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}
package com.example.myapplication;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ActivityDashboardBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminAnnouncementsActivity extends DrawerBaseActivity {

    EditText etTitle, etMessage;
    Button btnAddAnnouncement;
    RecyclerView recyclerAdminAnnouncements;
    AnnouncementAdapter adapter;
    List<AnnouncementModel> announcementList;
    List<String> announcementKeys;

    DatabaseReference announcementRef;
    ActivityDashboardBinding activityDashboardBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDashboardBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(activityDashboardBinding.getRoot());
        getLayoutInflater().inflate(R.layout.annoncement, activityDashboardBinding.contentFrame, true);

        etTitle = findViewById(R.id.etTitle);
        etMessage = findViewById(R.id.etMessage);
        btnAddAnnouncement = findViewById(R.id.btnAddAnnouncement);
        recyclerAdminAnnouncements = findViewById(R.id.recyclerAdminAnnouncements);

        announcementList = new ArrayList<>();
        announcementKeys = new ArrayList<>();
        adapter = new AnnouncementAdapter(this, announcementList);

        recyclerAdminAnnouncements.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdminAnnouncements.setAdapter(adapter);

        announcementRef = FirebaseDatabase.getInstance().getReference("Announcement");

        btnAddAnnouncement.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String message = etMessage.getText().toString().trim();

            if (title.isEmpty() || message.isEmpty()) {
                Toast.makeText(this, "Please enter both title and message", Toast.LENGTH_SHORT).show();
                return;
            }

            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            AnnouncementModel announcement = new AnnouncementModel(title, message, currentDate);

            String id = announcementRef.push().getKey();
            if (id != null) {
                announcementRef.child(id).setValue(announcement)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Announcement added", Toast.LENGTH_SHORT).show();
                            etTitle.setText("");
                            etMessage.setText("");
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to add: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        fetchAnnouncements();
        initSwipeToDelete();
    }

    private void fetchAnnouncements() {
        announcementRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                announcementList.clear();
                announcementKeys.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    AnnouncementModel model = data.getValue(AnnouncementModel.class);
                    if (model != null) {
                        announcementList.add(model);
                        announcementKeys.add(data.getKey()); // Track Firebase keys
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminAnnouncementsActivity.this, "Failed to load announcements", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false; // no move operation
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                String keyToDelete = announcementKeys.get(position);

                announcementRef.child(keyToDelete).removeValue()
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(AdminAnnouncementsActivity.this, "Announcement deleted", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(AdminAnnouncementsActivity.this, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

                // Remove from local list (handled in fetchAnnouncements but safe fallback)
                announcementList.remove(position);
                announcementKeys.remove(position);
                adapter.notifyItemRemoved(position);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerAdminAnnouncements);
    }
}
