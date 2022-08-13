package com.taskmanager.horkrux.Activites;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.taskmanager.horkrux.Models.Users;
import com.taskmanager.horkrux.R;
import com.taskmanager.horkrux.databinding.ActivityProfileBinding;

public class Profile extends AppCompatActivity {

    private ActivityProfileBinding binding;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private final Context context = Profile.this;
    private String USER_PATH;
    private String m_Text;
    private Uri selectedImage = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        initialize
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        USER_PATH = "Users/" + firebaseAuth.getUid() + "/";


//        Toast.makeText(this,MainActivity.count.getTodo()+"" , Toast.LENGTH_SHORT).show();
        binding.todoCount.setText(String.valueOf(MainActivity.count.getTodo()));
        binding.inProgressCount.setText(String.valueOf(MainActivity.count.getInProgress()));
        binding.doneCount.setText(String.valueOf(MainActivity.count.getDone()));

        binding.editUserName.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Enter User Name");


            final EditText input = new EditText(context);

            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", (dialog, which) -> {
                m_Text = input.getText().toString();
                if (m_Text.isEmpty()) {
                    Toast.makeText(context, "Username cant be empty!!", Toast.LENGTH_SHORT).show();
                } else {
                    database.getReference(USER_PATH).child("userName").setValue(m_Text).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(context, "Username updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        });


        binding.profileImage.setOnClickListener(v -> ImagePicker.with(Profile.this)
                .cropSquare()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start());

        binding.updateProfile.setOnClickListener(view -> {

            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Updating profile please wait ...");
            dialog.show();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference reference = storage.getReference().child("usersprofiles")
                    .child(firebaseAuth.getUid());

            reference.putFile(selectedImage).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            database.getReference().child("Users").child(firebaseAuth.getUid()).child("userProfile").setValue(uri.toString());

                            Toast.makeText(Profile.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            binding.updateProfile.setVisibility(View.GONE);
                        }
                    });
                } else {
                    Toast.makeText(Profile.this, task.getException().toString(), Toast.LENGTH_SHORT).show();

                }
            });
        });

        binding.backButton.setOnClickListener(view -> {
            finish();
        });


        database.getReference().child(USER_PATH).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                binding.profileName.setText(user.getUserName());
                binding.profileEmail.setText(user.getUserEmail());
                binding.userProfileCategory.setText(user.getUserDept());
                Glide.with(Profile.this).load(user.getUserProfile()).placeholder(R.drawable.place_holder).into(binding.profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (data.getData() != null) {
                selectedImage = data.getData();
                binding.profileImage.setImageURI(selectedImage);
                binding.updateProfile.setVisibility(View.VISIBLE);
            }


        }
    }
}