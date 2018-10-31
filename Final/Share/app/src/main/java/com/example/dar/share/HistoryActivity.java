package com.example.dar.share;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HistoryActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference databaseReference;
    private LinearLayout linearLayout;
    private Integer x = 0;
    private String[] str = new String[100];
    private FirebaseUser uID;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        firebaseAuth = FirebaseAuth.getInstance();
        uID = firebaseAuth.getCurrentUser();

        linearLayout = (LinearLayout) findViewById(R.id.layout);

        databaseReference = FirebaseDatabase.getInstance().getReference("travel");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                linearLayout.removeAllViews();
                int i = 0;
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    if(data.child("Available").getValue().equals("2")){
                        for (DataSnapshot users: data.child("users").getChildren()){
                            if(!users.hasChild("UserID")){
                                if(users.getValue().toString().equals(uID.getUid())){
                                    i = 1;
                                }
                            }
                        }
                    }

                    if(i == 1){
                        x++;
                        LinearLayout samp = new LinearLayout(HistoryActivity.this);
                        samp.setBackgroundResource(R.drawable.customborder);
                        samp.setId(x);
                        String origin = data.child("OriginString").getValue().toString();
                        String destination = data.child("DestinationString").getValue().toString();
                        String noofusers = data.child("NoOfUsers").getValue().toString();

                        TextView textView1 = new TextView(HistoryActivity.this);
                        textView1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        textView1.setText("Origin : " + origin + " \nDestination: " + destination + " \nNo. of People in the room: " + noofusers);

                        str[x] = data.getKey().toString();

                        samp.addView(textView1);
                        linearLayout.addView(samp);
                        samp.setOnClickListener(HistoryActivity.this);
                    }

                    i = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        Integer i = v.getId();
        Intent intent = new Intent(this, InsideHistoryActivity.class);
        intent.putExtra("id", str[i]);
        startActivity(intent);
    }
}
