package com.example.milos.flickerapp;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MenuItem.OnMenuItemClickListener{

    final Context context = this;
    private ProgressDialog dialog;
    private ListView lv;
    private GridView gv;
    ArrayList<FlickrModel> flickrList = new ArrayList<>();
    private FlickrAdapter flickrAdapter;
    private EditText search;
    final String TAG = "JSON";
    private String url = "https://api.flickr.com/services/feeds/photos_public.gne?tags=kitten&format=json&nojsoncallback=1";
    private JSONPareser pareser = new JSONPareser();
    private FlickrGidAdapter flickrGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        lv = (ListView) findViewById(R.id.listjson);
        gv = (GridView) findViewById(R.id.flickr_grid);
        gv.setVisibility(View.GONE);
        search = (EditText) findViewById(R.id.search);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.logo);
        getSupportActionBar().setTitle(" Flickr App");
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        new getData().execute();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
    }

    public class getData extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context,R.style.AppCompatAlertDialogStyle);
            dialog.setMessage("Please wait");
            dialog.setCancelable(false);
            dialog.show();
            search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    flickrList.clear();
                    final String tags = url.replace("kitten", s.toString());

                    if (s.length() > 0) {
                        search.setGravity(Gravity.LEFT | Gravity.TOP);
                    } else {
                        search.setGravity(Gravity.CENTER);
                    }

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
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
                            flickrAdapter = new FlickrAdapter(getApplicationContext(), R.layout.flickr_item, flickrList);
                            flickrAdapter.notifyDataSetChanged();
                            lv.setAdapter(flickrAdapter);

                            flickrGridAdapter = new FlickrGidAdapter(getApplicationContext(), R.layout.flickr_grid_item, flickrList);
                            flickrGridAdapter.notifyDataSetChanged();
                            gv.setAdapter(flickrGridAdapter);
                        }
                    }.execute();
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }

        @Override
        protected Void doInBackground(String... params) {

            String jsonStr = pareser.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject obj = new JSONObject(jsonStr);
                    JSONArray list = obj.getJSONArray("items");
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

                        flickrList.add(model);


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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
            // Updating parsed JSON data into ListView
            flickrAdapter = new FlickrAdapter(getApplicationContext(), R.layout.flickr_item, flickrList);
            lv.setAdapter(flickrAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().toString().equalsIgnoreCase("List View")) {
            lv.setVisibility(View.VISIBLE);
            gv.setVisibility(View.GONE);

            flickrAdapter = new FlickrAdapter(getApplicationContext(), R.layout.flickr_item, flickrList);
            lv.setAdapter(flickrAdapter);
        } else {
            if (item.getTitle().toString().equalsIgnoreCase("Grid View")) {
                gv.setVisibility(View.VISIBLE);
                lv.setVisibility(View.GONE);

                flickrGridAdapter = new FlickrGidAdapter(getApplicationContext(), R.layout.flickr_grid_item, flickrList);
                gv.setAdapter(flickrGridAdapter);
            }
        }
        return true;
    }
    private CharSequence menuIconWithText(Drawable r, String title) {

        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder.setTitle("      Choose your next step");
        alertDialogBuilder
                .setMessage("                 Click yes to exit!")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.super.onBackPressed();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}

