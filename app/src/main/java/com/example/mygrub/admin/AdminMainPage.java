package com.example.mygrub.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygrub.R;
import com.example.mygrub.admin.faq.FAQAdmin;
import com.example.mygrub.admin.knowledge.KnowledgeMain;
import com.example.mygrub.admin.reports.ReportMain;
import com.example.mygrub.admin.reports.events.FragmentEventReport;
import com.example.mygrub.admin.reports.lists.FragmentListingReport;
import com.example.mygrub.admin.reports.users.FragmentUserReport;
import com.example.mygrub.admin.settings.ChangePassA;
import com.example.mygrub.admin.suspensions.SuspensionsMain;
import com.example.mygrub.admin.verifications.VerificationMain;
import com.example.mygrub.preapp.Login;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminMainPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Toolbar toolbar;
    private TextView totalUserNumber, verificationNumber, listingRNumber, eventRNumber, userRNumber;
    private int defaultCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main_page);

        drawerLayout = findViewById(R.id.drawerLayout);
        navView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        totalUserNumber = findViewById(R.id.totalNumber);
        verificationNumber = findViewById(R.id.verificationNumber);
        userRNumber = findViewById(R.id.usersReportNumber);
        listingRNumber = findViewById(R.id.listingReportNumber);
        eventRNumber = findViewById(R.id.eventReportNumber);

        setClick(totalUserNumber);
        setClick(verificationNumber);
        setClick(userRNumber);
        setClick(listingRNumber);
        setClick(eventRNumber);

        showNumbers();

        navView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(this);
        navView.setCheckedItem(R.id.drawer_home);
    }

    private void setClick(TextView cardText) {
        cardText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMainPage.this, ReportMain.class);
                if (cardText.getId() == totalUserNumber.getId()){
                    startActivity(new Intent(AdminMainPage.this, SuspensionsMain.class));
                } else if (cardText.getId() == verificationNumber.getId()){
                    startActivity(new Intent(AdminMainPage.this, VerificationMain.class));
                } else if (cardText.getId() == userRNumber.getId()){
                    intent.putExtra("id", R.id.userReportFragment);
                    startActivity(intent);
                } else if (cardText.getId() == listingRNumber.getId()){
                    intent.putExtra("id", R.id.listingReportFragment);
                    startActivity(intent);
                } else if (cardText.getId() == eventRNumber.getId()){
                    intent.putExtra("id", R.id.eventReportFragment);
                    startActivity(intent);
                }
            }
        });
    }

    private void showNumbers() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User Profiles");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    long count = snapshot.getChildrenCount();
                    totalUserNumber.setText(String.valueOf(count));
                } else totalUserNumber.setText(String.valueOf(defaultCount));

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference verifyRef = FirebaseDatabase.getInstance().getReference().child("Verification Requests");
        verifyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    long count = snapshot.getChildrenCount();
                    verificationNumber.setText(String.valueOf(count));
                } else verificationNumber.setText(String.valueOf(defaultCount));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Reports");
        usersRef.child("Users").child("Active").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    long count = snapshot.getChildrenCount();
                    userRNumber.setText(String.valueOf(count));
                } else userRNumber.setText(String.valueOf(defaultCount));

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference listRef = FirebaseDatabase.getInstance().getReference().child("Reports");
        listRef.child("Listings").child("Active").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    long count = snapshot.getChildrenCount();
                    listingRNumber.setText(String.valueOf(count));
                } else listingRNumber.setText(String.valueOf(defaultCount));

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("Reports");
        eventRef.child("Events").child("Active").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    long count = snapshot.getChildrenCount();
                    eventRNumber.setText(String.valueOf(count));
                } else eventRNumber.setText(String.valueOf(defaultCount));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        if (item.getItemId() == R.id.drawer_home) startActivity(new Intent(AdminMainPage.this, AdminMainPage.class));
        else if (item.getItemId() == R.id.drawer_reports) startActivity(new Intent(AdminMainPage.this, ReportMain.class));
        else if (item.getItemId() == R.id.drawer_verification) startActivity(new Intent(AdminMainPage.this, VerificationMain.class));
        else if (item.getItemId() == R.id.drawer_suspensions) startActivity(new Intent(AdminMainPage.this, SuspensionsMain.class));
        //TODO: Undo this later
//        else if (item.getItemId() == R.id.drawer_knowledge) startActivity(new Intent(AdminMainPage.this, KnowledgeMain.class));
        else if (item.getItemId() == R.id.drawer_faq) startActivity(new Intent(AdminMainPage.this, FAQAdmin.class));
        else if (item.getItemId() == R.id.drawer_settings) startActivity(new Intent(AdminMainPage.this, ChangePassA.class));
        else if (item.getItemId() == R.id.drawer_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(AdminMainPage.this, Login.class));
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}