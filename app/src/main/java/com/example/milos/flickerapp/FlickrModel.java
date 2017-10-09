package com.example.milos.flickerapp;

/**
 * Created by Milos on 31-Jul-17.
 */

public class FlickrModel {

    private String title;
    private String link;
    private String media;
    private String date_taken;
    private String author;
    private String tags;
    private String localPath;

    FlickrModel(String media, String author, String tags, String date_taken, String title, String localPath) {
        this.media = media;
        this.author = author;
        this.tags = tags;
        this.date_taken = date_taken;
        this.title = title;
        this.localPath = localPath;
    }

    FlickrModel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    String getLink() {
        return link;
    }

    void setLink(String link) {
        this.link = link;
    }

    String getMedia() {
        return media;
    }

    void setMedia(String media) {
        this.media = media;
    }

    String getDate_taken() {
        return date_taken;
    }

    void setDate_taken(String date_taken) {
        this.date_taken = date_taken;
    }

    String getAuthor() {
        return author;
    }

    void setAuthor(String author) {
        this.author = author;
    }

    String getTags() {
        return tags;
    }

    void setTags(String tags) {
        this.tags = tags;
    }

    String getLocalPath() {
        return localPath;
    }

    void setLocalPath(String localPath) {
        this.localPath = localPath;
    }
}
