package com.yo2f.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

public class CheckOutActivity extends AppCompatActivity {
    EditText numberEt;
    Button orangeBt, etislatBt, vodBt, weBt,rechargeBt;
    ProgressDialog progress;
    Toast toast;
    DatabaseReference databaseReference, database, db;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String time;
    public  int ORDERSNUM , num;
    private InterstitialAd mInterstitialAd;
    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        numberEt = findViewById(R.id.editTextNumber);
        rechargeBt = findViewById(R.id.button4);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Order");
        db = FirebaseDatabase.getInstance().getReference("admin");
        database = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        numberEt.setText(MainActivity.PHONE);
        db = FirebaseDatabase.getInstance().getReference("admin");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ORDERSNUM= snapshot.child("OrdersNum").getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                toast.makeText(CheckOutActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        String [] strings = {
                getResources().getString(R.string.orange),
               getResources().getString(R.string.etisalat),
                getResources().getString(R.string.vodafone),
                getResources().getString(R.string.we)};
        rechargeBt.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.title);
            builder .setSingleChoiceItems(strings, 0, (dialog, which) -> {
               num =which;
            })
                    .setPositiveButton(R.string.postive, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            switch (num){
                                case 0:
                                    chechOut(2);
                                    return;
                                case 1:
                                    chechOut(1);
                                    return;
                                case 2:
                                    chechOut(0);
                                    return;
                                case 3:
                                    chechOut(5);
                                    return;
                                default:
                                    toast.makeText(CheckOutActivity.this,"Please Choose 1",Toast.LENGTH_SHORT).show();
                                    return;
                            }
                        }
                    })
                    .setNegativeButton(R.string.negative, (dialog, id) -> dialog.dismiss());
            builder.show();

        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        MobileAds.initialize(CheckOutActivity.this, initializationStatus -> {
        });

        mAdView.loadAd(adRequest);
        if (mInterstitialAd != null) {
            mInterstitialAd.show(CheckOutActivity.this);}
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showInistAd(getString(R.string.checkout_inis));
                }
            }, 5000);
        }
    }
    private void showInistAd(String adId){
        InterstitialAd.load(this, adId, new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("Main Activity ", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("Main Activity ", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
        if (mInterstitialAd != null) {
            mInterstitialAd.show(CheckOutActivity.this);
        }
    }
    private void chechOut(int num) {
            String  number =numberEt.getText().toString();
            if (!TextUtils.isEmpty(number)&&number.length()==11&& number.startsWith("01")) {
                if (MainActivity.POINTS >= 50) {
                    progress = new ProgressDialog(CheckOutActivity.this);
                    MainActivity.POINTS -= 50;
                    MainActivity.ORDERS += 1;
                    ORDERSNUM +=1;
                    FirebaseDatabase.getInstance().getReference("admin").child("OrdersNum").setValue(ORDERSNUM);
                    database.child("points").setValue(MainActivity.POINTS);
                    database.child("orders").setValue(MainActivity.ORDERS);
                    time = DateFormat.getInstance().format(new Date());
                    databaseReference.child(String.valueOf(ORDERSNUM)).child("time").setValue(time);
                    databaseReference.child(String.valueOf(ORDERSNUM)).child("userId").setValue(user.getUid());
                    databaseReference.child(String.valueOf(ORDERSNUM)).child("number").setValue(number);
                    numberEt.setText("");
                    // We company
                    if (num ==5) {
                        progress.setTitle("WE Recharge...");
                        progress.setMessage("Sending your request!");
                        progress.setCancelable(true);
                        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progress.show();
                        progress.closeOptionsMenu();
                        databaseReference.child(String.valueOf(ORDERSNUM)).child("Company").setValue("WE");
                        db.child("Orders").child("WE").child(String.valueOf(ORDERSNUM)).setValue(number);
                        db.child("Logs").child(user.getUid()).child("Orders").child(String.valueOf(MainActivity.ORDERS)).setValue(time);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progress.cancel();
                                toast.makeText(CheckOutActivity.this, "WE recharge request done, Please wait for confirmation", Toast.LENGTH_LONG).show();
                            }
                        }, 2000);
                    }
                    // Etisalat company
                else if (num==1){
                        progress.setTitle("Etisalat Recharge...");
                        progress.setMessage("Sending your request!");
                        progress.setCancelable(true);
                        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progress.show();
                        progress.closeOptionsMenu();
                        databaseReference.child(String.valueOf(ORDERSNUM)).child("Company").setValue("Etisalat");
                        db.child("Orders").child("Etisalat").child(String.valueOf(ORDERSNUM)).setValue(number);
                        db.child("Logs").child(user.getUid()).child("Orders").child(String.valueOf(MainActivity.ORDERS)).setValue(time);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progress.cancel();
                                toast.makeText(CheckOutActivity.this, "Etisalat recharge request done, Please wait for confirmation", Toast.LENGTH_LONG).show();
                            }
                        }, 2000);


                    }
                // Orange company
                else if (num == 2){
                        progress = new ProgressDialog(CheckOutActivity.this);
                        progress.setTitle("Orange Recharge...");
                        progress.setMessage("Sending your request!");
                        progress.setCancelable(true);
                        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progress.show();
                        progress.closeOptionsMenu();
                        databaseReference.child(String.valueOf(ORDERSNUM)).child("Company").setValue("Orange");
                        db.child("Orders").child("Orange").child(String.valueOf(ORDERSNUM)).setValue(number);
                        db.child("Logs").child(user.getUid()).child("Orders").child(String.valueOf(MainActivity.ORDERS)).setValue(time);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progress.cancel();
                                toast.makeText(CheckOutActivity.this, "Orange recharge request done, Please wait for confirmation", Toast.LENGTH_LONG).show();
                            }
                        }, 2000);
                    }
                // Vodafone Company
                else if (num == 0){
                        progress.setTitle("Vodafone Recharge...");
                        progress.setMessage("Sending your request!");
                        progress.setCancelable(true);
                        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progress.show();
                        progress.closeOptionsMenu();
                        numberEt.setText("");
                        databaseReference.child(String.valueOf(ORDERSNUM)).child("Company").setValue("Vodafone");
                        db.child("Orders").child("Vodafone").child(String.valueOf(ORDERSNUM)).setValue(number);
                        db.child("Logs").child(user.getUid()).child("Orders").child(String.valueOf(MainActivity.ORDERS)).setValue(time);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progress.cancel();
                                toast.makeText(CheckOutActivity.this, "Vodafone recharge request done, Please wait for confirmation", Toast.LENGTH_LONG).show();

                            }
                        }, 2000);

                    }
                }
                else {
                    toast.makeText(CheckOutActivity.this, getResources().getString(R.string.no_enough), Toast.LENGTH_LONG).show();
                }
            } else {
                toast.makeText(CheckOutActivity.this, "Please Write the phone number", Toast.LENGTH_LONG).show();
                numberEt.setError("Write the phone number");
            }
            showInistAd(getString(R.string.checkout_inis));
        }



}