package com.example.myapplication.page.CreationCenter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Controller.LedResourceController;
import com.example.myapplication.Controller.PlayRecordController;
import com.example.myapplication.PlayRecord;
import com.example.myapplication.RetrofitClient;
import com.example.myapplication.TextActivity;
import com.example.myapplication.data.LedResource.LedResource;
import com.example.myapplication.data.Result.ResultItem;
import com.example.myapplication.page.CreationCenter.Doodle.DoodleActivity;
import com.example.myapplication.page.Home.HomeActivity;
import com.example.myapplication.LocalAdapter;
import com.example.myapplication.page.Profile.ProfileActivity;
import com.example.myapplication.R;
import com.example.myapplication.page.Search.SearchResultActivity;
import com.example.myapplication.page.Shopping.ShoppingActivity;
import com.example.myapplication.page.Profile.UserProfileActivity;
import com.example.myapplication.data.User.User;
import com.example.myapplication.data.ViewSharer;
import com.example.myapplication.page.Login.LoginActivity;
import com.example.myapplication.page.Park.ParkActivity;
import com.example.myapplication.ui.ResultAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.view.View;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CreationCenterActivity extends AppCompatActivity {

    private RecyclerView recyclerViewHistory;
    private RecyclerView recyclerViewLocal;
    private ResultAdapter resultAdapter;
    private LocalAdapter localAdapter;
    private Retrofit retrofit;
    private LedResourceController ledResourceController;
    private PlayRecordController playRecordController;
    private ViewSharer viewSharer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_center_passenger);
        Log.e("1111", "onCreate: "+ getFilesDir() );

        // 初始化按钮
        Button buttonDoodle = findViewById(R.id.button_doodle);
        Button buttonAnimation = findViewById(R.id.button_animation);
        Button buttonMusic = findViewById(R.id.button_music);
        Button buttonImage = findViewById(R.id.button_image);
        Button buttonPhoto = findViewById(R.id.button_photo);
        Button buttonText = findViewById(R.id.button_text);
        try {
            retrofit = RetrofitClient.getClient(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ledResourceController = retrofit.create(LedResourceController.class);
        playRecordController = retrofit.create(PlayRecordController.class);
        viewSharer=(ViewSharer)getApplication();
        ImageButton imageButton = findViewById(R.id.image_button); // 新添加的图片按钮

        // 获取 BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 设置默认选中项为创作中心
        bottomNavigationView.setSelectedItemId(R.id.nav_creation_center);
        // 监听 RadioGroup 的选择变化
        RadioGroup radioGroup = findViewById(R.id.radio_group);
        // 初始化 RecyclerView
        recyclerViewHistory = findViewById(R.id.recycler_view_history);
        recyclerViewLocal = findViewById(R.id.recycler_view_local);

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
                ViewSharer viewSharer=(ViewSharer)getApplication();
                User user=viewSharer.getUser();
                if (user.getPermissionId().equals("0")){
                    startActivity(new Intent(CreationCenterActivity.this, UserProfileActivity.class));
                    overridePendingTransition(0, 0);
                }
                // 跳转到个人中心
                else {
                    startActivity(new Intent(CreationCenterActivity.this, ProfileActivity.class));
                    overridePendingTransition(0, 0);
                }
                return true;
            }
            return false;
        });

        // 设置按钮点击监听器（可选）
        buttonDoodle.setOnClickListener(v -> {
            Intent intent = new Intent(CreationCenterActivity.this, DoodleActivity.class);
            startActivity(intent);
        });

        /*
        buttonAnimation.setOnClickListener(v -> {
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
        });*/

        buttonText.setOnClickListener(v -> {
            Intent intent = new Intent(CreationCenterActivity.this, TextActivity.class);
            startActivity(intent);
        });


        // 设置图片按钮点击监听器
        imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(CreationCenterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
        ViewSharer viewSharer=(ViewSharer)getApplication();
        User user=viewSharer.getUser();
        if (user.getPermissionId().equals("1")){
            imageButton.setVisibility(View.VISIBLE);
            recyclerViewLocal.setVisibility(View.GONE);
            recyclerViewHistory.setVisibility(View.GONE);
            radioGroup.setVisibility(View.GONE);
        }
        else {
            List<String> localList = new ArrayList<>();
            localList.add("本地资源1");
            localList.add("本地资源2");
            localList.add("本地资源3");

            // 设置适配器
            List<ResultItem> historyList = new ArrayList<>();

            resultAdapter = new ResultAdapter(historyList, ledResourceController, this);
            recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewHistory.setAdapter(resultAdapter);

            recyclerViewLocal.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewLocal.setAdapter(localAdapter);

            // 设置默认选中项
            RadioButton radioButtonHistory = findViewById(R.id.radio_history);
            radioButtonHistory.setChecked(true);


            resultAdapter.updateData(getSampleData(user.getUserId()));
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.radio_history) {
                    // 显示历史记录内容
                    recyclerViewHistory.setVisibility(View.VISIBLE);
                    recyclerViewLocal.setVisibility(View.GONE);
                } else if (checkedId == R.id.radio_local) {
                    // 显示本地资源内容
                    recyclerViewHistory.setVisibility(View.GONE);
                    recyclerViewLocal.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private List<ResultItem> getSampleData(String userId) {
        // 清空历史列表，准备加载新数据
        List<ResultItem> sampleData = new ArrayList<>();

        // 发起异步请求，获取历史记录数据
        Call<List<PlayRecord>> call1 = playRecordController.showPlayRecordByUserId(userId);
        call1.enqueue(new Callback<List<PlayRecord>>() {
            @Override
            public void onResponse(Call<List<PlayRecord>> call, Response<List<PlayRecord>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PlayRecord> list = response.body();
                    for (PlayRecord playRecord : list) {
                        // 添加数据到历史记录列表
                        sampleData.add(new ResultItem(
                                playRecord.getLedResource().getName(),
                                playRecord.getLedResource().getDetail(),
                                playRecord.getLedResource().getViewWebUrl(),
                                playRecord.getLedResource().getResourceId()
                        ));
                        Log.e("test", "onResponse: "+sampleData );
                    }
                    // 更新适配器数据
                    resultAdapter.updateData(sampleData);
                } else {
                    // 如果请求失败，添加错误数据
                    sampleData.add(new ResultItem("错误", "网络问题", "image10.jpg", "1"));
                    resultAdapter.updateData(sampleData);
                }
                resultAdapter.updateData(sampleData);
            }

            @Override
            public void onFailure(Call<List<PlayRecord>> call, Throwable t) {
                Log.e("error", "onFailure: " + t.getMessage());
                Toast.makeText(CreationCenterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return sampleData;
    }

}