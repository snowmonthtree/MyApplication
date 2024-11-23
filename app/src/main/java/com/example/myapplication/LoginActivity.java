package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.OkHttpClient;
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
        TextView register_now = findViewById(R.id.register_now);
        register_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        TextView change_password = findViewById(R.id.change_password_1);
        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
        EditText userId = findViewById(R.id.getUsername);
        EditText passWord = findViewById(R.id.Password);
        Button button = findViewById(R.id.login);
        CheckBox checkBox = findViewById(R.id.checkBox1);

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
                Call<String> call = apiService1.getData(userId.getText().toString(),passWord.getText().toString());
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            String data = response.body();
                            // 处理数据
                            if (data.equals("User not found"))
                            {
                            runOnUiThread(() -> {
                                new AlertDialog.Builder(LoginActivity.this)
                                        .setTitle("错误")
                                        .setMessage("用户名或密码错误")
                                        .setPositiveButton("确定", (dialog, which) -> {
                                            // 确定按钮的点击事件
                                        })
                                        .show();
                            });
                        }
                            else {
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
                    public void onFailure(Call<String> call, Throwable t) {
                        // 处理错误
                        Log.e("NetworkRequest", "onFailure triggered");
                        Log.e("error",t.getClass().getName() + ", Message: " + t.getMessage());
                    }
                });
            }
        });
    }
}