package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.data.User.User;
import com.example.myapplication.data.User.UserViewModel;
import com.example.myapplication.data.ViewSharer;
import com.example.myapplication.page.Login.LoginActivity;
import com.example.myapplication.page.Park.ParkActivity;
import com.example.myapplication.page.SwitchUser.SwitchUserActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.widget.ImageButton;
import android.widget.Toast;

public class CreationCenterActivity extends AppCompatActivity {
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_center_passenger);

        // 初始化按钮
        Button buttonDoodle = findViewById(R.id.button_doodle);
        Button buttonAnimation = findViewById(R.id.button_animation);
        Button buttonMusic = findViewById(R.id.button_music);
        Button buttonImage = findViewById(R.id.button_image);
        Button buttonPhoto = findViewById(R.id.button_photo);
        Button buttonText = findViewById(R.id.button_text);
        ImageButton imageButton = findViewById(R.id.image_button); // 新添加的图片按钮
        ViewSharer app = (ViewSharer) getApplication();

        user=app.getUser();
        // 获取 BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 设置默认选中项为创作中心
        bottomNavigationView.setSelectedItemId(R.id.nav_creation_center);

        // 设置点击监听器
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // 跳转到首页
                startActivity(new Intent(CreationCenterActivity.this, HomeActivity.class));
                overridePendingTransition(0, 0); // 可选，用于移除过渡动画
                return true;
            } else if (itemId == R.id.nav_square) {
                // 跳转到广场
                startActivity(new Intent(CreationCenterActivity.this, ParkActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_creation_center) {
                // 当前已经是创作中心
                return true;
            } else if (itemId == R.id.nav_shopping) {
                // 跳转到购物页面
                startActivity(new Intent(CreationCenterActivity.this, ShoppingActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                // 跳转到个人中心
                startActivity(new Intent(CreationCenterActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
        if (!user.getPermissionId().equals("1")){
            imageButton.setVisibility(View.GONE);
        }

        // 设置按钮点击监听器（可选）
        buttonDoodle.setOnClickListener(v -> {
            if (user.getPermissionId().equals("1")){
                Toast.makeText(this, "请登录", Toast.LENGTH_SHORT).show();
            }
            else {
                Intent intent = new Intent(CreationCenterActivity.this, DoodleActivity.class);
                startActivity(intent);
            }
        });

       /* buttonAnimation.setOnClickListener(v -> {
            Intent intent = new Intent(CreationCenterActivity.this, AnimationActivity.class);
            startActivity(intent);
        });

        buttonMusic.setOnClickListener(v -> {
            Intent intent = new Intent(CreationCenterActivity.this, MusicActivity.class);
            startActivity(intent);
        });

        buttonImage.setOnClickListener(v -> {
            Intent intent = new Intent(CreationCenterActivity.this, ImageActivity.class);
            startActivity(intent);
        });

        buttonPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(CreationCenterActivity.this, PhotoActivity.class);
            startActivity(intent);
        });

        buttonText.setOnClickListener(v -> {
            Intent intent = new Intent(CreationCenterActivity.this, TextActivity.class);
            startActivity(intent);
        });
*/
        // 设置图片按钮点击监听器
        imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(CreationCenterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}