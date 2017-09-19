package com.example.milos.flickerapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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

                    //Get the first email
                    if (cursorNum.moveToNext()) {
                        int columnIndex_email = cursorNum.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
                        String stringEmail = cursorNum.getString(columnIndex_email);
                        SendEmailActivity sendEmail = (SendEmailActivity) getActivity();
                        if (AppState.isClicked) {
                            if (isValidEmail(stringEmail)) {
                                sendEmail.sendTo.setText(stringEmail);
                            } else {
                                Toast.makeText(getContext(), getString(R.string.wrong_email_format), Toast.LENGTH_SHORT).show();
                            }
                        } else if (AppState.isClickedCc) {
                            if (isValidEmail(stringEmail)) {
                                sendEmail.sendCc.setText(stringEmail);
                            } else {
                                Toast.makeText(getContext(), getString(R.string.wrong_email_format), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), getString(R.string.email_does_not_exist), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    //}
    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
