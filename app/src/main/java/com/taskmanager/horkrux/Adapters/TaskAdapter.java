package com.taskmanager.horkrux.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.taskmanager.horkrux.Activites.AssignTaskActivity;
import com.taskmanager.horkrux.Activites.SubmitTaskActivity;
import com.taskmanager.horkrux.Models.Task;
import com.taskmanager.horkrux.R;
import com.taskmanager.horkrux.databinding.TaskLayoutBinding;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private final Context context;
    private ArrayList<Task> tasks;
    private final String from;

    public TaskAdapter(Context context, ArrayList<Task> tasks, String from) {
        this.context = context;
        this.tasks = tasks;
        this.from = from;
    }

    @NonNull
    @Override
    public TaskAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_layout, parent, false);
        return new TaskAdapter.TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.TaskViewHolder holder, int position) {

        String priority = tasks.get(position).getTaskPriority();
        if (priority.equals(Task.LOW)) {
            holder.binding.taskItem.setCardBackgroundColor(context.getResources().getColor(R.color.low_green));
            holder.binding.startingDate.setTextColor(context.getResources().getColor(R.color.dark_green));
            holder.binding.deadlineDate.setTextColor(context.getResources().getColor(R.color.dark_green));
            holder.binding.priorityShow.setChipBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.dark_green)));
            holder.binding.priorityShow.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
            holder.binding.userTaskTitle.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.title_text)));
            holder.binding.userTaskDescription.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.desc_text)));
        } else if (priority.equals(Task.MEDIUM)) {
            holder.binding.taskItem.setCardBackgroundColor(context.getResources().getColor(R.color.low_yellow));
            holder.binding.startingDate.setTextColor(context.getResources().getColor(R.color.dark_yellow));
            holder.binding.deadlineDate.setTextColor(context.getResources().getColor(R.color.dark_yellow));
            holder.binding.priorityShow.setChipBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.dark_yellow)));
            holder.binding.priorityShow.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
            holder.binding.userTaskTitle.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.title_text)));
            holder.binding.userTaskDescription.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.desc_text)));
        } else {
            holder.binding.taskItem.setCardBackgroundColor(context.getResources().getColor(R.color.low_red));
            holder.binding.startingDate.setTextColor(context.getResources().getColor(R.color.dark_red));
            holder.binding.deadlineDate.setTextColor(context.getResources().getColor(R.color.dark_red));
            holder.binding.priorityShow.setChipBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.dark_red)));
            holder.binding.priorityShow.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
            holder.binding.userTaskTitle.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.title_text)));
            holder.binding.userTaskDescription.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.desc_text)));
        }


        holder.binding.userTaskTitle.setText(tasks.get(position).getTaskTitle());
        holder.binding.userTaskDescription.setText(tasks.get(position).getTaskDescription());
        holder.binding.startingDate.setText(tasks.get(position).getTaskAssigned());
        holder.binding.deadlineDate.setText(tasks.get(position).getTaskDeadline());
        holder.binding.priorityShow.setText(tasks.get(position).getTaskPriority());


        if (from == null) {
            holder.binding.taskItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SubmitTaskActivity.class);
                    intent.putExtra("selectedTask", tasks.get(holder.getAdapterPosition()));
                    context.startActivity(intent);
                }
            });
        } else {
            holder.binding.taskItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AssignTaskActivity.class);
                    intent.putExtra("selectedTask", tasks.get(holder.getAdapterPosition()));
                    context.startActivity(intent);
                }
            });
        }


    }


    public void applyFilter(ArrayList<Task> filteredTask) {
        tasks = filteredTask;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TaskLayoutBinding binding;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = binding.bind(itemView);
        }
    }
}
