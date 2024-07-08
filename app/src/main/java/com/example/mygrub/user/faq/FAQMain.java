package com.example.mygrub.user.faq;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygrub.R;
import com.example.mygrub.user.home.food.ListingItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class FAQMain extends AppCompatActivity {

    private RecyclerView faqView;
    private ArrayList<FAQItem> itemList;
    private FAQAdapter adapter;
    private ProgressBar loading;
    private FloatingActionButton faqBtn;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ImageView backBtn;
    private DatabaseReference faqRef = FirebaseDatabase.getInstance().getReference().child("FAQ").child("Posted");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_faq);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        faqView = findViewById(R.id.faqView);
        backBtn = findViewById(R.id.backBtn);
        faqBtn = findViewById(R.id.faqBtn);
        loading = findViewById(R.id.loadingBar);

        faqView.setLayoutManager(new LinearLayoutManager(FAQMain.this));
        itemList = new ArrayList<>();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        faqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        faqRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot shot : snapshot.getChildren()){
                    FAQItem item = shot.getValue(FAQItem.class);
                    Objects.requireNonNull(item).setKey(shot.getKey());
                    itemList.add(item);
                }

                adapter = new FAQAdapter(FAQMain.this, itemList);
                faqView.setAdapter(adapter);
                setClick(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void openDialog() {
        View view = LayoutInflater.from(FAQMain.this).inflate(R.layout.dialog_faq_question, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(FAQMain.this);

        EditText questionBox = view.findViewById(R.id.question);
        TextView questionDisplay = view.findViewById(R.id.faqQuestion);

        builder.setView(view);
        builder.setPositiveButton("Send", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
                String question = questionBox.getText().toString();

                questionDisplay.setTextColor(getColor(R.color.dark_sea_green));
                questionBox.setError(null);

                if (TextUtils.isEmpty(question)){
                    questionDisplay.setTextColor(getColor(R.color.ruby_red));
                    questionBox.setError("Question must not be empty!");
                    questionBox.requestFocus();
                } else {
                    loading.setVisibility(View.VISIBLE);
                    HashMap faq = new HashMap();
                    faq.put("question", question);
                    faq.put("userID", user.getUid());
                    faq.put("userEmail", user.getEmail());

                    DatabaseReference faqRef2 = FirebaseDatabase.getInstance().getReference().child("FAQ");
                    faqRef2.child("Asked").push().setValue(faq).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                makeToast("FAQ item successfully added!");
                            } else makeToast("Something went wrong!");
                            dialog.dismiss();
                            loading.setVisibility(View.GONE);
                        }
                    });
                }

            }
        });
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
        View view = LayoutInflater.from(FAQMain.this).inflate(R.layout.dialog_faq_answer, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(FAQMain.this);

        TextView faqAnsDisplay = view.findViewById(R.id.faqAnsDisplay);
        faqAnsDisplay.setText(item.getAnswer());

        builder.setView(view);
        builder.setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void makeToast(String msg){
        Toast.makeText(FAQMain.this, msg, Toast.LENGTH_SHORT).show();
    }
}