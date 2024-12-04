package com.example.myapplication.data.Comment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Comment {

    private String commentId;  // 评论的唯一标识符

    private String resourceId;  // 资源的唯一标识符

    private String userId;  // 用户的唯一标识符

    private String commentContext;  // 评论内容

    private String commentTime;  // 评论时间

    // 构造函数
    public Comment() {}

    public Comment(String commentId, String resourceId, String userId, String commentContext, String commentTime) {
        this.commentId = commentId;
        this.resourceId = resourceId;
        this.userId = userId;
        this.commentContext = commentContext;
        this.commentTime = commentTime;
    }

    // Getter 和 Setter 方法

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommentContext() {
        return commentContext;
    }

    public void setCommentContext(String commentContext) {
        this.commentContext = commentContext;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId='" + commentId + '\'' +
                ", resourceId='" + resourceId + '\'' +
                ", userId='" + userId + '\'' +
                ", commentContext='" + commentContext + '\'' +
                ", commentTime='" + commentTime + '\'' +
                '}';
    }
}

