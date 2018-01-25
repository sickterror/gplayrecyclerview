package com.timelesssoftware.gplayrecyclerview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Luka on 24.1.2018.
 */
public class GooglePlayRecyclerView extends RelativeLayout {

    private static final String TAG = "gPlayRv";
    private int backgroundColor;

    private float imageScale;
    /**
     * The end of image movement.
     * When this threshold is reached the @gBackground stops moving
     */
    public int bacKgroundThreshold;
    /**
     * The End background alpha.
     * This is the alpha of the image on the end of its path
     */
    public float endBackgroundAlpha;
    /**
     * The Start offset.
     * The starting offset of the recylcer view
     */
    public int startOffset;
    /**
     * The Start background offset.
     *
     * @gBackround stargin offset. The margin from which the image stars moving
     */
    public int startBackgroundOffset;
    /**
     * The image view that holds the @gBackground image.
     */
    public ImageView gBackground;

    private RelativeLayout mainLayout;
    private int endOffsetMargin;
    private View rootView;
    private RecyclerView gRecyclerView;
    private RecyclerView.Adapter adapter;
    private GooglePlayRecyclerViewScrollListener googlePlayRecyclerViewScrollListener;
    private LinearLayoutManager layout;
    private Drawable gDrawable;
    private int scrollOfset = 0;

    /**
     * Instantiates a new Google play recycler view.
     *
     * @param context the context
     */
    public GooglePlayRecyclerView(Context context) {
        super(context);
        init(context);
    }

    /**
     * Instantiates a new Google play recycler view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public GooglePlayRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.GooglePlayRecyclerView,
                0, 0);
        try {
            int startingOffsetAttr = typedArray.getDimensionPixelSize(R.styleable.GooglePlayRecyclerView_start_offset, 0);
            int endOffsetAttr = typedArray.getDimensionPixelSize(R.styleable.GooglePlayRecyclerView_end_background_offset, 0);
            int startBackgroundAttr = typedArray.getDimensionPixelSize(R.styleable.GooglePlayRecyclerView_start_background_offset, 0);
            imageScale = (typedArray.getFloat(R.styleable.GooglePlayRecyclerView_image_scale, 1) > 1) ? 1 : typedArray.getFloat(R.styleable.GooglePlayRecyclerView_image_scale, 1);
            gDrawable = typedArray.getDrawable(R.styleable.GooglePlayRecyclerView_image);
            endBackgroundAlpha = typedArray.getFloat(R.styleable.GooglePlayRecyclerView_end_background_alpha, 0);
            bacKgroundThreshold = typedArray.getInt(R.styleable.GooglePlayRecyclerView_background_step, 2);
            backgroundColor = typedArray.getColor(R.styleable.GooglePlayRecyclerView_background_color, 2);
            //Convert dp to px
            endOffsetMargin = dpToPixels(endOffsetAttr);
            startOffset = dpToPixels(startingOffsetAttr);
            startBackgroundOffset = dpToPixels(startBackgroundAttr);
        } catch (Exception e) {
            typedArray.recycle();
        }
        init(getContext());
        typedArray.recycle();
    }

    private void init(Context context) {
        rootView = inflate(context, R.layout.google_play_rv, this);
        gRecyclerView = rootView.findViewById(R.id.gp_rv);
        //gRecyclerView.setPadding(startOffset, 0, 0, 0);
        mainLayout = findViewById(R.id.constraint_layout);
        gBackground = rootView.findViewById(R.id.gp_background);
        gBackground.setImageDrawable(gDrawable);
        gBackground.setScaleX(imageScale);
        gBackground.setScaleY(imageScale);
        layout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        gRecyclerView.setLayoutManager(layout);
        gRecyclerView.addOnScrollListener(onScrollListener);
        setMarginsToChild(gBackground, startBackgroundOffset);
        mainLayout.setBackgroundColor(backgroundColor);
        gRecyclerView.addItemDecoration(new PaddingItemDecoration(startOffset));
        new GLinearSnapHelper().attachToRecyclerView(getgRecyclerView());
    }


    /**
     * Sets adatper.
     *
     * @param adatper the adatper
     */
    public void setAdatper(RecyclerView.Adapter adatper) {
        this.adapter = adatper;
        gRecyclerView.setAdapter(adatper);
    }

    /**
     * Gets adapter.
     *
     * @return the adapter
     */
    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    public LinearLayoutManager getLayout() {
        return layout;
    }

    public RecyclerView getgRecyclerView() {
        return gRecyclerView;
    }

    /**
     * Sets google play recycler view scroll listener.
     *
     * @param googlePlayRecyclerViewScrollListener the google play recycler view scroll listener
     */
    public void setGooglePlayRecyclerViewScrollListener(GooglePlayRecyclerViewScrollListener googlePlayRecyclerViewScrollListener) {
        this.googlePlayRecyclerViewScrollListener = googlePlayRecyclerViewScrollListener;
    }

    /**
     * The interface Google play recycler view scroll listener.
     */
    public interface GooglePlayRecyclerViewScrollListener {

        /**
         * On scroll state changed.
         *
         * @param recyclerView the recycler view
         * @param newState     the new state
         */
        void onScrollStateChanged(RecyclerView recyclerView, int newState);

        /**
         * On scrolled.
         *
         * @param recyclerView the recycler view
         * @param dx           the dx
         * @param dy           the dy
         */
        void onScrolled(RecyclerView recyclerView, int dx, int dy);

    }

    /**
     * Converts dp into pixels
     *
     * @param dp
     * @return
     */
    private int dpToPixels(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    /**
     * Sets left margin to child
     *
     * @param v
     * @param margin
     */
    private void setMarginsToChild(View v, int margin) {
        LayoutParams params = (LayoutParams) gBackground.getLayoutParams();
        params.leftMargin = margin;
        v.setLayoutParams(params);
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (googlePlayRecyclerViewScrollListener != null)
                googlePlayRecyclerViewScrollListener.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (googlePlayRecyclerViewScrollListener != null)
                googlePlayRecyclerViewScrollListener.onScrolled(recyclerView, dx, dy);

            int marginToAnimate = scrollOfset / bacKgroundThreshold;
            LayoutParams params = (LayoutParams) gBackground.getLayoutParams();
            params.leftMargin = startBackgroundOffset - marginToAnimate;
            //No need to change values
            if (params.leftMargin >= endOffsetMargin) {
                gBackground.setLayoutParams(params);
                int alpha = calculateAlpha(startOffset - recyclerView.computeHorizontalScrollOffset());
                gBackground.setAlpha(alpha);
            }
            //Ge the scroll offset of the first itemViewHolder
            scrollOfset += dx;
        }
    };

    private int calculateAlpha(float currentMargin) {
        int endOffset = (endOffsetMargin < 0 ? -endOffsetMargin : endOffsetMargin);
        float calc = ((currentMargin + endOffset) / (startOffset + endOffset));
        if (calc <= endBackgroundAlpha)
            return (int) (255 * endBackgroundAlpha);
        return (int) (255 * calc);
    }


    private class PaddingItemDecoration extends RecyclerView.ItemDecoration {
        private final int size;

        public PaddingItemDecoration(int size) {
            this.size = size;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.left += size;
            }
        }
    }
}
