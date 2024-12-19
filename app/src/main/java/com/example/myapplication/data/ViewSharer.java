package com.example.myapplication.data;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Intent;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.data.LedResource.LedResource;
import com.example.myapplication.data.User.User;
import com.example.myapplication.data.User.UserViewModel;
import com.example.myapplication.page.Login.LoginActivity;

public class ViewSharer extends Application {
    private User user;

    private LedResource ledResource;

    private final String path="https://1.95.83.162:8081/api/";

    @Override
    public void onCreate() {
        super.onCreate();
        user=new User();

        // 设置全局的 UncaughtExceptionHandler
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            // 记录日志（你也可以在这里上传日志信息到服务器等）
            new AlertDialog.Builder(this)
                    .setTitle("公告")
                    .setMessage("由于后端服务器网络问题,请确保当前页面完全加载后再执行操作,同时不要操作的过于频繁")
                    .setPositiveButton("确定", (dialog, which) -> {
                        // 确定按钮的点击事件
                    })
                    .show();

            // 这里你可以处理异常，比如跳转到一个错误页面或直接终止应用


            // 终止应用程序
            System.exit(0);
        });
   }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public String getPath() {
        return path;
    }
    public LedResource getLedResource() {
        return ledResource;
    }

    public void setLedResource(LedResource ledResource) {
        this.ledResource = ledResource;
    }

}
