package com.example.mygrub.user.profile;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygrub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentDependants#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDependants extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //---VARIABLE---
    private RecyclerView dependantsView;
    private ArrayList<DependantItem> itemList;
    private DependantAdapter adapter;
    private RelativeLayout emptyLayout, notEmptyLayout;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dependantsRef = FirebaseDatabase.getInstance().getReference()
            .child("User Profiles").child(user.getUid()).child("Dependants");
    private Button addBtn, yesBtn;
    private ProgressBar loading;

    //----DIALOG ASSETS----
    private TextView firstNameLabel, lastNameLabel, birthdateLabel, relationLabel, employmentLabel;
    private EditText firstNameET, lastNameET, birthdateET;
    private Spinner relationSpinner, employmentSpinner;
    private String firstName, lastName, birthdate, relation, employment, newRelation, newEmployment;
    private DatePickerDialog pickerDialog;

    public FragmentDependants() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentDependants.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentDependants newInstance(String param1, String param2) {
        FragmentDependants fragment = new FragmentDependants();
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
        View view = inflater.inflate(R.layout.fragment_user_dependants, container, false);

        dependantsView = view.findViewById(R.id.dependantsView);
        emptyLayout = view.findViewById(R.id.emptyLayout);
        notEmptyLayout = view.findViewById(R.id.notEmptyLayout);
        addBtn = view.findViewById(R.id.addNonBtn);
        yesBtn = view.findViewById(R.id.yesBtn);
        loading = view.findViewById(R.id.loadingBar);

        dependantsView.setLayoutManager(new LinearLayoutManager(getContext()));

        itemList = new ArrayList<>();

        dependantsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                itemList.clear();

                for (DataSnapshot shot : snapshot.getChildren()) {
                    DependantItem item = shot.getValue(DependantItem.class);
                    Objects.requireNonNull(item).setKey(shot.getKey());
                    itemList.add(item);
                }
                if (itemList.isEmpty()) {
                    notEmptyLayout.setVisibility(View.GONE);
                    emptyLayout.setVisibility(View.VISIBLE);
                } else {
                    notEmptyLayout.setVisibility(View.VISIBLE);
                    emptyLayout.setVisibility(View.GONE);
                }

                adapter = new DependantAdapter(getContext(), itemList);
                adapter.notifyDataSetChanged();
                dependantsView.setAdapter(adapter);
                adapter.setOnItemClickListener(new DependantAdapter.OnItemClickListener() {
                    @Override
                    public void onEditButtonClick(DependantItem item) {
                        openEditDialog(item);
                    }

                    @Override
                    public void onDeleteButtonClick(DependantItem item) {
                        openDeleteDialog(item);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        setClick(addBtn);
        setClick(yesBtn);

        // Inflate the layout for this fragment
        return view;
    }

    private void openEditDialog(DependantItem item) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_dependant, null);
        TextView title = view.findViewById(R.id.title);
        firstNameLabel = view.findViewById(R.id.firstNameLabel);
        lastNameLabel = view.findViewById(R.id.lastNameLabel);
        birthdateLabel = view.findViewById(R.id.birthdateLabel);
        relationLabel = view.findViewById(R.id.relationLabel);
        employmentLabel = view.findViewById(R.id.employmentLabel);

        firstNameET = view.findViewById(R.id.firstNameET);
        lastNameET = view.findViewById(R.id.lastNameET);
        birthdateET = view.findViewById(R.id.birthdateET);
        relationSpinner = view.findViewById(R.id.relationSpinner);
        employmentSpinner = view.findViewById(R.id.employmentSpinner);

        firstNameET.setText(item.getFirstName());
        lastNameET.setText(item.getLastName());
        birthdateET.setText(item.getBirthdate());

        title.setText(getText(R.string.edit_dependant_label));

        setSpinner(relationSpinner, R.array.relationship);
        setSpinner(employmentSpinner, R.array.employmentD);

        relationSpinner.setSelection(((ArrayAdapter)relationSpinner.getAdapter()).getPosition(item.getRelation()));
        employmentSpinner.setSelection(((ArrayAdapter)employmentSpinner.getAdapter()).getPosition(item.getEmploymentStatus()));

        birthdateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        builder.setPositiveButton("Submit", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstName = firstNameET.getText().toString();
                lastName = lastNameET.getText().toString();
                birthdate = birthdateET.getText().toString();

                resetAddDialogFields();

                if (TextUtils.isEmpty(firstName)) {
                    firstNameLabel.setTextColor(getContext().getColor(R.color.bright_red));
                    firstNameET.setError("First name cannot be empty!");
                    firstNameET.requestFocus();
                } else if (TextUtils.isEmpty(lastName)) {
                    lastNameLabel.setTextColor(getContext().getColor(R.color.bright_red));
                    lastNameET.setError("Last name cannot be empty.");
                    lastNameET.requestFocus();
                } else if (TextUtils.isEmpty(birthdate)) {
                    birthdateLabel.setTextColor(getContext().getColor(R.color.bright_red));
                    birthdateET.setError("Birth date cannot be empty.");
                    birthdateET.requestFocus();
                } else if (relation.equals("Select")) {
                    relationLabel.setTextColor(getContext().getColor(R.color.bright_red));
                    Toast.makeText(getActivity(), "Select the dependant's relation to you.", Toast.LENGTH_SHORT).show();
                } else if (employment.equals("Select")) {
                    employmentLabel.setTextColor(getContext().getColor(R.color.bright_red));
                    Toast.makeText(getActivity(), "Select the dependant's employment status.", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();

                    HashMap dependantData = new HashMap();
                    dependantData.put("firstName", firstName);
                    dependantData.put("lastName", lastName);
                    dependantData.put("birthdate", birthdate);
                    dependantData.put("relation", relation);
                    dependantData.put("employmentStatus", employment);

                    loading.setVisibility(View.VISIBLE);
                    updateDependantData(dependantData, item);
                }
            }
        });
    }

    private void updateDependantData(HashMap dependantData, DependantItem item) {
        dependantsRef.child(item.getKey()).updateChildren(dependantData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Dependant updated successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Data update failed!", Toast.LENGTH_SHORT).show();
                }
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void openDeleteDialog(DependantItem item) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_delete_dependant, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        builder.setPositiveButton("Yes, I'm sure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loading.setVisibility(View.VISIBLE);
                deleteDependant(item);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getActivity().getColor(R.color.ruby_red));
            }
        });
        dialog.show();
    }

    private void deleteDependant(DependantItem item) {
        dependantsRef.child(item.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Dependant deleted successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void setClick(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddDialog();
            }
        });
    }

    private void openAddDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_dependant, null);

        firstNameLabel = view.findViewById(R.id.firstNameLabel);
        lastNameLabel = view.findViewById(R.id.lastNameLabel);
        birthdateLabel = view.findViewById(R.id.birthdateLabel);
        relationLabel = view.findViewById(R.id.relationLabel);
        employmentLabel = view.findViewById(R.id.employmentLabel);

        firstNameET = view.findViewById(R.id.firstNameET);
        lastNameET = view.findViewById(R.id.lastNameET);
        birthdateET = view.findViewById(R.id.birthdateET);
        relationSpinner = view.findViewById(R.id.relationSpinner);
        employmentSpinner = view.findViewById(R.id.employmentSpinner);

        setSpinner(relationSpinner, R.array.relationship);
        setSpinner(employmentSpinner, R.array.employmentD);

        birthdateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        builder.setPositiveButton("Add", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstName = firstNameET.getText().toString();
                lastName = lastNameET.getText().toString();
                birthdate = birthdateET.getText().toString();

                resetAddDialogFields();

                if (TextUtils.isEmpty(firstName)) {
                    firstNameLabel.setTextColor(getContext().getColor(R.color.bright_red));
                    firstNameET.setError("First name cannot be empty!");
                    firstNameET.requestFocus();
                } else if (TextUtils.isEmpty(lastName)) {
                    lastNameLabel.setTextColor(getContext().getColor(R.color.bright_red));
                    lastNameET.setError("Last name cannot be empty.");
                    lastNameET.requestFocus();
                } else if (TextUtils.isEmpty(birthdate)) {
                    birthdateLabel.setTextColor(getContext().getColor(R.color.bright_red));
                    birthdateET.setError("Birth date cannot be empty.");
                    birthdateET.requestFocus();
                } else if (relation.equals("Select")) {
                    relationLabel.setTextColor(getContext().getColor(R.color.bright_red));
                    Toast.makeText(getActivity(), "Select the dependant's relation to you.", Toast.LENGTH_SHORT).show();
                } else if (employment.equals("Select")) {
                    employmentLabel.setTextColor(getContext().getColor(R.color.bright_red));
                    Toast.makeText(getActivity(), "Select the dependant's employment status.", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();

                    HashMap dependantData = new HashMap();
                    dependantData.put("firstName", firstName);
                    dependantData.put("lastName", lastName);
                    dependantData.put("birthdate", birthdate);
                    dependantData.put("relation", relation);
                    dependantData.put("employmentStatus", employment);

                    loading.setVisibility(View.VISIBLE);
                    insertDependantData(dependantData);
                }
            }
        });
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

    private void insertDependantData(HashMap dependantData) {
        dependantsRef.push().setValue(dependantData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Dependant added successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Data insertion failed!", Toast.LENGTH_SHORT).show();
                }
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void resetAddDialogFields() {
        firstNameLabel.setTextColor(getContext().getColor(R.color.dark_sea_green));
        lastNameLabel.setTextColor(getContext().getColor(R.color.dark_sea_green));
        birthdateLabel.setTextColor(getContext().getColor(R.color.dark_sea_green));
        relationLabel.setTextColor(getContext().getColor(R.color.dark_sea_green));
        employmentLabel.setTextColor(getContext().getColor(R.color.dark_sea_green));

        firstNameET.setError(null);
        lastNameET.setError(null);
        birthdateET.setError(null);
    }

    private void setSpinner(Spinner spinner, int arrayID) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), arrayID, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinner == relationSpinner)
                    relation = adapterView.getItemAtPosition(i).toString();
                else if (spinner == employmentSpinner)
                    employment = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
}