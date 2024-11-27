package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.data.User.User;
import com.example.myapplication.data.ViewSharer;
import com.example.myapplication.page.Login.LoginActivity;
import com.example.myapplication.page.Park.ParkActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_user);

        // 设置系统栏内边距
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 获取 BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 设置默认选中项为个人中心
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        // 设置点击监听器
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // 跳转到首页
                startActivity(new Intent(UserProfileActivity.this, HomeActivity.class));
                overridePendingTransition(0, 0); // 可选，用于移除过渡动画
                return true;
            } else if (itemId == R.id.nav_square) {
                // 跳转到广场页面
                startActivity(new Intent(UserProfileActivity.this, ParkActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_creation_center) {
                // 跳转到创作中心
                startActivity(new Intent(UserProfileActivity.this, CreationCenterActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_shopping) {
                // 跳转到购物页面
                startActivity(new Intent(UserProfileActivity.this, ShoppingActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                // 当前已经是个人中心页面
                return true;
            }
            return false;
        });

        // 初始化数据
        List<FunctionItem> functionItems = new ArrayList<>();
        functionItems.add(new FunctionItem("设置"));
        functionItems.add(new FunctionItem("修改信息"));
        functionItems.add(new FunctionItem("关于我们"));
        functionItems.add(new FunctionItem("检查更新"));
        // 可以继续添加更多的功能选项...

        // 初始化 ListView 和 Adapter
        ListView mannersList = findViewById(R.id.mannersList);
        FunctionAdapter adapter = new FunctionAdapter(this, functionItems);
        mannersList.setAdapter(adapter);

        // 设置点击监听器，使用 Lambda 表达式
        mannersList.setOnItemClickListener((parent, view, position, id) -> {
            FunctionItem selectedItem = (FunctionItem) parent.getItemAtPosition(position);
            switch (selectedItem.getTitle()) {
                case "设置":
                    Intent settingsIntent = new Intent(UserProfileActivity.this, SettingsActivity.class);
                    startActivity(settingsIntent);
                    break;
                case "修改信息":
                    Intent editInfoIntent = new Intent(UserProfileActivity.this, EditInfoActivity.class);
                    startActivity(editInfoIntent);
                    break;
                case "关于我们":
                    Intent aboutUsIntent = new Intent(UserProfileActivity.this, AboutUsActivity.class);
                    startActivity(aboutUsIntent);
                    break;
                case "检查更新":
                    Intent checkUpdateIntent = new Intent(UserProfileActivity.this, CheckUpdateActivity.class);
                    startActivity(checkUpdateIntent);
                    break;
                // 处理其他功能选项...
            }
        });

        // 初始化注销按钮和退出按钮
        Button buttonLogout = findViewById(R.id.buttonLogout);
        Button buttonExit = findViewById(R.id.buttonExit);

        // 设置注销按钮的点击监听器
        buttonLogout.setOnClickListener(v -> {
            // 创建 Intent 并启动登录页面（假设登录页面为 LoginActivity）
            User user=new User();
            ViewSharer viewSharer=(ViewSharer)getApplication();
            viewSharer.setUser(user);
            Intent logoutIntent = new Intent(UserProfileActivity.this, LoginActivity.class);
            startActivity(logoutIntent);
            // 可以选择在这里清除用户的登录状态
            finish(); // 关闭当前活动
        });

        // 设置退出按钮的点击监听器
        buttonExit.setOnClickListener(v -> {
            // 退出应用
            finishAffinity(); // 结束所有活动
            System.exit(0); // 退出应用
        });
    }
}