package com.example.milos.flickerapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Milos on 08-Sep-17.
 */

public class SendEmailActivity extends AppCompatActivity {

    private String imageEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);

        imageEmail = getIntent().getStringExtra("imageEmail");
        imageEmail.toString();

    }
}
