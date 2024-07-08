package com.example.mygrub.admin.suspensions.all;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygrub.R;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private ArrayList<UserItem> itemList;
    private OnItemClickListener onItemClickListener;

    public UserAdapter(Context context, ArrayList<UserItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_user_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.emailLabel.setText(itemList.get(position).getEmail());
        holder.meritsDisplay.setText(String.valueOf(itemList.get(position).getMerits()));
        holder.demeritsDisplay.setText(String.valueOf(itemList.get(position).getDemerits()));
        holder.itemView.setOnClickListener(view -> onItemClickListener.onClick(itemList.get(position)));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public interface OnItemClickListener {
        void onClick(UserItem item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView emailLabel, meritsDisplay, demeritsDisplay;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            emailLabel = itemView.findViewById(R.id.emailLabel);
            meritsDisplay = itemView.findViewById(R.id.meritsDisplay);
            demeritsDisplay = itemView.findViewById(R.id.demeritsDisplay);
        }
    }
}
