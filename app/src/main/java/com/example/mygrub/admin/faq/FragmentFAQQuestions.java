package com.example.mygrub.admin.faq;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygrub.R;
import com.example.mygrub.user.faq.FAQAdapter;
import com.example.mygrub.user.faq.FAQItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
 * Use the {@link FragmentFAQQuestions#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentFAQQuestions extends Fragment {
    private RecyclerView faqView;
    private ArrayList<FAQAskItem> itemList;
    private FAQAskAdapter adapter;
    private ProgressBar loading;
    private DatabaseReference faqRef = FirebaseDatabase.getInstance().getReference().child("FAQ").child("Asked");

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentFAQQuestions() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentFAQQuestions.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentFAQQuestions newInstance(String param1, String param2) {
        FragmentFAQQuestions fragment = new FragmentFAQQuestions();
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
        View view = inflater.inflate(R.layout.fragment_admin_faq_questions, container, false);

        faqView = view.findViewById(R.id.faqView);
        loading = view.findViewById(R.id.loadingBar);

        faqView.setLayoutManager(new LinearLayoutManager(getContext()));
        itemList = new ArrayList<>();

        itemList = new ArrayList<>();

        faqRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                itemList.clear();

                for (DataSnapshot shot : snapshot.getChildren()){
                    FAQAskItem item = shot.getValue(FAQAskItem.class);
                    Objects.requireNonNull(item).setKey(shot.getKey());
                    itemList.add(item);
                }

                adapter = new FAQAskAdapter(getActivity(), itemList);
                adapter.notifyDataSetChanged();
                faqView.setAdapter(adapter);
                setClick(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private void setClick(FAQAskAdapter adapter) {
        adapter.setOnItemClickListener(new FAQAskAdapter.OnItemClickListener() {
            @Override
            public void onClick(FAQAskItem item) {
                openDialog(item);
            }
        });
    }

    private void openDialog(FAQAskItem item) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_faq_question_dis, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        TextView question = view.findViewById(R.id.faqQueDisplay);
        question.setText(item.getQuestion());

        builder.setView(view);
        builder.setPositiveButton("Reply by Email", null)
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        loading.setVisibility(View.VISIBLE);
                        faqRef.child(item.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                makeToast("FAQ Item deleted!");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                makeToast("FAQ Item failed to be deleted!");
                            }
                        });
                        loading.setVisibility(View.GONE);
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmailReply(item.getUserEmail(), item.getQuestion());
            }
        });
    }

    private void sendEmailReply(String userEmail, String question) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND, Uri.fromParts("mailto", userEmail,
                null));

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Answer to Your Question(s).");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear User,\n\nTo your question of \" " + question +
                "\", \n\nThe answer is:\n");

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