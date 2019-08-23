package com.creation.android.receivenoti;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Signup_method extends AppCompatActivity {

    Button guest, folk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_method);

        guest = findViewById(R.id.btn_guest);
        folk = findViewById(R.id.btn_folk);

        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Signup_method.this, Guest_signup.class);
                startActivity(i);
            }
        });

        folk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Signup_method.this, Folk_signup.class);
                startActivity(i);
            }
        });
    }
}
