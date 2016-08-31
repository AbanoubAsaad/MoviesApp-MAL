package com.abanoub.android.moviesapp.api;

import com.abanoub.android.moviesapp.api.Models.MoviesResponse;
import com.abanoub.android.moviesapp.api.Models.ReviewsResponse;
import com.abanoub.android.moviesapp.api.Models.VideosResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RetrofitInterface {

    public static String API_KEY = "cee0351719165fbec89df420b7145c76";
    public static String BASE_URL = "https://api.themoviedb.org/3/movie/";
    public static String MOVIES_REQUEST = "{type}?api_key=" + API_KEY;
    public static String REVIEWS_REQUEST = "{id}/reviews?api_key=" + API_KEY;
    public static String TRAILERS_REQUEST = "{id}/videos?api_key=" + API_KEY;


    @GET(MOVIES_REQUEST)
    Call<MoviesResponse> getMovies(@Path("type") String type);

    @GET(REVIEWS_REQUEST)
    Call<ReviewsResponse> getReviews(@Path("id") String id);

    @GET(TRAILERS_REQUEST)
    Call<VideosResponse> getTrailers(@Path("id") String id);

}
