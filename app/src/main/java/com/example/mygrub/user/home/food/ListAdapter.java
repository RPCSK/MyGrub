package com.example.mygrub.user.home.food;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygrub.R;
import com.example.mygrub.user.faq.FAQItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ListingItem> itemList;
    private OnItemClickListener onItemClickListener;

    public ListAdapter(Context context, ArrayList<ListingItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public void setFilteredList(ArrayList<ListingItem> filteredList){
        this.itemList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_list_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.ViewHolder holder, int position) {
        ListingItem item = itemList.get(position);
        item.setAllTags();


        Picasso.get().load(item.getImageUrl()).into(holder.image);
        holder.title.setText(itemList.get(position).getTitle());
        holder.username.setText(itemList.get(position).getUsername());
        holder.location.setText(itemList.get(position).getLocation());
        holder.chip1.setText(itemList.get(position).getHalalTag());
        holder.chip2.setText(itemList.get(position).getDietTag0());
        holder.chip3.setText(itemList.get(position).getTypeTag0());
        holder.itemView.setOnClickListener(view -> onItemClickListener.onClick(itemList.get(position)));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(ListingItem item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView title, username, location,chip1, chip2, chip3 ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.listImage);
            title = itemView.findViewById(R.id.listTitle);
            username = itemView.findViewById(R.id.listUsername);
            location = itemView.findViewById(R.id.listLocDetail);
            chip1 = itemView.findViewById(R.id.listChip1);
            chip2 = itemView.findViewById(R.id.listChip2);
            chip3 = itemView.findViewById(R.id.listChip3);
        }
    }
}
