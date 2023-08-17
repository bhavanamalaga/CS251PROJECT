package com.example.socialley;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.provider.ContactsContract;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CustomAdapter adapter;
    List<User> usersList;
    FirebaseAuth firebaseAuth;
    ProgressDialog pd;
    String type;
    String postID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Bundle extras = getIntent().getExtras();
        type = "0";
        if (extras != null) {
            if (extras.containsKey("type")) {
                type = extras.getString("type");
                if (type.equals("1")) {
                    postID = extras.getString("pid");
                }
            }
        }
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(UserListActivity.this));
        usersList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(UserListActivity.this);
        pd.setMessage("Loading");
        pd.show();
        getAllUsers();
        pd.dismiss();
    }

    private void getAllUsers() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (type.equals("0")) {
            DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("users");
            usersReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    usersList.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        User user = dataSnapshot1.getValue(User.class);
                        if (user.getUserId() != null && !user.getUserId().equals(firebaseUser.getUid())) {
                            usersList.add(user);
                        } else {

                        }
                    }
                    adapter = new CustomAdapter(UserListActivity.this, usersList);
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else if (type.equals("1")) {
            if (postID != null) {
                Query query = FirebaseDatabase.getInstance().getReference("Likes").child(postID);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            String userString = snapshot1.getKey().toString();
                            Query userQuery = FirebaseDatabase.getInstance().getReference("users").child(userString);
                            usersList.clear();
                            userQuery.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User user = snapshot.getValue(User.class);
                                    usersList.add(user);
                                    adapter = new CustomAdapter(UserListActivity.this, usersList);
                                    recyclerView.setAdapter(adapter);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
        else if (type.equals("2")) {
            Query query = FirebaseDatabase.getInstance().getReference("Friends").child(firebaseAuth.getUid());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String userString = snapshot1.getKey().toString();
                        Query userQuery = FirebaseDatabase.getInstance().getReference("users").child(userString);
                        usersList.clear();
                        userQuery.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                usersList.add(user);
                                adapter = new CustomAdapter(UserListActivity.this, usersList);
                                recyclerView.setAdapter(adapter);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}