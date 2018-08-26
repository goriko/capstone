package com.example.administrator.share;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangePinActivity extends AppCompatActivity implements View.OnClickListener {

    private Integer i = 0;
    private String p1, p2, truePin;
    private TextView textView;
    private Pinview pinView;
    private Button buttonProceed;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pin);

        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference(user.getUid().toString());

        textView = (TextView) findViewById(R.id.textView);
        pinView = (Pinview) findViewById(R.id.pinView);
        buttonProceed = (Button) findViewById(R.id.buttonProceed);

        textView.setText("Enter Pin Number");
        buttonProceed.setOnClickListener(this);

    }

    private void firstClick(){
        p1 = pinView.getValue().toString();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                truePin = dataSnapshot.child("Pin").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(!p1.equals(truePin)){
            Toast.makeText(this, "Please enter the right pin number", Toast.LENGTH_SHORT).show();
            return;
        }

        i = 1;
        textView.setText("Please enter new Pin Number");
    }

    private void secondClick(){
        p1 = pinView.getValue().toString();
        i = 2;
        textView.setText("Please enter again your new Pin Number");
    }

    private void thirdClick(){
        p2 = pinView.getValue().toString();

        if(!p1.equals(p2)){
            Toast.makeText(this, "Wrong pin number.Please try again", Toast.LENGTH_SHORT).show();
            return;
        }

        Integer pin = Integer.valueOf(p1);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        databaseReference.child("Pin").setValue(pin);
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
    }

    @Override
    public void onClick(View v) {
        if(i == 0){
            firstClick();
        }else if(i == 1){
            secondClick();
        }else if(i == 2){
            thirdClick();
        }
    }
}

