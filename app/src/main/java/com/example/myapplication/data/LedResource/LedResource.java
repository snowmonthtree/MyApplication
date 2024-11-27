package com.example.myapplication.data.LedResource;

import java.sql.Timestamp;

public class LedResource {

    private String resourceId;        // Resource_ID
    private String userId;            // User_ID
    private String pixelSize;         // Pixel_Size
    private int downloadCount;        // Download_Count
    private String resourceWebUrl;    // Resource_Web_URL
    private String displayType;       // Display_Type
    private String name;              // name
    private int likes;                // Likes
    private String detail;            // Detail
    private String viewWebUrl;        // View_Web_URL
    private int commentNum;           // Comment_Num
    private Timestamp upTime;         // Up_Time

    // Getters and Setters
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

    public String getPixelSize() {
        return pixelSize;
    }

    public void setPixelSize(String pixelSize) {
        this.pixelSize = pixelSize;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public String getResourceWebUrl() {
        return resourceWebUrl;
    }

    public void setResourceWebUrl(String resourceWebUrl) {
        this.resourceWebUrl = resourceWebUrl;
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getViewWebUrl() {
        return viewWebUrl;
    }

    public void setViewWebUrl(String viewWebUrl) {
        this.viewWebUrl = viewWebUrl;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public Timestamp getUpTime() {
        return upTime;
    }

    public void setUpTime(Timestamp upTime) {
        this.upTime = upTime;
    }

    @Override
    public String toString() {
        return "LedResource{" +
                "resourceId='" + resourceId + '\'' +
                ", userId='" + userId + '\'' +
                ", pixelSize='" + pixelSize + '\'' +
                ", downloadCount=" + downloadCount +
                ", resourceWebUrl='" + resourceWebUrl + '\'' +
                ", displayType='" + displayType + '\'' +
                ", name='" + name + '\'' +
                ", likes=" + likes +
                ", detail='" + detail + '\'' +
                ", viewWebUrl='" + viewWebUrl + '\'' +
                ", commentNum=" + commentNum +
                ", upTime=" + upTime +
                '}';
    }
}
