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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OrgAdapter extends RecyclerView.Adapter<OrgAdapter.ViewHolder> {

    private Context context;
    private ArrayList<OrgItem> itemList;
    private OnItemClickListener listener;

    public OrgAdapter(Context context, ArrayList<OrgItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public OrgAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_org_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrgAdapter.ViewHolder holder, int position) {
        OrgItem item = itemList.get(position);
        item.setOrgDetails();

        if (item.getImageUrl() != null)
            Picasso.get().load(item.getImageUrl()).into(holder.image);
        holder.orgTitle.setText(itemList.get(position).getKey());
        holder.address.setText(itemList.get(position).getAddress());
        holder.contact.setText(itemList.get(position).getContact());
        holder.link.setText(itemList.get(position).getLink());

        holder.orgTitle.setOnClickListener(view -> {
            if (listener != null) {
                listener.onTitleClick(item);
            }
        });
        holder.address.setOnClickListener(view -> {
            if (listener != null) {
                listener.onAddressClick(item);
            }
        });
        holder.contact.setOnClickListener(view -> {
            if (listener != null) {
                listener.onContactClick(item);
            }
        });
        holder.link.setOnClickListener(view -> {
            if (listener != null) {
                listener.onLinkClick(item);
            }
        });



    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public interface OnItemClickListener {
        void onTitleClick(OrgItem item);
        void onAddressClick(OrgItem item);
        void onContactClick(OrgItem item);
        void onLinkClick(OrgItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView orgTitle, address, contact, link;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            orgTitle = itemView.findViewById(R.id.orgTitle);
            address = itemView.findViewById(R.id.locationDetail);
            contact = itemView.findViewById(R.id.contactDetail);
            link = itemView.findViewById(R.id.webDetail);
            image = itemView.findViewById(R.id.orgImage);
        }
    }
}
