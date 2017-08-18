package com.example.milos.flickerapp;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by Milos on 18-Aug-17.
 */

public class FlickrGidAdapter extends ArrayAdapter<FlickrModel> {

    private Context context;

    public FlickrGidAdapter(Context context, int resource, ArrayList<FlickrModel> obj) {
        super(context, resource, obj);
        this.context = context;
    }

    @Nullable
    @Override
    public FlickrModel getItem(int position) {
        return super.getItem(position);
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.flickr_grid_item, null);
            viewHolder = new FlickrGidAdapter.ViewHolder();

            viewHolder.imageGrid = convertView.findViewById(R.id.grid_photo);
            convertView.setTag(viewHolder);
        }/*
          If convertView already exists, just get the tag and set it in the viewHolder attribute
        */ else {
            viewHolder = (FlickrGidAdapter.ViewHolder) convertView.getTag();
        }
        //Populate the row's layout
        final FlickrModel obj = getItem(position);

        Picasso.with(getContext())
                .load(obj.getMedia())
                .into(viewHolder.imageGrid);

        return convertView;
    }

    private static class ViewHolder {
        ImageView imageGrid;
    }
}
