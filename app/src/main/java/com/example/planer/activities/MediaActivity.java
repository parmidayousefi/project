
package com.example.myapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MediaActivity extends AppCompatActivity {

    private static final int PICK_MEDIA = 1001;
    private FirebaseStorage storage;
    private StorageReference mediaRef;
    private Uri selectedUri;
    private ImageView previewImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        storage = FirebaseStorage.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mediaRef = storage.getReference().child("users").child(user.getUid()).child("media");

        Button btnSelect = findViewById(R.id.btn_select_media);
        Button btnUpload = findViewById(R.id.btn_upload_media);
        previewImage = findViewById(R.id.img_preview);

        btnSelect.setOnClickListener(v -> openFileChooser());
        btnUpload.setOnClickListener(v -> {
            if (selectedUri != null) {
                uploadFile();
            } else {
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/* video/*");
        startActivityForResult(intent, PICK_MEDIA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_MEDIA && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedUri = data.getData();
            previewImage.setImageURI(selectedUri);
        }
    }

    private void uploadFile() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading...");
        pd.show();

        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        StorageReference fileRef = mediaRef.child(fileName);

        fileRef.putFile(selectedUri)
                .addOnSuccessListener(taskSnapshot -> {
                    pd.dismiss();
                    Toast.makeText(this, "Upload successful", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show();
                });
    }
}
