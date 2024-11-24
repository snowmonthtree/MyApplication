package com.example.myapplication.page.ChangePassword;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;

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
        EditText newCode=findViewById(R.id.newCode);
        Button getCode=findViewById(R.id.getCode);
        Button reset=findViewById(R.id.reset);
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
            }
        });
    }
}