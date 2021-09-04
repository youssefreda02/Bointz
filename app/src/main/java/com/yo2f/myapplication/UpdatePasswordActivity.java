package com.yo2f.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdatePasswordActivity extends AppCompatActivity {
    EditText oldPass, newPass, conPass ;
    String email ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        email = SettingActivity.email;
        oldPass = findViewById(R.id.profile_et_old_password);
        newPass = findViewById(R.id.profile_et_new_password);
        conPass = findViewById(R.id.profile_et_con_password);
    }

    public void save(View view) {
        checkinputs();
    }
    private void checkinputs() {

        if (!TextUtils.isEmpty(oldPass.getText().toString())) {
            if (!TextUtils.isEmpty(newPass.getText().toString())&&newPass.getText().toString().length()>6){
                if (!TextUtils.isEmpty(conPass.getText().toString())){
                    if (conPass.getText().toString().equals(newPass.getText().toString())){
                    checkOldPass();
                    }
                    else {
                        conPass.setError("rewrite the password");


                    }
                }
                else {
                    conPass.setError("Cannot Be Empty");
                    Toast.makeText(this, "can't be empty", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                newPass.setError("Cannot Be Empty or less than 6");

                Toast.makeText(this, "can't be empty", Toast.LENGTH_SHORT).show();
            }
        } else {
            oldPass.setError("Cannot Be Empty");
            Toast.makeText(getApplicationContext(), "Please Fill All", Toast.LENGTH_SHORT).show();
        }

    }
    public void checkOldPass(){
        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Updating Password...");
        progress.setMessage("Please Wait!");
        progress.setCancelable(true);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
        progress.closeOptionsMenu();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(email,oldPass.getText().toString());
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                user.updatePassword(newPass.getText().toString()).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        progress.cancel();
                        Toast.makeText(UpdatePasswordActivity.this, "Password Updated successfully!", Toast.LENGTH_SHORT).show();
                    }
                    else {progress.cancel();
                        Toast.makeText(UpdatePasswordActivity.this, "Error please try again later!", Toast.LENGTH_SHORT).show();

                    }
                }
                    );
            }
            else {
                oldPass.setError("wrong pass");
                Toast.makeText(UpdatePasswordActivity.this, "Error please try again later!", Toast.LENGTH_SHORT).show();
                progress.cancel();
            }
        });

    }
}