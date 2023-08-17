package com.example.socialley;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyHolder> {

    Context context;
    FirebaseAuth firebaseAuth;
    String uid;

    public CustomAdapter(Context context, List<User> list) {
        this.context = context;
        this.list = list;
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();
    }

    List<User> list;

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        String userProfilePic = list.get(position).getProfilePic();
        String userName = list.get(position).getUsername();
        String userEmail = list.get(position).getEmail();
        holder.name.setText(userName);
        holder.email.setText(userEmail);
        if (!userProfilePic.equals("")) {
            try {
                Glide.with(context).load(userProfilePic).into(holder.profilePic);
            } catch (Exception e) {
            }
        }
        else {
            try {
                Glide.with(context).load(R.drawable.ic_action_profile).into(holder.profilePic);
            } catch (Exception e) {
            }
        }
        FirebaseDatabase.getInstance().getReference("users").orderByChild("email").equalTo(holder.email.getText().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (snapshot1.exists()) {
                        User user = snapshot1.getValue(User.class);
                        holder.currentUserID = user.getUserId();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Query query = FirebaseDatabase.getInstance().getReference("Friends");
        query.orderByChild(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if (snapshot1.hasChild(holder.currentUserID)) {
                            holder.isFriend = true;
                            holder.addFriend.setText("Remove Friend");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        holder.addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.isFriend) {
                    FirebaseDatabase.getInstance().getReference("Friends").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(holder.currentUserID).setValue("friend");
                    FirebaseDatabase.getInstance().getReference("Friends").child(holder.currentUserID).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("friend");
                    Toast.makeText(context.getApplicationContext(), "You are now friends with " + holder.name.getText(), Toast.LENGTH_LONG).show();
                    holder.isFriend = true;
                    holder.addFriend.setText("Remove Friend");
                }
                else {
                    FirebaseDatabase.getInstance().getReference("Friends").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(holder.currentUserID).removeValue();
                    FirebaseDatabase.getInstance().getReference("Friends").child(holder.currentUserID).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                    Toast.makeText(context.getApplicationContext(), "You no longer friends with " + holder.name.getText(), Toast.LENGTH_LONG).show();
                    holder.isFriend = false;
                    holder.addFriend.setText("Add Friend");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        CircleImageView profilePic;
        TextView name, email;
        Button addFriend;

        FirebaseAuth firebaseAuth;
        FirebaseUser firebaseUser;
        FirebaseDatabase firebaseDatabase;
        DatabaseReference databaseReference;

        String currentUserID;
        Boolean isFriend;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("users");
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();

            profilePic = itemView.findViewById(R.id.profilePicUser);
            name = itemView.findViewById(R.id.nameUser);
            email = itemView.findViewById(R.id.emailUser);
            addFriend = itemView.findViewById(R.id.add_friend);

            currentUserID = "";
            isFriend = false;

            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(itemView.getContext(), ViewProfile.class);
                    i.putExtra("email", email.getText());
                    itemView.getContext().startActivity(i);
                }
            });


        }
    }
}
