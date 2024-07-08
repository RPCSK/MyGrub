package com.example.mygrub.user.home.food;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygrub.R;
import com.example.mygrub.user.UserMainPage;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHome extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //--- VARIABLE ---
    private RecyclerView listingView;
    private ArrayList<ListingItem> itemList;
    private ListAdapter adapter;
    private RelativeLayout filterLayout;
    private ChipGroup halalGroup, dietGroup, typeGroup;
    private Button applyBtn, clearBtn, resetBtn;
    private TextView emptyMsg, emptySearchMsg, collapsibleToggle;
    private int filterOpened = 0;
    private int collapsed = 0;
    private DatabaseReference listRef = FirebaseDatabase.getInstance().getReference().child("Listings");

    public FragmentHome() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentHome.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentHome newInstance(String param1, String param2) {
        FragmentHome fragment = new FragmentHome();
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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_home, container, false);

        getActivity().findViewById(R.id.fabBtn).setVisibility(View.VISIBLE);

        filterLayout = view.findViewById(R.id.filterLayout);
        applyBtn = view.findViewById(R.id.applyBtn);
        clearBtn = view.findViewById(R.id.clearBtn);
        resetBtn = view.findViewById(R.id.resetBtn);
        collapsibleToggle = view.findViewById(R.id.collapsibleToggle);

        halalGroup = view.findViewById(R.id.halalGroup);
        dietGroup = view.findViewById(R.id.dietGroup);
        typeGroup = view.findViewById(R.id.typeGroup);

        listingView = view.findViewById(R.id.listingView);
        emptyMsg = view.findViewById(R.id.emptyLabel);
        emptySearchMsg = view.findViewById(R.id.emptySearchLabel);
        listingView.setLayoutManager(new LinearLayoutManager(getContext()));

        collapsibleToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (collapsed == 0){
                    collapsed = 1;
                    filterLayout.setLayoutParams
                            (new RelativeLayout.LayoutParams
                                    (RelativeLayout.LayoutParams.MATCH_PARENT, 150));
                    collapsibleToggle.setText(getActivity().getString(R.string.expand_label));
                    collapsibleToggle.setTextColor(getActivity().getColor(R.color.bright_green));
                } else {
                    collapsed = 0;
                    filterLayout.setLayoutParams
                            (new RelativeLayout.LayoutParams
                                    (RelativeLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    collapsibleToggle.setText(getActivity().getString(R.string.minimise_label));
                    collapsibleToggle.setTextColor(getActivity().getColor(R.color.bright_red));
                }
            }
        });

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyFilters();
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetSelection();
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFilter();
            }
        });

        itemList = new ArrayList<>();

        listRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                itemList.clear();

                for (DataSnapshot shot : snapshot.getChildren()){
                    if (shot.child("status").getValue().toString().equals("Open")){
                        ListingItem item = shot.getValue(ListingItem.class);
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
                    listingView.setVisibility(View.GONE);
                    emptyMsg.setVisibility(View.VISIBLE);
                } else {
                    listingView.setVisibility(View.VISIBLE);
                    emptyMsg.setVisibility(View.GONE);
                }

                Collections.reverse(itemList);
                adapter = new ListAdapter(getContext(), itemList);
                adapter.notifyDataSetChanged();
                listingView.setAdapter(adapter);
                setClick(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void resetFilter() {
        resetSelection();
        emptySearchMsg.setVisibility(View.GONE);
        adapter.setFilteredList(itemList);
        adapter.notifyDataSetChanged();
    }

    private void resetSelection() {
        halalGroup.clearCheck();
        dietGroup.clearCheck();
        typeGroup.clearCheck();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((UserMainPage) getActivity()).getSupportActionBar().setTitle(getString(R.string.home_label));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.actionbar_home_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.filter_icon){
            if (filterOpened == 0){
                filterOpened = 1;
                filterLayout.setVisibility(View.VISIBLE);
            } else {
                filterOpened = 0;
                filterLayout.setVisibility(View.GONE);
            }
        } else if (item.getItemId() == R.id.search_icon){
            SearchView searchView = (SearchView) item.getActionView();
            searchView.setQueryHint(getString(R.string.searching_list_label));

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String keyword) {
                    filterList(keyword);
                    return true;
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    private void applyFilters(){
        ArrayList<ListingItem> filteredList = new ArrayList<>();
        ArrayList<String> filters = new ArrayList<>();

        getChips(filters);

        if(!filters.isEmpty()){
            for (ListingItem item : itemList){
                if (itemContainsAllTags(item.getTags(), filters)){
                    filteredList.add(item);
                    emptySearchMsg.setVisibility(View.GONE);
                }
                else adapter.setFilteredList(filteredList);
            }

            if (filteredList.isEmpty()){
                emptySearchMsg.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "No match found!", Toast.LENGTH_SHORT).show();
            } else {
                adapter.setFilteredList(filteredList);
            }
            collapsed = 1;
            filterLayout.setLayoutParams
                    (new RelativeLayout.LayoutParams
                            (RelativeLayout.LayoutParams.MATCH_PARENT, 150));
            collapsibleToggle.setText(getActivity().getString(R.string.expand_label));
            collapsibleToggle.setTextColor(getActivity().getColor(R.color.bright_green));
        }
    }

    private boolean itemContainsAllTags(Map<String, String> tags, ArrayList<String> filters) {
        for (String data : filters) {
            if (!tags.containsValue(data)) {
                return false;
            }
        }
        return true;
    }

    private void getChips(ArrayList<String> filters) {
        ArrayList<Integer> idHalal = (ArrayList<Integer>) halalGroup.getCheckedChipIds();

        for (Integer id: idHalal){
            Chip chip = halalGroup.findViewById(id);
            if (chip.isChecked()){
                filters.add(chip.getText().toString());
            }
        }
        ArrayList<Integer> idDiet = (ArrayList<Integer>) dietGroup.getCheckedChipIds();
        for (Integer id: idDiet){
            Chip chip = dietGroup.findViewById(id);
            if (chip.isChecked()){
                filters.add(chip.getText().toString());
            }
        }
        ArrayList<Integer> idType = (ArrayList<Integer>) typeGroup.getCheckedChipIds();
        for (Integer id: idType){
            Chip chip = typeGroup.findViewById(id);
            if (chip.isChecked()){
                filters.add(chip.getText().toString());
            }
        }
    }

    private void filterList(String keyword) {
        ArrayList<ListingItem> filteredList = new ArrayList<>();
        for (ListingItem item : itemList){
            if (item.getTitle().toLowerCase().contains(keyword.toLowerCase())){
                filteredList.add(item);
                emptySearchMsg.setVisibility(View.GONE);
            }
            else adapter.setFilteredList(filteredList);
        }
        if (filteredList.isEmpty()){
            emptySearchMsg.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), "No match found!", Toast.LENGTH_SHORT).show();
        } else {
            emptySearchMsg.setVisibility(View.GONE);
            adapter.setFilteredList(filteredList);
        }
    }

    private void setClick(ListAdapter adapter) {
        adapter.setOnItemClickListener(new ListAdapter.OnItemClickListener() {
            @Override
            public void onClick(ListingItem item) {
                Intent intent = new Intent(getActivity(), FoodDetails.class);
                intent.putExtra("title", item.getTitle());
                intent.putExtra("username", item.getUsername());
                intent.putExtra("location", item.getLocation());
                intent.putExtra("listID", item.getKey());
                intent.putExtra("userID", item.getUserID());
                intent.putExtra("imageUrl", item.getImageUrl());
                intent.putExtra("contact", item.getContact());
                intent.putExtra("desc", item.getDesc());
                intent.putExtra("quantity", item.getQuantity());
                intent.putExtra("unit", item.getUnit());
                startActivity(intent);
            }
        });
    }
}