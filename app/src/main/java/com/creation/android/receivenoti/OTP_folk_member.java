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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OTP_folk_member extends AppCompatActivity {

    EditText et_otp;
    String folk_id, codeSent, email, name, phoneNumber, dob;
    Button verify, email_otp, resend_otp;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_folk_member);

        // Retrieving the data from previous activity
        email = getIntent().getExtras().getString("email");
        name = getIntent().getExtras().getString("name");
        folk_id = getIntent().getExtras().getString("folk_id");
        phoneNumber = getIntent().getExtras().getString("phone");
        dob = getIntent().getExtras().getString("dob");


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
//                sendEmailVerification();
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
            Toast.makeText(getApplicationContext(),"OTP has been sent to your registered mobile number",Toast.LENGTH_SHORT).show();

            // In case of auto verification of the code
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(),"Verification Failed" + ' ' + e,Toast.LENGTH_LONG).show();
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

            Toast.makeText(getApplicationContext(),"Your session has timed out",Toast.LENGTH_SHORT).show();
        }
    };

    private void VerifySignInCode() {
        String codeEntered = et_otp.getText().toString().trim();
        if (codeEntered.isEmpty()){
            Toast.makeText(getApplicationContext(),"Please enter the otp sent to your registered mobile number before clicking on verify",Toast.LENGTH_SHORT).show();
            return;
        }else {
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
//                            Toast.makeText(getApplicationContext(),"OTP has been verified"+name+email,Toast.LENGTH_SHORT).show();
//                            Intent i = new Intent(OTP_folk_member.this,Set_Password.class);
//                            i.putExtra("name",name);
//                            i.putExtra("phone",phoneNumber);
//                            i.putExtra("email",email);
//                            i.putExtra("dob",dob);
//                            i.putExtra("folk_id",folk_id);
//                            i.putExtra("flag",flag); //flag = 1  =>  folk member
//                            startActivity(i);


                            // Access Cloud Firestore instance
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            //Adding the data of the user to firestore database for folk member
                            Map<String, Object> folk = new HashMap<>();
                            folk.put("name", name);
                            folk.put("folk_id",folk_id);
                            folk.put("email", email);
                            folk.put("phone", phoneNumber);
                            folk.put("dob", dob);

                            Log.d("Checking the data", name + email + phoneNumber + dob);
                            db.collection("folk_app_users")
                                    .add(folk)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(getApplicationContext(), "Your data has been added to folk users", Toast.LENGTH_SHORT).show();
                                            // Moving to login activity
                                            startActivity(new Intent(OTP_folk_member.this,MainActivity.class));
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Some error ocurred while writing to the database" + e.toString(), Toast.LENGTH_LONG).show();
                                            Log.d("Error in data", e.toString());
                                        }
                                    });

                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getApplicationContext(),"The verification code entered was invalid",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }





//    private void sendEmailVerification() {
//
//        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
//                .setDisplayName(name)
//                .build();
//        user.updateProfile(profileChangeRequest)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Log.d("Profile updated", user.getDisplayName());
//                        }
//                    }
//                });
//
//
//        user.updateEmail(email)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(getApplicationContext(), "Email account updated", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
//
//        //Sending the verification email to the user
//        user.sendEmailVerification()
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(getApplicationContext(), "A verification email has been sent to your account", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
//        // Checking if the user's email is verified
//        if (user.isEmailVerified()) {
//            Toast.makeText(getApplicationContext(), "Your email id has been verified successfully", Toast.LENGTH_LONG).show();
//            Intent i = new Intent(OTP_folk_member.this, Set_Password.class);
//            i.putExtra("name", name);
//            i.putExtra("phone", phoneNumber);
//            i.putExtra("email", email);
//            i.putExtra("dob", dob);
//            i.putExtra("folk_id",folk_id);
//            i.putExtra("flag",flag); // flag = 1  =>  Folk member
//            startActivity(i);
//        } else {
//            Toast.makeText(getApplicationContext(), "Your email id has  not been verified", Toast.LENGTH_LONG).show();
//        }
//    }

}

