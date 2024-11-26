package com.example.myapplication.page.Park;


import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.ArrayAdapter;
import androidx.appcompat.widget.SearchView;
import android.widget.Spinner;
import android.widget.ImageButton;
import android.view.View;


import com.example.myapplication.CreationCenterActivity;
import com.example.myapplication.HomeActivity;
import com.example.myapplication.ProfileActivity;
import com.example.myapplication.R;
import com.example.myapplication.page.Bluetooth.BluetoothActivity;
import com.example.myapplication.SearchResultActivity;
import com.example.myapplication.ShoppingActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.content.Intent;

public class ParkActivity extends AppCompatActivity {
    private SearchView parkSearch;

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
        parkSearch=findViewById(R.id.park_searchbox);

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
        // 设置 OnQueryTextListener
        parkSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 用户提交查询时的处理逻辑
                Intent searchIntent = new Intent(ParkActivity.this, SearchResultActivity.class);
                searchIntent.putExtra("query", query);
                startActivity(searchIntent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 用户输入搜索时的实时搜索逻辑，可以进行实时查询等
                return false;
            }
        });

        // 给每个 ImageButton 设置点击事件
        setImageButtonClickListener(R.id.imageButton1);
        setImageButtonClickListener(R.id.imageButton2);
        setImageButtonClickListener(R.id.imageButton3);
        setImageButtonClickListener(R.id.imageButton4);
        setImageButtonClickListener(R.id.imageButton5);
        setImageButtonClickListener(R.id.imageButton6);
        setImageButtonClickListener(R.id.imageButton7);
        setImageButtonClickListener(R.id.imageButton8);


        // 获取 ImageButton 的引用
        ImageButton imageButton1 = findViewById(R.id.imageButton1);
        ImageButton imageButton2 = findViewById(R.id.imageButton2);
        ImageButton imageButton3 = findViewById(R.id.imageButton3);
        ImageButton imageButton4 = findViewById(R.id.imageButton4);
        ImageButton imageButton5 = findViewById(R.id.imageButton5);
        ImageButton imageButton6 = findViewById(R.id.imageButton6);
        ImageButton imageButton7 = findViewById(R.id.imageButton7);
        ImageButton imageButton8 = findViewById(R.id.imageButton8);

        // 设置图片资源
        imageButton1.setImageResource(R.drawable.image1);  // 替换为你需要的图片资源
        imageButton2.setImageResource(R.drawable.image2);  // 替换为你需要的图片资源
        imageButton3.setImageResource(R.drawable.image3);  // 替换为你需要的图片资源
        imageButton4.setImageResource(R.drawable.image4);  // 替换为你需要的图片资源
        imageButton5.setImageResource(R.drawable.image5);  // 替换为你需要的图片资源
        imageButton6.setImageResource(R.drawable.image6);  // 替换为你需要的图片资源
        imageButton7.setImageResource(R.drawable.image7);  // 替换为你需要的图片资源
        imageButton8.setImageResource(R.drawable.image8);  // 替换为你需要的图片资源


    }

    private void setImageButtonClickListener(int imageButtonId) {
        ImageButton imageButton = findViewById(imageButtonId);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取点击的图片资源 ID
                int imageResId = -1; // 默认值

                // 使用 if-else 来决定图片资源
                if (imageButtonId == R.id.imageButton1) {
                    imageResId = R.drawable.image1;  // 替换为实际图片资源
                } else if (imageButtonId == R.id.imageButton2) {
                    imageResId = R.drawable.image2;  // 替换为实际图片资源
                } else if (imageButtonId == R.id.imageButton3) {
                    imageResId = R.drawable.image3;  // 替换为实际图片资源
                } else if (imageButtonId == R.id.imageButton4) {
                    imageResId = R.drawable.image4;  // 替换为实际图片资源
                } else if (imageButtonId == R.id.imageButton5) {
                    imageResId = R.drawable.image5;  // 替换为实际图片资源
                } else if (imageButtonId == R.id.imageButton6) {
                    imageResId = R.drawable.image6;  // 替换为实际图片资源
                } else if (imageButtonId == R.id.imageButton7) {
                    imageResId = R.drawable.image7;  // 替换为实际图片资源
                } else if (imageButtonId == R.id.imageButton8) {
                    imageResId = R.drawable.image8;  // 替换为实际图片资源
                }

                // 创建跳转 Intent，传递图片资源 ID
                Intent intent = new Intent(ParkActivity.this, BluetoothActivity.class);
                intent.putExtra("image_res_id", imageResId);
                startActivity(intent);
            }
        });
    }
}