package com.example.codingcafe.chatter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;


public class GroupMessageAdapter  extends RecyclerView.Adapter<GroupMessageAdapter.GroupMessageViewHolder> {
    private List<GroupMessages> userMessagesList;
    public GroupMessageAdapter (List<GroupMessages> userMessagesList)
    {
        this.userMessagesList = userMessagesList;
    }
    @NonNull
    @Override
    public GroupMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_messages_for_group, parent, false);


        return new GroupMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupMessageViewHolder holder, int position) {

        final GroupMessages messages = userMessagesList.get(position);

        String fromUserID = messages.getName();

        holder.receiverMessageText.setVisibility(View.GONE);
        holder.senderMessageText.setVisibility(View.GONE);
        holder.messageSenderPicture.setVisibility(View.GONE);
        holder.messageReceiverPicture.setVisibility(View.GONE);


        if (fromUserID.equals(store.mobileno))
        {
            holder.senderMessageText.setVisibility(View.VISIBLE);

            holder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
            holder.senderMessageText.setTextColor(Color.BLACK);
            holder.senderMessageText.setText( messages.getMessage() + "\n" + messages.getTime() + " - " + messages.getDate()  );
        }
        else
        {
             DatabaseReference usersRef;


            usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(messages.getName());
            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.hasChild("name"))
                    {
                        holder.receiverMessageText.setVisibility(View.VISIBLE);

                        holder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
                        holder.receiverMessageText.setTextColor(Color.BLACK);
                       final String receiverImage = dataSnapshot.child("name").getValue().toString();
                        holder.receiverMessageText.setText(messages.getName() +"   "+ receiverImage  +"\n"+messages.getMessage() + "\n" + messages.getTime() + " - " + messages.getDate());


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();

    }

    public class GroupMessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView senderMessageText, receiverMessageText;
        public ImageView messageSenderPicture, messageReceiverPicture;




        public GroupMessageViewHolder(@NonNull View itemView) {
            super(itemView);


            senderMessageText = (TextView) itemView.findViewById(R.id.sender_messsage_text);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);
        }
    }
}
