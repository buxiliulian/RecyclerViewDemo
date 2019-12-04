package com.umx.recyclerviewdemo;

import android.net.Uri;
import android.provider.ContactsContract;

public interface ContactsQuery {
    Uri URI = ContactsContract.Contacts.CONTENT_URI;

    String[] PROJECTION = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.SORT_KEY_PRIMARY
    };

    String NAME = ContactsContract.Contacts.DISPLAY_NAME;

    String SORT_KEY = ContactsContract.Contacts.SORT_KEY_PRIMARY;
}
