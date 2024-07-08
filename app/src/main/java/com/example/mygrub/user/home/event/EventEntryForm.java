package com.example.mygrub.user.home.event;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mygrub.R;
import com.example.mygrub.user.UserMainPage;
import com.example.mygrub.user.home.food.ListEntryForm;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class EventEntryForm extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView eventImage, qrImage;
    private Uri eventUri, qrUri;
    private DatePickerDialog pickerDialog;
    private TextView addCancelQr, qrLabel, typeLabel;
    private EditText titleET, startDateET, endDateET, descET, contactET, locationET;
    private Spinner typeSpinner;
    private String title, startDate, endDate, type, desc, contact, location, srcID;
    private String username;
    private int qrEnable = 0;
    private String verifyStatus, imageUrl;
    private Button addBtn, backBtn;
    private ProgressBar loading;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_entry);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        eventImage = findViewById(R.id.eventImage);
        qrImage = findViewById(R.id.qrImage);

        titleET = findViewById(R.id.titleET);
        startDateET = findViewById(R.id.startDateET);
        endDateET = findViewById(R.id.endDateET);
        typeSpinner = findViewById(R.id.typeSpinner);
        descET = findViewById(R.id.descET);
        contactET = findViewById(R.id.contactET);
        locationET = findViewById(R.id.locationET);

        addCancelQr = findViewById(R.id.addCancelQr);
        qrLabel = findViewById(R.id.qrLabel);
        typeLabel = findViewById(R.id.typeLabel);
        addBtn = findViewById(R.id.addBtn);
        backBtn = findViewById(R.id.backBtn);
        loading = findViewById(R.id.loadingBar);

        username = getIntent().getStringExtra("username");

        verifyStatus = getIntent().getStringExtra("verify");
        imageUrl = getIntent().getStringExtra("imageUrl");

        startDateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker(startDateET);
            }
        });

        endDateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker(endDateET);
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EventEntryForm.this, R.array.eventType, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
        setListener(typeSpinner);

        addCancelQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (qrEnable == 0) {
                    qrEnable = 1;
                    addCancelQr.setText(getString(R.string.cancel_qr_label));
                    addCancelQr.setTextColor(getColor(R.color.bright_red));
                    qrLabel.setVisibility(View.VISIBLE);
                    qrImage.setVisibility(View.VISIBLE);
                } else if (qrEnable == 1) {
                    qrEnable = 0;
                    addCancelQr.setText(getString(R.string.add_qr_label));
                    addCancelQr.setTextColor(getColor(R.color.bright_blue));
                    qrImage.setImageResource(R.drawable.form_icon_image);
                    qrUri = null;
                    qrLabel.setVisibility(View.GONE);
                    qrImage.setVisibility(View.GONE);
                }
            }
        });

        eventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                srcID = "event";
                openFileChooser();
            }
        });

        qrImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                srcID = "qr";
                openFileChooser();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkFields();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void openDatePicker(EditText field) {
        pickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
                String dateString = format.format(calendar.getTime());
                field.setText(dateString);
            }
        }, 2024, 0, 1);
        pickerDialog.show();
    }

    private void setListener(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                type = adapterView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null
                && data.getData() != null) {
            if (srcID.equals("event")) {
                eventUri = data.getData();
                Picasso.get().load(eventUri).into(eventImage);
            } else if (srcID.equals("qr")) {
                qrUri = data.getData();
                Picasso.get().load(qrUri).into(qrImage);
            } else
                Toast.makeText(EventEntryForm.this, "Unable to identify request source!", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkFields() {
        title = titleET.getText().toString();
        startDate = startDateET.getText().toString();
        endDate = endDateET.getText().toString();
        desc = descET.getText().toString();
        contact = contactET.getText().toString();
        location = locationET.getText().toString();

        titleET.setError(null);
        startDateET.setError(null);
        endDateET.setError(null);
        qrLabel.setTextColor(getColor(R.color.ashen_olive));
        typeLabel.setTextColor(getColor(R.color.ashen_olive));
        descET.setError(null);
        contactET.setError(null);
        locationET.setError(null);

        if (eventUri == null) {
            Toast.makeText(this, "Please select the event's picture.", Toast.LENGTH_SHORT).show();
        } else if (qrEnable == 1 && qrUri == null) {
            qrLabel.setTextColor(getColor(R.color.bright_red));
            Toast.makeText(this, "Please select the QR picture.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(title)) {
            titleET.setError("Title is needed!");
            titleET.requestFocus();
        } else if (TextUtils.isEmpty(startDate)) {
            startDateET.setError("Start date is needed!");
            startDateET.requestFocus();
        } else if (TextUtils.isEmpty(endDate)) {
            endDateET.setError("End date is needed!");
            endDateET.requestFocus();
        } else if (type.equals("Select")) {
            typeLabel.setTextColor(getColor(R.color.bright_red));
            Toast.makeText(this, "Select the event type!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(desc)) {
            descET.setError("Description is needed!");
            descET.requestFocus();
        } else if (TextUtils.isEmpty(contact)) {
            contactET.setError("Contact is needed!");
            contactET.requestFocus();
        } else if (TextUtils.isEmpty(location)) {
            locationET.setError("Location is needed!");
            locationET.requestFocus();
        } else {
            loading.setVisibility(View.VISIBLE);

            HashMap listData = new HashMap();
            insertData(listData);
        }
    }

    private void insertImage(HashMap listData, String listingID, DatabaseReference listRef) {
        StorageReference storeRef = FirebaseStorage.getInstance().getReference()
                .child("events/" + listingID);
        storeRef.putFile(eventUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storeRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        listData.put("imageUrl", uri.toString());
                        if (qrEnable == 1) insertQR(listData, listingID, listRef);
                        else {
                            listRef.setValue(listData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(EventEntryForm.this, "Event Uploaded!", Toast.LENGTH_SHORT).show();
                                        checkEventScore();
                                    } else {
                                        Toast.makeText(EventEntryForm.this, "Event Insertion Failed!", Toast.LENGTH_SHORT).show();
                                        loading.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EventEntryForm.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void insertQR(HashMap listData, String listingID, DatabaseReference listRef) {
        StorageReference storeRef = FirebaseStorage.getInstance().getReference()
                .child("events/qr/" + listingID);
        storeRef.putFile(qrUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storeRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        listData.put("qrUrl", uri.toString());

                        listRef.setValue(listData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(EventEntryForm.this, "Event Uploaded!", Toast.LENGTH_SHORT).show();
                                    checkEventScore();
                                } else {
                                    Toast.makeText(EventEntryForm.this, "Event Insertion Failed!", Toast.LENGTH_SHORT).show();
                                    loading.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EventEntryForm.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void checkEventScore() {
        HashMap deed = new HashMap();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User Profiles");
        userRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("eventScore").exists()){
                    int point = Integer.parseInt(snapshot.child("eventScore").getValue().toString());
                    int newPoint = point + 1;
                    deed.put("eventScore", newPoint);
                } else {
                    deed.put("eventScore", 1);
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
                    Intent intent = new Intent(EventEntryForm.this, UserMainPage.class);
                    intent.putExtra("verify", verifyStatus);
                    intent.putExtra("imageUrl", imageUrl);
                    startActivity(intent);
                    finish();
                } else makeToast("Failed to record fulfilled request!");
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void insertData(HashMap listData) {
        DatabaseReference listRef = FirebaseDatabase.getInstance().getReference().child("Events")
                .push();

        String listingID = listRef.getKey().toString();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy h:mm a");
        String dateTime = simpleDateFormat.format(calendar.getTime());

        listData.put("title", title);
        listData.put("startDate", startDate);
        listData.put("endDate", endDate);
        listData.put("type", type);
        listData.put("qrEnabled", String.valueOf(qrEnable));
        listData.put("desc", desc);
        listData.put("contact", contact);
        listData.put("location", location);
        listData.put("userID", user.getUid());
        listData.put("status", "active");
        listData.put("username", username);
        listData.put("created_datetime", dateTime);

        insertImage(listData, listingID, listRef);
    }

    public void makeToast(String msg){
        Toast.makeText(EventEntryForm.this, msg, Toast.LENGTH_SHORT).show();
    }
}