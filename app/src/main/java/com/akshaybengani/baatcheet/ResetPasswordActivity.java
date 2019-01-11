package com.akshaybengani.baatcheet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

public class ResetPasswordActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private MaterialEditText editTextResetEmail;
    private MaterialButton materialButtonReset;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Toolbar configuration
        toolbar = findViewById(R.id.default_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reset Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Progress Dialog data
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Sending Email ...");
        progressDialog.setMessage("A password reset email to your mail ID");
        progressDialog.setCanceledOnTouchOutside(false);

        // Initialization
        firebaseAuth = FirebaseAuth.getInstance();
        editTextResetEmail = findViewById(R.id.materialResetEmail);
        materialButtonReset = findViewById(R.id.buttonReset);

        materialButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });


    }// end of onCreate

    private void resetPassword() {
        progressDialog.show();
        if (dataValidation()) {

            firebaseAuth.sendPasswordResetEmail(editTextResetEmail.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ResetPasswordActivity.this, "Please Check Your Email", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(intent);
                    }else {
                        Toast.makeText(ResetPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                }
            });

        }else {
            Toast.makeText(ResetPasswordActivity.this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

    }

    private boolean dataValidation() {
        String email = editTextResetEmail.getText().toString().trim();
        return !TextUtils.isEmpty(email) && email.contains("@") && email.contains(".");
    }


}// end of class
