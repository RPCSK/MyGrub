package com.example.mygrub.user.posts;

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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mygrub.R;
import com.example.mygrub.preapp.FragmentRegistrationMoreProfile;
import com.example.mygrub.user.RWDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ListEditForm extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView listImage;
    private Uri imageUri;
    private EditText titleET, quantityET, unitET, descET, contactET, locationET;
    private String title, quantity, unit, desc, contact, location, status;
    private ChipGroup halalGroup, dietGroup, typeGroup;
    private ArrayList<Integer> checkedDiet, checkedType;
    private Spinner statusSpinner;
    private TextView halalLabel, dietLabel, typeLabel, statusLabel;
    private String listingID, imageUrl;
    private Button doneBtn, cancelBtn, deleteBtn;
    private ProgressBar loading;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    private DatabaseReference listRef = FirebaseDatabase.getInstance().getReference()
            .child("Listings");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_list_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        statusSpinner = findViewById(R.id.statusSpinner);
        titleET = findViewById(R.id.titleET);
        quantityET = findViewById(R.id.quantityET);
        unitET = findViewById(R.id.unitET);
        descET = findViewById(R.id.descET);
        contactET = findViewById(R.id.contactET);
        locationET = findViewById(R.id.locationET);

        statusLabel = findViewById(R.id.statusLabel);
        halalLabel = findViewById(R.id.halalLabel);
        dietLabel = findViewById(R.id.dietLabel);
        typeLabel = findViewById(R.id.typeLabel);

        halalGroup = findViewById(R.id.halalGroup);
        dietGroup = findViewById(R.id.dietGroup);
        typeGroup = findViewById(R.id.typeGroup);

        doneBtn = findViewById(R.id.doneBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        loading = findViewById(R.id.loadingBar);

        listImage = findViewById(R.id.listImage);

        listingID = getIntent().getStringExtra("listingID").toString();
        status = getIntent().getStringExtra("status").toString();
        imageUrl = getIntent().getStringExtra("imageUrl").toString();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (ListEditForm.this, R.array.list_status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);
        setListener(statusSpinner);
        statusSpinner.setSelection(((ArrayAdapter)statusSpinner.getAdapter()).getPosition(status));

        showData();

        listImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
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
                loading.setVisibility(View.VISIBLE);
                openDialog();
            }
        });
    }

    private void openDialog() {
        View view = LayoutInflater.from(ListEditForm.this).inflate(R.layout.dialog_confirm_delete, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ListEditForm.this);

        TextView deleteTitle = view.findViewById(R.id.deleteTitle);
        deleteTitle.setText(titleET.getText().toString());

        builder.setView(view);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteListing();
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

    private void deleteListing() {
        listRef.child(listingID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    StorageReference storeRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                    storeRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(ListEditForm.this, "Listing deleted!", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    });
                } else {
                    Toast.makeText(ListEditForm.this, "Listing failed to delete!", Toast.LENGTH_SHORT).show();
                }
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void showData() {
        listRef.child(listingID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RWDetails listReader = snapshot.getValue(RWDetails.class);
                Picasso.get().load(listReader.imageUrl).into(listImage);
                titleET.setText(listReader.title);
                quantityET.setText(listReader.quantity);
                unitET.setText(listReader.unit);
                descET.setText(listReader.desc);
                locationET.setText(listReader.location);
                contactET.setText(listReader.contact);

                for (DataSnapshot data : snapshot.child("tags").getChildren()){
                    for (int i = 0; i < halalGroup.getChildCount(); i++) {
                        Chip chip = (Chip) halalGroup.getChildAt(i);
                        if (chip != null && chip.getText().toString().equals(data.getValue().toString())) {
                            chip.setChecked(true);
                            break;
                        }
                    }
                    for (int i = 0; i < dietGroup.getChildCount(); i++) {
                        Chip chip = (Chip) dietGroup.getChildAt(i);
                        if (chip != null && chip.getText().toString().equals(data.getValue().toString())) {
                            chip.setChecked(true);
                        }
                    }
                    for (int i = 0; i < typeGroup.getChildCount(); i++) {
                        Chip chip = (Chip) typeGroup.getChildAt(i);
                        if (chip != null && chip.getText().toString().equals(data.getValue().toString())) {
                            chip.setChecked(true);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setListener(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                status = adapterView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void checkFields() {
        title = titleET.getText().toString();
        quantity = quantityET.getText().toString();
        unit = unitET.getText().toString();
        desc = descET.getText().toString();
        contact = contactET.getText().toString();
        location = locationET.getText().toString();
        checkedDiet = (ArrayList<Integer>) dietGroup.getCheckedChipIds();
        checkedType = (ArrayList<Integer>) typeGroup.getCheckedChipIds();

        titleET.setError(null);
        quantityET.setError(null);
        unitET.setError(null);
        descET.setError(null);
        contactET.setError(null);
        locationET.setError(null);
        statusLabel.setTextColor(getColor(R.color.old_green));
        halalLabel.setTextColor(getColor(R.color.old_green));
        dietLabel.setTextColor(getColor(R.color.old_green));
        typeLabel.setTextColor(getColor(R.color.old_green));

        if (TextUtils.isEmpty(title)) {
            titleET.setError("Title is needed!");
            titleET.requestFocus();
        } else if (TextUtils.isEmpty(quantity)) {
            quantityET.setError("Quantity is needed!");
            quantityET.requestFocus();
        } else if (TextUtils.isEmpty(unit)) {
            unitET.setError("Unit is needed!");
            unitET.requestFocus();
        } else if (TextUtils.isEmpty(desc)) {
            descET.setError("Description is needed!");
            descET.requestFocus();
        } else if (TextUtils.isEmpty(contact)) {
            contactET.setError("Contact is needed!");
            contactET.requestFocus();
        } else if (TextUtils.isEmpty(location)) {
            locationET.setError("Location is needed!");
            locationET.requestFocus();
        } else if (status.equals("Select")) {
            statusLabel.setTextColor(getColor(R.color.ruby_red));
            statusLabel.requestFocus();
            Toast.makeText(ListEditForm.this, "Please select listing status", Toast.LENGTH_SHORT).show();
        } else if (halalGroup.getCheckedChipId() == -1) {
            halalLabel.setTextColor(getColor(R.color.ruby_red));
            Toast.makeText(this, "Select at least 1 relevant halal tag!", Toast.LENGTH_SHORT).show();
            halalLabel.requestFocus();
        } else if (checkedDiet.isEmpty()) {
            dietLabel.setTextColor(getColor(R.color.ruby_red));
            Toast.makeText(this, "Select at least 1 relevant diet tag!", Toast.LENGTH_SHORT).show();
            dietLabel.requestFocus();
        } else if (checkedType.isEmpty()) {
            typeLabel.setTextColor(getColor(R.color.ruby_red));
            Toast.makeText(this, "Select at least 1 relevant food tag!", Toast.LENGTH_SHORT).show();
            typeLabel.requestFocus();
        } else {
            loading.setVisibility(View.VISIBLE);

            HashMap listData = new HashMap();

            if (imageUri !=  null) insertImage(listData);
            else insertData(listData);

        }
    }
    private void insertImage(HashMap listData) {
        StorageReference storeRef = FirebaseStorage.getInstance().getReference()
                .child("listings/" + listingID);
        storeRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storeRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        listData.put("imageUrl", uri.toString());
                        insertData(listData);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ListEditForm.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void insertData(HashMap listData) {
        DatabaseReference listRef = FirebaseDatabase.getInstance().getReference()
                .child("Listings").child(listingID);

        HashMap tags = new HashMap();

        Chip halalChip = findViewById(halalGroup.getCheckedChipId());
        tags.put("halalTag", halalChip.getText().toString());

        for (int i = 0; i < checkedDiet.size(); i++) {
            Chip chip = findViewById(checkedDiet.get(i));
            tags.put("dietTag" + i, chip.getText().toString());
        }
        for (int i = 0; i < checkedType.size(); i++) {
            Chip chip = findViewById(checkedType.get(i));
            tags.put("typeTag" + i, chip.getText().toString());
        }

        listData.put("title", title);
        listData.put("quantity", quantity);
        listData.put("unit", unit);
        listData.put("desc", desc);
        listData.put("contact", contact);
        listData.put("location", location);
        listData.put("tags", tags);
        listData.put("userID", user.getUid());
        listData.put("status", status);

        listRef.updateChildren(listData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ListEditForm.this, "Listing updated!", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }

                else {
                    Toast.makeText(ListEditForm.this, "Data Update Failed!", Toast.LENGTH_SHORT).show();
                    loading.setVisibility(View.GONE);
                }
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
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(listImage);
        }
    }
}