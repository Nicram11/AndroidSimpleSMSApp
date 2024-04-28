package com.example.smsapp.smsHandling;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.smsapp.SmsMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SmsReader {

    public static List<SmsMessage> readSmsMessages(Context context) {
        List<SmsMessage> smsList = new ArrayList<>();
        Uri uriSms = Uri.parse("content://sms/");
        Cursor cursor = context.getContentResolver().query(uriSms, new String[]{"_id", "address", "date", "body", "type"},
                "address IS NOT NULL", null, "date DESC");

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
        return smsList;
    }
}
