package com.creation.android.receivenoti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button sign_out_btn;
    private FirebaseFirestore mFireStore;
    private String mUserId;
    private String mUserName;
    TextView profile_name,email;
    ImageView profile_image_iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profile_name=findViewById(R.id.username_tv);
        email=findViewById(R.id.email_tv);

        mAuth=FirebaseAuth.getInstance();
        mFireStore=FirebaseFirestore.getInstance();
        sign_out_btn=findViewById(R.id.sign_out);

        mUserId=mAuth.getCurrentUser().getUid();
        mUserName=mAuth.getCurrentUser().getDisplayName();

        mFireStore.collection("Users").document(mUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String user_name=documentSnapshot.getString("name");
                String user_image=documentSnapshot.getString("image");
                String user_email=documentSnapshot.getString("email");

                profile_name.setText(user_name);
                email.setText(user_email);
                //Glide.with(Profile.this).load(user_image).into(profile_image_iv);
                /*Glide
                        .with(Profile.this)
                        .load(user_image)
                        .centerCrop()
                        .placeholder(R.drawable.googleg_standard_color_18)
                        .into(profile_image_iv);*/


            }
        });

        sign_out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Map<String, Object> tokenMapRemove = new HashMap<>();
                tokenMapRemove.put("token_id", FieldValue.delete());

                mFireStore.collection("FolkGuide").document(mUserId).update(tokenMapRemove).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        mAuth.signOut();
                        Intent intent=new Intent(ProfileActivity.this,LoginActivity.class);
                        startActivity(intent);

                    }
                });
            }
        });
    }
}
