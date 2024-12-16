package com.example.myapplication.page.Video;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Controller.CommentsController;
import com.example.myapplication.Controller.LedResourceController;
import com.example.myapplication.Controller.PlayRecordController;
import com.example.myapplication.R;
import com.example.myapplication.RetrofitClient;
import com.example.myapplication.data.Comment.Comment;
import com.example.myapplication.data.LedResource.LedResource;
import com.example.myapplication.data.ViewSharer;
import com.example.myapplication.page.Bluetooth.BluetoothActivity;
import com.example.myapplication.page.Login.LoginActivity;
import com.example.myapplication.ui.CommentAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private LedResourceController ledResourceController;
    private PlayRecordController playRecordController;
    private List<Comment> comments;
    private EditText editText;
    private Button button;
    private ViewSharer viewSharer;
    private String resourceId;
    private View detailSection;
    private View commentSection;

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
        editText=findViewById(R.id.editTextComment);
        button=findViewById(R.id.buttonSendComment);
        viewSharer=(ViewSharer) getApplication();
        try {
            retrofit = RetrofitClient.getClient(PlayVideoActivity.this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        commentsController=retrofit.create(CommentsController.class);
        ledResourceController=retrofit.create(LedResourceController.class);
        playRecordController=retrofit.create(PlayRecordController.class);
        detailSection=findViewById(R.id.detailSection);
        commentSection=findViewById(R.id.commentSection);
        RadioButton radioButtondetail = findViewById(R.id.radio_detail);
        radioButtondetail.setChecked(true);

        // 监听 RadioGroup 的选择变化
        RadioGroup radioGroup = findViewById(R.id.radio_groups);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_detail) {
                detailSection.setVisibility(View.VISIBLE);
                commentSection.setVisibility(View.GONE);
                detailSection.requestLayout();  // 强制重新布局
                commentSection.requestLayout();
            } else if (checkedId == R.id.radio_comment) {
                commentSection.setVisibility(View.VISIBLE);
                detailSection.setVisibility(View.GONE);
                detailSection.requestLayout();  // 强制重新布局
                commentSection.requestLayout();
            }
        });

        comments=new ArrayList<>();

        // 初始化 VideoView
        initVideoView();

        // 初始化 RecyclerView
        initRecyclerView();

        button.setOnClickListener(view -> sendComment());

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
        resourceId=intent.getStringExtra("ledId");
        if (!viewSharer.getUser().getPermissionId().equals("1")) {
            Call<Void> call1 = playRecordController.addPlayRecord(viewSharer.getUser().getUserId(), resourceId);
            call1.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.e("test21321", "onResponse: 1");
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("test21321", "onFailure: ");
                }
            });
        }
        Call<LedResource> call=ledResourceController.getResourceById(resourceId);
        call.enqueue(new Callback<LedResource>() {
            @Override
            public void onResponse(Call<LedResource> call, Response<LedResource> response) {
                if (response.body()!=null&&response.isSuccessful()){
                    fetchImage(response.body().getViewWebUrl(),imageView);
                    CheckBox like=findViewById(R.id.like);
                    TextView textView=findViewById(R.id.downloadcount);
                    TextView textView1=findViewById(R.id.watchedNum);
                    TextView detail=findViewById(R.id.detail);
                    detail.setText(response.body().getDetail());
                    textView1.setText("播放量:"+response.body().getPlaybackVolume());
                    textView.setText("下载量:"+response.body().getDownloadCount());
                    RadioButton radioButton=findViewById(R.id.radio_comment);
                    radioButton.setText("评论:"+response.body().getCommentNum());
                }
            }

            @Override
            public void onFailure(Call<LedResource> call, Throwable t) {

            }
        });
        imageView.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.INVISIBLE);
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
        // 创建一些静态评论数据
        Call<List<Comment>> call=commentsController.getCommentsByResourceId(resourceId);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                        comments.addAll(response.body());
                    comments.add(new Comment("1","1","Jane Smith", "没有更多了", "012345678901234567890123456789"));
                    // 设置适配器
                    CommentAdapter adapter = new CommentAdapter(comments);
                    recyclerViewComments.setAdapter(adapter);

                    // 设置布局管理器
                    recyclerViewComments.setLayoutManager(new LinearLayoutManager(PlayVideoActivity.this));

                } else {
                    comments.add(new Comment("1","1","Jane Smith", "暂无评论", "012345678901234567890123456789"));
                    Log.e("1", "onResponse: " );
                    // 设置适配器
                    CommentAdapter adapter = new CommentAdapter(comments);
                    recyclerViewComments.setAdapter(adapter);

                    // 设置布局管理器
                    recyclerViewComments.setLayoutManager(new LinearLayoutManager(PlayVideoActivity.this));

                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {

                comments.add(new Comment("1","1","Jane Smith", "出错咯", "012345678901234567890123456789"));
                // 网络请求失败
                Log.e("2", "onFailure: ");
                // 设置适配器
                CommentAdapter adapter = new CommentAdapter(comments);
                recyclerViewComments.setAdapter(adapter);

                // 设置布局管理器
                recyclerViewComments.setLayoutManager(new LinearLayoutManager(PlayVideoActivity.this));

            }
        });

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
    private void sendComment() {
        if (!viewSharer.getUser().getPermissionId().equals("0")){
            new AlertDialog.Builder(PlayVideoActivity.this)
                    .setTitle("错误")
                    .setMessage("先登录")
                    .setPositiveButton("登录",(dialog,witch) ->{
                    Intent intent=new Intent(PlayVideoActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finishAffinity();
                    })
                    .setNegativeButton("稍后", (dialog,witch) ->{

                    })
                    .show();
        }
        else {
            Call<String> call = commentsController.addComment(resourceId, viewSharer.getUser().getUserId(), editText.getText().toString());
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    String temp=response.body();
                    Toast.makeText(PlayVideoActivity.this, temp, Toast.LENGTH_SHORT).show();
                    comments.clear();
                    initRecyclerView();
                }
                @Override
                public void onFailure(Call<String> call ,Throwable T){
                    Log.e("1", "onFailure: I Don't Know" );
                }
            });
        }
    }
    private void fetchImage(String viewWebUrl, ImageView imageView) {
        // 假设 ledResourceController 是 Retrofit 接口，调用 getImage 方法来获取图片
        ledResourceController.getImage(viewWebUrl).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseBody responseBody = response.body();
                    Bitmap bitmap = convertResponseBodyToBitmap(responseBody);

                    if (bitmap != null) {
                        // 使用 Glide 将 Bitmap 加载到 ImageView
                        Glide.with(imageView.getContext())
                                .load(bitmap)  // 直接加载 Bitmap
                                .placeholder(R.drawable.test)  // 占位图
                                .error(R.drawable.ic_doodle_back)  // 错误图
                                .into(imageView);  // 将图片显示到 ImageView
                    } else {
                        Log.e("Image Fetch", "Bitmap is null");
                    }
                } else {
                    Log.e("Image Fetch", "onResponse: Image fetch failed1");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // 网络请求失败
                Log.e("Image Fetch", "onFailure: Image fetch failed", t);
            }
        });
    }

    // 将ResponseBody转换为Bitmap
    private Bitmap convertResponseBodyToBitmap(ResponseBody responseBody) {
        Bitmap bitmap = null;
        InputStream inputStream = responseBody.byteStream();
        try {
            bitmap = BitmapFactory.decodeStream(inputStream);  // 将输入流解码为Bitmap
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();  // 关闭输入流
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
}