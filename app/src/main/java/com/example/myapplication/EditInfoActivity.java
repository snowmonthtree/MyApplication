package com.example.myapplication;

import android.Manifest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.Controller.UserController;
import com.example.myapplication.data.User.User;
import com.example.myapplication.data.ViewSharer;
import com.example.myapplication.page.ChangePassword.ChangePasswordActivity;
import com.example.myapplication.page.Register.RegisterActivity;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EditInfoActivity extends AppCompatActivity {

    private EditText etNickname;
    private Button btnSelectAvatar;
    private Button btnChangePassword;
    private Button btnSaveChanges;
    private ImageView imageViewAvatar;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<Intent> changePasswordLauncher;
    private ViewSharer viewSharer;
    private User user;
    private Retrofit retrofit;
    private UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        // 初始化视图组件
        etNickname = findViewById(R.id.et_nickname);
        btnSelectAvatar = findViewById(R.id.btn_select_avatar);
        btnChangePassword = findViewById(R.id.btn_change_password);
        btnSaveChanges = findViewById(R.id.btn_save_changes);
        imageViewAvatar = findViewById(R.id.imageView6); // 假设头像显示在顶部的 ImageView 中
        try {
            retrofit = RetrofitClient.getClient(EditInfoActivity.this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        userController = retrofit.create(UserController.class);
        viewSharer=(ViewSharer)getApplication();
        user=viewSharer.getUser();
        // 注册图像选择器的结果回调
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();

                    }
                }
        );

        // 注册密码修改的结果回调
        changePasswordLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Toast.makeText(EditInfoActivity.this, "密码已成功修改", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // 设置点击事件
        btnSelectAvatar.setOnClickListener(v -> openImagePicker());

        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(EditInfoActivity.this, ChangePasswordActivity.class);
            changePasswordLauncher.launch(intent);
        });

        btnSaveChanges.setOnClickListener(v -> saveChanges());

    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void saveChanges() {
        if (selectedImageUri!=null) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // 如果没有权限，请求权限
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        100);
            }
        }
        String nickname = etNickname.getText().toString().trim();
        if (nickname.isEmpty()) {
            Toast.makeText(this, "昵称不能为空", Toast.LENGTH_SHORT).show();
            return;
        } else {
            try {
                File file = getFileFromUri(EditInfoActivity.this, selectedImageUri);

                if (file.exists()) {
                    // 创建 RequestBody，用于文件上传
                    RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestBody);  // 设置表单的键值

                    // 创建 Retrofit 实例并进行文件上传
                    user.setName(nickname);
                    viewSharer.setUser(user);
                    Gson gson = new Gson();
                    String userJson = gson.toJson(user);
                    RequestBody userRequestBody = RequestBody.create(userJson, MediaType.parse("application/json"));
                    Call<String> call = userController.imageUpload(userRequestBody,body);

                    call.enqueue(new retrofit2.Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                            if (response.isSuccessful()) {
                                // 成功处理
                                Toast.makeText(EditInfoActivity.this, "信息已保存", Toast.LENGTH_SHORT).show();
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
                    System.out.println("文件不存在！");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // 保存昵称和其他信息
        // 这里可以调用您的后端 API 或者更新本地数据存储

    }
    @SuppressLint("Range")
    public static File getFileFromUri(Context context, Uri uri) {
        // 如果是 ContentProvider 类型的 URI（例如从图库获取的图片 URI）
        String filePath = "";

        if (uri != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                    DocumentsContract.isDocumentUri(context, uri)) {
                // 处理 Document URI，例如在 Android 4.4 及以上版本
                String docId = DocumentsContract.getDocumentId(uri);
                if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                    String id = docId.split(":")[1];  // 获取图片 ID
                    String[] columns = {MediaStore.Images.Media.DATA};
                    String selection = MediaStore.Images.Media._ID + "=?";
                    Cursor cursor = context.getContentResolver().query(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            columns,
                            selection,
                            new String[]{id},
                            null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        filePath = cursor.getString(cursor.getColumnIndex(columns[0]));
                        cursor.close();
                    }
                }
            } else {
                // 处理普通的 URI
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
                    cursor.close();
                }
            }
        }
        return new File(filePath);  // 返回文件对象
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 判断请求码是否匹配
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予，执行文件操作
                saveChanges();
            } else {
                // 权限被拒绝，提示用户
                Toast.makeText(this, "Permission denied, cannot access the file", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
