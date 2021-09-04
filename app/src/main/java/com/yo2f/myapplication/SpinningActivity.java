package com.yo2f.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hifnawy.spinningWheelLib.DimensionUtil;
import com.hifnawy.spinningWheelLib.SpinningWheelView;
import com.hifnawy.spinningWheelLib.WheelEventsListener;
import com.hifnawy.spinningWheelLib.model.MarkerPosition;
import com.hifnawy.spinningWheelLib.model.WheelColorSection;
import com.hifnawy.spinningWheelLib.model.WheelDrawableSection;
import com.hifnawy.spinningWheelLib.model.WheelSection;
import com.hifnawy.spinningWheelLib.model.WheelTextSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpinningActivity extends AppCompatActivity {

    final List<WheelSection> wheelSections = new ArrayList<>();
    private AdView mAdView;
    ImageView homeImage;
    TextView homeText;
    SpinningWheelView wheelView;

    private RewardedAd mRewardedAd;
    private InterstitialAd mInterstitialAd;

    float homeTextScaleX;
    Toast toast ;
    float homeTextScaleY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinning2);

        homeImage = findViewById(R.id.mainBackgroundImage);
        homeText = findViewById(R.id.mainTextView);

        homeTextScaleX = homeText.getScaleX();
        homeTextScaleY = homeText.getScaleY();

        //Init WheelSection list
        wheelSections.add(new WheelDrawableSection(R.drawable.abstract_2));
        wheelSections.add(new WheelTextSection("Hard Luck")
                .setSectionBackgroundColor(ContextCompat.getColor(this, R.color.red))
                .setSectionForegroundColor(ContextCompat.getColor(this, R.color.white)));
        wheelSections.add(new WheelTextSection("Hard Luck")
                .setSectionBackgroundColor(ContextCompat.getColor(this, R.color.green))
        .setSectionForegroundColor(ContextCompat.getColor(this,R.color.white)));
        wheelSections.add(new WheelDrawableSection(R.drawable.abstract_3));
        wheelSections.add(new WheelTextSection("Hard Luck")
                .setSectionBackgroundColor(ContextCompat.getColor(this, R.color.teal_200))
                .setSectionForegroundColor(ContextCompat.getColor(this, R.color.yellow)));
        wheelSections.add(new WheelDrawableSection(R.drawable.abstract_4));
        wheelSections.add(new WheelTextSection("Hard Luck")
                .setSectionBackgroundColor(ContextCompat.getColor(this, R.color.green))
                .setSectionForegroundColor(ContextCompat.getColor(this, R.color.white)));
        wheelSections.add(new WheelDrawableSection(R.drawable.abstract_5));
        wheelSections.add(new WheelTextSection("Hard Luck")
                .setSectionBackgroundColor(ContextCompat.getColor(this, R.color.orange))
                .setSectionForegroundColor(ContextCompat.getColor(this, R.color.yellow)));
        wheelSections.add(new WheelDrawableSection(R.drawable.abstract_6));
        wheelSections.add(new WheelDrawableSection(R.drawable.abstract_7));
        wheelSections.add(new WheelTextSection("Hard Luck")
                .setSectionBackgroundColor(ContextCompat.getColor(this, R.color.yellow))
                .setSectionForegroundColor(ContextCompat.getColor(this, R.color.green)));
        wheelSections.add(new WheelDrawableSection(R.drawable.abstract_8));
        wheelSections.add(new WheelDrawableSection(R.drawable.example_layer_list_drawable));
        wheelSections.add(new WheelTextSection("Hard Luck")
                .setSectionBackgroundColor(ContextCompat.getColor(this, R.color.green))
                .setSectionForegroundColor(ContextCompat.getColor(this, R.color.black)));

        //Init wheelView and set parameters
        wheelView = findViewById(R.id.spinningWheelView);

        wheelView.setWheelSections(wheelSections);
        wheelView.setMarkerPosition(MarkerPosition.TOP);

        wheelView.setWheelBorderLineColor(R.color.border);
        wheelView.setWheelBorderLineThickness(10);

        wheelView.setWheelSeparatorLineColor(R.color.separator);
        wheelView.setWheelSeparatorLineThickness(2);

        wheelView.setFlingVelocityDampening(1.01f);

        //Set wheelEventsListener
        wheelView.setWheelEventsListener(new WheelEventsListener() {
            @Override
            public void onWheelStopped() {
                toast.makeText(SpinningActivity.this, "Wheel Stopped", Toast.LENGTH_SHORT).show();
                InterstitialAd.load(SpinningActivity.this, getString(R.string.spinning_int), new AdRequest.Builder().build(),
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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (mInterstitialAd != null) {
                            mInterstitialAd.show(SpinningActivity.this);
                        } else {
                            Log.d("TAG", "The interstitial ad wasn't ready yet.");
                        }
                    }
                }, 5000);
            }

            @Override
            public void onWheelFlung() {
                if (isInternetAvailable()){
                ObjectAnimator animatorX = ObjectAnimator.ofFloat(homeText, "scaleX", homeTextScaleX);
                ObjectAnimator animatorY = ObjectAnimator.ofFloat(homeText, "scaleY", homeTextScaleY);

                animatorX.setDuration(500);
                animatorY.setDuration(500);

                animatorX.start();
                animatorY.start();

                toast.makeText(SpinningActivity.this, R.string.started, Toast.LENGTH_SHORT).show();}
                else {
                    ProgressDialog progress= new ProgressDialog(SpinningActivity.this);
                    progress.setTitle("Checking Internet Connection...");
                    progress.setMessage("Please Wait!");
                    progress.setCancelable(true);
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.show();
                    progress.closeOptionsMenu();
                    new Handler().postDelayed(progress::cancel,2000);
                    startActivity(new Intent(SpinningActivity.this,MainActivity.class));
                    toast.makeText(SpinningActivity.this,"Please Check Your Internet",Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onWheelSectionChanged(int sectionIndex, double angle) {
                WheelSection section = wheelView.getWheelSections().get(sectionIndex);
                switch (section.getType()) {
                    case TEXT:
                        homeText.setText(((WheelTextSection) section).getText());
                        break;
                    default:
                        homeText.setText(sectionIndex + " Point");

                        break;
                }

            }

            @Override
            public void onWheelSettled(int sectionIndex, double angle) {
                final int selectedIndex = sectionIndex;
                final float scaleUpFactor = 6f;
                final float scaleDownFactor = 2f;

                ObjectAnimator animatorX = ObjectAnimator.ofFloat(homeText, "scaleX", homeText.getScaleX() + DimensionUtil.convertPixelsToDp(scaleUpFactor));
                ObjectAnimator animatorY = ObjectAnimator.ofFloat(homeText, "scaleY", homeText.getScaleY() + DimensionUtil.convertPixelsToDp(scaleUpFactor));
                animatorX.setDuration(300);
                animatorY.setDuration(300);

                animatorX.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        ObjectAnimator animatorXX = ObjectAnimator.ofFloat(homeText, "scaleX",
                                homeText.getScaleX() - DimensionUtil.convertPixelsToDp(scaleDownFactor));
                        animatorXX.setDuration(300);
                        animatorXX.start();
                    }
                });

                animatorY.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        ObjectAnimator animatorYY = ObjectAnimator.ofFloat(homeText, "scaleY",
                                homeText.getScaleY() - DimensionUtil.convertPixelsToDp(scaleDownFactor));
                        animatorYY.setDuration(300);
                        animatorYY.start();
                    }
                });

                animatorX.start();
                animatorY.start();

                Animation fadeOut = AnimationUtils.loadAnimation(SpinningActivity.this, R.anim.fade_out);
                homeImage.startAnimation(fadeOut);

                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        if (MainActivity.SPINS <=0){
                            toast.makeText(SpinningActivity.this, "No more available Spins", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SpinningActivity.this,MainActivity.class));
                        }
                        MainActivity.SPINS -=1;
                        MainActivity.SPINNING +=1;
                        InterstitialAd.load(SpinningActivity.this, getString(R.string.spinning_int2), new AdRequest.Builder().build(),
                                new InterstitialAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
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


                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if ((wheelSections.size() > 0) && selectedIndex >= 0&&isInternetAvailable()) {
                            mRewardedAd=createAndLoadRewardedAd(getString(R.string.spinning_rew));
                            DatabaseReference db= FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());                            int textC;
                            WheelSection section = wheelSections.get(selectedIndex);
                            db.child("spinner").setValue(MainActivity.SPINS);
                            db.child("spinning").setValue(MainActivity.SPINNING);
                            new Handler().postDelayed(() -> {

                                if (mInterstitialAd != null) {
                                    mInterstitialAd.show(SpinningActivity.this);
                                } else {
                                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                                }
                            }, 7000);
                            Log.d(" SpinningActivity TAG"," This is the section "+selectedIndex);
                            switch (section.getType()) {

                                case TEXT:
                                    homeImage.setImageDrawable(null);
                                    homeImage.setBackgroundColor(((WheelTextSection) section).getBackgroundColor());
                                    SpinningActivity.this.homeText.setTextColor(((WheelTextSection) section).getForegroundColor());
                                    break;

                                case DRAWABLE:
                                    homeImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), ((WheelDrawableSection) section).getDrawableRes()));
                                    if (MainActivity.SPINS <=0){
                                        toast.makeText(SpinningActivity.this, "No more available Spins", Toast.LENGTH_SHORT).show();
                                    }
                                    MainActivity.POINTS += sectionIndex;
                                    db.child("points").setValue(MainActivity.POINTS);
                                    if (sectionIndex ==0){
                                        toast.makeText(SpinningActivity.this,"Gzz u won extra spin",Toast.LENGTH_SHORT).show();
                                        MainActivity.SPINS +=1;
                                        db.child("spinner").setValue(MainActivity.SPINS);
                                    }
                                    else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(SpinningActivity.this);
                                        builder.setTitle("Get the double points");
                                        builder.setMessage("Watch this Video");
                                        builder .setPositiveButton("Earn", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                mRewardedAd.show(SpinningActivity.this, new RewardedAdCallback() {
                                                    @Override
                                                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                                        MainActivity.POINTS += sectionIndex;
                                                        db.child("points").setValue(MainActivity.POINTS);

                                                    }

                                                    @Override
                                                    public void onRewardedAdClosed() {
                                                        super.onRewardedAdClosed();
                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                            }
                                                        },60000);
                                                    }
                                                });
                                                dialog.dismiss();
                                            }
                                        })
                                                .setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();

                                                    }
                                                });
                                        builder.show();
                                    }

                                    break;
                                case COLOR:
                                    homeImage.setImageDrawable(null);
                                    homeImage.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), ((WheelColorSection) section).getColor()));

                                    int cB = calculateBrightnessEstimate(((WheelColorSection) section).getColor());

                                    textC = ~cB;

                                    SpinningActivity.this.homeText.setTextColor(Color.rgb(textC, textC, textC));

                                    break;
                                default:
                                    toast.makeText(SpinningActivity.this, "Unkown Wheel Section Type Landed", Toast.LENGTH_SHORT).show();
                            }

                            Animation fadeIn = AnimationUtils.loadAnimation(SpinningActivity.this, R.anim.fade_in);
                            homeImage.startAnimation(fadeIn);
                        }


                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                toast.makeText(SpinningActivity.this, "Finished", Toast.LENGTH_SHORT).show();
            }
        });

        //Finally, generate wheel background
        wheelView.generateWheel();


        wheelView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {
                ObjectAnimator scaleXAnimator = new ObjectAnimator().ofFloat(wheelView, "scaleX", 0f, 1f);
                ObjectAnimator scaleYAnimator = new ObjectAnimator().ofFloat(wheelView, "scaleY", 0f, 1f);

                ObjectAnimator rotationAnimator = new ObjectAnimator().ofFloat(wheelView, "rotation", 0f, 360f);

                scaleXAnimator.setDuration(300);
                scaleYAnimator.setDuration(300);

                scaleXAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        ObjectAnimator scaleXAnimator = new ObjectAnimator().ofFloat(wheelView, "scaleX", 1f, 0.9f);
                        scaleXAnimator.setDuration(100);

                        scaleXAnimator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);

                                ObjectAnimator scaleXAnimator = new ObjectAnimator().ofFloat(wheelView, "scaleX", 0.9f, 1f);
                                scaleXAnimator.setDuration(100);
                                scaleXAnimator.start();

                            }
                        });

                        scaleXAnimator.start();

                    }
                });

                scaleYAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        ObjectAnimator scaleYAnimator = new ObjectAnimator().ofFloat(wheelView, "scaleY", 1f, 0.9f);
                        scaleYAnimator.setDuration(100);

                        scaleYAnimator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);

                                ObjectAnimator scaleYAnimator = new ObjectAnimator().ofFloat(wheelView, "scaleY", 0.9f, 1f);
                                scaleYAnimator.setDuration(100);
                                scaleYAnimator.start();

                            }
                        });

                        scaleYAnimator.start();

                    }
                });

                rotationAnimator.setDuration(300);

                scaleXAnimator.start();
                scaleYAnimator.start();
                rotationAnimator.start();
            }

            @Override
            public void onViewDetachedFromWindow(View view) {

            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        MobileAds.initialize(SpinningActivity.this, initializationStatus -> {
        });
        mAdView.loadAd(adRequest);
        InterstitialAd.load(this, getString(R.string.spinning_int3), new AdRequest.Builder().build(),
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
    }

    public int calculateBrightnessEstimate(int color) {
        int R = (color >> 16) & 0xFF;
        int G = (color >> 8) & 0xFF;
        int B = (color >> 0) & 0xFF;

        return (R + B + G) / 3;
    }




    public void flingWheel(View view) {
        if (MainActivity.SPINS <=0){
            toast.makeText(SpinningActivity.this, "No more available Spins", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SpinningActivity.this,MainActivity.class));
        }
        else {
            wheelView.flingWheel(1000 + (1000 * (int) Math.pow(2, new Random().nextInt(10))), (new Random().nextFloat() > 0.5));
        }

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
                toast.makeText(SpinningActivity.this, "Opps Sorry to Load the video", Toast.LENGTH_SHORT).show();
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);


        return rewardedAd;
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