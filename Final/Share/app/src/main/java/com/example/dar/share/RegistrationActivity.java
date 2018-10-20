package com.example.dar.share;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static java.lang.Boolean.FALSE;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int PICK_IMAGE_REQUEST = 234;
    private FirebaseAuth firebaseAuth;

    private EditText editTextFName;
    private EditText editTextLName;
    private Spinner spinnerGender;
    private EditText editTextContactNumber;
    private EditText editTextGContactNumber;
    private Button buttonFile;
    private Button buttonProceed;

    private StorageReference storageReference;

    private Uri filePath;

    private DatabaseReference databaseReference;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        firebaseAuth = FirebaseAuth.getInstance();

        user = firebaseAuth.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        editTextFName = (EditText) findViewById(R.id.editTextFName);
        editTextLName = (EditText) findViewById(R.id.editTextLName);
        spinnerGender = (Spinner) findViewById(R.id.spinnerGender);
        editTextContactNumber = (EditText) findViewById(R.id.editTextNumber);
        editTextGContactNumber = (EditText) findViewById(R.id.editTextGuardianNumber);
        buttonFile = (Button)findViewById(R.id.buttonFile);
        buttonProceed = (Button) findViewById(R.id.buttonProceed);


        String[] items = new String[]{"Male", "Female"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinnerGender.setAdapter(adapter);

        buttonFile.setOnClickListener(this);
        buttonProceed.setOnClickListener(this);
    }

    private void saveUserInformation(){
        String FName = editTextFName.getText().toString().trim();
        String LName = editTextLName.getText().toString().trim();
        String Gender = spinnerGender.getSelectedItem().toString();
        String Num = editTextContactNumber.getText().toString().trim();
        String GuardianNum = editTextGContactNumber.getText().toString().trim();

        if(android.util.Patterns.PHONE.matcher(Num).matches() == FALSE){
            Toast.makeText(this, "Please Enter a correct phone number", Toast.LENGTH_LONG).show();
            editTextContactNumber.setText("");
            return;
        }else if(android.util.Patterns.PHONE.matcher(GuardianNum).matches() == FALSE){
            Toast.makeText(this, "Please Enter a correct phone number", Toast.LENGTH_LONG).show();
            editTextGContactNumber.setText("");
            return;
        }

        uploadFile();

        AddUserInformation addUserInformation = new AddUserInformation(FName, LName, Gender, Num, GuardianNum);

        databaseReference.child("users").child(user.getUid()).setValue(addUserInformation).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(RegistrationActivity.this, "Information Saved...", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), PinNumberActivity.class));
                }
            }
        });
    }

    private void uploadFile(){
        StorageReference riversRef = storageReference.child("profile/"+user.getUid().toString()+".jpg");
        riversRef.putFile(filePath);
    }

    private void showFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select an Image"), PICK_IMAGE_REQUEST);
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
            saveUserInformation();
        }else if(v == buttonFile){
            showFileChooser();
        }
    }
}

