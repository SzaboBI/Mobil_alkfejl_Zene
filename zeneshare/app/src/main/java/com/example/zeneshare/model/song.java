package com.example.zeneshare.model;

import java.io.File;

public class song {
    private String title;
    private String author;
    private String fileName;
    private Boolean downloaded;
    private String user;

    public song(){}

    public song(String title, String author, String fileName, String uploader){
        this.title = title;
        this.author = author;
        this.fileName = fileName;
        this.downloaded = false;
        this.user = uploader;
    }
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }
    public String getFileName(){
        return fileName;
    }
    public String getUser(){
        return user;
    }
    public Boolean getDownloaded(){
        return downloaded;
    }
    public void download(){
        this.downloaded = true;
    }
}
