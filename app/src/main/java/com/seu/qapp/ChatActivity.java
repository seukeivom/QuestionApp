package com.seu.qapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.seu.qapp.adapter.MessageListAdapter;
import com.seu.qapp.model.Message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class ChatActivity extends AppCompatActivity {


    private EditText editText;
    private ImageButton sendBtn;
    private static final String TAG = "ChatActivity";
    private FirebaseUser currentUser;
    private RecyclerView recyclerView;
    private TextView textView;
    private ActionBar actionBar;
    private FirebaseAuth auth;
    private DatabaseReference messagedb;
    private FirebaseDatabase database;
    private List<Message> messages;
    private MessageListAdapter adapter;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.chat_bg);
        BitmapDrawable backgroundDrawable = new BitmapDrawable(bitmap);
        this.getWindow().getDecorView().setBackgroundDrawable(backgroundDrawable);
        mp = MediaPlayer.create(this, R.raw.sound);
        init();

    }

    private void init() {
        actionBar = getSupportActionBar();

        textView = findViewById(R.id.title);
        editText = findViewById(R.id.edittext_chatbox);
        sendBtn = findViewById(R.id.button_chatbox_send);
        sendBtn.setVisibility(View.INVISIBLE);
        recyclerView = findViewById(R.id.recycler_view);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setTitle("Group Chat");

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        currentUser = auth.getCurrentUser();
        messages = new ArrayList<>();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editText.getText().toString().length() > 0) {
                    sendBtn.setVisibility(View.VISIBLE);
                } else {
                    sendBtn.setVisibility(View.INVISIBLE);
                }
            }
        });

        sendBtn.setOnClickListener(v -> {
            sendMessage();
            mp.start();
        });
    }

    private void sendMessage() {
        Date date = new Date();
        Message message = new Message(currentUser.getDisplayName(), currentUser.getUid(), editText.getText().toString(), date);
        editText.setText("");
        messagedb.push().setValue(message);
    }

    @Override
    protected void onStart() {
        super.onStart();

        messagedb = database.getReference("messages");
        messagedb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Message message = dataSnapshot.getValue(Message.class);
                Objects.requireNonNull(message).setMessage_id(dataSnapshot.getKey());
                messages.add(message);
                displayMessages(messages);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message = dataSnapshot.getValue(Message.class);
                Objects.requireNonNull(message).setMessage_id(dataSnapshot.getKey());
                List<Message> newMessage = new ArrayList<>();

                for (Message m : messages) {
                    if (m.getMessage_id().equals(message.getMessage_id())) {
                        newMessage.add(message);
                    } else {
                        newMessage.add(m);
                    }

                }
                messages = newMessage;
                displayMessages(messages);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Message message = dataSnapshot.getValue(Message.class);
                Objects.requireNonNull(message).setMessage_id(dataSnapshot.getKey());
                List<Message> newMessage = new ArrayList<>();

                for (Message m : messages) {
                    if (!m.getMessage_id().equals(message.getMessage_id())) {
                        newMessage.add(m);
                    }
                }
                messages = newMessage;
                displayMessages(messages);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        messages = new ArrayList<>();

    }

    private void displayMessages(List<Message> messages) {
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        adapter = new MessageListAdapter(ChatActivity.this, messages, currentUser);
        recyclerView.setAdapter(adapter);
        recyclerView.smoothScrollToPosition(adapter.getItemCount());

    }


}
