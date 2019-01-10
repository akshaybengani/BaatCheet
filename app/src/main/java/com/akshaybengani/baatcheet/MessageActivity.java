package com.akshaybengani.baatcheet;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.akshaybengani.baatcheet.Adapters.MessagesAdapter;
import com.akshaybengani.baatcheet.ModelClasses.MessageModel;
import com.akshaybengani.baatcheet.ModelClasses.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    String receiverImageURL, receiveruserName, receiveruserID, senderuserID;
    Toolbar toolbar;
    CircleImageView toolbarProfileImage;
    TextView toolbarUserName;
    DatabaseReference databaseReference,dbrefChatList,dbUserStatus;

    ImageButton btnSend, btnSmiley;
    EditText editTextMessage;

    MessagesAdapter messagesAdapter;
    List<MessageModel> messageModelList;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // Get data from the previous activity
        receiverImageURL = getIntent().getStringExtra("imageURL");
        receiveruserID = getIntent().getStringExtra("userId");
        receiveruserName = getIntent().getStringExtra("userName");

        // To set the back button in the toolbar
        toolbar = findViewById(R.id.messageActivityToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        // To set the toolbar information
        toolbarProfileImage = findViewById(R.id.profileImage);
        toolbarUserName = findViewById(R.id.profileUserName);
        toolbarUserName.setText(receiveruserName);
        if (receiverImageURL.equals("default")) {
            toolbarProfileImage.setImageResource(R.mipmap.ic_launcher);
        } else {
           // Picasso.get().load(receiverImageURL).networkPolicy(NetworkPolicy.OFFLINE).into(toolbarProfileImage, new Callback() {
            //    @Override
           //     public void onSuccess() {
          //      }
          //      @Override
         //       public void onError(Exception e) {
                        Picasso.get().load(receiverImageURL).into(toolbarProfileImage);
        //        }
        //    });
        }

        // Click events of Bottom Messaging system
        btnSend = findViewById(R.id.imageButtonSend);
        btnSmiley = findViewById(R.id.imageButtonSmiley);
        editTextMessage = findViewById(R.id.editTextMessage);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        btnSmiley.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSmileyMenu();
            }
        });

        // Firebase Initialization
        senderuserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbUserStatus = FirebaseDatabase.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("BaatCheet/Chats/");
        //databaseReference.keepSynced(true);

        // Setup the RecyclerView
        recyclerView = findViewById(R.id.recyclerMessages);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        // To fill up the menu
        readMessage();

    }// end of oncreate

    private void sendMessage() {

        if (!TextUtils.isEmpty(editTextMessage.getText())) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("sender", senderuserID);
            hashMap.put("receiver", receiveruserID);
            hashMap.put("message", editTextMessage.getText().toString());

            databaseReference.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    editTextMessage.setText("");
                }
            });


            dbrefChatList = FirebaseDatabase.getInstance().
                    getReference("BaatCheet/ChatList/")
                    .child(senderuserID)
                    .child(receiveruserID);

            dbrefChatList.keepSynced(true);

            dbrefChatList.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()){
                        dbrefChatList.child("id").setValue(receiveruserID);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });



        }// end of if
    }// end of sendMessage

    private void openSmileyMenu() {
    }

    private void readMessage() {
        messageModelList = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // This is the 1st use of this list dont know why we have  empty it
                    //messageModelList.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        MessageModel messageModel = dataSnapshot1.getValue(MessageModel.class);
                        if (messageModel.getReceiver().equals(receiveruserID) &&
                                messageModel.getSender().equals(senderuserID) ||
                                messageModel.getReceiver().equals(senderuserID) &&
                                        messageModel.getSender().equals(receiveruserID)) {
                            messageModelList.add(messageModel);
                        }
                    }
                    messagesAdapter = new MessagesAdapter(messageModelList, receiverImageURL);
                    recyclerView.setAdapter(messagesAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }// end of readMessage


    private  void status(String status){
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);
        dbUserStatus.child("BaatCheet/Users").child(senderuserID).updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

}// end of class

/*
* Android Notifications
* BarChart
* ChatApplication
* JUMess
* JUMessManagement
* RecyclerCheckBox
* barcode Reader
* Android Basics
* SOS Buddy
* Akshay Bengani
* */