package com.luminous.mpartner.deeplinking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.luminous.mpartner.activities.ProfileActivity;
import com.luminous.mpartner.home_page.activities.HomePageActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DeeplinkingHandler extends AppCompatActivity {
    List<String> pathSegments;
    private int TYPE;

    Bundle bundle;
    private Uri data;
    String src;
    Intent i = null;

    Map<Integer, String> hashTable;
    Context context;

    @SuppressLint("UseSparseArrays")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        Bundle bundle = this.getIntent().getExtras();

        if (bundle != null && bundle.containsKey("src"))
            src = bundle.getString("src");


        hashTable = new HashMap<Integer, String>();
        hashTable.put(1, "Profile");
        hashTable.put(2, "contact-us");
        hashTable.put(3, "scheme");
        hashTable.put(4, "KnowMore");
        hashTable.put(5, "Media");
        hashTable.put(6, "Catalog");
        hashTable.put(7, "PriceList");
        hashTable.put(8, "Connect");
        hashTable.put(9, "Notification");
        hashTable.put(10, "LoadCalculator");
        hashTable.put(11, "Gallery");
        hashTable.put(12, "Lucky7");
        hashTable.put(13, "ServiceEscalation");
        hashTable.put(14, "Faqs");
        hashTable.put(15, "Reports");
        hashTable.put(16, "Lucky7Dealer");
        // change
        hashTable.put(17, "Sales");
        hashTable.put(18, "DealerManagement");
        pathSegments = new ArrayList<String>();
        Intent intent = getIntent();
        bundle = this.getIntent().getExtras();

        try {
            data = intent.getData();

            if (data == null) {
                data = Uri.parse(bundle.getString("Uri"));
            }

            pathSegments = data.getPathSegments();
            TYPE = getDeepLinkClassType(pathSegments.get(0));

            switch (TYPE) {
                case 1:
//                    new DeepLinkContactUs(pathSegments, DeeplinkingHandler.this);
                    i = new Intent(context, ProfileActivity.class);
                    break;
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:

// change
                case 17:
                case 18:
                    i = new Intent(context, HomePageActivity.class);
                    break;
                default:
                    i = new Intent(context, HomePageActivity.class);
                    break;
            }

            i.putExtra("tag", pathSegments.get(0));
            startActivity(i);
        } catch (Exception e) {
            Intent i = new Intent(DeeplinkingHandler.this, HomePageActivity.class);
            startActivity(i);
        }
        finish();
    }

    private int getDeepLinkClassType(String deepLinkType) {
        int type = 0;

        for (Map.Entry entry : hashTable.entrySet()) {
            if (deepLinkType.equalsIgnoreCase(entry.getValue().toString())) {
                type = (Integer) entry.getKey();
            }
        }
        return type;
    }

}
