package com.example.mygrub.admin.reports.users;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mygrub.R;
import com.example.mygrub.admin.reports.lists.ListReportDetails;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class UserReportDetails extends AppCompatActivity {
    private Button strikeOBtn, strikeRBtn, dismissBtn;
    private ImageView detailImg;
    private TextView messageDisplay, contactDetail, requesterDisplay,
            typeLabel, descDisplay;
    private String type, desc, offenderID, reporterID, reportID, offenderMessage, offenderContact,
            offenderName;
    private ProgressBar loading;
    private Toolbar toolbar;
    private DatabaseReference repRef = FirebaseDatabase.getInstance().getReference()
            .child("Reports").child("Users").child("Active");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_report_user_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setBindings();
        loading.setVisibility(View.VISIBLE);
        showDataList();
        setButtons();
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

    private void setBindings() {
        detailImg = findViewById(R.id.requesterImage);
        requesterDisplay = findViewById(R.id.requesterDisplay);
        contactDetail = findViewById(R.id.contactDetail);
        messageDisplay = findViewById(R.id.messageDisplay);
        loading = findViewById(R.id.loadingBar);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle(R.string.user_r_label);
        setSupportActionBar(toolbar);

        typeLabel = findViewById(R.id.typeLabel);
        descDisplay = findViewById(R.id.descDisplay);
        strikeOBtn = findViewById(R.id.strikeOBtn);
        strikeRBtn = findViewById(R.id.strikeRBtn);
        dismissBtn = findViewById(R.id.dismissBtn);

        reportID = getIntent().getStringExtra("reportID");
        offenderID = getIntent().getStringExtra("offenderID");
        reporterID = getIntent().getStringExtra("reporterID");
        type = getIntent().getStringExtra("type");
        desc = getIntent().getStringExtra("desc");
        offenderMessage = getIntent().getStringExtra("offenderMessage");
        offenderContact = getIntent().getStringExtra("offenderContact");
        offenderName = getIntent().getStringExtra("offenderName");
    }

    private void showDataList() {
        Picasso.get().load(getIntent().getStringExtra("imageUrl")).into(detailImg);
        requesterDisplay.setText(offenderName);
        contactDetail.setText(offenderContact);
        messageDisplay.setText(offenderMessage);
        typeLabel.setText(type);
        descDisplay.setText(desc);
        loading.setVisibility(View.GONE);
    }

    private void setButtons() {
        strikeOBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                giveDemerit(offenderID);

            }
        });

        strikeRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                giveDemerit(reporterID);
            }
        });

        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading.setVisibility(View.VISIBLE);
                dismissCase(repRef, reportID);
                loading.setVisibility(View.GONE);
                makeToast("Case dismissed!");
                onBackPressed();
            }
        });
    }

    private void giveDemerit(String userID) {
        HashMap demerits = new HashMap();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User Profiles");
        userRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("demerits").exists()){
                    int point = Integer.parseInt(snapshot.child("demerits").getValue().toString());
                    int newPoint = point + 1;
                    demerits.put("demerits", newPoint);
                } else {
                    demerits.put("demerits", 1);
                }
                submitDemerit(userRef, userID, demerits);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void submitDemerit(DatabaseReference userRef, String userID, HashMap demerits) {
        userRef.child(userID).updateChildren(demerits).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                if (userID.equals(offenderID))
                    makeToast("Strike (demerit) given to the offender!");
                else if (userID.equals(reporterID))
                    makeToast("Strike (demerit) given to the reporter!");
            }
        });
    }

    private void dismissCase(DatabaseReference repRef, String key) {
        repRef.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
            }
        });
    }

    private void makeToast(String message) {
        Toast.makeText(UserReportDetails.this, message, Toast.LENGTH_SHORT).show();
    }
}