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


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        context = this;
        getSupportActionBar().setTitle("Favorites");

        gridView = (GridView) findViewById(R.id.fav_grid);
        listView = (ListView) findViewById(R.id.fav_list);

        gridView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);

        // geting info from flickrgridAdapter
        sqlHelper = new SqlHelperFavorites(context);
        flickrModels = (ArrayList<FlickrModel>) sqlHelper.getAllInfo();
        flickrModels.toString();

        //filling grid adapter with inforamtion
        flickrGidAdapter = new FlickrGidAdapter(context, R.layout.flickr_grid_item, flickrModels);
        gridView.setAdapter(flickrGidAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (item.getTitle().toString().equalsIgnoreCase("List View")) {
            listView.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);

            flickrAdapter = new FlickrAdapter(context, R.layout.flickr_item, flickrModels);
            listView.setAdapter(flickrAdapter);
        } else {
            if (item.getTitle().toString().equalsIgnoreCase("Grid View")) {
                gridView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);

                flickrGidAdapter = new FlickrGidAdapter(context, R.layout.flickr_grid_item, flickrModels);
                gridView.setAdapter(flickrGidAdapter);
            }
        }
        return true;
    }
}
