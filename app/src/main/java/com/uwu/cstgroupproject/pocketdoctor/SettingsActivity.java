package com.uwu.cstgroupproject.pocketdoctor;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity
{
    private Toolbar mToolbar;

    private EditText status,username,fullname,city,dob,gender,height,weight,bloodgroup;
    private Button UpdateAccountSettigs;
    private CircleImageView userprofileimage;

    private DatabaseReference SettingsuserRef;
    private FirebaseAuth mAuth;

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        SettingsuserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        mToolbar = (Toolbar)findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        status = (EditText)findViewById(R.id.settings_status);
        username = (EditText)findViewById(R.id.settings_profile_username);
        fullname = (EditText)findViewById(R.id.settings_profile_fullname);
        city = (EditText)findViewById(R.id.settings_profile_city);
        dob = (EditText)findViewById(R.id.settings_dob);
        gender = (EditText)findViewById(R.id.settings_gender);
        height = (EditText)findViewById(R.id.settings_height);
        weight = (EditText)findViewById(R.id.settings_weight);
        bloodgroup =  (EditText)findViewById(R.id.settings_bloodgroup);
        userprofileimage = (CircleImageView)findViewById(R.id.settings_profile_image);

        UpdateAccountSettigs = (Button)findViewById(R.id.update_account_settings_button);

        SettingsuserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String usrprofileimage = dataSnapshot.child("profileimage").getValue().toString();
                    String usrstatus = dataSnapshot.child("status").getValue().toString();
                    String usrusername = dataSnapshot.child("username").getValue().toString();
                    String usrfullname = dataSnapshot.child("fullname").getValue().toString();
                    String usrcity = dataSnapshot.child("city").getValue().toString();
                    String usrdob = dataSnapshot.child("dob").getValue().toString();
                    String usrgender = dataSnapshot.child("gender").getValue().toString();
                    String usrheight = dataSnapshot.child("height").getValue().toString();
                    String usrweight = dataSnapshot.child("weight").getValue().toString();
                    String userbloodgoup = dataSnapshot.child("bloodgoup").getValue().toString();

                    Picasso.with(SettingsActivity.this).load(usrprofileimage).placeholder(R.drawable.profile).into(userprofileimage);
                    status.setText(usrstatus);
                    username.setText(usrusername);
                    fullname.setText(usrfullname);
                    city.setText(usrcity);
                    dob.setText(usrdob);
                    gender.setText(usrgender);
                    height.setText(usrheight);
                    weight.setText(usrweight);
                    bloodgroup.setText(userbloodgoup);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
