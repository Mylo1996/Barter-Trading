package com.barter_trading_app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barter_trading_app.R;
import com.barter_trading_app.UserData;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<UserData> users;
    private OnUserClickListener mListener;

    public UserAdapter(Context _context, List<UserData> _users){
        this.mContext = _context;
        this.users = _users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserData user = users.get(position);
        holder.username.setText(user.firstName+" "+user.sureName);
        Picasso.with(mContext).load(user.profileImageUrl).placeholder(R.mipmap.ic_launcher).into(holder.usersProfileCircle);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView username;
        CircleImageView usersProfileCircle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            usersProfileCircle = itemView.findViewById(R.id.usersProfileCircle);

            itemView.setOnClickListener(this);
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
    }
    public interface OnUserClickListener{
        void onItemClick(int position);
    }

    public void setOnUserClickListener(OnUserClickListener listener){
        mListener = listener;
    }

}
