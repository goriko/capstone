package com.example.guanzon.share;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.goodiebag.pinview.Pinview;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PinNumberActivity extends AppCompatActivity implements View.OnClickListener {

    private Integer i = 0;
    private String p1, p2;
    private TextView textView;
    private Pinview pinView;
    private Button buttonProceed;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_number);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        textView = (TextView) findViewById(R.id.textView);
        pinView = (Pinview) findViewById(R.id.pinView);
        buttonProceed = (Button) findViewById(R.id.buttonProceed);

        textView.setText("Add Pin Number");
        buttonProceed.setOnClickListener(this);
    }

    public void first(){
        p1 = pinView.getValue().toString();
        i = 1;
        textView.setText("Re enter Pin number");
        pinView.setValue("");
        //lacking make pinview nothing again
    }

    public void addPin(){
        p2 = pinView.getValue().toString();

        if(!p1.equals(p2)){
            return;
        }

        Integer pin = Integer.valueOf(p1);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        databaseReference.child("users").child(user.getUid().toString()).child("Pin").setValue(pin);
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));

    }

    @Override
    public void onClick(View v) {
        if(v == buttonProceed && i == 0){
            first();
        }else if(v == buttonProceed && i == 1){
            addPin();
        }
    }
}

