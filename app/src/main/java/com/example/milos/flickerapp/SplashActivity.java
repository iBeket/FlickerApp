package com.example.milos.flickerapp;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * Created by Milos on 08-Aug-17.
 */

public class SplashActivity extends AppCompatActivity {

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        LinearLayout mainFrame = ((LinearLayout) findViewById(R.id.splash));
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this,
                R.anim.hyperspace_jump);
        mainFrame.startAnimation(hyperspaceJumpAnimation);

        final SharedPreferences sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);

        //handles permissions that will be displayed to user when app starts
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS, Manifest.permission.CAMERA}, 0);
        } else {
            int secondsDelayed = 1;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                    finish();
                }
            }, secondsDelayed * 2000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            int secondsDelayed = 1;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                    finish();
                }
            }, secondsDelayed * 1000);
        } else {
            int secondsDelayed = 1;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Toast.makeText(SplashActivity.this, getString(R.string.unable_to_save_photo), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                    finish();
                }
            }, secondsDelayed * 1000);
        }
    }
}

