package com.example.administrator.share;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonSiginIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignup;
    private TextView textViewPassword;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            //profile activity
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonSiginIn = (Button) findViewById(R.id.buttonSignIn);
        textViewSignup = (TextView) findViewById(R.id.textViewSignUp);
        textViewPassword = (TextView) findViewById(R.id.textViewPassword);

        progressDialog = new ProgressDialog(this);

        buttonSiginIn.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);
        textViewPassword.setOnClickListener(this);

    }

    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            //email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }else if(TextUtils.isEmpty(password)){
            //password is empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        //if validations are passed
        progressDialog.setMessage("Logging in....");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                         progressDialog.dismiss();
                         if(task.isSuccessful()){
                             // profile activity
                             finish();
                             startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                         }else{
                             String message = task.getException().getMessage();
                             Toast.makeText(LoginActivity.this, "Error Occurred: "+message, Toast.LENGTH_SHORT).show();
                             editTextPassword.setText("");
                             editTextEmail.setText("");
                         }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if(v == buttonSiginIn){
            userLogin();
        }

        if(v == textViewSignup){
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        if(v == textViewPassword){
            finish();
            startActivity(new Intent(this, ResetPasswordActivity.class));
        }
    }
}
