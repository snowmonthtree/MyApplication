package com.example.myapplication.data.PlayList;
import com.example.myapplication.data.User.User;

import java.io.Serializable;
import java.sql.Timestamp;

public class PlayList implements Serializable {
    private String playlistId;
    private User user;
    private String playlistName;
    private Timestamp createTime;  // 用String表示时间，因为安卓没有Timestamp类型
    private String playlistType;

    // Getter and Setter methods
    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getPlaylistType() {
        return playlistType;
    }

    public void setPlaylistType(String playlistType) {
        this.playlistType = playlistType;
    }
}
