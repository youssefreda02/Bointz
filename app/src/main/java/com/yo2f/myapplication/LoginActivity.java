package com.yo2f.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    TextView signUpTv;

    private EditText et_login_email, et_login_password;
    Button btn_login;
    ProgressDialog progress;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    int backbtn =0;
    private AdView mAdView;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signUpTv = findViewById(R.id.sign_up_tv);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null&& isInternetAvailable() )
        {
            Intent i = new Intent(LoginActivity.this,MainActivity.class );
            startActivity(i);
            finish();

        }
        signUpTv.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this,RegisterActivity.class)));
        btn_login = findViewById(R.id.button2);

        et_login_email = findViewById(R.id.editTextTextEmailAddress);
        et_login_password= findViewById(R.id.editTextTextPassword);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        MobileAds.initialize(LoginActivity.this, initializationStatus -> {
        });

        mAdView.loadAd(adRequest);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                finish();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                // ...
            } else {
                if (response ==null){
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
                    finish();

                }
                try {
                    Toast.makeText(this, response.toString(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                finish();

            }
        }
    }
    private void check() {
        if(!TextUtils.isEmpty(et_login_email.getText().toString())) {
            if (!TextUtils.isEmpty(et_login_password.getText().toString())) {
                signIn();
            }
            else
            {
                et_login_password.setError("Password Cannot Be Empty");
            }
        }
        else
        {
            et_login_email.setError("Email Cannot Be Empty");
        }
    }
    private void signIn() {

        progress = new ProgressDialog(this);
        progress.setTitle("Signing In...");
        progress.setMessage("Please Wait!");
        progress.setCancelable(true);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
        progress.closeOptionsMenu();
        firebaseAuth.signInWithEmailAndPassword(et_login_email.getText().toString(),et_login_password.getText().toString())
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else
                    {
                        progress.cancel();
                        String error = task.getException().getMessage();
                        Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
                    }
                });

    }
    @Override
    public void onBackPressed() {
        if (backbtn==0){
            Toast.makeText(this, "Press back again to close", Toast.LENGTH_SHORT).show();
            backbtn+=1;
        }
        else {
            finish();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                backbtn=0;
            }
        },1000);

    }
    public boolean isInternetAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final boolean b = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return b;
    }
}