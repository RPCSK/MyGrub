package com.example.mygrub.preapp;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mygrub.R;
import com.example.mygrub.user.UserMainPage;
import com.example.mygrub.user.home.food.ListEntryForm;
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
import com.squareup.picasso.Picasso;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentRegistrationProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentRegistrationProfile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //----Variables-----
    private DatePickerDialog pickerDialog;
    private TextInputEditText firstNameEdit, lastNameEdit, bdEdit;
    private Spinner genderSpinner, raceSpinner, religionSpinner;
    private Button submitBtn, backBtn;
    private static final int PICK_IMAGE_REQUEST = 1;
    private String email, password, firstName, lastName, birthdate;
    private Uri imageUri;
    private ImageView profilePic;
    private ProgressBar loading;
    private String gender = "Select", race = "Select", religion = "Select";
    private static final String TAG = "Register";

    public FragmentRegistrationProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentRegistrationProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentRegistrationProfile newInstance(String param1, String param2) {
        FragmentRegistrationProfile fragment = new FragmentRegistrationProfile();
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
        View view = inflater.inflate(R.layout.fragment_preapp_reg_profile, container, false);

        loading = view.findViewById(R.id.loadingBar);
        firstNameEdit = view.findViewById(R.id.fnEdit);
        lastNameEdit = view.findViewById(R.id.lnEdit);
        bdEdit = view.findViewById(R.id.bdEdit);
        genderSpinner = view.findViewById(R.id.genderSpinner);
        raceSpinner = view.findViewById(R.id.raceSpinner);
        religionSpinner = view.findViewById(R.id.religionSpinner);
        profilePic = view.findViewById(R.id.profilePic);
        submitBtn = view.findViewById(R.id.submitBtn);
        backBtn = view.findViewById(R.id.backBtn);

        Bundle result = getArguments();
        email = result.getString("email");
        password = result.getString("password");

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        bdEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker();
            }
        });
        
        setSpinner(genderSpinner, R.array.gender);
        setSpinner(raceSpinner, R.array.race);
        setSpinner(religionSpinner, R.array.religion);

        

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstName = firstNameEdit.getText().toString();
                lastName = lastNameEdit.getText().toString();
                birthdate = bdEdit.getText().toString();
                checkFields();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment frag = new FragmentRegistrationAccount();
                FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                fm.replace(R.id.fragmentBoxReg, frag).commit();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void setSpinner(Spinner spinner, int arrayID) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), arrayID, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        setListener(spinner);
    }
    
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null
                && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(profilePic);
        }
    }

    private void checkFields() {
        firstNameEdit.setError(null);
        lastNameEdit.setError(null);
        bdEdit.setError(null);

        if (imageUri == null){
            Toast.makeText(getActivity(), "Please choose a picture for your profile.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(firstName)) {
            firstNameEdit.setError("First name is required!");
            firstNameEdit.requestFocus();
        } else if (TextUtils.isEmpty(lastName)) {
            lastNameEdit.setError("Last name is required!");
            lastNameEdit.requestFocus();
        } else if (TextUtils.isEmpty(birthdate)) {
            bdEdit.setError("Select your birth date!");
            bdEdit.requestFocus();
        } else if (gender.equals("Select")) {
            Toast.makeText(getActivity(), "Select your gender!", Toast.LENGTH_SHORT).show();
        } else if (race.equals("Select")) {
            Toast.makeText(getActivity(), "Select your race!", Toast.LENGTH_SHORT).show();
        } else if (religion.equals("Select")) {
            Toast.makeText(getActivity(), "Select your religion!", Toast.LENGTH_SHORT).show();
        } else {
            openDialog();
        }

    }

    private void openDialog() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_register_wall, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(view);
        builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Bundle result = new Bundle();
                result.putString("email", email);
                result.putString("password", password);
                result.putString("firstName", firstName);
                result.putString("lastName", lastName);
                result.putString("birthdate", birthdate);
                result.putString("gender", gender);
                result.putString("religion", religion);
                result.putString("race", race);
                result.putString("imageUri", String.valueOf(imageUri));

                Fragment frag = new FragmentRegistrationMoreProfile();
                frag.setArguments(result);
                FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                fm.replace(R.id.fragmentBoxReg, frag).commit();
            }
        }).setNegativeButton("Skip", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loading.setVisibility(View.VISIBLE);
                registerAccount();
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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

    @NonNull
    private HashMap getHashMap() {
        HashMap profileData = new HashMap();
        profileData.put("firstName", firstName);
        profileData.put("lastName", lastName);
        profileData.put("birthdate", birthdate);
        profileData.put("gender", gender);
        profileData.put("race", race);
        profileData.put("merits", 0);
        profileData.put("demerits", 0);
        profileData.put("religion", religion);
        profileData.put("suspended", "0");
        profileData.put("verify", "0");
        profileData.put("email", email);
        profileData.put("privilege", "user");
        return profileData;
    }

    private void insertImage(String userId, HashMap profileData) {
        StorageReference storeRef = FirebaseStorage.getInstance().getReference()
                .child("profiles/").child(userId);
        storeRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storeRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                      profileData.put("imageUrl", uri.toString());
                      profileData.put("imageName", userId);
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

    private void setListener(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (spinner == genderSpinner)
                gender = adapterView.getItemAtPosition(position).toString();
                else if (spinner == raceSpinner)
                    race = adapterView.getItemAtPosition(position).toString();
                else if (spinner == religionSpinner)
                    religion = adapterView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void openDatePicker() {
        pickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                bdEdit.setText(String.valueOf(day) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year));
            }
        }, 2024, 0, 1);
        pickerDialog.show();
    }
}