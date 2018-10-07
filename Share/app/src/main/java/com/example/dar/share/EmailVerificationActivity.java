package com.example.dar.share;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EmailVerificationActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private TextView textViewVerified;
    private Button buttonProceed;
    private DatabaseReference databaseReference;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        textViewVerified = (TextView) findViewById(R.id.textVeiwVerified);
        buttonProceed = (Button) findViewById(R.id.buttonProceed);
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid().toString());

        buttonProceed.setOnClickListener(this);

        verify();

    }

    @Override
    protected void onResume() {
        super.onResume();
        user.reload();
        verify();
    }

    private void verify(){
        if(user.isEmailVerified()){
            textViewVerified.setText("Email has been verified");
            buttonProceed.setVisibility(View.VISIBLE);
        }else{
            user.sendEmailVerification();
        }
    }

    @Override
    public void onClick(View v) {
        if(buttonProceed == v) {
            startActivity(new Intent(this, RegistrationActivity.class));
        }
    }
}
