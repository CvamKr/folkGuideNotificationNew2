package com.creation.android.receivenoti;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Folk_signup extends AppCompatActivity {

    Button next;
    EditText et_folk_id;
    String enteredfolk_id,email,name,phoneNumber,dob,folk_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folk_signup);

        next = findViewById(R.id.btn_next);
        et_folk_id = findViewById(R.id.et_folk_id);





        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                enteredfolk_id = et_folk_id.getText().toString().trim();

                // Access Cloud Firestore instance from Activity
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("folk")
                        .whereEqualTo("folk_id", enteredfolk_id)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
                                        email = document.get("email").toString();
                                        name = document.get("name").toString();
                                        folk_id = document.get("folk_id").toString();
                                        phoneNumber = document.get("phone").toString();
                                        dob = document.get("dob").toString();

                                        Toast.makeText(getApplicationContext(), "Hare Krishna " + name + ' ' + phoneNumber, Toast.LENGTH_LONG).show();

                                        Intent i = new Intent(Folk_signup.this,OTP_folk_member.class);
                                        i.putExtra("email",email);
                                        i.putExtra("name",name);
                                        i.putExtra("folk_id",folk_id);
                                        i.putExtra("phone",phoneNumber);
                                        i.putExtra("dob",dob);
                                        startActivity(i);

                                    }
                                } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
                                    Toast.makeText(getApplicationContext(), "Please re-check the FOLK ID entered", Toast.LENGTH_LONG).show();
                                }
                            }
                        });




            }
        });
    }
}
