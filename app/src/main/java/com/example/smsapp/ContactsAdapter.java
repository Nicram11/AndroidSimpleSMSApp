package com.example.smsapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private List<String> contacts;
    private Consumer<String> onContactClicked;

    public ContactsAdapter(List<String> contacts, Consumer<String> onContactClicked) {
        this.contacts = contacts;
        this.onContactClicked = onContactClicked;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String contact = contacts.get(position);
        holder.textView.setText(contact);
        holder.itemView.setOnClickListener(v -> {
            String phoneNumber = contact.substring(contact.indexOf("(") + 1, contact.length() - 1);
            onContactClicked.accept(phoneNumber);
        });

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
