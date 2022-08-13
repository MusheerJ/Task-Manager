package com.taskmanager.horkrux.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.taskmanager.horkrux.R;
import com.taskmanager.horkrux.Models.Users;

public class sign_up_fragment extends Fragment {

    EditText uemail, uname, upass;
    Button signup;
    float v = 0;

    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    Users user = new Users();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.signup_fragment,container,false);


        //Declarations
        uemail = root.findViewById(R.id.signupemail);
        uname = root.findViewById(R.id.fullname);
        upass = root.findViewById(R.id.signpassword);
        signup = root.findViewById(R.id.signupbutton);

        //firebase declarations
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        //animation and stuff
        uemail.setTranslationY(800);
        uname.setTranslationY(800);
        upass.setTranslationY(800);
        signup.setTranslationY(800);

        uemail.setAlpha(v);
        uname.setAlpha(v);
        upass.setAlpha(v);
        signup.setAlpha(v);

        uemail.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();
        uname.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(600).start();
        upass.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(800).start();
        signup.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(1000).start();

        //firebase connection
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = uemail.getText().toString().trim();
                String password = upass.getText().toString().trim();
                String username = uname.getText().toString().trim();

                if(email.isEmpty() || password.isEmpty() || username.isEmpty())
                {
                    Toast.makeText(getActivity(),"Fill All Fields",Toast.LENGTH_SHORT).show();
                }else if (password.length() < 7) {

                    Toast.makeText(getActivity(),"Must be 8 characters or more",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    user.setUserEmail(uemail.getText().toString().trim());
                    user.setUserPass(upass.getText().toString().trim());
                    user.setUserName(uname.getText().toString().trim());

                    //register user
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(getActivity(),"Resgistration Succesfull",Toast.LENGTH_SHORT).show();
                               sendEmailVerification();
                               user.setFireuserid(firebaseAuth.getUid());
                                updateUser(user);

                            }
                            else{
                                Toast.makeText(getActivity(),"Resgistration Unsuccesfull",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });

        return root;
    }
//  update the user in database
    private void updateUser(Users username) {
        firebaseDatabase.getReference().child("Users").child(firebaseAuth.getUid()).setValue(username).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                Toast.makeText(getActivity(),"user is in database",Toast.LENGTH_SHORT).show();
                Log.d("result", "onComplete: " + task.getException());
                }
                else{
                Toast.makeText(getActivity(),"user is not add in database",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    //send verification email
    private void sendEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null)
        {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getActivity(),"Verification e-mail Sent",Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();

                }
            });
        }
        else{
            Toast.makeText(getActivity(),"Failed to send verification e-mail",Toast.LENGTH_SHORT).show();
        }
    }

}
