package com.example.myapplication;

public class ResultItem {
    private String title;
    private String description;
    private int iconResource;

    public ResultItem(String title, String description, int iconResource) {
        this.title = title;
        this.description = description;
        this.iconResource = iconResource;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getIconResource() {
        return iconResource;
    }
}
