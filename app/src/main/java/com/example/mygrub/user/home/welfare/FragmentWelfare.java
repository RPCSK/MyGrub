package com.example.mygrub.user.home.welfare;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.mygrub.R;
import com.example.mygrub.user.UserMainPage;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentWelfare#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentWelfare extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //---VARIABLES---
    private CardView orgCard, aidCard;

    public FragmentWelfare() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentWelfare.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentWelfare newInstance(String param1, String param2) {
        FragmentWelfare fragment = new FragmentWelfare();
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
        View view = inflater.inflate(R.layout.fragment_main_welfare, container, false);
        getActivity().findViewById(R.id.fabBtn).setVisibility(View.GONE);

        orgCard = view.findViewById(R.id.orgCard);
        aidCard = view.findViewById(R.id.aidCard);

        orgCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), OrgMain.class));
            }
        });

        aidCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AidList.class));
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.removeItem(R.id.search_icon);
        menu.removeItem(R.id.filter_icon);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((UserMainPage) getActivity()).getSupportActionBar().setTitle(getString(R.string.welfare_label));
    }
}