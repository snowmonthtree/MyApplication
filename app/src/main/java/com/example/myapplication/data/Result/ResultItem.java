package com.example.myapplication.data.Result;

public class ResultItem {
    private String title;
    private String description;
    private String iconResource;
    private String resourceId;

    public ResultItem(String title, String description, String iconResource,String resourceId) {
        this.title = title;
        this.description = description;
        this.iconResource = iconResource;
        this.resourceId=resourceId;
    }

    public String getResourceId() {
        return resourceId;
    }
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getIconResource() {
        return iconResource;
    }
}
