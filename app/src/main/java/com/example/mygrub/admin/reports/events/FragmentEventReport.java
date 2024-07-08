package com.example.mygrub.admin.reports.events;

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
import com.example.mygrub.admin.reports.lists.ListReportDetails;
import com.example.mygrub.user.RWDetails;
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
 * Use the {@link FragmentEventReport#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentEventReport extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //--------VARIABLES-----------
    private RecyclerView eventReportView;
    private ArrayList<EventReportItem> itemList;
    private EventReportAdapter adapter;
    private TextView emptyMsg;
    private DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference()
            .child("Reports").child("Events").child("Active");

    public FragmentEventReport() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentEventReport.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentEventReport newInstance(String param1, String param2) {
        FragmentEventReport fragment = new FragmentEventReport();
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
        View view = inflater.inflate(R.layout.fragment_admin_report_event, container, false);

        eventReportView = view.findViewById(R.id.eventReportView);
        emptyMsg = view.findViewById(R.id.emptyLabel);
        eventReportView.setLayoutManager(new LinearLayoutManager(getContext()));

        itemList = new ArrayList<>();

        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                itemList.clear();

                for (DataSnapshot shot : snapshot.getChildren()){
                    EventReportItem item = shot.getValue(EventReportItem.class);
                    if (!itemList.contains(item)) {
                        Objects.requireNonNull(item).setKey(shot.getKey());
                        itemList.add(item);
                    } else {
                        int index = itemList.indexOf(item);
                        itemList.set(index, item);
                    }
                }

                if (itemList.isEmpty()){
                    eventReportView.setVisibility(View.GONE);
                    emptyMsg.setVisibility(View.VISIBLE);
                } else {
                    eventReportView.setVisibility(View.VISIBLE);
                    emptyMsg.setVisibility(View.GONE);
                }

                adapter = new EventReportAdapter(getContext(), itemList);
                adapter.notifyDataSetChanged();
                eventReportView.setAdapter(adapter);
                setClick(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private void setClick(EventReportAdapter adapter) {
        adapter.setOnItemClickListener(new EventReportAdapter.OnItemClickListener() {
            @Override
            public void onClick(EventReportItem item) {
                DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference()
                        .child("Events");
                eventRef.child(item.getEventID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            RWDetails eventReader = snapshot.getValue(RWDetails.class);

                            Intent intent = new Intent(getActivity(), EventReportDetails.class);
                            intent.putExtra("title", eventReader.title);
                            intent.putExtra("username", eventReader.username);
                            intent.putExtra("location", eventReader.location);
                            intent.putExtra("imageUrl", eventReader.imageUrl);
                            intent.putExtra("eventType", eventReader.type);
                            intent.putExtra("eventDesc", eventReader.desc);
                            intent.putExtra("contact", eventReader.contact);
                            intent.putExtra("dates", eventReader.startDate + "  -  "
                                    + eventReader.endDate);
                            intent.putExtra("qrEnabled", eventReader.qrEnabled);
                            if (eventReader.qrEnabled.equals("1")){
                                intent.putExtra("qrUrl", eventReader.qrUrl);
                            }

                            intent.putExtra("type", item.getType());
                            intent.putExtra("desc", item.getDesc());
                            intent.putExtra("reportID", item.getKey());
                            intent.putExtra("eventID", item.getEventID());
                            intent.putExtra("reporterID", item.getReporterID());
                            intent.putExtra("offenderID", item.getOffenderID());

                            startActivity(intent);
                        } else
                            Toast.makeText(getActivity(), "Event doesn't exist anymore!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}