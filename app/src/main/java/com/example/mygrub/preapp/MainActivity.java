package com.example.mygrub.preapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mygrub.R;
import com.example.mygrub.user.UserMainPage;
import com.example.mygrub.admin.AdminMainPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private ProgressBar loading;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loading = findViewById(R.id.loadingBar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(auth.getCurrentUser() != null){
            loading.setVisibility(View.VISIBLE);
            checkSuspension(auth.getCurrentUser().getUid());
        }
        else {
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        }
    }

    private void checkSuspension(String userID) {
        DatabaseReference modeRef = FirebaseDatabase.getInstance().getReference().child("User Profiles");
        modeRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String check = snapshot.child("suspended").getValue().toString();
                if (check.equals("0")){
                    loading = findViewById(R.id.loadingBar);
                    openModule(userID);
                } else {
                    Toast.makeText(MainActivity.this, "Your account is suspended!", Toast.LENGTH_SHORT).show();
                    auth.signOut();
                    loading.setVisibility(View.GONE);
                    startActivity(new Intent(MainActivity.this, Login.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void openModule(String userID) {
        DatabaseReference modeRef = FirebaseDatabase.getInstance().getReference().child("User Profiles");
        modeRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String check = snapshot.child("privilege").getValue().toString();
                if (check.equals("user")){
                    Intent intent = new Intent (MainActivity.this, UserMainPage.class);
                    intent.putExtra("verify", snapshot.child("verify").getValue().toString());
                    intent.putExtra("imageUrl", snapshot.child("imageUrl").getValue().toString());
                    System.out.println(snapshot.child("imageUrl").getValue().toString());
                    startActivity(intent);
                    finish();
                }
                else if (check.equals("admin")){
                    startActivity(new Intent (MainActivity.this, AdminMainPage.class));
                    finish();
                }
                else {
                    Toast.makeText(MainActivity.this, "Privilege check failed!", Toast.LENGTH_SHORT).show();
                }
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loading.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
