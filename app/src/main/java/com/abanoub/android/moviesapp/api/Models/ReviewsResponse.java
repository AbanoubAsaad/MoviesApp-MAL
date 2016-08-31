package com.abanoub.android.moviesapp.api.Models;


import java.util.ArrayList;

public class ReviewsResponse {

    private ArrayList<Review> results;

    public ReviewsResponse(ArrayList<Review> results) {
        this.results = results;
    }

    public ArrayList<Review> getResults() {
        return results;
    }

    public void setResults(ArrayList<Review> results) {
        this.results = results;
    }
}
