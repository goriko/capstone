package com.example.dar.systemdesign;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.Objects;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

import static com.example.dar.systemdesign.R.color.colorPrimary;

public class NavBarActivity extends AppCompatActivity {
    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;
    private Fragment fragment;
    public static Context sContext;
    public static String userid, roomId = null, roomStatus = null;
    public static AlarmManager alarmManager_time, alarmManager_advance;
    public Integer i = 0, j = 0, x;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_bar);
        sContext = getApplicationContext();

        mMainFrame= (FrameLayout) findViewById(R.id.main_frame);
        mMainNav=  (BottomNavigationView) findViewById(R.id.main_nav);
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        userid = user.getUid();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_frame, new ProfileActivity()).commit();

        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager fragmentManager= getSupportFragmentManager();
                FragmentTransaction transaction= fragmentManager.beginTransaction();

                switch (item.getItemId()){
                    case R.id.nav_home:
                        mMainNav.setItemBackgroundResource(colorPrimary);
                        transaction.replace(R.id.main_frame, new ProfileActivity()).commit();
                        return true;

                    case R.id.nav_room:
                        if (roomId == null){
                            mMainNav.setItemBackgroundResource(R.color.colorAccent);
                            transaction.replace(R.id.main_frame, new RoomActivity()).addToBackStack(null).commit();
                        }else{
                            fragment = new InsideRoomActivity(roomId, roomStatus);
                            replaceFragment(fragment);
                        }
                        return true;

                    case R.id.nav_settings:
                        mMainNav.setItemBackgroundResource(R.color.colorPrimaryDark);
                        transaction.replace(R.id.main_frame, new SettingsActivity()).addToBackStack(null).commit();
                        return true;

                    default:
                        return false;

                }
            }


        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        fragment = new InsideRoomActivity(intent.getExtras().get("id").toString(), intent.getExtras().get("status").toString());
        replaceFragment(fragment);
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() == 0){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Are you sure you want to exit?");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                            System.exit(0);
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
        }else if (getSupportFragmentManager().findFragmentByTag("InsideRoom") != null) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Are you sure you want to exit this room?");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            delete();
                            roomId = roomStatus = null;
                            fragment = new RoomActivity();
                            replaceFragment(fragment);
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
        }else{
            getFragmentManager().popBackStackImmediate();
            getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryCount()-1, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void replaceFragment(Fragment someFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_frame, someFragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    //remove user from room in db
    public void delete(){
        databaseReference = FirebaseDatabase.getInstance().getReference("travel").child(roomId);
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String value = data.getValue().toString();
                    String key = data.getKey().toString();
                    if (value.equals(userid)) {
                        if (key.equals("Leader")) {
                            i++;
                        }
                        databaseReference.child("users").child(key).removeValue();
                        j++;
                    }
                }
                if(i == 1){
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot data: dataSnapshot.getChildren()){
                                if (!data.hasChild("UserID") && i==1){
                                    databaseReference.child("users").child("Leader").setValue(data.getValue().toString());
                                    databaseReference.child("users").child(data.getKey()).removeValue();
                                    i=0;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){

            }
        });

        databaseReference.child("Guests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    if(data.child("CompanionId").getValue().toString().equals(userid)){
                        databaseReference.child("Guests").child(data.getKey().toString()).removeValue();
                        j++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("NoOfUsers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String NoOfUsers = dataSnapshot.getValue().toString();
                x = Integer.valueOf(NoOfUsers) - j;
                if(x == 0){
                    databaseReference.removeValue();
                }else{
                    databaseReference.child("Available").setValue(1);
                    databaseReference.child("NoOfUsers").setValue(x.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Intent intent_time = new Intent(sContext, NotificationTime.class);
        PendingIntent pendingIntent_time = PendingIntent.getBroadcast(sContext, 1, intent_time, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager_time.cancel(pendingIntent_time);

        Intent intent_advance = new Intent(sContext, NotificationAdvance.class);
        PendingIntent pendingIntent_advance = PendingIntent.getBroadcast(sContext, 1, intent_advance, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager_advance.cancel(pendingIntent_advance);

        x=j=i=0;
    }

}
