package com.example.mygrub.admin.reports.lists;

import android.content.res.ColorStateList;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ListReportDetails extends AppCompatActivity {
    private Button removeBtn, strikeOBtn, strikeRBtn, dismissBtn;
    private ImageView detailImg;
    private TextView titleT, usernameT, locationT, contactT, descT, quantityT,
            typeLabel, descDisplay;
    private ChipGroup chipGroup;
    private String listID, type, desc, offenderID, reporterID, reportID;
    private ProgressBar loading;
    private Toolbar toolbar;
    private DatabaseReference repRef = FirebaseDatabase.getInstance().getReference()
            .child("Reports").child("Listings").child("Active");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_report_list_details);
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
        detailImg = findViewById(R.id.detailImg);
        titleT = findViewById(R.id.title);
        usernameT = findViewById(R.id.username);
        locationT = findViewById(R.id.locationDetail);
        chipGroup = findViewById(R.id.chipGroup);
        contactT = findViewById(R.id.contactDetail);
        descT = findViewById(R.id.itemDesc);
        quantityT = findViewById(R.id.quantityDetail);
        loading = findViewById(R.id.loadingBar);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle(R.string.list_r_label);
        setSupportActionBar(toolbar);

        typeLabel = findViewById(R.id.typeLabel);
        descDisplay = findViewById(R.id.descDisplay);
        removeBtn = findViewById(R.id.removeBtn);
        strikeOBtn = findViewById(R.id.strikeOBtn);
        strikeRBtn = findViewById(R.id.strikeRBtn);
        dismissBtn = findViewById(R.id.dismissBtn);

        listID = getIntent().getStringExtra("listID");
        reportID = getIntent().getStringExtra("reportID");
        offenderID = getIntent().getStringExtra("offenderID");
        reporterID = getIntent().getStringExtra("reporterID");
        type = getIntent().getStringExtra("type");
        desc = getIntent().getStringExtra("desc");
    }

    private void setButtons() {
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference listRef = FirebaseDatabase.getInstance().getReference()
                        .child("Listings").child(listID);
                listRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        loading.setVisibility(View.VISIBLE);
                        removeListing(listRef, snapshot.child("imageUrl").getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

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

    private void giveMeritToReporter() {
        HashMap merits = new HashMap();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User Profiles");
        userRef.child(reporterID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("merits").exists()){
                    int point = Integer.parseInt(snapshot.child("merits").getValue().toString());
                    int newPoint = point + 1;
                    merits.put("merits", newPoint);
                } else {
                    merits.put("merits", 1);
                }
                submitMerit(userRef, merits);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void submitMerit(DatabaseReference userRef, HashMap merits) {
        userRef.child(reporterID).updateChildren(merits).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                checkCases();
            }
        });
    }

    private void removeListing(DatabaseReference listRef, String imageUrl) {
        listRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    StorageReference storeRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                    storeRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            makeToast("Listing deleted successfully!");
                            giveMeritToReporter();
                        }
                    });
                } else {
                    makeToast("Listing failed to delete!");
                }
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void checkCases() {
        repRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot shot : snapshot.getChildren()){
                    if (shot.child("listID").getValue().toString()
                            .equals(listID) && !shot.getKey().equals(reportID)){
                        dismissCase(repRef, shot.getKey());
                    }
                }
                dismissCase(repRef, reportID);
                onBackPressed();
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

    private void showDataList() {
        Picasso.get().load(getIntent().getStringExtra("imageUrl")).into(detailImg);
        titleT.setText(getIntent().getStringExtra("title"));
        usernameT.setText(getIntent().getStringExtra("username"));
        locationT.setText(getIntent().getStringExtra("location"));
        quantityT.setText(getIntent().getStringExtra("quantity")
                + " " + getIntent().getStringExtra("unit"));
        descT.setText(getIntent().getStringExtra("listDesc"));
        contactT.setText(getIntent().getStringExtra("contact"));
        setTags();
        chipGroup.setEnabled(false);
        loading.setVisibility(View.GONE);
    }

    private void setTags() {
        typeLabel.setText(type);
        descDisplay.setText(desc);

        Map<String, String> tags = (Map<String, String>) getIntent().getSerializableExtra("tags");
        for (Map.Entry<String, String> entry : tags.entrySet()) {
            Chip chip = new Chip(ListReportDetails.this);
            chip.setText(entry.getValue());
            chip.setChipBackgroundColor(ColorStateList.valueOf(getColor(R.color.emerald_green)));
            chip.setTextColor(getColor(R.color.white));
            chipGroup.addView(chip);
        }
    }

    private void makeToast(String message) {
        Toast.makeText(ListReportDetails.this, message, Toast.LENGTH_SHORT).show();
    }
}