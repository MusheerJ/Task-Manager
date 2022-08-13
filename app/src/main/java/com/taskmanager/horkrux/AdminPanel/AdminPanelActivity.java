package com.taskmanager.horkrux.AdminPanel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.taskmanager.horkrux.Activites.AssignTaskActivity;
import com.taskmanager.horkrux.Activites.MainActivity;
import com.taskmanager.horkrux.AuthNew.NewLoginActivity;
import com.taskmanager.horkrux.AuthNew.NewSignUp;
import com.taskmanager.horkrux.Models.Users;
import com.taskmanager.horkrux.R;
import com.taskmanager.horkrux.databinding.ActivityAdminPanelBinding;

public class AdminPanelActivity extends AppCompatActivity {

    private ActivityAdminPanelBinding binding;
    private final Context context = AdminPanelActivity.this;
    private int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminPanelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.dashBoardHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MainActivity.class));
            }
        });

        binding.adminMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, binding.adminMenu);
                popupMenu.getMenuInflater().inflate(R.menu.admin_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        if (menuItem.getItemId() == R.id.createTask) {
                            Intent intent = new Intent(context, AssignTaskActivity.class);
                            startActivity(intent);
                            return true;
                        }
                        if (menuItem.getItemId() == R.id.adminUserView) {
                            startActivity(new Intent(context, MainActivity.class));
                            return true;
                        }
                        if (menuItem.getItemId() == R.id.createUser) {
                            startActivity(new Intent(context, NewSignUp.class));
                            return true;
                        }

                        if (menuItem.getItemId() == R.id.admin_logout) {
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(context, NewLoginActivity.class));
                            finishAffinity();
                            Toast.makeText(context, "Logout success", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        return false;


                    }
                });
                // Showing the popup menu
                popupMenu.show();
            }
        });
        binding.teamAndroid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent teamAndroidIntent = new Intent(context, TeamMemberList.class);
                teamAndroidIntent.putExtra("requestedTeam", Users.ANDROID_DEPT);
                startActivity(teamAndroidIntent);
            }
        });

        binding.teamWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent teamAndroidIntent = new Intent(context, TeamMemberList.class);
                teamAndroidIntent.putExtra("requestedTeam", Users.WEB_DEPT);
                startActivity(teamAndroidIntent);
            }
        });

        binding.teamUiUx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent teamAndroidIntent = new Intent(context, TeamMemberList.class);
                teamAndroidIntent.putExtra("requestedTeam", Users.UI_UX_DEPT);
                startActivity(teamAndroidIntent);
            }
        });

        binding.sendNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(AdminPanelActivity.this, SendNotificationsActivity.class));
            }
        });

    }

}