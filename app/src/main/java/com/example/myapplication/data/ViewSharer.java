package com.example.myapplication.data;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

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

    private String listId;

    @Override
    public void onCreate() {
        super.onCreate();
        user=new User();

        // 设置全局的 UncaughtExceptionHandler
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            // 在 UI 线程中执行操作
            new Handler(Looper.getMainLooper()).post(() -> {
                // 使用 getApplicationContext() 获取应用上下文，防止 this 引用错误
                new AlertDialog.Builder(getApplicationContext()) // 使用合适的 context
                        .setTitle("公告")
                        .setMessage("由于后端服务器网络问题, 请确保当前页面完全加载后再执行操作, 同时不要操作的过于频繁")
                        .setPositiveButton("确定", (dialog, which) -> {
                            // 可以选择调用其他方法而不是直接退出
                            // System.exit(0);  // 强制退出应用
                            android.os.Process.killProcess(android.os.Process.myPid()); // 更优雅的退出应用
                        })
                        .show();
            });

            // 记录日志
            Log.e("UncaughtException", "Thread: " + thread.getName(), throwable);

            // 终止应用程序
            android.os.Process.killProcess(android.os.Process.myPid()); // 更优雅的退出应用
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
    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

}
