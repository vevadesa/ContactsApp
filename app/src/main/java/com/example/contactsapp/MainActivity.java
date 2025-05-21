package com.example.contactsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ContactAdapter.OnContactDeleteListener {

    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private TextView contactsTitle;
    private FloatingActionButton fabAdd;

    private ArrayList<Contact> contacts;
    private ContactAdapter adapter;

    private ActivityResultLauncher<Intent> addContactLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactsTitle = findViewById(R.id.contactsTitle);
        recyclerView = findViewById(R.id.recyclerView);
        emptyTextView = findViewById(R.id.emptyTextView);
        fabAdd = findViewById(R.id.fabAdd);

        contacts = new ArrayList<>();
        adapter = new ContactAdapter(contacts, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        updateEmptyText();

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
            addContactLauncher.launch(intent);
        });

        addContactLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Contact newContact = (Contact) result.getData().getSerializableExtra("contact");
                        if (newContact != null) {
                            contacts.add(newContact);
                            adapter.notifyItemInserted(contacts.size() - 1);
                            recyclerView.scrollToPosition(contacts.size() - 1);
                            updateEmptyText();
                        }
                    }
                });
    }

    private void updateEmptyText() {
        if (contacts.isEmpty()) {
            emptyTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            contactsTitle.setVisibility(View.GONE);
        } else {
            emptyTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            contactsTitle.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onContactDelete(int position) {
        contacts.remove(position);
        adapter.notifyItemRemoved(position);
        updateEmptyText();
        Toast.makeText(this, "Контакт видалено", Toast.LENGTH_SHORT).show();
    }
}
