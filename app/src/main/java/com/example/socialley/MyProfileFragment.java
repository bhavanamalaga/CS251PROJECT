package com.example.socialley;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;


public class MyProfileFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;
    DatabaseReference reference;
    StorageReference storageReference;

    Button update_username_button;
    Button update_password_button;


    String oldUsername;
    String oldPassword;
    String uid;

    ImageView profilepic;
    Uri imageUri;
    ProgressBar pb;
    ProgressDialog pd;

    Boolean start;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    private Boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private Boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {

                } else {
                    Toast.makeText(this.getContext(),"Sorry, this activity requires camera permission",Toast.LENGTH_LONG).show();
                }
            });

    private ActivityResultLauncher<String> requestStoragePermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {

                } else {
                    Toast.makeText(this.getContext(),"Sorry, this activity requires storage permission",Toast.LENGTH_LONG).show();
                }
            });

    ActivityResultLauncher<String> pickFromGallery =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    new ActivityResultCallback<Uri>() {
                        @Override
                        public void onActivityResult(Uri uri) {
                            imageUri = uri;
                            uploadProfileCoverPhoto(imageUri);
                        }
                    });

    ActivityResultLauncher<Intent> pickFromCamera =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        uploadProfileCoverPhoto(imageUri);
                    }
                    else {
                        Toast.makeText(getContext(), "Data is null",Toast.LENGTH_LONG).show();
                    }
                }
            });

    private void uploadProfileCoverPhoto(final Uri uri) {

        String filePathName = "Users/Profile_Pics/" + firebaseUser.getUid();
        StorageReference storageReference1 = storageReference.child(filePathName);
        storageReference1.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;

                final Uri downloadUri = uriTask.getResult();
                if (uriTask.isSuccessful()) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("profilePic", downloadUri.toString());
                    reference.child(firebaseUser.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "Updated", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Error Updating ", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        pd = new ProgressDialog(this.getContext());
        pd.setCanceledOnTouchOutside(false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        update_password_button = view.findViewById(R.id.update_password_button);
        update_username_button = view.findViewById(R.id.update_username_button);


        profilepic = view.findViewById(R.id.profile_image);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");


        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Select image from");
                String[] options = {"Camera", "Gallery"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            if (!checkCameraPermission()) {
                                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
                                requestCameraPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            }
                            else {
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(MediaStore.Images.Media.TITLE, "Temp_pic");
                                contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
                                imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                pickFromCamera.launch(cameraIntent);
                                Toast.makeText(v.getContext(),"Camera Permission has already been granted",Toast.LENGTH_LONG).show();
                            }
                        } else if (which == 1) {
                            if (!checkStoragePermission()) {
                                requestStoragePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            }
                            else {
                                pickFromGallery.launch("image/*");
                                Toast.makeText(v.getContext(),"Storage Permission has already been granted",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
                builder.create().show();
            }
        });

        reference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    oldPassword = dataSnapshot.child("password").getValue().toString();

                    if (dataSnapshot.child("profilePic").getValue().toString() != "") {
                        try{
                            Glide.with(getActivity().getApplicationContext()).load(dataSnapshot.child("profilePic").getValue().toString()).into(profilepic);
                        }
                        catch (Exception e) {
                            Toast.makeText(getContext(),"Somri", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(getContext(),"Sorry", Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        update_username_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage("Updating Name");
                System.out.println("HELLO");
                showNamephoneupdate("name");
            }
            private void showNamephoneupdate(final String key) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_update_name, null);
                final EditText username = view.findViewById(R.id.update_username);
                Button editname = view.findViewById(R.id.update_name);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(view);
                final AlertDialog dialog = builder.create();
                dialog.show();

                editname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String usernam = username.getText().toString().trim();
                        if (TextUtils.isEmpty(usernam)) {
                            Toast.makeText(MyProfileFragment.this.getContext(), "New UserName can't be empty", Toast.LENGTH_LONG).show();
                            return;
                        }
                        dialog.dismiss();
                        updatename(usernam);
                    }
                });
            }
            private void updatename(final String usernam) {
                pd.show();
                final FirebaseUser user = firebaseAuth.getCurrentUser();

                reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("username").setValue(usernam);
                pd.dismiss();
            }

        });


        update_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage("Changing Password");
                showPasswordChangeDailog();
            }

            private void showPasswordChangeDailog() {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_update_pswd, null);
                final EditText oldpass = view.findViewById(R.id.oldpasslog);
                final EditText newpass = view.findViewById(R.id.newpasslog);
                Button editpass = view.findViewById(R.id.updatepass);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(view);
                final AlertDialog dialog = builder.create();
                dialog.show();
                editpass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String oldp = oldpass.getText().toString().trim();
                        String newp = newpass.getText().toString().trim();
                        if (TextUtils.isEmpty(oldp)) {
                            Toast.makeText(MyProfileFragment.this.getContext(), "Current Password cant be empty", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (TextUtils.isEmpty(newp)) {
                            Toast.makeText(MyProfileFragment.this.getContext(), "New Password cant be empty", Toast.LENGTH_LONG).show();
                            return;
                        }
                        dialog.dismiss();
                        updatePassword(oldp, newp);
                    }
                });
            }
            private void updatePassword(final String oldp, final String newp) {
                pd.show();
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), oldp);
                user.reauthenticate(authCredential)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                user.updatePassword(newp)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                pd.dismiss();
                                                Toast.makeText(MyProfileFragment.this.getContext(), "Changed Password", Toast.LENGTH_LONG).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(MyProfileFragment.this.getContext(), "Failed", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(MyProfileFragment.this.getContext(), "Failed", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        return view;
    }
}