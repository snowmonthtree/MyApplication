package com.example.myapplication.page.ChangePassword;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.ApiService;
import com.example.myapplication.R;
import com.example.myapplication.RetrofitClient;
import com.example.myapplication.User;
import com.example.myapplication.UserViewModel;
import com.example.myapplication.page.Register.RegisterActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText userPassword;
    private EditText userPassword1;
    private EditText userMail;
    private EditText newCode;
    private Button getCode;
    private Button reset;
    private UserViewModel userViewModel;
    private User user;

    Retrofit retrofit;
    ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        userPassword = findViewById(R.id.newUserpassword);
        userPassword1 = findViewById(R.id.newUserpassword1);
        userMail = findViewById(R.id.newUseremail);
        newCode = findViewById(R.id.newCode);
        getCode = findViewById(R.id.getCode);
        reset = findViewById(R.id.reset);
        userViewModel= new ViewModelProvider(this).get(UserViewModel.class);
        reset.setOnClickListener(v->reset());
    }
    //重置密码按键的处理
    private void reset() {
        if (!userPassword.getText().toString().equals(userPassword1.getText().toString())) {
            new AlertDialog.Builder(ChangePasswordActivity.this)
                    .setTitle("错误")
                    .setMessage("两次密码不一致")
                    .setPositiveButton("确定", (dialog, which) -> {
                        // 确定按钮的点击事件
                    })
                    .show();
        }
        else {
            user=new User();
            //检查邮箱
            user.setEmail(userMail.getText().toString());
            //设置与后端的连接
            try {
                retrofit = RetrofitClient.getClient(ChangePasswordActivity.this);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            //设置控制器，获得接口
            apiService = retrofit.create(ApiService.class);
            Call<String> call = apiService.changePassword(userMail.getText().toString(),userPassword.getText().toString());
            //获得服务器的返回信息
            call.enqueue(new Callback<String>() {
                public void onResponse(Call<String> call, Response<String> response) {
                    String data = response.body();
                    Toast.makeText(ChangePasswordActivity.this, data, Toast.LENGTH_SHORT).show();
                    //如果成功,更新ViewModel
                    if (data.equals("success")) {
                        user.setPassword(userPassword1.getText().toString());
                        userViewModel.setUser(user);
                    }
                }

                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("error", "change error" + t.getMessage());
                }
            });
        }
    }
}