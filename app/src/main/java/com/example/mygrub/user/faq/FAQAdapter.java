package com.example.mygrub.user.faq;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygrub.R;
import com.example.mygrub.admin.faq.FAQAskItem;

import java.util.ArrayList;

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.ViewHolder> {

    private Context context;
    private ArrayList<FAQItem> itemList;
    private OnItemClickListener onItemClickListener;

    public FAQAdapter(Context context, ArrayList<FAQItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public FAQAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_faq_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FAQAdapter.ViewHolder holder, int position) {
        int count = position + 1;
        holder.questionDisplay.setText(itemList.get(position).getQuestion());
        holder.questionLabel.setText("Question " + count);
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
        void onClick(FAQItem item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView questionLabel, questionDisplay;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            questionLabel = itemView.findViewById(R.id.questionLabel);
            questionDisplay = itemView.findViewById(R.id.questionDisplay);
        }
    }
}
