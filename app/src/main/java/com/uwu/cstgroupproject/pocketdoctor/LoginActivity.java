package com.uwu.cstgroupproject.pocketdoctor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private Button LoginButton;
    private EditText UserEmail, UserPassword;
    private TextView Reglink;
    private ProgressDialog Loadingbar;

    private FirebaseAuth mAuth;

    private ImageView GoogleSigninButton;
    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleSignInClient;
    private static final String TAG = "LoginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();


        LoginButton = (Button)findViewById(R.id.login);
        UserEmail = (EditText)findViewById(R.id.login_email);
        UserPassword = (EditText)findViewById(R.id.login_password);
        Reglink = (TextView)findViewById(R.id.Register_link);

        GoogleSigninButton = (ImageView)findViewById(R.id.signin_google);

        Loadingbar = new ProgressDialog(this);

        Reglink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToRegisterActivity();
            }
        });


        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllowUserToLogin();
            }
        });

        // Configure Google Sign In
         GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

         mGoogleSignInClient = new GoogleApiClient.Builder(this)
                 .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                     @Override
                     public void onConnectionFailed(@NonNull ConnectionResult connectionResult) 
                     {
                         Toast.makeText(LoginActivity.this, "Connection To Google Sign In Failed..", Toast.LENGTH_SHORT).show();
                     }
                 })
                 .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                 .build();

        GoogleSigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


    }



    //Google  SignIn

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN)
        {
            Loadingbar.setTitle("Google Sign In");
            Loadingbar.setMessage("Please Wait While We are Allowing You to Login Using Google Account..");
            Loadingbar.setCanceledOnTouchOutside(true);
            Loadingbar.show();

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if(result.isSuccess())
            {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                Toast.makeText(this, "Please wait, while we are getting your authentication result..", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Couldn't get authentication result..", Toast.LENGTH_SHORT).show();
                Loadingbar.dismiss();
            }

        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Log.d(TAG, "signInWithCredential:success");
                            SendUserToMainActivity();
                            Loadingbar.dismiss();

                        } else
                        {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            String message = task.getException().toString();
                            SendUserToLoginActivity();
                            Toast.makeText(LoginActivity.this, "Not Authenticated: " + message, Toast.LENGTH_SHORT).show();
                            Loadingbar.dismiss();
                        }


                    }
                });
    }


    @Override
    protected void onStart()
    {
        super.onStart();

       FirebaseUser currentuser = mAuth.getCurrentUser();
        if(currentuser != null){
            SendUserToMainActivity();
        }
    }


    private void AllowUserToLogin()
    {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this,"Please Enter Your E-mail",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Please Enter Your Password",Toast.LENGTH_SHORT).show();
        }
        else {

            Loadingbar.setTitle("Login");
            Loadingbar.setMessage("Please Wait While We are Allowing You to Login to Pocket Doctor..");
            Loadingbar.setCanceledOnTouchOutside(true);
            Loadingbar.show();

            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                SendUserToMainActivity();

                                Toast.makeText(LoginActivity.this,"You Are Logged In Successfully.",Toast.LENGTH_SHORT).show();
                                Loadingbar.dismiss();
                            }
                            else{
                                String message = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this,"Error Occured " + message,Toast.LENGTH_SHORT).show();
                                Loadingbar.dismiss();
                            }
                        }
                    });

        }
    }



    private void SendUserToLoginActivity() {
        Intent mainIntent = new Intent(LoginActivity.this,LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
    }
}
