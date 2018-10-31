package com.example.dar.share;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TaxiDetailsActivity extends AppCompatActivity {

    private Button buttonProceed;
    private EditText edittextPlateNum;
    private EditText edittextNum;
    private EditText edittextOperator;
    private String id;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi_details);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        buttonProceed = (Button) findViewById(R.id.buttonProceed);
        edittextPlateNum = (EditText) findViewById(R.id.editTextPlateNum);
        edittextNum = (EditText) findViewById(R.id.editTextNum);
        edittextOperator = (EditText) findViewById(R.id.editTextOperator);
        id = getIntent().getExtras().get("id").toString();

        buttonProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String plate = edittextPlateNum.getText().toString();
                String num = edittextNum.getText().toString();
                String operator = edittextOperator.getText().toString();
                Taxi taxi = new Taxi(plate, num, operator);
                databaseReference.child("travel").child(id).child("taxi").setValue(taxi);

                databaseReference.child("travel").child(id).child("Available").setValue("0");

                Intent intent = new Intent(TaxiDetailsActivity.this, InsideRoomActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

    }
}
