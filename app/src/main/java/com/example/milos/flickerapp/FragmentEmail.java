package com.example.milos.flickerapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Milos on 07-Sep-17.
 */

public class FragmentEmail extends Fragment {

    public static final FragmentEmail newInstance() {

        FragmentEmail f = new FragmentEmail();
        Bundle bd = new Bundle();

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_email, container, false);
        return view;
    }
}
