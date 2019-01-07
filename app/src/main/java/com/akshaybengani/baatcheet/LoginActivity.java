package com.akshaybengani.baatcheet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rengwuxian.materialedittext.MaterialEditText;

public class LoginActivity extends AppCompatActivity {

     private MaterialEditText materialEditTextUsername,materialEditTextPassword;
     private MaterialButton btnLogin;
     private String username,password;

    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login Activity");

        materialEditTextUsername = (MaterialEditText)findViewById(R.id.matTextUsername);
        materialEditTextPassword = (MaterialEditText)findViewById(R.id.matTextPassword);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Logging up");
        progressDialog.setMessage("Just wait a while to see future");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();

    }


    public void showSignUpPage(View view) {
        Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
        startActivity(intent);
    }


    public void loginMyUser(View view) {
        progressDialog.show();

        username = materialEditTextUsername.getText().toString().trim();
        password = materialEditTextPassword.getText().toString().trim();

        if (validateData()){

            firebaseAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "signInWithEmail:success");
                                Toast.makeText(LoginActivity.this, "Authentication Successfull.", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                progressDialog.dismiss();
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });


        }else{
            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this, "Please Enter Valid Details", Toast.LENGTH_SHORT).show();
        }


    }

    private boolean validateData() {
        return (password.length() > 6 && username.length() > 6);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // checks the user is already loged in or not
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

    }//end of onstart


}// end of class




