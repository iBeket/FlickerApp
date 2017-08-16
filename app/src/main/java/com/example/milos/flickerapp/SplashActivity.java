package com.example.milos.flickerapp;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

/**
 * Created by Milos on 08-Aug-17.
 */

public class SplashActivity extends AppCompatActivity {

    //private boolean visible = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.logo);
        getSupportActionBar().setTitle(" Flickr App");
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
           // visible = false;
        } else {
            int secondsDelayed = 1;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }, secondsDelayed * 2000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
           // visible = true;
            int secondsDelayed = 1;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }, secondsDelayed * 1000);
        } else {
            int secondsDelayed = 1;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Toast.makeText(SplashActivity.this, "You are not able to safe photos", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }, secondsDelayed * 1000);
        }
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.popup_menu,menu);
//        menu.clear();
//        if (visible == false) {
//            menu.findItem(R.id.dropdown_menu3).setVisible(false);
//        }else{
//            menu.findItem(R.id.dropdown_menu3).setVisible(true);
//        }
//        return super.onPrepareOptionsMenu(menu);
//    }
}

