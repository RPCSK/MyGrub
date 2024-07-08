package com.example.mygrub.user.posts;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mygrub.R;
import com.example.mygrub.user.profile.FragmentDependants;
import com.example.mygrub.user.profile.FragmentProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MyPostsMain extends AppCompatActivity {

    private ImageView backBtn;
    private Button listingsBtn, eventsBtn, requestsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_posts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backBtn = findViewById(R.id.backBtn);
        listingsBtn = findViewById(R.id.listingsBtn);
        eventsBtn = findViewById(R.id.eventsBtn);
        requestsBtn = findViewById(R.id.requestsBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        listingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventsBtn.setBackgroundTintList(getColorStateList(R.color.grey));
                eventsBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_mint));
                listingsBtn.setBackgroundTintList(getColorStateList(R.color.white));
                listingsBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.old_green));
                requestsBtn.setBackgroundTintList(getColorStateList(R.color.grey));
                requestsBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_mint));
                replaceFragment(new FragmentListPosts());
            }
        });

        eventsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventsBtn.setBackgroundTintList(getColorStateList(R.color.white));
                eventsBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.old_green));
                listingsBtn.setBackgroundTintList(getColorStateList(R.color.grey));
                listingsBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_mint));
                requestsBtn.setBackgroundTintList(getColorStateList(R.color.grey));
                requestsBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_mint));
                replaceFragment(new FragmentEventPosts());
            }
        });

        requestsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventsBtn.setBackgroundTintList(getColorStateList(R.color.grey));
                eventsBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_mint));
                listingsBtn.setBackgroundTintList(getColorStateList(R.color.grey));
                listingsBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_mint));
                requestsBtn.setBackgroundTintList(getColorStateList(R.color.white));
                requestsBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.old_green));
                replaceFragment(new FragmentRequests());
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmentMyPostsView, fragment).commit();
    }
}