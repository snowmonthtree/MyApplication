package com.example.myapplication.page.Park;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.myapplication.CreationCenterActivity;
import com.example.myapplication.HomeActivity;
import com.example.myapplication.PlayVideoActivity;
import com.example.myapplication.ProfileActivity;
import com.example.myapplication.R;
import com.example.myapplication.SearchResultActivity;
import com.example.myapplication.ShoppingActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.content.Intent;
import androidx.appcompat.widget.SearchView;
import android.widget.ImageButton;

public class ParkActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_park);

        // 设置系统栏内边距
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 获取 BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 设置默认选中项为广场
        bottomNavigationView.setSelectedItemId(R.id.nav_square);

        // 设置点击监听器
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // 跳转到首页
                startActivity(new Intent(ParkActivity.this, HomeActivity.class));
                overridePendingTransition(0, 0); // 可选，用于移除过渡动画
                return true;
            } else if (itemId == R.id.nav_square) {
                // 当前已经是广场页面
                return true;
            } else if (itemId == R.id.nav_creation_center) {
                // 跳转到创作中心
                startActivity(new Intent(ParkActivity.this, CreationCenterActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_shopping) {
                // 跳转到购物页面
                startActivity(new Intent(ParkActivity.this, ShoppingActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                // 跳转到个人中心
                startActivity(new Intent(ParkActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });

        // 获取 Spinner
        Spinner spinner = findViewById(R.id.spinner);

        // 创建适配器
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.size_options, android.R.layout.simple_spinner_item);

        // 设置下拉列表的样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 绑定适配器到 Spinner
        spinner.setAdapter(adapter);

        // 设置默认选中的项
        spinner.setSelection(0); // 选择第一个项，即“大小”

        // 获取 SearchView 并设置点击事件
        SearchView searchView = findViewById(R.id.park_searchbox);

        // 设置搜索框点击监听器
        searchView.setOnClickListener(v -> {
            // 当用户点击搜索框时，跳转到搜索结果页面
            Intent intent = new Intent(ParkActivity.this, SearchResultActivity.class);
            startActivity(intent);
        });

        // 设置搜索框文本提交监听器
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 当用户提交搜索请求时触发
                // 这里可以启动一个新的 Activity 来显示搜索结果
                Intent intent = new Intent(ParkActivity.this, SearchResultActivity.class);
                intent.putExtra("search_query", query);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 当搜索文本改变时触发
                return false;
            }
        });

        // 获取并设置 ImageButtons 的点击监听器
        ImageButton imageButton1 = findViewById(R.id.imageButton1);
        ImageButton imageButton2 = findViewById(R.id.imageButton2);
        ImageButton imageButton3 = findViewById(R.id.imageButton3);
        ImageButton imageButton4 = findViewById(R.id.imageButton4);
        ImageButton imageButton5 = findViewById(R.id.imageButton5);
        ImageButton imageButton6 = findViewById(R.id.imageButton6);
        ImageButton imageButton7 = findViewById(R.id.imageButton7);
        ImageButton imageButton8 = findViewById(R.id.imageButton8);

        // 设置点击监听器
        setImageButtonClickListener(imageButton1);
        setImageButtonClickListener(imageButton2);
        setImageButtonClickListener(imageButton3);
        setImageButtonClickListener(imageButton4);
        setImageButtonClickListener(imageButton5);
        setImageButtonClickListener(imageButton6);
        setImageButtonClickListener(imageButton7);
        setImageButtonClickListener(imageButton8);
    }

    private void setImageButtonClickListener(ImageButton imageButton) {
        imageButton.setOnClickListener(v -> {
            // 当用户点击 ImageButton 时，跳转到 PlayVideoActivity
            Intent intent = new Intent(ParkActivity.this, PlayVideoActivity.class);
            startActivity(intent);
        });
    }
}