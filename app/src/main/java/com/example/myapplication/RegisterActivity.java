package com.example.myapplication;

import static java.security.AccessController.getContext;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        EditText userName=findViewById(R.id.newUsername);
        EditText userPassword=findViewById(R.id.newUserpassword);
        EditText userPassword1=findViewById(R.id.newUserpassword1);
        EditText userEmail=findViewById(R.id.newUseremail);
        EditText newCode=findViewById(R.id.newCode);
        Button getCode=findViewById(R.id.getCode);
        Button register=findViewById(R.id.register);
        CheckBox checkBox=findViewById(R.id.checkBox);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!userPassword.getText().toString().equals(userPassword1.getText().toString())){
                    new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("错误")
                            .setMessage("两次密码不一致")
                            .setPositiveButton("确定", (dialog, which) -> {
                                // 确定按钮的点击事件
                            })
                            .show();
                }
                else {
                    User newUser = new User();
                    newUser.setName(userName.getText().toString());
                    newUser.setPassword(userPassword.getText().toString());
                    newUser.setEmail(userEmail.getText().toString());
                    Retrofit retrofit;
                    try {
                        retrofit = RetrofitClient.getClient(RegisterActivity.this);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    ApiService apiService = retrofit.create(ApiService.class);
                    Call<String> call = apiService.insertUser(newUser);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, response.body(), Toast.LENGTH_SHORT).show();

                            } else {
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
                            Log.e("error", t.getClass().getName() + ", Message: " + t.getMessage());
                        }

                    });
                }
            }
        });
    }
}