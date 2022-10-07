package com.rizalfadiaalfikri.whatsapp.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rizalfadiaalfikri.whatsapp.MainActivity;
import com.rizalfadiaalfikri.whatsapp.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button btn_update_account_settings;
    private EditText ed_username, ed_status;
    private CircleImageView user_profile_image;

    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private StorageReference userProfileImagesRef;

    private static final int GalleryPick = 1;

    private ProgressDialog loadingBar;
    private Toolbar settingsToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        loadingBar = new ProgressDialog(this);

        initializefields();

    }

    private void initializefields() {
        ed_username = findViewById(R.id.ed_set_user_name);
        ed_status = findViewById(R.id.ed_set_profile_status);
        user_profile_image = findViewById(R.id.set_profile_image);
        btn_update_account_settings = findViewById(R.id.btn_update_settings_button);

        settingsToolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Account Settings");

        btn_update_account_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSettings();
            }
        });

        retrieveUserInfo();

        user_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galeryIntent = new Intent();
                galeryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galeryIntent.setType("image/*");
                startActivityForResult(galeryIntent, GalleryPick);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data!= null) {
            Uri imageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                
                loadingBar.setTitle("Set Profile Image");
                loadingBar.setMessage("Please wait, your profile image is updating.....");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                
                Uri resultUri = result.getUri();

                StorageReference filePath = userProfileImagesRef.child(currentUserID + ".jpeg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SettingsActivity.this, "Profile Image uploaded successfully", Toast.LENGTH_SHORT).show();

                            final String downloadUri = task.getResult().getMetadata().getReference().getDownloadUrl().toString();

                            rootRef.child("Users").child(currentUserID).child("image")
                                    .setValue(downloadUri)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SettingsActivity.this, "Image save in Database, Successfully.....", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                                Glide.with(SettingsActivity.this).load(downloadUri).into(user_profile_image);
                                            } else {
                                                String message = task.getException().toString();
                                                Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });

                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });

            }
        }

    }

    private void retrieveUserInfo() {
        rootRef.child("Users").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if ((snapshot.exists()) && (snapshot.hasChild("name") && (snapshot.hasChild("image")))) {
                            String retrieveUserName = snapshot.child("name").getValue().toString();
                            String retrieveStatus = snapshot.child("status").getValue().toString();
                            String retrieveProfileImage = snapshot.child("image").getValue().toString();

                            ed_username.setText(retrieveUserName);
                            ed_status.setText(retrieveStatus);

                            Toast.makeText(SettingsActivity.this, retrieveProfileImage, Toast.LENGTH_SHORT).show();
                            Log.d("IMAGE", retrieveProfileImage.toString());

                            Glide.with(SettingsActivity.this)
                                    .load(retrieveProfileImage)
                                    .into(user_profile_image);

                        } else if ((snapshot.exists()) && (snapshot.hasChild("name"))) {
                            String retrieveUserName = snapshot.child("name").getValue().toString();
                            String retrieveStatus = snapshot.child("status").getValue().toString();

                            ed_username.setText(retrieveUserName);
                            ed_status.setText(retrieveStatus);
                        } else {
                            Toast.makeText(SettingsActivity.this, "Please set & update your profile information", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void updateSettings() {
        String setUsername = ed_username.getText().toString();
        String setStatus = ed_status.getText().toString();

        if(TextUtils.isEmpty(setUsername)) {
            Toast.makeText(this, "Please write your user name", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(setStatus)) {
            Toast.makeText(this, "Please write your status", Toast.LENGTH_SHORT).show();
        }

        else {
            HashMap<String, Object> profileMap = new HashMap<>();

            profileMap.put("uid", currentUserID);
            profileMap.put("name", setUsername);
            profileMap.put("status", setStatus);

            rootRef.child("Users").child(currentUserID).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sendUserToMainActivity();
                                Toast.makeText(SettingsActivity.this, "Profile Update Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = task.getException().toString();
                                Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Animatoo.animateSwipeRight(SettingsActivity.this);
        startActivity(intent);
        finish();
    }
}