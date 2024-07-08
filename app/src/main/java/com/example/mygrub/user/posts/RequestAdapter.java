package com.example.mygrub.user.posts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygrub.R;
import com.example.mygrub.user.home.food.ListAdapter;
import com.example.mygrub.user.home.food.ListingItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    private Context context;
    private ArrayList<RequestItem> itemList;
    private OnItemClickListener onItemClickListener;

    public RequestAdapter(Context context, ArrayList<RequestItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public RequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_user_requests_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.ViewHolder holder, int position) {
        RequestItem item = itemList.get(position);

        Picasso.get().load(item.getImageUrl()).into(holder.image);
        holder.title.setText(itemList.get(position).getTitle());
        holder.requestTime.setText(itemList.get(position).getCreated_datetime());
        holder.itemView.setOnClickListener(view -> onItemClickListener.onClick(itemList.get(position)));

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(RequestItem item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, requestTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.listTitle);
            image = itemView.findViewById(R.id.listImage);
            requestTime = itemView.findViewById(R.id.requestTime);
        }

    }
}
