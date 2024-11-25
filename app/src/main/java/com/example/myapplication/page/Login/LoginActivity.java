package com.example.myapplication.page.Login;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.ApiService;
import com.example.myapplication.User;
import com.example.myapplication.UserViewModel;
import com.example.myapplication.page.ChangePassword.ChangePasswordActivity;
import com.example.myapplication.page.Park.ParkActivity;
import com.example.myapplication.R;
import com.example.myapplication.page.Register.RegisterActivity;
import com.example.myapplication.RetrofitClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //跳转注册页面
        TextView register_now = findViewById(R.id.register_now);
        register_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        //跳转修改密码页面
        TextView change_password = findViewById(R.id.change_password_1);
        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
        EditText userEmail = findViewById(R.id.getEmail);
        EditText passWord = findViewById(R.id.Password);
        Button button = findViewById(R.id.login);
        CheckBox checkBox = findViewById(R.id.checkBox1);

        UserViewModel userViewModel=new ViewModelProvider(this).get(UserViewModel.class);

        Retrofit retrofit1;
        try {
            retrofit1 = RetrofitClient.getClient(LoginActivity.this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Retrofit finalRetrofit = retrofit1;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiService apiService1= finalRetrofit.create(ApiService.class);
                Call<User> call = apiService1.getUser(userEmail.getText().toString(),passWord.getText().toString());
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()) {
                            User data = response.body();
                            // 处理数据
                            if (data==null)
                            {
                            runOnUiThread(() -> {
                                new AlertDialog.Builder(LoginActivity.this)
                                        .setTitle("错误")
                                        .setMessage("邮箱或密码错误")
                                        .setPositiveButton("确定", (dialog, which) -> {
                                            // 确定按钮的点击事件
                                        })
                                        .show();
                            });
                        }
                            else {
                                userViewModel.setUser(data);
                                Intent intent = new Intent(LoginActivity.this, ParkActivity.class);
                                startActivity(intent);
                            }

                        }
                        else {
                            Log.e("NetworkRequest", "Response not successful. Status Code: " + response.code());
                            // 进一步打印错误信息
                            try {
                                // 获取服务器返回的错误消息
                                String errorBody = response.errorBody().string();
                                Log.e("NetworkRequest", "Error Body: " + errorBody);
                            } catch (IOException e) {
                                Log.e("NetworkRequest", "Error reading the error body", e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        // 处理错误
                        Log.e("NetworkRequest", "onFailure triggered");
                        Log.e("error",t.getClass().getName() + ", Message: " + t.getMessage());
                    }
                });
            }
        });
    }
}