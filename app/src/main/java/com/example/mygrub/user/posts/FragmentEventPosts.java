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

import com.example.mygrub.R;
import com.example.mygrub.user.home.event.EventAdapter;
import com.example.mygrub.user.home.event.EventItem;
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
import java.util.Collections;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentEventPosts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentEventPosts extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //-----VARIABLES-------
    private RecyclerView eventView;
    private ArrayList<EventItem> itemList;
    private EventAdapter adapter;
    private TextView emptyMsg;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("Events");

    public FragmentEventPosts() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentEventPosts.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentEventPosts newInstance(String param1, String param2) {
        FragmentEventPosts fragment = new FragmentEventPosts();
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

        View view = inflater.inflate(R.layout.fragment_user_events, container, false);

        eventView = view.findViewById(R.id.eventView);
        emptyMsg = view.findViewById(R.id.emptyLabel);
        eventView.setLayoutManager(new LinearLayoutManager(getContext()));

        itemList = new ArrayList<>();

        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                itemList.clear();

                for (DataSnapshot shot : snapshot.getChildren()){
                    if (shot.child("userID").getValue().toString().equals(user.getUid())){
                        EventItem item = shot.getValue(EventItem.class);
                        if (!itemList.contains(item)) {
                            Objects.requireNonNull(item).setKey(shot.getKey());
                            itemList.add(item);
                        } else {
                            int index = itemList.indexOf(item);
                            itemList.set(index, item);
                        }
                    }
                }
                if (itemList.isEmpty()){
                    eventView.setVisibility(View.GONE);
                    emptyMsg.setVisibility(View.VISIBLE);
                } else {
                    eventView.setVisibility(View.VISIBLE);
                    emptyMsg.setVisibility(View.GONE);
                }

                Collections.reverse(itemList);
                adapter = new EventAdapter(getContext(), itemList);
                adapter.notifyDataSetChanged();
                eventView.setAdapter(adapter);
                setClick(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void setClick(EventAdapter adapter) {
        adapter.setOnItemClickListener(new EventAdapter.OnItemClickListener() {
            @Override
            public void onClick(EventItem item) {
                Intent intent = new Intent(getActivity(), EventEditForm .class);
                intent.putExtra("eventID", item.getKey());
                intent.putExtra("type", item.getType());
                intent.putExtra("imageUrl", item.getImageUrl());
                if (item.getQrEnabled().equals("1")) intent.putExtra("qrUrl", item.getQrUrl());
                intent.putExtra("qrEnabled", item.getQrEnabled());
                startActivity(intent);
            }
        });
    }
}