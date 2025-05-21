package com.example.contactsapp;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;

public class AddContactActivity extends AppCompatActivity {

    private EditText nameEdit, emailEdit, phoneEdit;
    private ImageView photoView;
    private Button takePhotoBtn, selectPhotoBtn, addBtn;

    private Bitmap contactPhoto;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        nameEdit = findViewById(R.id.editName);
        emailEdit = findViewById(R.id.editEmail);
        phoneEdit = findViewById(R.id.editPhone);
        photoView = findViewById(R.id.photoView);
        takePhotoBtn = findViewById(R.id.btnTakePhoto);
        selectPhotoBtn = findViewById(R.id.btnSelectPhoto);
        addBtn = findViewById(R.id.btnAddContact);

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        contactPhoto = (Bitmap) extras.get("data");
                        photoView.setImageBitmap(contactPhoto);
                    }
                });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            try {
                                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                                contactPhoto = BitmapFactory.decodeStream(inputStream);
                                photoView.setImageBitmap(contactPhoto);
                                if (inputStream != null) inputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Помилка при виборі зображення", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        takePhotoBtn.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                cameraLauncher.launch(cameraIntent);
            } else {
                Toast.makeText(this, "Камера не доступна", Toast.LENGTH_SHORT).show();
            }
        });

        selectPhotoBtn.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryIntent.setType("image/*");
            galleryLauncher.launch(galleryIntent);
        });

        addBtn.setOnClickListener(v -> {
            String name = nameEdit.getText().toString().trim();
            String email = emailEdit.getText().toString().trim();
            String phone = phoneEdit.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Заповніть всі поля", Toast.LENGTH_SHORT).show();
                return;
            }

            if (contactPhoto == null) {
                Toast.makeText(this, "Зробіть фото або виберіть зображення", Toast.LENGTH_SHORT).show();
                return;
            }

            String imagePath = saveImageToInternalStorage(contactPhoto);
            if (imagePath == null) {
                Toast.makeText(this, "Не вдалося зберегти фото", Toast.LENGTH_SHORT).show();
                return;
            }

            Contact newContact = new Contact(name, email, phone, imagePath);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("contact", newContact);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private String saveImageToInternalStorage(Bitmap bitmap) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        String fileName = "contact_" + System.currentTimeMillis() + ".jpg";
        File mypath = new File(directory, fileName);

        try (FileOutputStream fos = new FileOutputStream(mypath)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return mypath.getAbsolutePath();
    }
}
