package com.abanoub.android.moviesapp;

import android.app.Application;

import com.abanoub.android.moviesapp.api.RetrofitInterface;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppController extends Application {

    private RetrofitInterface mApiService;

    @Override
    public void onCreate() {
        super.onCreate();

        initRetrofit();

    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitInterface.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mApiService = retrofit.create(RetrofitInterface.class);
    }

    public RetrofitInterface getApiService() {
        return mApiService;
    }
}
