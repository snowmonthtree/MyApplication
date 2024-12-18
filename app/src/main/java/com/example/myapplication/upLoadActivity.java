package com.example.myapplication;

import static com.example.myapplication.EditInfoActivity.getFileFromUri;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.Controller.LedResourceController;
import com.example.myapplication.data.LedResource.LedResource;
import com.example.myapplication.data.ViewSharer;
import com.google.gson.Gson;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class upLoadActivity extends AppCompatActivity {
    private ImageView imageView;
    private EditText detail;
    private EditText name;
    private Button upload;
    private Button clear;
    private LedResource ledResource;
    private ViewSharer viewSharer;
    private LedResourceController ledResourceController;
    private Retrofit retrofit;
    private Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_up_load);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        try {
            retrofit=RetrofitClient.getClient(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ledResourceController=retrofit.create(LedResourceController.class);
        detail=findViewById(R.id.editDetail);
        name=findViewById(R.id.editName);
        imageView=findViewById(R.id.localImageView);
        upload=findViewById(R.id.upload);
        clear=findViewById(R.id.clear);
        viewSharer=(ViewSharer)getApplication();
        Intent intent=getIntent();
        uri=intent.getParcelableExtra("uri");
        imageView.setImageURI(uri);
        ledResource=new LedResource();
        ledResource.setUserId(viewSharer.getUser().getUserId());
        ledResource.setPixelSize("8*32");
        ledResource.setDownloadCount(0);
        ledResource.setDisplayType("image");
        ledResource.setLikes(0);
        ledResource.setCommentNum(0);
        ledResource.setPlaybackVolume(0);
        upload.setOnClickListener(view -> upLoad());
        clear.setOnClickListener(view -> clear());
    }
    private void upLoad(){
        ledResource.setName(name.getText().toString());
        ledResource.setDetail(detail.getText().toString());
        Log.e("1231231231", "upLoad: "+uri );
        try {
        File file=new File(uri.getPath());

        if (file.exists()) {
            // 创建 RequestBody，用于文件上传
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestBody);  // 设置表单的键值

            // 创建 Retrofit 实例并进行文件上传

            Gson gson = new Gson();
            String ledJson = gson.toJson(ledResource);
            RequestBody ledRequestBody = RequestBody.create(ledJson, MediaType.parse("application/json"));
            Call<String> call = ledResourceController.uploadLedResource(ledRequestBody,viewSharer.getUser().getUserId(),body);

            call.enqueue(new retrofit2.Callback<String>() {
                @Override
                public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                    if (response.isSuccessful()) {
                        // 成功处理
                        Toast.makeText(upLoadActivity.this, "信息已保存", Toast.LENGTH_SHORT).show();
                    } else {
                        // 错误处理
                        System.out.println("上传失败: " + response.code()+response.errorBody().toString());
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    // 网络错误处理
                    t.printStackTrace();
                }
            });
        } else {
            System.out.println("文件不存在！ no file?");
        }
    } catch (Exception e) {
        throw new RuntimeException(e);
    }

    }
    private void clear(){
        new AlertDialog.Builder(upLoadActivity.this)
                .setTitle("警告")
                .setMessage("该操作不可逆")
                .setPositiveButton("确定",(dialog,which)->{
                    File file = new File(uri.getPath());

                    // 检查文件是否存在
                    if (file.exists()) {
                        // 删除文件
                        boolean isDeleted = file.delete();
                        if (isDeleted) {
                            // 文件删除成功
                            Log.d("FileDelete", "File deleted successfully");
                            finish();
                        } else {
                            // 文件删除失败
                            Log.d("FileDelete", "File deletion failed");
                            finish();
                        }
                    } else {
                        // 文件不存在
                        Log.d("FileDelete", "File does not exist");
                        finish();
                    }
                })
                .setNegativeButton("取消",(dialog,which)->{
                    return;
                })
                .show();
    }
}