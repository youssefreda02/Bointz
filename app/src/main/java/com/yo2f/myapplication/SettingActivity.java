package com.yo2f.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingActivity extends AppCompatActivity {

    public static String email;
    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;
    String mypreferences1 = "mypref";


    private DatabaseReference d;
    SharedPreferences pref1;
    ProgressDialog progress;


    private EditText profile_et_name, profile_et_email, profile_et_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        profile_et_name = findViewById(R.id.profile_et_name);
        profile_et_email = findViewById(R.id.profile_et_email);
        profile_et_phone = findViewById(R.id.profile_et_phone);

        firebaseAuth = FirebaseAuth.getInstance();

        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser==null){
            startActivity(new Intent(SettingActivity.this,LoginActivity.class));
        }
        TextView signUP = findViewById(R.id.sign_up_tv);
        signUP.setOnClickListener(v -> {
           startActivity( new Intent(SettingActivity.this,UpdatePasswordActivity.class));
        });

        d = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        d.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                email=dataSnapshot.child("email").getValue(String.class);
                profile_et_name.setHint(dataSnapshot.child("name").getValue(String.class));
                profile_et_email.setHint(email);
                profile_et_phone.setHint(dataSnapshot.child("phone").getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Couldn't Fetch Data (Check Your Internet Connection)", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkinputs() {

        if (!TextUtils.isEmpty(profile_et_email.getText().toString())) {
            if (!TextUtils.isEmpty(profile_et_name.getText().toString())){
                if (!TextUtils.isEmpty(profile_et_phone.getText().toString())){
                    updateAccount();
                }
                else {
                    profile_et_phone.setError("Phone Cannot Be Empty");
                    Toast.makeText(this, "Phone number can't be empty", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                profile_et_name.setError("Name Cannot Be Empty");

                Toast.makeText(this, "Name can't be empty", Toast.LENGTH_SHORT).show();
            }
        } else {
            profile_et_email.setError("Email Cannot Be Empty");
            Toast.makeText(getApplicationContext(), "Please Fill Your Email", Toast.LENGTH_SHORT).show();
        }

    }
    private void updateAccount() {
        final String name = profile_et_name.getText().toString();
        final String phone = profile_et_phone.getText().toString();
        progress = new ProgressDialog(SettingActivity.this);
        progress.setTitle("Updating Account...");
        progress.setMessage("Taking you to next page...");

        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
        firebaseAuth = FirebaseAuth.getInstance();

        currentUser = firebaseAuth.getCurrentUser();
        currentUser.updateEmail(email).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String error = "Error "+e.toString();
                Toast.makeText(SettingActivity.this, error, Toast.LENGTH_LONG).show();
                finish();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                d.child("name").setValue(name);
                d.child("phone").setValue(phone);
                d.child("email").setValue(email);
                pref1 = getSharedPreferences(mypreferences1, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref1.edit();
                editor.putString("username", name);
                editor.commit();

                Intent mainIntent = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();

            }
        });







    }

    public void save(View view) {
        checkinputs();
    }
}
