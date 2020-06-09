package com.example.codingcafe.chatter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private String currentGroupName;
    private ImageButton SendMessageButton;
    private EditText userMessageInput;
    private DatabaseReference UsersRef, GroupNameRef, GroupMessageKeyRef;
    private String  currentUserID, currentUserName, currentDate, currentTime;
    private DatabaseReference RootRef;

    private final List<GroupMessages> groupMessagesList =new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private GroupMessageAdapter groupMessageAdapter;
    private RecyclerView users_messages_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        currentGroupName = getIntent().getExtras().get("group_name").toString();
        mToolbar = (Toolbar) findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName);

        GroupNameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName).child("messages");
        RootRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName).child("messages");




        InitializeFields();


        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {   check_users_count();


            }
        });

    }

    private void check_users_count() {

        DatabaseReference fbDb ;
            fbDb = FirebaseDatabase.getInstance().getReference().child("Groups");



        fbDb.child(currentGroupName).child("groupmembers")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // get total available quest
                        int size = (int) dataSnapshot.getChildrenCount();
                        if(size==1){
                            EditText editText=findViewById(R.id.input_message);
                            editText.setError("add people to send messages");
                            editText.requestFocus();
                            return;
                        }
                        else{
                            SaveMessageInfoToDatabase();
                            userMessageInput.setText("");

                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void InitializeFields()
    {


        SendMessageButton = (ImageButton) findViewById(R.id.send_message_btn);
        userMessageInput = (EditText) findViewById(R.id.input_message);

        groupMessageAdapter =new GroupMessageAdapter(groupMessagesList);
        users_messages_list=(RecyclerView) findViewById(R.id.group_messages_list);
        linearLayoutManager =new LinearLayoutManager(this);
        users_messages_list.setLayoutManager(linearLayoutManager);
        users_messages_list.setAdapter(groupMessageAdapter);


    }

    @Override
    protected void onStart() {
        super.onStart();
        groupMessagesList.clear();
        RootRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                GroupMessages groupMessages =dataSnapshot.getValue(GroupMessages.class);

                groupMessagesList.add(groupMessages);
                groupMessageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void SaveMessageInfoToDatabase()
    {
        String message = userMessageInput.getText().toString();
        String messagekEY = GroupNameRef.push().getKey();

        if (TextUtils.isEmpty(message))
        {
            Toast.makeText(this, "Please write message first...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentDateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calForTime.getTime());


            HashMap<String, Object> groupMessageKey = new HashMap<>();
            GroupNameRef.updateChildren(groupMessageKey);

            GroupMessageKeyRef = GroupNameRef.child(messagekEY);

            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name", store.mobileno);
            messageInfoMap.put("message", message);
            messageInfoMap.put("date", currentDate);
            messageInfoMap.put("time", currentTime);
            GroupMessageKeyRef.updateChildren(messageInfoMap);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_2, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.add_member)
        {   add_member();
        }
        if (item.getItemId() == R.id.exit_group)
        {
            exitGroup();
        }

        return true;
    }

    private void exitGroup() {

DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Groups");

        databaseReference.child(currentGroupName).child("groupmembers").child(store.mobileno)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                          Toast.makeText(GroupChatActivity.this,"group exit success",Toast.LENGTH_LONG).show();
                          Intent intent=new Intent(GroupChatActivity.this,MainActivity.class);
                          startActivity(intent);
                        }
                    }
                });
        databaseReference.child(currentGroupName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("groupmembers")) {


                    }
                    else{
                        DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference().child("Groups");


                        databaseReference1.child(currentGroupName)
                                .removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(GroupChatActivity.this,"group exit success",Toast.LENGTH_LONG).show();
                                            Intent intent=new Intent(GroupChatActivity.this,MainActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void add_member() {

        Intent intent=new Intent(GroupChatActivity.this,AddPeople.class);
        intent.putExtra("cgroupname",currentGroupName);
        startActivity(intent);
    }

   /* @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(GroupChatActivity.this,MainActivity.class);
        startActivity(setIntent);
    }

    */


}
