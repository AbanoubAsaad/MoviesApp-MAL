package com.abanoub.android.moviesapp.api.Models;

public class Video {

    private String id;
    private String key;
    private String name;


    public Video(String id, String key, String name) {
        this.id = id;
        this.key = key;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
