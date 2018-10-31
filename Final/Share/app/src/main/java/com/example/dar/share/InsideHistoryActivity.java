package com.example.dar.share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class InsideHistoryActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private TextView textViewId;
    private TextView textViewOrigin;
    private TextView textViewDestination;
    private TextView textViewDeparture;
    private TextView textViewTime;
    private TextView textViewFare;
    private TextView textViewPlateNum;
    private TextView textViewNum;
    private TextView textViewOperator;
    private LinearLayout linearLayout;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inside_history);

        databaseReference = FirebaseDatabase.getInstance().getReference("travel").child(getIntent().getExtras().get("id").toString());
        storageReference = FirebaseStorage.getInstance().getReference("travel/" + getIntent().getExtras().get("id").toString() + ".jpg");

        textViewId = (TextView) findViewById(R.id.textViewId);
        textViewOrigin = (TextView) findViewById(R.id.textViewOrigin);
        textViewDestination = (TextView) findViewById(R.id.textViewDestination);
        textViewDeparture = (TextView) findViewById(R.id.textViewDeparture);
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        textViewFare = (TextView) findViewById(R.id.textViewFare);
        textViewPlateNum = (TextView) findViewById(R.id.textViewPlateNum);
        textViewNum = (TextView) findViewById(R.id.textViewNum);
        textViewOperator = (TextView) findViewById(R.id.textViewOperator);
        textViewId.setText(getIntent().getExtras().get("id").toString());
        linearLayout = (LinearLayout) findViewById(R.id.linearLayoutMembers);
        imageView =(ImageView) findViewById(R.id.imageView);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                textViewOrigin.setText(dataSnapshot.child("OriginString").getValue().toString());
                textViewDestination.setText(dataSnapshot.child("DestinationString").getValue().toString());
                String hourString = dataSnapshot.child("DepartureHour").getValue().toString();
                int hour = Integer.parseInt(hourString);
                String p;
                if(hour > 12){
                    p = "pm";
                    hour = hour - 12;
                }else{
                    p = "am";
                }
                textViewDeparture.setText(hour+":"+dataSnapshot.child("DepartureMinute").getValue().toString() +" "+p);
                textViewTime.setText(dataSnapshot.child("EstimatedTravelTime").getValue().toString() + " minute(s)");
                textViewFare.setText("PHP "+dataSnapshot.child("FareFrom").getValue().toString()+"-"+dataSnapshot.child("FareTo").getValue().toString());
                textViewPlateNum.setText(dataSnapshot.child("taxi").child("PlateNumber").getValue().toString());
                textViewNum.setText(dataSnapshot.child("taxi").child("TaxiNumber").getValue().toString());
                textViewOperator.setText(dataSnapshot.child("taxi").child("Operator").getValue().toString());

                for(DataSnapshot data : dataSnapshot.child("users").getChildren()){
                    DatabaseReference ref;
                    if(data.hasChildren()){
                        ref = FirebaseDatabase.getInstance().getReference("users").child(data.child("UserID").getValue().toString());
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String Fname = dataSnapshot.child("Fname").getValue().toString();
                                String Lname = dataSnapshot.child("Lname").getValue().toString();
                                TextView textView1 = new TextView(InsideHistoryActivity.this);
                                textView1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT));
                                textView1.setText(data.child("Name").getValue().toString()+" (With: "+Fname + " " + Lname+")");
                                linearLayout.addView(textView1);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }else{
                        ref = FirebaseDatabase.getInstance().getReference("users").child(data.getValue().toString());
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String Fname = dataSnapshot.child("Fname").getValue().toString();
                                String Lname = dataSnapshot.child("Lname").getValue().toString();
                                TextView textView1 = new TextView(InsideHistoryActivity.this);
                                textView1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT));
                                textView1.setText(Fname + " " + Lname);
                                linearLayout.addView(textView1);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        DisplayMetrics dm = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(dm);

                        imageView.setImageBitmap(bm);
                    }
                });
    }
}
