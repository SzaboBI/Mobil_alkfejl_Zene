package com.example.zeneshare.model;

import java.io.File;

public class song {
    private String title;
    private double size;
    private String author;
    public song(String title, double size, String author){
        this.title=title;
        this.size=size;
        this.author= author;
    }

    public String getTitle() {
        return title;
    }

    public double getSize() {
        return size;
    }
    public String getSizeAsString(){
        return Double.toString(size);
    }

    public String getAuthor() {
        return author;
    }
}
