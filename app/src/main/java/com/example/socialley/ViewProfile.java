package com.example.socialley;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewProfile extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    TextView usernameField, emailField, dobField, nationalityField, genderField, locationField;
    ImageView profilePicField;
    Button viewProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");

        usernameField = findViewById(R.id.other_username);
        emailField = findViewById(R.id.other_email);
        dobField = findViewById(R.id.other_dob);
        nationalityField = findViewById(R.id.other_nationality);
        genderField = findViewById(R.id.other_gender);
        locationField = findViewById(R.id.other_location);
        profilePicField = findViewById(R.id.other_profile_image);
        viewProfile = findViewById(R.id.view_posts);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String email = extras.getString("email");
            if (email != null) {
                databaseReference.orderByChild("email").equalTo(email).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            User user = snapshot1.getValue(User.class);
                            if (snapshot1.exists()) {
                                usernameField.setText("Username : " + user.getUsername());
                                emailField.setText("Email : " + user.getEmail());
                                if (!user.getProfilePic().equals("")) {
                                    try {
                                        Glide.with(getApplicationContext()).load(user.getProfilePic()).into(profilePicField);
                                    }
                                    catch (Exception e) {

                                    }
                                }
                                dobField.setText("Gender : " + user.getDob());
                                nationalityField.setText("Nationality : " + user.getNationality());
                                locationField.setText("Location : " + user.getLocation());
                                genderField.setText("Gender : " + user.getGender());

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            else {
                Toast.makeText(getApplicationContext(), "Unable to access account details", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Unable to access account details", Toast.LENGTH_SHORT).show();
        }
        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("type", "1");
                bundle.putString("email", extras.getString("email"));
                MyHomeFragment homefragment = new MyHomeFragment();
                homefragment.setArguments(bundle);
                FragmentTransaction homefragmentTransaction = getSupportFragmentManager().beginTransaction();
                homefragmentTransaction.replace(R.id.viewcontent, homefragment, "");
                homefragmentTransaction.commit();
            }
        });
    }
}