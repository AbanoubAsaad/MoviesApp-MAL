package com.abanoub.android.moviesapp.activites;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.abanoub.android.moviesapp.api.Models.Movie;
import com.abanoub.android.moviesapp.api.Models.MoviesResponse;
import com.abanoub.android.moviesapp.AppController;
import com.abanoub.android.moviesapp.adapters.GridRecyclerViewAdapter;
import com.abanoub.android.moviesapp.fragments.MovieDetailsFragment;
import com.abanoub.android.moviesapp.R;
import com.abanoub.android.moviesapp.utils.LocalPreferences;
import com.abanoub.android.moviesapp.database.MoviesProvider;
import com.abanoub.android.moviesapp.database.MoviesTables;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesListActivity extends AppCompatActivity implements GridRecyclerViewAdapter.OnItemClickListener, MovieDetailsFragment.OnFavoriteChangedListener {

    public static final String POPULAR_MOVIES = "popular";
    public static final String TOP_RATED_MOVIES = "top_rated";
    public static final String FAVORITE_MOVIES = "favorite";
    public static final String MOVIE_KEY = "MOVIE_KEY";

    public static final int MOVIES_LOADER_ID = 0;
    public static final String MOVIES_RESPONSE_KEY = "MOVIES_RESPONSE";
    public static final String MOVIES_TYPE_KEY = "MOVIES_TYPE";

    private String mCurrentMoviesType = "";

    private boolean mTwoPane;
    private RecyclerView mRecyclerView;
    private GridRecyclerViewAdapter mAdapter;
    private ProgressDialog mProgressDialog;
    private TextView mNoFavorite;
    private LocalPreferences mLocalPreferences;
    private MoviesResponse mMoviesResponse;
    private MenuItem mPopularMenuItem;
    private MenuItem mTopRatedMenuItem;
    private MenuItem mFavoriteMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        mLocalPreferences = new LocalPreferences(this);
        setupRecyclerView();

        mNoFavorite = (TextView) findViewById(R.id.no_favorite_tv);

        if (savedInstanceState == null) {
            loadData();
        } else {
            mMoviesResponse = (MoviesResponse) savedInstanceState.getSerializable(MOVIES_RESPONSE_KEY);
            mCurrentMoviesType = savedInstanceState.getString(MOVIES_TYPE_KEY);
            if (mMoviesResponse == null)
                loadData();
            else {
                mAdapter.changeList(mMoviesResponse.getResults());
            }

            setTitle();
        }

        if (findViewById(R.id.item_detail_container) != null)
            mTwoPane = true;

        getLoaderManager().initLoader(MOVIES_LOADER_ID, null, moviesLoader);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();

        MovieDetailsFragment movieDetailsFragment = (MovieDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.item_detail_container);
        if (movieDetailsFragment != null)
            movieDetailsFragment.setOnFavoriteChangedListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();

        if (mMoviesResponse != null) {
            outState.putSerializable(MOVIES_RESPONSE_KEY, mMoviesResponse);
            outState.putString(MOVIES_TYPE_KEY, mCurrentMoviesType);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        mTopRatedMenuItem = menu.findItem(R.id.top_rated);
        mPopularMenuItem = menu.findItem(R.id.popular);
        mFavoriteMenuItem = menu.findItem(R.id.favorite);
        setMenuItems();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.popular:
                mLocalPreferences.setMoviesType(POPULAR_MOVIES);
                break;
            case R.id.top_rated:
                mLocalPreferences.setMoviesType(TOP_RATED_MOVIES);
                break;
            case R.id.favorite:
                mLocalPreferences.setMoviesType(FAVORITE_MOVIES);
                break;
        }
        setMenuItems();
        loadData();

        return super.onOptionsItemSelected(item);
    }


    private void setupRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_gridView);
        assert mRecyclerView != null;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (getResources().getBoolean(R.bool.isTablet))
                mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            else
                mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        } else {
            if (getResources().getBoolean(R.bool.isTablet))
                mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            else
                mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        mAdapter = new GridRecyclerViewAdapter(this, null);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnFavoriteClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void loadData() {
        setTitle();
        mCurrentMoviesType = mLocalPreferences.getMoviesType();
        if (mCurrentMoviesType.equalsIgnoreCase(FAVORITE_MOVIES)) {
            getLoaderManager().restartLoader(MOVIES_LOADER_ID, null, moviesLoader);
        } else {
            mNoFavorite.setVisibility(View.GONE);
            mProgressDialog = ProgressDialog.show(this, getString(R.string.loading), getString(R.string.loading));
            Call<MoviesResponse> call = ((AppController) getApplication()).getApiService().getMovies(mCurrentMoviesType);
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    mMoviesResponse = response.body();
                    mAdapter.changeList(mMoviesResponse.getResults());
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Toast.makeText(MoviesListActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                }
            });
        }
        if (mTwoPane && getSupportFragmentManager().findFragmentById(R.id.item_detail_container) != null)
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.item_detail_container)).commit();

    }

    @Override
    public void onItemClick(View view, Movie movie) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putSerializable(MOVIE_KEY, movie);
            MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();
            movieDetailsFragment.setOnFavoriteChangedListener(this);
            movieDetailsFragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, movieDetailsFragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailsActivity.class);
            intent.putExtra(MOVIE_KEY, movie);

            startActivity(intent);
        }
    }

    @Override
    public void onFavoriteClick() {
        MovieDetailsFragment movieDetailsFragment = (MovieDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.item_detail_container);
        if (movieDetailsFragment != null)
            movieDetailsFragment.updateFavorite();
    }

    private void setTitle() {
        String type = mLocalPreferences.getMoviesType();
        if (type.equalsIgnoreCase(POPULAR_MOVIES)) {
            setTitle(R.string.popular_title);
        } else if (type.equalsIgnoreCase(TOP_RATED_MOVIES)) {
            setTitle(R.string.top_title);
        } else {
            setTitle(R.string.favorite_title);
        }
    }

    private void setMenuItems() {
        String type = mLocalPreferences.getMoviesType();

        switch (type) {
            case POPULAR_MOVIES:
                mPopularMenuItem.setVisible(false);
                mTopRatedMenuItem.setVisible(true);
                mFavoriteMenuItem.setVisible(true);
                break;
            case TOP_RATED_MOVIES:
                mPopularMenuItem.setVisible(true);
                mTopRatedMenuItem.setVisible(false);
                mFavoriteMenuItem.setVisible(true);
                break;
            case FAVORITE_MOVIES:
                mPopularMenuItem.setVisible(true);
                mTopRatedMenuItem.setVisible(true);
                mFavoriteMenuItem.setVisible(false);
                break;
        }
    }

    private LoaderManager.LoaderCallbacks<Cursor> moviesLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(MoviesListActivity.this, MoviesProvider.MOVIES_URI, null, null, null, MoviesTables.MOVIE_ID + " ASC");
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

            if (!mCurrentMoviesType.equalsIgnoreCase(FAVORITE_MOVIES))
                return;

            ArrayList<Movie> movies = new ArrayList<>();
            if (cursor.moveToFirst()) {
                mNoFavorite.setVisibility(View.GONE);
                do {
                    Movie movie = new Movie(cursor.getString(cursor.getColumnIndex(MoviesTables.MOVIE_POSTER_PATH)),
                            cursor.getString(cursor.getColumnIndex(MoviesTables.MOVIE_OVERVIEW)),
                            cursor.getString(cursor.getColumnIndex(MoviesTables.MOVIE_RELEASE_DATE)),
                            cursor.getLong(cursor.getColumnIndex(MoviesTables.MOVIE_ID)),
                            cursor.getString(cursor.getColumnIndex(MoviesTables.MOVIE_TITLE)),
                            cursor.getDouble(cursor.getColumnIndex(MoviesTables.MOVIE_VOTE_AVERAGE)));
                    movies.add(movie);
                } while ((cursor.moveToNext()));
            } else {
                mNoFavorite.setVisibility(View.VISIBLE);
            }
            mAdapter.changeList(movies);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    };

    @Override
    public void onFavoriteChanged() {
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }
}
