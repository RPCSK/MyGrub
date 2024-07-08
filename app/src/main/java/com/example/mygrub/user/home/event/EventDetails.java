package com.example.mygrub.user.home.event;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.mygrub.user.home.food.FoodDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class EventDetails extends AppCompatActivity {

    private ImageView detailImg, qrImage;
    private TextView eventTitle, type, username, location, dates, itemDesc, contact, qrLabel, title;
    private String eventID, userID, qrEnabled, imageUrl, qrUrl, reportType, reportDesc;
    private Button backBtn, reportBtn;
    private Toolbar toolbar;
    private Spinner typeSpinner;
    private EditText descET;
    private ProgressBar loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

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
        backBtn = findViewById(R.id.backBtn);
        reportBtn = findViewById(R.id.reportBtn);
        toolbar = findViewById(R.id.toolbar);
        loading = findViewById(R.id.loadingBar);

        setSupportActionBar(toolbar);

        eventID = getIntent().getStringExtra("eventID");
        userID = getIntent().getStringExtra("userID");
        qrEnabled = getIntent().getStringExtra("qrEnabled");
        imageUrl = getIntent().getStringExtra("imageUrl");
        qrUrl = getIntent().getStringExtra("qrUrl");

        loading.setVisibility(View.VISIBLE);

        Picasso.get().load(imageUrl).into(detailImg);
        eventTitle.setText(getIntent().getStringExtra("title"));
        type.setText(getIntent().getStringExtra("type"));
        username.setText(getIntent().getStringExtra("username"));
        location.setText(getIntent().getStringExtra("location"));
        dates.setText(getIntent().getStringExtra("dates"));
        itemDesc.setText(getIntent().getStringExtra("desc"));
        contact.setText(getIntent().getStringExtra("contact"));

        if (qrEnabled.equals("1")) {
            Picasso.get().load(qrUrl).into(qrImage);
            qrLabel.setVisibility(View.VISIBLE);
            qrImage.setVisibility(View.VISIBLE);

        }

        loading.setVisibility(View.GONE);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
    }

    private void openDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_report_this, null);

        typeSpinner = view.findViewById(R.id.typeSpinner);
        descET = view.findViewById(R.id.descET);
        title = view.findViewById(R.id.title);

        title.setText(getText(R.string.report_event_label));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.reportEType, android.R.layout.simple_spinner_item);
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
                    Toast.makeText(EventDetails.this, "Select case type!", Toast.LENGTH_SHORT).show();
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
                .child("Reports").child("Events").child("Active").push();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy h:mm a");
        String dateTime = simpleDateFormat.format(calendar.getTime());

        HashMap reportData = new HashMap();
        reportData.put("reporterID", user.getUid());
        reportData.put("offenderID", userID);
        reportData.put("eventID", eventID);
        reportData.put("type", reportType);
        reportData.put("desc", reportDesc);
        reportData.put("created_datetime", dateTime);
        reportData.put("status", "active");

        repRef.setValue(reportData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(EventDetails.this, "Report successfully lodged!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EventDetails.this, "Report failed!", Toast.LENGTH_SHORT).show();
                }
                loading.setVisibility(View.GONE);
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
        if (item.getItemId() == R.id.back_icon) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}