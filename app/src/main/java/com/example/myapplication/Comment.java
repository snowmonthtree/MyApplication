package com.example.myapplication;

public class Comment {
    private String author;
    private String text;
    private String date;

    public Comment(String author, String text, String date) {
        this.author = author;
        this.text = text;
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }
}
