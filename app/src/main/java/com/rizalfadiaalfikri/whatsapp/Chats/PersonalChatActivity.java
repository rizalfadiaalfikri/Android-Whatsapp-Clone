package com.rizalfadiaalfikri.whatsapp.Chats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rizalfadiaalfikri.whatsapp.Adapters.MessageAdapter;
import com.rizalfadiaalfikri.whatsapp.Model.Messages;
import com.rizalfadiaalfikri.whatsapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalChatActivity extends AppCompatActivity {

    private String user_id, user_name, messageReceiverImage, messageSenderID;

    private TextView userName, userLastSeen;
    private CircleImageView userImage;
    private Toolbar chatToolbar;

    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    private ImageButton sendMessageButton;
    private EditText messageInputText;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter adapter;
    private RecyclerView userMessageRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_chat);

        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();

        user_id = getIntent().getExtras().get("user_id").toString();
        user_name = getIntent().getExtras().get("user_name").toString();
        messageReceiverImage = getIntent().getExtras().get("user_image").toString();

        initializeControllers();

        userName.setText(user_name);
        Glide.with(this)
                .load(messageReceiverImage)
                .into(userImage);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPersonalMessage();
            }
        });

    }

    private void initializeControllers() {

        chatToolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(chatToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(view);

        userImage =  findViewById(R.id.custom_profile_image);
        userName =  findViewById(R.id.custom_profile_name);
        userLastSeen =  findViewById(R.id.custom_user_last_seen);

        sendMessageButton = (ImageButton) findViewById(R.id.imageBtn_send_message);
        messageInputText = (EditText) findViewById(R.id.ed_input_message);

        adapter = new MessageAdapter(messagesList);
        userMessageRecyclerView = findViewById(R.id.recyclerView_personal_chat);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessageRecyclerView.setLayoutManager(linearLayoutManager);
        userMessageRecyclerView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();

        rootRef.child("Messages").child(messageSenderID).child(user_id)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Messages messages = snapshot.getValue(Messages.class);

                        messagesList.add(messages);
                        adapter.notifyDataSetChanged();

                        userMessageRecyclerView.smoothScrollToPosition(userMessageRecyclerView.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void sendPersonalMessage() {
        String messageText = messageInputText.getText().toString();

        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "First Write yout message", Toast.LENGTH_SHORT).show();
        } else {
            String messageSenderRef = "Messages/" + messageSenderID + "/" + user_id;
            String messageReceiverRef = "Messages/" + user_id + "/" + messageSenderID;

            DatabaseReference userMessageKeyRef = rootRef.child("Messages")
                    .child(messageSenderID)
                    .child(user_id)
                    .push();

            String messagePushID = userMessageKeyRef.getKey();
            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);

            rootRef.updateChildren(messageBodyDetails)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(PersonalChatActivity.this, "Message sent successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(PersonalChatActivity.this, "Error sent message", Toast.LENGTH_SHORT).show();
                            }

                            messageInputText.setText("");
                        }
                    });
        }
    }
}