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
        EditText userPassword=findViewById(R.id.newUserpassword);
        EditText userPassword1=findViewById(R.id.newUserpassword1);
        EditText userMail=findViewById(R.id.main);
        EditText newCode=findViewById(R.id.newCode);
        Button getCode=findViewById(R.id.getCode);
        Button reset=findViewById(R.id.reset);

        UserViewModel userViewModel=new ViewModelProvider(this).get(UserViewModel.class);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!userPassword.getText().toString().equals(userPassword1.getText().toString())){
                    new AlertDialog.Builder(ChangePasswordActivity.this)
                            .setTitle("错误")
                            .setMessage("两次密码不一致")
                            .setPositiveButton("确定", (dialog, which) -> {
                                // 确定按钮的点击事件
                            })
                            .show();
                }
                else {
                    User user=new User();
                    user.setEmail(userMail.getText().toString());
                    Retrofit retrofit;
                    try {
                        retrofit = RetrofitClient.getClient(ChangePasswordActivity.this);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    ApiService apiService = retrofit.create(ApiService.class);
                    Call<String> call = apiService.changePassword(user);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            String data=response.body();
                            Toast.makeText(ChangePasswordActivity.this, data, Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e("error", "change error"+t.getMessage() );
                        }
                    });
                }
            }
        });
    }
}