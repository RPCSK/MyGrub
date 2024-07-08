package com.example.mygrub.user.home.food;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygrub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class FoodDetails extends AppCompatActivity {

    private ImageView detailImg;
    private TextView titleT, usernameT, locationT, contactT, descT, quantityT;
    private ChipGroup chipGroup;
    private String title, username, location, contact, desc, listID, imageUrl, quantity, unit,
    reportType, reportDesc, ownerID, halalTag, dietTag, typeTag, requestContact, requestDesc;
    private Button requestBtn, reportBtn;
    private Toolbar toolbar;
    private ProgressBar loading;
    private Spinner typeSpinner;
    private EditText descET;

    public FoodDetails() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_food_details);

        detailImg = findViewById(R.id.detailImg);
        titleT = findViewById(R.id.title);
        usernameT = findViewById(R.id.username);
        locationT = findViewById(R.id.locationDetail);
        chipGroup = findViewById(R.id.chipGroup);
        contactT = findViewById(R.id.contactDetail);
        descT = findViewById(R.id.itemDesc);
        quantityT = findViewById(R.id.quantityDetail);
        requestBtn = findViewById(R.id.requestBtn);
        reportBtn = findViewById(R.id.reportBtn);
        loading = findViewById(R.id.loadingBar);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        loading.setVisibility(View.VISIBLE);

        imageUrl = getIntent().getStringExtra("imageUrl");
        title = getIntent().getStringExtra("title");
        username = getIntent().getStringExtra("username");
        location = getIntent().getStringExtra("location");
        contact = getIntent().getStringExtra("contact");
        quantity = getIntent().getStringExtra("quantity");
        unit = getIntent().getStringExtra("unit");
        desc = getIntent().getStringExtra("desc");
        listID = getIntent().getStringExtra("listID");
        ownerID = getIntent().getStringExtra("userID");

        Picasso.get().load(imageUrl).into(detailImg);
        titleT.setText(title);
        usernameT.setText(username);
        locationT.setText(location);
        quantityT.setText(quantity + " " + unit);
        descT.setText(desc);
        contactT.setText(contact);
        setTags();
        chipGroup.setEnabled(false);

        loading.setVisibility(View.GONE);

        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRequestDialog();
            }
        });

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openReportDialog();
            }
        });
    }

    private void openRequestDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_request_this, null);

        EditText itemName = view.findViewById(R.id.itemName);
        EditText contactET = view.findViewById(R.id.contactDetail);
        EditText descET = view.findViewById(R.id.descET);

        itemName.setText(title);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setPositiveButton("Submit", null)
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
                requestContact = contactET.getText().toString();
                requestDesc = descET.getText().toString();
                contactET.setError(null);
                descET.setError(null);

                if (TextUtils.isEmpty(requestContact)){
                    contactET.setError("Leave your contact info here.");
                    contactET.requestFocus();
                } else if (TextUtils.isEmpty(requestDesc)){
                    descET.setError("Message cannot be empty.");
                    descET.requestFocus();
                } else {
                    dialog.dismiss();
                    loading.setVisibility(View.VISIBLE);
                    insertRequest();
                }
            }
        });
    }

    private void insertRequest() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference repRef = FirebaseDatabase.getInstance().getReference()
                .child("User Profiles").child(ownerID).child("Requests").push();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy h:mm a");
        String dateTime = simpleDateFormat.format(calendar.getTime());

        HashMap reportData = getHashMap(user, dateTime);

        repRef.setValue(reportData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(FoodDetails.this, "Request successfully submitted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FoodDetails.this, "Request failed!", Toast.LENGTH_SHORT).show();
                }
                loading.setVisibility(View.GONE);
            }
        });
    }

    @NonNull
    private HashMap getHashMap(FirebaseUser user, String dateTime) {
        HashMap reportData = new HashMap();
        reportData.put("requesterID", user.getUid());
        reportData.put("listID", listID);
        reportData.put("imageUrl", imageUrl);
        reportData.put("title", title);
        reportData.put("location", location);
        reportData.put("halalTag", halalTag);
        reportData.put("dietTag", dietTag);
        reportData.put("typeTag", typeTag);
        reportData.put("contact", requestContact);
        reportData.put("desc", requestDesc);
        reportData.put("created_datetime", dateTime);
        reportData.put("status", "unread");
        return reportData;
    }

    private void openReportDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_report_this, null);

        typeSpinner = view.findViewById(R.id.typeSpinner);
        descET = view.findViewById(R.id.descET);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.reportType, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                reportType = adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setPositiveButton("Submit", null)
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
                reportDesc = descET.getText().toString();
                descET.setError(null);
                if (reportType.equals("Select")){
                    makeToast("Select report type!");
                } else if (TextUtils.isEmpty(reportDesc)){
                    descET.setError("Description cannot be empty.");
                    descET.requestFocus();
                } else {
                    dialog.dismiss();
                    loading.setVisibility(View.VISIBLE);
                    insertReport();
                }
            }
        });
    }

    private void insertReport() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference repRef = FirebaseDatabase.getInstance().getReference()
                .child("Reports").child("Listings").child("Active").push();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy h:mm a");
        String dateTime = simpleDateFormat.format(calendar.getTime());

        HashMap reportData = new HashMap();
        reportData.put("reporterID", user.getUid());
        reportData.put("offenderID", ownerID);
        reportData.put("listID", listID);
        reportData.put("type", reportType);
        reportData.put("desc", reportDesc);
        reportData.put("created_datetime", dateTime);
        reportData.put("status", "active");

        repRef.setValue(reportData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    makeToast("Report successfully lodged!");
                } else {
                    makeToast("Report failed!");
                }
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void setTags() {
        DatabaseReference tagRef = FirebaseDatabase.getInstance().getReference().child("Listings");
        tagRef.child(listID).child("tags").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                halalTag = snapshot.child("halalTag").getValue().toString();
                dietTag = snapshot.child("dietTag0").getValue().toString();
                typeTag = snapshot.child("typeTag0").getValue().toString();

                for (DataSnapshot data : snapshot.getChildren()){
                    Chip chip = new Chip(FoodDetails.this);
                    chip.setText(data.getValue().toString());
                    chip.setChipBackgroundColor(ColorStateList.valueOf(getColor(R.color.emerald_green)));
                    chip.setTextColor(getColor(R.color.white));
                    chipGroup.addView(chip);
                }
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

    public void makeToast(String msg){
        Toast.makeText(FoodDetails.this, msg, Toast.LENGTH_SHORT).show();
    }
}