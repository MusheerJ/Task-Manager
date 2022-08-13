package com.taskmanager.horkrux.Activites;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taskmanager.horkrux.Adapters.NotificationAdapter;
import com.taskmanager.horkrux.Notification.NotificationData;
import com.taskmanager.horkrux.databinding.ActivityViewNotificationsBinding;

import java.util.ArrayList;
import java.util.Collections;

public class ViewNotificationsActivity extends AppCompatActivity {

    final private Context context = ViewNotificationsActivity.this;
    private ActivityViewNotificationsBinding binding;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private NotificationAdapter adapter;
    private ArrayList<NotificationData> notifications;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewNotificationsBinding.inflate(getLayoutInflater());
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        setContentView(binding.getRoot());


        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(context, notifications);


        binding.recyclerView2.setAdapter(adapter);
        binding.recyclerView2.setLayoutManager(new LinearLayoutManager(context));

        binding.notificationProgress.setVisibility(View.VISIBLE);

        database.getReference().child("Notifications/" + auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notifications.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    notifications.add(snapshot1.getValue(NotificationData.class));
                    Log.d("TAG", "onDataChange: ");
                }
                binding.notificationProgress.setVisibility(View.GONE);
                if (notifications.isEmpty()) {
                    binding.notificationEmptyMessage.setVisibility(View.VISIBLE);
                } else {
                    binding.notificationEmptyMessage.setVisibility(View.GONE);

                }
                Collections.reverse(notifications);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.backButtonInNoti.setOnClickListener(v -> {
            finish();
        });


    }
}