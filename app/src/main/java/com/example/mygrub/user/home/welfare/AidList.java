package com.example.mygrub.user.home.welfare;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygrub.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AidList extends AppCompatActivity {

    private RecyclerView aidView;
    private ArrayList<AidItem> itemList;
    private AidAdapter adapter;
    private Toolbar toolbar;
    private RadioGroup radioGroup;
    private HashMap metadata;
    private boolean childCheck;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welfare_aid_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        aidView = findViewById(R.id.aidView);
        toolbar = findViewById(R.id.toolbar);
        radioGroup = findViewById(R.id.radioGroup);

        toolbar.setTitle(R.string.recommend_aid_label);
        setSupportActionBar(toolbar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        aidView.setLayoutManager(layoutManager);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                if (id == R.id.showAllBtn){
                    filterList("All");
                } else if (id == R.id.showEliBtn){
                    filterList("Eligible");
                } else if (id == R.id.showNonBtn){
                    filterList("Non-Eligible");
                } else makeToast("Radio button error: ID not found.");
            }
        });

        generateMetadata();

        itemList = new ArrayList<>();

        DatabaseReference aidRef = FirebaseDatabase.getInstance().getReference()
                .child("Knowledge Base").child("Programs");

        aidRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot shot : snapshot.getChildren()){
                    AidItem item = shot.getValue(AidItem.class);
                    Objects.requireNonNull(item).setKey(shot.getKey());
                    itemList.add(item);
                }

                //TODO: create custom no match message

//                if (itemList.isEmpty()){
//                    listingView.setVisibility(View.GONE);
//                    emptyMsg.setVisibility(View.VISIBLE);
//                } else {
//                    listingView.setVisibility(View.VISIBLE);
//                    emptyMsg.setVisibility(View.GONE);
//                }

                adapter = new AidAdapter(AidList.this, itemList);
                aidView.setAdapter(adapter);
                adapter.setOnItemClickListener(new AidAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(AidItem item) {
                        copyText(item, "Program Link");
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void copyText(AidItem info, String label) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, info.getLink());
        clipboard.setPrimaryClip(clip);
        makeToast(info.getKey() + " link has been copied!");

        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(info.getLink())));
    }

    private void filterList(String mode) {
        ArrayList<AidItem> filteredList = new ArrayList<>();

        if (mode.equals("All")){

            adapter.setFilteredList(itemList);
            adapter.notifyDataSetChanged();

        } else if (mode.equals("Eligible")){

            for (AidItem item : itemList){
                if (itemContainsAllRules(item.getRules(), metadata)){
                    filteredList.add(item);
                    //emptySearchMsg.setVisibility(View.GONE);
                }
                else adapter.setFilteredList(filteredList);
            }

        } else if (mode.equals("Non-Eligible")){

            for (AidItem item : itemList){
                if (!itemContainsAllRules(item.getRules(), metadata)){
                    filteredList.add(item);
                    //emptySearchMsg.setVisibility(View.GONE);
                }
                else adapter.setFilteredList(filteredList);
            }

        } else makeToast("Filter error: Invalid filter mode!");
    }

    private boolean itemContainsAllRules(Map<String, String> rules, HashMap userData) {
        for (Map.Entry<String, String> rule : rules.entrySet()) {
            String ruleItem = rule.getValue();

            if (!userData.containsValue(ruleItem)) {
                return false;
            }
        }
        return true;
    }

    private void generateMetadata() {
        metadata = new HashMap();
        String dateFormat = "d/M/yyyy";

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("User Profiles").child(user.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Gender group
                String gender = snapshot.child("gender").getValue().toString();
                metadata.put("gender", gender);

                //Race group
                metadata.put("race", snapshot.child("race").getValue().toString());

                //Religion group
                metadata.put("religion", snapshot.child("religion").getValue().toString());

                //Age group
                String birthdate = snapshot.child("birthdate").getValue().toString();
                LocalDate startDate = parseStringToDate(birthdate, dateFormat);
                LocalDate endDate = LocalDate.now();
                int age = calculateYearsDifference(startDate, endDate);

                metadata.put("ageYears", age);

                if (age < 18) metadata.put("age", "Children");
                else if (age >= 18 && age < 60) metadata.put("age", "Adult");
                else metadata.put("age", "Elderly");

                //Dependent related status
                if (snapshot.child("Dependants").exists()) {
                    for (DataSnapshot dependent : snapshot.child("Dependants").getChildren()) {
                        if (dependent.child("relation").getValue().toString().equals("Child")) {
                            childCheck = true;
                            metadata.put("parentalStatus", "Yes");
                            break;
                        }
                    }
                }

                if (!snapshot.child("maritalStatus").exists() ||
                        !snapshot.child("nationality").exists() ||
                        !snapshot.child("state").exists() ||
                        !snapshot.child("employmentStatus").exists() ||
                        !snapshot.child("OKU").exists() ||
                        !snapshot.child("income").exists()) {

                    makeToast("Please complete your profile so a proper recommendation can be generated.");

                } else {

                    // Parenthood style status
                    String marital = snapshot.child("maritalStatus").getValue().toString();
                    metadata.put("maritalStatus", snapshot.child("maritalStatus").getValue().toString());

                    if (childCheck && marital.equals("Single") || childCheck && marital.equals("Divorced") ||
                            childCheck && marital.equals("Widow")) {

                        if (gender.equals("Female")) {
                            metadata.put("parenthood", "Single Mother");
                            metadata.put("parentStatus", "Parent");
                        }
                        else {
                            metadata.put("parenthood", "Single Father");
                            metadata.put("parentStatus", "Parent");
                        }

                    } else metadata.put("parenthood", "Not Parent");

                    // financial background
                    int income = Integer.parseInt(snapshot.child("income").getValue().toString());
                    if (income < 4850) {
                        metadata.put("financeStatus", "B40");
                    } else if (income >= 4850 && income < 10960) {
                        metadata.put("financeStatus", "M40");
                    } else if (income >= 10960) {
                        metadata.put("financeStatus", "T20");
                    }

                    // State
                    metadata.put("state", snapshot.child("state").getValue().toString());

                    // Employment Status
                    metadata.put("employmentStatus", snapshot.child("employmentStatus").getValue().toString());
                    if (!snapshot.child("employmentStatus").getValue().toString().equals("Student"))
                        metadata.put("studentStatus", "Not Student");

                    // Nationality status
                    if (snapshot.child("nationality").getValue().toString().equals("Malaysian")) {
                        metadata.put("nationality", "Malaysian");
                    } else metadata.put("nationality", "Non-Malaysian");

                    // OKU Status
                    if ( snapshot.child("OKU").getValue().toString().equals("Yes"))
                        metadata.put("oku", "OKU");
                    else  metadata.put("oku", "No");
                }

                //TODO: Delete this later after done
//                metadata.forEach(
//                        (key, value)
//                                -> System.out.println(key + " = " + value));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public static LocalDate parseStringToDate(String dateString, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDate.parse(dateString, formatter);
    }

    public static int calculateYearsDifference(LocalDate startDate, LocalDate endDate) {
        Period period = Period.between(startDate, endDate);
        return period.getYears();
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

    private void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}