package com.example.administrator.share;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UpdatePicActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonFile;
    private Button buttonProceed;

    private static final int PICK_IMAGE_REQUEST = 234;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pic);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        buttonFile = (Button)findViewById(R.id.buttonFile);
        buttonProceed = (Button) findViewById(R.id.buttonProceed);

        buttonFile.setOnClickListener(this);
        buttonProceed.setOnClickListener(this);
    }

    private void showFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select an Image"), PICK_IMAGE_REQUEST);
    }

    private void uploadFile(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        StorageReference riversRef = storageReference.child("profile/"+user.getUid().toString()+".jpg");
        riversRef.putFile(filePath);
        startActivity(new Intent(this, ProfileActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();
        }
    }

    @Override
    public void onClick(View v) {
        if(v == buttonProceed){
            uploadFile();
        }else if(v == buttonFile){
            showFileChooser();
        }
    }
}
