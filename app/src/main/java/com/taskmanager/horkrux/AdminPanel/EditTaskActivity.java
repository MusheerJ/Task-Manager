package com.taskmanager.horkrux.AdminPanel;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListPopupWindow;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.FirebaseDatabase;
import com.taskmanager.horkrux.Adapters.UserAdapter;
import com.taskmanager.horkrux.Constants;
import com.taskmanager.horkrux.Models.Task;
import com.taskmanager.horkrux.Models.Users;
import com.taskmanager.horkrux.R;
import com.taskmanager.horkrux.databinding.ActivityEditTaskBinding;
import com.taskmanager.horkrux.databinding.AssignParticipantsPopupBinding;

import java.util.ArrayList;

public class EditTaskActivity extends AppCompatActivity {

    final private Context context = EditTaskActivity.this;
    private ActivityEditTaskBinding binding;
    private Task selectedTask;
    private UserAdapter userAdapter;
    private ListPopupWindow userList;
    private FirebaseDatabase database;
    private ArrayList<Users> assignedUserList;
    private AlertDialog showUsers;
    public static ArrayList<Users> items;
    public static ArrayList<String> showingItems;
    private AssignParticipantsPopupBinding popupBinding;
    private AdminUserAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        selectedTask = (Task) getIntent().getSerializableExtra("selectedTask");
        assignedUserList = new ArrayList<>();


        createDialog();
        items = new ArrayList<>();
        showingItems = new ArrayList<>();
        initUtils();


        binding.assignTaskToUserBtnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showUsers.show();
            }
        });


    }

    private void createDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.assign_participants_popup, null);
        popupBinding = AssignParticipantsPopupBinding.bind(view);
        popupBinding.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new AdminUserAdapter(context, selectedTask.getGrpTask(), "EditTask");
        popupBinding.recyclerView.setAdapter(adapter);

        showUsers = new AlertDialog.Builder(context)
                .setTitle("Add Participants")
                .setView(popupBinding.getRoot())
                .create();

        adapter.notifyDataSetChanged();
    }

    private void initUtils() {


        binding.taskTitleAdmin.setText(selectedTask.getTaskTitle());
        binding.taskDescAdmin.setText(selectedTask.getTaskDescription());
        binding.startDate.setText(selectedTask.getTaskAssigned());
        binding.dueDate.setText(selectedTask.getTaskDeadline());
        markStatus();
        markPriority();


        userAdapter = new UserAdapter(context, selectedTask.getGrpTask(), Constants.SubmitTask);
        binding.participantsAdmin.setLayoutManager(new GridLayoutManager(context, 2));
        binding.participantsAdmin.setAdapter(userAdapter);

        userAdapter.notifyDataSetChanged();
    }

    private void markPriority() {
        if (selectedTask.getTaskPriority().equals(Task.HIGH)) {
            binding.highAdmin.setCheckable(true);
            binding.highAdmin.setChecked(true);
            return;
        }
        if (selectedTask.getTaskPriority().equals(Task.MEDIUM)) {
            binding.mediumAdmin.setCheckable(true);
            binding.mediumAdmin.setChecked(true);
            return;
        }
        if (selectedTask.getTaskPriority().equals(Task.LOW)) {
            binding.lowAdmin.setCheckable(true);
            binding.lowAdmin.setChecked(true);
        }
    }

    private void markStatus() {
//        Toast.makeText(context, selectedTask.getTaskStatus(), Toast.LENGTH_SHORT).show();
        if (selectedTask.getTaskStatus().equals(Task.TODO)) {
            binding.TodoAdmin.setCheckable(true);
            binding.TodoAdmin.setChecked(true);
            return;
        }
        if (selectedTask.getTaskStatus().equals(Task.IN_PROGRESS)) {
            binding.inProgressAdmin.setCheckable(true);
            binding.inProgressAdmin.setChecked(true);
            return;
        }
        if (selectedTask.getTaskStatus().equals(Task.DONE)) {
            binding.DoneAdmin.setCheckable(true);
            binding.DoneAdmin.setChecked(true);
        }

    }
}