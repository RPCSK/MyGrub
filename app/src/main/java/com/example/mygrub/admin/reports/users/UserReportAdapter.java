package com.example.mygrub.admin.reports.users;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygrub.R;

import java.util.ArrayList;

public class UserReportAdapter extends RecyclerView.Adapter<UserReportAdapter.ViewHolder> {

    private Context context;
    private ArrayList<UserReportItem> itemList;
    private OnItemClickListener onItemClickListener;



    public UserReportAdapter(Context context, ArrayList<UserReportItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public UserReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_report_listevent_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserReportAdapter.ViewHolder holder, int position) {
        holder.typeLabel.setText(itemList.get(position).getType());
        holder.descDisplay.setText(itemList.get(position).getDesc());
        holder.reportTimeDisplay.setText(itemList.get(position).getCreated_datetime());
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
        void onClick(UserReportItem item);
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
