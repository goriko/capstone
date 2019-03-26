package com.example.dar.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;

public class InsideRoom extends AppCompatActivity {

    private TextView textView1;
    private Button button1;

    private NotificationHelper mNotoficationHelper;

    private String id;
    private int hour = 15, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inside_room);

        id = getIntent().getExtras().get("id").toString();
        if (id.equals("room1")){
            minute = 6;
        }else{
            minute = 4;
        }

        mNotoficationHelper = new NotificationHelper(this);

        textView1 = (TextView) findViewById(R.id.id);
        button1 = (Button) findViewById(R.id.button);
        textView1.setText(id);

        alarm(hour, minute);

    }

    public void alarm(int hour, int minute){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        alarmManager.cancel(pendingIntent);
    }
}
