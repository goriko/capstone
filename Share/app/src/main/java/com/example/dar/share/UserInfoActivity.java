package com.example.dar.share;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private TextView textViewfname, textViewlname, textViewgender, textViewnumber, textViewgnumber;
    private Button buttonedit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid().toString());

        textViewfname = (TextView) findViewById(R.id.textViewFName);
        textViewlname = (TextView) findViewById(R.id.textViewLName);
        textViewgender = (TextView) findViewById(R.id.textViewGender);
        textViewnumber = (TextView) findViewById(R.id.textViewNumber);
        textViewgnumber = (TextView) findViewById(R.id.textViewGuardianNumber);
        buttonedit = (Button) findViewById(R.id.buttonEdit);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                textViewfname.setText(dataSnapshot.child("FName").getValue().toString());
                textViewlname.setText(dataSnapshot.child("LName").getValue().toString());
                textViewgender.setText(dataSnapshot.child("Gender").getValue().toString());
                textViewnumber.setText(dataSnapshot.child("Number").getValue().toString());
                textViewgnumber.setText(dataSnapshot.child("GuardianNumber").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        buttonedit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == buttonedit){
            startActivity(new Intent(this, EditUserInfoActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ProfileActivity.class));
    }
}

