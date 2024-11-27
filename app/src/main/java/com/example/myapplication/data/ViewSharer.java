package com.example.myapplication.data;

import android.app.Application;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.data.User.User;
import com.example.myapplication.data.User.UserViewModel;

public class ViewSharer extends Application {
    private User user;

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
}
