package com.abanoub.android.moviesapp.api.Models;


import java.util.ArrayList;

public class VideosResponse {

    private ArrayList<Video> results;

    public VideosResponse(ArrayList<Video> results) {
        this.results = results;
    }

    public ArrayList<Video> getResults() {
        return results;
    }

    public void setResults(ArrayList<Video> results) {
        this.results = results;
    }
}
