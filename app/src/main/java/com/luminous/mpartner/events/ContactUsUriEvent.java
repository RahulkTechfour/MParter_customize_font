package com.luminous.mpartner.events;

import android.net.Uri;

public class ContactUsUriEvent {
    private Uri uri;

    public ContactUsUriEvent(Uri uri) {
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }
}
