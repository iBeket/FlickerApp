package com.example.milos.flickerapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;;
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
    final int RQS_PICKCONTACT = 1;
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
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        email_info = view.findViewById(R.id.email_contacts);
        final Uri uriContact = ContactsContract.Contacts.CONTENT_URI;
        Intent intentPickContact = new Intent(Intent.ACTION_PICK, uriContact);
        startActivityForResult(intentPickContact, RQS_PICKCONTACT);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub


        if (resultCode == RESULT_OK) {
            if (requestCode == RQS_PICKCONTACT) {
                Uri returnUri = data.getData();
                Cursor cursor = getActivity().getContentResolver().query(returnUri, null, null, null, null);

                if (cursor.moveToNext()) {
                    int columnIndex_ID = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                    String contactID = cursor.getString(columnIndex_ID);

                    int columnIndex_HASPHONENUMBER = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
                    String stringHasPhoneNumber = cursor.getString(columnIndex_HASPHONENUMBER);

                    if (stringHasPhoneNumber.equalsIgnoreCase("1")) {
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
                            if(AppState.isClicked){
                                sendEmail.sendTo.setText(stringEmail);
                            }else if (AppState.isClickedCc){
                                sendEmail.sendCc.setText(stringEmail);
                            }
                           // email_info.setText(stringEmail);
                        } else {
                            Toast.makeText(getContext(), "This contact does not have Email Address", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        email_info.setText("Contacts are empty");
                    }
                }
            }
        }
    }
}
