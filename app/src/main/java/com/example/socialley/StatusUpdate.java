package com.example.socialley;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.socialley.databinding.ActivityStatusUpdateBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class StatusUpdate extends AppCompatActivity {

    ActivityStatusUpdateBinding binding;
    FirebaseStorage firebaseStorage;
    FirebaseAuth mAuth;
    FirebaseDatabase database;


    
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityStatusUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseStorage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        database.getReference().child("users").child(mAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User users= snapshot.getValue(User.class);
//                        Picasso.get().load(users.getProfilePic())
//                                .placeholder(R.drawable.profile)
//                                .into(binding.profileImage);
//                        if (!users.getProfilePic().equals("")) {
//                            try {
//                                Glide.with(context).load(users.getProfilePic()).into(binding.profileImage);
//                            }
//                            catch (Exception e) {
//
//                            }
//                        }

                        binding.editStatus.setText(users.getStatus());
                        binding.editUserName.setText(users.getUsername());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(StatusUpdate.this,HomeActivity.class);
                startActivity(intent);
            }
        });

        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = binding.editStatus.getText().toString();
                String userName = binding.editUserName.getText().toString();

                HashMap<String,Object> objectHashMap = new HashMap<>();
                objectHashMap.put("username",userName);
                objectHashMap.put("status",status);

                database.getReference().child("users").child(mAuth.getUid())
                        .updateChildren(objectHashMap);

                Toast.makeText(StatusUpdate.this,"Status Updated",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(StatusUpdate.this,HomeActivity.class);
                startActivity(intent);

            }
        });


    }

}