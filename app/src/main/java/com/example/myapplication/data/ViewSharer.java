package com.example.myapplication.data;

import android.app.Application;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.data.LedResource.LedResource;
import com.example.myapplication.data.User.User;
import com.example.myapplication.data.User.UserViewModel;

public class ViewSharer extends Application {
    private User user;

    private LedResource ledResource;

    private final String path="https://1.95.83.162:8081/api/";

    @Override
    public void onCreate() {
        super.onCreate();
        user=new User();
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
