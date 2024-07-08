package com.example.mygrub.user.home.welfare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygrub.R;
import com.example.mygrub.user.home.food.ListingItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AidAdapter extends RecyclerView.Adapter<AidAdapter.ViewHolder> {

    private Context context;
    private ArrayList<AidItem> itemList;
    private OnItemClickListener listener;

    public AidAdapter(Context context, ArrayList<AidItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public void setFilteredList(ArrayList<AidItem> filteredList){
        this.itemList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AidAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_aid_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AidAdapter.ViewHolder holder, int position) {
        AidItem item = itemList.get(position);
        item.setAidDetails();

        if (item.getImageUrl() != null)
            Picasso.get().load(item.getImageUrl()).into(holder.image);
        holder.aidTitle.setText(itemList.get(position).getKey());

        holder.aidTitle.setOnClickListener(view -> listener.onClick(itemList.get(position)));
        holder.moreInfo.setOnClickListener(view -> listener.onClick(itemList.get(position)));
        holder.itemView.setOnClickListener(view -> listener.onClick(itemList.get(position)));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public interface OnItemClickListener {
        void onClick(AidItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView aidTitle, moreInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.aidImage);
            aidTitle = itemView.findViewById(R.id.aidTitle);
            moreInfo = itemView.findViewById(R.id.moreInfo);
        }
    }
}
