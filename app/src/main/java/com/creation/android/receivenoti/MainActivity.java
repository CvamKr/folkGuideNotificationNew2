package com.creation.android.receivenoti;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    //private Toolbar mTopToolbar;
    Button profile_btn;
    private FirebaseFirestore mFireStore;
    private TextView mNotifData;

    //firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    //databases references
    CollectionReference UsersColRef
            = db.collection("Users");

    //arraylist
    ArrayList<FolkBoyData> folkBoyDataArrayList = new ArrayList<>();
    private RecyclerView folkGuideNotificationRv;

    //recyclerView
    FolkGuideNotificationAdapter folkGuideNotificationAdapter;



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

        JodaTimeAndroid.init(this);


        String dataMessage = getIntent().getStringExtra("message");
        final String dataFrom = getIntent().getStringExtra("from_user_id");

//        mNotifData = (TextView) findViewById(R.id.notif_text);

//        mNotifData.setText(" FROM : " + dataFrom + " | MESSAGE : " + dataMessage);





        setFolkGuideAdapter();
        loadFolkGuideNotification();



    }

    private void loadFolkGuideNotification() {
        String currentFolkGuideId = mAuth.getCurrentUser().getUid();
        Toast.makeText(this, "Folk Guide id " + currentFolkGuideId, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onCreate: folk guide id: " + currentFolkGuideId);

        db.collection("FolkGuideNotifications")
                .document(currentFolkGuideId)
                .collection("Notifications")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d(TAG, "onSuccess: " + queryDocumentSnapshots.getDocuments());

                        for (QueryDocumentSnapshot retrievedDocSnap : queryDocumentSnapshots) {

                            FolkBoyData currentFolkBoy = retrievedDocSnap.toObject(FolkBoyData.class);

                            folkBoyDataArrayList.add(currentFolkBoy);
                        }
                        folkGuideNotificationAdapter.notifyDataSetChanged();


                        folkGuideNotificationAdapter.mySetOnClickListner(new FolkGuideNotificationAdapter.MyClickListner() {
                            @Override
                            public void myAcceptReqBtnClick(View view, int currentPosition) {
                                sendAcceptNotification(currentPosition);
                                Toast.makeText(MainActivity.this, "Request Accepted!", Toast.LENGTH_SHORT).show();
//
                            }

                            @Override
                            public void myDeclineReqBtnClick(View view, int currentPosition) {
                                // showCustomDialog(currentPosition);
                            }
                        });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });

    }


    private void setFolkGuideAdapter() {
        folkGuideNotificationRv = findViewById(R.id.rv_fg_noti);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        ((LinearLayoutManager) layoutManager).setOrientation(RecyclerView.VERTICAL);
        folkGuideNotificationRv.setLayoutManager(layoutManager);


        folkGuideNotificationAdapter = new FolkGuideNotificationAdapter(MainActivity.this, folkBoyDataArrayList);
        folkGuideNotificationRv.setAdapter(folkGuideNotificationAdapter);
    }

    public void sendAcceptNotification(int currentPostition) {

        //get folk boy to whom notification is to be sent.
        FolkBoyData currentFolkBoy = folkBoyDataArrayList.get(currentPostition);
        String folkBoyid = currentFolkBoy.getFb_id();

        //before booking bed, check for the booked beds
        BookAftercheckingBookedBedInDatabase(folkBoyid);


    }




    private void BookAftercheckingBookedBedInDatabase(final String folk_boy_id) {
        Log.d(TAG, "BookAftercheckingBookedBedInDatabase: in");
        final ArrayList<String> arrayListBookedBedNo = new ArrayList<>();
//
        CollectionReference BookedBedColRef
                = db.collection("BookedBed");

        db.collection("BookedBed")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot retrievedDoc : queryDocumentSnapshots) {
                            String booked_bed_no = retrievedDoc.getString("booked_bed");
                            arrayListBookedBedNo.add(booked_bed_no);
                        }

                        ////for debugging
                        Log.d(TAG, "onSuccess: arraylistBookedBed" + Arrays.toString(arrayListBookedBedNo.toArray()));
//                    Log.d(TAG, "onSuccess: last booked bed: " + arrayListBookedBedNo.get(arrayListBookedBedNo.size()-1));
                        Toast.makeText(MainActivity.this, "Booked Bed: " + Arrays.toString(arrayListBookedBedNo.toArray()), Toast.LENGTH_SHORT).show();

                        nowBookBed(arrayListBookedBedNo, folk_boy_id);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });


    }
    private void nowBookBed(ArrayList<String> arrayListBookedBedNo, String folk_boy_id) {
        Log.d(TAG, "nowBookBed: in");

        String allotedBedNo = "1";

        if (arrayListBookedBedNo.size() ==1) {

            Map<String, Object> allotedBed = new HashMap<>();
            allotedBed.put("booked_bed", allotedBedNo);

            db.collection("BookedBed")
                    .add(allotedBed);
        } else {
//            String last_booked_bed = arrayListBookedBedNo.get(arrayListBookedBedNo.size()-1);

            int latest_bed_alloted = Integer.parseInt(Collections.max(arrayListBookedBedNo));

            allotedBedNo = "" + (latest_bed_alloted + 1);

            Map<String, Object> allotedBed = new HashMap<>();
            allotedBed.put("booked_bed", allotedBedNo);

            db.collection("BookedBed")
                    .add(allotedBed);

            Log.d(TAG, "nowBookBed: alloted_bed_no: " + allotedBedNo);
            Toast.makeText(getApplicationContext(), "last booked bed: " + latest_bed_alloted, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "sendAcceptNotification: last booked bed: " + latest_bed_alloted);

        }


        ////capture time at which notification is sent.
        Date What_Is_Today = Calendar.getInstance().getTime();
        //for current date, not used
        SimpleDateFormat Dateformat = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = Dateformat.format(What_Is_Today);

        //for current time
        SimpleDateFormat timeformat = new SimpleDateFormat("HH: mm: ss");
        String currentTime = timeformat.format(What_Is_Today);

        //joda time current time
        DateTime jtCurrentTime = DateTime.now();
        jtCurrentTime.withZone(DateTimeZone.forID("Asia/Calcutta"));

        Log.d(TAG, "jt current time: " + jtCurrentTime);



        // Format for input
        DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");
//        String strJtCurrentTime = dtf.parse(jtCurrentTime.toString());
        String strJtCurrentTime = jtCurrentTime.toString();

        DateTime.parse("201711201515",DateTimeFormat.forPattern("yyyyMMddHHmm")).toString("yyyyMMdd");



        Toast.makeText(this, currentDate + " " + currentTime, Toast.LENGTH_LONG).show();
        Log.d(TAG, "sendAcceptNotification: " + currentDate + " " + currentTime);
        ////capture time ends.

        ////collect notification detail.
        String comment = "Congratulation! " + "\n" + "Your accommodation has been booked.";

        HashMap<String, Object> notification = new HashMap<>();
        notification.put("notification: ", comment);
        notification.put("reqAcceptedTime", strJtCurrentTime);
        //        notification.put("fbName", folkBoyName);
        notification.put("allotedBedNo", allotedBedNo);
//        Log.d(TAG, "sendAcceptNotification: alloted bed" + allotedBedNo);


        db.collection("FolkBoyNotifications")
                .document(folk_boy_id)
                .set(notification);


    }

}
