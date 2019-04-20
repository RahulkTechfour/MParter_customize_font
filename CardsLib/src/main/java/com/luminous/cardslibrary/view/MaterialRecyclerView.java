package com.luminous.cardslibrary.view;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;

import com.luminous.cardslibrary.R;
import com.luminous.cardslibrary.listeners.RecyclerItemClickListener;


public class MaterialRecyclerView extends RecyclerView {

    private static final int DEFAULT_COLUMNS_PORTRAIT = 1;
    private static final int DEFAULT_COLUMNS_LANDSCAPE = 2;

    private View mEmptyView;
    private int mColumnCount;
    private int mColumnCountLandscape = DEFAULT_COLUMNS_LANDSCAPE;
    private int mColumnCountPortrait = DEFAULT_COLUMNS_PORTRAIT;
    private final AdapterDataObserver mEmptyViewObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            checkIfEmpty();
        }
    };

    public MaterialRecyclerView(Context context) {
        this(context, null);
    }

    public MaterialRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);


        setAdapter(new MaterialRecycleAdapter(new OnSwipeAnimation() {
            @Override
            public void animate(final int position) {
                RecyclerView.ViewHolder holder = findViewHolderForPosition(position);
            }
        }, new OnAdapterItemsChanged() {
            @Override
            public void onAddItem(int position, boolean scroll) {
                if (scroll) {
                    scrollToPosition(position);
                }
                checkIfEmpty();
            }

            @Override
            public void onRemoveItem() {
                checkIfEmpty();
            }
        }));

        if (attrs != null) {
            // get the number of columns
            TypedArray typedArray = context.obtainStyledAttributes(attrs,
                    R.styleable.MaterialRecyclerView, defStyle, 0);

            mColumnCount = typedArray.getInteger(R.styleable.MaterialRecyclerView_column_count, 0);
            if (mColumnCount > 0) {
                mColumnCountPortrait = mColumnCount;
                mColumnCountLandscape = mColumnCount;
            } else {
                mColumnCountPortrait = typedArray.getInteger(
                        R.styleable.MaterialRecyclerView_column_count_portrait,
                        DEFAULT_COLUMNS_PORTRAIT);
                mColumnCountLandscape = typedArray.getInteger(
                        R.styleable.MaterialRecyclerView_column_count_landscape,
                        DEFAULT_COLUMNS_LANDSCAPE);
            }

            boolean isLandscape = isLandscape();
            mColumnCount = isLandscape ? mColumnCountLandscape : mColumnCountPortrait;
            setColumnLayout(mColumnCount);

            typedArray.recycle();
        }
    }

    public <T extends MaterialRecycleAdapter> void setAdapter(@NonNull final T adapter) {
        final RecyclerView.Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(mEmptyViewObserver);
        }
        super.setAdapter(adapter);
        adapter.registerAdapterDataObserver(mEmptyViewObserver);
    }

    @Override
    public MaterialRecycleAdapter getAdapter() {
        return (MaterialRecycleAdapter) super.getAdapter();
    }

    public int getColumnCount() {
        return mColumnCount;
    }

    public void setColumnCount(int columnCount) {
        mColumnCount = columnCount;
    }

    public int getColumnCountLandscape() {
        return mColumnCountLandscape;
    }

    public void setColumnCountLandscape(int columnCountLandscape) {
        mColumnCountLandscape = columnCountLandscape;
    }

    public int getColumnCountPortrait() {
        return mColumnCountPortrait;
    }

    public void setColumnCountPortrait(int columnCountPortrait) {
        mColumnCountPortrait = columnCountPortrait;
    }

    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
        checkIfEmpty();
    }

    public void addOnItemTouchListener(RecyclerItemClickListener.OnItemClickListener listener) {
        RecyclerItemClickListener itemClickListener =
                new RecyclerItemClickListener(getContext(), listener);
        itemClickListener.setRecyclerView(this);
        addOnItemTouchListener(itemClickListener);
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        boolean isLandscape = isLandscape();
        int newColumnCount = isLandscape ? mColumnCountLandscape : mColumnCountPortrait;
        if (mColumnCount != newColumnCount) {
            mColumnCount = newColumnCount;
            setColumnLayout(mColumnCount);
        }
    }

    private void setColumnLayout(int columnCount) {
        if (columnCount > 1) {
            setLayoutManager(new StaggeredGridLayoutManager(columnCount,
                    StaggeredGridLayoutManager.VERTICAL));
        } else {
            setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.VERTICAL, false));
        }
    }

    private boolean isLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    private void checkIfEmpty() {
        if (mEmptyView != null) {
            mEmptyView.setVisibility(getAdapter().isEmpty() ? VISIBLE : GONE);
            setVisibility(getAdapter().isEmpty() ? GONE : VISIBLE);
        }
    }

    /**
     *
     */
    interface OnSwipeAnimation {
        /**
         * @param position
         */
        void animate(final int position);
    }

    /**
     *
     */
    interface OnAdapterItemsChanged {
        /**
         * @param position
         * @param scroll
         */
        void onAddItem(final int position, boolean scroll);

        /**
         *
         */
        void onRemoveItem();
    }
}
