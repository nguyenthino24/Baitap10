package com.example.firebase;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UploadActivity extends AppCompatActivity {
    private static final int PICK_VIDEO_REQUEST = 1;
    private Uri videoUri;
    private Button chooseVideoButton, uploadVideoButton;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        chooseVideoButton = findViewById(R.id.chooseVideoButton);
        uploadVideoButton = findViewById(R.id.uploadVideoButton);

        chooseVideoButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("video/*");
            startActivityForResult(intent, PICK_VIDEO_REQUEST);
        });

        uploadVideoButton.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                Toast.makeText(this, "Vui lòng đăng nhập trước", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }
            if (videoUri != null) {
                uploadVideoToFirebase(videoUri);
            } else {
                Toast.makeText(this, "Vui lòng chọn video trước", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            videoUri = data.getData();
            Toast.makeText(this, "Đã chọn video", Toast.LENGTH_SHORT).show();

            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoURI(videoUri);
            videoView.setOnPreparedListener(mp -> videoView.start());
        }
    }
    private void uploadVideoToFirebase(Uri videoUri) {
        String userId = mAuth.getCurrentUser().getUid();
        StorageReference storageRef = storage.getReference();
        StorageReference videoRef = storageRef.child("videos/" + userId + "/" + System.currentTimeMillis() + ".mp4");

        videoRef.putFile(videoUri)
                .addOnSuccessListener(taskSnapshot -> {
                    videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Toast.makeText(this, "Upload thành công: " + uri.toString(), Toast.LENGTH_LONG).show();
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Upload thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}