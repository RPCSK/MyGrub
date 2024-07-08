package com.example.mygrub.user.posts;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mygrub.R;
import com.example.mygrub.user.home.food.FoodDetails;
import com.example.mygrub.user.settings.DeleteAcc;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Objects;

public class RequestDetail extends AppCompatActivity {

    private ImageView listImage, requesterImage;
    private TextView title, location, tag1, tag2, tag3,
            requesterDisplay, contactDetail, messageDisplay;
    private String reportType, reportDesc;
    private Button fulfilledBtn, rejectBtn, backBtn, reportBtn;
    private ProgressBar loading;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference reqRef = FirebaseDatabase.getInstance().getReference()
            .child("User Profiles").child(user.getUid()).child("Requests");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_request_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setBindings();
        showData();
        setButtons();
    }

    private void setButtons() {
        fulfilledBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog(fulfilledBtn);
            }
        });

        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog(rejectBtn);
            }
        });

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openReportDialog();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void openDialog(Button button) {
        View view = LayoutInflater.from(RequestDetail.this).inflate(R.layout.dialog_confirmation_wall, null);

        TextView wallMsg = view.findViewById(R.id.wallMsg);

        if (button == fulfilledBtn) wallMsg.setText(getString(R.string.fulfill_sure));
        else if (button == rejectBtn) wallMsg.setText(getString(R.string.reject_sure));

        AlertDialog.Builder builder = new AlertDialog.Builder(RequestDetail.this);
        builder.setView(view);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loading.setVisibility(View.VISIBLE);
                if (button == fulfilledBtn)
                    fulfillRequest();
                else if (button == rejectBtn)
                    dismissRequest();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onBackPressed();
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.old_green));
            }
        });
        dialog.show();
    }

    private void fulfillRequest() {
        HashMap deed = new HashMap();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User Profiles");
        userRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("fulfilled").exists()){
                    int point = Integer.parseInt(snapshot.child("fulfilled").getValue().toString());
                    int newPoint = point + 1;
                    deed.put("fulfilled", newPoint);
                } else {
                    deed.put("fulfilled", 1);
                }
                submitDeed(userRef, deed);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void submitDeed(DatabaseReference userRef, HashMap deed) {
        userRef.child(user.getUid()).updateChildren(deed).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    dismissRequest();
                } else makeToast("Failed to record fulfilled request!");
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void dismissRequest() {
        reqRef.child(Objects.requireNonNull(getIntent().getStringExtra("requestID")))
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    makeToast("Request dismissed!");
                } else {
                    makeToast("Reject request failed!");
                }
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void openReportDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_report_this, null);

        Spinner typeSpinner;
        TextView descET, title;

        title = view.findViewById(R.id.title);
        typeSpinner = view.findViewById(R.id.typeSpinner);
        descET = view.findViewById(R.id.descET);

        title.setText(getString(R.string.report_user_label));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.reportUType, android.R.layout.simple_spinner_item);
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
                    makeToast("Select case type!");
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
        DatabaseReference repRef = FirebaseDatabase.getInstance().getReference()
                .child("Reports").child("Users").child("Active").push();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy h:mm a");
        String dateTime = simpleDateFormat.format(calendar.getTime());

        HashMap reportData = new HashMap();
        reportData.put("reporterID", user.getUid());
        reportData.put("offenderID", getIntent().getStringExtra("requesterID"));
        reportData.put("type", reportType);
        reportData.put("desc", reportDesc);
        reportData.put("offenderContact", contactDetail.getText().toString());
        reportData.put("offenderMessage", messageDisplay.getText().toString());
        reportData.put("created_datetime", dateTime);
        reportData.put("status", "active");

        repRef.setValue(reportData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                   makeToast("Report lodged successfully!");
                }
                else {
                    makeToast("Report failed!");
                }
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void showData() {
        Picasso.get().load(getIntent().getStringExtra("imageUrl")).into(listImage);
        title.setText(getIntent().getStringExtra("title"));
        location.setText(getIntent().getStringExtra("location"));
        tag1.setText(getIntent().getStringExtra("halalTag"));
        tag2.setText(getIntent().getStringExtra("dietTag"));
        tag3.setText(getIntent().getStringExtra("typeTag"));
        contactDetail.setText(getIntent().getStringExtra("contact"));
        messageDisplay.setText(getIntent().getStringExtra("desc"));

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("User Profiles")
                .child(Objects.requireNonNull(getIntent().getStringExtra("requesterID")));
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    requesterDisplay.setText(snapshot.child("firstName").getValue().toString()
                    + " " + snapshot.child("lastName").getValue().toString());
                    Picasso.get().load(snapshot.child("imageUrl").getValue().toString()).into(requesterImage);
                } else {
                    requesterDisplay.setText("Deleted User");
                    makeToast("This user no longer exist!");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setBindings() {
        listImage = findViewById(R.id.listImage);
        requesterImage = findViewById(R.id.requesterImage);
        title = findViewById(R.id.listTitle);
        location = findViewById(R.id.listLocDetail);
        tag1 = findViewById(R.id.listChip1);
        tag2 = findViewById(R.id.listChip2);
        tag3 = findViewById(R.id.listChip3);
        requesterDisplay = findViewById(R.id.requesterDisplay);
        contactDetail = findViewById(R.id.contactDetail);
        messageDisplay = findViewById(R.id.messageDisplay);
        fulfilledBtn = findViewById(R.id.fulfilledBtn);
        rejectBtn = findViewById(R.id.rejectBtn);
        backBtn = findViewById(R.id.backBtn);
        reportBtn = findViewById(R.id.reportBtn);
        loading = findViewById(R.id.loadingBar);
    }

    private void makeToast(String msg){
        Toast.makeText(RequestDetail.this, msg, Toast.LENGTH_SHORT).show();
    }
}