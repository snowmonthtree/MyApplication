package com.example.myapplication.page.Video;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Controller.CommentsController;
import com.example.myapplication.R;
import com.example.myapplication.RetrofitClient;
import com.example.myapplication.data.Comment.Comment;
import com.example.myapplication.page.Bluetooth.BluetoothActivity;
import com.example.myapplication.page.Login.LoginActivity;
import com.example.myapplication.page.Park.ParkActivity;
import com.example.myapplication.ui.CommentAdapter;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PlayVideoActivity extends AppCompatActivity {
    private VideoView videoView;
    private MediaController mediaController;
    private Retrofit retrofit;
    private CommentsController commentsController;
    private List<Comment> comments;

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
        try {
            retrofit = RetrofitClient.getClient(PlayVideoActivity.this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        commentsController=retrofit.create(CommentsController.class);
        comments=new ArrayList<>();

        // 初始化 VideoView
        initVideoView();

        // 初始化 RecyclerView
        initRecyclerView();
    }

    private void initVideoView() {
        // 找到 VideoView 实例
        videoView = findViewById(R.id.videoView);
        ImageView imageView=findViewById(R.id.preImageView);

        /*设置视频路径
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.your_video_file;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);*/

        //显示图片
        Intent intent = getIntent();
        byte[] byteArray = intent.getByteArrayExtra("image");
        if (byteArray != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.INVISIBLE);
        }
        toDisplay(imageView);

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
        Intent intent=getIntent();
        String resourceId=intent.getStringExtra("ledId");
        // 创建一些静态评论数据
        Call<List<Comment>> call=commentsController.getCommentsByResourceId(resourceId);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                        comments.addAll(response.body());
                    // 设置适配器
                    CommentAdapter adapter = new CommentAdapter(comments);
                    recyclerViewComments.setAdapter(adapter);

                    // 设置布局管理器
                    recyclerViewComments.setLayoutManager(new LinearLayoutManager(PlayVideoActivity.this));

                } else {
                    Log.e("1", "onResponse: " );
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                // 网络请求失败
                Log.e("2", "onFailure: ");
            }
        });
        comments.add(new Comment("1","1","John Doe", "This is a sample comment.", "2"));
        comments.add(new Comment("1","1","Jane Smith", "Another sample comment.", "1"));

    }
    private void toDisplay(ImageView imageView){
        imageView.setOnClickListener(v -> {
            // 获取 ImageButton 上的 Bitmap
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

                // 将 Bitmap 转换为字节数组
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                Intent intent = new Intent(PlayVideoActivity.this, BluetoothActivity.class);
                intent.putExtra("image", byteArray);
                startActivity(intent);
            }
        });
    }

}