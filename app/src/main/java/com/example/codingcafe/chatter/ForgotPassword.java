package com.example.codingcafe.chatter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class ForgotPassword extends AppCompatActivity {
    private EditText mobile;
    private EditText password;
    private EditText rpassword;
    private EditText otp;
    private Button otpverif;
    private Button submit;
    private String mobile1;
    private String mVerificationId;
    //The edittext to input the code
    //firebase auth object
    private FirebaseAuth mAuth;


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected au6tomatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                otp.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(ForgotPassword.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);




        InitializeFields();
        otpverif.setOnClickListener(new View.OnClickListener() {
            @Override


            public void onClick(View v) {
                mobile1 = mobile.getText().toString().trim();
                if (mobile1.isEmpty() || mobile.length() != 10) {
                    mobile.setError("Enter valid number");
                    mobile.requestFocus();
                    return;
                }
                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild(mobile1)) {
                                sendVerificationCode(mobile1);


                            }
                            else{
                                Toast.makeText(ForgotPassword.this,"user dosen't exist",Toast.LENGTH_LONG).show();
                                mobile.setError("user dosen't exists");
                                mobile.requestFocus();
                                return;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = otp.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    otp.setError("Enter valid code");
                    otp.requestFocus();
                    return;
                }

                //verifying the code entered manually
                verifyVerificationCode(code);

            }
        });
    }





    private void sendVerificationCode (String mobile2){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile2,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }
    private void verifyVerificationCode (String code){
        //creating the credential
        if (password.getText().toString().trim().isEmpty() || password.getText().toString().trim().length() < 6) {
            password.setError("Enter more than 6 letters");
            password.requestFocus();
            return;
        }
        if (rpassword.getText().toString().trim().isEmpty() || rpassword.getText().toString().trim().length() < 6) {
            rpassword.setError("Enter valid details");
            rpassword.requestFocus();
            return;
        }
        if (!(rpassword.getText().toString().trim().equals(password.getText().toString().trim()))) {
            rpassword.setError("Enter same as password");
            rpassword.requestFocus();
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }
    private void signInWithPhoneAuthCredential (PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(ForgotPassword.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference();
                            myRef.child("Users").child(mobile1).child("password").setValue(password.getText().toString().trim());
                            Intent intent=new Intent(ForgotPassword.this,LoginActivity.class);
                            startActivity(intent);

                            Toast.makeText(ForgotPassword.this,"successs",Toast.LENGTH_LONG).show();

                            //verification successful we will start the chat activity

                        } else {
                            Toast.makeText(ForgotPassword.this,"failure",Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }
    private void InitializeFields () {
        mobile = findViewById(R.id.mobile);
        password = findViewById(R.id.password);
        rpassword = findViewById(R.id.rpassword);
        otp = findViewById(R.id.otp);
        otpverif = findViewById(R.id.otpverif);
        submit = findViewById(R.id.submit);
        mAuth = FirebaseAuth.getInstance();


    }
}
