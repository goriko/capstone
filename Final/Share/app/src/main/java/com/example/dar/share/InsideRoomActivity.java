package com.example.dar.share;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class InsideRoomActivity extends AppCompatActivity {

    private TextView textViewID;
    private LinearLayout linearLayout;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private DatabaseReference ref;
    private Integer x, i = 0, j=0;
    private String id, roomid;
    private EmojiconEditText editTextMessage;
    private Button buttonSend;
    private Button buttonPic;
    private Button buttonGuest;
    private LinearLayout messages;
    private AlarmManager alarmManager_time;
    private AlarmManager alarmManager_advance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inside_room);

        roomid = getIntent().getExtras().get("id").toString();

        linearLayout = (LinearLayout) findViewById(R.id.layout);
        textViewID = (TextView) findViewById(R.id.textViewID);
        editTextMessage = (EmojiconEditText) findViewById(R.id.editTextMessage);
        textViewID.setText(roomid);
        buttonSend = (Button) findViewById(R.id.buttonSend);
        buttonPic = (Button) findViewById(R.id.buttonPic);
        buttonGuest = (Button) findViewById(R.id.buttonGuest);
        messages = (LinearLayout) findViewById(R.id.message);

        buttonPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InsideRoomActivity.this, TakePicActivity.class);
                intent.putExtra("id", roomid);
                startActivity(intent);
            }
        });

        buttonGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guest();
            }
        });

        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);

        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        id = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("travel").child(getIntent().getExtras().get("id").toString());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                alarm(Integer.valueOf(dataSnapshot.child("DepartureHour").getValue().toString()), Integer.valueOf(dataSnapshot.child("DepartureMinute").getValue().toString()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(InsideRoomActivity.this);
                    builder1.setMessage("Leader has left the room. Redirecting to rooms");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startActivity(new Intent(InsideRoomActivity.this, JoinRoomActivity.class));
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                } else {
                    linearLayout.removeAllViews();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        if(data.hasChild("UserID")){
                            String str = data.child("UserID").getValue().toString();
                            String name = data.child("Name").getValue().toString();
                            ref = FirebaseDatabase.getInstance().getReference("users").child(str);
                            ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String Fname = dataSnapshot.child("Fname").getValue().toString();
                                    String Lname = dataSnapshot.child("Lname").getValue().toString();
                                    TextView textView1 = new TextView(InsideRoomActivity.this);
                                    textView1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT));
                                    textView1.setText("Guest: " + name + " (with: "+ Fname+" "+Lname+")");
                                    linearLayout.addView(textView1);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }else{
                            String str = data.getValue().toString();
                            ref = FirebaseDatabase.getInstance().getReference("users").child(str);
                            ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String Fname = dataSnapshot.child("Fname").getValue().toString();
                                    String Lname = dataSnapshot.child("Lname").getValue().toString();
                                    TextView textView1 = new TextView(InsideRoomActivity.this);
                                    textView1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT));
                                    textView1.setText(Fname + " " + Lname);
                                    linearLayout.addView(textView1);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messages.removeAllViews();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    LinearLayout samp = new LinearLayout(InsideRoomActivity.this);
                    samp.setBackgroundResource(R.drawable.customborder);

                    String message = data.child("MessageText").getValue().toString();
                    String user = data.child("MessageUser").getValue().toString();
                    long time = Long.parseLong(data.getKey().toString());
                    String dateString = new SimpleDateFormat("dd-MM-yyyy (h:mm a)").format(new Date(time));

                    TextView textView1 = new TextView(InsideRoomActivity.this);
                    textView1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    textView1.setText("User: " + user + "\nMessage: " + message + "\nTime: " + dateString);

                    samp.addView(textView1);
                    messages.addView(samp);
                }
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = editTextMessage.getText().toString();
                Message m = new Message(str, user.getUid().toString());
                String date = String.valueOf(new Date().getTime());
                databaseReference.child("messages").child(date).setValue(m);
                editTextMessage.setText("");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(InsideRoomActivity.this);
        builder1.setMessage("Are you sure you want to leave this group?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        delete();
                        startActivity(new Intent(InsideRoomActivity.this, JoinRoomActivity.class));
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    public void delete() {
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if(data.hasChild("UserID")){
                        String value = data.child("UserID").getValue().toString();
                        String key = data.getKey().toString();
                        if(value.equals(id)){
                            databaseReference.child("users").child(key).removeValue();
                            j++;
                        }
                    }else {
                        String value = data.getValue().toString();
                        String key = data.getKey().toString();
                        if (value.equals(id)) {
                            if (key.equals("Leader")) {
                                databaseReference.removeValue();
                            } else {
                                databaseReference.child("users").child(key).removeValue();
                                j++;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(InsideRoomActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference.child("NoOfUsers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String NoOfUsers = dataSnapshot.getValue().toString();
                x = Integer.valueOf(NoOfUsers) - j;
                databaseReference.child("Available").setValue(1);
                databaseReference.child("NoOfUsers").setValue(x.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Intent intent_time = new Intent(InsideRoomActivity.this, NotificationTime.class);
        intent_time.putExtra("id", getIntent().getExtras().get("id").toString());
        PendingIntent pendingIntent_time = PendingIntent.getBroadcast(InsideRoomActivity.this, 24444, intent_time, 0);
        alarmManager_time.cancel(pendingIntent_time);

        Intent intent_advance = new Intent(InsideRoomActivity.this, NotificationAdvance.class);
        intent_advance.putExtra("id", getIntent().getExtras().get("id").toString());
        PendingIntent pendingIntent_advance = PendingIntent.getBroadcast(InsideRoomActivity.this, 24444, intent_advance, 0);
        alarmManager_advance.cancel(pendingIntent_advance);
    }

    public void alarm(int departureHour, int departureMinute) {
        alarmManager_time = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager_advance = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Date date = new Date();

        Calendar alarm_time = Calendar.getInstance();
        Calendar alarm_advance = Calendar.getInstance();
        Calendar cal_now = Calendar.getInstance();

        alarm_time.setTime(date);
        alarm_advance.setTime(date);
        cal_now.setTime(date);

        alarm_time.set(Calendar.HOUR_OF_DAY, departureHour);
        alarm_time.set(Calendar.MINUTE, departureMinute);
        alarm_time.set(Calendar.SECOND, 0);

        if (departureMinute >= 5) {
            alarm_advance.set(Calendar.HOUR_OF_DAY, departureHour);
            alarm_advance.set(Calendar.MINUTE, departureMinute - 5);
            alarm_advance.set(Calendar.SECOND, 0);
        } else {
            int i = 5 - departureMinute;
            alarm_advance.set(Calendar.HOUR_OF_DAY, departureHour - 1);
            alarm_advance.set(Calendar.MINUTE, 60 - i);
            alarm_advance.set(Calendar.SECOND, 0);
        }

        if (alarm_time.before(cal_now)) {
            alarm_time.add(Calendar.DATE, 1);
        }

        Intent intent_time = new Intent(InsideRoomActivity.this, NotificationTime.class);
        intent_time.putExtra("id", getIntent().getExtras().get("id").toString());
        PendingIntent pendingIntent_time = PendingIntent.getBroadcast(InsideRoomActivity.this, 24444, intent_time, 0);
        alarmManager_time.set(AlarmManager.RTC_WAKEUP, alarm_time.getTimeInMillis(), pendingIntent_time);

        if (alarm_advance.before(cal_now)) {
            alarm_advance.add(Calendar.DATE, 1);
        }

        Intent intent_advance = new Intent(InsideRoomActivity.this, NotificationAdvance.class);
        intent_advance.putExtra("id", getIntent().getExtras().get("id").toString());
        PendingIntent pendingIntent_advance = PendingIntent.getBroadcast(InsideRoomActivity.this, 24444, intent_advance, 0);
        alarmManager_advance.set(AlarmManager.RTC_WAKEUP, alarm_advance.getTimeInMillis(), pendingIntent_advance);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    if(data.getValue().equals(id) && data.getKey().equals("Leader")){
                        buttonPic.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void guest(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Enter guest name");

        EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AddMember guest = new AddMember();
                guest.add(roomid, input.getText().toString());
            }
        });

        alertDialog.show();
    }
}
