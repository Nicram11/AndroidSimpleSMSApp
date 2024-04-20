package com.example.smsapp;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SmsAdapter extends BaseAdapter {

    private Context context;
    private List<SmsMessage> smsList;

    public SmsAdapter(Context context, List<SmsMessage> smsList) {
        this.context = context;
        this.smsList = smsList;
    }

    @Override
    public int getCount() {
        return smsList.size();
    }

    @Override
    public Object getItem(int position) {
        return smsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_sms, parent, false);
        }

        TextView senderView = convertView.findViewById(R.id.sender);
        TextView messageView = convertView.findViewById(R.id.message);
        TextView typeView = convertView.findViewById(R.id.message_type);
        TextView dateView = convertView.findViewById(R.id.message_date);

        SmsMessage sms = smsList.get(position);
        String contactName = getContactName(sms.getSender(), context.getContentResolver());
        if (contactName != null) {
            senderView.setText(contactName + " (" + sms.getSender() + ")");
        } else {
            senderView.setText(sms.getSender());
        }
        messageView.setText(sms.getMessage());
        dateView.setText(DataUtils.formatDate(sms.getDate()));

        if (sms.getType() == 1) {
            typeView.setText("Otrzymana");
            typeView.setTextColor(ContextCompat.getColor(context, R.color.received_message));
        } else if (sms.getType() == 2) {
            typeView.setText("Wysłana");
            typeView.setTextColor(ContextCompat.getColor(context, R.color.sent_message));
        }

        return convertView;
    }

    private String getContactName(String phoneNumber, ContentResolver contentResolver) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        Cursor cursor = contentResolver.query(uri, projection, null, null, null);

        if (cursor != null) {
            try {
                int index = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                if (cursor.moveToFirst() && index != -1) {
                    return cursor.getString(index);
                }
            } finally {
                cursor.close();
            }
        }
        return null;
    }

}
