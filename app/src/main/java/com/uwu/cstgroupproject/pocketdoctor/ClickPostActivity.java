package com.uwu.cstgroupproject.pocketdoctor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickPostActivity extends AppCompatActivity
{

    private ImageView PostImage;
    private TextView PostDescription;
    private Button EditPostButton , DeletePostButton;

    private DatabaseReference ClickPostRef;
    private FirebaseAuth mAuth;

    private String PostKey, currentUserID, databaseUserID , description , image;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        PostKey = getIntent().getExtras().get("PostKey").toString();
        ClickPostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);

        PostImage = (ImageView)findViewById(R.id.click_post_image);
        PostDescription = (TextView)findViewById(R.id.click_post_description);
        EditPostButton = (Button)findViewById(R.id.edit_post_button);
        DeletePostButton = (Button)findViewById(R.id.delete_post_button);

        DeletePostButton.setVisibility(View.INVISIBLE);
        EditPostButton.setVisibility(View.INVISIBLE);

        ClickPostRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
               if(dataSnapshot.exists())
               {
                   description = dataSnapshot.child("description").getValue().toString();
                   image = dataSnapshot.child("postimage").getValue().toString();
                   databaseUserID = dataSnapshot.child("uid").getValue().toString();

                   PostDescription.setText(description);
                   Picasso.with(ClickPostActivity.this).load(image).into(PostImage);


                   if(currentUserID.equals(databaseUserID))
                   {
                       DeletePostButton.setVisibility(View.VISIBLE);
                       EditPostButton.setVisibility(View.VISIBLE);
                   }

                   EditPostButton.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           EditCurrentPost(description);
                       }
                   });
               }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        DeletePostButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DeleteCurrentPost();
            }
        });

    }

    private void EditCurrentPost(String description)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("Edit Post");

        final EditText inputField = new EditText(ClickPostActivity.this);
        inputField.setText(description);
        builder.setView(inputField);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                ClickPostRef.child("description").setValue(inputField.getText().toString());
                Toast.makeText(ClickPostActivity.this, "Post Has Been Updated Successfully.", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.background_dark);

    }

    private void DeleteCurrentPost()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("Delete Post");
        builder.setMessage("Are you sure you want to Delete Post?");
        builder.setCancelable(true);

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                ClickPostRef.removeValue();
                SendUserToMainActivity();
                Toast.makeText(ClickPostActivity.this, "Post Has Been Deleted Successfully.", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.background_dark);


    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(ClickPostActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}
