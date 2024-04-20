package com.example.smsapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.Manifest;
import android.widget.TextView;
import android.widget.Toast;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseDrawerActivity  {
    private List<SmsMessage> smsList = new ArrayList<>();
    private ListView listView;
    private SmsAdapter adapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("SMS_RECEIVED_ACTION");
        registerReceiver(smsReceiver, filter);
        readSms();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(smsReceiver);
    }

    private BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String sender = intent.getStringExtra("sender");
            String messageBody = intent.getStringExtra("messageBody");
            long timestamp = intent.getLongExtra("timestamp", System.currentTimeMillis());
            readSms();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        listView = (ListView) findViewById(R.id.listView);
        adapter = new SmsAdapter(this, smsList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SmsMessage clickedMessage = smsList.get(position);
                openConversation(clickedMessage);
            }
        });

        checkAndRequestPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 123: {
                if (grantResults.length > 0) {
                    boolean allPermissionsGranted = true;
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            allPermissionsGranted = false;
                            break;
                        }
                    }
                    if (allPermissionsGranted) {
                        readSms();
                    } else {
                        Toast.makeText(this, "Nie przyznano uprawnień", Toast.LENGTH_SHORT).show();
                    }
                }
                return;
            }
        }
    }

    private void checkAndRequestPermissions() {
        String[] permissions = {
                Manifest.permission.READ_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_CONTACTS
        };

        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 123);
        } else {
            readSms();
        }
    }

    private void readSms() {
        Uri uriSms = Uri.parse("content://sms/");
        Cursor cursor = getContentResolver().query(uriSms, new String[]{"_id", "address", "date", "body", "type"},
                "address IS NOT NULL", null, "date DESC");

        smsList.clear();
        HashMap<String, SmsMessage> lastMessages = new HashMap<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String address = cursor.getString(1);

                if (!lastMessages.containsKey(address)) {
                    String date = cursor.getString(2);
                    String body = cursor.getString(3);
                    int type = cursor.getInt(4);
                    SmsMessage sms = new SmsMessage(address, date, body, type);
                    lastMessages.put(address, sms);
                }
            }
            cursor.close();
        }

        smsList.addAll(lastMessages.values());
        adapter.notifyDataSetChanged();
    }
    private void openConversation(SmsMessage message) {
        Intent intent= new Intent(MainActivity.this, ConversationActivity.class);
        intent.putExtra("PHONE_NUMBER", message.getSender());
        startActivity(intent);
    }

}