package com.taskmanager.horkrux.AdminPanel;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taskmanager.horkrux.Models.Users;
import com.taskmanager.horkrux.databinding.ActivityTeamMemberListBinding;

import java.util.ArrayList;

public class TeamMemberList extends AppCompatActivity {

    private ActivityTeamMemberListBinding binding;
    private final Context context = TeamMemberList.this;
    private AdminUserAdapter adapter;
    private FirebaseDatabase database;
    private ArrayList<Users> users;
    String requestedTeam;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTeamMemberListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        requestedTeam = getIntent().getStringExtra("requestedTeam");

        users = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        adapter = new AdminUserAdapter(context, users, null);

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.adminUserRecylerView.setLayoutManager(new LinearLayoutManager(context));
        binding.adminUserRecylerView.setAdapter(adapter);
        loadUsers();

    }

    private void loadUsers() {
        String path = "Users/";
        binding.progressBar.setVisibility(View.VISIBLE);
        database.getReference().child(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot s : snapshot.getChildren()) {
                    Users user = s.getValue(Users.class);
//                    users.add(user);
                    String dept = user.getUserDept();
//                    Toast.makeText(context, user.getUserDept(), Toast.LENGTH_SHORT).show();
                    try {
                        if (dept.equals(requestedTeam)) {
                            users.add(user);
                        }
                    } catch (Exception e) {
                        Log.d("", "onDataChange: ");
                    }

                    adapter.notifyDataSetChanged();
                }
                binding.progressBar.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}