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

public class NavBarActivity extends AppCompatActivity {
    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;
    private Fragment fragment;
    public static Context sContext;
    public static AlarmManager alarmManager_time;
    public static AlarmManager alarmManager_advance;
    public static String Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_bar);
        sContext = getApplicationContext();

        mMainFrame= (FrameLayout) findViewById(R.id.main_frame);
        mMainNav=  (BottomNavigationView) findViewById(R.id.main_nav);

        load();

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
                        mMainNav.setItemBackgroundResource(R.color.colorAccent);
                        transaction.replace(R.id.main_frame, new RoomActivity()).addToBackStack(null).commit();
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
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage("Are you sure you want to exit this room?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(NavBarActivity.this, Id, Toast.LENGTH_SHORT).show();
                                delete();
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
            }else{
                getFragmentManager().popBackStackImmediate();
                getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryCount()-1, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        fragment = new InsideRoomActivity(intent.getExtras().get("id").toString());
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
        Intent intent_time = new Intent(this, NotificationTime.class);
        PendingIntent pendingIntent_time = PendingIntent.getBroadcast(this, 24444, intent_time, 0);
        alarmManager_time.cancel(pendingIntent_time);

        Intent intent_advance = new Intent(this, NotificationAdvance.class);
        PendingIntent pendingIntent_advance = PendingIntent.getBroadcast(this, 24444, intent_advance, 0);
        alarmManager_advance.cancel(pendingIntent_advance);
    }
}
