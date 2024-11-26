package com.example.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import android.widget.MediaController;
import android.widget.VideoView;

import com.example.myapplication.data.Comment.Comment;
import com.example.myapplication.ui.CommentAdapter;

public class PlayVideoActivity extends AppCompatActivity {
    private VideoView videoView;
    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        // 初始化 VideoView
        initVideoView();

        // 初始化 RecyclerView
        initRecyclerView();
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