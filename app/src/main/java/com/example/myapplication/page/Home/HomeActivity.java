package com.example.myapplication.page.Home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.widget.SearchView;

import android.widget.ListView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.ManageAuditActivity;
import com.example.myapplication.ManageCommentActivity;
import com.example.myapplication.ManageResourceActivity;
import com.example.myapplication.ManageUserActivity;
import com.example.myapplication.PlayListActivity;
import com.example.myapplication.page.SwitchUser.SwitchUserActivity;
import com.example.myapplication.ui.FunctionAdapter;
import com.example.myapplication.data.Function.FunctionItem;
import com.example.myapplication.page.Home.DownLoad.downLoadActivity;
import com.example.myapplication.page.Profile.ProfileActivity;
import com.example.myapplication.R;
import com.example.myapplication.page.Search.SearchResultActivity;
import com.example.myapplication.page.Shopping.ShoppingActivity;
import com.example.myapplication.page.Profile.UserProfileActivity;
import com.example.myapplication.data.User.User;
import com.example.myapplication.data.ViewSharer;
import com.example.myapplication.page.Bluetooth.BluetoothActivity;
import com.example.myapplication.page.CreationCenter.CreationCenterActivity;
import com.example.myapplication.page.Park.ParkActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private SearchView homeSearchbox;
    private ImageButton imageButton;
    private ActivityResultLauncher<Intent> enableBtLauncher;
    private ViewSharer viewSharer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 设置系统栏内边距
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 初始化视图
        homeSearchbox = findViewById(R.id.home_searchbox);
        imageButton = findViewById(R.id.imageButton);
        viewSharer=(ViewSharer)getApplication();

        // 获取 BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 设置默认选中项为首页
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        // 设置点击监听器
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // 当前已经是首页
                return true;
            } else if (itemId == R.id.nav_square) {
                // 跳转到广场
                startActivity(new Intent(HomeActivity.this, ParkActivity.class));
                overridePendingTransition(0, 0); // 可选，用于移除过渡动画
                return true;
            } else if (itemId == R.id.nav_creation_center) {
                // 跳转到创作中心
                startActivity(new Intent(HomeActivity.this, CreationCenterActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_shopping) {
                // 跳转到购物页面
                startActivity(new Intent(HomeActivity.this, ShoppingActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                ViewSharer viewSharer=(ViewSharer)getApplication();
                User user=viewSharer.getUser();
                if (!user.getPermissionId().equals("-1")){
                    startActivity(new Intent(HomeActivity.this, UserProfileActivity.class));
                    overridePendingTransition(0, 0);
                }
                // 跳转到个人中心
                else {
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                    overridePendingTransition(0, 0);
                }
                return true;
            }
            return false;
        });

        // 设置搜索框的监听器
        homeSearchbox.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 当用户提交搜索时，执行跳转逻辑
                Intent searchIntent = new Intent(HomeActivity.this, SearchResultActivity.class);
                searchIntent.putExtra("search_query", query);
                startActivity(searchIntent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 当搜索文本变化时，可以在这里处理实时搜索建议等
                return false;
            }
        });

        // 使用新的 Activity Result API
        enableBtLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // 蓝牙已开启
                        // 可以在这里进行进一步的操作
                    } else {
                        // 用户取消了蓝牙开启请求
                        Toast.makeText(HomeActivity.this, "请开启蓝牙以使用此功能", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // 使用 Lambda 表达式设置图片按钮的点击监听器
        imageButton.setOnClickListener(v -> {
            // 检查蓝牙权限
            Intent intent=new Intent(HomeActivity.this, BluetoothActivity.class);
            startActivity(intent);
        });
        List<FunctionItem> functionItems = new ArrayList<>();
        functionItems.add(new FunctionItem("查看已下载文件"));
        if (viewSharer.getUser().getPermissionId().equals("-1")){
            functionItems.add(new FunctionItem("登录以使用更多功能..."));
        }
        if(!viewSharer.getUser().getPermissionId().equals("-1")) {
            functionItems.add(new FunctionItem("查看当前播放序列"));
            functionItems.add(new FunctionItem("管理已上传的资源"));
            functionItems.add(new FunctionItem("管理评论"));
        }
        if (viewSharer.getUser().getPermissionId().equals("1")) {
            functionItems.add(new FunctionItem("管理用户"));
            functionItems.add(new FunctionItem("查看举报"));
        }
        // 初始化 ListView 和 Adapter
        ListView mannersList = findViewById(R.id.mannalist);
        FunctionAdapter adapter = new FunctionAdapter(this, functionItems);
        mannersList.setAdapter(adapter);

        // 设置点击监听器，使用 Lambda 表达式
        mannersList.setOnItemClickListener((parent, view, position, id) -> {
            FunctionItem selectedItem = (FunctionItem) parent.getItemAtPosition(position);
            switch (selectedItem.getTitle()) {
                case "查看已下载文件":
                    Intent downLoadIntent = new Intent(HomeActivity.this, downLoadActivity.class);
                    startActivity(downLoadIntent);
                    break;
                case "查看当前播放序列":
                    Intent playListIntent=new Intent(HomeActivity.this, PlayListActivity.class);
                    startActivity(playListIntent);
                    break;
                case "管理用户":
                    Intent manageUserIntent=new Intent(HomeActivity.this, ManageUserActivity.class);
                    startActivity(manageUserIntent);
                    break;
                case "管理已上传的资源" :
                    Intent manageResourceIntent=new Intent(HomeActivity.this, ManageResourceActivity.class);
                    startActivity(manageResourceIntent);
                    break;
                case "管理评论":
                    Intent manageCommentIntent=new Intent(HomeActivity.this, ManageCommentActivity.class);
                    startActivity(manageCommentIntent);
                    break;
                case "查看举报":
                    Intent manageAuditIntent=new Intent(HomeActivity.this, ManageAuditActivity.class);
                    startActivity(manageAuditIntent);
                    break;
                case "登录以使用更多功能...":
                    Intent loginIntent=new Intent(HomeActivity.this, SwitchUserActivity.class);
                    startActivity(loginIntent);
                    finishAffinity();
                    break;
            }
        });
    }




}