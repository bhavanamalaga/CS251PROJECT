package com.example.socialley;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    Button dob, location, phone, gender, nationality;
    EditText  locationField, phoneField, genderField, nationalityField;
    TextView dobField;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    Query query;

    String dobVal, locationVal, phoneVal, genderVal, nationalityVal;
    int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dob = findViewById(R.id.update_dob_button);
        location = findViewById(R.id.update_location_button);
        phone = findViewById(R.id.update_phoneno_button);
        gender = findViewById(R.id.update_gender_button);
        nationality = findViewById(R.id.update_nationality_button);

        dobField = findViewById(R.id.dob);
        locationField = findViewById(R.id.edit_location);
        phoneField = findViewById(R.id.edit_phoneno);
        genderField = findViewById(R.id.edit_gender);
        nationalityField = findViewById(R.id.edit_nationality);


        dobVal = "";
        locationVal = "";
        phoneVal = "";
        genderVal = "";
        nationalityVal = "";

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        query = firebaseDatabase.getReference("users").orderByChild("email").equalTo(firebaseUser.getEmail());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (snapshot1.exists()) {
                        if (snapshot1.child("dob").getValue()!=null) {
                            dobVal = snapshot1.child("dob").getValue().toString();
                        }
                        if (snapshot1.child("location").getValue()!=null) {
                            locationVal = snapshot1.child("location").getValue().toString();
                        }
                        if (snapshot1.child("phone").getValue()!=null) {
                            phoneVal = snapshot1.child("phone").getValue().toString();
                        }
                        if (snapshot1.child("gender").getValue()!=null) {
                            genderVal = snapshot1.child("gender").getValue().toString();
                        }
                    }
                }
                dobField.setText(dobVal);
                locationField.setText(locationVal);
                phoneField.setText(phoneVal);
                genderField.setText(genderVal);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(SettingsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                dobVal = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                dobField.setText(dobVal);
                                firebaseDatabase.getReference("users").child(firebaseUser.getUid()).child("dob").setValue(dobVal);
                                //Toast.makeText(getApplicationContext(), "Updated Date of Birth", Toast.LENGTH_LONG).show();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseDatabase.getReference("users").child(firebaseUser.getUid()).child("location").setValue(locationField.getText().toString());
                Toast.makeText(getApplicationContext(), "Updated location", Toast.LENGTH_LONG).show();
            }
        });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseDatabase.getReference("users").child(firebaseUser.getUid()).child("phone").setValue(phoneField.getText().toString());
                Toast.makeText(getApplicationContext(), "Updated Phone number", Toast.LENGTH_LONG).show();
            }
        });

        nationality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseDatabase.getReference("users").child(firebaseUser.getUid()).child("nationality").setValue(nationalityField.getText().toString());
                Toast.makeText(getApplicationContext(), "Updated Nationality", Toast.LENGTH_LONG).show();
            }
        });

        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String genderval = genderField.getText().toString();
                if (genderval.equals("Male") || genderval.equals("Female") || genderval.equals("Other")) {
                    firebaseDatabase.getReference("users").child(firebaseUser.getUid()).child("gender").setValue(genderval);
                    Toast.makeText(getApplicationContext(), "Updated gender", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please enter a valid value", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}