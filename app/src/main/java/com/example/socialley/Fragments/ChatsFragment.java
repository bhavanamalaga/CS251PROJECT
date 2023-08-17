package com.example.socialley.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.socialley.Adapters.UsersAdapter;
import com.example.socialley.User;
import com.example.socialley.databinding.FragmentChatsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ChatsFragment extends Fragment {

    public ChatsFragment() {
        // Required empty public constructor

    }

    FragmentChatsBinding binding;
    ArrayList<User> list = new ArrayList<>();
    FirebaseDatabase database;
    FirebaseUser mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        Log.i("place","chat Adapter");
//        Log.i("Current user Id",mAuth.getUid());
//        mAuth.getUid();

        binding = FragmentChatsBinding.inflate(inflater,container,false);

        UsersAdapter adapter = new UsersAdapter(list,getContext());
        binding.chatRecyclerView.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.chatRecyclerView.setLayoutManager(linearLayoutManager);

        // Now we are displaying all users from the database
        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

//                    Log.i("CurrentUser",mAuth.getUid().toString());
                    Log.i("Data Snapshot",dataSnapshot.getValue().toString());
                    if(dataSnapshot.getKey().equals(mAuth.getUid()))continue;
                    User tempUser = dataSnapshot.getValue(User.class);
                    tempUser.setUserId(dataSnapshot.getKey());

                    list.add(tempUser);
//                    Log.i("User",tempUser.getUserId());
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return binding.getRoot();
    }
}