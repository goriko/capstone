package com.example.guanzon.share;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;

    private ImageView imageView;
    private TextView textViewUserEmail;
    private Button buttonPin;
    private Button buttonInfo;
    private Button buttonLogout;
    private Button buttonPic;
    private Button buttonMap;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        final FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid().toString());
        storageReference = FirebaseStorage.getInstance().getReference("profile/"+user.getUid().toString()+".jpg");

        imageView = (ImageView) findViewById(R.id.imageView);
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        buttonPin = (Button) findViewById(R.id.buttonPin);
        buttonInfo = (Button) findViewById(R.id.buttonInfo);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonMap = (Button) findViewById(R.id.buttonMap);
        buttonPic = (Button) findViewById(R.id.buttonPic);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String Fname = dataSnapshot.child("FName").getValue().toString();
                String Lname = dataSnapshot.child("LName").getValue().toString();
                textViewUserEmail.setText("Welcome "+Fname+" "+Lname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Please finish registration", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProfileActivity.this, RegistrationActivity.class));
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

        buttonPin.setOnClickListener(this);
        buttonInfo.setOnClickListener(this);
        buttonPic.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);
        buttonMap.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == buttonLogout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }else if(v == buttonPin){
            startActivity(new Intent(this, ChangePinActivity.class));
        }else if(v == buttonInfo){
            startActivity(new Intent(this, UserInfoActivity.class));
        }else if(v == buttonPic){
            startActivity(new Intent(this, UpdatePicActivity.class));
        }else if(v == buttonMap){
            startActivity(new Intent(this, MapActivity.class));
        }
    }
}

