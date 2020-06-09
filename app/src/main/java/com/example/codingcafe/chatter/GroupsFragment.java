package com.example.codingcafe.chatter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {
    private View groupFragmentView;
    private DatabaseReference GroupsRef,UsersRef;
    private RecyclerView groupsRecyclerList;



    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        groupFragmentView = inflater.inflate(R.layout.fragment_groups, container, false);
        GroupsRef =FirebaseDatabase.getInstance().getReference().child("Groups");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Groups");


        groupsRecyclerList = (RecyclerView) groupFragmentView.findViewById(R.id.groups_list);
        groupsRecyclerList.setLayoutManager(new LinearLayoutManager(getContext()));


        return groupFragmentView;
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<groups> options = new FirebaseRecyclerOptions.Builder<groups>().setQuery(GroupsRef,groups.class).build();
        FirebaseRecyclerAdapter<groups, GrouplistViewHolder> adapter=new FirebaseRecyclerAdapter<groups,GrouplistViewHolder>(options){
            @Override
            protected void onBindViewHolder(@NonNull final GrouplistViewHolder  grouplistViewHolder, final int i, @NonNull groups model) {


                final String userIDs = getRef(i).getKey();
                UsersRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.child("groupmembers").hasChild(store.mobileno)) {
                                 grouplistViewHolder.groupName.setText(userIDs);
                                 grouplistViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         String group_name=getRef(i).getKey();
                                         Intent intent=new Intent(getContext(),GroupChatActivity.class);
                                         intent.putExtra("group_name",group_name);
                                         startActivity(intent);
                                         store.cgroupname=group_name;
                                     }
                                 });

                            }
                            else{
                                grouplistViewHolder.groupName.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            public GrouplistViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.groups_display_layout, viewGroup, false);
                GroupsFragment.GrouplistViewHolder viewHolder = new GroupsFragment.GrouplistViewHolder(view);
                return viewHolder;
            }
        };

     groupsRecyclerList.setAdapter(adapter);
     adapter.startListening();
    }
    public static class  GrouplistViewHolder extends RecyclerView.ViewHolder{
        TextView groupName;
        public GrouplistViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName =itemView.findViewById(R.id.group_name);
        }


    }
}
