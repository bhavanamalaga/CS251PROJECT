package com.example.socialley;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.LinearGradient;
import android.os.Bundle;
import android.content.Intent;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialley.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    Button sign_in_button, btn_google ;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth auth_user;
    ProgressDialog progressDialog;
    TextView clickSignUp;
    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        database = FirebaseDatabase.getInstance();

        auth_user = FirebaseAuth.getInstance();
        email = findViewById(R.id.sign_in_email);
        password = findViewById(R.id.sign_in_password);
        sign_in_button = findViewById(R.id.sign_in_button);
        clickSignUp = findViewById(R.id.clickSignUp);
        btn_google = findViewById(R.id.btnGoogle);

        if(auth_user.getCurrentUser()!=null){
            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
            startActivity(intent);
        }

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Login to your account");

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        View.OnClickListener buttonClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                String email_text = email.getText().toString().trim();
                String password_text = password.getText().toString().trim();
                if (TextUtils.isEmpty(email_text)) {
                    email.setError("Please enter your Email address");
                    email.requestFocus();
                } else if (TextUtils.isEmpty(password_text)) {
                    password.setError("Please enter your password");
                    password.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email_text).matches()) {
                    email.setError("Please enter a valid Email address");
                    email.requestFocus();
                } else {
                    progressDialog.show();
                    auth_user.signInWithEmailAndPassword(email_text, password_text)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if (!task.isSuccessful()) {
                                        // there was an error
                                        if (password.length() < 8) {
                                            Toast.makeText(getApplicationContext(), "Password must be more than 8 digit", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "ERROR " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        };
        sign_in_button.setOnClickListener(buttonClickListener);
        clickSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });


        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        if (auth_user.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    }

    int RC_SIGN_IN = 65; // any  value below 100
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth_user.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = auth_user.getCurrentUser();

                            User userGoogle = new User();
                            userGoogle.setUserId(user.getUid());
                            userGoogle.setUsername(user.getDisplayName());
                            userGoogle.setProfilePic(user.getPhotoUrl().toString());

                            database.getReference().child("users").child(user.getUid()).setValue(userGoogle);

                            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                            startActivity(intent);
                            Toast.makeText(LoginActivity.this,"Sign in With Google",Toast.LENGTH_SHORT).show();
                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
//                                Snackbar.make(binding.getRoot(),"Authtentication Failed",Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }






}