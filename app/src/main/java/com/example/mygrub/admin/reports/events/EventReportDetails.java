package com.example.mygrub.admin.reports.events;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EventReportDetails extends AppCompatActivity {

    private ImageView detailImg, qrImage;
    private Button removeBtn, strikeOBtn, strikeRBtn, dismissBtn;
    private TextView eventTitle, type, username, location, dates, itemDesc, contact, qrLabel, title,
            typeLabel, descDisplay;
    private String eventID, qrEnabled, imageUrl, qrUrl, reportType, reportDesc, desc,
            offenderID, reporterID, reportID;
    private Toolbar toolbar;
    private ProgressBar loading;
    private DatabaseReference repRef = FirebaseDatabase.getInstance().getReference()
            .child("Reports").child("Events").child("Active");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_report_event_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setBindings();
        showEventData();
        setButtons();
    }

    private void setButtons() {
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference()
                        .child("Events").child(eventID);
                eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        loading.setVisibility(View.VISIBLE);
                        removeEvent(eventRef);
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

    private void removeEvent(DatabaseReference eventRef) {
        eventRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if (qrEnabled.equals("1")) {
                    removeQR();
                } else removeImage();
            }
        });
    }

    private void removeQR() {
        StorageReference storeRef = FirebaseStorage.getInstance().getReferenceFromUrl(qrUrl);
        storeRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                removeImage();
            }
        });
    }

    private void removeImage() {
        StorageReference storeRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
        storeRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                makeToast("Event deleted successfully!");
                giveMeritToReporter();
            }
        });
    }

    private void dismissCase(DatabaseReference repRef, String reportID) {
        repRef.child(reportID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
            }
        });
    }

    private void giveDemerit(String userID) {
        HashMap demerits = new HashMap();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User Profiles");
        userRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("demerits").exists()) {
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
                if (snapshot.child("merits").exists()) {
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

    private void checkCases() {
        repRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot shot : snapshot.getChildren()) {
                    if (shot.child("eventID").getValue().toString()
                            .equals(eventID) && !shot.getKey().equals(reportID)) {
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

    private void showEventData() {
        loading.setVisibility(View.VISIBLE);

        typeLabel.setText(reportType);
        descDisplay.setText(desc);

        Picasso.get().load(imageUrl).into(detailImg);
        eventTitle.setText(getIntent().getStringExtra("title"));
        type.setText(getIntent().getStringExtra("eventType"));
        username.setText(getIntent().getStringExtra("username"));
        location.setText(getIntent().getStringExtra("location"));
        dates.setText(getIntent().getStringExtra("dates"));
        itemDesc.setText(getIntent().getStringExtra("eventDesc"));
        contact.setText(getIntent().getStringExtra("contact"));

        if (qrEnabled.equals("1")) {
            Picasso.get().load(qrUrl).into(qrImage);
            qrLabel.setVisibility(View.VISIBLE);
            qrImage.setVisibility(View.VISIBLE);
        }

        loading.setVisibility(View.GONE);
    }

    private void setBindings() {
        detailImg = findViewById(R.id.detailImg);
        qrImage = findViewById(R.id.qrImage);
        qrLabel = findViewById(R.id.qrLabel);
        eventTitle = findViewById(R.id.title);
        type = findViewById(R.id.typeDetail);
        username = findViewById(R.id.username);
        location = findViewById(R.id.locationDetail);
        dates = findViewById(R.id.datesDetail);
        itemDesc = findViewById(R.id.itemDesc);
        contact = findViewById(R.id.contactDetail);
        loading = findViewById(R.id.loadingBar);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle(R.string.event_r_label);
        setSupportActionBar(toolbar);

        typeLabel = findViewById(R.id.typeLabel);
        descDisplay = findViewById(R.id.descDisplay);
        removeBtn = findViewById(R.id.removeBtn);
        strikeOBtn = findViewById(R.id.strikeOBtn);
        strikeRBtn = findViewById(R.id.strikeRBtn);
        dismissBtn = findViewById(R.id.dismissBtn);

        reportID = getIntent().getStringExtra("reportID");
        offenderID = getIntent().getStringExtra("offenderID");
        reporterID = getIntent().getStringExtra("reporterID");
        reportType = getIntent().getStringExtra("type");
        desc = getIntent().getStringExtra("desc");

        eventID = getIntent().getStringExtra("eventID");
        qrEnabled = getIntent().getStringExtra("qrEnabled");
        imageUrl = getIntent().getStringExtra("imageUrl");
        qrUrl = getIntent().getStringExtra("qrUrl");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_back_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.back_icon) {
            getOnBackPressedDispatcher().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void makeToast(String message) {
        Toast.makeText(EventReportDetails.this, message, Toast.LENGTH_SHORT).show();
    }
}