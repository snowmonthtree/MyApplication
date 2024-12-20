package com.example.myapplication.page.Video;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.example.myapplication.Controller.LikesController;
import com.example.myapplication.Controller.PlayRecordController;
import com.example.myapplication.LEDResource;
import com.example.myapplication.R;
import com.example.myapplication.RetrofitClient;
import com.example.myapplication.data.Comment.Comment;
import com.example.myapplication.data.LedResource.LedResource;
import com.example.myapplication.data.ViewSharer;
import com.example.myapplication.page.Bluetooth.BluetoothActivity;
import com.example.myapplication.page.Login.LoginActivity;
import com.example.myapplication.page.Park.ParkActivity;
import com.example.myapplication.ui.CommentAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private LedResource ledResource;
    private View detailSection;
    private View commentSection;
    private LikesController likesController;
    private CheckBox like;
    private String resourceId;
    private ImageView imageView;

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
        likesController=retrofit.create(LikesController.class);
        detailSection=findViewById(R.id.detailSection);
        commentSection=findViewById(R.id.commentSection);
        like=findViewById(R.id.like);
        RadioButton radioButtonDetail = findViewById(R.id.radio_detail);

        radioButtonDetail.setChecked(true);

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
        imageView=findViewById(R.id.preImageView);

        /*设置视频路径
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.your_video_file;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);*/

        //显示图片
        Intent intent = getIntent();
        resourceId=intent.getStringExtra("ledId");
        initResource(intent.getStringExtra("ledId"));
        if (!viewSharer.getUser().getPermissionId().equals("1")) {
            Call<Void> call1 = playRecordController.addPlayRecord(viewSharer.getUser().getUserId(), resourceId);
            call1.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {

                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("test21321", "onFailure: ");
                }
            });
        }
        like.setOnClickListener(view -> likeClick());
        Call<LedResource> call=ledResourceController.getResourceById(resourceId);
        call.enqueue(new Callback<LedResource>() {
            @Override
            public void onResponse(Call<LedResource> call, Response<LedResource> response) {
                if (response.body()!=null&&response.isSuccessful()){
                    fetchImage(response.body().getViewWebUrl(),imageView);
                    setLike();
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
                ledResource.setPlaybackVolume(ledResource.getPlaybackVolume()+1);
                Log.e("nmdwsm", "toDisplay: "+ledResource.getPlaybackVolume() );
                update();
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
            Call<String> call = commentsController.addComment(ledResource.getResourceId(), viewSharer.getUser().getUserId(), editText.getText().toString());
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
            ledResource.setCommentNum(ledResource.getCommentNum()+1);
            update();
        }
    }
    private void fetchImage(String viewWebUrl, ImageView imageView) {
        // 假设 ledResourceController 是 Retrofit 接口，调用 getImage 方法来获取图片
        ledResourceController.getImage(viewWebUrl).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 从ResponseBody获取图片数据并设置到ImageButton
                    ResponseBody responseBody = response.body();
                    // 检查响应头中的 MIME 类型来判断图片类型
                    String contentType = response.headers().get("Content-Type");

                    if (contentType != null && contentType.contains("image/gif")) {
                        // 如果是 GIF 动态图片
                        // 使用 Glide 加载 GIF 图片
                        Glide.with(PlayVideoActivity.this)  // 使用 Activity 或 Application 上下文
                                .asGif()
                                .load(responseBody.byteStream())  // 获取字节流
                                .into(imageView);  // 加载到对应的 ImageButton

                    } else {
                        // 如果是静态图片（PNG, JPEG 等）
                        Bitmap bitmap = convertResponseBodyToBitmap(responseBody);

                        if (bitmap != null) {
                            // 缩放图片到指定尺寸
                            bitmap = Bitmap.createScaledBitmap(bitmap, 900, 300, false);

                            // 设置 Bitmap 到 ImageButton
                            imageView.setImageBitmap(bitmap);
                        }
                        else {
                            Log.e("Image Fetch", "Bitmap is null");

                    }
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
    private void setLike(){
        Call<Integer> call1=likesController.getLikesNum(resourceId);
        call1.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
            like.setText("点赞:"+response.body());
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });

        Call<Boolean> call=likesController.userIfLike(viewSharer.getUser().getUserId(),resourceId);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    like.setChecked(response.body());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

            }
        });
    }
    private void likeClick(){
        Call<String> call=likesController.likeResource(viewSharer.getUser().getUserId(),ledResource.getResourceId());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.body().equals("点赞成功")) {
                        ledResource.setLikes(ledResource.getLikes()+1);
                    }
                    else {
                        ledResource.setLikes(ledResource.getLikes()-1);
                    }
                    Toast.makeText(PlayVideoActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                    update();
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("1232", "likeClick: "+"4444444444444" );
            }
        });
    }
    private void initResource(String id){
        Call<LedResource> call=ledResourceController.getResourceById(id);
        call.enqueue(new Callback<LedResource>() {
            @Override
            public void onResponse(Call<LedResource> call, Response<LedResource> response) {
                if (response.isSuccessful()&&response.body()!=null) {
                    ledResource = response.body();
                    TextView textView=findViewById(R.id.downloadcount);
                    TextView textView1=findViewById(R.id.watchedNum);
                    TextView detail=findViewById(R.id.detail);
                    detail.setText(response.body().getDetail());
                    textView1.setText("播放量:"+response.body().getPlaybackVolume());
                    textView.setText("下载量:"+response.body().getDownloadCount());
                    RadioButton radioButton=findViewById(R.id.radio_comment);
                    radioButton.setText("评论:"+response.body().getCommentNum());
                    textView.setOnClickListener(view -> save());

                }
            }

            @Override
            public void onFailure(Call<LedResource> call, Throwable t) {

            }
        });
    }
    private void update(){
        System.out.println(ledResource.toString());
        Call<String> call=ledResourceController.updateResource(resourceId,viewSharer.getUser().getUserId(),ledResource.getPlaybackVolume(),ledResource.getDownloadCount(),ledResource.getCommentNum());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.body()!=null){
                    Toast.makeText(PlayVideoActivity.this, response.body(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(PlayVideoActivity.this, "ERROR"+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void save(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission_group.STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限，请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission_group.STORAGE},
                    100);
        }
        try {
            File directory=new File(getFilesDir(),"download");
            // 获取存储路径，这里使用外部存储目录
            if (!directory.exists()) {
                directory.mkdirs(); // 创建目录
            }

            // 创建文件
            File file = new File(directory, ledResource.getViewWebUrl());
            FileOutputStream outStream = new FileOutputStream(file);

            // 将 Bitmap 保存为 PNG 格式
            Drawable drawable=imageView.getDrawable();
            BitmapDrawable bitmapDrawable=(BitmapDrawable) drawable;
            bitmapDrawable.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, outStream);

            // 关闭文件输出流
            outStream.flush();
            outStream.close();
            ledResource.setDownloadCount(ledResource.getDownloadCount()+1);
            update();
            /*MediaScannerConnection.scanFile(this,
                    new String[]{Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()},
                    null,
                    (path, uri) -> Log.d("MediaScanner", "File scanned: " + path));
*/
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Toast.makeText(this, "LED Resource Saved:\n" +"下载成功", Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 判断请求码是否匹配
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予，执行文件操作
                save();
            } else {
                // 权限被拒绝，提示用户
                Toast.makeText(this, "Permission denied, cannot access the file", Toast.LENGTH_SHORT).show();
            }
        }
    }
}