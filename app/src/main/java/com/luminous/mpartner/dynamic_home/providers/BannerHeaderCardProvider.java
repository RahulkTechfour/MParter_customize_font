package com.luminous.mpartner.dynamic_home.providers;

import android.app.Activity;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.luminous.cardslibrary.card.Card;
import com.luminous.mpartner.R;
import com.luminous.mpartner.dynamic_home.entities.HomeCardEntity;
import com.luminous.mpartner.dynamic_home.entities.ImageEntity;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BannerHeaderCardProvider extends LuminousCardProvider {

    ViewPager viewPager;
    CirclePageIndicator circlePageIndicator;
    int count;
    public static Timer timer;
    static Handler handler;


    @Override
    protected void onCreated() {
        super.onCreated();
        setLayout(R.layout.card_banner_header);
        Log.e("Garvit", "created");
    }


    @Override
    public void render(@NonNull final View view, @NonNull final Card card) {
        super.render(view, card);
        Log.e("Garvit", "render");

        final HomeCardEntity homeCardEntity = (HomeCardEntity) getData();

        viewPager = (ViewPager) findViewById(view, R.id.viewpager, ViewPager.class);
        circlePageIndicator = (CirclePageIndicator) findViewById(view, R.id.indicator, CirclePageIndicator.class);

        viewPager.setClipToPadding(false);

        List<ImageEntity> banners = homeCardEntity.getBannercardData();
        if (banners != null && banners.size() > 0) {
            setTripOffersData(banners, homeCardEntity);
        }
    }

    public void setTripOffersData(final List<ImageEntity> banners, HomeCardEntity homeCardEntity) {

        viewPager.setAdapter(new BannerViewPagerAdapter(getContext(), banners));
        viewPager.setCurrentItem(0);
        circlePageIndicator.setViewPager(viewPager);

        // Timer for auto sliding
//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                ((Activity) getContext()).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (count <= banners.size()) {
//                            viewPager.setCurrentItem(count);
//                            count++;
//                        } else {
//                            count = 0;
//                            viewPager.setCurrentItem(count);
//                        }
//                    }
//                });
//            }
//        }, 3000, 3000);


        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (count <= banners.size()) {
                    viewPager.setCurrentItem(count);
                    Log.e("Garvit", count + " >> " + banners.size());
                    count++;
                } else {
                    count = 0;
                    viewPager.setCurrentItem(0);
                }
                handler.postDelayed(this, 3000);
            }
        }, 3000);

//        viewPager.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                timer.cancel();
//                timer.purge();
//                return false;
//            }
//        });

    }

    public static void stopTimer() {
        handler.removeCallbacksAndMessages(null);
//        timer.cancel();
//        timer.purge();
    }

}