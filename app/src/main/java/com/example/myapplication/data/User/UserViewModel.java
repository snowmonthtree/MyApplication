package com.example.myapplication.data.User;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {

    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();

    // 获取 User 数据
    public LiveData<User> getUser() {
        return userLiveData;
    }

    // 更新 User 数据
    public void setUser(User user) {
        userLiveData.setValue(user);
    }
}

