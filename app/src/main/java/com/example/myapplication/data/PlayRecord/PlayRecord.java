package com.example.myapplication.data.PlayRecord;

import com.example.myapplication.data.LedResource.LedResource;
import com.example.myapplication.data.User.User;

public class PlayRecord {

    private PlayRecordId id;         // 复合主键
    private User user;               // 用户
    private LedResource ledResource; // LED 资源

    // 获取资源ID
    public String getResourceId() {
        return ledResource != null ? ledResource.getResourceId() : null;
    }

    // 获取用户ID (这里返回的是用户的名字)
    public String getUserId() {
        return user != null ? user.getName() : null;
    }

    // Getter 和 Setter 方法
    public PlayRecordId getId() {
        return id;
    }

    public void setId(PlayRecordId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LedResource getLedResource() {
        return ledResource;
    }

    public void setLedResource(LedResource ledResource) {
        this.ledResource = ledResource;
    }
}
