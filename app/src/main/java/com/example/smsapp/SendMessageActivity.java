package com.example.smsapp;

import static android.telephony.PhoneNumberUtils.formatNumber;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SendMessageActivity extends BaseDrawerActivity {

    private EditText editTextPhoneNumber;
    private EditText editTextMessageText;
    private Button buttonSendMessage;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_send_message;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextMessageText = findViewById(R.id.editTextMessageText);
        buttonSendMessage = findViewById(R.id.buttonSendMessage);

        buttonSendMessage.setOnClickListener(v -> sendSmsMessage());
        loadContacts();
    }
    private void loadContacts() {
        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        );

        List<String> contacts = new ArrayList<>();
        if (cursor != null) {
            int indexName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int indexNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            while (cursor.moveToNext()) {
                String name = cursor.getString(indexName);
                String phoneNumber = cursor.getString(indexNumber);
                contacts.add(name + " (" + formatNumber(phoneNumber) + ")");
            }
            cursor.close();

            RecyclerView recyclerView = findViewById(R.id.recyclerViewContacts);
            ContactsAdapter adapter = new ContactsAdapter(contacts, phoneNumber -> {
                editTextPhoneNumber.setText(phoneNumber);  // Ustawienie numeru telefonu po kliknięciu
            });
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            Toast.makeText(this, "No contacts found!", Toast.LENGTH_SHORT).show();
        }
    }


    private void sendSmsMessage() {
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        String messageText = editTextMessageText.getText().toString().trim();

        if (phoneNumber.isEmpty() || messageText.isEmpty()) {
            Toast.makeText(this, "Numer telefonu i wiadomość nie mogą być puste.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            try {
                smsManager.sendTextMessage(phoneNumber, null, messageText, null, null);
                Toast.makeText(this, "Wiadomość wysłana", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, ConversationActivity.class);
                intent.putExtra("PHONE_NUMBER", phoneNumber);
                startActivity(intent);

            } catch (Exception e) {
                Toast.makeText(this, "Błąd podczas wysyłania wiadomości: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
