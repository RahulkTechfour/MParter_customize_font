package com.luminous.mpartner.dynamic_home.providers;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.luminous.mpartner.R;
import com.luminous.mpartner.dynamic_home.entities.ImageEntity;

import java.util.List;


public class BannerViewPagerAdapter extends PagerAdapter {

    private Context _context;
    private List<ImageEntity> banners;
    private LayoutInflater _layoutInflater;

    public BannerViewPagerAdapter(Context context, List<ImageEntity> banners) {
        this._context = context;
        this.banners = banners;
        _layoutInflater = LayoutInflater.from(_context);

    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = _layoutInflater.inflate(R.layout.single_image_layout, container, false);
        if (itemView != null) {


            final ImageEntity imageEntity = banners.get(position);

            ImageView iv_item_image = itemView.findViewById(R.id.iv_item_image);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });


            if (imageEntity != null && !TextUtils.isEmpty(imageEntity.getBackgroundImage())) {
                Glide.with(_context)
                        .load(imageEntity.getBackgroundImage())
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)).into(iv_item_image);
            } else {

            }
        }
        container.addView(itemView, 0);
        return itemView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public int getCount() {
        return banners == null ? 0 : banners.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
