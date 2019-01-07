package com.akshaybengani.baatcheet;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.akshaybengani.baatcheet.Fragments.ChatFragment;
import com.akshaybengani.baatcheet.Fragments.UsersFragment;
import com.akshaybengani.baatcheet.ModelClasses.UserModel;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private CircleImageView circleImageView;
    private TextView textViewUserName;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.mainActivityToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        circleImageView = findViewById(R.id.profileImage);
        textViewUserName = findViewById(R.id.profileUserName);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.myViewPager);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("BaatCheet/Users/").child(firebaseUser.getUid());
        databaseReference.keepSynced(true);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final UserModel userModel = dataSnapshot.getValue(UserModel.class);
                textViewUserName.setText(userModel.getUsername());

                if (userModel.getImageURL().equals("default"))
                {
                    Picasso.get().load(R.mipmap.ic_launcher).into(circleImageView);
                }
                else
                    {
                        Picasso.get().load(userModel.getImageURL()).into(circleImageView);
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });// end of value event listener


        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        /*
                Make sure to use
                android.support.v4.app.Fragment

         */

        viewPagerAdapter.addFragments(new ChatFragment(), "Chats");
        viewPagerAdapter.addFragments(new UsersFragment(),"Users");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }// end of onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // This is to inflate the options menu on the toolbar
        getMenuInflater().inflate(R.menu.topleftmenu,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            // For performing actions on the menu layout
            if (item.getItemId() == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this,SplashScreen.class);
                startActivity(intent);
                finish();
                return true;
            }

        return false;
    }



    class ViewPagerAdapter extends FragmentPagerAdapter{

        private ArrayList<Fragment> fragments;
        private  ArrayList<String> titles;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int i) {
          return   fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragments(Fragment fragment,String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return  titles.get(position);
        }

    }// end of ViewPagerAdapter class


}// end of class
