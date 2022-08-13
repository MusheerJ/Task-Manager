package com.taskmanager.horkrux.AuthNew;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.taskmanager.horkrux.Models.Users;
import com.taskmanager.horkrux.R;
import com.taskmanager.horkrux.databinding.ActivityNewSignUpBinding;

public class NewSignUp extends AppCompatActivity {

    private ActivityNewSignUpBinding binding;

    final String[] deptCategory = {Users.ANDROID_DEPT, Users.WEB_DEPT, Users.UI_UX_DEPT, Users.MBA_DEPT};
    final int Android_Dev = 0, Web_Dev = 1, UI_UX = 2, MBA = 3;
    private ArrayAdapter fieldCategoryAdapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private Users user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewSignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //firebase declarations
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        user = new Users();


//        load field
        fieldCategoryAdapter = new ArrayAdapter(getApplicationContext(), R.layout.home_list_item, deptCategory);
        binding.createUserField.setAdapter(fieldCategoryAdapter);

        binding.createUserField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                user.setUserDept(deptCategory[position]);
            }
        });


        binding.createUserLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = binding.createUserEmail.getText().toString().trim();
                String password = binding.createUserPass.getText().toString().trim();
                String username = binding.crateUserName.getText().toString().trim();


                if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Fill All Fields", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 7) {

                    Toast.makeText(getApplicationContext(), "Must be 8 characters or more", Toast.LENGTH_SHORT).show();

                } else if (binding.createUserField.getText().toString().equals("Select Field")) {
                    Toast.makeText(getApplicationContext(), "Please select a field", Toast.LENGTH_SHORT).show();
                } else {
                    user.setUserEmail(binding.createUserEmail.getText().toString().trim());
                    user.setUserPass(binding.createUserPass.getText().toString().trim());
                    user.setUserName(binding.crateUserName.getText().toString().trim());
                    user.setUserProfile(Users.NO_PROFILE);

                    //register user
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Resgistration Succesfull", Toast.LENGTH_SHORT).show();
                                sendEmailVerification();
                                user.setFireuserid(firebaseAuth.getUid());
                                updateUser(user);
//                                resetAttributes();


                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

            }
        });

    }

    private void resetAttributes() {

        binding.crateUserName.setText("");
        binding.createUserEmail.setText(null);
        binding.createUserPass.setText(null);
        binding.createUserField.setText(null);
    }


    //send verification email
    private void sendEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "Verification e-mail Sent", Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();

                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Failed to send verification e-mail", Toast.LENGTH_SHORT).show();
        }
    }

    //  update the user in database
    private void updateUser(Users username) {
        firebaseDatabase.getReference().child("Users").child(firebaseAuth.getUid()).setValue(username).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
//                    Toast.makeText(getApplicationContext(), "user is in database", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(NewSignUp.this, NewLoginActivity.class));
                    Log.d("result", "onComplete: " + task.getException());
                } else {
                    Toast.makeText(getApplicationContext(), "user is not add in database", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}