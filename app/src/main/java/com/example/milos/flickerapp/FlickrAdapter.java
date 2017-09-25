package com.example.milos.flickerapp;

import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.text.Html;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
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

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by Milos on 31-Jul-17.
 */

public class FlickrAdapter extends ArrayAdapter<FlickrModel> {
    private Context context;
    private ScaleAnimation scale;
    private ClipboardManager clipboardManager;
    private ClipData clipData;
    private SqlHelperFavorites sqlHelper;


    public FlickrAdapter(Context context, int resource, ArrayList<FlickrModel> obj) {
        super(context, resource, obj);
        this.context = context;
    }

    @Nullable
    @Override
    public FlickrModel getItem(int position) {
        return super.getItem(position);
    }

    /**
     * This method returns the row related view,
     * first, checks if the actual view (convertView) is set
     */
    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        //Initialize the helper class
        final ViewHolder viewHolder;
        /*
          If convertView is not set, inflate the row layout and get its views' references
          then set the helper class as a tag for the convertView
        */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.flickr_item, null);
            viewHolder = new ViewHolder();

            viewHolder.image = convertView.findViewById(R.id.image_flickr);
            viewHolder.title = convertView.findViewById(R.id.title_flickr);
            viewHolder.author = convertView.findViewById(R.id.author_flickr);
            viewHolder.dateTaken = convertView.findViewById(R.id.date_flickr);
            // viewHolder.description = convertView.findViewById(R.id.description_flickr);
            // viewHolder.description.setSelected(true);

            viewHolder.tag = convertView.findViewById(R.id.tag_flickr);
            viewHolder.tag.setSelected(true);

            viewHolder.optionButton = convertView.findViewById(R.id.button_option_flickr);
            convertView.setTag(viewHolder);
        }
        /*
          If convertView already exists, just get the tag and set it in the viewHolder attribute
        */
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Populate the row's layout
        final FlickrModel obj = getItem(position);

        assert obj != null;
        viewHolder.title.setText(obj.getTitle());
        viewHolder.author.setText(obj.getAuthor());
        // viewHolder.description.setText(Html.fromHtml(obj.getDescription()));
        viewHolder.tag.setText(obj.getTags());
        viewHolder.dateTaken.setText(obj.getDate_taken());
        Picasso.with(getContext())
                .load(obj.getMedia())
                .into(viewHolder.image);

        //when picture is clicked go to full screen
        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(obj.getMedia());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setDataAndType(uri, "image/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }

        });

        // popupmenu
        viewHolder.optionButton.setOnClickListener(new View.OnClickListener() {
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

                //ListView home screen
                if (!(context.getClass().getName().equalsIgnoreCase("com.example.milos.flickerapp.FavoritesActivity"))) {

                    popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            //copies the link to clipboard
                            if (item.getTitle().equals(context.getString(R.string.pop_copy_link))) {

                                copyLinkClipboard(obj);
                                //share post
                            } else if (item.getTitle().equals(context.getString(R.string.pop_share_post))) {

                                sharePost(obj);
                                //saves image on sdcard
                            } else if (item.getTitle().equals(context.getString(R.string.pop_save_image))) {

                                saveImage(obj);
                                //adds image to favorites and stores it
                            } else if (item.getTitle().equals(context.getString(R.string.pop_add_to_fav))) {

                                sqlHelper = new SqlHelperFavorites(context);
                                if (!sqlHelper.ifExists(obj)) {
                                    sqlHelper.addContact(obj);
                                    Toast.makeText(context, context.getString(R.string.item_add_to_fav), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, context.getString(R.string.item_already_added), Toast.LENGTH_SHORT).show();
                                }
                                //starts activity EmailActivity
                            } else if (item.getTitle().equals(context.getString(R.string.pop_share_email))) {
                                sendEmail(obj);
                            }

                            return true;
                        }
                    });
                    popup.show();

                    //listView Favorites
                } else {
                    popup.getMenuInflater().inflate(R.menu.favorites_menu, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            //copies the link to clipboard
                            if (item.getTitle().equals(context.getString(R.string.fav_copy_link))) {

                                copyLinkClipboard(obj);
                                //share post
                            } else if (item.getTitle().equals(context.getString(R.string.fav_share_post))) {
                                sharePost(obj);
                                //saves image on sdcard
                            } else if (item.getTitle().equals(context.getString(R.string.fav_save_image))) {

                                saveImage(obj);
                                //deletes image from favorites
                            } else if (item.getTitle().equals(context.getString(R.string.fav_delete_from_fav))) {

                                sqlHelper = new SqlHelperFavorites(context);
                                sqlHelper.deleteFromBase(obj);
                                Toast.makeText(context, context.getString(R.string.item_deleted_from_fav), Toast.LENGTH_SHORT).show();
                                Intent intent1 = new Intent(context, FavoritesActivity.class);
                                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(intent1);
                                //starts activity EmailActivity
                            } else if (item.getTitle().equals(context.getString(R.string.fav_share_email))) {
                                sendEmail(obj);
                            }
                            return true;
                        }
                    });
                    popup.show();
                }
            }
        });

        //starting animation
        animateView();
        convertView.startAnimation(scale);
        return convertView;
    }

    private static class ViewHolder {
        /* This is an helper class used to save
        *  each component of the listView row layout */
        TextView title;
        TextView author;
        // TextView description;
        TextView tag;
        TextView dateTaken;
        ImageView image;
        ImageButton optionButton;
    }

    private void animateView() {
        scale = new ScaleAnimation((float) 1.0, (float) 1.0, (float) 0.0, (float) 1.0);
        scale.setFillAfter(true);
        scale.setDuration(500);
    }

    //copies image link on clipboard
    private void copyLinkClipboard(FlickrModel obj) {

        clipboardManager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        clipData = ClipData.newPlainText("text", obj.getLink());
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(context, context.getString(R.string.copy_link_pop), Toast.LENGTH_SHORT).show();
    }

    //shares post on multiple apps
    private void sharePost(FlickrModel obj) {
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
                    intent.putExtra(Intent.EXTRA_TEXT, obj.getMedia());
                    intent.putExtra(Intent.EXTRA_SUBJECT, obj.getTitle());
                    intent.setPackage(packageName);
                    targetShareIntents.add(intent);
                }
            }
            if (!targetShareIntents.isEmpty()) {
                Intent chooserIntent = Intent.createChooser(targetShareIntents.remove(0), context.getString(R.string.choose_app_to_share));
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toArray(new Parcelable[]{}));
                chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(chooserIntent);
            }
        }
    }

    //saves image on phones storage
    private void saveImage(FlickrModel obj) {
        try {
            File direct = new File(Environment.getExternalStorageDirectory()
                    + "/FlickrPhotos");

            if (!direct.exists()) {
                direct.mkdirs();
            }

            DownloadManager mgr = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

            Uri downloadUri = Uri.parse(obj.getMedia());
            DownloadManager.Request request = new DownloadManager.Request(
                    downloadUri);
            String title = obj.getTitle() + ".jpg";
            request.setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI
                            | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false).setTitle("Demo")
                    .setDescription("Flickr photos")
                    .setDestinationInExternalPublicDir("/FlickrPhotos", title);

            mgr.enqueue(request);
            Toast.makeText(context, context.getString(R.string.photo_saved), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, context.getString(R.string.photo_not_saved), Toast.LENGTH_SHORT).show();
        }
    }

    //calls sendEmail activity
    private void sendEmail(FlickrModel obj) {
        Intent intent = new Intent(context, SendEmailActivity.class);
        intent.putExtra("imageEmail", obj.getMedia());
        context.startActivity(intent);
    }
}
