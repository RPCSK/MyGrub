package com.example.mygrub.user.settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mygrub.R;
import com.example.mygrub.user.RWDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ChangeEmail extends AppCompatActivity {

    private EditText currentEmailET, newEmailET, passwordET;
    private Button updateBtn, backBtn;
    private String newEmail, oldEmail, password;
    private ProgressBar loading;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_setts_ce);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        currentEmailET = findViewById(R.id.currEmailET);
        newEmailET = findViewById(R.id.newEmailET);
        passwordET = findViewById(R.id.passET);
        updateBtn = findViewById(R.id.updateBtn);
        backBtn = findViewById(R.id.backBtn);
        loading = findViewById(R.id.loadingBar);

        if (user == null){
            Toast.makeText(ChangeEmail.this, "Invalid user session!",Toast.LENGTH_SHORT).show();
        }
        else {
            showEmailDetail(user);
        }

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newEmail = newEmailET.getText().toString();
                password = passwordET.getText().toString();

                if (TextUtils.isEmpty(newEmail)){
                    newEmailET.setError("Enter the new email address here!");
                    newEmailET.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()){
                    newEmailET.setError("Invalid email. Try again.");
                    newEmailET.requestFocus();
                }
                else if (TextUtils.isEmpty(password)){
                    passwordET.setError("Enter password here!");
                    passwordET.requestFocus();
                }
                else if (password.length() < 6){
                    passwordET.setError("Password should have 6 or more characters!");
                    passwordET.requestFocus();
                }
                else {
                    loading.setVisibility(View.VISIBLE);
                    validateChange(user, oldEmail, newEmail, password);
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    private void showEmailDetail(FirebaseUser user) {
        String uid = user.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User Profiles");
        ref.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RWDetails readParentDetails = snapshot.getValue(RWDetails.class);
                if (readParentDetails != null) {
                    String email = readParentDetails.email;
                    currentEmailET.setText(email);
                    oldEmail = email;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChangeEmail.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validateChange(FirebaseUser user, String oldEmail, String newEmail, String password) {
        AuthCredential credential = EmailAuthProvider.getCredential(oldEmail, password);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    updateEmail(user, newEmail);
                }
                else {
                    loading.setVisibility(View.GONE);
                    passwordET.setError("Wrong password! Try again.");
                    passwordET.requestFocus();
                }

            }
        });
    }

    private void updateEmail(FirebaseUser user, String newEmail) {
        user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    user.sendEmailVerification();
                    updateAccDatabase(user, newEmail);
                    Toast.makeText(ChangeEmail.this, "Email updated! Please verify your new email.", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        throw task.getException();
                    }
                    catch (Exception e){
                        Toast.makeText(ChangeEmail.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void updateAccDatabase(FirebaseUser user, String newEmail) {
        String uid = user.getUid();
        HashMap newEmailDetail = new HashMap();
        newEmailDetail.put("email", newEmail.toLowerCase().trim());

        DatabaseReference emailRef = FirebaseDatabase.getInstance().getReference().child("User Profiles");

        emailRef.child(uid).updateChildren(newEmailDetail).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    onBackPressed();
                } else{
                    try {
                        throw task.getException();
                    } catch (Exception e){
                        Toast.makeText(ChangeEmail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                loading.setVisibility(View.GONE);
            }
        });
    }
}