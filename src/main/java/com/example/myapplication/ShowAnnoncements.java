package com.example.myapplication;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ActivityDashboardBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShowAnnoncements extends DrawerBaseForPatient {

    RecyclerView recyclerAllAnnouncements;
    AnnouncementAdapter adapter;
    List<AnnouncementModel> announcementList;
    DatabaseReference announcementRef;
    ActivityDashboardBinding activityDashboardBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDashboardBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(activityDashboardBinding.getRoot());

        // Inflate Doctor's layout inside the drawer
        getLayoutInflater().inflate(R.layout.activity_all_annoncements, activityDashboardBinding.contentFrame, true);
        recyclerAllAnnouncements = findViewById(R.id.recyclerAllAnnouncements);
        announcementList = new ArrayList<>();
        adapter = new AnnouncementAdapter(this, announcementList);

        recyclerAllAnnouncements.setLayoutManager(new LinearLayoutManager(this));
        recyclerAllAnnouncements.setAdapter(adapter);

        announcementRef = FirebaseDatabase.getInstance().getReference("Announcement");

        fetchAnnouncements();
    }

    private void fetchAnnouncements() {
        announcementRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                announcementList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    AnnouncementModel model = data.getValue(AnnouncementModel.class);
                    if (model != null) {
                        announcementList.add(model);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShowAnnoncements.this, "Failed to load announcements", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
