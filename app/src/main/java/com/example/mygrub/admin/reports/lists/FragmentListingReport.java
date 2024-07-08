package com.example.mygrub.admin.reports.lists;

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
import com.example.mygrub.user.RWDetails;
import com.example.mygrub.user.home.food.FoodDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentListingReport#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentListingReport extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //----VARIABLE

    private RecyclerView listReportView;
    private ArrayList<ListReportItem> itemList;
    private ListReportAdapter adapter;
    private TextView emptyMsg;
    private DatabaseReference listRef = FirebaseDatabase.getInstance().getReference()
            .child("Reports").child("Listings").child("Active");

    public FragmentListingReport() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentListingReport.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentListingReport newInstance(String param1, String param2) {
        FragmentListingReport fragment = new FragmentListingReport();
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
        View view = inflater.inflate(R.layout.fragment_admin_report_list, container, false);

        listReportView = view.findViewById(R.id.listReportView);
        emptyMsg = view.findViewById(R.id.emptyLabel);
        listReportView.setLayoutManager(new LinearLayoutManager(getContext()));

        itemList = new ArrayList<>();

        listRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                itemList.clear();

                for (DataSnapshot shot : snapshot.getChildren()){
                    ListReportItem item = shot.getValue(ListReportItem.class);
                    if (!itemList.contains(item)) {
                        Objects.requireNonNull(item).setKey(shot.getKey());
                        itemList.add(item);
                    } else {
                        int index = itemList.indexOf(item);
                        itemList.set(index, item);
                    }
                }

                if (itemList.isEmpty()){
                    listReportView.setVisibility(View.GONE);
                    emptyMsg.setVisibility(View.VISIBLE);
                } else {
                    listReportView.setVisibility(View.VISIBLE);
                    emptyMsg.setVisibility(View.GONE);
                }

                adapter = new ListReportAdapter(getContext(), itemList);
                adapter.notifyDataSetChanged();
                listReportView.setAdapter(adapter);
                setClick(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private void setClick(ListReportAdapter adapter) {
        adapter.setOnItemClickListener(new ListReportAdapter.OnItemClickListener() {
            @Override
            public void onClick(ListReportItem item) {
                DatabaseReference listingRef = FirebaseDatabase.getInstance().getReference().child("Listings");
                listingRef.child(item.getListID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            RWDetails listReader = snapshot.getValue(RWDetails.class);

                            Intent intent = new Intent(getActivity(), ListReportDetails.class);
                            intent.putExtra("title", listReader.title);
                            intent.putExtra("username", listReader.username);
                            intent.putExtra("location", listReader.location);
                            intent.putExtra("imageUrl", listReader.imageUrl);
                            intent.putExtra("contact", listReader.contact);
                            intent.putExtra("listDesc", listReader.desc);
                            intent.putExtra("quantity", listReader.quantity);
                            intent.putExtra("unit", listReader.unit);
                            intent.putExtra("tags", (Serializable) listReader.tags);

                            intent.putExtra("type", item.getType());
                            intent.putExtra("desc", item.getDesc());
                            intent.putExtra("reportID", item.getKey());
                            intent.putExtra("listID", item.getListID());
                            intent.putExtra("reporterID", item.getReporterID());
                            intent.putExtra("offenderID", item.getOffenderID());

                            startActivity(intent);
                        } else
                            Toast.makeText(getActivity(), "Listing doesn't exist anymore!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }
}