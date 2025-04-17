package com.example.firebase;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.util.HashMap;
import java.util.Map;

public class UploadVideoActivity extends AppCompatActivity {

    private Button btnSelectVideo, btnUploadVideo;
    private ProgressBar uploadProgressBar;
    private Uri videoUri;
    private boolean isCloudinaryInitialized = false;

    private final ActivityResultLauncher<Intent> videoPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    videoUri = result.getData().getData();
                    btnUploadVideo.setEnabled(true);
                    Toast.makeText(this, "Video selected", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);

        btnSelectVideo = findViewById(R.id.btnSelectVideo);
        btnUploadVideo = findViewById(R.id.btnUploadVideo);
        uploadProgressBar = findViewById(R.id.uploadProgressBar);

        if (uploadProgressBar == null) {
            Log.e("UploadVideoActivity", "ProgressBar with ID uploadProgressBar not found in layout");
        }

        btnUploadVideo.setEnabled(false); // Disable upload button until a video is selected

        // Initialize Cloudinary
        try {
            Map config = new HashMap();
            config.put("cloud_name", "dckj6mejp"); // Replace with your Cloudinary cloud name
            config.put("api_key", "361316581949833");       // Replace with your Cloudinary API key
            config.put("api_secret", "m9gtTer4iJX1ZLo1Rqj57sWE4lY"); // Replace with your Cloudinary API secret
            MediaManager.init(this, config);
            isCloudinaryInitialized = true;
        } catch (Exception e) {
            Toast.makeText(this, "Cloudinary initialization failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // Select video button
        btnSelectVideo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("video/*");
            videoPickerLauncher.launch(intent);
        });

        // Upload video button
        btnUploadVideo.setOnClickListener(v -> {
            if (videoUri != null && isCloudinaryInitialized) {
                if (uploadProgressBar != null) {
                    uploadProgressBar.setVisibility(View.VISIBLE);
                } else {
                    Log.e("UploadVideoActivity", "Cannot show ProgressBar, it is null");
                }
                uploadToCloudinary(videoUri);
            } else {
                Toast.makeText(this, "Please select a video or check Cloudinary configuration", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadToCloudinary(Uri videoUri) {
        MediaManager.get().upload(videoUri)
                .option("resource_type", "video")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Toast.makeText(UploadVideoActivity.this, "Upload started", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        // Update progress if needed
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        if (uploadProgressBar != null) {
                            uploadProgressBar.setVisibility(View.GONE);
                        }
                        String videoUrl = (String) resultData.get("url");
                        Toast.makeText(UploadVideoActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();

                        // Pass the new video URL back to VideoShortFireBaseActivity
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("video_url", videoUrl);
                        resultIntent.putExtra("video_title", "Uploaded Video");
                        resultIntent.putExtra("video_desc", "User uploaded video");
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        if (uploadProgressBar != null) {
                            uploadProgressBar.setVisibility(View.GONE);
                        }
                        Toast.makeText(UploadVideoActivity.this, "Upload failed: " + error.getDescription(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        // Handle reschedule if needed
                    }
                })
                .dispatch();
    }
}