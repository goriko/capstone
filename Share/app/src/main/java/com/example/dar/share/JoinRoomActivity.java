package com.example.dar.share;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JoinRoomActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference databaseReference;
    private LinearLayout linearLayout;
    private Integer x = 0;
    private String[] str = new String[100];

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);

        linearLayout = (LinearLayout) findViewById(R.id.layout);

        databaseReference = FirebaseDatabase.getInstance().getReference("travel");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                linearLayout.removeAllViews();
                Integer i;
                for(DataSnapshot data: dataSnapshot.getChildren()){{
                    if(data.child("Available").getValue().toString().equals("1")){
                        x++;
                        LinearLayout samp = new LinearLayout(JoinRoomActivity.this);
                        samp.setBackgroundResource(R.drawable.customborder);
                        samp.setId(x);
                        String origin = data.child("Origin").getValue().toString();
                        String destination = data.child("Destination").getValue().toString();
                        String noofusers = data.child("NoOfUsers").getValue().toString();

                        TextView textView1 = new TextView(JoinRoomActivity.this);
                        textView1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        textView1.setText("Origin : " + origin + " \nDestination: " + destination + " \nNo. of People in the room: " + noofusers);

                        str[x] = data.getKey().toString();

                        samp.addView(textView1);
                        linearLayout.addView(samp);
                        samp.setOnClickListener(JoinRoomActivity.this);
                    }
                }
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
        Intent intent = new Intent(this, InsideRoomActivity.class);
        intent.putExtra("id", str[i]);
        startActivity(intent);
    }
}
