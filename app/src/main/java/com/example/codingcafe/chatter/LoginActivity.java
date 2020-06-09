package com.example.codingcafe.chatter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText mobile;
    private EditText password;
    private Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        InitializeFields();
        findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });
        findViewById(R.id.forgot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(intent);

            }
        });

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mobile.getText().toString().trim().isEmpty() || mobile.getText().toString().trim().length() != 10) {
                    mobile.setError("Enter valid mobile number");
                    mobile.requestFocus();
                    return;
                }
                if (password.getText().toString().trim().isEmpty()) {
                    password.setError("Enter password");
                    password.requestFocus();
                    return;
                }
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Users").child(mobile.getText().toString());

                // Read from the database
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated
                            String value=dataSnapshot.child("password").getValue().toString();
                        if(password.getText().toString().trim().equals(value)){
                            SharedPreferences pref;
                            pref = getSharedPreferences("user_details", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("mobile", mobile.getText().toString().trim());
                            editor.putString("password", password.getText().toString().trim());
                            editor.commit();
                            SendUserToMainActivity();
                        }
                        else {
                            password.setError("Enter correct password");
                            password.requestFocus();
                            return;
                        }}
                        else{
                            mobile.setError("User dosent exist");
                            mobile.requestFocus();
                            return;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                       // Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });


            }
        });

    }

    private void InitializeFields() {
        mobile=findViewById(R.id.mobile);
        password=findViewById(R.id.password);
        submit=findViewById(R.id.submit);
    }


    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
