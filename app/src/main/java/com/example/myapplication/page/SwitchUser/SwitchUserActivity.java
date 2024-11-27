package com.example.myapplication.page.SwitchUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.data.User.User;
import com.example.myapplication.data.User.UserViewModel;
import com.example.myapplication.data.ViewSharer;
import com.example.myapplication.page.Bluetooth.BluetoothActivity;
import com.example.myapplication.page.Login.LoginActivity;
import com.example.myapplication.page.Park.ParkActivity;

public class SwitchUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_switch_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button button=findViewById(R.id.login_or_register);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SwitchUserActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        Button button1=findViewById(R.id.youke_mode);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 获取 Application 实例并通过它获取 UserViewModel 实例
                Intent intent=new Intent(SwitchUserActivity.this, ParkActivity.class);
                startActivity(intent);
            }
        });
    }
}
