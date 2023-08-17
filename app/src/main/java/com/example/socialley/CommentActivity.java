package com.example.socialley;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CommentActivity extends AppCompatActivity {

    String postId;
    RecyclerView recyclerView;
    ImageView picture;
    ImageView image;
    TextView name,time,title,description, like, tcomment;
    String hisuid, ptime, myuid, myname, myemail, mydp, uimage, plikes, hisdp, hisname;
    EditText comment;
    ImageView imagep;
    ProgressDialog progressDialog;
    ImageButton sendbtn;
    LinearLayout profile;

    List<comment> commentList;
    CustomCommentAdapter adapterComment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        postId = getIntent().getStringExtra("pid");

        recyclerView = findViewById(R.id.recyclecomment);
        picture = findViewById(R.id.pictureco);
        image = findViewById(R.id.pimagetvco);
        name = findViewById(R.id.unameco);
        time = findViewById(R.id.utimeco);
        title = findViewById(R.id.ptitleco);

        myemail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        description = findViewById(R.id.descriptco);
        tcomment = findViewById(R.id.pcommenttv);
        like = findViewById(R.id.plikebco);
        comment = findViewById(R.id.typecommet);
        sendbtn = findViewById(R.id.sendcomment);
        profile = findViewById(R.id.profilelayout);
        progressDialog = new ProgressDialog(this);

        PostInfo();
        UserInfo();
        loadComments();

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommentActivity.this, HomeActivity.class);
                intent.putExtra("pid", postId);
                startActivity(intent);
            }
        });

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Helllloooooooooo about to post");
                postComment();
            }
        });

    }

    private  void loadComments(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        commentList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    comment modelComment = dataSnapshot1.getValue(comment.class);
                    commentList.add(modelComment);
                    adapterComment = new CustomCommentAdapter(getApplicationContext(), commentList, myuid, postId);
                    recyclerView.setAdapter(adapterComment);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void postComment() {
        progressDialog.setMessage("Adding Comment");

        final String commentss = comment.getText().toString().trim();
        if (TextUtils.isEmpty(commentss)) {
            Toast.makeText(CommentActivity.this, "Empty comment", Toast.LENGTH_LONG).show();
            return;
        }
        progressDialog.show();
        String timestamp = String.valueOf(System.currentTimeMillis());
        DatabaseReference commentsRef= FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");
        Query query = FirebaseDatabase.getInstance().getReference("users").orderByChild(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user!=null) {
                        System.out.println("Helllloooooooooo");
                        System.out.println(user);
                        myname = user.username;
                        mydp = user.profilePic;
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "You are making a mistake", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("cId", timestamp);
        hashMap.put("comment", commentss);
        hashMap.put("ptime", timestamp);
        hashMap.put("uid", myuid);
        hashMap.put("uemail", myemail);
        hashMap.put("udp", mydp);
        hashMap.put("uname", myname);
        commentsRef.child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(CommentActivity.this, "Added", Toast.LENGTH_LONG).show();
                comment.setText("");
                commentcount();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(CommentActivity.this, "Failed", Toast.LENGTH_LONG).show();
            }
        });
    }
    boolean count = false;

    private void commentcount() {
        count = true;
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (count) {
                    String comments = "" + dataSnapshot.child("pcomments").getValue();
                    int newcomment = Integer.parseInt(comments) + 1;
                    reference.child("pcomments").setValue("" + newcomment);
                    count = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void UserInfo() {

        Query myref = FirebaseDatabase.getInstance().getReference("users");
        myref.orderByChild("userId").equalTo(myuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    myname = dataSnapshot1.child("username").getValue().toString();
                    mydp = dataSnapshot1.child("profilePic").getValue().toString();
                    try {
                        Glide.with(CommentActivity.this).load(mydp).into(imagep);
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void PostInfo() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = databaseReference.orderByChild("ptime").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String ptitle = dataSnapshot1.child("title").getValue().toString();
                    String descriptions = dataSnapshot1.child("description").getValue().toString();
                    uimage = dataSnapshot1.child("pimage").getValue().toString();
                    hisdp = dataSnapshot1.child("uimage").getValue().toString();
                    hisuid = dataSnapshot1.child("uid").getValue().toString();
                    String uemail = dataSnapshot1.child("uemail").getValue().toString();
                    hisname = dataSnapshot1.child("uname").getValue().toString();
                    ptime = dataSnapshot1.child("ptime").getValue().toString();
                    plikes = dataSnapshot1.child("plikes").getValue().toString();
                    String commentcount = dataSnapshot1.child("pcomments").getValue().toString();
                    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                    calendar.setTimeInMillis(Long.parseLong(ptime));
                    String timedate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
                    name.setText(hisname);
                    title.setText(ptitle);
                    description.setText(descriptions);
                    like.setText(plikes + " Likes");
                    time.setText(timedate);
                    tcomment.setText(commentcount + " Comments");
                    if (uimage.equals("noImage")) {
                        image.setVisibility(View.GONE);
                    } else {
                        image.setVisibility(View.VISIBLE);
                        try {
                            Glide.with(CommentActivity.this).load(uimage).into(image);
                        } catch (Exception e) {

                        }
                    }
                    try {
                        Glide.with(CommentActivity.this).load(hisdp).into(picture);
                    } catch (Exception e) {

                    }


                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


}