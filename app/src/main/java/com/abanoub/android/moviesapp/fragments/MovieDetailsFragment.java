package com.abanoub.android.moviesapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abanoub.android.moviesapp.api.Models.Movie;
import com.abanoub.android.moviesapp.api.Models.Review;
import com.abanoub.android.moviesapp.api.Models.ReviewsResponse;
import com.abanoub.android.moviesapp.api.Models.Video;
import com.abanoub.android.moviesapp.api.Models.VideosResponse;
import com.abanoub.android.moviesapp.activites.MoviesListActivity;
import com.abanoub.android.moviesapp.AppController;
import com.abanoub.android.moviesapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class MovieDetailsFragment extends Fragment {

    private LayoutInflater mLayoutInflater;
    private ViewGroup mContainerViewGroup;

    private OnFavoriteChangedListener onFavoriteChangedListener;

    private Movie mCurrentMovie;
    private LinearLayout mReviewsLayout;
    private LinearLayout mTrailersLayout;
    private ImageView mFavoriteIcon;

    public MovieDetailsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(MoviesListActivity.MOVIE_KEY)) {
            mCurrentMovie = (Movie) getArguments().getSerializable(MoviesListActivity.MOVIE_KEY);
            getActivity().setTitle(mCurrentMovie.getTitle());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);

        mLayoutInflater = inflater;
        mContainerViewGroup = container;

        mReviewsLayout = (LinearLayout) rootView.findViewById(R.id.reviews_layout);
        mTrailersLayout = (LinearLayout) rootView.findViewById(R.id.trailers_layout);
        mFavoriteIcon = (ImageView) rootView.findViewById(R.id.favorite_icon);

        final ImageView image = (ImageView) rootView.findViewById(R.id.image);
        Picasso.with(getActivity()).load(mCurrentMovie.getPoster_path()).into(image, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

            }
        });

        ((TextView) rootView.findViewById(R.id.description)).setText(mCurrentMovie.getOverview());
        ((TextView) rootView.findViewById(R.id.user_rating)).setText(mCurrentMovie.getVote_average() + "");
        ((TextView) rootView.findViewById(R.id.release_date)).setText(mCurrentMovie.getRelease_date());

        updateFavorite();

        mFavoriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentMovie.isFavorite(getActivity())) {
                    mCurrentMovie.removeFromFavorite(getContext());
                    mFavoriteIcon.setImageResource(R.drawable.ic_fav_inactive);
                } else {
                    mCurrentMovie.addToFavorite(getContext());
                    mFavoriteIcon.setImageResource(R.drawable.ic_fav_active);
                }
                if (onFavoriteChangedListener != null)
                    onFavoriteChangedListener.onFavoriteChanged();
            }
        });


        loadReviews(mCurrentMovie.getId() + "");
        loadTrailers(mCurrentMovie.getId() + "");

        return rootView;
    }

    public void setOnFavoriteChangedListener(OnFavoriteChangedListener onFavoriteChangedListener) {
        this.onFavoriteChangedListener = onFavoriteChangedListener;
    }

    private void loadReviews(String id) {
        Call<ReviewsResponse> call = ((AppController) getActivity().getApplication()).getApiService().getReviews(id);
        call.enqueue(new retrofit2.Callback<ReviewsResponse>() {
            @Override
            public void onResponse(Call<ReviewsResponse> call, Response<ReviewsResponse> response) {
                if (response.code() == 200) {
                    ReviewsResponse res = response.body();
                    ArrayList<Review> reviews = res.getResults();
                    if (reviews != null && reviews.size() != 0) {
                        for (int i = 0; i < reviews.size(); i++) {
                            Review review = reviews.get(i);
                            View reviewView = mLayoutInflater.inflate(R.layout.item_review, mContainerViewGroup, false);
                            ((TextView) reviewView.findViewById(R.id.user_name)).setText(review.getAuthor());
                            ((TextView) reviewView.findViewById(R.id.user_review)).setText(review.getContent());
                            mReviewsLayout.addView(reviewView);
                        }
                    } else {
                        mReviewsLayout.findViewById(R.id.no_reviews).setVisibility(View.VISIBLE);
                    }
                } else {
                    mReviewsLayout.findViewById(R.id.no_reviews).setVisibility(View.VISIBLE);
                }
                mReviewsLayout.findViewById(R.id.reviews_progress).setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ReviewsResponse> call, Throwable t) {
                Log.d("onFailure", t.toString());
                mReviewsLayout.findViewById(R.id.reviews_progress).setVisibility(View.GONE);
                mReviewsLayout.findViewById(R.id.no_reviews).setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadTrailers(String id) {
        Call<VideosResponse> call = ((AppController) getActivity().getApplication()).getApiService().getTrailers(id);
        call.enqueue(new retrofit2.Callback<VideosResponse>() {
            @Override
            public void onResponse(Call<VideosResponse> call, Response<VideosResponse> response) {
                if (response.code() == 200) {
                    VideosResponse res = response.body();
                    ArrayList<Video> reviews = res.getResults();
                    if (reviews != null && reviews.size() != 0) {
                        for (int i = 0; i < reviews.size(); i++) {
                            final Video video = reviews.get(i);
                            View reviewView = mLayoutInflater.inflate(R.layout.item_trailer, mContainerViewGroup, false);
                            ((TextView) reviewView.findViewById(R.id.trailer_name)).setText(video.getName());
                            reviewView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String url = "http://www.youtube.com/watch?v=" + video.getKey();
                                    getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                                }
                            });
                            mTrailersLayout.addView(reviewView);
                        }
                    } else {
                        mTrailersLayout.findViewById(R.id.no_trailers).setVisibility(View.VISIBLE);
                    }
                } else {
                    mTrailersLayout.findViewById(R.id.no_trailers).setVisibility(View.VISIBLE);
                }
                mTrailersLayout.findViewById(R.id.trailers_progress).setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<VideosResponse> call, Throwable t) {
                Log.d("onFailure", t.toString());
                mTrailersLayout.findViewById(R.id.trailers_progress).setVisibility(View.GONE);
                mTrailersLayout.findViewById(R.id.no_trailers).setVisibility(View.VISIBLE);
            }
        });
    }

    public void updateFavorite() {
        if (mCurrentMovie.isFavorite(getContext()))
            mFavoriteIcon.setImageResource(R.drawable.ic_fav_active);
        else
            mFavoriteIcon.setImageResource(R.drawable.ic_fav_inactive);
    }

    public interface OnFavoriteChangedListener {
        void onFavoriteChanged();
    }
}
