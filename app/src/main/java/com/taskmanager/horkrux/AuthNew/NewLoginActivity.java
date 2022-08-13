package com.taskmanager.horkrux.AuthNew;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.taskmanager.horkrux.Activites.MainActivity;
import com.taskmanager.horkrux.AdminPanel.AdminPanelActivity;
import com.taskmanager.horkrux.databinding.ActivityNewLoginBinding;

public class NewLoginActivity extends AppCompatActivity {

    private ActivityNewLoginBinding binding;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        Initialization
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        dialog = new ProgressDialog(this);

        dialog.setMessage("Validating login details please wait !");


        firebaseAuth.signInWithEmailAndPassword("marcus.codes.05@gmail.com", "12345678").addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                startActivity(new Intent(NewLoginActivity.this, AdminPanelActivity.class));
            }
        });

//        login user

        //        if user is already logged in
        if (firebaseUser != null && firebaseUser.isEmailVerified()) {
            startActivity(new Intent(getApplicationContext(), AdminPanelActivity.class));
            finish();
        }

        //        if user is not looged in


        binding.signupbutton.setOnClickListener(view -> {
            startActivity(new Intent(this, NewSignUp.class));

        });
        binding.loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mail = binding.loginEmail.getText().toString().trim();
                String password = binding.loginPassword.getText().toString().trim();

                if (mail.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "All fields are required to fill", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 7) {

                    Toast.makeText(getApplicationContext(), "Password must be 8 characters", Toast.LENGTH_SHORT).show();

                } else {

                    dialog.show();

                    firebaseAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                checkMailVerification();
                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    });

                    Log.d("", "onClick: " + "1223");
                }


            }
        });


    }

    private void checkMailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser.isEmailVerified() == true) {

            Toast.makeText(getApplicationContext(), "Logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            dialog.dismiss();
            finish();
        } else {
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), "User Not verified", Toast.LENGTH_SHORT).show();
        }
    }

}