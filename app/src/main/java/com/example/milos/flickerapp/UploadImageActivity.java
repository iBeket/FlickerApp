package com.example.milos.flickerapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * Created by Milos on 09-Oct-17.
 */

public class UploadImageActivity extends AppCompatActivity {

    private static final int SELECT_PHOTO = 100;
    private static final int CAMERA_REQUEST = 1992;

    ImageButton uploadButtonOptions;
    ImageView uploadImage;
    EditText uploadTitle;
    EditText uploadDescription;
    EditText uploadTags;
    Button uploadButton;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        context = this;

        getSupportActionBar().setTitle(getString(R.string.upload_image_name));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        uploadImage = (ImageView) findViewById(R.id.image_upload);
        uploadTitle = (EditText) findViewById(R.id.title_upload);
        uploadDescription = (EditText) findViewById(R.id.description_upload);
        uploadTags = (EditText) findViewById(R.id.tags_upload);
        uploadButton = (Button) findViewById(R.id.upload_image_button);
        uploadButtonOptions = (ImageButton) findViewById(R.id.button_upload);

        uploadButtonOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //background color of the popupmenu
                Context warpper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
                PopupMenu popup = new PopupMenu(warpper, v);
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
                popup.getMenuInflater().inflate(R.menu.upload_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals(context.getString(R.string.upload_camera))) {
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);

                        } else if (item.getTitle().equals(context.getString(R.string.upload_gallery))) {
                            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                            photoPickerIntent.setType("image/*");
                            startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                        }
                        return true;
                    }

                });
                popup.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap imageGallery = BitmapFactory.decodeStream(imageStream);
                    uploadImage.setImageBitmap(imageGallery);
                }
            case CAMERA_REQUEST:
                if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {

                    Bundle extras = imageReturnedIntent.getExtras();
                    Bitmap bmp = (Bitmap) extras.get("data");
                    uploadImage.setImageBitmap(bmp);
                }
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
