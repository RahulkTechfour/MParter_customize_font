package com.luminous.mpartner.dynamic_home.providers;

import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.luminous.cardslibrary.card.Card;
import com.luminous.mpartner.R;
import com.luminous.mpartner.dynamic_home.entities.HomeCardEntity;
import com.luminous.mpartner.dynamic_home.utils.HomeCardUtils;
import com.luminous.mpartner.utilities.CommonUtility;
import com.luminous.mpartner.utilities.PermissionUtils;

public class schemecardprovider extends LuminousCardProvider {

    private Bitmap bitmap;

    @Override
    protected void onCreated() {
        super.onCreated();
        setLayout(R.layout.price_list_item);
    }


    @Override
    public void render(@NonNull final View view, @NonNull final Card card) {
        super.render(view, card);
        final HomeCardEntity homeCardEntity = (HomeCardEntity) getData();

        final TextView title = (TextView) findViewById(view, R.id.tv_title, TextView.class);
        final ImageView ivPdf = (ImageView) findViewById(view, R.id.ivPdf, ImageView.class);
        final ImageView ivShare = (ImageView) findViewById(view, R.id.ivShare, ImageView.class);


        if (!TextUtils.isEmpty(homeCardEntity.getTitle())) {
            title.setText(homeCardEntity.getTitle());
        }

        if (!TextUtils.isEmpty(homeCardEntity.getTitleColor())) {
            title.setTextColor(Color.parseColor(homeCardEntity.getTitleColor()));
        }

        final TextView subtitle = (TextView) findViewById(view, R.id.tv_subtitle, TextView.class);
        if (!TextUtils.isEmpty(homeCardEntity.getSubTitle())) {
            subtitle.setText(homeCardEntity.getSubTitle());
        } else {
            subtitle.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(homeCardEntity.getSubtitleColor())) {
            subtitle.setTextColor(Color.parseColor(homeCardEntity.getSubtitleColor()));
        }

        ivPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(homeCardEntity.getCardAction())) {
                    if (!PermissionUtils.hasSelfPermission((Activity) getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
                        CommonUtility.openSettingsScreen(getContext());
                    } else {
                        CommonUtility.downloadFile(getContext(), homeCardEntity.getCardAction(), "schemes.pdf");
                    }
                }
            }
        });

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(homeCardEntity.getCardAction())) {
                    if (bitmap != null) {
                        CommonUtility.shareImageLink(homeCardEntity.getCardAction(), getContext(), bitmap);
                    }
                }
            }
        });

        //Image
        final LinearLayout llHeader = (LinearLayout) findViewById(view, R.id.ll_header, LinearLayout.class);
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
                            llHeader.setBackground(new BitmapDrawable(getContext().getResources(), resource));
                        }
                    });


        } else
            llHeader.setBackgroundResource(HomeCardUtils.getResourceId(getContext(), homeCardEntity.getMainImage()));
//Image
        final SubsamplingScaleImageView mainImage = (SubsamplingScaleImageView) findViewById(view, R.id.main_image, SubsamplingScaleImageView.class);

//        PhotoViewAttacher photoAttacher;
//        photoAttacher= new PhotoViewAttacher(mainImage);
//        photoAttacher.update();

        if (homeCardEntity.getMainImage().contains("http")) {
            Glide.with(getContext())
                    .asBitmap()
                    .load(homeCardEntity.getMainImage())
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

                            bitmap = resource;
                            mainImage.setImage(ImageSource.bitmap(resource));
                        }
                    });


        } else
            mainImage.setBackgroundResource(HomeCardUtils.getResourceId(getContext(), homeCardEntity.getMainImage()));

        mainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(homeCardEntity.getCardAction())) {
//                    if (!TextUtils.isEmpty(homeCardEntity.getCardAction())) {
//                        Intent deeplinkIntent = new Intent(getContext(), DeeplinkingHandler.class);
//                        deeplinkIntent.setData(Uri.parse(homeCardEntity.getCardAction()));
//                        getContext().startActivity(deeplinkIntent);
//                    }
                }
            }
        });

    }
}
