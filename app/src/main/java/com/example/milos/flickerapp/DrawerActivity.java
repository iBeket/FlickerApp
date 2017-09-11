package com.example.milos.flickerapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context context;
    private ProgressDialog dialog;
    private ListView lv;
    private GridView gv;
    ArrayList<FlickrModel> flickrList = new ArrayList<>();
    private FlickrAdapter flickrAdapter;
    private EditText search;
    final String TAG = "JSON";
    public static final String baseURL = "https://api.flickr.com/services/feeds/photos_public.gne?tags=planet&format=json&nojsoncallback=1";
    private JSONPareser pareser = new JSONPareser();
    private FlickrGidAdapter flickrGridAdapter;
    private SwipeRefreshLayout swipeRefreshList;
    private SqlHelper sqlHelper;
    private TextView entries;
    private DrawerLayout drawer;
    private TextView counter;
    private NavigationView navigationView;
    private SqlHelperFavorites sqlHelperFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Flickr App");

        context = this;
        entries = (TextView) findViewById(R.id.entries);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        counter = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_favorites));


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        lv = (ListView) findViewById(R.id.listjson);
        gv = (GridView) findViewById(R.id.flickr_grid);
        gv.setVisibility(View.GONE);
        search = (EditText) findViewById(R.id.search);
        swipeRefreshList = (SwipeRefreshLayout) findViewById(R.id.list_refresh);

        new getData().execute();

        swipeRefreshList.setColorScheme(new int[]{android.R.color.holo_blue_dark, android.R.color.holo_blue_light, android.R.color.holo_green_dark, android.R.color.holo_green_light});
        swipeRefreshList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //new getData().execute();
                //swipeRefreshList.setRefreshing(false);
                finish();
                Intent intent = new Intent(context, DrawerActivity.class);
                startActivity(intent);
                //flickrList.clear();
            }
        });

        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition =
                        (lv == null || lv.getChildCount() == 0) ?
                                0 : lv.getChildAt(0).getTop();
                swipeRefreshList.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        gv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPositionGrid =
                        (gv == null || gv.getChildCount() == 0) ?
                                0 : gv.getChildAt(0).getTop();
                swipeRefreshList.setEnabled(firstVisibleItem == 0 && topRowVerticalPositionGrid >= 0);
            }
        });

        //calls method every second checking if something is added to favorites
        final Handler handler1 = new Handler();
        final int delay1 = 1000; //milliseconds
        handler1.postDelayed(new Runnable() {
            public void run() {
                initializeCountDrawer();
                handler1.postDelayed(this, delay1);
            }
        }, delay1);

        setMenuCounter(R.id.nav_photos, 20);
    }

    public class getData extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);
            dialog.setMessage("Please wait");
            dialog.setCancelable(false);
            dialog.show();

            swipeRefreshList.setEnabled(false);
            search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                private Timer timer = new Timer();
                private final long DELAY = 500; // milliseconds

                @Override
                public void afterTextChanged(final Editable s) {

                    //if there is now text on search bar cursor is not visible
                    if (s.length() > 0) {
                        search.setCursorVisible(true);
                    } else {
                        search.setCursorVisible(false);
                    }

                    timer.cancel();
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final String tags = baseURL.replace("planet", s.toString());

                                    new AsyncTask<Void, Void, Void>() {
                                        @Override
                                        protected Void doInBackground(Void... voids) {
                                            flickrList.clear();
                                            String searchBar = pareser.makeServiceCall(tags);
                                            if (searchBar != null) {
                                                try {
                                                    JSONObject obj = new JSONObject(searchBar);
                                                    JSONArray list = obj.getJSONArray("items");
                                                    for (int i = 0; i < list.length(); i++) {
                                                        JSONObject jsonObject = list.getJSONObject(i);
                                                        FlickrModel model = new FlickrModel();

                                                        JSONObject media;
                                                        media = jsonObject.getJSONObject("media");
                                                        model.setMedia(media.getString("m"));

                                                        String title = String.valueOf(jsonObject.getString("title"));
                                                        model.setTitle(title);

                                                        model.setLink(jsonObject.getString("link"));

                                                        String dateTaken = jsonObject.getString("date_taken");
                                                        int index = dateTaken.indexOf('T');
                                                        String date = dateTaken.substring(0, index);
                                                        model.setDate_taken(date);
                                                        //  model.setDescription(jsonObject.getString("description"));
                                                        String hashTag = "#" + String.valueOf(jsonObject.getString("tags")).replace(" ", " #");
                                                        model.setTags(hashTag);

                                                        String author = "By:" + String.valueOf(jsonObject.getString("author").replace("nobody@flickr.com", "").replace("(", "").replace(")", ""));
                                                        model.setAuthor(author);

                                                        flickrList.add(model);
                                                        //  sqlHelper.addContact(model);
                                                    }
                                                } catch (Exception e) {
                                                    e.toString();
                                                }
                                            } else {
                                                Log.e(TAG, "Couldn't get json from server.");
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(),
                                                                "Unable to read json",
                                                                Toast.LENGTH_LONG)
                                                                .show();
                                                    }
                                                });
                                            }
                                            return null;
                                        }

                                        @Override
                                        protected void onPostExecute(Void aVoid) {

                                            // Updating parsed JSON data into ListView
                                            flickrAdapter = new FlickrAdapter(getApplicationContext(), R.layout.flickr_item, flickrList);
                                            flickrAdapter.notifyDataSetChanged();
                                            lv.setAdapter(flickrAdapter);

                                            // Updating parsed JSON data into GridView
                                            flickrGridAdapter = new FlickrGidAdapter(context, R.layout.flickr_grid_item, flickrList);
                                            flickrGridAdapter.notifyDataSetChanged();
                                            gv.setAdapter(flickrGridAdapter);

                                            //if there are no search results
                                            if (flickrList.size() == 0) {
                                                entries.setText("No results found");
                                                entries.bringToFront();
                                            } else {
                                                lv.bringToFront();
                                                gv.bringToFront();
                                            }
                                        }
                                    }.execute();
                                }
                            });
                        }
                    }, DELAY);
                }
            });
        }

        @Override
        protected Void doInBackground(String... params) {
            sqlHelper = new SqlHelper(context);

            String jsonStr = pareser.makeServiceCall(baseURL);
            if (jsonStr != null) {
                try {
                    JSONObject obj = new JSONObject(jsonStr);
                    JSONArray list = obj.getJSONArray("items");

                    sqlHelper.clearDatabase();

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject jsonObject = list.getJSONObject(i);
                        FlickrModel model = new FlickrModel();

                        JSONObject media;
                        media = jsonObject.getJSONObject("media");
                        model.setMedia(media.getString("m"));

                        String title = String.valueOf(jsonObject.getString("title")).replace("- The Caturday", "").replace(": http://thecaturday.us", "");
                        model.setTitle(title);

                        model.setLink(jsonObject.getString("link"));

                        String dateTaken = jsonObject.getString("date_taken");
                        int index = dateTaken.indexOf('T');
                        String date = dateTaken.substring(0, index);
                        model.setDate_taken(date);
                        //  model.setDescription(jsonObject.getString("description"));
                        String hashTag = "#" + String.valueOf(jsonObject.getString("tags")).replace(" ", " #");
                        model.setTags(hashTag);

                        String author = "By:" + String.valueOf(jsonObject.getString("author")).replace("nobody@flickr.com", "").replace("(", "").replace(")", "");
                        model.setAuthor(author);

                        //every time app is opened or refresh it will download all 20 items
                        Uri downloadUri = Uri.parse(model.getMedia());
                        model.setLocalPath(downloadUri.getPath());

                        //cache images
                        getTempFile(context, baseURL);

                        //fill the list and database
                        flickrList.add(model);
                        sqlHelper.addContact(model);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                sqlHelper = new SqlHelper(context);

                                //if there is no internet connection update ListView
                                flickrList = (ArrayList<FlickrModel>) sqlHelper.getAllInfo();
                                flickrAdapter = new FlickrAdapter(context, R.layout.flickr_item, flickrList);
                                flickrAdapter.notifyDataSetChanged();
                                lv.setAdapter(flickrAdapter);

                                //if there is no internet connection update GridView
                                flickrGridAdapter = new FlickrGidAdapter(context, R.layout.flickr_grid_item, flickrList);
                                flickrGridAdapter.notifyDataSetChanged();
                                gv.setAdapter(flickrGridAdapter);

                                Toast.makeText(getApplicationContext(),
                                        "No internet connection please check your connectivity",
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            swipeRefreshList.setEnabled(true);

            // start notification service
            startService(new Intent(DrawerActivity.this, JSONService.class));

            // Updating parsed JSON data into ListView
            flickrAdapter = new FlickrAdapter(context, R.layout.flickr_item, flickrList);
            flickrAdapter.notifyDataSetChanged();
            lv.setAdapter(flickrAdapter);

            // Updating parsed JSON data into GridView
            flickrGridAdapter = new FlickrGidAdapter(context, R.layout.flickr_grid_item, flickrList);
            flickrGridAdapter.notifyDataSetChanged();
            gv.setAdapter(flickrGridAdapter);
        }
    }

    @Override
    public void onBackPressed() {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context, R.style.AppCompatAlertDialogStyle);
            alertDialogBuilder.setTitle("         Are you sure you want to exit?");
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            System.exit(0);
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
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
            lv.setVisibility(View.VISIBLE);
            gv.setVisibility(View.GONE);

            flickrAdapter = new FlickrAdapter(getApplicationContext(), R.layout.flickr_item, flickrList);
            lv.setAdapter(flickrAdapter);
        } else {
            if (item.getTitle().toString().equalsIgnoreCase("Grid View")) {
                gv.setVisibility(View.VISIBLE);
                lv.setVisibility(View.GONE);

                flickrGridAdapter = new FlickrGidAdapter(context, R.layout.flickr_grid_item, flickrList);
                gv.setAdapter(flickrGridAdapter);
            }
        }
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_sign_out) {
            Intent intent = new Intent(this, SignInActivity.class);
            intent.putExtra("signout", true);
            AppState.loggedIn = false;
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_photos) {

            lv.setVisibility(View.VISIBLE);
            gv.setVisibility(View.GONE);

            flickrAdapter = new FlickrAdapter(getApplicationContext(), R.layout.flickr_item, flickrList);
            lv.setAdapter(flickrAdapter);

        } else if (id == R.id.nav_favorites) {
            startActivity(new Intent(DrawerActivity.this, FavoritesActivity.class));
        }

        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    //saves images on app`s cash directory
    public File getTempFile(Context context, String url) {
        File file = null;
        try {
            String fileName = Uri.parse(url).getLastPathSegment();
            file = File.createTempFile(fileName, null, context.getCacheDir());
        } catch (IOException e) {
        }
        return file;
    }

    //shows the number of items in drawer menu of favorites
    private void initializeCountDrawer() {
        //Gravity property aligns the text
        sqlHelperFavorites = new SqlHelperFavorites(context);

        counter.setGravity(Gravity.CENTER);
        counter.setPadding(10, 10, 10, 10);
        counter.setTypeface(null, Typeface.BOLD);
        counter.setTextColor(getResources().getColor(R.color.color_text));

        sqlHelperFavorites.getCount();
        long count = sqlHelperFavorites.getCount();
        String temp = String.valueOf(count);

        counter.setText(temp);
    }

    //shows the number of items in drawer menu of FlickrList
    private void setMenuCounter(@IdRes int itemId, int count) {
        TextView view = (TextView) navigationView.getMenu().findItem(itemId).getActionView();
        view.setText(count > 0 ? String.valueOf(count) : null);
    }
}