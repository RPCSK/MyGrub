package com.example.mygrub.admin.faq;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygrub.R;
import com.example.mygrub.admin.reports.lists.FragmentListingReport;
import com.example.mygrub.admin.reports.users.FragmentUserReport;
import com.example.mygrub.user.faq.FAQAdapter;
import com.example.mygrub.user.faq.FAQItem;
import com.example.mygrub.user.faq.FAQMain;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class FAQAdmin extends AppCompatActivity {
    private FloatingActionButton addBtn;
    private Toolbar toolbar;
    private Button postedBtn, askedBtn;
    private ProgressBar loading;
    private DatabaseReference faqRef = FirebaseDatabase.getInstance().getReference().child("FAQ").child("Posted");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_faq);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loading = findViewById(R.id.loadingBar);
        addBtn = findViewById(R.id.addBtn);
        postedBtn = findViewById(R.id.postedBtn);
        askedBtn = findViewById(R.id.askedBtn);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle(R.string.faq_label);
        setSupportActionBar(toolbar);

        loadFragment();

        postedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postedBtn.setBackgroundTintList(getColorStateList(R.color.white));
                postedBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.tiramisu_orange));
                askedBtn.setBackgroundTintList(getColorStateList(R.color.grey));
                askedBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_orange_brown));
                replaceFragment(new FragmentFAQDisplay());
            }
        });

        askedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askedBtn.setBackgroundTintList(getColorStateList(R.color.white));
                askedBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.tiramisu_orange));
                postedBtn.setBackgroundTintList(getColorStateList(R.color.grey));
                postedBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_orange_brown));
                replaceFragment(new FragmentFAQQuestions());
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddDialog();
            }
        });

    }

    private void loadFragment() {
        int intentFragment = getIntent().getIntExtra("id", 0);

        if (intentFragment == R.id.faqPostedFragment) {
            postedBtn.setBackgroundTintList(getColorStateList(R.color.white));
            postedBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.tiramisu_orange));
            askedBtn.setBackgroundTintList(getColorStateList(R.color.grey));
            askedBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_orange_brown));
            replaceFragment(new FragmentFAQDisplay());
        } else if (intentFragment == R.id.faqAskedFragment) {
            askedBtn.setBackgroundTintList(getColorStateList(R.color.white));
            askedBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.tiramisu_orange));
            postedBtn.setBackgroundTintList(getColorStateList(R.color.grey));
            postedBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_orange_brown));
            replaceFragment(new FragmentFAQQuestions());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_back_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.back_icon){
            getOnBackPressedDispatcher().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openAddDialog() {
        View view = LayoutInflater.from(FAQAdmin.this).inflate(R.layout.dialog_faq_add, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(FAQAdmin.this);

        EditText faqQueET = view.findViewById(R.id.question);
        EditText faqAnsET = view.findViewById(R.id.answer);
        TextView faqQue = view.findViewById(R.id.faqQuestion);
        TextView faqAns = view.findViewById(R.id.faqAnswer);

        builder.setView(view);
        builder.setPositiveButton("Save", null)
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
                String question = faqQueET.getText().toString();
                String answer = faqAnsET.getText().toString();

                faqQue.setTextColor(getColor(R.color.dark_sea_green));
                faqAns.setTextColor(getColor(R.color.dark_sea_green));
                faqQueET.setError(null);
                faqAnsET.setError(null);

                if (TextUtils.isEmpty(question)){
                    faqQue.setTextColor(getColor(R.color.ruby_red));
                    faqQueET.setError("Question must not be empty!");
                    faqQueET.requestFocus();
                } else if (TextUtils.isEmpty(answer)){
                    faqAns.setTextColor(getColor(R.color.ruby_red));
                    faqAnsET.setError("Answer must not be empty!");
                    faqAnsET.requestFocus();
                } else {
                    loading.setVisibility(View.VISIBLE);
                    HashMap faq = new HashMap();
                    faq.put("question", question);
                    faq.put("answer", answer);
                    faqRef.push().setValue(faq).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
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

    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmentFAQContainer, fragment).commit();
    }

    private void makeToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}