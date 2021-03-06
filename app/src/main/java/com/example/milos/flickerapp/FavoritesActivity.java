package com.example.milos.flickerapp;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Milos on 31-Aug-17.
 */

public class FavoritesActivity extends AppCompatActivity {

    private Context context;
    private GridView gridView;
    private ListView listView;
    private FlickrAdapter flickrAdapter;
    private FlickrGidAdapter flickrGidAdapter;
    ArrayList<FlickrModel> flickrModels = new ArrayList<>();
    private SqlHelperFavorites sqlHelper;
    private TextView textView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        context = this;
        getSupportActionBar().setTitle(getString(R.string.fav_name));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gridView = (GridView) findViewById(R.id.fav_grid);
        listView = (ListView) findViewById(R.id.fav_list);
        textView = (TextView) findViewById(R.id.fav_info);

        gridView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);

        // geting info from flickrgridAdapter
        sqlHelper = new SqlHelperFavorites(context);
        flickrModels = (ArrayList<FlickrModel>) sqlHelper.getAllInfo();

        //if there is no image added to favorites
        if (flickrModels.size() == 0) {
            textView.setText(getString(R.string.fav_nothing_added));
            textView.bringToFront();
        } else {
            //filling grid adapter with inforamtion
            flickrGidAdapter = new FlickrGidAdapter(context, R.layout.flickr_grid_item, flickrModels);
            gridView.setAdapter(flickrGidAdapter);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        //getMenuInflater().inflate(R.menu.back_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

                return true;
            case R.id.list_view:
                listView.setVisibility(View.VISIBLE);
                gridView.setVisibility(View.GONE);
                //if there are no search results
                if (flickrModels.size() == 0) {
                    textView.setText(getString(R.string.fav_nothing_added));
                    textView.bringToFront();
                } else {
                    flickrAdapter = new FlickrAdapter(context, R.layout.flickr_item, flickrModels);
                    listView.setAdapter(flickrAdapter);
                }

                return true;
            case R.id.gird_view:
                gridView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);

                flickrGidAdapter = new FlickrGidAdapter(context, R.layout.flickr_grid_item, flickrModels);
                gridView.setAdapter(flickrGidAdapter);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
