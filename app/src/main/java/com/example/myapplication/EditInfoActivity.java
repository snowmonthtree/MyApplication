package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.example.myapplication.data.User.User;
import com.example.myapplication.data.ViewSharer;
import com.example.myapplication.page.ChangePassword.ChangePasswordActivity;

import java.io.FileNotFoundException;
import java.io.InputStream;

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
        String nickname = etNickname.getText().toString().trim();
        if (nickname.isEmpty()) {
            Toast.makeText(this, "昵称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedImageUri!=null){
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                // 处理 bitmap，例如显示在 ImageView
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        user.setName(nickname);
        viewSharer.setUser(user);
        // 保存昵称和其他信息
        // 这里可以调用您的后端 API 或者更新本地数据存储
        Toast.makeText(this, "信息已保存", Toast.LENGTH_SHORT).show();
    }
}