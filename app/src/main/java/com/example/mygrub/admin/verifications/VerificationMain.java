package com.example.mygrub.admin.verifications;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygrub.R;
import com.example.mygrub.admin.AdminMainPage;
import com.example.mygrub.user.faq.FAQAdapter;
import com.example.mygrub.user.faq.FAQItem;
import com.example.mygrub.user.faq.FAQMain;
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

public class VerificationMain extends AppCompatActivity {

    private RecyclerView verifyView;
    private ArrayList<VerificationItem> itemList;
    private VerificationAdapter adapter;
    private TextView emptyMsg;
    private Toolbar toolbar;
    private DatabaseReference verifyRef = FirebaseDatabase.getInstance().getReference()
            .child("Verification Requests");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_verify_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emptyMsg = findViewById(R.id.emptyLabel);
        verifyView = findViewById(R.id.verifyView);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle(R.string.verifications_label);
        setSupportActionBar(toolbar);

        verifyView.setLayoutManager(new LinearLayoutManager(VerificationMain.this));
        itemList = new ArrayList<>();

        verifyRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                itemList.clear();

                for (DataSnapshot shot : snapshot.getChildren()){
                    VerificationItem item = shot.getValue(VerificationItem.class);
                    Objects.requireNonNull(item).setKey(shot.getKey());
                    itemList.add(item);
                }

                if (itemList.isEmpty()){
                    verifyView.setVisibility(View.GONE);
                    emptyMsg.setVisibility(View.VISIBLE);
                } else {
                    verifyView.setVisibility(View.VISIBLE);
                    emptyMsg.setVisibility(View.GONE);
                }

                adapter = new VerificationAdapter(VerificationMain.this, itemList);
                adapter.notifyDataSetChanged();
                verifyView.setAdapter(adapter);
                setClick(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    private void setClick(VerificationAdapter adapter) {
        adapter.setOnItemClickListener(new VerificationAdapter.OnItemClickListener() {
            @Override
            public void onApproveBtnClick(VerificationItem item) {
                verifyRef.child(item.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        updateUserStatus(item);
                    }
                });
            }

            @Override
            public void onRejectBtnClick(VerificationItem item) {
                verifyRef.child(item.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        sendEmailNotification(item.getEmail(), "Rejected");
                    }
                });
            }
        });
    }

    private void updateUserStatus(VerificationItem item) {
        HashMap userData = new HashMap();
        userData.put("verify", "1");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("User Profiles").child(item.getUserID());
        userRef.updateChildren(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    sendEmailNotification(item.getEmail(), "Approved");
                }
            }
        });
    }

    private void sendEmailNotification(String userEmail, String status) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND, Uri.fromParts(
                "mailto", userEmail, null));
        if (status.equals("Approved")){
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Account Verification Approved " + status);
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear User,\n\nYour account verification request" +
                    " has been " + status.toLowerCase() + ". \n\nYou may now access features associated with it now!" +
                    "\n\nThank you.");
        } else {
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Account Verification Rejected " + status);
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear User,\n\nYour account verification request" +
                    " has been " + status.toLowerCase() + ". \n\nPlease contact us via @randomHandle " +
                    "if you wish to make an appeal." + "\n\nThank you.");
        }

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
            Log.d("EmailNotification", "Email intent started successfully.");
        } catch (android.content.ActivityNotFoundException ex) {
            Log.e("EmailNotification", "No email client installed on the device.");
        }
    }
}