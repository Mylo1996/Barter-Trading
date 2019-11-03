package com.barter_trading_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Review> reviewList;

    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.mContext = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.review_layout, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.textViewUserName.setText(review.userName);
        holder.textViewReview.setText(review.reviewMessage);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewUserName;
        public TextView textViewReview;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);
            textViewReview = itemView.findViewById(R.id.textViewReview);
        }

    }



}

