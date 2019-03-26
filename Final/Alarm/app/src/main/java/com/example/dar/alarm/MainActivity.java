package com.example.dar.alarm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textView1, textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView1 = (TextView) findViewById(R.id.room1);
        textView2 = (TextView) findViewById(R.id.room2);

        textView1.setOnClickListener(this);
        textView2.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(this, InsideRoom.class);
        if(v == textView1){
            i.putExtra("id", "room1");
            startActivity(i);
        }else{
            i.putExtra("id", "room2");
            startActivity(i);
        }
    }
}
