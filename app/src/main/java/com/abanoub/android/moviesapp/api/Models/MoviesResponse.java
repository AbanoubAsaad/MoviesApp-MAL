package com.abanoub.android.moviesapp.api.Models;


import java.io.Serializable;
import java.util.ArrayList;

public class MoviesResponse implements Serializable{

    private ArrayList<Movie> results;

    public MoviesResponse(ArrayList<Movie> results) {
        this.results = results;
    }

    public ArrayList<Movie> getResults() {
        return results;
    }

    public void setResults(ArrayList<Movie> results) {
        this.results = results;
    }
}
