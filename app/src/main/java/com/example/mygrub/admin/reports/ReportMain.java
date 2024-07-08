package com.example.mygrub.admin.reports;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mygrub.R;
import com.example.mygrub.admin.reports.events.FragmentEventReport;
import com.example.mygrub.admin.reports.lists.FragmentListingReport;
import com.example.mygrub.admin.reports.users.FragmentUserReport;

public class ReportMain extends AppCompatActivity {
    private Toolbar toolbar;
    private Button userBtn, listingBtn, eventBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_report_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.toolbar);
        userBtn = findViewById(R.id.userBtn);
        listingBtn = findViewById(R.id.listingBtn);
        eventBtn = findViewById(R.id.eventBtn);

        toolbar.setTitle(R.string.reports_label);
        setSupportActionBar(toolbar);

        loadFragment();

        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userBtn.setBackgroundTintList(getColorStateList(R.color.white));
                userBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.tiramisu_orange));
                listingBtn.setBackgroundTintList(getColorStateList(R.color.grey));
                listingBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_orange_brown));
                eventBtn.setBackgroundTintList(getColorStateList(R.color.grey));
                eventBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_orange_brown));
                replaceFragment(new FragmentUserReport());
            }
        });

        listingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listingBtn.setBackgroundTintList(getColorStateList(R.color.white));
                listingBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.tiramisu_orange));
                userBtn.setBackgroundTintList(getColorStateList(R.color.grey));
                userBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_orange_brown));
                eventBtn.setBackgroundTintList(getColorStateList(R.color.grey));
                eventBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_orange_brown));
                replaceFragment(new FragmentListingReport());
            }
        });

        eventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventBtn.setBackgroundTintList(getColorStateList(R.color.white));
                eventBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.tiramisu_orange));
                listingBtn.setBackgroundTintList(getColorStateList(R.color.grey));
                listingBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_orange_brown));
                userBtn.setBackgroundTintList(getColorStateList(R.color.grey));
                userBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_orange_brown));
                replaceFragment(new FragmentEventReport());
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
        if (item.getItemId() == R.id.back_icon){
            getOnBackPressedDispatcher().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFragment() {
        int intentFragment = getIntent().getIntExtra("id", 0);

        if (intentFragment == R.id.userReportFragment) {
            userBtn.setBackgroundTintList(getColorStateList(R.color.white));
            userBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.tiramisu_orange));
            listingBtn.setBackgroundTintList(getColorStateList(R.color.grey));
            listingBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_orange_brown));
            eventBtn.setBackgroundTintList(getColorStateList(R.color.grey));
            eventBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_orange_brown));
            replaceFragment(new FragmentUserReport());
        } else if (intentFragment == R.id.listingReportFragment) {
            listingBtn.setBackgroundTintList(getColorStateList(R.color.white));
            listingBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.tiramisu_orange));
            userBtn.setBackgroundTintList(getColorStateList(R.color.grey));
            userBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_orange_brown));
            eventBtn.setBackgroundTintList(getColorStateList(R.color.grey));
            eventBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_orange_brown));
            replaceFragment(new FragmentListingReport());
        } else if (intentFragment == R.id.eventReportFragment) {
            eventBtn.setBackgroundTintList(getColorStateList(R.color.white));
            eventBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.tiramisu_orange));
            listingBtn.setBackgroundTintList(getColorStateList(R.color.grey));
            listingBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_orange_brown));
            userBtn.setBackgroundTintList(getColorStateList(R.color.grey));
            userBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_orange_brown));
            replaceFragment(new FragmentEventReport());
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmentReportsContainer, fragment).commit();
    }
}