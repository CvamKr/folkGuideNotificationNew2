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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Set_Password extends AppCompatActivity {

    String phone, email, name,dob, setPsw, confPsw,folk_id;
    int flag; //To check if the person is a guest or folk member
    EditText setPassword, confirmPassword;
    Button done;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set__password);

        // Retrieving the data from previous activity
//        try{
//            folk_id = getIntent().getExtras().getString("folk_id");
//        }catch (Exception e){
//            Log.d("Folk_id exception","Guest Signup");
//            flag = 1;
//        }

        flag = getIntent().getExtras().getInt("flag");
        if (flag == 1) {
            // flag = 1  =>  folk member. Take folk id fro the intent
            folk_id = getIntent().getExtras().getString("folk_id");
        }
        phone = getIntent().getExtras().getString("phone");
        email = getIntent().getExtras().getString("email");
        name = getIntent().getExtras().getString("name");
        dob = getIntent().getExtras().getString("dob");

        setPassword = findViewById(R.id.et_setPassword);
        confirmPassword = findViewById(R.id.et_confirmPassword);
        done = findViewById(R.id.btn_done);

        mAuth = FirebaseAuth.getInstance();

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPsw = setPassword.getText().toString().trim();
                confPsw = confirmPassword.getText().toString().trim();

                if (setPsw.equals(confPsw) && setPsw.length() >= 6){
                    Toast.makeText(getApplicationContext(),"Congratulations! You have successfully created your account",Toast.LENGTH_LONG);


                    // Access Cloud Firestore instance
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    if (flag == 0) {
                        //Adding the data of the user to firestore database for guest
                        Map<String, Object> user = new HashMap<>();
                        user.put("name", name);
                        user.put("email", email);
                        user.put("phone", phone);
                        user.put("dob", dob);
                        user.put("password", setPsw);

                        Log.d("Checking the data", name + email + phone + dob + setPsw);


                        db.collection("users")
                                .add(user)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(getApplicationContext(), "Your data has been added", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Some error ocurred while writing to the database" + e.toString(), Toast.LENGTH_LONG).show();
                                        Log.d("Error in data", e.toString());
                                    }
                                });
                    }else {

                        //Adding the data of the user to firestore database for folk member
                        //flag = 1

                        Map<String, Object> folk = new HashMap<>();
                        folk.put("name", name);
                        folk.put("folk_id",folk_id);
                        folk.put("email", email);
                        folk.put("phone", phone);
                        folk.put("dob", dob);
                        folk.put("password", setPsw);

                        Log.d("Checking the data", name + email + phone + dob + setPsw);
                        db.collection("folk_app_users")
                                .add(folk)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(getApplicationContext(), "Your data has been added to folk users", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Some error ocurred while writing to the database" + e.toString(), Toast.LENGTH_LONG).show();
                                        Log.d("Error in data", e.toString());
                                    }
                                });
                    }

                    // Moving to the login activity
                    Intent i = new Intent(Set_Password.this,MainActivity.class);
                    startActivity(i);

                }else if (setPsw.length() < 6){
                    Toast.makeText(getApplicationContext(),"Make sure that the passowrdd is atleast 6 characters long",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(),"The passwords do not match",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
