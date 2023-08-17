package com.example.socialley;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialley.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyHomeFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    RecyclerView postrecycle;
    FloatingActionButton fab;
    List<Post> posts;
    CustomPostAdapter adapterPosts;
    String uid;
    ProgressDialog pd;
    String type, email;
    Button myPosts, friendsPosts, allPosts;
    Boolean active;

    public MyHomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getArguments() != null) {
            type = getArguments().getString("type");
            if (type.equals("1")) {
                email = getArguments().getString("email");
            }
        }
        else {
            type = "0";
        }
        active = false;
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_home, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        uid = FirebaseAuth.getInstance().getUid();
        postrecycle = view.findViewById(R.id.recyclerposts);
        myPosts = view.findViewById(R.id.my_posts_btn);
        friendsPosts = view.findViewById(R.id.friends_posts_btn);
        allPosts = view.findViewById(R.id.all_posts_btn);

        if (type.equals("0") || (type.equals("1") && email.equals(firebaseAuth.getCurrentUser().getEmail()))) {
            myPosts.setVisibility(View.VISIBLE);
            friendsPosts.setVisibility(View.VISIBLE);
            allPosts.setVisibility(View.VISIBLE);
            active = true;
        }

        posts = new ArrayList<>();
        pd = new ProgressDialog(getActivity());
        loadMyPosts();
        pd.setCanceledOnTouchOutside(false);

        myPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (active) {
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "1");
                    bundle.putString("email", firebaseAuth.getCurrentUser().getEmail());
                    MyHomeFragment homefragment = new MyHomeFragment();
                    homefragment.setArguments(bundle);
                    FragmentTransaction homefragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    homefragmentTransaction.replace(R.id.content, homefragment, "");
                    homefragmentTransaction.commit();
                }
            }
        });

        friendsPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (active) {
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "2");
                    MyHomeFragment homefragment = new MyHomeFragment();
                    homefragment.setArguments(bundle);
                    FragmentTransaction homefragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    homefragmentTransaction.replace(R.id.content, homefragment, "");
                    homefragmentTransaction.commit();
                }
            }
        });

        allPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (active) {
                    MyHomeFragment homefragment = new MyHomeFragment();
                    FragmentTransaction homefragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    homefragmentTransaction.replace(R.id.content, homefragment, "");
                    homefragmentTransaction.commit();
                }
            }
        });

        return view;
    }

    private void loadMyPosts() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        postrecycle.setLayoutManager(layoutManager);

        Query query = FirebaseDatabase.getInstance().getReference("Posts");

        if (type.equals("1") || type.equals("0")) {
            if (type.equals("1")) {
                query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("uemail").equalTo(email);
            }
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    posts.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Post post = dataSnapshot1.getValue(Post.class);
                        posts.add(post);
                    }
                    adapterPosts = new CustomPostAdapter(getActivity(), posts);
                    postrecycle.setAdapter(adapterPosts);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
        else if (type.equals("2")) {
            Query query2 = FirebaseDatabase.getInstance().getReference("Friends").child(firebaseUser.getUid());
            HashMap<String, String> friends;
            friends = new HashMap<String, String>();
            query2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        friends.put(snapshot1.getKey(), "friend");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            Query query3 = FirebaseDatabase.getInstance().getReference("Posts");
            query3.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    posts.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Post post = snapshot1.getValue(Post.class);
                        if (friends.containsKey(post.getUid())) {
                            posts.add(post);
                        }
                    }
                    adapterPosts = new CustomPostAdapter(getActivity(), posts);
                    postrecycle.setAdapter(adapterPosts);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
}