package com.rizalfadiaalfikri.whatsapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rizalfadiaalfikri.whatsapp.Model.Messages;
import com.rizalfadiaalfikri.whatsapp.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    private List<Messages> userMessageList;
    private Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    public MessageAdapter(List<Messages> userMessageList) {
        this.userMessageList = userMessageList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_message_layout, parent, false);

        mAuth = FirebaseAuth.getInstance();

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String messageSenderId = mAuth.getCurrentUser().getUid();
        Messages messages = userMessageList.get(position);

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("image")) {
                    String receiverProfile = snapshot.child("image").getValue().toString();

                    Glide.with(holder.itemView.getContext())
                            .load(receiverProfile)
                            .error(R.drawable.profile_image)
                            .into(holder.message_profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (fromMessageType.equals("text")) {
            holder.receiver_message_text.setVisibility(View.INVISIBLE);
            holder.message_profile_image.setVisibility(View.INVISIBLE);

            if (fromUserID.equals(messageSenderId)) {
                holder.sender_message_text.setBackgroundResource(R.drawable.sender_message_layout);
                holder.sender_message_text.setTextColor(Color.BLACK);
                holder.sender_message_text.setText(messages.getMessage());
            } else {
                holder.sender_message_text.setVisibility(View.INVISIBLE);
                holder.message_profile_image.setVisibility(View.VISIBLE);
                holder.receiver_message_text.setVisibility(View.VISIBLE);

                holder.receiver_message_text.setBackgroundResource(R.drawable.receiver_message_layout);
                holder.receiver_message_text.setTextColor(Color.BLACK);
                holder.receiver_message_text.setText(messages.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView message_profile_image;
        TextView receiver_message_text, sender_message_text;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            message_profile_image = itemView.findViewById(R.id.message_profile_image);
            receiver_message_text = itemView.findViewById(R.id.receiver_message_text);
            sender_message_text = itemView.findViewById(R.id.sender_message_text);

        }
    }
}
