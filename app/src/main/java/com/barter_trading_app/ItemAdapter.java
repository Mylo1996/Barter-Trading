package com.barter_trading_app;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ImageViewHolder> {

    private Context mContext;
    private List<UploadedItem> itemsList;
    private OnItemClickListener mListener;
    private FirebaseUser fuser;

    public ItemAdapter(Context context, List<UploadedItem> itemslist){
        mContext = context;
        itemsList = itemslist;
        fuser = FirebaseAuth.getInstance().getCurrentUser();
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
        Picasso.with(mContext).load(uploadedItem.itemImageUrl).placeholder(R.mipmap.ic_launcher).fit().centerCrop().into(holder.imageViewItem);
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView textViewItemName;
        public ImageView imageViewItem;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewItemName = itemView.findViewById(R.id.textViewItemName);
            imageViewItem = itemView.findViewById(R.id.imageViewItem);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mListener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            if(itemsList.get(getAdapterPosition()).itemUserId.equals(fuser.getUid())) {
                MenuItem agreeItem = menu.add(Menu.NONE, 1, 1, "Set Agreed");
                agreeItem.setOnMenuItemClickListener(this);
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            if(mListener != null){
                int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        switch (item.getItemId()) {
                            case 1:
                                mListener.onAgreedItemClick(position);
                                return true;
                        }
                    }
            }
            return false;
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
        void onAgreedItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
}
