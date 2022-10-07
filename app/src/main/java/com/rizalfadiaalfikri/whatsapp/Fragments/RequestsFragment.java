package com.rizalfadiaalfikri.whatsapp.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rizalfadiaalfikri.whatsapp.Model.Contacts;
import com.rizalfadiaalfikri.whatsapp.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class RequestsFragment extends Fragment {

    private View requestView;
    private RecyclerView myRequestList;

    private DatabaseReference chatRequestRef, userRef, contactsRef;
    private FirebaseAuth mAuth;
    private String currentUserId;

    public RequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        requestView =  inflater.inflate(R.layout.fragment_requests, container, false);

        myRequestList = (RecyclerView) requestView.findViewById(R.id.recyclerView_chat_request);
        myRequestList.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Request");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        return requestView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatRequestRef.child(currentUserId), Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, RequestViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, RequestViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull RequestViewHolder holder, int position, @NonNull Contacts model) {
                        String userId = getRef(position).getKey();
                        DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();

                        holder.btn_accept.setVisibility(View.VISIBLE);
                        holder.btn_cancel.setVisibility(View.VISIBLE);

                        getTypeRef
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            String type = snapshot.getValue().toString();

                                            if (type.equals("received")) {
                                                userRef.child(userId).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.hasChild("image")) {
                                                            String profileImage = snapshot.child("image").getValue().toString();
                                                            Glide.with(getContext())
                                                                    .load(profileImage)
                                                                    .into(holder.userImage);
                                                        }
                                                        String profileUserName = snapshot.child("name").getValue().toString();
                                                        String profileUserStatus = snapshot.child("status").getValue().toString();

                                                        holder.userName.setText(profileUserName);
                                                        holder.userStatus.setText("Wants to connect with you");

                                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                CharSequence options[] = new CharSequence[]
                                                                        {
                                                                                "Accept",
                                                                                "Cancel"
                                                                        };
                                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                                builder.setTitle(profileUserName + " Chat Request");
                                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                                        if (i == 0) {
                                                                            contactsRef.child(currentUserId).child(userId).child("Contact")
                                                                                    .setValue("Saved")
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                contactsRef.child(userId).child(currentUserId).child("Contact")
                                                                                                        .setValue("Saved")
                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                if (task.isSuccessful()) {
                                                                                                                    chatRequestRef.child(currentUserId).child(userId)
                                                                                                                            .removeValue()
                                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                @Override
                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                    if (task.isSuccessful()) {
                                                                                                                                        chatRequestRef.child(userId).child(currentUserId)
                                                                                                                                                .removeValue()
                                                                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                    @Override
                                                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                        Toast.makeText(getContext(), "Contact Saved", Toast.LENGTH_SHORT).show();

                                                                                                                                                    }
                                                                                                                                                });
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            });

                                                                                                                }
                                                                                                            }
                                                                                                        });
                                                                                            }
                                                                                        }
                                                                                    });
                                                                        }
                                                                        if (i == 1) {
                                                                            chatRequestRef.child(currentUserId).child(userId)
                                                                                    .removeValue()
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                chatRequestRef.child(userId).child(currentUserId)
                                                                                                        .removeValue()
                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                Toast.makeText(getContext(), "Contact Deleted", Toast.LENGTH_SHORT).show();

                                                                                                            }
                                                                                                        });
                                                                                            }
                                                                                        }
                                                                                    });

                                                                        }
                                                                    }
                                                                });
                                                                builder.show();

                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                    }

                    @NonNull
                    @Override
                    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                        return new RequestViewHolder(view);
                    }
                };

        myRequestList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView userImage;
        private TextView userName, userStatus;
        private Button btn_accept, btn_cancel;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.users_profile_image);
            userName = itemView.findViewById(R.id.users_profile_name);
            userStatus = itemView.findViewById(R.id.users_profile_status);
            btn_accept = itemView.findViewById(R.id.btn_accept);
            btn_cancel = itemView.findViewById(R.id.btn_cancel);
        }
    }
}