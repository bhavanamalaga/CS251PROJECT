package com.example.socialley;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Comment;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomCommentAdapter extends RecyclerView.Adapter<CustomCommentAdapter.MyHolder>{
    Context context;
    String uid;
    String postid;
    List<comment> list;

    public CustomCommentAdapter(Context context, List<comment> list,String myuid , String postid) {
        this.context = context;
        this.list = list;
        this.uid = myuid;
        this.postid = postid;
    }


    @NonNull
    @Override
    public CustomCommentAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_comments, parent, false);
        return new CustomCommentAdapter.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomCommentAdapter.MyHolder holder, int position) {
        String name = list.get(position).getUname();
        String email = list.get(position).getUemail();
        String image = list.get(position).getUdp();

        String comment = list.get(position).getComment();
        String timestamp = list.get(position).getPtime();
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(timestamp));

        String timedate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        System.out.println("Username" + name);
        holder.name.setText(name);
        holder.time.setText(timedate);
        holder.comment.setText(comment);
        try {
            Glide.with(context).load(image).into(holder.userImage);
        } catch (Exception e) {
            try {
                Glide.with(context).load(R.drawable.ic_action_profile).into(holder.userImage);
            }
            catch (Exception f) {

            }
        }

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(), ViewProfile.class);
                intent.putExtra("email", email);
                context.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        ImageView userImage;
        TextView name, comment, time;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            name = itemView.findViewById(R.id.commentUser);
            comment = itemView.findViewById(R.id.commenttext);
            time = itemView.findViewById(R.id.commenttime);
        }
    }

}
