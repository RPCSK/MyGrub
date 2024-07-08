package com.example.mygrub.user.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygrub.R;

import java.util.ArrayList;

public class DependantAdapter extends RecyclerView.Adapter<DependantAdapter.ViewHolder> {

    private Context context;
    private ArrayList<DependantItem> itemList;
    private OnItemClickListener listener;

    public DependantAdapter(Context context, ArrayList<DependantItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public DependantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_dependants_view, parent, false);
        return new DependantAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DependantAdapter.ViewHolder holder, int position) {
        DependantItem item = itemList.get(position);

        holder.firstnameET.setText(itemList.get(position).getFirstName());
        holder.lastNameET.setText(itemList.get(position).getLastName());
        holder.birthdateET.setText(itemList.get(position).getBirthdate());
        holder.relationET.setText(itemList.get(position).getRelation());
        holder.employmentET.setText(itemList.get(position).getEmploymentStatus());
        holder.editBtn.setOnClickListener(view -> {
            if (listener != null) {
                listener.onEditButtonClick(item);
            }
        });

        holder.deleteBtn.setOnClickListener(view -> {
            if (listener != null) {
                listener.onDeleteButtonClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private EditText firstnameET, lastNameET, birthdateET, employmentET, relationET;
        private Button editBtn, deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            firstnameET = itemView.findViewById(R.id.firstNameET);
            lastNameET = itemView.findViewById(R.id.lastNameET);
            birthdateET = itemView.findViewById(R.id.birthdateET);
            relationET = itemView.findViewById(R.id.relationET);
            employmentET = itemView.findViewById(R.id.employmentET);
            editBtn = itemView.findViewById(R.id.editBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);

        }

    }
    public interface OnItemClickListener {
        void onEditButtonClick(DependantItem item);
        void onDeleteButtonClick(DependantItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
