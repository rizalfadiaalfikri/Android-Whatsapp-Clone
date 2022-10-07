package com.rizalfadiaalfikri.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rizalfadiaalfikri.whatsapp.Adapters.FragmentAdapter;
import com.rizalfadiaalfikri.whatsapp.Fragments.ChatsFragment;
import com.rizalfadiaalfikri.whatsapp.Fragments.ContactsFragment;
import com.rizalfadiaalfikri.whatsapp.Fragments.GroupsFragment;
import com.rizalfadiaalfikri.whatsapp.Fragments.RequestsFragment;
import com.rizalfadiaalfikri.whatsapp.LoginAndRegister.Login.LoginActivity;
import com.rizalfadiaalfikri.whatsapp.Session.SessionManager;
import com.rizalfadiaalfikri.whatsapp.Toolbar.FindFriendsActivity;
import com.rizalfadiaalfikri.whatsapp.Toolbar.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;

    private FirebaseUser curretUser;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(MainActivity.this);
        curretUser = mAuth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.main_tabs_page);
        toolbar = findViewById(R.id.toolbar);

        tabLayout.setupWithViewPager(viewPager);
        setSupportActionBar(toolbar);

        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        fragmentAdapter.addFragment(new ChatsFragment(), "CHATS");
        fragmentAdapter.addFragment(new GroupsFragment(), "GROUPS");
        fragmentAdapter.addFragment(new ContactsFragment(), "CONTACTS");
        fragmentAdapter.addFragment(new RequestsFragment(), "REQUEST");
        viewPager.setAdapter(fragmentAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.main_logout_option) {
            mAuth.signOut();
            SendUserToLoginActivity();
        }
        if (item.getItemId() == R.id.main_find_friends_option) {
            sendUserToFindFriendsActivity();
        }
        if (item.getItemId() == R.id.main_create_group_option) {
            requestNewGroup();
        }
        if (item.getItemId() == R.id.main_settings_option) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendUserToFindFriendsActivity() {
        Intent intent = new Intent(MainActivity.this, FindFriendsActivity.class);
        Animatoo.animateSlideUp(MainActivity.this);
        startActivity(intent);
    }

    private void requestNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter Group Name: ");

        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("e.g Coding Group");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String groupName = groupNameField.getText().toString();

                if (TextUtils.isEmpty(groupName)) {
                    Toast.makeText(MainActivity.this, "Please write your Group Name....", Toast.LENGTH_SHORT).show();
                } else {
                    createNewGroup(groupName);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();

    }

    private void createNewGroup(String groupName) {
        rootRef.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Create group is successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (curretUser == null) {
            SendUserToLoginActivity();
        } else {
            VerifyUserExistance();
        }
    }

    private void VerifyUserExistance() {
        String currentUserID = mAuth.getCurrentUser().getUid();
        rootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((snapshot.child("name").exists())) {
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SendUserToLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}