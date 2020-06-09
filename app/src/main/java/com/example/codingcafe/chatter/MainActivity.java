package com.example.codingcafe.chatter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;

    private TabsAccessorAdapter myTabsAccessorAdapter;
    private DatabaseReference RootRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RootRef = FirebaseDatabase.getInstance().getReference();
        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
       getSupportActionBar().setTitle("Chatter");


        myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        myTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);


        myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);

    }
    @Override
    protected void onStart()
    {
        super.onStart();
        SharedPreferences pref;

        pref = getSharedPreferences("user_details", MODE_PRIVATE);
        if (!(pref.contains("mobile") && pref.contains("password"))) {
            SendUserToLoginActivity();

        }
        else{
            VerifyUserExistance();
        }
        store.mobileno = pref.getString("mobile", "");



    }

    private void VerifyUserExistance() {
        SharedPreferences pref;

        pref = getSharedPreferences("user_details", MODE_PRIVATE);
        RootRef.child("Users").child(pref.getString("mobile","")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if ((dataSnapshot.child("name").exists()))
                {
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    SendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);

    }
    private void SendUserToSettingsActivity()
    {
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_option)
        {    SharedPreferences prf;
            prf = getSharedPreferences("user_details", MODE_PRIVATE);
            SharedPreferences.Editor editor = prf.edit();
            editor.clear();
            editor.commit();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.main_settings_option)
        {
           SendUserToSettingsActivity();
        }
        if (item.getItemId() == R.id.main_create_group_option)
        {
            RequestNewGroup();
        }

        if (item.getItemId() == R.id.main_find_friends_option)
        {
            SendUserToFindFriendsActivity();
        }


        return true;
    }


    private void SendUserToFindFriendsActivity()
    {
        Intent findFriendsIntent = new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(findFriendsIntent);
    }

    private void RequestNewGroup()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter Group Name :");

        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("e.g Hakuna Matata");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                final String groupName = groupNameField.getText().toString();

                if (TextUtils.isEmpty(groupName))
                {         Toast.makeText(MainActivity.this,"enter group name",Toast.LENGTH_LONG).show();

                }

                else
                {

                    DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Groups");

                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                if (dataSnapshot.hasChild(groupName.trim())) {

                                    Toast.makeText(MainActivity.this,"group name already exists try another",Toast.LENGTH_LONG).show();
                                }
                                else{
                                    CreateNewGroup(groupName.trim());

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }
    private void CreateNewGroup(final String groupName)
    {



        RootRef.child("Groups").child(groupName).child("groupmembers").child(store.mobileno).setValue("1")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            //Toast.makeText(MainActivity.this, groupName + " group is Created Successfully...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        RootRef.child("Groups").child(groupName).child("groupname").setValue(groupName)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            //Toast.makeText(MainActivity.this, groupName + " group is Created Successfully...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    /*public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

     */

}
