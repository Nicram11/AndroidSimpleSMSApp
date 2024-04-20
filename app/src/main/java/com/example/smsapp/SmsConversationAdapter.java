package com.example.smsapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SmsConversationAdapter extends RecyclerView.Adapter<SmsConversationAdapter.ViewHolder> {

    private List<SmsMessage> messages;

    public SmsConversationAdapter(List<SmsMessage> messages) {
        this.messages = messages;
    }

    public void setMessages(List<SmsMessage> messages) {
        this.messages = messages;
    }

    @Override
    public SmsConversationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sms_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SmsMessage message = messages.get(position);

        if (message.isSent()) {
            holder.layoutSent.setVisibility(View.VISIBLE);
            holder.layoutReceived.setVisibility(View.GONE);
            holder.textBodySent.setText(message.getMessage());
            holder.textDateSent.setText(DataUtils.formatDate(message.getDate()));
        } else {
            holder.layoutReceived.setVisibility(View.VISIBLE);
            holder.layoutSent.setVisibility(View.GONE);
            holder.textBodyReceived.setText(message.getMessage());
            holder.textDateReceived.setText(DataUtils.formatDate(message.getDate()));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View layoutSent;
        public View layoutReceived;
        public TextView textBodySent;
        public TextView textBodyReceived;
        public TextView textDateSent;
        public TextView textDateReceived;

        public ViewHolder(View itemView) {
            super(itemView);
            layoutSent = itemView.findViewById(R.id.layout_sent);
            layoutReceived = itemView.findViewById(R.id.layout_received);
            textBodySent = itemView.findViewById(R.id.text_message_body_sent);
            textBodyReceived = itemView.findViewById(R.id.text_message_body_received);
            textDateSent = itemView.findViewById(R.id.text_message_date_sent);
            textDateReceived = itemView.findViewById(R.id.text_message_date_received);
        }
    }

}
