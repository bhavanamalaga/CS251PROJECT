package com.example.socialley;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseUser;


public class RegistrationActivity extends AppCompatActivity {
    EditText email, username, password;
    Button sign_up_button ;
    FirebaseAuth auth_user;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    TextView go_to_sign_in_button;
    ProgressDialog progressDialog;
    Button btn_google;
    FirebaseDatabase database;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().hide();

        email = findViewById(R.id.sign_up_email);
        username = findViewById(R.id.sign_up_username);
        btn_google = findViewById(R.id.btnGoogle);
        password = findViewById(R.id.sign_up_password);
        sign_up_button = findViewById(R.id.sign_up_button);
        go_to_sign_in_button = findViewById(R.id.go_to_sign_in_button);
        auth_user = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(RegistrationActivity.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We're creating your account");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        if(auth_user.getCurrentUser()!=null){
            Intent intent = new Intent(RegistrationActivity.this,HomeActivity.class);
            startActivity(intent);
        }


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        View.OnClickListener buttonClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                if (v.getId() == R.id.sign_up_button) {
                    rootNode = FirebaseDatabase.getInstance();
                    reference = rootNode.getReference("users");

                    String username_text = username.getText().toString().trim();
                    String email_text = email.getText().toString().trim();
                    String password_text = password.getText().toString().trim();
                    if (TextUtils.isEmpty(username_text)) {
                        username.setError("Can't create an account with empty username");
                        username.requestFocus();
                    } else if (TextUtils.isEmpty(password_text)) {
                        password.setError("Please enter your password");
                        password.requestFocus();
                    } else if (TextUtils.isEmpty(email_text)) {
                        email.setError("Please enter your Email address");
                        email.requestFocus();
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(email_text).matches()) {
                        email.setError("Please enter a valid email address");
                        email.requestFocus();
                    } else if (password_text.length() < 8) {
                        password.setError("Password should be minimum 8 characters long");
                        password.requestFocus();
                    } else {
                        progressDialog.show();
                        auth_user.createUserWithEmailAndPassword(email_text, password_text)
                                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        progressDialog.dismiss();

                                        if (!task.isSuccessful()) {
                                            Toast.makeText(RegistrationActivity.this,"ERROR "+ task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        } else {
                                            User newuser = new User(username_text, email_text, password_text);
                                            newuser.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(newuser);
                                            Toast.makeText(RegistrationActivity.this, "Successfully Registered", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));
                                            newuser.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            finish();
                                        }
                                    }
                                });
                    }
                }
                else if (v.getId() == R.id.go_to_sign_in_button) {
                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        sign_up_button.setOnClickListener(buttonClickListener);
        go_to_sign_in_button.setOnClickListener(buttonClickListener);

        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database= FirebaseDatabase.getInstance();
                signIn();
            }
        });

        if (auth_user.getCurrentUser() != null) {
            Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
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

                            Intent intent = new Intent(RegistrationActivity.this,HomeActivity.class);
                            startActivity(intent);
                            Toast.makeText(RegistrationActivity.this,"Sign in With Google",Toast.LENGTH_SHORT).show();
                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(RegistrationActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
//                                Snackbar.make(binding.getRoot(),"Authtentication Failed",Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }





}