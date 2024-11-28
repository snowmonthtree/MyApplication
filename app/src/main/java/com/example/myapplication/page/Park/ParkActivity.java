package com.example.myapplication.page.Park;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.myapplication.Controller.LedResourceController;
import com.example.myapplication.page.CreationCenter.CreationCenterActivity;
import com.example.myapplication.page.Home.HomeActivity;
import com.example.myapplication.page.Video.PlayVideoActivity;
import com.example.myapplication.page.Profile.ProfileActivity;
import com.example.myapplication.R;
import com.example.myapplication.RetrofitClient;
import com.example.myapplication.SearchResultActivity;
import com.example.myapplication.page.Shopping.ShoppingActivity;
import com.example.myapplication.page.Profile.UserProfileActivity;
import com.example.myapplication.data.LedResource.LedResource;
import com.example.myapplication.data.User.User;
import com.example.myapplication.data.ViewSharer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.content.Intent;
import androidx.appcompat.widget.SearchView;
import android.widget.ImageButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ParkActivity extends AppCompatActivity {
    LedResourceController ledResourceController;
    Retrofit retrofit;
    private ImageButton[] imageButtons = new ImageButton[8];

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
                ViewSharer viewSharer=(ViewSharer)getApplication();
                User user=viewSharer.getUser();
                if (user.getPermissionId().equals("0")){
                    startActivity(new Intent(ParkActivity.this, UserProfileActivity.class));
                    overridePendingTransition(0, 0);
                }
                // 跳转到个人中心
                else {
                    startActivity(new Intent(ParkActivity.this, ProfileActivity.class));
                    overridePendingTransition(0, 0);
                }
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
         imageButtons[0] = findViewById(R.id.imageButton1);
         imageButtons[1] = findViewById(R.id.imageButton2);
         imageButtons[2] = findViewById(R.id.imageButton3);
       imageButtons[3] = findViewById(R.id.imageButton4);
         imageButtons[4] = findViewById(R.id.imageButton5);
         imageButtons[5]= findViewById(R.id.imageButton6);
        imageButtons[6] = findViewById(R.id.imageButton7);
         imageButtons[7] = findViewById(R.id.imageButton8);

        try {
            retrofit = RetrofitClient.getClient(ParkActivity.this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ledResourceController = retrofit.create(LedResourceController.class);
        Call< List <LedResource>> call=ledResourceController.getLatestLedResources();
        call.enqueue(new Callback<List<LedResource>>() {
            @Override
            public void onResponse(Call<List<LedResource>> call, Response<List<LedResource>> response) {
                List<LedResource> ledResources=response.body();
                if (response.isSuccessful() && response.body() != null) {
                    int i=0;
                    List<LedResource> resources = response.body();
                    for (LedResource resource : resources) {
                        String imageName = resource.getViewWebUrl();
                        fetchImage(imageName,i);  // 根据图片名称获取图片
                        i++;
                    }
                } else {
                    Log.e("parkTest", "fail" );
                }
            }

            @Override
            public void onFailure(Call<List<LedResource>> call, Throwable t) {
                Log.e("park test", "onFailure: "+t.getMessage() );

            }
        });
        // 设置点击监听器
        setImageButtonClickListener(imageButtons[1]);
        setImageButtonClickListener(imageButtons[2]);
        setImageButtonClickListener(imageButtons[3]);
        setImageButtonClickListener(imageButtons[4]);
        setImageButtonClickListener(imageButtons[5]);
        setImageButtonClickListener(imageButtons[6]);
        setImageButtonClickListener(imageButtons[7]);
        setImageButtonClickListener(imageButtons[0]);
    }

    private void setImageButtonClickListener(ImageButton imageButton) {
        imageButton.setOnClickListener(v -> {
            // 获取 ImageButton 上的 Bitmap
            Drawable drawable = imageButton.getDrawable();
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

                // 将 Bitmap 转换为字节数组
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                // 当用户点击 ImageButton 时，跳转到 PlayVideoActivity
                Intent intent = new Intent(ParkActivity.this, PlayVideoActivity.class);
                intent.putExtra("image", byteArray);
                startActivity(intent);
            }
        });
    }
    private void fetchImage(String imageName,int i) {
        ledResourceController.getImage(imageName).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 从ResponseBody获取图片数据并设置到ImageButton
                    ResponseBody responseBody = response.body();
                    Bitmap bitmap = convertResponseBodyToBitmap(responseBody);
                    if (bitmap != null) {
                        imageButtons[i].setImageBitmap(bitmap);  // 设置Bitmap到ImageButton
                    }
                } else {
                    // 图片加载失败，处理错误
                    Log.e("1", "onResponse: " );
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // 网络请求失败
                Log.e("2", "onFailure: ");
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
}