package com.barter_trading_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ImageViewHolder> {

    private Context mContext;
    private List<UploadedItem> itemsList;

    public ItemAdapter(Context context, List<UploadedItem> itemslist){
        mContext = context;
        itemsList = itemslist;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_layout,parent,false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        UploadedItem uploadedItem = itemsList.get(position);
        holder.textViewItemName.setText(uploadedItem.itemName);
        Picasso.with(mContext).load(uploadedItem.itemImageUrl).fit().centerCrop().into(holder.imageViewItem);
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewItemName;
        public ImageView imageViewItem;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewItemName = itemView.findViewById(R.id.textViewItemName);
            imageViewItem = itemView.findViewById(R.id.imageViewItem);
        }
    }
}
