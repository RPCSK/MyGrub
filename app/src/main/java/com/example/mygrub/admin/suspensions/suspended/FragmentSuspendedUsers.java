package com.example.mygrub.admin.suspensions.suspended;

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
import com.example.mygrub.admin.suspensions.all.UserAdapter;
import com.example.mygrub.admin.suspensions.all.UserItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSuspendedUsers#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSuspendedUsers extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //---VARIABLES----
    private RecyclerView userView;
    private ArrayList<UserItem> itemList;
    private UserAdapter adapter;
    private TextView emptyMsg;
    private ProgressBar loading;
    private String userID, suspensionsKey, email;
    private int merits, demerits;
    private DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
            .child("User Profiles");
    private DatabaseReference suspendedRef = FirebaseDatabase.getInstance().getReference()
            .child("Suspensions");

    public FragmentSuspendedUsers() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSuspendedUsers.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSuspendedUsers newInstance(String param1, String param2) {
        FragmentSuspendedUsers fragment = new FragmentSuspendedUsers();
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
        View view = inflater.inflate(R.layout.fragment_admin_suspend_suspended, container, false);

        userView = view.findViewById(R.id.userView);
        emptyMsg = view.findViewById(R.id.emptyLabel);
        loading = view.findViewById(R.id.loadingBar);
        userView.setLayoutManager(new LinearLayoutManager(getContext()));

        itemList = new ArrayList<>();

        suspendedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();

                for (DataSnapshot shot : snapshot.getChildren()){
                    UserItem item = shot.getValue(UserItem.class);
                    if (!itemList.contains(item)) {
                        Objects.requireNonNull(item).setKey(shot.getKey());
                        itemList.add(item);
                    } else {
                        int index = itemList.indexOf(item);
                        itemList.set(index, item);
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
                suspensionsKey = item.getKey();
                userID = item.getUserID();
                email = item.getEmail();
                openDialog();
            }
        });
    }
    
    private void openDialog(){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_unsuspend_user, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        TextView suspendMsg = view.findViewById(R.id.suspendMsg);
        suspendMsg.append(" " + email+ "?");

        builder.setView(view);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loading.setVisibility(View.VISIBLE);
                unSuspendUser();
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

    private void unSuspendUser() {
        HashMap userData = new HashMap();
        userData.put("suspended", "0");
        userRef.child(userID).updateChildren(userData).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                deleteSuspensionRecord();
            }
        });
    }

    private void deleteSuspensionRecord() {
        suspendedRef.child(suspensionsKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    makeToast("User account unsuspended successfully!");
                    sendEmailNotification(email);
                } else makeToast("Unban failed!");
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void sendEmailNotification(String email) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND, Uri.fromParts(
                "mailto", email, null));

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "The Ban On Your Account Is Lifted.");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear User,\n\nYour account has been unsuspended." +
                " You may now log in and use your account." +
                "\n\nThank you for patience and cooperation.");
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