package com.example.dar.share;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class InsideRoomActivity extends AppCompatActivity {

    private TextView textViewID;
    private LinearLayout linearLayout;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private DatabaseReference ref;
    private Integer x, i=0;
    private String id;
    private EmojiconEditText editTextMessage;
    private Button buttonSend;
    private LinearLayout messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inside_room);

        linearLayout = (LinearLayout) findViewById(R.id.layout);
        textViewID = (TextView) findViewById(R.id.textViewID);
        textViewID.setText(getIntent().getExtras().get("id").toString());
        editTextMessage = (EmojiconEditText) findViewById(R.id.editTextMessage);
        buttonSend = (Button) findViewById(R.id.buttonSend);
        messages = (LinearLayout) findViewById(R.id.message);

        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);

        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        id = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("travel").child(getIntent().getExtras().get("id").toString());

        databaseReference.child("NoOfUsers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String NoOfUsers = dataSnapshot.getValue().toString();
                x = Integer.valueOf(NoOfUsers) + 1;
                databaseReference.child("NoOfUsers").setValue(x.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(i == 0){
                    if(!dataSnapshot.hasChild("Member1")){
                        databaseReference.child("users").child("Member1").setValue(user.getUid());
                    }else if(!dataSnapshot.hasChild("Member2")){
                        databaseReference.child("users").child("Member2").setValue(user.getUid());
                    }else if(!dataSnapshot.hasChild("Member3")){
                        databaseReference.child("users").child("Member3").setValue(user.getUid());
                    }
                    i++;
                }else{
                    linearLayout.removeAllViews();
                    for(DataSnapshot data: dataSnapshot.getChildren()){
                        String str = data.getValue().toString();
                        ref = FirebaseDatabase.getInstance().getReference("users").child(str);
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String Fname = dataSnapshot.child("FName").getValue().toString();
                                String Lname = dataSnapshot.child("LName").getValue().toString();
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messages.removeAllViews();
                for (DataSnapshot data : dataSnapshot.getChildren()){
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
        super.onBackPressed();

        databaseReference.child("NoOfUsers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String NoOfUsers = dataSnapshot.getValue().toString();
                x = Integer.valueOf(NoOfUsers) - 1;
                databaseReference.child("NoOfUsers").setValue(x.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    String value = data.getValue().toString();
                    String key = data.getKey().toString();
                    if(value.equals(id)){
                        if(key.equals("Leader")){
                            databaseReference.removeValue();
                        }else{
                            databaseReference.child("users").child(key).removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

