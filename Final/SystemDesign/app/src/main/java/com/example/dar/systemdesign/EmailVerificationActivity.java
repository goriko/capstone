package com.example.dar.systemdesign;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
        user.sendEmailVerification();
        textViewVerified = (TextView) findViewById(R.id.textVeiwVerified);
        buttonProceed = (Button) findViewById(R.id.buttonProceed);
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid().toString());

        buttonProceed.setOnClickListener(this);

        Handler handler = new Handler();
        int delay = 1000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                user.reload();
                Log.d("myTag", "eyyy");
                if(user.isEmailVerified() == true){
                    change();
                }
                handler.postDelayed(this, delay);
            }
        }, delay);

    }

    public void change(){
        textViewVerified.setText("Email has been verified");
        buttonProceed.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textViewVerified.getLayoutParams();
        params.setMargins(0,50,0,0);
        textViewVerified.setLayoutParams(params);
    }


    @Override
    public void onClick(View v) {
        if(buttonProceed == v) {
            startActivity(new Intent(this, RegistrationActivity.class));
        }
    }
}
