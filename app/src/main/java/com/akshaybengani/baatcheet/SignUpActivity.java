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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    MaterialEditText materialEditTextUsername, materialEditTextPassword, materialEditTextEmail;
    MaterialButton register;
    String username, email, password;
    private ProgressDialog progressDialog;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        toolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sign Up Activity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        materialEditTextUsername = (MaterialEditText) findViewById(R.id.matTextUsername);
        materialEditTextPassword = (MaterialEditText) findViewById(R.id.matTextPassword);
        materialEditTextEmail = (MaterialEditText) findViewById(R.id.matTextEmail);
        register = (MaterialButton) findViewById(R.id.btnRegister);

        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Signing Up ...");
        progressDialog.setMessage("Buckle up to see the future");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("BaatCheet/Users/");


    }// end of onCreate


    public void showLoginPage(View view) {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void registerMyUser(View view) {

        progressDialog.show();

        email = materialEditTextEmail.getText().toString().trim();
        username = materialEditTextUsername.getText().toString().trim();
        password = materialEditTextPassword.getText().toString().trim();

        if (validateData()) {

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("", "createUserWithEmail:success");

                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                String userId = firebaseUser.getUid();

                                // HashMap is used to bind the data into object without creating a new class
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("id", userId);
                                hashMap.put("username", username);
                                hashMap.put("imageURL", "default");
                                hashMap.put("status","offline");

                                databaseReference.child(userId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            // change the name of class
                                            Log.d("MyTask","Account Creation successfull");
                                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();

                                        } else {
                                            progressDialog.dismiss();
                                               Toast.makeText(SignUpActivity.this,"Problem with the network connection please try again",Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });


                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("", "createUserWithEmail:failure", task.getException());
                                progressDialog.dismiss();
                                Toast.makeText(SignUpActivity.this, "Account Already exists.", Toast.LENGTH_SHORT).show();
                            }

                            //...
                        }
                    });

        } else {
            progressDialog.dismiss();
            Toast.makeText(SignUpActivity.this, "Please Enter Valid Details", Toast.LENGTH_SHORT).show();
        }

    }// end of registerFunction

    private boolean validateData() {
        return (email.contains("@") && email.contains(".")) && (password.length() > 6 && username.length() > 6);
    }


}// end of class
