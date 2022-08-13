package com.taskmanager.horkrux.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.taskmanager.horkrux.Activites.MainActivity;
import com.taskmanager.horkrux.AdminPanel.AdminPanelActivity;
import com.taskmanager.horkrux.R;

public class LoginTabfragment extends Fragment {

    EditText email, pass;
    TextView forgotpass;
    Button login;
    float v = 0;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;


    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.login_fragment, container, false);

        //Initialization
        email = root.findViewById(R.id.loginemail);
        pass = root.findViewById(R.id.loginpass);
        forgotpass = root.findViewById(R.id.forgotpass);
        login = root.findViewById(R.id.loginbutton);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();



//        animation
        email.setTranslationY(800);
        pass.setTranslationY(800);
        forgotpass.setTranslationY(800);
        login.setTranslationY(800);


        email.setAlpha(v);
        pass.setAlpha(v);
        forgotpass.setAlpha(v);
        login.setAlpha(v);


        email.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();
        pass.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(600).start();
        forgotpass.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(800).start();
        login.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(1000).start();


//        login user

//        if user is already logged in
        if (firebaseUser != null) {
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }

//        if user is not looged in
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mail = email.getText().toString().trim();
                String password = pass.getText().toString().trim();

                if(mail.isEmpty() || password.isEmpty())
                {
                    Toast.makeText(getActivity(), "All fields are required to fill",Toast.LENGTH_SHORT).show();
                }
                else
                {

                    firebaseAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                checkMailVerification();
                            }
                            else
                            {
                                Toast.makeText(getActivity(),"Account does not exists",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        return root;

    }

    private void checkMailVerification()
    {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser.isEmailVerified() == true)
        {
            Toast.makeText(getActivity(),"logged in",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), AdminPanelActivity.class));
            getActivity().finish();
        }
        else
        {
            Toast.makeText(getActivity(),"Email Not verified",Toast.LENGTH_SHORT).show();
        }
    }

}
