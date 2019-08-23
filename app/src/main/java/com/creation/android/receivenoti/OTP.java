package com.creation.android.receivenoti;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OTP extends AppCompatActivity {

    EditText et_otp;
    String phone, codeSent, email, name, phoneNumber, dob;
    Button verify, email_otp, resend_otp;
    FirebaseAuth mAuth;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        db = FirebaseFirestore.getInstance();


        // Retrieving the data from previous activity
        phone = getIntent().getExtras().getString("phoneNumber");
        email = getIntent().getExtras().getString("email");
        name = getIntent().getExtras().getString("name");
        dob = getIntent().getExtras().getString("dob");

        // Phone number according to the E.164 format
        phoneNumber = "+91 " + phone;

        et_otp = findViewById(R.id.et_otp);
        email_otp = findViewById(R.id.btn_email_otp);
        mAuth = FirebaseAuth.getInstance();
        verify = findViewById(R.id.btn_verify);
        resend_otp = findViewById(R.id.btn_resend_otp);


        // To send the otp to the registered mobile number
        sendVerificationCode();


        // To send the verification code to the user's mobile
//        generateVerificationCode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendVerificationCode();
//            }
//        });


        // To verify the the code entered by the user
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifySignInCode();
            }
        });

        // Email verification of the user
//        email_otp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               sendEmailVerification();
//            }
//        });

        // To resend the otp to the user
        resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCode();
            }
        });

    }

    private void sendVerificationCode() {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacksPhoneAuthActivity.java
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Toast.makeText(getApplicationContext(), "OTP has been sent to your registered mobile number", Toast.LENGTH_SHORT).show();

            // In case of auto verification of the code
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(), "Verification Failed" + ' ' + e, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //OTP that has been sent to the user's phone is 's'
            codeSent = s;
        }

        @Override
        public void onCodeAutoRetrievalTimeOut(String s) {
            super.onCodeAutoRetrievalTimeOut(s);

            Toast.makeText(getApplicationContext(), "Your session has timed out", Toast.LENGTH_SHORT).show();
        }
    };

    private void VerifySignInCode() {
        String codeEntered = et_otp.getText().toString().trim();
        if (codeEntered.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter the otp sent to your registered mobile number before clicking on verify", Toast.LENGTH_SHORT).show();
            return;
        } else {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, codeEntered);
            signInWithPhoneAuthCredential(credential);
        }
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                          
                            final String user_id = mAuth.getCurrentUser().getUid();


                            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(OTP.this, new OnSuccessListener<InstanceIdResult>() {
                                @Override
                                public void onSuccess(InstanceIdResult instanceIdResult) {
                                    String folk_guide_token_id = instanceIdResult.getToken();
                                    Log.e("folk_guide_token_id", folk_guide_token_id);
                                    Map<String, Object> userMap = new HashMap<>();
                                    userMap.put("name", name);
                                    userMap.put("email", email);
                                    userMap.put("phone", phoneNumber);

                                    userMap.put("folk_guide_token_id", folk_guide_token_id);


                                    //Adding the data of the user to firestore database for guest


                                    db.collection("FolkGuide").document(user_id).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(), "Your data has been added", Toast.LENGTH_SHORT).show();
                                            // Moving to login activity
                                            startActivity(new Intent(OTP.this, Splash_screen.class));
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Some error ocurred while writing to the database" + e.toString(), Toast.LENGTH_LONG).show();
                                            Log.d("Error in data", e.toString());

                                        }
                                    });


                                }
                            });

                        }else {
                            Toast.makeText(getApplicationContext(), "Error getting documents: " + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}

