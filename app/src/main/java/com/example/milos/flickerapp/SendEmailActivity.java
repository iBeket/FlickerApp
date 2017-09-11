package com.example.milos.flickerapp;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by Milos on 08-Sep-17.
 */

public class SendEmailActivity extends AppCompatActivity {

    private String imageEmail;
    private EditText emailText;
    private EditText subject;
    public EditText sendTo;
    public EditText sendCc;
    private ImageView addTo;
    private ImageView addCc;
    private Button sendEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);

        imageEmail = getIntent().getStringExtra("imageEmail");

        emailText = (EditText) findViewById(R.id.text_email);
        emailText.setText(imageEmail);

        subject = (EditText) findViewById(R.id.subject_email);
        subject.setText(subject.getHint() + " Check this image out");

        sendTo = (EditText) findViewById(R.id.send_to);
        sendCc = (EditText) findViewById(R.id.send_to_cc);

        addTo = (ImageView) findViewById(R.id.add_to_email);
        addCc = (ImageView) findViewById(R.id.add_to_cc);

        sendEmail = (Button) findViewById(R.id.send_email_button);

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        addTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppState.isClicked = true;
                AppState.isClickedCc = false;
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentEmail fragment = FragmentEmail.newInstance();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(android.R.id.content, fragment, "Contant_fragment");
                fragmentTransaction.commit();
            }
        });

        addCc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppState.isClickedCc = true;
                AppState.isClicked = false;
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentEmail fragment = FragmentEmail.newInstance();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(android.R.id.content, fragment, "Contant_fragment");
                fragmentTransaction.commit();
            }
        });
    }
}