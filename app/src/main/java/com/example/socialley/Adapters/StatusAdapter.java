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
import com.example.socialley.StatusUpdate;
import com.example.socialley.User;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder> {

    ArrayList<User> list;
    Context context;

    public StatusAdapter(ArrayList<User> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_status_user,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User usersDisplay = list.get(position);

//        Picasso.get().load(usersDisplay.getProfilePic()).placeholder(R.drawable.profile).into(holder.imageView);
        if (!usersDisplay.getProfilePic().equals("")) {
            try {
                Glide.with(context).load(usersDisplay.getProfilePic()).into(holder.imageView);
            }
            catch (Exception e) {

            }
        }

        if(usersDisplay.getStatus()!=null)holder.statusMessage.setText(usersDisplay.getStatus());

        holder.userNameView.setText(usersDisplay.getUsername());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("User",FirebaseAuth.getInstance().getCurrentUser().getUid());
                Log.i("User",usersDisplay.getUserId());
                if(usersDisplay.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    Intent intent= new Intent(context, StatusUpdate.class);
                    context.startActivity(intent);
                    return;
                }

                Intent intent= new Intent(context, ChatDetailActivity.class);
                intent.putExtra("userId",usersDisplay.getUserId());
                intent.putExtra("profilPic",usersDisplay.getProfilePic());
                intent.putExtra("userName",usersDisplay.getUsername());

                context.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView userNameView , statusMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.profile_image);
            userNameView = itemView.findViewById(R.id.userNameView);
            statusMessage = itemView.findViewById(R.id.statusMessage);
        }
    }
}
