package com.example.dar.systemdesign;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ProfileActivity extends Fragment {

    private TextView textViewName;
    private TextView textViewEmail;
    private ImageView imageView;
    private Button buttonEdit;
    private Fragment fragment = null;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

        final FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid().toString());
        storageReference = FirebaseStorage.getInstance().getReference("profile/" + user.getUid().toString() + ".jpg");

        textViewName = (TextView) rootView.findViewById(R.id.textViewName);
        textViewEmail = (TextView) rootView.findViewById(R.id.textViewEmail);
        buttonEdit = (Button) rootView.findViewById(R.id.buttonEdit);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Toast.makeText(getActivity(), "Please finish registration", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), RegistrationActivity.class));
                } else {
                    String Fname = dataSnapshot.child("Fname").getValue().toString();
                    String Lname = dataSnapshot.child("Lname").getValue().toString();
                    textViewName.setText(Fname + " " + Lname);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        textViewEmail.setText(user.getEmail());

        final long ONE_MEGABYTE = 1024 * 1024 * 5;
        storageReference.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        float aspectRatio = bm.getWidth() /
                                (float) bm.getHeight();

                        int width = 90;
                        int height = Math.round(width / aspectRatio);

                        bm = Bitmap.createScaledBitmap(
                                bm, width, height, false);

                        imageView.setImageBitmap(bm);
                    }
                });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new EditUserActivity();
                replaceFragment(fragment);
            }
        });

        return rootView;
    }

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
