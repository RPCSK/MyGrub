package com.example.mygrub.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygrub.R;
import com.example.mygrub.preapp.Login;
import com.example.mygrub.user.contribution.ContributionMain;
import com.example.mygrub.user.faq.FAQMain;
import com.example.mygrub.user.home.event.EventEntryForm;
import com.example.mygrub.user.home.food.ListEntryForm;
import com.example.mygrub.user.home.event.FragmentEvent;
import com.example.mygrub.user.home.food.FragmentHome;
import com.example.mygrub.user.home.welfare.FragmentWelfare;
import com.example.mygrub.user.posts.MyPostsMain;
import com.example.mygrub.user.profile.ProfileMain;
import com.example.mygrub.user.settings.Settings;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UserMainPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Toolbar toolbar;
    private boolean isExpanded = false;
    private FloatingActionButton fab, addListBtn, postEventBtn;
    private TextView addListLabel, postEventLabel;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private BottomNavigationView bottomNav;
    private Intent listIntent, eventIntent;
    private String verifyStatus, imageUrl;
    private Animation rotateClock, rotateAnti, fromBottom, toBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main_page);

        drawerLayout = findViewById(R.id.drawerLayout);
        navView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        bottomNav = findViewById(R.id.bottom_nav);
        fab = findViewById(R.id.fabBtn);
        addListBtn = findViewById(R.id.addListBtn);
        postEventBtn = findViewById(R.id.postEventBtn);
        addListLabel = findViewById(R.id.addListingLabel);
        postEventLabel = findViewById(R.id.postEventLabel);

        rotateClock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_clockwise);
        rotateAnti = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_anticlock);
        fromBottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_from_bottom);
        toBottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_to_bottom);

        verifyStatus = getIntent().getStringExtra("verify");
        imageUrl = getIntent().getStringExtra("imageUrl");

        if (verifyStatus.equals("0")){
            postEventBtn.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.grey));
            postEventBtn.setImageDrawable(getDrawable(R.drawable.floating_icon_locked));
            postEventLabel.setVisibility(View.GONE);
        }

        setSupportActionBar(toolbar);


        listIntent = new Intent(UserMainPage.this, ListEntryForm.class);
        eventIntent = new Intent(UserMainPage.this, EventEntryForm.class);

        listIntent.putExtra("verify", verifyStatus);
        listIntent.putExtra("imageUrl", imageUrl);

        eventIntent.putExtra("verify", verifyStatus);
        eventIntent.putExtra("imageUrl", imageUrl);

        navView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(this);
        navView.setCheckedItem(R.id.drawer_home);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isExpanded){
                    shrinkFab();
                } else expandFab();
            }
        });

        addListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shrinkFab();
                startActivity(listIntent);
            }
        });

        postEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyStatus.equals("1")){
                    shrinkFab();
                    startActivity(eventIntent);
                }
               else Toast.makeText(UserMainPage.this, "You must be verified to access this!", Toast.LENGTH_SHORT).show();
            }
        });

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home) replaceFragment(new FragmentHome());
            else if (item.getItemId() == R.id.bottom_event) replaceFragment(new FragmentEvent());
            else if (item.getItemId() == R.id.bottom_welfare) replaceFragment(new FragmentWelfare());
            return true;
        });

        FirebaseUser user = auth.getCurrentUser();

        if (user == null){
            Toast.makeText(UserMainPage.this, "Invalid user session!",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserMainPage.this, Login.class));
            finish();
        }
        else {
            showProfileName(user.getUid());
        }

    }

    private void expandFab() {
        fab.startAnimation(rotateClock);
        addListBtn.startAnimation(fromBottom);
        postEventBtn.startAnimation(fromBottom);
        addListLabel.startAnimation(fromBottom);
        postEventLabel.startAnimation(fromBottom);
        isExpanded = true;
    }

    private void shrinkFab() {
        fab.startAnimation(rotateAnti);
        addListBtn.startAnimation(toBottom);
        postEventBtn.startAnimation(toBottom);
        addListLabel.startAnimation(toBottom);
        postEventLabel.startAnimation(toBottom);
        isExpanded = false;
    }

    private void showProfileName(String uid) {
        View header = navView.getHeaderView(0);
        TextView name = header.findViewById(R.id.first_name);
        ImageView pic = header.findViewById(R.id.header_profile_pic);
        Picasso.get().load(imageUrl).into(pic);
        DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference().child("User Profiles");
        profileRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    name.setText(snapshot.child("firstName").getValue().toString());
                    String username = snapshot.child("firstName").getValue().toString() + " " +
                            snapshot.child("lastName").getValue().toString();
                    listIntent.putExtra("username", username);
                    eventIntent.putExtra("username", username);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmentContainerMain, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START);
        else if (isExpanded == true) shrinkFab();
        else super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.drawer_home);
        else if (item.getItemId() == R.id.drawer_profile) startProfileIntent();
        else if (item.getItemId() == R.id.drawer_myPosts) startActivity(new Intent(UserMainPage.this, MyPostsMain.class));
        else if (item.getItemId() == R.id.drawer_myContributions) startActivity(new Intent(UserMainPage.this, ContributionMain.class));
        else if (item.getItemId() == R.id.drawer_settings) startActivity(new Intent(UserMainPage.this, Settings.class));
        else if (item.getItemId() == R.id.drawer_faq) startActivity(new Intent(UserMainPage.this, FAQMain.class));
        else if (item.getItemId() == R.id.drawer_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(UserMainPage.this, Login.class));
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startProfileIntent() {
        Intent intent = new Intent(UserMainPage.this, ProfileMain.class);
        DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference()
                .child("User Profiles").child(user.getUid());
        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RWDetails readData = snapshot.getValue(RWDetails.class);

                intent.putExtra("imageUrl", readData.imageUrl);
                intent.putExtra("verify", readData.verify);
                intent.putExtra("firstName", readData.firstName);
                intent.putExtra("lastName", readData.lastName);
                intent.putExtra("birthdate", readData.birthdate);
                intent.putExtra("gender", readData.gender);
                intent.putExtra("nationality", readData.nationality);
                intent.putExtra("marital", readData.maritalStatus);
                intent.putExtra("oku", readData.OKU);
                intent.putExtra("address1", readData.address1);
                intent.putExtra("address2", readData.address2);
                intent.putExtra("postcode", readData.postcode);
                intent.putExtra("town", readData.town);
                intent.putExtra("state", readData.state);
                intent.putExtra("employment", readData.employmentStatus);
                intent.putExtra("organization", readData.organization);
                intent.putExtra("income", readData.income);
                intent.putExtra("race", readData.race);
                intent.putExtra("religion", readData.religion);

                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}