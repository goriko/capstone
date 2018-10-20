package com.example.dar.share;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class CreateTravel{

    public DatabaseReference databaseReference;
    public FirebaseUser user;
    public String mKey;

    public String create(LatLng origin,
                         LatLng destination,
                         String originString,
                         String destinationString,
                         Integer fareFrom,
                         Integer fareTo,
                         Integer departureHour,
                         Integer departureMinute,
                         Integer estimatedTravelTime){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mKey = UUID.randomUUID().toString();

        AddTravelInformation addTravelInformation = new AddTravelInformation(origin, destination, originString, destinationString, 1, 1, fareFrom, fareTo, departureHour, departureMinute, estimatedTravelTime);
        databaseReference.child("travel").child(mKey).setValue(addTravelInformation);

        AddLeaderID addLeaderID = new AddLeaderID(user.getUid().toString());
        databaseReference.child("travel").child(mKey).child("users").setValue(addLeaderID);

        return mKey;
    }

}