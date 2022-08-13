package com.taskmanager.horkrux.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taskmanager.horkrux.Activites.AssignTaskActivity;
import com.taskmanager.horkrux.Activites.MainActivity;
import com.taskmanager.horkrux.Adapters.TaskAdapter;
import com.taskmanager.horkrux.Models.Task;
import com.taskmanager.horkrux.Models.Users;
import com.taskmanager.horkrux.R;
import com.taskmanager.horkrux.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Collections;

public class HomeFragment extends Fragment {


    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    final String[] taskCategories = {"ALL Tasks", "TO-DO", "IN PROGRESS", "DONE"};
    final int ALL = 0, TODO = 1, IN_PROGRESS = 2, DONE = 3;
    private ArrayAdapter taskCategoryAdapter;
    private TaskAdapter taskAdapter;
    private String currentUserId;
    private ArrayList<Task> userTasks;
    private Users user;


    public HomeFragment() {
    }

    public HomeFragment(Users user) {
        this.user = user;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        userTasks = new ArrayList<>();

        binding.homeProgress.setVisibility(View.VISIBLE);

        if (user != null) {
            binding.selectedUserView.setVisibility(View.VISIBLE);
            binding.selectedUserName.setText(user.getUserName());

            taskAdapter = new TaskAdapter(getContext(), userTasks, "");
            currentUserId = user.getFireuserid();

            binding.selectedUserName.setText(user.getUserName());
            binding.selectedUserMail.setText(user.getUserEmail());
            Glide.with(getContext()).load(user.getUserProfile()).placeholder(R.drawable.place_holder).into(binding.selectedUserImage);

        } else {
            binding.selectedUserView.setVisibility(View.GONE);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 200, 0, 0);
            binding.linearLayout.setLayoutParams(params);

            taskAdapter = new TaskAdapter(getContext(), userTasks, null);

            currentUserId = FirebaseAuth.getInstance().getUid();
        }

        setValues();
        loadTask();


        return binding.getRoot();
    }


    private void setValues() {
        taskCategoryAdapter = new ArrayAdapter(getContext(), R.layout.home_list_item, taskCategories);
        binding.taskCategory.setAdapter(taskCategoryAdapter);

        binding.taskCategory.setOnItemClickListener(taskCategoryListener);

        binding.userTaskRecylerView.setAdapter(taskAdapter);
        binding.userTaskRecylerView.setLayoutManager(new LinearLayoutManager(getContext()));


    }

    View.OnClickListener assignTaskBtnAction = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getContext(), AssignTaskActivity.class));
        }
    };

    // loading user tasks
    void loadTask() {


        FirebaseDatabase.getInstance().getReference().child("all-tasks")
                .child("user-tasks")
                .child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int todo = 0;
                        int inProgress = 0;
                        int done = 0;
                        int all;
                        userTasks.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Task task = snap.getValue(Task.class);
                            assert task != null;
                            if (task.getTaskStatus().equals(Task.TODO)) {
                                todo++;
                            } else if (task.getTaskStatus().equals(Task.IN_PROGRESS)) {
                                inProgress++;
                            } else {
                                done++;
                            }
                            userTasks.add(task);
                        }
                        Collections.reverse(userTasks);

//                        Toast.makeText(getContext(), "" + todo, Toast.LENGTH_SHORT).show();

                        try {

                            MainActivity.count.setTodo(todo);
                            MainActivity.count.setInProgress(inProgress);
                            MainActivity.count.setDone(done);
                            MainActivity.count.setAll(done + todo + inProgress);
                        } catch (Exception e) {

                        }


//                        binding.selectedUserMail.setText("TODO : " + count[0] + "\n" + "IN PROGRESS : " + count[1] + "\n" + "DONE : " + count[2]);

                        taskAdapter.notifyDataSetChanged();

                        try {

                            binding.homeProgress.setVisibility(View.GONE);

                            if (userTasks.isEmpty()) {
                                binding.taskEmptyMsg.setText("No tasks available");
                                binding.taskEmptyMsg.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                binding.taskEmptyMsg.setVisibility(View.GONE);

                            }
                        } catch (Exception e) {

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }


    // for applying filter
    AdapterView.OnItemClickListener taskCategoryListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == ALL) {
                taskAdapter.applyFilter(userTasks);
            } else if (position == TODO) {
                ArrayList<Task> todoTasks = new ArrayList<>();
                for (Task task : userTasks) {
                    if (task.getTaskStatus().equals(Task.TODO)) {
                        todoTasks.add(task);
                    }
                }
                taskAdapter.applyFilter(todoTasks);
            } else if (position == IN_PROGRESS) {
                ArrayList<Task> inProgressTasks = new ArrayList<>();
                for (Task task : userTasks) {
                    if (task.getTaskStatus().equals(Task.IN_PROGRESS)) {
                        inProgressTasks.add(task);
                    }
                }
                taskAdapter.applyFilter(inProgressTasks);
            } else {
                ArrayList<Task> doneTasks = new ArrayList<>();
                for (Task task : userTasks) {
                    if (task.getTaskStatus().equals(Task.DONE)) {
                        doneTasks.add(task);
                    }
                }
                taskAdapter.applyFilter(doneTasks);
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}