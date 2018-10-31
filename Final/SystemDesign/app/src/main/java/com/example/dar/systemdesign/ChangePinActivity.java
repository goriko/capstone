package com.example.dar.systemdesign;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ChangePinActivity extends Fragment {

    private Integer i = 0;
    private String p1, p2, truePin;
    private TextView textView;
    private Pinview pinView;
    private Button buttonProceed;
    private Fragment fragment = null;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_change_pin, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid().toString());

        textView = (TextView) rootView.findViewById(R.id.textView);
        pinView = (Pinview) rootView.findViewById(R.id.pinView);
        buttonProceed = (Button) rootView.findViewById(R.id.buttonProceed);

        textView.setText("Enter Pin Number");
        buttonProceed.setOnClickListener(new View.OnClickListener() {
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
        });

        return rootView;
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
            Toast.makeText(getActivity(), "Please enter the right pin number", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getActivity(), "Wrong pin number.Please try again", Toast.LENGTH_SHORT).show();
            return;
        }

        Integer pin = Integer.valueOf(p1);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        databaseReference.child("Pin").setValue(pin);
        Toast.makeText(getActivity(), "Successfully Changed Pin", Toast.LENGTH_SHORT).show();
        fragment = new ProfileActivity();
        replaceFragment(fragment);
    }
    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}
