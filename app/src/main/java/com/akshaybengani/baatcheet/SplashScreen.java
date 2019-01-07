package com.akshaybengani.baatcheet;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    // Splash Time
    private static final  int SPLASH_TIME=2000;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*No needed to remove I have already removed from the styles we will be using custom toolbars*/
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getSupportActionBar().hide();

        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_TIME);


    }


    @Override
    protected void onStart() {
        super.onStart();
        // checks the user is already loged in or not
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null){
            Intent intent = new Intent(SplashScreen.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

    }//end of onstart


}// end of class
