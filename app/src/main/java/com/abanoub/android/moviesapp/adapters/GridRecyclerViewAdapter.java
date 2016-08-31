package com.abanoub.android.moviesapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.abanoub.android.moviesapp.api.Models.Movie;
import com.abanoub.android.moviesapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GridRecyclerViewAdapter extends RecyclerView.Adapter<GridRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Movie> mMovies;
    private OnItemClickListener onItemClickListener;
    private OnItemClickListener onFavoriteClickListener;
    private Context mContext;

    public GridRecyclerViewAdapter(Context context, ArrayList<Movie> movies) {
        this.mMovies = movies;
        mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnFavoriteClickListener(OnItemClickListener onFavoriteClickListener) {
        this.onFavoriteClickListener = onFavoriteClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Movie movie = mMovies.get(position);
        holder.image.setImageBitmap(null);
        Picasso.with(holder.image.getContext()).load(movie.getPoster_path()).into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(view, movie);
            }
        });

        if (movie.isFavorite(mContext))
            holder.favorite.setImageResource(R.drawable.ic_fav_active);
        else
            holder.favorite.setImageResource(R.drawable.ic_fav_inactive);

        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (movie.isFavorite(mContext)) {
                    movie.removeFromFavorite(mContext);
                    holder.favorite.setImageResource(R.drawable.ic_fav_inactive);
                } else {
                    movie.addToFavorite(mContext);
                    holder.favorite.setImageResource(R.drawable.ic_fav_active);
                }
                onFavoriteClickListener.onFavoriteClick();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mMovies == null)
            return 0;

        return mMovies.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public ImageView favorite;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            favorite = (ImageView) itemView.findViewById(R.id.favorite);
        }
    }

    public void changeList(ArrayList<Movie> movies) {
        this.mMovies = movies;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Movie movie);
        void onFavoriteClick();
    }
}
