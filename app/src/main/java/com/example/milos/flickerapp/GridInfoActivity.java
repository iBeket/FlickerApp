package com.example.milos.flickerapp;

import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Milos on 21-Aug-17.
 */

public class GridInfoActivity extends AppCompatActivity {

    TextView titleGrid;
    TextView authorGrid;
    TextView tagGrid;
    TextView dateTakenGrid;
    ImageView imageGird;
    ImageButton buttonGrid;

    String titleG;
    String authorG;
    String tagG;
    String dateG;
    String linkG;
    String imageG;
    private Boolean isFromGrid;

    private Context context;
    private ClipboardManager clipboardManager;
    private ClipData clipData;
    private SqlHelperFavorites sqlHelper;
    private FlickrModel flikrModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_info);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sqlHelper = new SqlHelperFavorites(getApplicationContext());
        flikrModel = new FlickrModel();
        context = this;

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // geting info from flickrgridAdapter
        titleG = getIntent().getStringExtra("title");
        authorG = getIntent().getStringExtra("author");
        tagG = getIntent().getStringExtra("tag");
        dateG = getIntent().getStringExtra("date");
        linkG = getIntent().getStringExtra("link");
        imageG = getIntent().getStringExtra("image");
        isFromGrid = getIntent().getBooleanExtra("isFromGridAdapter", false);

        //puting information in flickr model
        flikrModel.setTitle(titleG);
        flikrModel.setAuthor(authorG);
        flikrModel.setTags(tagG);
        flikrModel.setDate_taken(dateG);
        flikrModel.setLink(linkG);
        flikrModel.setMedia(imageG);


        titleGrid = (TextView) findViewById(R.id.grid_title);
        titleGrid.setText("Title: " + titleG);

        authorGrid = (TextView) findViewById(R.id.grid_author);
        authorGrid.setText(authorG);

        tagGrid = (TextView) findViewById(R.id.gird_tag);
        tagGrid.setText(tagG);

        dateTakenGrid = (TextView) findViewById(R.id.grid_date);
        dateTakenGrid.setText("Date taken: " + dateG);

        imageGird = (ImageView) findViewById(R.id.image_gird);
        Picasso.with(getApplicationContext())
                .load(getIntent().getStringExtra("image"))
                .into(imageGird);

        imageGird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(imageG);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setDataAndType(uri, "image/*");
                context.startActivity(intent);
            }
        });

        buttonGrid = (ImageButton) findViewById(R.id.button_option_gird);

        buttonGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //background color of the popupmenu
                Context warpper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
                PopupMenu popup = new PopupMenu(warpper, view);
                try {
                    Field[] fields = popup.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(popup);
                            Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                            Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                            setForceIcons.invoke(menuPopupHelper, true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //grid view home screen
                if (!isFromGrid) {
                    popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {

                            //copies the link to clipboard
                            if (item.getTitle().equals("Copy link to clipboard")) {
                                clipboardManager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                                clipData = ClipData.newPlainText("text", linkG);
                                clipboardManager.setPrimaryClip(clipData);
                                Toast.makeText(context, "Link copied to clipboard", Toast.LENGTH_SHORT).show();

                            } else if (item.getTitle().equals("Share post")) {
                                //gives us option to choose on which social network we want to share post
                                List<Intent> targetShareIntents = new ArrayList<>();
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.setType("text/plain");
                                List<ResolveInfo> resInfos = context.getPackageManager().queryIntentActivities(shareIntent, 0);
                                if (!resInfos.isEmpty()) {
                                    for (ResolveInfo resInfo : resInfos) {
                                        String packageName = resInfo.activityInfo.packageName;

                                        if (packageName.contains("com.whatsapp") || packageName.contains("com.google.android.apps.messaging")
                                                || packageName.contains("com.twitter.android") || packageName.contains("com.facebook.orca")
                                                || packageName.contains("com.google.android.gm") || packageName.contains("com.facebook.katana")
                                                || packageName.contains("com.google.android.talk") || packageName.contains("com.skype.raider")
                                                || packageName.contains("com.google.android.apps.plus") || packageName.contains("com.android.mms")) {
                                            Intent intent = new Intent();
                                            intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
                                            intent.setAction(Intent.ACTION_SEND);
                                            intent.setType("text/plain");
                                            intent.putExtra(Intent.EXTRA_TEXT, imageG);
                                            intent.putExtra(Intent.EXTRA_SUBJECT, titleG);
                                            intent.setPackage(packageName);
                                            targetShareIntents.add(intent);
                                        }
                                    }
                                    if (!targetShareIntents.isEmpty()) {
                                        Intent chooserIntent = Intent.createChooser(targetShareIntents.remove(0), "Choose app to share");
                                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toArray(new Parcelable[]{}));
                                        chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(chooserIntent);
                                    }
                                }
                                //saves image on sdcard
                            } else if (item.getTitle().equals("Save image")) {
                                try {
                                    File direct = new File(Environment.getExternalStorageDirectory()
                                            + "/FlickrPhotos");

                                    if (!direct.exists()) {
                                        direct.mkdirs();
                                    }

                                    DownloadManager mgr = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

                                    Uri downloadUri = Uri.parse(imageG);
                                    DownloadManager.Request request = new DownloadManager.Request(
                                            downloadUri);
                                    String title = titleG + ".jpg";
                                    request.setAllowedNetworkTypes(
                                            DownloadManager.Request.NETWORK_WIFI
                                                    | DownloadManager.Request.NETWORK_MOBILE)
                                            .setAllowedOverRoaming(false).setTitle("Demo")
                                            .setDescription("Flickr photos")
                                            .setDestinationInExternalPublicDir("/FlickrPhotos", title);

                                    mgr.enqueue(request);
                                    Toast.makeText(context, "Photo saved to: /sdcard/FlickrPhotos", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Toast.makeText(context, "Unable to safe image", Toast.LENGTH_SHORT).show();
                                }
                            } else if (item.getTitle().equals("Add to Favorites")) {
                                if (!sqlHelper.ifExists(flikrModel)) {
                                    sqlHelper.addContact(flikrModel);
                                    Toast.makeText(context, "Item is added to favorites", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Item is already added", Toast.LENGTH_SHORT).show();
                                }
                            } else if (item.getTitle().equals("Share via email")) {

                                Intent intent = new Intent(context, SendEmailActivity.class);
                                intent.putExtra("imageEmail", flikrModel.getMedia());
                                startActivity(intent);
                            }
                            return true;
                        }
                    });
                    popup.show();

                    //gridView Favorites
                } else {
                    popup.getMenuInflater().inflate(R.menu.favorites_menu, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {

                            //copies the link to clipboard
                            if (item.getTitle().equals("Copy link to clipboard")) {
                                clipboardManager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                                clipData = ClipData.newPlainText("text", linkG);
                                clipboardManager.setPrimaryClip(clipData);
                                Toast.makeText(context, "Link copied to clipboard", Toast.LENGTH_SHORT).show();

                            } else if (item.getTitle().equals("Share post")) {
                                //gives us option to choose on which social network we want to share post
                                List<Intent> targetShareIntents = new ArrayList<>();
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.setType("text/plain");
                                List<ResolveInfo> resInfos = context.getPackageManager().queryIntentActivities(shareIntent, 0);
                                if (!resInfos.isEmpty()) {
                                    for (ResolveInfo resInfo : resInfos) {
                                        String packageName = resInfo.activityInfo.packageName;

                                        if (packageName.contains("com.whatsapp") || packageName.contains("com.google.android.apps.messaging")
                                                || packageName.contains("com.twitter.android") || packageName.contains("com.facebook.orca")
                                                || packageName.contains("com.google.android.gm") || packageName.contains("com.facebook.katana")
                                                || packageName.contains("com.google.android.talk") || packageName.contains("com.skype.raider")
                                                || packageName.contains("com.google.android.apps.plus") || packageName.contains("com.android.mms")) {
                                            Intent intent = new Intent();
                                            intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
                                            intent.setAction(Intent.ACTION_SEND);
                                            intent.setType("text/plain");
                                            intent.putExtra(Intent.EXTRA_TEXT, imageG);
                                            intent.putExtra(Intent.EXTRA_SUBJECT, titleG);
                                            intent.setPackage(packageName);
                                            targetShareIntents.add(intent);
                                        }
                                    }
                                    if (!targetShareIntents.isEmpty()) {
                                        Intent chooserIntent = Intent.createChooser(targetShareIntents.remove(0), "Choose app to share");
                                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toArray(new Parcelable[]{}));
                                        chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(chooserIntent);
                                    }
                                }
                                //saves image on sdcard
                            } else if (item.getTitle().equals("Save image")) {
                                try {
                                    File direct = new File(Environment.getExternalStorageDirectory()
                                            + "/FlickrPhotos");

                                    if (!direct.exists()) {
                                        direct.mkdirs();
                                    }

                                    DownloadManager mgr = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

                                    Uri downloadUri = Uri.parse(imageG);
                                    DownloadManager.Request request = new DownloadManager.Request(
                                            downloadUri);
                                    String title = titleG + ".jpg";
                                    request.setAllowedNetworkTypes(
                                            DownloadManager.Request.NETWORK_WIFI
                                                    | DownloadManager.Request.NETWORK_MOBILE)
                                            .setAllowedOverRoaming(false).setTitle("Demo")
                                            .setDescription("Flickr photos")
                                            .setDestinationInExternalPublicDir("/FlickrPhotos", title);

                                    mgr.enqueue(request);
                                    Toast.makeText(context, "Photo saved to: /sdcard/FlickrPhotos", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Toast.makeText(context, "Unable to safe image", Toast.LENGTH_SHORT).show();
                                }
                            } else if (item.getTitle().equals("Delete from favorites")) {

                                sqlHelper.deleteFromBase(flikrModel);
                                Toast.makeText(context, "Item removed", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, FavoritesActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } else if (item.getTitle().equals("Share via email")) {

                                Intent intent = new Intent(context, SendEmailActivity.class);
                                intent.putExtra("imageEmail", flikrModel.getMedia());
                                startActivity(intent);
                            }
                            return true;
                        }
                    });
                    popup.show();
                }
            }
        });
    }

    //back key to go to the previous screen
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
