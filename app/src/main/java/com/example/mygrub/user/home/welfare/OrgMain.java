package com.example.mygrub.user.home.welfare;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mygrub.R;
import com.example.mygrub.user.UserMainPage;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class OrgMain extends AppCompatActivity {

    private Toolbar toolbar;
    private Spinner areaSpinner, serviceSpinner;
    private TextView areaLabel, serviceLabel;
    private Button recommendBtn, showAllBtn;
    private String area, service;
    private ArrayList<OrgItem> itemList;
    private ProgressBar loading;
    private boolean childCheck, areaCheck, serviceCheck;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welfare_org_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.toolbar);
        areaSpinner = findViewById(R.id.areaSpinner);
        serviceSpinner = findViewById(R.id.serviceSpinner);
        recommendBtn = findViewById(R.id.recommendBtn);
        showAllBtn = findViewById(R.id.showAllBtn);
        areaLabel = findViewById(R.id.areaLabel);
        serviceLabel = findViewById(R.id.serviceLabel);
        loading = findViewById(R.id.loadingBar);

        areaCheck = true;
        serviceCheck = true;

        toolbar.setTitle(R.string.welfare_org_label);
        setSupportActionBar(toolbar);

        setSpinner(areaSpinner, R.array.area);
        setSpinner(serviceSpinner, R.array.services);

        recommendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkFields();
            }
        });

        showAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading.setVisibility(View.VISIBLE);
                createUserMetadata("General");
            }
        });

    }

    private void createUserMetadata(String mode) {
        HashMap metadata = new HashMap();
        HashMap needs = new HashMap();
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

                //Age group
                String birthdate = snapshot.child("birthdate").getValue().toString();
                LocalDate startDate = parseStringToDate(birthdate, dateFormat);
                LocalDate endDate = LocalDate.now();
                int age = calculateYearsDifference(startDate, endDate);

                if (age < 18) metadata.put("age", "Children");
                else if (age >= 18 && age < 60) metadata.put("age", "Adult");
                else metadata.put("age", "Elderly");

                //Dependent related status
                if (snapshot.child("Dependants").exists()) {
                    for (DataSnapshot dependent : snapshot.child("Dependants").getChildren()) {
                        if (dependent.child("relation").getValue().toString().equals("Child")) {
                            childCheck = true;
                            break;
                        }
                    }
                } else metadata.put("parentalStatus", "No");

                if (childCheck) {
                    metadata.put("parentalStatus", "Yes");
                }

                if (!snapshot.child("maritalStatus").exists() ||
                        !snapshot.child("nationality").exists() ||
                        !snapshot.child("OKU").exists() ||
                        !snapshot.child("income").exists()) {

                    makeToast("Please complete your profile so a proper recommendation can be generated.");

                } else {

                    // Parenthood style status
                    String marital = snapshot.child("maritalStatus").getValue().toString();
                    if (childCheck && marital.equals("Single") || childCheck && marital.equals("Divorced") ||
                            childCheck && marital.equals("Widow")) {

                        if (gender.equals("Female")) metadata.put("parenthood", "Single Mother");
                        else metadata.put("parenthood", "Single Father");

                    } else metadata.put("parenthood", "Not Relevant");

                    // financial background
                    int income = Integer.parseInt(snapshot.child("income").getValue().toString());
                    if (income < 4850) {
                        metadata.put("financeStatus", "B40");
                    } else if (income >= 4850 && income < 10960) {
                        metadata.put("financeStatus", "M40");
                    } else if (income >= 10960) {
                        metadata.put("financeStatus", "T20");
                    }

                    // Nationality status
                    if (snapshot.child("nationality").getValue().toString().equals("Malaysian")) {
                        metadata.put("nationality", "Local");
                    } else metadata.put("nationality", "Foreigner");

                    // OKU Status
                    metadata.put("oku", snapshot.child("OKU").getValue().toString());
                }

                if (mode.equals("General")) generateRecommendation(metadata);
                else if (mode.equals("Specifics"))
                {
                   if (!area.equals("Select")) needs.put("area", area);
                   if (!service.equals("Select")) needs.put("service", service);
                   generateRecommendation(metadata, needs);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateRecommendation(HashMap metadata, HashMap needs) {
        itemList = new ArrayList<>();
        Collection<String> values = metadata.values();

        DatabaseReference orgRef = FirebaseDatabase.getInstance().getReference().child("Knowledge Base")
                .child("Organizations");
        orgRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot org : snapshot.getChildren()) {
                    OrgItem item = org.getValue(OrgItem.class);

                    String demoRule = org.child("rules").child("demo").getValue().toString();

                    if (needs.containsKey("area")) {
                        String areaRule = org.child("rules").child("area").getValue().toString();
                        if (needs.get("area").equals(areaRule)) areaCheck = true;
                        else areaCheck = false;
                    }

                    if (needs.containsKey("service")){
                        for (DataSnapshot serviceRule : org.child("rules").child("services").getChildren()){
                            if (serviceRule.getValue().equals(service)){
                                serviceCheck = true;
                                break;
                            }
                            else serviceCheck = false;
                        }
                    }

                    for (String value : values) {
                        if (areaCheck && serviceCheck && demoRule.contains(value) ||
                                areaCheck && serviceCheck && demoRule.equals("Everyone")) {
                            Objects.requireNonNull(item).setKey(org.getKey());
                            itemList.add(item);
                            break;
                        }
                    }
                }

                Intent intent = new Intent(OrgMain.this, OrgList.class);
                intent.putParcelableArrayListExtra("itemList", itemList);
                startActivity(intent);
                finish();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        loading.setVisibility(View.GONE);
    }

    private void generateRecommendation(HashMap metadata) {
        itemList = new ArrayList<>();
        Collection<String> values = metadata.values();

        DatabaseReference orgRef = FirebaseDatabase.getInstance().getReference().child("Knowledge Base")
                .child("Organizations");
        orgRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot org : snapshot.getChildren()) {
                    OrgItem item = org.getValue(OrgItem.class);
                    String demoRule = org.child("rules").child("demo").getValue().toString();

                    for (String value : values) {
                        if (demoRule.contains(value) || demoRule.equals("Everyone")) {
                            Objects.requireNonNull(item).setKey(org.getKey());
                            itemList.add(item);
                            break;
                        }
                    }
                }

                Intent intent = new Intent(OrgMain.this, OrgList.class);
                intent.putParcelableArrayListExtra("itemList", itemList);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        loading.setVisibility(View.GONE);
    }

    public static LocalDate parseStringToDate(String dateString, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDate.parse(dateString, formatter);
    }

    public static int calculateYearsDifference(LocalDate startDate, LocalDate endDate) {
        Period period = Period.between(startDate, endDate);
        return period.getYears();
    }

    private void checkFields() {
        areaLabel.setTextColor(getColor(R.color.old_green));
        serviceLabel.setTextColor(getColor(R.color.old_green));

        if (area.equals("Select") && service.equals("Select")) {
            areaLabel.setTextColor(getColor(R.color.ruby_red));
            serviceLabel.setTextColor(getColor(R.color.ruby_red));
            makeToast("Please fill in at least one field for recommendation!");
        } else {
            loading.setVisibility(View.VISIBLE);
            createUserMetadata("Specifics");;
        }
    }

    private void setSpinner(Spinner spinner, int arrayID) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(OrgMain.this, arrayID, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (spinner.equals(areaSpinner))
                    area = adapterView.getItemAtPosition(position).toString();
                else if (spinner.equals(serviceSpinner))
                    service = adapterView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
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
            getOnBackPressedDispatcher().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void makeToast(String msg) {
        Toast.makeText(OrgMain.this, msg, Toast.LENGTH_SHORT).show();
    }
}