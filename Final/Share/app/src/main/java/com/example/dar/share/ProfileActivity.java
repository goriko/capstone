package com.example.dar.share;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    private ImageView imageView;
    private TextView textViewUserEmail;
    private Button buttonPin;
    private Button buttonInfo;
    private Button buttonLogout;
    private Button buttonPic;
    private Button buttonMap;
    private Button buttonJoin;
    private Button buttonHistory;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        final FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid().toString());
        storageReference = FirebaseStorage.getInstance().getReference("profile/" + user.getUid().toString() + ".jpg");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Toast.makeText(ProfileActivity.this, "Please finish registration", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ProfileActivity.this, RegistrationActivity.class));
                } else {
                    String Fname = dataSnapshot.child("Fname").getValue().toString();
                    String Lname = dataSnapshot.child("Lname").getValue().toString();
                    textViewUserEmail.setText("Welcome " + Fname + " " + Lname);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imageView = (ImageView) findViewById(R.id.imageView);
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        buttonPin = (Button) findViewById(R.id.buttonPin);
        buttonInfo = (Button) findViewById(R.id.buttonInfo);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonMap = (Button) findViewById(R.id.buttonMap);
        buttonJoin = (Button) findViewById(R.id.buttonJoin);
        buttonHistory = (Button) findViewById(R.id.buttonHistory);
        buttonPic = (Button) findViewById(R.id.buttonPic);


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

        buttonPin.setOnClickListener(this);
        buttonInfo.setOnClickListener(this);
        buttonPic.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);
        buttonMap.setOnClickListener(this);
        buttonJoin.setOnClickListener(this);
        buttonHistory.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonLogout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        } else if (v == buttonPin) {
            startActivity(new Intent(this, ChangePinActivity.class));
        } else if (v == buttonInfo) {
            startActivity(new Intent(this, UserInfoActivity.class));
        } else if (v == buttonPic) {
            startActivity(new Intent(this, UpdatePicActivity.class));
        } else if (v == buttonMap) {
            startActivity(new Intent(this, MapsActivity.class));
        } else if (v == buttonJoin) {
            startActivity(new Intent(this, JoinRoomActivity.class));
        }else if(v == buttonHistory){
            startActivity(new Intent(this, HistoryActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ProfileActivity.this);
        builder1.setMessage("Are you sure you want to exit?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        System.exit(0);
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}