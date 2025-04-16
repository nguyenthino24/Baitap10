package com.example.firebase;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class VideoShortActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private VideosAdapter videosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_short);

        viewPager2 = findViewById(R.id.vpager);
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
        // Danh sách video từ Cloudinary (ví dụ tĩnh)
        List<Video1Model> videoList = new ArrayList<>();
        videoList.add(new Video1Model("Video 1", "Description 1", "https://res.cloudinary.com/dckj6mejp/video/upload/v1744422221/sample_u0ecvw.mp4"));
//        videoList.add(new Video1Model("Video 2", "Description 2", "https://res.cloudinary.com/your_cloud_name/video/upload/v123456789/sample_video2.mp4"));
        // Thêm các video khác nếu cần

        // Thiết lập adapter
        videosAdapter = new VideosAdapter(videoList);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        viewPager2.setAdapter(videosAdapter);
    }

}