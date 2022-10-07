package com.rizalfadiaalfikri.whatsapp.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rizalfadiaalfikri.whatsapp.Adapters.FindFriendsAdapter;
import com.rizalfadiaalfikri.whatsapp.Model.Contacts;
import com.rizalfadiaalfikri.whatsapp.R;

import java.util.ArrayList;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView findFriendsRecyclerView;

    private FindFriendsAdapter adapter;
    private ArrayList<Contacts> contacts;

    private DatabaseReference friendRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        findFriendsRecyclerView = findViewById(R.id.find_friends_recyclerView);
        findFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mToolbar = (Toolbar) findViewById(R.id.find_friends_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter = new FindFriendsAdapter(FindFriendsActivity.this);
        friendRef = FirebaseDatabase.getInstance().getReference().child("Users");
        friendRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Contacts> contacts = new ArrayList<>();
                for(DataSnapshot data : snapshot.getChildren()) {
                    Contacts contacts1 = data.getValue(Contacts.class);
                    contacts1.setKey(data.getKey());
                    contacts.add(contacts1);
                }

                adapter.setItems(contacts);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        findFriendsRecyclerView.setAdapter(adapter);
    }
}