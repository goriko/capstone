package com.example.administrator.share;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;

    private EditText editTextFName;
    private EditText editTextLName;
    private Spinner spinnerGender;
    private EditText editTextContactNumber;
    private EditText editTextGContactNumber;
    private EditText editTextPinNumber;
    private EditText editTextPinNumber2;
    private Button buttonProceed;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        editTextFName = (EditText) findViewById(R.id.editTextFName);
        editTextLName = (EditText) findViewById(R.id.editTextLName);
        spinnerGender = (Spinner) findViewById(R.id.spinnerGender);
        editTextContactNumber = (EditText) findViewById(R.id.editTextNumber);
        editTextGContactNumber = (EditText) findViewById(R.id.editTextGuardianNumber);
        editTextPinNumber = (EditText) findViewById(R.id.editTextPinNumber);
        editTextPinNumber2 = (EditText) findViewById(R.id.editTextPinNumber2);
        buttonProceed = (Button) findViewById(R.id.buttonProceed);

        String[] items = new String[]{"Male", "Female"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinnerGender.setAdapter(adapter);

        buttonProceed.setOnClickListener(this);
    }

    private void saveUserInformation(){
        String FName = editTextFName.getText().toString().trim();
        String LName = editTextLName.getText().toString().trim();
        String Gender = spinnerGender.getSelectedItem().toString();
        String Num = editTextContactNumber.getText().toString().trim();
        String GuardianNum = editTextGContactNumber.getText().toString().trim();
        String PinNum = editTextPinNumber.getText().toString().trim();
        String PinNum2 = editTextPinNumber2.getText().toString().trim();

        if(!PinNum.equals(PinNum2)){
            Toast.makeText(this, "Not the same pin Number Please try again", Toast.LENGTH_SHORT).show();
            return;
        }

        Integer Number = Integer.valueOf(Num);
        Integer GuardianNumber = Integer.valueOf(GuardianNum);
        Integer PinNumber = Integer.valueOf(PinNum);
        Integer PinNumber2 = Integer.valueOf(PinNum2);

        UserInformation userInformation = new UserInformation(FName, LName, Gender, Number, GuardianNumber, PinNumber);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        databaseReference.child(user.getUid()).setValue(userInformation)
            .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                }
            });

        Toast.makeText(this, "Information Saved...", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onClick(View v) {
        if(v == buttonProceed){
            saveUserInformation();
        }
    }
}
