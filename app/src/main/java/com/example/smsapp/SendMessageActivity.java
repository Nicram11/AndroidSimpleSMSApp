package com.example.smsapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

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

        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSmsMessage();
            }
        });
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
