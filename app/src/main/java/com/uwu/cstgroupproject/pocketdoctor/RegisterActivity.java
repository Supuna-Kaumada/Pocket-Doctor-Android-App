package com.uwu.cstgroupproject.pocketdoctor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText UserEmail, UserPassword, UserConfPassword;
    private Button CreateAccount;

    private ProgressDialog Loadingbar;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        UserEmail = (EditText)findViewById(R.id.reg_email);
        UserPassword = (EditText)findViewById(R.id.reg_password);
        UserConfPassword = (EditText)findViewById(R.id.reg_confirmpassword);
        CreateAccount = (Button)findViewById(R.id.reg_createaccount);

        Loadingbar = new ProgressDialog(this);


        CreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateNewAccount();
            }
        });

    }

    @Override
    protected void onStart()
    {
        super.onStart();

       FirebaseUser currentuser = mAuth.getCurrentUser();
        if(currentuser != null)
        {
            SendUserToMainActivity();
        }

    }


    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }



    private void CreateNewAccount() {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        String confirmpassword = UserConfPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please Enter Your E-mail",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please Enter Your Password",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(confirmpassword)){
            Toast.makeText(this,"Please Confirm Your Password",Toast.LENGTH_SHORT).show();
        }
        else if(!password.equals(confirmpassword)){
            Toast.makeText(this,"Your Password Do not match with Confirm Password",Toast.LENGTH_SHORT).show();
        }
        else{

            Loadingbar.setTitle("Creating New Account");
            Loadingbar.setMessage("Please Wait While We are Creating Your New Pocket Doctor Account..");
            Loadingbar.show();
            Loadingbar.setCanceledOnTouchOutside(true);

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                SendUserToSetupActivity();

                                Toast.makeText(RegisterActivity.this,"You Are Authenticated Successfully",Toast.LENGTH_SHORT).show();
                                Loadingbar.dismiss();
                            }
                            else{
                                String message = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this,"Error Occured " + message,Toast.LENGTH_SHORT).show();
                                Loadingbar.dismiss();
                           }
                        }
                    });
        }


    }

    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(RegisterActivity.this,SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }
}
