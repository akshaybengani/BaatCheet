package com.akshaybengani.baatcheet.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akshaybengani.baatcheet.ModelClasses.UserModel;
import com.akshaybengani.baatcheet.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private CircleImageView circleImageView;
    private TextView textViewUserName;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        circleImageView = view.findViewById(R.id.profilePicture);
        textViewUserName = view.findViewById(R.id.profileName);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("BaatCheet/Users/").child(firebaseUser.getUid());
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final UserModel userModel = dataSnapshot.getValue(UserModel.class);
                textViewUserName.setText(userModel.getUsername());
                if (userModel.getImageURL().equals("default")) {
                    Picasso.get().load(R.mipmap.ic_launcher).into(circleImageView);
                }
                else {
                    Picasso.get().load(userModel.getImageURL()).into(circleImageView);
                }
            }// end of onDataChange
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });// end of value event listener

        return view;
    }

}
