package com.example.mygrub.user.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mygrub.R;
import com.example.mygrub.preapp.FragmentRegistrationMoreProfile;
import com.example.mygrub.preapp.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteAcc extends AppCompatActivity {

    private EditText passET;
    private String pass;
    private Button deleteBtn, backBtn;
    private ProgressBar loading;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_setts_da);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        passET = findViewById(R.id.passET);
        deleteBtn = findViewById(R.id.deleteBtn);
        backBtn = findViewById(R.id.backBtn);
        loading = findViewById(R.id.loadingBar);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pass = passET.getText().toString();
                if (TextUtils.isEmpty(pass)) {
                    passET.setError("Please enter your password");
                    passET.requestFocus();
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
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), pass);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    openDialog();
                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        Toast.makeText(DeleteAcc.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void openDialog() {
        View view = LayoutInflater.from(DeleteAcc.this).inflate(R.layout.dialog_delete_acc, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(DeleteAcc.this);
        builder.setView(view);
        builder.setPositiveButton("Yes, I'm sure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loading.setVisibility(View.VISIBLE);
                deleteData(user);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onBackPressed();
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.ruby_red));
            }
        });
        dialog.show();
    }

    private void deleteData(FirebaseUser user) {
        DatabaseReference accRef = FirebaseDatabase.getInstance().getReference().child("User Profiles");
        accRef.child(user.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    deleteUser(user);
                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        Toast.makeText(DeleteAcc.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void deleteUser(FirebaseUser user) {
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(DeleteAcc.this, "Your account has been deleted!", Toast.LENGTH_SHORT).show();
                    auth.signOut();
                    startActivity(new Intent(DeleteAcc.this, Login.class));
                    finish();
                } else {
                    loading.setVisibility(View.GONE);
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        Toast.makeText(DeleteAcc.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
