package com.example.newsapp;

public class News {

    private final String title;
    private final String content;
    private final String searchTitle;

    public News(String title, String content) {
        this.title = title;
        this.content = content;
        this.searchTitle=title.toLowerCase();
    }


    public String getSearchTitle() {
        return searchTitle;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

}
