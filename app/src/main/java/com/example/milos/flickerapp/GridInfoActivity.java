package com.example.milos.flickerapp;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Milos on 21-Aug-17.
 */

public class GridInfoActivity extends AppCompatActivity {

    TextView titleGrid;
    TextView authorGrid;
    TextView tagGrid;
    TextView dateTakenGrid;
    TextView imageDescription;

    private String titleG;
    private String authorG;
    private String tagG;
    private String dateG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_info);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        titleG = getIntent().getStringExtra("title");
        authorG = getIntent().getStringExtra("author");
        tagG = getIntent().getStringExtra("tag");
        dateG = getIntent().getStringExtra("date");

        imageDescription = (TextView) findViewById(R.id.page_title);
        imageDescription.setText("Image Description");

        titleGrid = (TextView) findViewById(R.id.grid_title);
        titleGrid.setText("Title: " + titleG);

        authorGrid = (TextView) findViewById(R.id.grid_author);
        authorGrid.setText("Picture taken by: " + authorG);

        tagGrid = (TextView) findViewById(R.id.gird_tag);
        tagGrid.setText(tagG);

        dateTakenGrid = (TextView) findViewById(R.id.grid_date);
        dateTakenGrid.setText("Date taken: " + dateG);
    }
}
