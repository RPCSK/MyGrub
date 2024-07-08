package com.example.mygrub.admin.faq;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygrub.R;

import java.util.ArrayList;

public class FAQAskAdapter extends RecyclerView.Adapter<FAQAskAdapter.ViewHolder> {
    private Context context;
    private ArrayList<FAQAskItem> itemList;
    private OnItemClickListener onItemClickListener;

    public FAQAskAdapter(Context context, ArrayList<FAQAskItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public FAQAskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_faq_question, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FAQAskAdapter.ViewHolder holder, int position) {
        holder.question.setText(itemList.get(position).getQuestion());
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
        void onClick(FAQAskItem item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView question;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            question = itemView.findViewById(R.id.questionDisplay);
        }
    }
}
