package com.creation.android.receivenoti;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    //private Toolbar mTopToolbar;
    Button profile_btn;
    private FirebaseFirestore mFireStore;
    private TextView mNotifData;


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {

            sendToLogin();

        }

    }

    private void sendToLogin() {

        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        profile_btn = findViewById(R.id.profile_btn_id);
        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });

        mFireStore = FirebaseFirestore.getInstance();

        String from_name = getIntent().getStringExtra("name");
        String message = getIntent().getStringExtra("message");
        String berth = getIntent().getStringExtra("berth");


        mNotifData = (TextView) findViewById(R.id.notifi_tv_id);

        mNotifData.setText("FROM : " + from_name + "\nMESSAGE : " + message+"\nBeerth: "+berth);


    }


}

