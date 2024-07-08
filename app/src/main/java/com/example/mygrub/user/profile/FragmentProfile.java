package com.example.mygrub.user.profile;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygrub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentProfile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //---VARIABLES----
    private ImageView profileImage;
    private static final int PICK_IMAGE_REQUEST = 1;
    private TextView firstNameLabel, lastNameLabel, birthdateLabel, genderLabel, nationalityLabel,
            maritalLabel, okuLabel, address1Label, address2Label, postcodeLabel, townLabel, stateLabel,
            employmentLabel, organizationLabel, incomeLabel, raceLabel, religionLabel;
    private TextView verifyDisplay, editMainLabel, doneMainLabel, editSecondLabel, doneSecondLabel,
            editThirdLabel, doneThirdLabel;
    private EditText firstNameET, lastNameET, birthdateET, address1ET, address2ET, postcodeET, townET,
            organizationET, incomeET;
    private Spinner genderSpinner, raceSpinner, religionSpinner,
            maritalSpinner, nationalitySpinner, okuSpinner, stateSpinner,
            employmentSpinner;
    private String imageUrl, firstName, lastName, birthdate, gender, marital, nationality,
            oku, verify, address1, address2, postcode, town, state, employment, organization, income,
            race, religion;
    private String newFirstName, newLastName, newBirthdate, newAddress1, newAddress2, newPostcode,
            newTown, newOrganization, newIncome, newRace, newReligion;
    private String newGender, newMarital, newNationality, newOku, newState, newEmployment;
    private Uri imageUri;
    private Button verifyBtn;
    private DatePickerDialog pickerDialog;
    private ProgressBar loading;
    private int editMainMode = 0;
    private int editSecondMode = 0;
    private int editThirdMode = 0;
    private int changePic = 0;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public FragmentProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentProfile newInstance(String param1, String param2) {
        FragmentProfile fragment = new FragmentProfile();
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
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        setBindings(view);

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        genderSpinner.setEnabled(false);
        raceSpinner.setEnabled(false);
        religionSpinner.setEnabled(false);
        nationalitySpinner.setEnabled(false);
        maritalSpinner.setEnabled(false);
        okuSpinner.setEnabled(false);
        stateSpinner.setEnabled(false);
        employmentSpinner.setEnabled(false);

        newNationality = newMarital = newOku = newState = newEmployment
                = newRace = newReligion = "Select";

        showProfileInfo();

        setAdapter(genderSpinner, R.array.gender);
        setAdapter(raceSpinner, R.array.race);
        setAdapter(religionSpinner, R.array.religion);
        setAdapter(nationalitySpinner, R.array.nationality);
        setAdapter(maritalSpinner, R.array.marital);
        setAdapter(okuSpinner, R.array.oku);
        setAdapter(stateSpinner, R.array.state);
        setAdapter(employmentSpinner, R.array.employment);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editMainMode == 1) openFileChooser();
            }
        });

        birthdateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker();
            }
        });

        editMainLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editMainMode == 0) {
                    enableMainEdit();
                } else {
                    restoreMainDataDisplay();
                    resetMainFieldsError();
                    disableMainEdit();
                }
            }
        });

        doneMainLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkMainFields();
            }
        });

        editSecondLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editSecondMode == 0) {
                    enableSecondEdit();
                } else {
                    restoreSecondDataDisplay();
                    resetSecondFieldsError();
                    disableSecondEdit();
                }
            }
        });

        doneSecondLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkSecondFields();
            }
        });

        editThirdLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editThirdMode == 0) {
                    enableThirdEdit();
                } else {
                    restoreThirdDataDisplay();
                    resetThirdFieldsError();
                    disableThirdEdit();
                }
            }
        });

        doneThirdLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkThirdFields();
            }
        });

        return view;
    }

    private void openDialog() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_verify_wall, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(view);
        builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loading.setVisibility(View.VISIBLE);
                sendVerificationRequest();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendVerificationRequest() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy h:mm a");
        String dateTime = simpleDateFormat.format(calendar.getTime());

        HashMap verifyData = new HashMap();
        verifyData.put("userID", user.getUid());
        verifyData.put("email", user.getEmail());
        verifyData.put("created_datetime", dateTime);

        DatabaseReference verifyRef = FirebaseDatabase.getInstance().getReference()
                .child("Verification Requests").push();

        verifyRef.setValue(verifyData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Request sent successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Request failed!", Toast.LENGTH_SHORT).show();
                }
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void checkThirdFields() {
        newOrganization = organizationET.getText().toString();
        newIncome = incomeET.getText().toString();

        resetThirdFieldsError();

        if (newEmployment.equals("Select")) {
            employmentLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.ruby_red));
            Toast.makeText(getActivity(), "Please select your employment status.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(newOrganization) || newOrganization.equalsIgnoreCase("No Info")) {
            organizationLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.ruby_red));
            organizationET.setError("Organization info  is needed!");
            organizationET.requestFocus();
        } else if (TextUtils.isEmpty(newIncome) || newIncome.equalsIgnoreCase("No Info")) {
            incomeLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.ruby_red));
            incomeET.setError("Income info is needed!");
            incomeET.requestFocus();
        } else {
            loading.setVisibility(View.VISIBLE);

            HashMap profileData = new HashMap();
            profileData.put("employmentStatus", newEmployment);
            profileData.put("organization", newOrganization);
            profileData.put("income", newIncome);

            updateData(profileData);
        }
    }

    private void disableThirdEdit() {
        editThirdMode = 0;

        editThirdLabel.setText(getString(R.string.edit_label));
        editThirdLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.bright_blue));
        doneThirdLabel.setVisibility(View.GONE);

        employmentSpinner.setEnabled(false);
        organizationET.setEnabled(false);
        incomeET.setEnabled(false);

        organizationET.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
        incomeET.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
    }

    private void resetThirdFieldsError() {
        organizationET.setError(null);
        incomeET.setError(null);

        employmentLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.old_green));
        organizationLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.old_green));
        incomeLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.old_green));
    }

    private void restoreThirdDataDisplay() {
        setAdapter(employmentSpinner, R.array.employment);
        organizationET.setText(organization);
        incomeET.setText(income);
    }

    private void enableThirdEdit() {
        editThirdMode = 1;

        editThirdLabel.setText(getString(R.string.cancel_label));
        editThirdLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.bright_red));
        doneThirdLabel.setVisibility(View.VISIBLE);

        employmentSpinner.setEnabled(true);
        organizationET.setEnabled(true);
        incomeET.setEnabled(true);

        organizationET.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tea_green));
        incomeET.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tea_green));
    }

    private void checkSecondFields() {
        newAddress1 = address1ET.getText().toString();
        newAddress2 = address2ET.getText().toString();
        newPostcode = postcodeET.getText().toString();
        newTown = townET.getText().toString();

        resetSecondFieldsError();

        if (TextUtils.isEmpty(newAddress1) || newAddress1.equalsIgnoreCase("No Info")) {
            address1Label.setTextColor(ContextCompat.getColor(getContext(), R.color.ruby_red));
            address1ET.setError("Address 1 info  is needed!");
            address1ET.requestFocus();
        } else if (TextUtils.isEmpty(newPostcode) || newPostcode.equalsIgnoreCase("No Info")) {
            postcodeLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.ruby_red));
            postcodeET.setError("Postcode info is needed!");
            postcodeET.requestFocus();
        } else if (TextUtils.isEmpty(newTown) || newTown.equalsIgnoreCase("No Info")) {
            townLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.ruby_red));
            townET.setError("Town info is needed!");
            townET.requestFocus();
        } else if (newState.equals("Select")) {
            stateLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.ruby_red));
            Toast.makeText(getActivity(), "Please select your state.", Toast.LENGTH_SHORT).show();
        } else {
            loading.setVisibility(View.VISIBLE);

            if (TextUtils.isEmpty(newAddress2) || newAddress2 == null) newAddress2 = "No Info";

            HashMap profileData = new HashMap();
            profileData.put("address1", newAddress1);
            profileData.put("address2", newAddress2);
            profileData.put("postcode", newPostcode);
            profileData.put("town", newTown);
            profileData.put("state", newState);

            updateData(profileData);
        }
    }

    private void disableSecondEdit() {
        editSecondMode = 0;

        editSecondLabel.setText(getString(R.string.edit_label));
        editSecondLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.bright_blue));
        doneSecondLabel.setVisibility(View.GONE);

        address1ET.setEnabled(false);
        address2ET.setEnabled(false);
        postcodeET.setEnabled(false);
        townET.setEnabled(false);
        stateSpinner.setEnabled(false);

        address1ET.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
        address2ET.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
        postcodeET.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
        townET.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
    }

    private void resetSecondFieldsError() {
        address1ET.setError(null);
        postcodeET.setError(null);
        townET.setError(null);

        address1Label.setTextColor(ContextCompat.getColor(getContext(), R.color.old_green));
        postcodeLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.old_green));
        townLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.old_green));
        stateLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.old_green));
    }

    private void restoreSecondDataDisplay() {
        setAdapter(stateSpinner, R.array.state);
        address1ET.setText(address1);
        address2ET.setText(address2);
        postcodeET.setText(postcode);
        townET.setText(town);
    }

    private void enableSecondEdit() {
        editSecondMode = 1;

        editSecondLabel.setText(getString(R.string.cancel_label));
        editSecondLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.bright_red));
        doneSecondLabel.setVisibility(View.VISIBLE);

        address1ET.setEnabled(true);
        address2ET.setEnabled(true);
        postcodeET.setEnabled(true);
        townET.setEnabled(true);
        stateSpinner.setEnabled(true);

        address1ET.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tea_green));
        address2ET.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tea_green));
        postcodeET.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tea_green));
        townET.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tea_green));
    }

    private void setBindings(View view) {
        profileImage = view.findViewById(R.id.profileImage);
        verifyDisplay = view.findViewById(R.id.verifyDisplay);

        firstNameET = view.findViewById(R.id.firstNameET);
        lastNameET = view.findViewById(R.id.lastNameET);
        birthdateET = view.findViewById(R.id.birthdateET);
        address1ET = view.findViewById(R.id.address1ET);
        address2ET = view.findViewById(R.id.address2ET);
        postcodeET = view.findViewById(R.id.postcodeET);
        townET = view.findViewById(R.id.townET);
        organizationET = view.findViewById(R.id.organizationET);
        incomeET = view.findViewById(R.id.incomeET);

        genderSpinner = view.findViewById(R.id.genderSpinner);
        raceSpinner = view.findViewById(R.id.raceSpinner);
        religionSpinner = view.findViewById(R.id.religionSpinner);
        nationalitySpinner = view.findViewById(R.id.nationalitySpinner);
        maritalSpinner = view.findViewById(R.id.maritalSpinner);
        okuSpinner = view.findViewById(R.id.okuSpinner);
        stateSpinner = view.findViewById(R.id.stateSpinner);
        employmentSpinner = view.findViewById(R.id.employmentSpinner);

        firstNameLabel = view.findViewById(R.id.firstNameLabel);
        lastNameLabel = view.findViewById(R.id.lastNameLabel);
        birthdateLabel = view.findViewById(R.id.birthdateLabel);
        genderLabel = view.findViewById(R.id.genderLabel);
        raceLabel = view.findViewById(R.id.raceLabel);
        religionLabel = view.findViewById(R.id.religionLabel);
        nationalityLabel = view.findViewById(R.id.nationalityLabel);
        maritalLabel = view.findViewById(R.id.maritalLabel);
        okuLabel = view.findViewById(R.id.okuLabel);
        address1Label = view.findViewById(R.id.address1Label);
        address2Label = view.findViewById(R.id.address2Label);
        postcodeLabel = view.findViewById(R.id.postcodeLabel);
        townLabel = view.findViewById(R.id.townLabel);
        stateLabel = view.findViewById(R.id.stateLabel);
        employmentLabel = view.findViewById(R.id.employmentLabel);
        organizationLabel = view.findViewById(R.id.organizationLabel);
        incomeLabel = view.findViewById(R.id.incomeLabel);

        loading = view.findViewById(R.id.loadingBar);

        editMainLabel = view.findViewById(R.id.editMainCardLabel);
        doneMainLabel = view.findViewById(R.id.doneEditMainCardLabel);
        editSecondLabel = view.findViewById(R.id.editSecondCardLabel);
        doneSecondLabel = view.findViewById(R.id.doneEditSecondCardLabel);
        editThirdLabel = view.findViewById(R.id.editThirdCardLabel);
        doneThirdLabel = view.findViewById(R.id.doneEditThirdCardLabel);
        verifyBtn = view.findViewById(R.id.verifyBtn);
    }

    private void checkMainFields() {
        newFirstName = firstNameET.getText().toString();
        newLastName = lastNameET.getText().toString();
        newBirthdate = birthdateET.getText().toString();

        resetMainFieldsError();

        if (imageUri == null && changePic == 1)
            Toast.makeText(getActivity(), "Please choose a picture for your profile.", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(newFirstName)) {
            firstNameLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.ruby_red));
            firstNameET.setError("First name cannot be empty!");
            firstNameET.requestFocus();
        } else if (TextUtils.isEmpty(newLastName)) {
            lastNameLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.ruby_red));
            lastNameET.setError("First name cannot be empty!");
            lastNameET.requestFocus();
        } else if (TextUtils.isEmpty(newBirthdate)) {
            birthdateLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.ruby_red));
            birthdateET.setError("First name cannot be empty!");
            birthdateET.requestFocus();
        } else if (newGender.equals("Select")) {
            genderLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.ruby_red));
            Toast.makeText(getActivity(), "Please select your gender.", Toast.LENGTH_SHORT).show();
        } else if (newRace.equals("Select")) {
            raceLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.ruby_red));
            Toast.makeText(getActivity(), "Please select your race.", Toast.LENGTH_SHORT).show();
        } else if (newReligion.equals("Select")) {
            religionLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.ruby_red));
            Toast.makeText(getActivity(), "Please select your religion.", Toast.LENGTH_SHORT).show();
        } else if (newNationality.equals("Select")) {
            nationalityLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.ruby_red));
            Toast.makeText(getActivity(), "Please select your nationality.", Toast.LENGTH_SHORT).show();
        } else if (newMarital.equals("Select")) {
            maritalLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.ruby_red));
            Toast.makeText(getActivity(), "Please select your marital status.", Toast.LENGTH_SHORT).show();
        } else if (newOku.equals("Select")) {
            okuLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.ruby_red));
            Toast.makeText(getActivity(), "Please select your OKU status.", Toast.LENGTH_SHORT).show();
        } else {
            loading.setVisibility(View.VISIBLE);

            disableMainEdit();

            HashMap profileData = getHashMap();

            if (changePic == 1) {
                updateImage(profileData);
            } else updateData(profileData);
        }
    }

    @NonNull
    private HashMap getHashMap() {
        HashMap profileData = new HashMap();
        profileData.put("firstName", newFirstName);
        profileData.put("lastName", newLastName);
        profileData.put("birthdate", newBirthdate);
        profileData.put("gender", newGender);
        profileData.put("race", newRace);
        profileData.put("religion", newReligion);
        profileData.put("nationality", newNationality);
        profileData.put("maritalStatus", newMarital);
        profileData.put("OKU", newOku);
        return profileData;
    }

    private void updateImage(HashMap profileData) {
        StorageReference storeRef = FirebaseStorage.getInstance().getReference()
                .child("profiles/" + user.getUid());
        storeRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storeRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageUrl = uri.toString();
                        profileData.put("imageUrl", imageUrl);
                        updateData(profileData);
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

    private void updateData(HashMap profileData) {
        DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference().child("User Profiles");
        profileRef.child(user.getUid()).updateChildren(profileData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    updateEventListing();
                    Toast.makeText(getActivity(), "Data successfully updated!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Data update failed!", Toast.LENGTH_SHORT).show();
                }
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void updateEventListing() {
        DatabaseReference listRef = FirebaseDatabase.getInstance().getReference().child("Listings");
        listRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    if (data.child("userID").getValue().toString().equals(user.getUid())) {
                        DatabaseReference listRef = FirebaseDatabase.getInstance().getReference().child("Listings").child(data.getKey());
                        listRef.child("username").setValue(newFirstName + " " + newLastName).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("Events");
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    if (data.child("userID").getValue().toString().equals(user.getUid())) {
                        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("Events").child(data.getKey());
                        eventRef.child("username").setValue(newFirstName + " " + newLastName).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getActivity().finish();
    }

    private void resetMainFieldsError() {
        firstNameET.setError(null);
        lastNameET.setError(null);
        birthdateET.setError(null);

        firstNameLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.old_green));
        lastNameLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.old_green));
        birthdateLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.old_green));
        genderLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.old_green));
        raceLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.old_green));
        religionLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.old_green));
        nationalityLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.old_green));
        maritalLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.old_green));
        okuLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.old_green));
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
            changePic = 1;
            Picasso.get().load(imageUri).into(profileImage);
        }
    }

    private void restoreMainDataDisplay() {
        setAdapter(genderSpinner, R.array.gender);
        setAdapter(raceSpinner, R.array.race);
        setAdapter(religionSpinner, R.array.religion);
        setAdapter(nationalitySpinner, R.array.nationality);
        setAdapter(maritalSpinner, R.array.marital);
        setAdapter(okuSpinner, R.array.oku);

        Picasso.get().load(imageUrl).into(profileImage);
        firstNameET.setText(firstName);
        lastNameET.setText(lastName);
        birthdateET.setText(birthdate);
    }

    private void enableMainEdit() {
        editMainMode = 1;

        editMainLabel.setText(getString(R.string.cancel_label));
        editMainLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.bright_red));
        doneMainLabel.setVisibility(View.VISIBLE);

        firstNameET.setEnabled(true);
        lastNameET.setEnabled(true);
        birthdateET.setEnabled(true);
        genderSpinner.setEnabled(true);
        raceSpinner.setEnabled(true);
        religionSpinner.setEnabled(true);
        nationalitySpinner.setEnabled(true);
        maritalSpinner.setEnabled(true);
        okuSpinner.setEnabled(true);

        firstNameET.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tea_green));
        lastNameET.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tea_green));
        birthdateET.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tea_green));
    }

    private void disableMainEdit() {
        editMainMode = 0;

        editMainLabel.setText(getString(R.string.edit_label));
        editMainLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.bright_blue));
        doneMainLabel.setVisibility(View.GONE);

        firstNameET.setEnabled(false);
        lastNameET.setEnabled(false);
        birthdateET.setEnabled(false);
        genderSpinner.setEnabled(false);
        raceSpinner.setEnabled(false);
        religionSpinner.setEnabled(false);
        nationalitySpinner.setEnabled(false);
        maritalSpinner.setEnabled(false);
        okuSpinner.setEnabled(false);

        firstNameET.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
        lastNameET.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
        birthdateET.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
    }

    private void openDatePicker() {
        pickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                birthdateET.setText(String.valueOf(day) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year));
            }
        }, 2024, 0, 1);
        pickerDialog.show();
    }

    private void setAdapter(Spinner spinner, int itemArrayID) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), itemArrayID, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (spinner.equals(genderSpinner) && editMainMode == 0) {
                    spinner.setSelection(adapter.getPosition(gender));
                } else if (spinner.equals(genderSpinner) && editMainMode == 1) {
                    newGender = adapterView.getItemAtPosition(position).toString();
                } else if (spinner.equals(raceSpinner) && editMainMode == 0) {
                    spinner.setSelection(adapter.getPosition(race));
                } else if (spinner.equals(raceSpinner) && editMainMode == 1) {
                    newRace = adapterView.getItemAtPosition(position).toString();
                } else if (spinner.equals(religionSpinner) && editMainMode == 0) {
                    spinner.setSelection(adapter.getPosition(religion));
                } else if (spinner.equals(religionSpinner) && editMainMode == 1) {
                    newReligion = adapterView.getItemAtPosition(position).toString();
                } else if (spinner.equals(nationalitySpinner) && editMainMode == 0) {
                    spinner.setSelection(adapter.getPosition(nationality));
                } else if (spinner.equals(nationalitySpinner) && editMainMode == 1) {
                    newNationality = adapterView.getItemAtPosition(position).toString();
                } else if (spinner.equals(maritalSpinner) && editMainMode == 0) {
                    spinner.setSelection(adapter.getPosition(marital));
                } else if (spinner.equals(maritalSpinner) && editMainMode == 1) {
                    newMarital = adapterView.getItemAtPosition(position).toString();
                } else if (spinner.equals(okuSpinner) && editMainMode == 0) {
                    spinner.setSelection(adapter.getPosition(oku));
                } else if (spinner.equals(okuSpinner) && editMainMode == 1) {
                    newOku = adapterView.getItemAtPosition(position).toString();
                } else if (spinner.equals(stateSpinner) && editSecondMode == 0) {
                    spinner.setSelection(adapter.getPosition(state));
                } else if (spinner.equals(stateSpinner) && editSecondMode == 1) {
                    newState = adapterView.getItemAtPosition(position).toString();
                } else if (spinner.equals(employmentSpinner) && editThirdMode == 0) {
                    spinner.setSelection(adapter.getPosition(employment));
                } else if (spinner.equals(employmentSpinner) && editThirdMode == 1) {
                    newEmployment = adapterView.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void showProfileInfo() {

        imageUrl = getActivity().getIntent().getStringExtra("imageUrl");
        verify = getActivity().getIntent().getStringExtra("verify");
        firstName = getActivity().getIntent().getStringExtra("firstName");
        lastName = getActivity().getIntent().getStringExtra("lastName");
        birthdate = getActivity().getIntent().getStringExtra("birthdate");
        gender = getActivity().getIntent().getStringExtra("gender");
        race = getActivity().getIntent().getStringExtra("race");
        religion = getActivity().getIntent().getStringExtra("religion");
        nationality = getActivity().getIntent().getStringExtra("nationality");
        marital = getActivity().getIntent().getStringExtra("marital");
        oku = getActivity().getIntent().getStringExtra("oku");
        address1 = getActivity().getIntent().getStringExtra("address1");
        address2 = getActivity().getIntent().getStringExtra("address2");
        postcode = getActivity().getIntent().getStringExtra("postcode");
        town = getActivity().getIntent().getStringExtra("town");
        state = getActivity().getIntent().getStringExtra("state");
        employment = getActivity().getIntent().getStringExtra("employment");
        organization = getActivity().getIntent().getStringExtra("organization");
        income = getActivity().getIntent().getStringExtra("income");


        Picasso.get().load(imageUrl).into(profileImage);
        if (verify.equals("0")) {
            verifyDisplay.setText(getString(R.string.no_label));
            verifyDisplay.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.ruby_red));
            verifyBtn.setVisibility(View.VISIBLE);
        } else if (verify.equals("1")) {
            verifyDisplay.setText(getString(R.string.yes_label));
            verifyDisplay.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.turquoise));
            verifyBtn.setVisibility(View.GONE);
        }
        firstNameET.setText(firstName);
        lastNameET.setText(lastName);
        birthdateET.setText(birthdate);

        newGender = gender;
        newRace = race;
        newReligion = religion;

        if (nationality != null) newNationality = nationality;
        else nationality = "Select";

        if (marital != null) newMarital = marital;
        else marital = "Select";

        if (oku != null) newOku = oku;
        else oku = "Select";

        if (address1 != null) newAddress1 = address1;
        else address1 = "No Info";

        if (address2 != null) newAddress2 = address2;
        else address2 = "No Info";

        if (postcode != null) newPostcode = postcode;
        else postcode = "No Info";

        if (town != null) newTown = town;
        else town = "No Info";

        if (state != null) newState = state;
        else state = "Select";

        if (employment != null) newEmployment = employment;
        else employment = "Select";

        if (organization != null) newOrganization = organization;
        else organization = "No Info";

        if (income != null) newIncome = income;
        else income = "No Info";

        address1ET.setText(address1);
        address2ET.setText(address2);
        postcodeET.setText(postcode);
        townET.setText(town);
        organizationET.setText(organization);
        incomeET.setText(income);
    }
}