package com.uwu.cstgroupproject.pocketdoctor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity
{
    private TextView status,username,fullname,city,dob,gender,height,weight,bloodgroup,relationship;
    private CircleImageView userprofileimage;

    private DatabaseReference profilesuserRef;
    private FirebaseAuth mAuth;
    private StorageReference userProfileImageRef;

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        profilesuserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        status = (TextView)findViewById(R.id.my_profile_status);
        username = (TextView)findViewById(R.id.my_username);
        fullname = (TextView)findViewById(R.id.my_profile_fullname);
        city = (TextView)findViewById(R.id.my_city);
        dob = (TextView)findViewById(R.id.my_dob);
        gender = (TextView)findViewById(R.id.my_gender);
        height = (TextView)findViewById(R.id.my_height);
        weight = (TextView)findViewById(R.id.my_weight);
        bloodgroup =  (TextView)findViewById(R.id.my_bloodgroup);
        relationship = (TextView) findViewById(R.id.my_relationship);
        userprofileimage = (CircleImageView)findViewById(R.id.my_profile_pic);

        profilesuserRef.addValueEventListener(new ValueEventListener() {
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
                    String usrbloodgoup = dataSnapshot.child("bloodgoup").getValue().toString();
                    String usrrelationship = dataSnapshot.child("relationashipstatus").getValue().toString();

                    Picasso.with(ProfileActivity.this).load(usrprofileimage).placeholder(R.drawable.profile).into(userprofileimage);
                    status.setText(usrstatus);
                    username.setText("@"+usrusername);
                    fullname.setText(usrfullname);
                    city.setText(usrcity);
                    dob.setText("Birthday : "+usrdob);
                    gender.setText("Gender : "+usrgender);
                    height.setText("Height : "+usrheight+"cm");
                    weight.setText("Weight : "+usrweight+"Kg");
                    bloodgroup.setText("Blood Group : "+usrbloodgoup);
                    relationship.setText("Relationship Status : "+usrrelationship);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

    }


}
