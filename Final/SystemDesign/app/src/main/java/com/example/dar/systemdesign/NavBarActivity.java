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

public class NavBarActivity extends AppCompatActivity {
    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;
    private Fragment fragment;
    public static Context sContext;
    public static AlarmManager alarmManager_time;
    public static AlarmManager alarmManager_advance;
    private Integer j=0, x, i =0;
    public static String Id = null, userid, Pic;
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
        if(getIntent().hasExtra("id")){
            room();
        }else{
            load();
        }

        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager fragmentManager= getSupportFragmentManager();
                FragmentTransaction transaction= fragmentManager.beginTransaction();

                switch (item.getItemId()){
                    case R.id.nav_home:
                        mMainNav.setItemBackgroundResource(R.color.colorPrimary);
                        transaction.replace(R.id.main_frame, new ProfileActivity()).commit();
                        return true;


                    case R.id.nav_room:
                        if(Id == null){
                            mMainNav.setItemBackgroundResource(R.color.colorAccent);
                            transaction.replace(R.id.main_frame, new RoomActivity()).addToBackStack(null).commit();
                        }else{
                            if(Pic.equals("start")){
                                fragment = new TravelActivity(Id);
                                replaceFragment(fragment);
                            }else{
                                fragment = new InsideRoomActivity(Id, null);
                                replaceFragment(fragment);
                            }
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

    public void load(){
        FragmentManager fragmentManager= getSupportFragmentManager();
        FragmentTransaction transaction= fragmentManager.beginTransaction();
        mMainNav.setItemBackgroundResource(R.color.colorPrimary);
        transaction.replace(R.id.main_frame, new ProfileActivity()).commit();
    }

    public void room(){
        fragment = new InsideRoomActivity(getIntent().getExtras().get("id").toString(), getIntent().getExtras().get("pic").toString());
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
        }else{
            if(getSupportFragmentManager().findFragmentByTag("InsideRoom") != null){
                if(Pic.equals("start")){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                    builder1.setMessage("Unable to exit room while travelling");
                    builder1.setCancelable(true);

                    builder1.setNegativeButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }else{
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                    builder1.setMessage("Are you sure you want to exit this room?");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    delete();
                                    Id = null;
                                    getFragmentManager().popBackStackImmediate();
                                    getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryCount()-1, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
            }else{
                Log.d("EYY", "dasdasdadadsdasdad");
                getFragmentManager().popBackStackImmediate();
                getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryCount()-1, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        fragment = new InsideRoomActivity(intent.getExtras().get("id").toString(), intent.getExtras().get("pic").toString());
        replaceFragment(fragment);
    }
    public void replaceFragment(Fragment someFragment) {
        FragmentManager fragmentManager= getSupportFragmentManager();
        FragmentTransaction transaction= fragmentManager.beginTransaction();;
        transaction.replace(R.id.main_frame, someFragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }
    public void delete(){
        databaseReference = FirebaseDatabase.getInstance().getReference("travel").child(Id);
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
        Intent intent_time = new Intent(this, NotificationTime.class);
        PendingIntent pendingIntent_time = PendingIntent.getBroadcast(this, 24444, intent_time, 0);
        alarmManager_time.cancel(pendingIntent_time);

        Intent intent_advance = new Intent(this, NotificationAdvance.class);
        PendingIntent pendingIntent_advance = PendingIntent.getBroadcast(this, 24444, intent_advance, 0);
        alarmManager_advance.cancel(pendingIntent_advance);
        j=0;
    }
}
