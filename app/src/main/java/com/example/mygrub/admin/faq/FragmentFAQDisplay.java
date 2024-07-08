package com.example.mygrub.admin.faq;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
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
 * Use the {@link FragmentFAQDisplay#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentFAQDisplay extends Fragment {
    private RecyclerView faqView;
    private ArrayList<FAQItem> itemList;
    private FAQAdapter adapter;
    private ProgressBar loading;
    private DatabaseReference faqRef = FirebaseDatabase.getInstance().getReference().child("FAQ").child("Posted");

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentFAQDisplay() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentFAQDisplay.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentFAQDisplay newInstance(String param1, String param2) {
        FragmentFAQDisplay fragment = new FragmentFAQDisplay();
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
        View view = inflater.inflate(R.layout.fragment_admin_faq_display, container, false);

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
                    FAQItem item = shot.getValue(FAQItem.class);
                    Objects.requireNonNull(item).setKey(shot.getKey());
                    itemList.add(item);
                }

                adapter = new FAQAdapter(getActivity(), itemList);
                adapter.notifyDataSetChanged();
                faqView.setAdapter(adapter);
                setClick(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    private void setClick(FAQAdapter adapter) {
        adapter.setOnItemClickListener(new FAQAdapter.OnItemClickListener() {
            @Override
            public void onClick(FAQItem item) {
                openDialog(item);
            }
        });
    }

    private void openDialog(FAQItem item) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_faq_edit, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        EditText faqQueET = view.findViewById(R.id.question);
        EditText faqAnsET = view.findViewById(R.id.answer);
        TextView faqQue = view.findViewById(R.id.faqQuestion);
        TextView faqAns = view.findViewById(R.id.faqAnswer);
        faqAnsET.setText(item.getAnswer());
        faqQueET.setText(item.getQuestion());

        builder.setView(view);
        builder.setPositiveButton("Save", null)
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
                String question = faqQueET.getText().toString();
                String answer = faqAnsET.getText().toString();

                faqQue.setTextColor(getActivity().getColor(R.color.dark_sea_green));
                faqAns.setTextColor(getActivity().getColor(R.color.dark_sea_green));
                faqQueET.setError(null);
                faqAnsET.setError(null);

                if (TextUtils.isEmpty(question)){
                    faqQue.setTextColor(getActivity().getColor(R.color.ruby_red));
                    faqQueET.setError("Question must not be empty!");
                    faqQueET.requestFocus();
                } else if (TextUtils.isEmpty(answer)){
                    faqAns.setTextColor(getActivity().getColor(R.color.ruby_red));
                    faqAnsET.setError("Answer must not be empty!");
                    faqAnsET.requestFocus();
                } else {
                    loading.setVisibility(View.VISIBLE);
                    HashMap faq = new HashMap();
                    faq.put("question", question);
                    faq.put("answer", answer);
                    faqRef.child(item.getKey()).updateChildren(faq).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){
                                makeToast("FAQ item successfully edited!");
                            } else makeToast("Something went wrong!");
                            dialog.dismiss();
                            loading.setVisibility(View.GONE);
                        }
                    });
                }

            }
        });
    }

    private void makeToast(String msg){
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}