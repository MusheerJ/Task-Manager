package com.taskmanager.horkrux.Activites;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListPopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.taskmanager.horkrux.Adapters.UserAdapter;
import com.taskmanager.horkrux.CommonUtils;
import com.taskmanager.horkrux.Models.Task;
import com.taskmanager.horkrux.Models.Users;
import com.taskmanager.horkrux.Notification.ApiUtils;
import com.taskmanager.horkrux.Notification.NotificationData;
import com.taskmanager.horkrux.Notification.PushNotification;
import com.taskmanager.horkrux.R;
import com.taskmanager.horkrux.databinding.ActivityAssignTaskBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssignTaskActivity extends AppCompatActivity {
    final Context context = AssignTaskActivity.this;
    final String USER_TASK_PATH = "all-tasks/user-tasks";
    final String USERS_PATH = "Users";
    final String PROGRESS_MESSAGE = "Assigning Task";

    private boolean isTaskSubmitted = false;
    private ActivityAssignTaskBinding binding;
    private Task task;
    private FirebaseDatabase database;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    public static int count = 0;

    private Task selectedTask;
    private MaterialDatePicker.Builder materialDateBuilder;
    private MaterialDatePicker startDatePicker;
    private MaterialDatePicker dueDatePicker;
    private ListPopupWindow userList;
    private UserAdapter adapter;
    private ArrayAdapter userListAdapter;
    private ArrayList<Users> assignedList;
    public static ArrayList<Users> items;
    public static ArrayList<String> showingItems;
    private boolean isEdit = false;


    private ProgressDialog progressDialog;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAssignTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        Objects.requireNonNull(getSupportActionBar()).hide();

        selectedTask = (Task) getIntent().getSerializableExtra("selectedTask");
        if (selectedTask != null) {
            isEdit = true;
        }

        initTaskUtils();
        if (isEdit) {
            setTasKUsingSelectedTask();
        }
        loadUsers();


    }

    private void setTasKUsingSelectedTask() {
        assignedList.addAll(selectedTask.getGrpTask());
        binding.taskTitle.setText(selectedTask.getTaskTitle());
        binding.taskDescription.setText(selectedTask.getTaskDescription());
        binding.startDate.setText(selectedTask.getTaskAssigned());
        binding.dueDate.setText(selectedTask.getTaskDeadline());
        binding.taskTitle.setText(selectedTask.getTaskTitle());

        binding.textView.setVisibility(View.VISIBLE);
        binding.statusLayout.setVisibility(View.VISIBLE);

        //setting priority
        if (selectedTask.getTaskPriority().equals(Task.LOW)) {
            binding.lowPriority.setChecked(true);
        }
        if (selectedTask.getTaskPriority().equals(Task.HIGH)) {
            binding.highPriority.setChecked(true);
        }
        if (selectedTask.getTaskPriority().equals(Task.MEDIUM)) {
            binding.mediumPriority.setChecked(true);
        }

        //setting status
        if (selectedTask.getTaskStatus().equals(Task.TODO)) {
            binding.todoStatus.setCheckable(true);
            binding.todoStatus.setChecked(true);
            binding.todoStatus.setCheckable(false);

        }
        if (selectedTask.getTaskStatus().equals(Task.IN_PROGRESS)) {
            binding.inProgressStatus.setCheckable(true);
            binding.inProgressStatus.setChecked(true);
            binding.inProgressStatus.setCheckable(false);
        }
        if (selectedTask.getTaskStatus().equals(Task.DONE)) {
            binding.doneStatus.setCheckable(true);
            binding.doneStatus.setChecked(true);
            binding.doneStatus.setCheckable(false);
        }


        adapter.notifyDataSetChanged();
    }


    private void initTaskUtils() {
        //init database variables
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();

        //init variables
        materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("SELECT A DATE");
        startDatePicker = materialDateBuilder.build();
        dueDatePicker = materialDateBuilder.build();
        assignedList = new ArrayList<>();
        items = new ArrayList<>();
        showingItems = new ArrayList<>();
        userList = new ListPopupWindow(context);

        if (isEdit) {
            adapter = new UserAdapter(AssignTaskActivity.this, assignedList, selectedTask.getTaskID());
        } else {
            adapter = new UserAdapter(AssignTaskActivity.this, assignedList, null);
        }

        userListAdapter = new ArrayAdapter(context, R.layout.user_list, showingItems);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(PROGRESS_MESSAGE);


        //date picker
        binding.startDate.setOnClickListener(startDatePick);
        binding.dueDate.setOnClickListener(dueDatePick);
        dueDatePicker.addOnPositiveButtonClickListener(dueDateOnPositive);
        startDatePicker.addOnPositiveButtonClickListener(startDateOnPositive);


        //action on add button
        binding.assignTaskToUserBtn.setOnClickListener(assignUserToTask);
        binding.taskAssignTo.setLayoutManager(new GridLayoutManager(AssignTaskActivity.this, 2));
        binding.taskAssignTo.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //action on submit task btn
        binding.submitTask.setOnClickListener(assignTask);

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    // action on submit task
    View.OnClickListener assignTask = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (binding.taskTitle.getText().toString().isEmpty() || binding.taskDescription.getText().toString().isEmpty()) {
                Toast.makeText(context, "Please fill all details", Toast.LENGTH_SHORT).show();
                return;

            }

            if (binding.priorityGroup.getCheckedChipId() == -1) {
                Toast.makeText(context, "Please select priority", Toast.LENGTH_SHORT).show();
                return;
            }

            if (assignedList.isEmpty()) {
                Toast.makeText(context, "Please Assign task to someone", Toast.LENGTH_SHORT).show();
                return;
            }

            if (binding.startDate.getText().toString().equals("Pick Start Date ....")) {
                Toast.makeText(context, "Please Pick start date", Toast.LENGTH_SHORT).show();
                return;

            }

            if (binding.dueDate.getText().toString().equals("Pick Due Date ....")) {
                Toast.makeText(context, "Please Pick a due date", Toast.LENGTH_SHORT).show();
                return;

            }


            //assign task values
            task = new Task();
            if (isEdit) {
                task.setTaskID(selectedTask.getTaskID());
            } else {
                task.setTaskID(CommonUtils.generateId());
            }
            task.setTaskTitle(binding.taskTitle.getText().toString());
            task.setTaskDescription(binding.taskDescription.getText().toString());
            task.setTaskPriority(getSelectedPriority());
            task.setGrpTask(assignedList);
            task.setTaskAssigned(binding.startDate.getText().toString());
            task.setTaskDeadline(binding.dueDate.getText().toString());
            task.setTaskStatus(Task.TODO);

            //add task to database
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setMessage("Do you really want to Assign this task");
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            progressDialog.show();
                            addTaskToDatabase();
                            CommonUtils.sendNotificationToUser(task, context);
                            isTaskSubmitted = true;
                        }
                    });
            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();


        }
    };

    private void sendNotificationToUser() {
        String topic = "/topics/" + task.getGrpTask().get(0).getFireuserid();
        NotificationData data = new NotificationData();
        data.setNotificationTitle(task.getTaskTitle());
        data.setNotificationMessage(task.getTaskDescription());
        PushNotification notification = new PushNotification(data, topic);

        ApiUtils.getClient().sendNotification(notification).enqueue(new Callback<PushNotification>() {
            @Override
            public void onResponse(Call<PushNotification> call, Response<PushNotification> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "SUCCESS", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PushNotification> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }


    //add data to database
    synchronized private void addTaskToDatabase() {

        for (Users user : task.getGrpTask()) {
            String path = USER_TASK_PATH + "/" + user.getFireuserid() + "/" + task.getTaskID();
            database.getReference()
                    .child(path)
                    .setValue(task)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        synchronized public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                            if (count == assignedList.size() - 1) {

                                if (!isEdit) {

                                    resetAllInputs();
                                }
                                Toast.makeText(context, "Task Assigned", Toast.LENGTH_SHORT).show();
                            } else {

                                count++;
                            }


                        }
                    });

            progressDialog.dismiss();


        }


    }

    void resetAllInputs() {
        binding.taskTitle.setText(null);
        binding.taskDescription.setText(null);
        binding.highPriority.setChecked(false);
        binding.mediumPriority.setChecked(false);
        binding.lowPriority.setChecked(false);
        assignedList.clear();
        adapter.notifyDataSetChanged();
        binding.startDate.setText(null);
        binding.dueDate.setText(null);
        binding.taskTitle.requestFocus();

        loadUsers();
        count = 0;


    }

    //action when click on + button
    View.OnClickListener assignUserToTask = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            userList.setHeight(ListPopupWindow.WRAP_CONTENT);
            userList.setWidth(600);
            userList.setAnchorView(v);
            userList.setAdapter(userListAdapter);

            userListAdapter.notifyDataSetChanged();
            userList.setOnItemClickListener(userClicked);
            userList.show();

        }
    };

    //load users from database
    private void loadUsers() {
        database.getReference().child(USERS_PATH).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    items.clear();
                    showingItems.clear();

                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Users user = snap.getValue(Users.class);
                        boolean add = true;
                        for (Users users : assignedList) {
                            if (users.getFireuserid().equals(user.getFireuserid())) {
                                add = false;
                                break;
                            }
                        }
                        if (add) {
                            items.add(user);
                            showingItems.add(user.getUserName());
                        }

                    }


                    userListAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.d("TAG", "onDataChange: ");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //action when user from list is clicked
    AdapterView.OnItemClickListener userClicked = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            assignedList.add(items.get(position));
            items.remove(position);
            showingItems.remove(position);
            userList.dismiss();
            adapter.notifyDataSetChanged();
        }
    };

    //action when pick start date is clicked
    View.OnClickListener startDatePick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
        }
    };


    //action when pick due date is clicked
    View.OnClickListener dueDatePick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dueDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
        }
    };

    // shows the calender
    MaterialPickerOnPositiveButtonClickListener startDateOnPositive = new MaterialPickerOnPositiveButtonClickListener() {
        @Override
        public void onPositiveButtonClick(Object selection) {
            binding.startDate.setText(startDatePicker.getHeaderText());
        }
    };


    //shows the calender
    MaterialPickerOnPositiveButtonClickListener dueDateOnPositive = new MaterialPickerOnPositiveButtonClickListener() {
        @Override
        public void onPositiveButtonClick(Object selection) {
            binding.dueDate.setText(dueDatePicker.getHeaderText());
        }
    };


    //generate task ID



    // for getting the selected priority
    private String getSelectedPriority() {
        if (binding.highPriority.isChecked()) {
            return "high";
        } else if (binding.mediumPriority.isChecked()) {
            return "medium";
        } else {
            return "low";
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}