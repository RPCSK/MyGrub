package com.example.mygrub.user.posts;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygrub.R;
import com.example.mygrub.user.home.food.ListAdapter;
import com.example.mygrub.user.home.food.ListingItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentRequests#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentRequests extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //-----VARIABLES-----
    private RecyclerView requestsView;
    private ArrayList<RequestItem> itemList;
    private RequestAdapter adapter;
    private TextView emptyMsg;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference()
            .child("User Profiles").child(user.getUid()).child("Requests");

    public FragmentRequests() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentRequests.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentRequests newInstance(String param1, String param2) {
        FragmentRequests fragment = new FragmentRequests();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_requests, container, false);

        requestsView = view.findViewById(R.id.requestsView);
        emptyMsg = view.findViewById(R.id.emptyLabel);
        requestsView.setLayoutManager(new LinearLayoutManager(getContext()));

        itemList = new ArrayList<>();

        requestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();

                for (DataSnapshot shot : snapshot.getChildren()){
                    RequestItem item = shot.getValue(RequestItem.class);
                    if (!itemList.contains(item)) {
                        Objects.requireNonNull(item).setKey(shot.getKey());
                        itemList.add(item);
                    } else {
                        int index = itemList.indexOf(item);
                        itemList.set(index, item);
                    }
                }

                if (itemList.isEmpty()){
                    requestsView.setVisibility(View.GONE);
                    emptyMsg.setVisibility(View.VISIBLE);
                } else {
                    requestsView.setVisibility(View.VISIBLE);
                    emptyMsg.setVisibility(View.GONE);
                }

                adapter = new RequestAdapter(getContext(), itemList);
                adapter.notifyDataSetChanged();
                requestsView.setAdapter(adapter);
                setClick(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void setClick(RequestAdapter adapter) {
        adapter.setOnItemClickListener(new RequestAdapter.OnItemClickListener() {
            @Override
            public void onClick(RequestItem item) {
                Intent intent = new Intent(getActivity(), RequestDetail.class);
                intent.putExtra("requesterID", item.getRequesterID());
                intent.putExtra("requestID", item.getKey());
                intent.putExtra("listID", item.getListID());
                intent.putExtra("imageUrl", item.getImageUrl());
                intent.putExtra("title", item.getTitle());
                intent.putExtra("location", item.getLocation());
                intent.putExtra("halalTag", item.getHalalTag());
                intent.putExtra("dietTag", item.getDietTag());
                intent.putExtra("typeTag", item.getTypeTag());
                intent.putExtra("contact", item.getContact());
                intent.putExtra("desc", item.getDesc());
                intent.putExtra("created_datetime", item.getCreated_datetime());
                intent.putExtra("status", item.getStatus());

                startActivity(intent);


            }
        });
    }

    private void makeToast(String msg){
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}