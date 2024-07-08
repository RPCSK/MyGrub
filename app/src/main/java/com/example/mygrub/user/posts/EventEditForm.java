package com.example.mygrub.user.posts;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mygrub.R;
import com.example.mygrub.user.RWDetails;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class EventEditForm extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView eventImage, qrImage;
    private Uri eventUri, qrUri;
    private DatePickerDialog pickerDialog;
    private TextView addCancelQr, qrLabel, typeLabel, restoreQr;
    private EditText titleET, startDateET, endDateET, descET, contactET, locationET;
    private Spinner typeSpinner;
    private String title, startDate, endDate, type, desc, contact, location, srcID, eventID, qrUrl,
            oriQrUrl, imageUrl;
    private String qrEnable = "0";
    private String oriQREnable;
    private Button updateBtn, cancelBtn, deleteBtn;
    private ProgressBar loading;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("Events");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_event_edit);
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
        restoreQr = findViewById(R.id.restoreQR);
        qrLabel = findViewById(R.id.qrLabel);
        typeLabel = findViewById(R.id.typeLabel);
        updateBtn = findViewById(R.id.updateBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        loading = findViewById(R.id.loadingBar);

        eventID = getIntent().getStringExtra("eventID");
        type = getIntent().getStringExtra("type");
        imageUrl = getIntent().getStringExtra("imageUrl");
        eventUri = Uri.parse(imageUrl);

        if (getIntent().getStringExtra("qrEnabled").equals("1")) {
            oriQrUrl = qrUrl = getIntent().getStringExtra("qrUrl");
            qrUri = Uri.parse(qrUrl);
        }

        showDetails();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EventEditForm.this, R.array.eventType, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
        setListener(typeSpinner);
        typeSpinner.setSelection(((ArrayAdapter) typeSpinner.getAdapter()).getPosition(type));

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

        addCancelQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (qrEnable.equals("0") && oriQREnable.equals("0")) {
                    qrEnable = "1";
                    addCancelQr.setText(getString(R.string.cancel_qr_label));
                    addCancelQr.setTextColor(getColor(R.color.bright_red));
                    qrLabel.setVisibility(View.VISIBLE);
                    qrImage.setVisibility(View.VISIBLE);
                } else if (qrEnable.equals("0") && oriQREnable.equals("1")) {
                    qrEnable = "1";
                    addCancelQr.setText(getString(R.string.cancel_qr_label));
                    addCancelQr.setTextColor(getColor(R.color.bright_red));
                    restoreQr.setVisibility(View.VISIBLE);
                    qrLabel.setVisibility(View.VISIBLE);
                    qrImage.setVisibility(View.VISIBLE);
                } else if (qrEnable.equals("1") && oriQREnable.equals("0")) {
                    qrEnable = "0";
                    addCancelQr.setText(getString(R.string.add_qr_label));
                    addCancelQr.setTextColor(getColor(R.color.bright_blue));
                    qrImage.setImageResource(R.drawable.form_icon_image);
                    qrUri = null;
                    qrLabel.setVisibility(View.GONE);
                    qrImage.setVisibility(View.GONE);
                } else if (qrEnable.equals("1") && oriQREnable.equals("1")) {
                    qrEnable = "0";
                    addCancelQr.setText(getString(R.string.add_qr_label));
                    addCancelQr.setTextColor(getColor(R.color.bright_blue));
                    qrImage.setImageResource(R.drawable.form_icon_image);
                    qrUrl = "";
                    qrUri = null;
                    restoreQr.setVisibility(View.GONE);
                    qrLabel.setVisibility(View.GONE);
                    qrImage.setVisibility(View.GONE);
                }
            }
        });

        restoreQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrUrl = oriQrUrl;
                Picasso.get().load(qrUrl).into(qrImage);
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

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkFields();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
    }

    private void openDialog() {
        View view = LayoutInflater.from(EventEditForm.this).inflate(R.layout.dialog_confirm_delete, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(EventEditForm.this);

        TextView deleteTitle = view.findViewById(R.id.deleteTitle);
        deleteTitle.setText(titleET.getText().toString());

        builder.setView(view);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteEvent();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loading.setVisibility(View.GONE);
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void deleteEvent() {
        if (oriQREnable.equals("1")){
            StorageReference storeRef = FirebaseStorage.getInstance().getReferenceFromUrl(oriQrUrl);
            storeRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                }
            });
        }
        StorageReference storeRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
        storeRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
            }
        });
        eventRef.child(eventID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(EventEditForm.this, "Event deleted!", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    Toast.makeText(EventEditForm.this, "Event failed to delete!", Toast.LENGTH_SHORT).show();
                }
                loading.setVisibility(View.GONE);
            }
        });
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

        if (qrEnable.equals("1") && oriQREnable.equals("0") && qrUri == null) {
            qrLabel.setTextColor(getColor(R.color.bright_red));
            Toast.makeText(this, "Please select the QR picture.", Toast.LENGTH_SHORT).show();
        } else if (qrEnable.equals("1") && oriQREnable.equals("1") && qrUri == null && qrUrl.isEmpty()) {
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

            HashMap eventData = new HashMap();

            if (!imageUrl.equals(eventUri.toString())) { // if image change, insert new image else...
                updateImage(eventData);
            } else if (qrEnable.equals("1") && oriQREnable.equals("0") || qrEnable.equals("1") && !qrUrl.equals(qrUri.toString())) { // for qr change, else...
                updateQr(eventData);
            } else if (qrEnable.equals("0") && oriQREnable.equals("1") && qrUrl.isEmpty() && qrUri == null) // for qr removal, else...
                removeQr(eventData);
            else updateData(eventData);
        }
    }

    private void updateImage(HashMap eventData) {
        StorageReference storeRef = FirebaseStorage.getInstance().getReference()
                .child("events/" + eventID);
        storeRef.putFile(eventUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storeRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        eventData.put("imageUrl", uri.toString());
                        if (qrEnable.equals("1") && oriQREnable.equals("0") || qrEnable.equals("1") && !qrUrl.equals(qrUri.toString())) {
                            updateQr(eventData);
                        } else if (qrEnable.equals("0") && oriQREnable.equals("1") && qrUrl.isEmpty() && qrUri == null) // for qr removal, else...
                            removeQr(eventData);
                        else updateData(eventData);
                    }
                });
            }
        });
    }

    private void updateQr(HashMap eventData) {
        StorageReference storeRef = FirebaseStorage.getInstance().getReference()
                .child("events/qr/" + eventID);
        storeRef.putFile(qrUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storeRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        eventData.put("qrUrl", uri.toString());
                        if (qrEnable.equals("0") && oriQREnable.equals("1") && qrUrl.isEmpty() && qrUri == null) // for qr removal, else...
                            removeQr(eventData);
                        else updateData(eventData);
                    }
                });
            }
        });
    }

    private void removeQr(HashMap eventData) {
        StorageReference storeRef = FirebaseStorage.getInstance().getReferenceFromUrl(oriQrUrl);
        storeRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                eventRef.child(eventID).child("qrEnabled").setValue("0").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        updateData(eventData);
                    }
                });
            }
        });
    }

    private void updateData(HashMap eventData) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy h:mm a");
        String dateTime = simpleDateFormat.format(calendar.getTime());

        eventData.put("title", title);
        eventData.put("startDate", startDate);
        eventData.put("endDate", endDate);
        eventData.put("type", type);
        eventData.put("qrEnabled", String.valueOf(qrEnable));
        eventData.put("desc", desc);
        eventData.put("contact", contact);
        eventData.put("location", location);
        eventData.put("userID", user.getUid());
        eventData.put("status", "active");
        eventData.put("created_datetime", dateTime);

        eventRef.child(eventID).updateChildren(eventData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EventEditForm.this, "Event successfully updated!", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    Toast.makeText(EventEditForm.this, "Event update failed!", Toast.LENGTH_SHORT).show();
                    loading.setVisibility(View.GONE);
                }
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
                Toast.makeText(EventEditForm.this, "Unable to identify request source!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDetails() {
        eventRef.child(eventID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RWDetails eventReader = snapshot.getValue(RWDetails.class);
                Picasso.get().load(eventReader.imageUrl).into(eventImage);
                titleET.setText(eventReader.title);
                startDateET.setText(eventReader.startDate);
                endDateET.setText(eventReader.endDate);
                titleET.setText(eventReader.title);
                descET.setText(eventReader.desc);
                locationET.setText(eventReader.location);
                contactET.setText(eventReader.contact);

                if (eventReader.qrEnabled.equals("1")) {
                    oriQREnable = "1";
                    qrEnable = "1";
                    qrUrl = eventReader.qrUrl;
                    addCancelQr.setText(getString(R.string.cancel_qr_label));
                    addCancelQr.setTextColor(getColor(R.color.bright_red));
                    qrLabel.setVisibility(View.VISIBLE);
                    qrImage.setVisibility(View.VISIBLE);
                    Picasso.get().load(qrUrl).into(qrImage);
                } else oriQREnable = "0";

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}