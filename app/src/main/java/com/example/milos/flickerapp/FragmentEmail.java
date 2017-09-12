package com.example.milos.flickerapp;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Milos on 07-Sep-17.
 */

public class FragmentEmail extends Fragment {
    //final int RQS_PICKCONTACT = 1;
    private TextView email_info;

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
        getPermissionToReadUserContacts();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        email_info = view.findViewById(R.id.email_contacts);
        final Uri uriContact = ContactsContract.Contacts.CONTENT_URI;
        Intent intentPickContact = new Intent(Intent.ACTION_PICK, uriContact);
        startActivityForResult(intentPickContact, PermissionConstant.RQS_PICKCONTACT);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub


        if (resultCode == RESULT_OK) {
            if (requestCode == PermissionConstant.RQS_PICKCONTACT) {
                Uri returnUri = data.getData();
                Cursor cursor = getActivity().getContentResolver().query(returnUri, null, null, null, null);

                if (cursor.moveToNext()) {
                    int columnIndex_ID = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                    String contactID = cursor.getString(columnIndex_ID);

                    //int columnIndex_HASPHONENUMBER = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
                   // String stringHasPhoneNumber = cursor.getString(columnIndex_HASPHONENUMBER);

                    //if (stringHasPhoneNumber.equalsIgnoreCase("1")) {
                        Cursor cursorNum = getActivity().getContentResolver().query(
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=" + contactID,
                                null,
                                null);

                        //Get the first phone number
                        if (cursorNum.moveToNext()) {
                            int columnIndex_email = cursorNum.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
                            String stringEmail = cursorNum.getString(columnIndex_email);
                            SendEmailActivity sendEmail = (SendEmailActivity) getActivity();
                            if (AppState.isClicked) {
                                sendEmail.sendTo.setText(stringEmail);
                            } else if (AppState.isClickedCc) {
                                sendEmail.sendCc.setText(stringEmail);
                            }
                        } else {
                            Toast.makeText(getContext(), "This contact does not have Email Address", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
    //}

    public void getPermissionToReadUserContacts() {

        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(
                    android.Manifest.permission.READ_CONTACTS)) {
            }

            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS},
                    PermissionConstant.RQS_PICKCONTACT);
        }
    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == PermissionConstant.RQS_PICKCONTACT) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Read Contacts permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Read Contacts permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
