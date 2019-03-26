package com.example.dar.pin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;

public class MainActivity extends AppCompatActivity {

    private int i = 0;

    private Pinview pinview;
    private Button buttonProceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pinview = (Pinview) findViewById(R.id.pinView);
        buttonProceed = (Button) findViewById(R.id.buttonProceed);

        buttonProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearPinViewChild();
            }
        });
    }

    private void clearPinViewChild() {
        for (int i = 0; i < pinview.getChildCount() ; i++) {
            EditText child = (EditText) pinview.getChildAt(i);
            child.setText("");

        }
    }
}
