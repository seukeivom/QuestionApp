package com.seu.qapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.seu.qapp.R;
import com.seu.qapp.model.Message;

import java.text.SimpleDateFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageListAdapter extends RecyclerView.Adapter {

    private List<Message> messages;
    private String uid;


    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private static final int VIEW_TYPE_MESSAGE_RECEIVED_SAME = 3;
    private static final String TAG = "MessageListAdapter";
    private final SimpleDateFormat dateformatter = new SimpleDateFormat("MMM dd, yyy");
    private final SimpleDateFormat timeformatter = new SimpleDateFormat("hh:mm a");

    public MessageListAdapter(Context context, List<Message> messageList, FirebaseUser user) {
        this.messages = messageList;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        uid = user.getUid();
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);

        if (message.getUser_id().equals(uid)) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else if (position != 0 && message.getUser_id().equals(messages.get(position - 1).getUser_id())) {
            return VIEW_TYPE_MESSAGE_RECEIVED_SAME;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED_SAME) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received_same, parent, false);
            return new ReceivedSameMessageHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED_SAME:
                ((ReceivedSameMessageHolder) holder).bind(message);
        }
    }


    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText, dateText;

        ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            nameText = itemView.findViewById(R.id.text_message_name);
            dateText = itemView.findViewById(R.id.dateView);

        }

        void bind(Message message) {
            dateText.setText(dateformatter.format(message.getTimestamp()));
            nameText.setText(message.getDisplayname());
            messageText.setText(message.getMessage());
            timeText.setText(timeformatter.format(message.getTimestamp()));

        }

    }

    private class ReceivedSameMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, dateText;

        ReceivedSameMessageHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            dateText = itemView.findViewById(R.id.dateView);
        }

        void bind(Message message) {

            dateText.setText(dateformatter.format(message.getTimestamp()));
            messageText.setText(message.getMessage());
            timeText.setText(timeformatter.format(message.getTimestamp()));

        }

    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, dateText;

        SentMessageHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            dateText = itemView.findViewById(R.id.dateView);
        }

        void bind(Message message) {
            dateText.setText(dateformatter.format(message.getTimestamp()));
            messageText.setText(message.getMessage());
            timeText.setText(timeformatter.format(message.getTimestamp()));
        }

    }
}
