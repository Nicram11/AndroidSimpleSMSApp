package com.example.smsapp;

import static android.telephony.PhoneNumberUtils.formatNumber;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ConversationActivity extends BaseDrawerActivity {
    SmsConversationAdapter adapter;
    List<SmsMessage> conversation = new ArrayList<>();
    private String currentPhoneNumber;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_conversation;
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("SMS_RECEIVED_ACTION");
        registerReceiver(messageReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(messageReceiver);
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String incomingPhoneNumber = intent.getStringExtra("sender");
            if (incomingPhoneNumber != null && incomingPhoneNumber.equals(currentPhoneNumber)) {
                loadConversation(currentPhoneNumber);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EditText editTextMessage = findViewById(R.id.editTextMessage);
        Button buttonSend = findViewById(R.id.buttonSend);



        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
 currentPhoneNumber = getIntent().getStringExtra("PHONE_NUMBER");
        if(currentPhoneNumber != null) {
            loadConversation(currentPhoneNumber);
            TextView contactInfoView = findViewById(R.id.text_contact_info);
            contactInfoView.setText(currentPhoneNumber);
        } else {
            Log.d("ConversationActivity", "Numer telefonu jest null");
        }

        IntentFilter filter = new IntentFilter("SMS_RECEIVED_ACTION");
        registerReceiver(messageReceiver, filter);


        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageToSend = editTextMessage.getText().toString();
                if (!messageToSend.isEmpty()) {
                    sendMessage(currentPhoneNumber,messageToSend);
                    adapter.notifyDataSetChanged();
                    editTextMessage.setText("");
                }
            }
        });

    }
    private void sendMessage(String phoneNumber, String messageText) {
        if (phoneNumber == null || phoneNumber.isEmpty() || messageText == null || messageText.isEmpty()) {
            Toast.makeText(this, "Numer telefonu lub wiadomość jest pusta.", Toast.LENGTH_SHORT).show();
            return;
        }

        SmsManager smsManager = SmsManager.getDefault();

        try {
            smsManager.sendTextMessage(phoneNumber, null, messageText, null, null);
            Toast.makeText(this, "Wiadomość wysłana", Toast.LENGTH_SHORT).show();
            SmsMessage sentMessage = new SmsMessage(phoneNumber, String.valueOf(Instant.now().toEpochMilli()), messageText, 2);
            conversation.add(sentMessage);
            updateUI(conversation);

        } catch (Exception e) {
            Toast.makeText(this, "Błąd podczas wysyłania wiadomości: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void loadConversation(String phoneNumber) {

        Uri uri = Uri.parse("content://sms");
        String[] projection = new String[] {"_id", "address", "date", "body", "type"};
        String selection = "(address = ? OR address = ?)";
        String[] selectionArgs = {phoneNumber, formatNumber(phoneNumber)};
        conversation.clear();
        Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, "date ASC");

        if (cursor != null && cursor.moveToFirst()) {
            int indexAddress = cursor.getColumnIndex("address");
            int indexBody = cursor.getColumnIndex("body");
            int indexDate = cursor.getColumnIndex("date");
            int indexType = cursor.getColumnIndex("type");

            do {
                String sender = cursor.getString(indexAddress);
                String body = cursor.getString(indexBody);
                String date = cursor.getString(indexDate);
                int type = cursor.getInt(indexType);

                SmsMessage message = new SmsMessage(sender, date, body, type);
                conversation.add(message);
            } while (cursor.moveToNext());

            cursor.close();
        }

        updateUI(conversation);
    }

    private void updateUI(List<SmsMessage> conversation) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
         adapter = (SmsConversationAdapter) recyclerView.getAdapter();

        if (adapter != null) {
            adapter.setMessages(conversation);
            adapter.notifyDataSetChanged();
        } else {
            adapter = new SmsConversationAdapter(conversation);
            recyclerView.setAdapter(adapter);
        }
    }
}