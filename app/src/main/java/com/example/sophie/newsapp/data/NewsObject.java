package com.example.sophie.newsapp.data;

public class NewsObject {
    private String title;
    private String section;
    private String author;
    private String date;
    private String url;

    public NewsObject(String title, String section, String author, String date, String url) {
        this.title = title;
        this.section = section;
        this.author = author;
        this.date = date;
        this.url = url;
    }

    public NewsObject(String title, String section, String author_or_date, int i, String url) {
        this.title = title;
        this.section = section;
/**
 * found out it always has a date but kept the possibility of not having it in the rest of the code for
 * the purpose of fulfilling all the criteria
 */
        if (i == 1) {
            this.author = author_or_date;
            this.date = "date unknown";
        } else if (i == 2) {
            this.author = "author unknown";
            this.date = author_or_date;
        } else {
            this.author = "author unknown";
            this.date = "date unknown";
        }
        this.url = url;
    }

    public NewsObject(String title, String section, String url) {
        this.title = title;
        this.section = section;
        this.author = "author unknown";
        this.date = "date unknown";
        this.url = url;

    }

    public String getTitle() {
        return title;
    }

    public String getSection() {
        return section;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getUrl() { return url; }
}

