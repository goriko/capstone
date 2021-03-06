package com.example.dar.systemdesign;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

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

import java.io.IOException;

public class EditUserActivity extends Fragment {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Fragment fragment = null;
    private EditText editTextfname, editTextlname, editTextnumber, editTextgnumber;
    private ImageView imageView;
    private Spinner spinnerGender;
    private LinearLayout uploadPhoto;
    private Button buttonSave;
    private Uri filePath = null;
    private static final int PICK_IMAGE_REQUEST = 234;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_user, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid().toString());
        storageReference = FirebaseStorage.getInstance().getReference("profile/" + user.getUid().toString() + ".jpg");

        editTextfname = (EditText) rootView.findViewById(R.id.editTextFName);
        editTextlname = (EditText) rootView.findViewById(R.id.editTextLName);
        editTextnumber = (EditText) rootView.findViewById(R.id.editTextNumber);
        editTextgnumber = (EditText) rootView.findViewById(R.id.editTextGuardianNumber);
        spinnerGender = (Spinner) rootView.findViewById(R.id.spinnerGender);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        uploadPhoto = (LinearLayout) rootView.findViewById(R.id.uploadPhoto);
        buttonSave = (Button) rootView.findViewById(R.id.buttonProceed);

        String[] items = new String[]{"Male", "Female"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        spinnerGender.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                editTextfname.setText(dataSnapshot.child("Fname").getValue().toString());
                editTextlname.setText(dataSnapshot.child("Lname").getValue().toString());
                editTextnumber.setText(dataSnapshot.child("ContactNumber").getValue().toString());
                editTextgnumber.setText(dataSnapshot.child("EmergencyContact").getValue().toString());
                if (dataSnapshot.child("Gender").getValue().toString().equals("Male")) {
                    spinnerGender.setSelection(0);
                } else {
                    spinnerGender.setSelection(1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("Fname").setValue(editTextfname.getText().toString());
                databaseReference.child("Lname").setValue(editTextlname.getText().toString());
                databaseReference.child("ContactNumber").setValue(editTextnumber.getText().toString());
                databaseReference.child("EmergencyContact").setValue(editTextgnumber.getText().toString());
                databaseReference.child("Gender").setValue(spinnerGender.getSelectedItem().toString());

                if (filePath != null){
                    storageReference.putFile(filePath);
                }

                fragment = new EditUserActivity();
                replaceFragment(fragment);

            }
        });

        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select an Image"), PICK_IMAGE_REQUEST);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();
            Bitmap bitmap;
            try {
                 bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                float aspectRatio = bitmap.getWidth() /
                        (float) bitmap.getHeight();

                int width = 90;
                int height = Math.round(width / aspectRatio);

                bitmap = Bitmap.createScaledBitmap(
                        bitmap, width, height, false);

                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
