package com.luminous.mpartner.deeplinking;

import android.content.Context;
import android.content.Intent;

import com.luminous.mpartner.home_page.activities.HomePageActivity;

import java.util.List;

public class DeepLinkContactUs {

    public DeepLinkContactUs(List<String> pathSegments, Context context) {
        try {
            Intent intent = new Intent(context, HomePageActivity.class);
            intent.putExtra("tag", pathSegments.get(0));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
