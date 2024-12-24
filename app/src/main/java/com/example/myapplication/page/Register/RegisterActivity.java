package com.example.myapplication.page.Register;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.Controller.UserController;
import com.example.myapplication.R;
import com.example.myapplication.RetrofitClient;
import com.example.myapplication.data.User.User;
import com.example.myapplication.page.Login.LoginActivity;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {
    private EditText userName;
    private EditText userPassword;
    private EditText userPassword1;
    private EditText userEmail;
    private EditText newCode;
    private Button getCode;
    private Button register;
    private CheckBox checkBox;
    private Retrofit retrofit;
    private UserController userController;
    private User newUser;

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
        userName=findViewById(R.id.newUsername);
        userPassword=findViewById(R.id.newUserpassword);
        userPassword1=findViewById(R.id.newUserpassword1);
        userEmail=findViewById(R.id.newUseremail);
        newCode=findViewById(R.id.newCode);
        getCode=findViewById(R.id.getCode);
        register=findViewById(R.id.register);
        checkBox=findViewById(R.id.checkBox);
        try {
            retrofit = RetrofitClient.getClient(RegisterActivity.this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        userController = retrofit.create(UserController.class);
        register.setOnClickListener(view -> Register() );
        getCode.setOnClickListener(view -> getCode());
    }
    private void Register() {
        if (!userPassword.getText().toString().equals(userPassword1.getText().toString())) {
            new AlertDialog.Builder(RegisterActivity.this)
                    .setTitle("错误")
                    .setMessage("两次密码不一致")
                    .setPositiveButton("确定", (dialog, which) -> {
                        // 确定按钮的点击事件
                    })
                    .show();
        }
        else if (userPassword.getText().length()<8 || userPassword.getText().length()>16){
            new AlertDialog.Builder(RegisterActivity.this)
                    .setTitle("错误")
                    .setMessage("密码长度不对")
                    .setPositiveButton("确定", (dialog, which) -> {
                        // 确定按钮的点击事件
                    })
                    .show();
        } else if (userName.getText().length()>10 || userName.getText().length()<1) {
            new AlertDialog.Builder(RegisterActivity.this)
                    .setTitle("错误")
                    .setMessage("用户名长度不能为空,也不能超过10")
                    .setPositiveButton("确定", (dialog, which) -> {
                        // 确定按钮的点击事件
                    })
                    .show();
        } else {
            newUser=new User();
            newUser.setName(userName.getText().toString());
            newUser.setPassword(userPassword.getText().toString());
            newUser.setEmail(userEmail.getText().toString());

            Call<String> call = userController.insertUser(newUser,newCode.getText().toString());
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                        Log.e("test", "onResponse: "+response.body() );
                        new AlertDialog.Builder(RegisterActivity.this)
                                .setTitle(response.body())
                                .setMessage("即将返回上一级")
                                .setPositiveButton("确定",(dialog,which)->{
                                    finish();
                                })
                                .show();

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
    private void getCode(){
        Toast.makeText(this, "验证码已发送,若未收到验证码,请检查邮箱是否正确", Toast.LENGTH_SHORT).show();
        Call<String> call=userController.getCode(userEmail.getText().toString());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.e("test", "onResponse: "+response.body() );
                    Toast.makeText(RegisterActivity.this, response.body()+" ", Toast.LENGTH_SHORT).show();
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