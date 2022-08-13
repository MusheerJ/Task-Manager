package com.taskmanager.horkrux.Activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.taskmanager.horkrux.AuthNew.NewLoginActivity;
import com.taskmanager.horkrux.CommonUtils;
import com.taskmanager.horkrux.Models.Count;
import com.taskmanager.horkrux.Models.Users;
import com.taskmanager.horkrux.R;
import com.taskmanager.horkrux.databinding.ActivityMainBinding;
import com.taskmanager.horkrux.databinding.NavHeaderMainBinding;
import com.taskmanager.horkrux.ui.home.HomeFragment;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public static Count count;
    private HomeFragment homeFragment;
    private FragmentTransaction fragmentTransaction;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private NavHeaderMainBinding navHeaderMainBinding;
    private NavigationView navigationView;
    private String USER_PATH;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        count = new Count(0, 0, 0, 0);

        //subscribe to notification
        USER_PATH = "Users/" + auth.getUid() + "/";
        String topic = "/topics/" + auth.getUid();
        FirebaseMessaging.getInstance().subscribeToTopic(topic);

//        getSupportActionBar().hide();
//

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        homeFragment = new HomeFragment();
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);


        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.currentActivity, homeFragment);
        transaction.commit();
        // to make the Navigation drawer icon always appear on the action bar
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.navDrawMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        binding.userNotifications.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), ViewNotificationsActivity.class));
        });
        setNavigation();


    }

    private void setNavigation() {
        navigationView = findViewById(R.id.navView);
        navHeaderMainBinding = NavHeaderMainBinding.bind(navigationView.getHeaderView(0));

        navHeaderMainBinding.cancelNavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        database.getReference().child(USER_PATH).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                navHeaderMainBinding.loggedInUserName.setText(user.getUserName());
                navHeaderMainBinding.loggedInUserMail.setText(user.getUserEmail());
                Glide.with(MainActivity.this).load(user.getUserProfile()).placeholder(R.drawable.place_holder).into(navHeaderMainBinding.loggedInUserProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                if (item.getItemId() == R.id.nav_home) {
                    drawerLayout.closeDrawers();
                    fragment = new HomeFragment();
                    return true;
                }
                if (item.getItemId() == R.id.nav_profile) {
//                    fragment = new GalleryFragment();
                    Intent profileIntent = new Intent(MainActivity.this, Profile.class);
//                    profileIntent.putExtra("count", count);
                    startActivity(profileIntent);
                    drawerLayout.closeDrawers();
                    return false;
                }
                if (item.getItemId() == R.id.nav_sign_out) {
                    auth.signOut();
                    CommonUtils.showToast(getApplicationContext(), "Signed out");
                    startActivity(new Intent(MainActivity.this, NewLoginActivity.class));

                    finishAffinity();
                    return false;
                }

                if (item.getItemId() == R.id.credits) {
                    startActivity(new Intent(MainActivity.this, AboutUs.class));
                    return false;
                }

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.currentActivity, fragment);
                transaction.commit();
                drawerLayout.closeDrawers();

                return true;
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        actionBarDrawerToggle.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }
}