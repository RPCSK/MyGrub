package com.example.mygrub.user.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mygrub.R;

public class Settings extends AppCompatActivity {

    private ImageView backBtn;
    private TextView changeEmail, changePass, deleteAcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_setts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backBtn = findViewById(R.id.backBtn);
        changeEmail = findViewById(R.id.changeEmailTxt);
        changePass = findViewById(R.id.changePassTxt);
        deleteAcc = findViewById(R.id.deleteAccTxt);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        setListener(changeEmail, new Intent(Settings.this, ChangeEmail.class));
        setListener(changePass, new Intent(Settings.this, ChangePass.class));
        setListener(deleteAcc, new Intent(Settings.this, DeleteAcc.class));
    }

    private void setListener(TextView textBtn, Intent intent) {
        textBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });
    }
}