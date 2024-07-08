package com.example.mygrub.preapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import com.example.mygrub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPassword extends AppCompatActivity {

    private EditText inputEmail;
    private Button resetPasswordBtn, backBtn;
    private ProgressBar loading;
    private FirebaseAuth auth;
    private final static String TAG = "ForgotPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_preapp_forgot);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputEmail = findViewById(R.id.emailET);
        resetPasswordBtn = findViewById(R.id.resetBtn);
        backBtn = findViewById(R.id.backBtn);
        loading = findViewById(R.id.loadingBar);

        resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = inputEmail.getText().toString();

                if (TextUtils.isEmpty(email)){
                    inputEmail.setError("Email cannot be empty!");
                    inputEmail.requestFocus();
                }
                else {
                    loading.setVisibility(View.VISIBLE);
                    resetPassword(email);
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

    private void resetPassword(String email) {
        auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ForgotPassword.this, "The reset password link has been sent to your inbox!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ForgotPassword.this, Login.class));
                    finish();
                }
                else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e){
                        inputEmail.setError("User does not exist!");
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(ForgotPassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
                loading.setVisibility(View.GONE);
            }
        });
    }
}