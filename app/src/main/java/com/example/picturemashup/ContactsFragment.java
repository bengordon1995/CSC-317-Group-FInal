package com.example.picturemashup;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
    Defines a fragment used to obtain and view the users contacts as a list
 */
public class ContactsFragment extends Fragment {
    private Activity containerActivity = null;
    private View inflatedView = null;
    private String sharedImageLocation;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;


    private ListView contactsListView;
    ArrayAdapter<String> contactsAdapter = null;
    private ArrayList<String> contacts = new ArrayList<String>();

    public ContactsFragment() { }


    /*
        Call from instantiating class to define context
     */
    public void setContainerActivity(Activity containerActivity) {
        this.containerActivity = containerActivity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_contacts, container, false);
        System.out.println("onCreateView Called");
        return inflatedView;
    }

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        System.out.println("onCreate Called");
        getContacts();
    }

    @Override
    public void onResume() {
        super.onResume();
        setupContactsAdapter();
    }

    /*
        loads all of the contacts on the users phone into an arraylist to be used by the listView
     */
    public void getContacts() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && containerActivity.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {

            int limit = 1000;
            Cursor cursor = containerActivity.getContentResolver().query(
                    ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            while (cursor.moveToNext() && limit > 0) {
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String given = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String contact = given + " :: " + contactId;
                contacts.add(contact);
                limit--;
            }
            cursor.close();
        }
        System.out.println(contacts.toString());
    }

    /*
        sets up the adapter, and applies an onclicklistener to all the individual list items, which
        spawns a new email intent to the specified recipient
     */
    private void setupContactsAdapter() {
        contactsListView =
                (ListView)containerActivity.findViewById(R.id.contact_list_view);
        contactsAdapter = new ArrayAdapter<String>(containerActivity, R.layout.text_row,
                R.id.text_row_text_view, contacts);
        contactsListView.setAdapter(contactsAdapter);
        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView v = view.findViewById(R.id.text_row_text_view);
                    String text = v.getText().toString();
                    String name = text.substring(0, text.indexOf(" :: "));
                    String recipientID = text.substring(text.indexOf(" :: ") + 4);
                    String email = getEmail(recipientID);

                    //create the email intent and start it
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    String absoluteFileLocation = getArguments().getString("imageAbsoluteLocation");

                    File file = new File(absoluteFileLocation);
                    Uri photoURI = FileProvider.getUriForFile(getActivity(),
                            "com.example.android.fileprovider",
                            file);
                    intent.setType("image/png");
                    intent.putExtra(Intent.EXTRA_STREAM, photoURI);
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
            }
        });
    }

    /*
        given the users id, find their email address and return it
     */
    public String getEmail(String id) {
        Cursor emails = containerActivity.getContentResolver().query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + id, null, null);
        while (emails.moveToNext()) {
            String emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
            return emailAddress;
        }
        emails.close();
        return null;
    }
}