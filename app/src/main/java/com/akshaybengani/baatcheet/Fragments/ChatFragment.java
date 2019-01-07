package com.akshaybengani.baatcheet.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akshaybengani.baatcheet.Adapters.UsersAdapter;
import com.akshaybengani.baatcheet.ModelClasses.MessageModel;
import com.akshaybengani.baatcheet.ModelClasses.UserModel;
import com.akshaybengani.baatcheet.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private UsersAdapter usersAdapter;
    private List<UserModel>  userModelList;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    private List<String> stringList;


    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recyclerChat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // private List<String> stringList; Declaration at top
        stringList = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("BaatCheet/Chats/");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //userModelList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    MessageModel messageModel = dataSnapshot1.getValue(MessageModel.class);

                    if (messageModel.getSender().equals(firebaseUser.getUid())){
                      stringList.add(messageModel.getReceiver());
                    }
                    if (messageModel.getReceiver().equals(firebaseUser.getUid())){
                        stringList.add(messageModel.getSender());
                    }
                }
                readChat();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


       return view;
    }

    private void readChat() {

        userModelList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("BaatCheet/Users/");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userModelList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    UserModel userModel = dataSnapshot1.getValue(UserModel.class);

                    for (String id: stringList){
                        if (userModel.getId().equals(id)){
                            if (userModelList.size() !=0){
                                for (UserModel userModel1 : userModelList){
                                    if (!userModel.getId().equals(userModel1.getId())){
                                        userModelList.add(userModel);
                                        Log.d("DataAdded",userModel.getId());
                                    } // If the existing list don't have same value for sender and reciever
                                } // end of inner userModel
                            } else {
                                userModelList.add(userModel);
                                Log.d("DataAdded",userModel.getId());
                            } // end of else
                        }   // end of userModel id equals string id
                    }   // end of String is loop
                }   // end of DataSnapshot loop

                usersAdapter = new UsersAdapter(userModelList);
                recyclerView.setAdapter(usersAdapter);


            } // end of onDataChange

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }// end of readChat()


}
