package com.example.socialley.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialley.ChatDetailActivity;
import com.example.socialley.R;
import com.example.socialley.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    ArrayList<User> list ;
    Context context;

    public UsersAdapter(ArrayList<User> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_show_user,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
// This function shows users in the list which are to be displayed
        Log.i("Position",String.valueOf(position));


        User userDisplay = list.get(position);
//        Picasso.get().load(userDisplay.getProfilePic()).placeholder(R.drawable.profile).into(holder.imageView);

        if (!userDisplay.getProfilePic().equals("")) {
            try {
                Glide.with(context).load(userDisplay.getProfilePic()).into(holder.imageView);
            }
            catch (Exception e) {

            }
        }

        holder.userNameView.setText(userDisplay.getUsername());

        // For setting last message on view
        FirebaseDatabase.getInstance().getReference().child("Chats")
                .child(FirebaseAuth.getInstance().getUid() + userDisplay.getUserId())
                .orderByChild("timestamp")
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()){
                            for(DataSnapshot snapshot1 : snapshot.getChildren()){
//                                Log.i("Key of snapshot1",snapshot1.child("userName").getValue().toString());
                                String currUser = FirebaseAuth.getInstance().getUid();
//                                Log.i("Authkey",FirebaseDatabase.getInstance().getReference("Users").child(currUser).child("userName").toString());
                                DatabaseReference currUserReference = FirebaseDatabase.getInstance().getReference("users").child(currUser).child("username");
                                currUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshotCurr) {
                                        String currUserName = snapshotCurr.getValue().toString();
                                        Log.i("UserName",snapshot1.child("userName").getValue().toString() + currUserName);

                                        if(snapshot1.child("userName").getValue().toString().equals(currUserName)){
                                            holder.lastMessage.setText("You : "+snapshot1.child("message").getValue().toString());
                                        } else {
                                            holder.lastMessage.setText(snapshot1.child("userName").getValue().toString()+  " : "+snapshot1.child("message").getValue().toString());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatDetailActivity.class);
                intent.putExtra("userId",userDisplay.getUserId());
                intent.putExtra("profilePic",userDisplay.getProfilePic());
                intent.putExtra("userName",userDisplay.getUsername());

                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        Log.i("List Size",String.valueOf(list.size()));
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        ImageView imageView;
        TextView userNameView, lastMessage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.profile_image_chat);
            userNameView = itemView.findViewById(R.id.userNameView);
            lastMessage = itemView.findViewById(R.id.lastMessage);


        }
    }
}
