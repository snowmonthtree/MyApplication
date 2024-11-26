package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.MediaController;
import android.widget.VideoView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.data.Comment.Comment;
import com.example.myapplication.page.Park.ParkActivity;
import com.example.myapplication.ui.CommentAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class PlayVideoActivity extends AppCompatActivity {
    private VideoView videoView;
    private MediaController mediaController;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_play_video);

        // 设置系统栏内边距
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 初始化 VideoView
        initVideoView();

        // 初始化 RecyclerView
        initRecyclerView();

        // 获取 BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 设置默认选中项为个人中心
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        // 设置点击监听器
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // 跳转到首页
                startActivity(new Intent(PlayVideoActivity.this, HomeActivity.class));
                overridePendingTransition(0, 0); // 可选，用于移除过渡动画
                return true;
            } else if (itemId == R.id.nav_square) {
                // 跳转到广场页面
                startActivity(new Intent(PlayVideoActivity.this, ParkActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_creation_center) {
                // 跳转到创作中心
                startActivity(new Intent(PlayVideoActivity.this, CreationCenterActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_shopping) {
                // 跳转到购物页面
                startActivity(new Intent(PlayVideoActivity.this, ShoppingActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                // 当前已经是个人中心页面
                return true;
            }
            return false;
        });
    }

    private void initVideoView() {
        // 找到 VideoView 实例
        videoView = findViewById(R.id.videoView);

        /*设置视频路径
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.your_video_file;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);*/

        // 设置 MediaController（可选）
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // 开始播放视频
        videoView.start();

        // 设置视频播放完成后的回调（可选）
        videoView.setOnCompletionListener(mp -> {
            // 视频播放完成后执行的操作
        });

        // 设置视频准备完成后的回调（可选）
        videoView.setOnPreparedListener(mp -> {
            // 视频准备完成后执行的操作
            mp.setLooping(true);
        });
    }

    private void initRecyclerView() {
        RecyclerView recyclerViewComments = findViewById(R.id.recyclerViewComments);

        // 创建一些静态评论数据
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment("John Doe", "This is a sample comment.", "1 day ago"));
        comments.add(new Comment("Jane Smith", "Another sample comment.", "2 days ago"));

        // 设置适配器
        CommentAdapter adapter = new CommentAdapter(comments);
        recyclerViewComments.setAdapter(adapter);

        // 设置布局管理器
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));
    }
}