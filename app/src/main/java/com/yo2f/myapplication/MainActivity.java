package com.yo2f.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public static int SPINS, POINTS, ORDERS, SPINNING ,USED=0;
    public static String PHONE;
    public Toast toast;
    TextView pointsTv, spinsTv;
    Button bt, spBt;
    int backbtn, rewAd = 0, opend = 0;
    String mypreferences1 = "mypref";
    SharedPreferences pref1, pref2;
    private AdView mAdView;
    private RewardedAd mRewardedAd;
    private InterstitialAd mInterstitialAd;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    // db for me and database for the user
    private DatabaseReference databaseReference, db;
    ProgressDialog progressDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        USED +=1;
        if (USED ==1){
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();}
        setContentView(R.layout.activity_main);
        ImageView imageView = findViewById(R.id.imgb);
        ConstraintLayout constraintLayout = findViewById(R.id.parent);
        TextView textView = findViewById(R.id.version_tv);
        if (USED ==1){
            textView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            constraintLayout.setVisibility(View.INVISIBLE);
        }

        if (isInternetAvailable()){
            new Handler().postDelayed(() -> {
                imageView.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);
                constraintLayout.setVisibility(View.VISIBLE);
                toast.makeText(this,"Welcome",Toast.LENGTH_SHORT).show();
                getSupportActionBar().show();
            }, 7000);
        }
        else {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Checking Internet Connection...");
            progressDialog.setMessage("Please Wait!");
            progressDialog.setCancelable(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
            progressDialog.closeOptionsMenu();
           new Handler().postDelayed(() -> {
               progressDialog.cancel();
               toast.makeText(this,"Please Check your Internet Connection",Toast.LENGTH_LONG).show();
               startActivity(new Intent(MainActivity.this, LoginActivity.class));
           },2000);

        }
        bt = findViewById(R.id.earn_bt);
        pointsTv = findViewById(R.id.points);
        spinsTv = findViewById(R.id.spins);
        mAdView = findViewById(R.id.adView);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();

        }
        pref2 = getSharedPreferences(mypreferences1, Context.MODE_PRIVATE);
        POINTS = pref2.getInt("points", 0);
        SPINS = pref2.getInt("spins",0);
        spBt = findViewById(R.id.button3);
        db = FirebaseDatabase.getInstance().getReference("admin");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PHONE = snapshot.child("phone").getValue(String.class);
                ORDERS = snapshot.child("orders").getValue(Integer.class);

                POINTS = snapshot.child("points").getValue(Integer.class);
                // This for the available spins for the user.
                SPINS = snapshot.child("spinner").getValue(Integer.class);
                // Spinning is the total spinning done.
                SPINNING = snapshot.child("spinning").getValue(Integer.class);

                spinsTv.setText(String.valueOf(SPINS));
                pointsTv.setText(String.valueOf(POINTS));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        mRewardedAd = createAndLoadRewardedAd(getString(R.string.main_rew));

        MobileAds.initialize(MainActivity.this, initializationStatus -> {
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        bt.setOnClickListener(v -> {
            bt.setClickable(false);
            if (rewAd == 0) {
                if (isInternetAvailable()) {
                            if (mRewardedAd.isLoaded()) {
                                Activity   activityContext = MainActivity.this;
                                RewardedAdCallback adCallback = new RewardedAdCallback() {
                                    @Override
                                    public void onRewardedAdOpened() {

                                        // Ad opened.
                                    }

                                    @Override
                                    public void onRewardedAdClosed() {
                                        // Ad closed.
                                        mRewardedAd = createAndLoadRewardedAd(getString(R.string.main_rew));
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                rewAd -= 1;
                                                if (mInterstitialAd != null) {
                                                    mInterstitialAd.show(MainActivity.this);
                                                } else {
                                                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                                                }
                                            }
                                        }, 5000);

                                    }

                                    @Override
                                    public void onUserEarnedReward(@NonNull RewardItem reward) {

                                        SPINS += reward.getAmount();
                                        rewAd = 1;
                                        opend = 1;
                                        databaseReference.child("spinner").setValue(SPINS);
                                        db.child("Logs").child(currentUser.getUid()).child("Spins").child(DateFormat.getInstance().format(new Date())).setValue(SPINS);
                                        pointsTv.setText(String.valueOf(POINTS));
                                        spinsTv.setText(String.valueOf(SPINS));
                                        spBt.setVisibility(View.VISIBLE);
                                        spBt.setClickable(true);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                rewAd = 0;
                                                if (mInterstitialAd != null) {
                                                    mInterstitialAd.show(MainActivity.this);
                                                    createAndLoadRewardedAd(getString(R.string.main_rew));
                                                } else {
                                                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                                                }
                                            }
                                        }, 10000);
                                    }

                                    @Override
                                    public void onRewardedAdFailedToShow(AdError adError) {
                                        // Ad failed to display.
                                    }
                                };
                                mRewardedAd.show(activityContext, adCallback);
                            } else {
                                Log.d("TAG", "The rewarded ad wasn't loaded yet.");
                                toast.makeText(MainActivity.this, "Opps The Service not Loaded yet", Toast.LENGTH_SHORT).show();
                                mRewardedAd = createAndLoadRewardedAd(getString(R.string.main_rew));
                            }
                }
                else {
                    Log.d(" Earn button pressed"," But no network");
                    toast.makeText(MainActivity.this, "Please check your Internet", Toast.LENGTH_LONG).show();
                }
            }
            else {
                toast.makeText(MainActivity.this, "Please Wait", Toast.LENGTH_LONG).show();
                new Handler().postDelayed(() -> {
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(MainActivity.this);
                        rewAd = 0;
                    } else {
                        Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    }
                }, 15000);
            }
            new Handler().postDelayed(() -> {
                rewAd =0;
                bt.setClickable(true);
            }, 10000);
        });


    }


    public void floatingBt(View view) {
        startActivity(new Intent(MainActivity.this, CheckOutActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        pointsTv.setText(String.valueOf(POINTS));
        spinsTv.setText(String.valueOf(SPINS));
        if (SPINS != 0) {
            spBt.setClickable(true);
            spBt.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        pref1 = getSharedPreferences(mypreferences1, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref1.edit();
        editor.putInt("points", POINTS);
        editor.putInt("spins", SPINS);
        editor.apply();
    }

    public RewardedAd createAndLoadRewardedAd(String adUnitId) {
        RewardedAd rewardedAd = new RewardedAd(this, adUnitId);
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
                super.onRewardedAdLoaded();
                Log.d(" Rewarded Ad Tag", " RewardedAd Loaded");
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                toast.makeText(MainActivity.this, "Opps Sorry to Load the video", Toast.LENGTH_SHORT).show();
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
        InterstitialAd.load(this, getString(R.string.main_int), new AdRequest.Builder().build(),
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
        if (rewardedAd.isLoaded()){
            Log.d(" Main rew Tag"," The rewarded ad loaded");
        }

        return rewardedAd;
    }

    @Override
    public void onBackPressed() {
        if (backbtn == 0) {
            toast.makeText(this, "Press back again to close", Toast.LENGTH_SHORT).show();
            backbtn += 1;
        } else {
            finish();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                backbtn = 0;

            }
        }, 1000);

    }

    public void spinner(View view) {
        if (SPINS == 0 && opend == 0) {
            spBt.setClickable(false);
            toast.makeText(MainActivity.this, " Please earn spins first", Toast.LENGTH_SHORT).show();
            spBt.setVisibility(View.INVISIBLE);
        } else {
            startActivity(new Intent(MainActivity.this, SpinningActivity.class));
        }
    }

    public boolean isInternetAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final boolean b = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return b;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_logout :
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                break;
            case R.id.support:
                startActivity(new Intent(MainActivity.this,AboutActivity.class));
                break;
            case R.id.action_settings :
                startActivity(new Intent(MainActivity.this,SettingActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}