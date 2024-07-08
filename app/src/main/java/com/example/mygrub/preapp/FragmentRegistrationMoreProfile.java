package com.example.mygrub.preapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mygrub.R;
import com.example.mygrub.user.UserMainPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentRegistrationMoreProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentRegistrationMoreProfile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //----Variables-----
    private TextInputEditText address1Edit, address2Edit, townEdit, postcodeEdit, orgEdit, incomeEdit;
    private Spinner stateSpinner, nationalitySpinner, maritalSpinner, okuSpinner, employmentSpinner;
    private Button submitBtn, backBtn;
    private String email, password, firstName, lastName, birthdate, gender, address1, address2, town,
            postcode, state, nationality, maritalStatus, oku, employmentStatus, organization, income,
            imageUri, race, religion;
    private ProgressBar loading;
    private static final String TAG = "Register";

    public FragmentRegistrationMoreProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentRegistrationMoreProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentRegistrationMoreProfile newInstance(String param1, String param2) {
        FragmentRegistrationMoreProfile fragment = new FragmentRegistrationMoreProfile();
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
        View view = inflater.inflate(R.layout.fragment_preapp_reg_more_profile, container, false);

        loading = view.findViewById(R.id.loadingBar);
        address1Edit = view.findViewById(R.id.address1Edit);
        address2Edit = view.findViewById(R.id.address2Edit);
        townEdit = view.findViewById(R.id.townEdit);
        postcodeEdit = view.findViewById(R.id.postcodeEdit);
        orgEdit = view.findViewById(R.id.orgEdit);
        incomeEdit = view.findViewById(R.id.incomeEdit);
        submitBtn = view.findViewById(R.id.submitBtn);
        backBtn = view.findViewById(R.id.backBtn);

        stateSpinner = view.findViewById(R.id.stateSpinner);
        nationalitySpinner = view.findViewById(R.id.nationalitySpinner);
        okuSpinner = view.findViewById(R.id.okuSpinner);
        employmentSpinner = view.findViewById(R.id.employmentSpinner);
        maritalSpinner = view.findViewById(R.id.maritalSpinner);

        Bundle result = getArguments();
        email = result.getString("email");
        password = result.getString("password");
        firstName = result.getString("firstName");
        lastName = result.getString("lastName");
        birthdate = result.getString("birthdate");
        gender = result.getString("gender");
        imageUri = result.getString("imageUri");
        race = result.getString("race");
        religion = result.getString("religion");

        state = nationality = maritalStatus = oku = employmentStatus = "Select";

        setSpinner(stateSpinner, R.array.state);
        setSpinner(nationalitySpinner, R.array.nationality);
        setSpinner(maritalSpinner, R.array.marital);
        setSpinner(okuSpinner, R.array.oku);
        setSpinner(employmentSpinner, R.array.employment);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                address1 = address1Edit.getText().toString();
                address2 = address2Edit.getText().toString();
                town = townEdit.getText().toString();
                postcode = postcodeEdit.getText().toString();
                organization = orgEdit.getText().toString();
                income = incomeEdit.getText().toString();
                checkFields();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment frag = new FragmentRegistrationProfile();
                FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                fm.replace(R.id.fragmentBoxReg, frag).commit();
            }
        });

        return view;
    }

    private void setSpinner(Spinner spinner, int itemArrayID) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), itemArrayID, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (spinner.equals(stateSpinner))
                    state = adapterView.getItemAtPosition(position).toString();
                else if (spinner.equals(nationalitySpinner))
                    nationality = adapterView.getItemAtPosition(position).toString();
                else if (spinner.equals(maritalSpinner))
                    maritalStatus = adapterView.getItemAtPosition(position).toString();
                else if (spinner.equals(okuSpinner))
                    oku = adapterView.getItemAtPosition(position).toString();
                else if (spinner.equals(employmentSpinner))
                    employmentStatus = adapterView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void checkFields() {
        address1Edit.setError(null);
        address2Edit.setError(null);
        postcodeEdit.setError(null);
        townEdit.setError(null);
        orgEdit.setError(null);
        incomeEdit.setError(null);

        if (TextUtils.isEmpty(address1)) {
            address1Edit.setError("Address is needed!");
            address1Edit.requestFocus();
        } else if (TextUtils.isEmpty(postcode)) {
            postcodeEdit.setError("Postcode is needed!");
            postcodeEdit.requestFocus();
        } else if (TextUtils.isEmpty(town)) {
            townEdit.setError("Town is needed!");
            townEdit.requestFocus();
        } else if (state.equals("Select")) {
            Toast.makeText(getActivity(), "Select your state!", Toast.LENGTH_SHORT).show();
        } else if (nationality.equals("Select")) {
            Toast.makeText(getActivity(), "Select your nationality!", Toast.LENGTH_SHORT).show();
        } else if (maritalStatus.equals("Select")) {
            Toast.makeText(getActivity(), "Select your marital status!", Toast.LENGTH_SHORT).show();
        } else if (oku.equals("Select")) {
            Toast.makeText(getActivity(), "Select your OKU status!", Toast.LENGTH_SHORT).show();
        } else if (employmentStatus.equals("Select")) {
            Toast.makeText(getActivity(), "Select your employment status!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(organization)) {
            orgEdit.setError("Organization is needed! (Write 'None' if not relevant.");
            orgEdit.requestFocus();
        } else if (TextUtils.isEmpty(income)) {
            incomeEdit.setError("Income is needed!");
            incomeEdit.requestFocus();
        } else {
            loading.setVisibility(View.VISIBLE);
            registerAccount();
        }
    }

    private void registerAccount() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    String userId = firebaseUser.getUid();

                    HashMap profileData = getHashMap();
                    insertImage(userId, profileData);

                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        loading.setVisibility(View.GONE);
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void insertImage(String userId, HashMap profileData) {
        StorageReference storeRef = FirebaseStorage.getInstance().getReference()
                .child("profiles/" + userId);
        storeRef.putFile(Uri.parse(imageUri)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storeRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        profileData.put("imageUrl", uri.toString());
                        createProfile(userId, profileData);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Upload Failed!", Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void createProfile(String userId, HashMap profileData) {
        DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference().child("User Profiles");
        profileRef.child(userId).setValue(profileData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(getActivity(), UserMainPage.class);
                    intent.putExtra("imageUrl", profileData.get("imageUrl").toString());
                    intent.putExtra("verify", "0");
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), "ProfileMain creation failed!", Toast.LENGTH_SHORT).show();
                }
                loading.setVisibility(View.GONE);
            }
        });
    }

    @NonNull
    private HashMap getHashMap() {
        HashMap profileData = new HashMap();
        profileData.put("firstName", firstName);
        profileData.put("lastName", lastName);
        profileData.put("birthdate", birthdate);
        profileData.put("gender", gender);
        profileData.put("address1", address1);
        profileData.put("address2", address2);
        profileData.put("postcode", postcode);
        profileData.put("town", town);
        profileData.put("religion", religion);
        profileData.put("race", race);
        profileData.put("merits", 0);
        profileData.put("demerits", 0);
        profileData.put("state", state);
        profileData.put("nationality", nationality);
        profileData.put("maritalStatus", maritalStatus);
        profileData.put("OKU", oku);
        profileData.put("employmentStatus", employmentStatus);
        profileData.put("organization", organization);
        profileData.put("income", income);
        profileData.put("suspended", "0");
        profileData.put("verify", "0");
        profileData.put("email", email);
        profileData.put("privilege", "user");
        return profileData;
    }
}