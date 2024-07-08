package com.example.mygrub.preapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.mygrub.R;
import com.example.mygrub.admin.AdminMainPage;
import com.example.mygrub.user.UserMainPage;
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

public class Login extends AppCompatActivity {

    private TextView here, forgotPassword;
    private TextInputEditText emailEdit, passwordEdit;
    private ProgressBar loading;
    private String email, password;
    private static final String TAG = "MainActivity";
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user;

    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preapp_login);

        loading = findViewById(R.id.loadingBar);
        emailEdit = findViewById(R.id.emailEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        forgotPassword = findViewById(R.id.forgotPass);
        login = findViewById(R.id.loginBtn);
        here = findViewById(R.id.here);

        here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, ForgotPassword.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailEdit.getText().toString().toLowerCase().trim();
                password = passwordEdit.getText().toString();

                checkFields(emailEdit, passwordEdit);
            }
        });

    }
    private void checkFields(TextInputEditText emailEdit, TextInputEditText passwordEdit) {
        emailEdit.setError(null);
        passwordEdit.setError(null);

        if (TextUtils.isEmpty(email)){
            emailEdit.setError("Enter your email here.");
            emailEdit.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEdit.setError("Invalid email. Try again.");
            emailEdit.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            passwordEdit.setError("Enter your password here");
            passwordEdit.requestFocus();
        } else {
            loading.setVisibility(View.VISIBLE);
            validateCredentials(email, password, emailEdit, passwordEdit);
        }
    }

    private void validateCredentials(String email, String password, TextInputEditText emailEdit, TextInputEditText passwordEdit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    user = auth.getCurrentUser();
                    String userID = user.getUid();
                    checkSuspension(userID);

                } else {
                    try {
                        throw task.getException();
                    }
                    catch (FirebaseAuthInvalidCredentialsException e){
                        emailEdit.setError("Wrong email or password. Try again.");
                        emailEdit.requestFocus();
                    }
                    catch (FirebaseAuthInvalidUserException e){
                        emailEdit.setError("User doesn't exist!");
                        emailEdit.requestFocus();
                    }
                    catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    loading.setVisibility(View.GONE);
                }
            }
        });
    }

    private void checkSuspension(String userID) {
        DatabaseReference modeRef = FirebaseDatabase.getInstance().getReference().child("User Profiles");
        modeRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String check = snapshot.child("suspended").getValue().toString();
                if (check.equals("0")){
                    loading = findViewById(R.id.loadingBar);
                    loading.setVisibility(View.VISIBLE);
                    openModule(userID);
                } else {
                    loading.setVisibility(View.GONE);
                    Toast.makeText(Login.this, "Your account is suspended!", Toast.LENGTH_SHORT).show();
                    auth.signOut();
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
                    Intent intent = new Intent (Login.this, UserMainPage.class);
                    intent.putExtra("verify", snapshot.child("verify").getValue().toString());
                    intent.putExtra("imageUrl", snapshot.child("imageUrl").getValue().toString());
                    startActivity(intent);
                    finish();
                }
                else if (check.equals("admin")){
                    startActivity(new Intent (Login.this, AdminMainPage.class));
                    finish();
                }
                else {
                    Toast.makeText(Login.this, "Privilege check failed!", Toast.LENGTH_SHORT).show();
                }
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loading.setVisibility(View.GONE);
                Toast.makeText(Login.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}