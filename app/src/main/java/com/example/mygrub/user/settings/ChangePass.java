package com.example.mygrub.user.settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePass extends AppCompatActivity {

    private EditText currPassET, newPassET, confirmPassET;
    private String currPass, newPass, confirmPass;
    private Button updateBtn, backBtn;
    private ProgressBar loading;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_setts_cp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        currPassET = findViewById(R.id.currPassET);
        newPassET = findViewById(R.id.newPassET);
        confirmPassET = findViewById(R.id.confirmPassET);
        updateBtn = findViewById(R.id.updateBtn);
        backBtn = findViewById(R.id.backBtn);
        loading = findViewById(R.id.loadingBar);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currPass = currPassET.getText().toString();
                newPass = newPassET.getText().toString();
                confirmPass = confirmPassET.getText().toString();

                if (TextUtils.isEmpty(currPass)) {
                    currPassET.setError("Please enter your old password");
                    currPassET.requestFocus();
                } else if (TextUtils.isEmpty(newPass)) {
                    newPassET.setError("Please enter your new password");
                    newPassET.requestFocus();
                } else if (TextUtils.isEmpty(confirmPass)) {
                    confirmPassET.setError("Please reenter your new password");
                    confirmPassET.requestFocus();
                } else if (newPass.length() < 6) {
                    newPassET.setError("Password cannot be less than 6 characters!");
                    newPassET.requestFocus();
                } else if (!confirmPass.matches(newPass)) {
                    confirmPassET.setError("This doesn't match your new password! Try again.");
                    confirmPassET.requestFocus();
                } else {
                    loading.setVisibility(View.VISIBLE);
                    validateUser(user);
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

    private void validateUser(FirebaseUser user) {
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currPass);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (!newPass.matches(currPass)) {
                        updatePassword(newPass);
                    } else {
                        loading.setVisibility(View.GONE);
                        newPassET.setError("New password cannot be the same as old password! Try again.");
                        newPassET.requestFocus();
                    }
                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        Toast.makeText(ChangePass.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    loading.setVisibility(View.GONE);
                }
            }
        });
    }

    private void updatePassword(String newPass) {
        user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ChangePass.this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        Toast.makeText(ChangePass.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                loading.setVisibility(View.GONE);
            }
        });
    }
}