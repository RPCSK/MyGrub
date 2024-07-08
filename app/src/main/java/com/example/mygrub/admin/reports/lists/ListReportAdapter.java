package com.example.mygrub.admin.reports.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygrub.R;

import java.util.ArrayList;

public class ListReportAdapter extends RecyclerView.Adapter<ListReportAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ListReportItem> itemList;
    private OnItemClickListener onItemClickListener;

    public ListReportAdapter(Context context, ArrayList<ListReportItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_report_listevent_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.typeLabel.setText(itemList.get(position).getType());
        holder.descDisplay.setText(itemList.get(position).getDesc());
        holder.reportTimeDisplay.setText(itemList.get(position).getCreated_datetime());
        holder.itemView.setOnClickListener(view -> onItemClickListener.onClick(itemList.get(position)));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public interface OnItemClickListener {
        void onClick(ListReportItem item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView typeLabel, descDisplay, reportTimeDisplay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            typeLabel = itemView.findViewById(R.id.typeLabel);
            descDisplay = itemView.findViewById(R.id.descDisplay);
            reportTimeDisplay = itemView.findViewById(R.id.reportTimeDisplay);
        }
    }
}
