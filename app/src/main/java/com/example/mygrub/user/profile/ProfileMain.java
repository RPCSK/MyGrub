package com.example.mygrub.user.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mygrub.R;
import com.example.mygrub.user.UserMainPage;
import com.example.mygrub.user.home.food.FragmentHome;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileMain extends AppCompatActivity {

    private ImageView backBtn;
    private Button profileBtn, dependantsBtn;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ProfileMain.this, UserMainPage.class);
                DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference()
                        .child("User Profiles").child(user.getUid());
                profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        intent.putExtra("imageUrl", snapshot.child("imageUrl").getValue().toString());
                        intent.putExtra("verify", snapshot.child("verify").getValue().toString());
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                finish();
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);

        backBtn = findViewById(R.id.backBtn);
        profileBtn = findViewById(R.id.profileBtn);
        dependantsBtn = findViewById(R.id.dependantsBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dependantsBtn.setBackgroundTintList(getColorStateList(R.color.grey));
                dependantsBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_mint));
                profileBtn.setBackgroundTintList(getColorStateList(R.color.white));
                profileBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.old_green));
                replaceFragment(new FragmentProfile());
            }
        });

        dependantsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dependantsBtn.setBackgroundTintList(getColorStateList(R.color.white));
                dependantsBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.old_green));
                profileBtn.setBackgroundTintList(getColorStateList(R.color.grey));
                profileBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_mint));
                replaceFragment(new FragmentDependants());
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmentProfileContainer, fragment).commit();
    }
}