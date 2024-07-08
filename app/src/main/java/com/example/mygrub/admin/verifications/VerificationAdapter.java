package com.example.mygrub.admin.verifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygrub.R;
import com.example.mygrub.user.profile.DependantItem;

import java.util.ArrayList;

public class VerificationAdapter extends RecyclerView.Adapter<VerificationAdapter.ViewHolder> {
    private Context context;
    private ArrayList<VerificationItem> itemList;
    private OnItemClickListener onItemClickListener;

    public VerificationAdapter(Context context, ArrayList<VerificationItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public VerificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_verify_view, parent, false);
        return new VerificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VerificationItem item = itemList.get(position);

        holder.emailLabel.setText(itemList.get(position).getEmail());
        holder.requestTimeDisplay.setText(itemList.get(position).getCreated_datetime());
        holder.approveBtn.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onApproveBtnClick(item);
            }
        });

        holder.rejectBtn.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onRejectBtnClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onApproveBtnClick(VerificationItem item);
        void onRejectBtnClick(VerificationItem item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView emailLabel, requestTimeDisplay;
        Button approveBtn, rejectBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            emailLabel = itemView.findViewById(R.id.emailLabel);
            requestTimeDisplay = itemView.findViewById(R.id.requestTimeDisplay);
            approveBtn = itemView.findViewById(R.id.approveBtn);
            rejectBtn = itemView.findViewById(R.id.rejectBtn);
        }
    }
}
