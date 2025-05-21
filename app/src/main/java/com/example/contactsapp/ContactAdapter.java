package com.example.contactsapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private final ArrayList<Contact> contacts;
    private final OnContactDeleteListener deleteListener;

    public interface OnContactDeleteListener {
        void onContactDelete(int position);
    }

    public ContactAdapter(ArrayList<Contact> contacts, OnContactDeleteListener listener) {
        this.contacts = contacts;
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view, deleteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);

        holder.nameText.setText(contact.getName());
        holder.emailText.setText(contact.getEmail());
        holder.phoneText.setText(contact.getPhone());

        String imagePath = contact.getImagePath();
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            if (bitmap != null) {
                holder.photoView.setImageBitmap(bitmap);
            } else {
                holder.photoView.setImageResource(android.R.drawable.sym_def_app_icon);
            }
        } else {
            holder.photoView.setImageResource(android.R.drawable.sym_def_app_icon);
        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, emailText, phoneText;
        ImageView photoView;
        Button deleteButton;

        public ContactViewHolder(@NonNull View itemView, OnContactDeleteListener listener) {
            super(itemView);

            nameText = itemView.findViewById(R.id.contactName);
            emailText = itemView.findViewById(R.id.contactEmail);
            phoneText = itemView.findViewById(R.id.contactPhone);
            photoView = itemView.findViewById(R.id.contactPhoto);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onContactDelete(getAdapterPosition());
                }
            });
        }
    }
}
