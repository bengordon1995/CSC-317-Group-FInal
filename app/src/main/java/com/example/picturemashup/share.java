package com.example.picturemashup;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.picturemashup.R;

import java.util.ArrayList;

public class share extends Fragment {
    private Activity containerActivity = null;
    private View inflatedView = null;
    private String screenShotLocation;

    private ListView contactsListView;
    ArrayAdapter<String> contactsAdapter = null;
    private ArrayList<String> contacts = new ArrayList<String>();

    public share() { }

    public void setContainerActivity(Activity containerActivity) {
        this.containerActivity = containerActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_share, container, false);
        return inflatedView;
    }

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
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
        int limit = 1000;
        Cursor cursor = containerActivity.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
        while (cursor.moveToNext() && limit > 0) {
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String given = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contact = given + " :: " + contactId;
            contacts.add(contact);
            limit--;
        }
        cursor.close();
    }

    /*
        sets up the adapter, and applies an onclicklistener to all the individual list items, which
        spawns a new email intent to the specified recipient
     */
    private void setupContactsAdapter() {
        contactsListView =
                (ListView)containerActivity.findViewById(R.id.contact_list_view);
        contactsAdapter = new
                ArrayAdapter<String>(containerActivity, R.layout.text_row,
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
                intent.setType("image/png");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(getArguments().getString("imageUriString")));
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