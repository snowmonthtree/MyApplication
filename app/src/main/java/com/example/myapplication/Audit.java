package com.example.myapplication;

import com.example.myapplication.data.LedResource.LedResource;
import com.example.myapplication.data.User.User;

import java.sql.Timestamp;

public class Audit {

    private String auditId;           // 审计ID
    private LedResource resource;     // 资源对象，假设 LedResource 是前端的实体类
    private User user;                // 用户对象，假设 User 是前端的实体类
    private String auditName;         // 审计名称
    private Timestamp auditTime;      // 审计时间
    private String auditUrl;          // 审计URL

    // Getter and Setter methods

    public String getAuditId() {
        return auditId;
    }

    public void setAuditId(String auditId) {
        this.auditId = auditId;
    }

    public LedResource getResource() {
        return resource;
    }

    public void setResource(LedResource resource) {
        this.resource = resource;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAuditName() {
        return auditName;
    }

    public void setAuditName(String auditName) {
        this.auditName = auditName;
    }

    public Timestamp getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Timestamp auditTime) {
        this.auditTime = auditTime;
    }

    public String getAuditUrl() {
        return auditUrl;
    }

    public void setAuditUrl(String auditUrl) {
        this.auditUrl = auditUrl;
    }
}
