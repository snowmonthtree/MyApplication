package com.example.myapplication.data.PlayRecord;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

public class PlayRecordId implements Serializable {
    private static final long serialVersionUID = -4221582231141875123L;

    private String userId;      // 用户ID
    private String resourceId;  // 资源ID
    private Timestamp playTime; // 播放时间

    // Getter 和 Setter 方法
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public Timestamp getPlayTime() {
        return playTime;
    }

    public void setPlayTime(Timestamp playTime) {
        this.playTime = playTime;
    }

    // 重写 equals 和 hashCode 方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayRecordId that = (PlayRecordId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(resourceId, that.resourceId) &&
                Objects.equals(playTime, that.playTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, resourceId, playTime);
    }
}
