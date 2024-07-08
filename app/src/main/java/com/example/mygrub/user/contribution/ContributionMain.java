package com.example.mygrub.user.contribution;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mygrub.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ContributionMain extends AppCompatActivity {

    private TextView fulfilledNumber, listingNumber, eventNumber, reportNumber;
    private ImageView backBtn;
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_contribution_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        fulfilledNumber = findViewById(R.id.fulfilledNumber);
        listingNumber = findViewById(R.id.listingNumber);
        eventNumber = findViewById(R.id.eventNumber);
        reportNumber = findViewById(R.id.reportNumber);
        backBtn = findViewById(R.id.backBtn);
        loading = findViewById(R.id.loadingBar);

        loading.setVisibility(View.VISIBLE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User Profiles");
        userRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.child("fulfilled").exists()){
                        fulfilledNumber.setText(snapshot.child("fulfilled").getValue().toString());
                    }

                    if (snapshot.child("listScore").exists()){
                        listingNumber.setText(snapshot.child("listScore").getValue().toString());
                    }

                   if (snapshot.child("eventScore").exists()){
                       eventNumber.setText(snapshot.child("eventScore").getValue().toString());
                   }

                   if (snapshot.child("merits").exists()){
                       reportNumber.setText(snapshot.child("merits").getValue().toString());
                   }

                } else{
                    makeToast("User doesn't exist!");
                }
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void makeToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}