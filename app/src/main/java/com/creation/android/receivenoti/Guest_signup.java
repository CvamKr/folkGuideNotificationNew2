package com.creation.android.receivenoti;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class Guest_signup extends AppCompatActivity {

    Button next;
    EditText et_phone,et_email,et_name, et_dob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_signup);

        next = findViewById(R.id.btn_next);
        et_phone = findViewById(R.id.et_phone);
        et_email = findViewById(R.id.et_email);
        et_name = findViewById(R.id.et_name);
        et_dob = findViewById(R.id.et_dob);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Guest_signup.this, OTP.class);
                intent.putExtra("phoneNumber",et_phone.getText().toString().trim());
                intent.putExtra("email",et_email.getText().toString().trim());
                intent.putExtra("name",et_name.getText().toString().trim());
                intent.putExtra("dob",et_dob.getText().toString().trim());
                startActivity(intent);
            }
        });
    }
}
