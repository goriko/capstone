package com.example.dar.share;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddMember {

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private Integer i = 0;

    public void add(String id){
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("travel").child(id);

        databaseReference.child("NoOfUsers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String NoOfUsers = dataSnapshot.getValue().toString();
                Integer x = Integer.valueOf(NoOfUsers) + 1;
                databaseReference.child("NoOfUsers").setValue(x.toString());
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (i==0){
                    if (!dataSnapshot.hasChild("Member1")) {
                        databaseReference.child("users").child("Member1").setValue(user.getUid());
                        Log.d("mem1", "mem");
                    } else if (!dataSnapshot.hasChild("Member2")) {
                        databaseReference.child("users").child("Member2").setValue(user.getUid());
                        Log.d("mem2", "mem");
                    } else if (!dataSnapshot.hasChild("Member3")) {
                        databaseReference.child("users").child("Member3").setValue(user.getUid());
                        Log.d("mem3", "mem");
                    }
                }
                i++;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
