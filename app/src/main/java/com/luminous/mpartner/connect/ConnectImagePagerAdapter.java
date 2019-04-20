package com.luminous.mpartner.connect;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.luminous.mpartner.R;
import com.luminous.mpartner.dynamic_home.entities.ImageEntity;

import java.util.List;


public class ConnectImagePagerAdapter extends PagerAdapter {

    private Context _context;
    private List<ImageEntity> banners;
    private LayoutInflater _layoutInflater;

    public ConnectImagePagerAdapter(Context context, List<ImageEntity> banners) {
        this._context = context;
        this.banners = banners;
        _layoutInflater = LayoutInflater.from(_context);

    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = _layoutInflater.inflate(R.layout.single_image_layout, container, false);
        if (itemView != null) {


//            final ImageEntity imageEntity = banners.get(position);

            ImageView iv_item_image = itemView.findViewById(R.id.iv_item_image);
            iv_item_image.setImageDrawable(_context.getResources().getDrawable(R.drawable.grid_default));


//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(_context, "Hiii", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//
//            if (imageEntity != null && !TextUtils.isEmpty(imageEntity.getBackgroundImage())) {
//                Glide.with(_context)
//                        .load(imageEntity.getBackgroundImage())
//                        .apply(new RequestOptions()
//                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
//                                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)).into(iv_item_image);
//            } else {
//                iv_item_image.setImageDrawable(_context.getResources().getDrawable(R.drawable.grid_default));
//            }
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
        return banners == null ? 4 : banners.size();
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
