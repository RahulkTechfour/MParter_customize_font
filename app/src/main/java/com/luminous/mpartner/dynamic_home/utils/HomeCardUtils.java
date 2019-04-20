package com.luminous.mpartner.dynamic_home.utils;


import android.content.Context;

import com.luminous.cardslibrary.card.Card;
import com.luminous.cardslibrary.card.CardProvider;
import com.luminous.mpartner.dynamic_home.entities.HomeCardEntity;

import java.util.ArrayList;
import java.util.List;

public class HomeCardUtils {
    public static String textViewName = "tv";
    public static String linearLayoutName = "ll";
    public static String imageViewName = "iv";

    public static String[] viewNameArr = {"One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve"};


    private static ArrayList<CardProvider> mCardProviderList = new ArrayList<>();

    public static int getResourceId(Context context, String imageName) {
        return context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
    }

    private static Card getHomeCard(Context context, HomeCardEntity homeCardEntity) {
        try {
            final String className = "com.luminous.mpartner.dynamic_home.providers." + homeCardEntity.getClassName();
            CardProvider mProvider = (CardProvider) Class.forName(className).newInstance();
            if (mProvider == null)
                return null;
            mCardProviderList.add(mProvider);
            return new Card.Builder(context, homeCardEntity)
                    .withProvider(mProvider)
                    .setData(homeCardEntity)
                    .endConfig()
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static List<Card> getHomePageCards(Context context, List<HomeCardEntity> dynamicHomePage) {
        mCardProviderList = new ArrayList<>();
        List<Card> cards = new ArrayList<>();
        if (dynamicHomePage != null && dynamicHomePage.size() > 0) {
            for (int i = 0; i < dynamicHomePage.size(); i++) {
                Card card = getHomeCard(context, dynamicHomePage.get(i));
                if (card == null)
                    continue;
                cards.add(card);
            }
        }

        return cards;
    }

    public static int getIdentifierId(Context context, String viewName) {
        return context.getResources().getIdentifier(viewName, "id", context.getPackageName());
    }
}
