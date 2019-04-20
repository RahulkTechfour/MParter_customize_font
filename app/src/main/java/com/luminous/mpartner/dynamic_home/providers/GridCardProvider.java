package com.luminous.mpartner.dynamic_home.providers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.luminous.cardslibrary.card.Card;
import com.luminous.mpartner.R;
import com.luminous.mpartner.customviews.ExpandableHeightGridView;
import com.luminous.mpartner.deeplinking.DeeplinkingHandler;
import com.luminous.mpartner.dynamic_home.entities.HomeCardEntity;
import com.luminous.mpartner.dynamic_home.entities.ImageEntity;
import com.luminous.mpartner.dynamic_home.utils.HomeCardUtils;
import com.luminous.mpartner.utilities.CommonUtility;

public class GridCardProvider extends LuminousCardProvider {

    ExpandableHeightGridView gridView;
    CardView cardView;

    @Override
    protected void onCreated() {
        super.onCreated();
        setLayout(R.layout.card_grid_layout);
    }


    @Override
    public void render(@NonNull final View view, @NonNull final Card card) {
        super.render(view, card);
        final HomeCardEntity homeCardEntity = (HomeCardEntity) getData();

        gridView = (ExpandableHeightGridView) findViewById(view, R.id.gridView, ExpandableHeightGridView.class);
        cardView = (CardView) findViewById(view, R.id.cardView, CardView.class);

        gridView.setExpanded(true);

        final TextView title = (TextView) findViewById(view, R.id.tv_title, TextView.class);

//        if (!TextUtils.isEmpty(homeCardEntity.getTitle())) {
            title.setText(homeCardEntity.getTitle());
//        }
        if (!TextUtils.isEmpty(homeCardEntity.getTitleColor())) {
            title.setTextColor(Color.parseColor(homeCardEntity.getTitleColor()));
        }

        final TextView subtitle = (TextView) findViewById(view, R.id.tv_subtitle, TextView.class);
//        if (!TextUtils.isEmpty(homeCardEntity.getSubTitle())) {
            subtitle.setText(homeCardEntity.getSubTitle());
//        }
        if (!TextUtils.isEmpty(homeCardEntity.getSubtitleColor())) {
            subtitle.setTextColor(Color.parseColor(homeCardEntity.getSubtitleColor()));
        }

        if (!TextUtils.isEmpty(homeCardEntity.getBackgroundImage())) {
            if (homeCardEntity.getBackgroundImage().contains("http")) {
                Glide.with(getContext())
                        .asBitmap()
                        .load(homeCardEntity.getBackgroundImage())
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                resource.setDensity(getContext().getResources().getDisplayMetrics().densityDpi);
                                return false;
                            }
                        })
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                cardView.setBackground(new BitmapDrawable(getContext().getResources(), resource));
                            }
                        });


            } else
                cardView.setBackgroundResource(HomeCardUtils.getResourceId(getContext(), homeCardEntity.getBackgroundImage()));
        }

        initViews(homeCardEntity);
    }

    public void initViews(final HomeCardEntity homeCardEntity) {

        BaseAdapter gridViewAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return (homeCardEntity != null && homeCardEntity.getCardData() != null) ?
                        homeCardEntity.getCardData().size() : 0;
            }

            @Override
            public Object getItem(int position) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public long getItemId(int position) {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final ImageEntity imageEntity = homeCardEntity.getCardData().get(position);
                View grid;
                if (convertView == null) {
                    LayoutInflater li = ((Activity) getContext()).getLayoutInflater();
                    grid = li.inflate(R.layout.grid_item_layout, null);

                    AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.
                            WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT);
                    if (!TextUtils.isEmpty(imageEntity.getImageHeight()) && !TextUtils.isEmpty(imageEntity.getImageWidth())) {
                        lp.width = Integer.parseInt(imageEntity.getImageWidth());
                        lp.height = Integer.parseInt(imageEntity.getImageHeight());
                    } else {
                        lp.width = getContext().getResources().getDimensionPixelSize(R.dimen.card_width);
                        lp.height = getContext().getResources().getDimensionPixelSize(R.dimen.card_height);
                    }

                    CommonUtility.printLog("HomeCard", lp.width + "---------" + lp.height);
                    grid.setLayoutParams(lp);

                } else {
                    grid = convertView;
                }


                ImageView imageView = grid.findViewById(R.id.imageView);
                TextView textView = grid.findViewById(R.id.textView);
                LinearLayout llBackground = grid.findViewById(R.id.ll_background);


                llBackground.setOnClickListener(v -> {
                    if (!TextUtils.isEmpty(imageEntity.getCardAction())) {
                        Intent deeplinkIntent = new Intent(getContext(), DeeplinkingHandler.class);
                        deeplinkIntent.setData(Uri.parse(imageEntity.getCardAction()));
                        getContext().startActivity(deeplinkIntent);
                    }
                });


                if (imageEntity.getMainImage() != null) {
                    Glide.with(getContext()).load(imageEntity.getMainImage())
                            .apply(new RequestOptions().placeholder(R.drawable.grid_default)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL))
                            .into(imageView);
                }


                if (imageEntity.getBackgroundImage() != null) {
                    if (imageEntity.getBackgroundImage().contains("http")) {
                        Glide.with(getContext())
                                .asBitmap()
                                .load(imageEntity.getBackgroundImage())
                                .listener(new RequestListener<Bitmap>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                        resource.setDensity(getContext().getResources().getDisplayMetrics().densityDpi);
                                        return false;
                                    }
                                })
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        llBackground.setBackground(new BitmapDrawable(getContext().getResources(), resource));
                                    }
                                });


                    } else
                        llBackground.setBackgroundResource(HomeCardUtils.getResourceId(getContext(), imageEntity.getBackgroundImage()));
                }


                if (!TextUtils.isEmpty(imageEntity.getTitle())) {
                    textView.setText(imageEntity.getTitle());
                }
                return grid;
            }
        };

        gridView.setAdapter(gridViewAdapter);
    }
}