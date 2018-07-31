package com.uwu.cstgroupproject.pocketdoctor;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

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

    private ProgressDialog Loadingbar;
    private StorageReference userProfileImageRef;

    final static int Gallery_Pick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        SettingsuserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

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

        Loadingbar = new ProgressDialog(this);

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
                    String usrbloodgoup = dataSnapshot.child("bloodgoup").getValue().toString();

                    Picasso.with(SettingsActivity.this).load(usrprofileimage).placeholder(R.drawable.profile).into(userprofileimage);
                    status.setText(usrstatus);
                    username.setText(usrusername);
                    fullname.setText(usrfullname);
                    city.setText(usrcity);
                    dob.setText(usrdob);
                    gender.setText(usrgender);
                    height.setText(usrheight);
                    weight.setText(usrweight);
                    bloodgroup.setText(usrbloodgoup);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        UpdateAccountSettigs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ValidateAccountSetings();
            }
        });

        userprofileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            Uri ImageUri = data.getData();

            CropImage.activity(ImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK)
            {
                Loadingbar.setTitle("Updating Profile Image");
                Loadingbar.setMessage("Please Wait While We are Updating Your Pocket Doctor Profile Image..");
                Loadingbar.setCanceledOnTouchOutside(true);
                Loadingbar.show();


                Uri resultUri = result.getUri();

                StorageReference filePath = userProfileImageRef.child(currentUserId + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful())
                        {
                            Toast.makeText(SettingsActivity.this,"Profile Image Uploaded Successfully!",Toast.LENGTH_SHORT).show();

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            SettingsuserRef.child("profileimage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful())
                                            {
                                                Intent selfIntent = new Intent(SettingsActivity.this,SettingsActivity.class);
                                                startActivity(selfIntent);

                                                Toast.makeText(SettingsActivity.this, "Profile Image Saved and Stored Successfully!", Toast.LENGTH_SHORT).show();
                                                Loadingbar.dismiss();
                                            }
                                            else
                                            {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(SettingsActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                                Loadingbar.dismiss();
                                            }
                                        }
                                    });
                        }
                        else
                        {
                            String mess= task.getException().getMessage();
                            Toast.makeText(SettingsActivity.this, "Error Occured: " + mess, Toast.LENGTH_SHORT).show();
                            Loadingbar.dismiss();
                        }

                    }
                });

            }
        }

    }



    private void ValidateAccountSetings()
    {

        String valstatus = status.getText().toString();
        String valusername = username.getText().toString();
        String valfullname = fullname.getText().toString();
        String valcity = city.getText().toString();
        String valdob = dob.getText().toString();
        String valgender = gender.getText().toString();
        String valheight = height.getText().toString();
        String valweight = weight.getText().toString();
        String valbloodgoup = bloodgroup.getText().toString();

        if(TextUtils.isEmpty(valstatus))
        {
            Toast.makeText(this, "Please Enter Your Status..", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(valusername))
        {
            Toast.makeText(this, "Please Enter Your Username..", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(valfullname))
        {
            Toast.makeText(this, "Please Enter Your Fullname..", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(valcity))
        {
            Toast.makeText(this, "Please Enter Your City..", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(valdob))
        {
            Toast.makeText(this, "Please Select Your Date Of Birth..", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(valgender))
        {
            Toast.makeText(this, "Please Select Your Gender..", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(valheight))
        {
            Toast.makeText(this, "Please Enter Your Height..", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(valweight))
        {
            Toast.makeText(this, "Please Enter Your Weight..", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(valbloodgoup))
        {
            Toast.makeText(this, "Please Enter Your Blood Group..", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Loadingbar.setTitle("Updating Account Settings");
            Loadingbar.setMessage("Please Wait While We are Updating Your Pocket Doctor Account Settings..");
            Loadingbar.setCanceledOnTouchOutside(true);
            Loadingbar.show();
            UpdateAccountInfo(valstatus,valusername,valfullname,valcity,valdob,valgender,valheight,valweight,valbloodgoup);
        }
    }

    private void UpdateAccountInfo(String valstatus, String valusername, String valfullname, String valcity, String valdob, String valgender, String valheight, String valweight, String valbloodgoup)
    {
        HashMap userMap = new HashMap();
        userMap.put("username",valusername);
        userMap.put("fullname",valfullname);
        userMap.put("city",valcity);
        userMap.put("dob",valdob);
        userMap.put("gender",valgender);
        userMap.put("status",valstatus);
        userMap.put("height",valheight);
        userMap.put("weight",valweight);
        userMap.put("bloodgoup",valbloodgoup);

        SettingsuserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {
             if(task.isSuccessful())
             {
                 Toast.makeText(SettingsActivity.this, "Account Settings Updated Successfully..", Toast.LENGTH_SHORT).show();
                 SendUserToMainActivity();
                 Loadingbar.dismiss();
             }
             else
             {
                 Toast.makeText(SettingsActivity.this, "Error Occured While Updating Account Settings..", Toast.LENGTH_SHORT).show();
                 Loadingbar.dismiss();
             }
            }
        });

    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}
