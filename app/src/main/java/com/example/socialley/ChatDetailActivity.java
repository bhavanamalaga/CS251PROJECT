package com.example.socialley;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.socialley.Adapters.ChatAdapter;
import com.example.socialley.Models.MessageModel;
import com.example.socialley.databinding.ActivityChatDetailBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetailActivity extends AppCompatActivity {

    ActivityChatDetailBinding binding;
    FirebaseDatabase database;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        String senderId = mAuth.getUid();

        String receiveId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");

        binding.userNameChat.setText(userName);
//        Picasso.get().load(profilePic).placeholder(R.drawable.profile).into(binding.profileImage);


        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatDetailActivity.this,HomeActivity.class);
                intent.putExtra("prevId",String.valueOf(R.id.chat));
                startActivity(intent);
            }
        });

        final ArrayList<MessageModel> messageModels = new ArrayList<>();
        final ChatAdapter chatAdapter = new ChatAdapter(messageModels,this,receiveId);
        binding.chatRecyclerView.setAdapter(chatAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        final String senderRoom = senderId + receiveId;
        final String receiverRoom = receiveId + senderId;

        database.getReference().child("Chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModels.clear();
                        for(DataSnapshot snapshot1 : snapshot.getChildren()){
                            MessageModel model = snapshot1.getValue(MessageModel.class);
                            model.setMessageId(snapshot1.getKey());

                            messageModels.add(model);
                        }
                        chatAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // on clicking send button
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String messageChat = binding.chatMessage.getText().toString();
                DatabaseReference senderNameData = database.getReference("users").child(senderId).child("username");
                senderNameData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String senderName = snapshot.getValue().toString();
                        final MessageModel model = new MessageModel(senderId,messageChat,senderName);
                        model.setTimeStamp(new Date().getTime());
                        // after sending we need to remove text from the textView
                        binding.chatMessage.setText("");

                        database.getReference().child("Chats")
                                .child(senderRoom)
                                .push()
                                .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
//                        Log.i("Receiver","Message Received");
                                database.getReference().child("Chats")
                                        .child(receiverRoom)
                                        .push()
                                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
//                                Log.i("Database","Received Message");

                                    }
                                });
                            }
                        });
//                        Log.i("Receiver","Message Received");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
//                final MessageModel model = new MessageModel(senderId,messageChat,senderName);
//                model.setTimeStamp(new Date().getTime());
//                // after sending we need to remove text from the textView
//                binding.chatMessage.setText("");
//
//                database.getReference().child("Chats")
//                        .child(senderRoom)
//                        .push()
//                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
////                        Log.i("Receiver","Message Received");
//                        database.getReference().child("Chats")
//                                .child(receiverRoom)
//                                .push()
//                                .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
////                                Log.i("Database","Received Message");
//
//                            }
//                        });
//                    }
//                });
////                        Log.i("Receiver","Message Received");



            }
        });





    }
}