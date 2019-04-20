package com.luminous.mpartner.dynamic_home.providers;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.luminous.cardslibrary.card.Card;
import com.luminous.mpartner.R;
import com.luminous.mpartner.connect.ConnectRecyclerViewAdapter;
import com.luminous.mpartner.dynamic_home.entities.HomeCardEntity;

public class ScrollImageCardProvider extends LuminousCardProvider {

    @Override
    protected void onCreated() {
        super.onCreated();
        setLayout(R.layout.scroll_image_card);
    }


    @Override
    public void render(@NonNull final View view, @NonNull final Card card) {
        super.render(view, card);
        final HomeCardEntity homeCardEntity = (HomeCardEntity) getData();

        final RecyclerView recyclerView = (RecyclerView) findViewById(view, R.id.imageRecyclerView, RecyclerView.class);
        final ImageView ivNext = (ImageView) findViewById(view, R.id.ivNext, ImageView.class);


        if (homeCardEntity.getCardData() != null && homeCardEntity.getCardData().size() >0) {
            ivNext.setOnClickListener(v -> recyclerView.scrollBy(recyclerView.computeHorizontalScrollExtent(), 0));


            ConnectRecyclerViewAdapter adapter = new ConnectRecyclerViewAdapter(getContext(), homeCardEntity.getCardData());
            recyclerView.setAdapter(adapter);
        }
    }
}

