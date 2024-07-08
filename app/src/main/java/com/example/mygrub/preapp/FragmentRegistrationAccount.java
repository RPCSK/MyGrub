package com.example.mygrub.preapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mygrub.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentRegistrationAccount#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentRegistrationAccount extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // ---- VARIABLES----

    private TextInputEditText  emailEdit, passwordEdit, confirmPasswordEdit;
    private String email, password, confirmPassword;
    private Button registerBtn, cancelBtn;
    private ProgressBar loading;

    public FragmentRegistrationAccount() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentRegistrationAccount.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentRegistrationAccount newInstance(String param1, String param2) {
        FragmentRegistrationAccount fragment = new FragmentRegistrationAccount();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preapp_reg_account, container, false);

        emailEdit = view.findViewById(R.id.emailEdit);
        passwordEdit = view.findViewById(R.id.passwordEdit);
        confirmPasswordEdit = view.findViewById(R.id.confirmPasswordEdit);
        registerBtn = view.findViewById(R.id.registerBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);
        loading = view.findViewById(R.id.loadingBar);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailEdit.getText().toString();
                password = passwordEdit.getText().toString();
                confirmPassword = confirmPasswordEdit.getText().toString();

                checkFields(email, password, confirmPassword, emailEdit, passwordEdit, confirmPasswordEdit);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Login.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void checkFields(String email, String password, String confirmPassword,
                             TextInputEditText emailEdit, TextInputEditText passwordEdit,
                             TextInputEditText confirmPasswordEdit){

        emailEdit.setError(null);
        passwordEdit.setError(null);
        confirmPasswordEdit.setError(null);

        if (TextUtils.isEmpty(email)){
            emailEdit.setError("Email cannot be empty!");
            emailEdit.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEdit.setError("Invalid email. Try again.");
            emailEdit.requestFocus();
        } else if (TextUtils.isEmpty(password)){
            passwordEdit.setError("Password cannot be empty!");
            passwordEdit.requestFocus();
        } else if (TextUtils.isEmpty(confirmPassword)){
            confirmPasswordEdit.setError("Reenter your password!");
            confirmPasswordEdit.requestFocus();
        } else if (password.length() < 6){
            passwordEdit.setError("Password must have at least 6 characters.");
            passwordEdit.requestFocus();
        } else if (!confirmPassword.equals(password)){
            confirmPasswordEdit.setError("Your passwords doesn't match. Try again.");
            confirmPasswordEdit.requestFocus();
        } else {
            loading.setVisibility(View.VISIBLE);
            checkAccountExistence(email.toLowerCase().trim(), password);
        }
    }

    private void checkAccountExistence(String email, String password) {
        DatabaseReference emailRefs = FirebaseDatabase.getInstance().getReference().child("User Profiles");
        emailRefs.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    emailEdit.setError("This email has already been registered. Use a different email.");
                    emailEdit.requestFocus();
                    loading.setVisibility(View.GONE);
                } else {
                    Bundle result = new Bundle();
                    result.putString("email", email);
                    result.putString("password", password);

                    Fragment frag = new FragmentRegistrationProfile();
                    frag.setArguments(result);
                    FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                    fm.replace(R.id.fragmentBoxReg, frag).commit();
                    loading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loading.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}