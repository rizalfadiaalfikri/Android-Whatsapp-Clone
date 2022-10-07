package com.rizalfadiaalfikri.whatsapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rizalfadiaalfikri.whatsapp.Model.Contacts;
import com.rizalfadiaalfikri.whatsapp.Profile.ProfileActivity;
import com.rizalfadiaalfikri.whatsapp.R;

import java.util.ArrayList;

public class FindFriendsAdapter extends RecyclerView.Adapter<FindFriendsAdapter.MyViewHolder> {
    Context context;
    ArrayList<Contacts> contacts = new ArrayList<>();

    public FindFriendsAdapter(Context context) {
        this.context = context;
    }

    public void setItems(ArrayList<Contacts> items) {
        contacts.addAll(items);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.user_profile.setText(contacts.get(position).getName());
        holder.user_status.setText(contacts.get(position).getStatus());

        Glide.with(context)
                .load(contacts.get(position).getImage())
                .into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_id = contacts.get(position).getKey();

                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("user_id", user_id);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView user_profile, user_status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.users_profile_image);
            user_profile = itemView.findViewById(R.id.users_profile_name);
            user_status = itemView.findViewById(R.id.users_profile_status);
        }
    }
}
