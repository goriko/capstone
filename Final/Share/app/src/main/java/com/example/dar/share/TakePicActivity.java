package com.example.dar.share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

public class TakePicActivity extends AppCompatActivity {

    private Button buttonCamera;
    private Button buttonProceed;
    private ImageView imageView;
    private String id;
    private Bitmap bitmap;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_pic);

        id = getIntent().getExtras().get("id").toString();
        storageReference = FirebaseStorage.getInstance().getReference();

        buttonCamera = (Button) findViewById(R.id.buttonCamera);
        buttonProceed = (Button) findViewById(R.id.buttonProceed);
        imageView = (ImageView) findViewById(R.id.imageView);

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });

        buttonProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bitmap = (Bitmap)data.getExtras().get("data");
        imageView.setImageBitmap(bitmap);
    }

    private void uploadFile(){
        StorageReference riversRef = storageReference.child("travel/"+id+".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        riversRef.putBytes(data);

        Intent intent = new Intent(this, TaxiDetailsActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }
}
