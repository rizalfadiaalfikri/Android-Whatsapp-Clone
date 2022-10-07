package com.rizalfadiaalfikri.whatsapp.Fragments;

import android.content.Intent;
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
import com.rizalfadiaalfikri.whatsapp.Chats.PersonalChatActivity;
import com.rizalfadiaalfikri.whatsapp.Model.Contacts;
import com.rizalfadiaalfikri.whatsapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsFragment extends Fragment {

    View view;
    RecyclerView recyclerView_chats;

    private DatabaseReference chatsRef, usersRef;
    private FirebaseAuth mAuth;
    String currentId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chats, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentId = mAuth.getCurrentUser().getUid();
        chatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentId);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        recyclerView_chats = (RecyclerView) view.findViewById(R.id.recyclerView_chat);
        recyclerView_chats.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatsRef, Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, ChatsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ChatsViewHolder holder, int position, @NonNull Contacts model) {
                        final String usersId = getRef(position).getKey();
                        final String[] profileImage = {"default_image"};
                        usersRef.child(usersId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    if (snapshot.hasChild("image")) {
                                        profileImage[0] = snapshot.child("image").getValue().toString();

                                        Glide.with(getContext())
                                                .load(profileImage[0])
                                                .into(holder.userImage);
                                    }
                                    String userName = snapshot.child("name").getValue().toString();
                                    String userStatus = snapshot.child("status").getValue().toString();

                                    holder.userName.setText(userName);
                                    holder.userStatus.setText("Last Seen:" + "\n" + "Date " + " Time");

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(getContext(), PersonalChatActivity.class);
                                            intent.putExtra("user_id", usersId);
                                            intent.putExtra("user_name", userName);
                                            intent.putExtra("user_image", profileImage[0]);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                        return new ChatsViewHolder(view);
                    }
                };

        recyclerView_chats.setAdapter(adapter);
        adapter.startListening();

    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView userImage;
        private TextView userName, userStatus;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.users_profile_image);
            userName = itemView.findViewById(R.id.users_profile_name);
            userStatus = itemView.findViewById(R.id.users_profile_status);

        }
    }
}