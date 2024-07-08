package com.example.mygrub.admin.suspensions.all;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygrub.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentAllUsers#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentAllUsers extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //----VARIABLES----
    private RecyclerView userView;
    private ArrayList<UserItem> itemList;
    private UserAdapter adapter;
    private TextView emptyMsg;
    private ProgressBar loading;
    private String userID, email;
    private int merits, demerits;
    private DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
            .child("User Profiles");

    public FragmentAllUsers() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentAllUsers.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentAllUsers newInstance(String param1, String param2) {
        FragmentAllUsers fragment = new FragmentAllUsers();
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
        View view = inflater.inflate(R.layout.fragment_admin_suspend_all, container, false);

        userView = view.findViewById(R.id.userView);
        emptyMsg = view.findViewById(R.id.emptyLabel);
        loading = view.findViewById(R.id.loadingBar);
        userView.setLayoutManager(new LinearLayoutManager(getContext()));

        itemList = new ArrayList<>();

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();

                for (DataSnapshot shot : snapshot.getChildren()){
                    UserItem item = shot.getValue(UserItem.class);
                    if (item.getPrivilege().equals("user")){
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
                    userView.setVisibility(View.GONE);
                    emptyMsg.setVisibility(View.VISIBLE);
                } else {
                    userView.setVisibility(View.VISIBLE);
                    emptyMsg.setVisibility(View.GONE);
                }

                adapter = new UserAdapter(getContext(), itemList);
                adapter.notifyDataSetChanged();
                userView.setAdapter(adapter);
                setClick(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void setClick(UserAdapter adapter) {
        adapter.setOnItemClickListener(new UserAdapter.OnItemClickListener() {
            @Override
            public void onClick(UserItem item) {
                userID = item.getKey();
                email = item.getEmail();
                merits = item.getMerits();
                demerits = item.getDemerits();
                openDialog();
            }
        });
    }

    private void openDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_suspend_user, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        TextView suspendMsg = view.findViewById(R.id.suspendMsg);
        suspendMsg.append(" " + email+ "?");

        builder.setView(view);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loading.setVisibility(View.VISIBLE);
                suspendUser();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getActivity().getColor(R.color.ruby_red));
            }
        });
        dialog.show();
    }

    private void suspendUser(){
        HashMap userData = new HashMap();
        userData.put("suspended", "1");
        userRef.child(userID).updateChildren(userData).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                recordSuspension();
            }
        });
    }

    private void recordSuspension() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy h:mm a");
        String dateTime = simpleDateFormat.format(calendar.getTime());

        HashMap suspendData = new HashMap();
        suspendData.put("userID", userID);
        suspendData.put("email", email);
        suspendData.put("merits", merits);
        suspendData.put("demerits", demerits);
        suspendData.put("suspend_date", dateTime);

        DatabaseReference suspendRef = FirebaseDatabase.getInstance().getReference()
                .child("Suspensions").push();
        suspendRef.setValue(suspendData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                makeToast("User suspended successfully!");
                loading.setVisibility(View.GONE);
                sendEmailNotification(email);
            }
        });
    }

    private void sendEmailNotification(String userEmail) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND, Uri.fromParts(
                "mailto", userEmail, null));

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your Account is Suspended.");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear User,\n\nYour account has been suspended." +
                " Please contact our team at @RandomHandle for any inquiries or further actions." +
                "\n\nThank you.");
        try {
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
            Log.d("EmailNotification", "Email intent started successfully.");
        } catch (android.content.ActivityNotFoundException ex) {
            Log.e("EmailNotification", "No email client installed on the device.");
        }
    }

    private void makeToast(String msg){
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}