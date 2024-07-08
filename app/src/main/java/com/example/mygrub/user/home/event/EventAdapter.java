package com.example.mygrub.user.home.event;

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


public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private Context context;
    private ArrayList<EventItem> itemList;

    private OnItemClickListener onItemClickListener;

    public EventAdapter(Context context, ArrayList<EventItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_event_view, parent, false);
        return new EventAdapter.ViewHolder(view);
    }

    public void setFilteredList(ArrayList<EventItem> filteredList){
        this.itemList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {
        EventItem item = itemList.get(position);

        Picasso.get().load(item.getImageUrl()).into(holder.image);
        holder.title.setText(itemList.get(position).getTitle());
        holder.dates.setText(String.format("%s - %s", itemList.get(position).getStartDate(),
                itemList.get(position).getEndDate()));
        holder.type.setText(itemList.get(position).getType());
        holder.location.setText(itemList.get(position).getLocation());
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
        void onClick(EventItem item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView title, location, dates, type;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.eventImage);
            title = itemView.findViewById(R.id.eventTitle);
            location = itemView.findViewById(R.id.eventLocation);
            dates = itemView.findViewById(R.id.eventDates);
            type = itemView.findViewById(R.id.eventType);

        }
    }
}
