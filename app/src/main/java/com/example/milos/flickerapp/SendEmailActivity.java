package com.example.milos.flickerapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Milos on 08-Sep-17.
 */

public class SendEmailActivity extends AppCompatActivity {

    private String imageEmail;
    public EditText sendTo;
    public EditText sendCc;

    private Boolean isToAdded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);

        getSupportActionBar().setTitle(getString(R.string.send_email_name));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageEmail = getIntent().getStringExtra("imageEmail");

        EditText emailText = (EditText) findViewById(R.id.text_email);
        emailText.setText(imageEmail);

        EditText subject = (EditText) findViewById(R.id.subject_email);
        subject.setText(subject.getHint() + getString(R.string.email_subject));

        sendTo = (EditText) findViewById(R.id.send_to);
        sendCc = (EditText) findViewById(R.id.send_to_cc);

        ImageView addTo = (ImageView) findViewById(R.id.add_to_email);
        ImageView addCc = (ImageView) findViewById(R.id.add_to_cc);

        final Button sendEmail = (Button) findViewById(R.id.send_email_button);
        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isToAdded || sendTo.getText().toString().trim().length() == 0) {
                    Toast.makeText(SendEmailActivity.this, "You need to enter Email Address first", Toast.LENGTH_SHORT).show();
                } else {
                    composeEmail();
                }

            }
        });

        //when clicked go to contacts fragment
        addTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppState.isClicked = true;
                AppState.isClickedCc = false;
                isToAdded = true;
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentEmail fragment = FragmentEmail.newInstance();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(android.R.id.content, fragment, "Contant_fragment");
                fragmentTransaction.commit();
            }
        });

        //when clicked go to contacts fragment
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

    public void composeEmail() {
        String[] addresses = new String[1];
        addresses[0] = sendTo.getText().toString();

        String[] addressesCc = new String[1];
        addressesCc[0] = sendCc.getText().toString();

        String subject = getString(R.string.email_subject);
        String emailBody = imageEmail;

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_CC, addressesCc);
        intent.putExtra(Intent.EXTRA_TEXT, emailBody);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}