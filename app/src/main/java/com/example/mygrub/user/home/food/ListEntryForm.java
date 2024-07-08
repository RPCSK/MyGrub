package com.example.mygrub.user.home.food;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mygrub.user.UserMainPage;
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
import com.squareup.picasso.*;

import com.example.mygrub.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ListEntryForm extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView listImage;
    private Uri imageUri;
    private EditText titleET, quantityET, unitET, descET, contactET, locationET;
    private String title, quantity, unit, desc, contact, location;
    private ChipGroup halalGroup, dietGroup, typeGroup;
    private ArrayList<Integer> checkedDiet, checkedType;
    private TextView halalLabel, dietLabel, typeLabel;
    private String username;
    private Button addBtn, backBtn;
    private String verifyStatus, imageUrl;
    private ProgressBar loading;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_food_entry);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        titleET = findViewById(R.id.titleET);
        quantityET = findViewById(R.id.quantityET);
        unitET = findViewById(R.id.unitET);
        descET = findViewById(R.id.descET);
        contactET = findViewById(R.id.contactET);
        locationET = findViewById(R.id.locationET);

        halalLabel = findViewById(R.id.halalLabel);
        dietLabel = findViewById(R.id.dietLabel);
        typeLabel = findViewById(R.id.typeLabel);

        halalGroup = findViewById(R.id.halalGroup);
        dietGroup = findViewById(R.id.dietGroup);
        typeGroup = findViewById(R.id.typeGroup);

        addBtn = findViewById(R.id.addBtn);
        backBtn = findViewById(R.id.backBtn);
        loading = findViewById(R.id.loadingBar);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        verifyStatus = getIntent().getStringExtra("verify");
        imageUrl = getIntent().getStringExtra("imageUrl");

        listImage = findViewById(R.id.listImage);
        listImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        halalLabel.setTextColor(getColor(R.color.old_green));
        dietLabel.setTextColor(getColor(R.color.old_green));
        typeLabel.setTextColor(getColor(R.color.old_green));

        if (imageUri == null) {
            Toast.makeText(this, "Please select the food's picture.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(title)) {
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
        } else if (halalGroup.getCheckedChipId() == -1) {
            halalLabel.setTextColor(getColor(R.color.ruby_red));
            makeToast("Select at least 1 relevant halal tag!");
            halalLabel.requestFocus();
        } else if (checkedDiet.isEmpty()) {
            dietLabel.setTextColor(getColor(R.color.ruby_red));
            makeToast("Select at least 1 relevant diet tag!");
            dietLabel.requestFocus();
        } else if (checkedType.isEmpty()) {
            typeLabel.setTextColor(getColor(R.color.ruby_red));
            makeToast("Select at least 1 relevant food tag!");
            typeLabel.requestFocus();
        } else {
            loading.setVisibility(View.VISIBLE);

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

            HashMap listData = new HashMap();
            listData.put("title", title);
            listData.put("quantity", quantity);
            listData.put("unit", unit);
            listData.put("desc", desc);
            listData.put("contact", contact);
            listData.put("location", location);
            listData.put("tags", tags);
            listData.put("userID", user.getUid());
            listData.put("status", "Open");
            listData.put("username", username);
            insertData(listData);

        }
    }

    private void insertImage(HashMap listData, String listingID, DatabaseReference listRef) {
        StorageReference storeRef = FirebaseStorage.getInstance().getReference()
                .child("listings/" + listingID);
        storeRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storeRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy h:mm a");
                        String dateTime = simpleDateFormat.format(calendar.getTime());

                        listData.put("imageUrl", uri.toString());
                        listData.put("created_datetime", dateTime);

                        listRef.setValue(listData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    makeToast("Listing Uploaded!");
                                    checkListScore();
                                } else {
                                    makeToast("Data Insertion Failed!");
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
                Toast.makeText(ListEntryForm.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void checkListScore() {
        HashMap deed = new HashMap();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User Profiles");
        userRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("listScore").exists()){
                    int point = Integer.parseInt(snapshot.child("listScore").getValue().toString());
                    int newPoint = point + 1;
                    deed.put("listScore", newPoint);
                } else {
                    deed.put("listScore", 1);
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
                    Intent intent = new Intent(ListEntryForm.this, UserMainPage.class);
                    intent.putExtra("verify", verifyStatus);
                    intent.putExtra("imageUrl", imageUrl);
                    startActivity(intent);
                    finish();
                } else makeToast("Failed to record list deed!");
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void insertData(HashMap listData) {
        DatabaseReference listRef = FirebaseDatabase.getInstance().getReference().child("Listings")
                .push();

        String listingID = listRef.getKey().toString();
        insertImage(listData, listingID, listRef);
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

    public void makeToast(String msg){
        Toast.makeText(ListEntryForm.this, msg, Toast.LENGTH_SHORT).show();
    }
}