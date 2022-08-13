package com.taskmanager.horkrux.AdminPanel;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.taskmanager.horkrux.Activites.AssignTaskActivity;
import com.taskmanager.horkrux.Activites.Profile;
import com.taskmanager.horkrux.Models.Users;
import com.taskmanager.horkrux.R;
import com.taskmanager.horkrux.databinding.AdminUserListBinding;

import java.util.ArrayList;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.AdminUserViewHolder> {
    Context context;
    ArrayList<Users> users;
    ArrayList<Users> backUsers;
    String from;


    public AdminUserAdapter(Context context, ArrayList<Users> users, String from) {
        this.context = context;
        this.users = users;
        this.backUsers = users;
        this.from = from;
    }

    @NonNull
    @Override
    public AdminUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_user_list, parent, false);
        return new AdminUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminUserViewHolder holder, int position) {

        holder.binding.adminUserName.setText(users.get(position).getUserName());
        Glide.with(context).load(users.get(position).getUserProfile()).into(holder.binding.profileImage);
        holder.binding.userDetailSee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserTasksActivity.class);
                intent.putExtra("selectedUser", users.get(holder.getAdapterPosition()));
                context.startActivity(intent);

            }
        });


    }

    public void reset() {
    }

    public void removeItem(int poi) {
        AssignTaskActivity.showingItems.add(users.get(poi).getUserName());
        AssignTaskActivity.items.add(users.remove(poi));

        notifyItemRemoved(poi);
        notifyItemRangeChanged(poi, users.size());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class AdminUserViewHolder extends RecyclerView.ViewHolder {

        AdminUserListBinding binding;

        public AdminUserViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = AdminUserListBinding.bind(itemView);
        }
    }
}

