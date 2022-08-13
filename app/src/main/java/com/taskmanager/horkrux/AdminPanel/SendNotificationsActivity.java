package com.taskmanager.horkrux.AdminPanel;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListPopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taskmanager.horkrux.Adapters.UserAdapter;
import com.taskmanager.horkrux.CommonUtils;
import com.taskmanager.horkrux.Models.Users;
import com.taskmanager.horkrux.R;
import com.taskmanager.horkrux.databinding.ActivitySendNotificationsBinding;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class SendNotificationsActivity extends AppCompatActivity {

    private ActivitySendNotificationsBinding binding;
    private FirebaseDatabase database;
    final private String PATH = "Users";
    ListPopupWindow userList;
    UserAdapter adapter;
    ArrayAdapter userListAdapter;
    ArrayList<Users> assignedList;
    public static ArrayList<Users> items;
    public static ArrayList<String> showingItems;
    private Context context = SendNotificationsActivity.this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        database = FirebaseDatabase.getInstance();
        assignedList = new ArrayList<>();
        items = new ArrayList<>();
        showingItems = new ArrayList<>();
        userList = new ListPopupWindow(context);


        adapter = new UserAdapter(context, assignedList, "SendNotification");
        userListAdapter = new ArrayAdapter(context, R.layout.user_list, showingItems);

        loadUsers();

        binding.addParticipants.setOnClickListener(addParticipants);
        binding.notificationParticipants.setLayoutManager(new GridLayoutManager(context, 2));
        binding.notificationParticipants.setAdapter(adapter);


        binding.backButtonNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        adapter.notifyDataSetChanged();
        binding.sendNotificationToUser.setOnClickListener(sendNotificationAction);
    }

    private void loadUsers() {
        database.getReference().child(PATH).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                showingItems.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Users user = snap.getValue(Users.class);
                    items.add(user);
                    showingItems.add(user.getUserName());
                }
                userListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    final private View.OnClickListener addParticipants = v -> {
        userList.setHeight(ListPopupWindow.WRAP_CONTENT);
        userList.setWidth(600);
        userList.setAnchorView(v);
        userList.setAdapter(userListAdapter);

        userListAdapter.notifyDataSetChanged();
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                assignedList.add(items.get(position));
                items.remove(position);
                showingItems.remove(position);
                userList.dismiss();
                adapter.notifyDataSetChanged();
            }
        });
        userList.show();


    };


    final private View.OnClickListener sendNotificationAction = v -> {

        String title = binding.taskTitle.getText().toString();
        String desc = binding.notificationDescription.getText().toString();

        if (title.isEmpty()) {
            binding.taskTitle.setError("Cant be empty");
            return;
        } else if (desc.isEmpty()) {
            binding.notificationDescription.setError("Cant be empty");
            return;
        } else if (assignedList.isEmpty()) {
            Toast.makeText(context, "Add some participants", Toast.LENGTH_SHORT).show();
            return;
        } else {
            binding.taskTitle.setText(null);
            binding.notificationDescription.setText(null);

            binding.taskTitle.setError(null);
            binding.notificationDescription.setError(null);



            CommonUtils.sendNotificationToUser(assignedList, context, title, desc);
            assignedList.clear();
            adapter.notifyDataSetChanged();
            loadUsers();

            Toast.makeText(context, "Notification sent", Toast.LENGTH_SHORT).show();
        }

    };


}