package com.example.mygrub.admin.suspensions;

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
import com.example.mygrub.admin.reports.lists.FragmentListingReport;
import com.example.mygrub.admin.reports.users.FragmentUserReport;
import com.example.mygrub.admin.suspensions.all.FragmentAllUsers;
import com.example.mygrub.admin.suspensions.suspended.FragmentSuspendedUsers;

public class SuspensionsMain extends AppCompatActivity {

    private Toolbar toolbar;
    private Button allBtn, suspendedBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_suspensions_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.toolbar);
        allBtn = findViewById(R.id.allUsersBtn);
        suspendedBtn = findViewById(R.id.suspendedUsersBtn);

        toolbar.setTitle(R.string.suspensions_label);
        setSupportActionBar(toolbar);

        allBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allBtn.setBackgroundTintList(getColorStateList(R.color.white));
                allBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.tiramisu_orange));
                suspendedBtn.setBackgroundTintList(getColorStateList(R.color.grey));
                suspendedBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_orange_brown));
                replaceFragment(new FragmentAllUsers());
            }
        });

        suspendedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                suspendedBtn.setBackgroundTintList(getColorStateList(R.color.white));
                suspendedBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.tiramisu_orange));
                allBtn.setBackgroundTintList(getColorStateList(R.color.grey));
                allBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warm_orange_brown));
                replaceFragment(new FragmentSuspendedUsers());
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

    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmentUsersContainer, fragment).commit();
    }
}