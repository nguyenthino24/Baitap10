package com.example.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.VideoView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class VideoShortActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private VideosAdapter videosAdapter;
    private List<Video1Model> videoList;

    // Launcher để nhận kết quả từ UploadVideoActivity
    private final ActivityResultLauncher<Intent> uploadVideoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String videoUrl = result.getData().getStringExtra("video_url");
                    String title = result.getData().getStringExtra("video_title");
                    String desc = result.getData().getStringExtra("video_desc");
                    videoList.add(new Video1Model(title, desc, videoUrl));
                    videosAdapter.notifyDataSetChanged();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_short);

        viewPager2 = findViewById(R.id.vpager);
        Button btnUpload = findViewById(R.id.btnUpload);

        // Xử lý sự kiện nhấn nút Upload
        btnUpload.setOnClickListener(v -> {
            Intent intent = new Intent(VideoShortActivity.this, UploadVideoActivity.class);
            uploadVideoLauncher.launch(intent);
        });

        setupVideos();

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                for (int i = 0; i < viewPager2.getChildCount(); i++) {
                    View child = viewPager2.getChildAt(i);
                    VideoView videoView = child.findViewById(R.id.videoView);
                    if (videoView != null && i != position) {
                        videoView.stopPlayback(); // Dừng video cũ
                    }
                }
            }
        });
    }

    private void setupVideos() {
        videoList = new ArrayList<>();
        // Thêm các video khác nếu cần
        videoList.add(new Video1Model("La Ngâu", "Chuyến đi 2N1Đ", "https://res.cloudinary.com/dckj6mejp/video/upload/v1744422221/sample_u0ecvw.mp4"));
        videoList.add(new Video1Model("Bình Thuận", "La Ngâu - Tánh Linh", "https://res.cloudinary.com/dckj6mejp/video/upload/v1744897583/dnrctnviyckytzqiocby.mp4"));

        videosAdapter = new VideosAdapter(videoList);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        viewPager2.setAdapter(videosAdapter);
    }
}