package com.rizalfadiaalfikri.whatsapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rizalfadiaalfikri.whatsapp.Model.Contacts;
import com.rizalfadiaalfikri.whatsapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsFragment extends Fragment {

    private View contactsView;
    private RecyclerView myContactList;

    private DatabaseReference contactsRef, userRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactsView =  inflater.inflate(R.layout.fragment_contacts, container, false);

        myContactList = (RecyclerView) contactsView.findViewById(R.id.recyclerView_contacts);
        myContactList.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        return contactsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactsRef, Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, ContactsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ContactsViewHolder holder, int position, @NonNull Contacts model) {
                String userId = getRef(position).getKey();

                userRef.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("image")) {

                            String profileImage = snapshot.child("image").getValue().toString();
                            String userName = snapshot.child("name").getValue().toString();
                            String userStatus = snapshot.child("status").getValue().toString();

                            holder.userName.setText(userName);
                            holder.userStatus.setText(userStatus);

                            Glide.with(getContext())
                                    .load(profileImage)
                                    .into(holder.userImage);

                        } else {
                            String userName = snapshot.child("name").getValue().toString();
                            String userStatus = snapshot.child("status").getValue().toString();

                            holder.userName.setText(userName);
                            holder.userStatus.setText(userStatus);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                return new ContactsViewHolder(view);
            }
        };

        myContactList.setAdapter(adapter);
        adapter.startListening();

    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView userImage;
        private TextView userName, userStatus;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.users_profile_image);
            userName = itemView.findViewById(R.id.users_profile_name);
            userStatus = itemView.findViewById(R.id.users_profile_status);

        }
    }
}