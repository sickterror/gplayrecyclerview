package com.timelesssoftware.gplayrecyclerview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Luka on 24.1.2018.
 */
public class GooglePlayRecyclerView extends RelativeLayout {

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

    private  float imageScale;
    private int endOffsetMargin;
    private View rootView;
    private RecyclerView gRecyclerView;
    private RecyclerView.Adapter adapter;
    private GooglePlayRecyclerViewScrollListener googlePlayRecyclerViewScrollListener;
    private LinearLayoutManager layout;
    private Drawable gDrawable;

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
        gRecyclerView.setPadding(startOffset, 0, 0, 0);
        gBackground = rootView.findViewById(R.id.gp_background);
        gBackground.setImageDrawable(gDrawable);
        gBackground.setScaleX(imageScale);
        gBackground.setScaleY(imageScale);
        layout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        gRecyclerView.setLayoutManager(layout);
        gRecyclerView.addOnScrollListener(onScrollListener);
        setMarginsToChild(gBackground, startBackgroundOffset);
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
    interface GooglePlayRecyclerViewScrollListener {

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
        Resources r = getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    /**
     * Sets left margin to child
     *
     * @param v
     * @param margin
     */
    private void setMarginsToChild(View v, int margin) {
        LayoutParams params = (LayoutParams) gBackground.getLayoutParams();
        Log.d("margin", params.leftMargin + " / " + endOffsetMargin);
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

            int marginToAnimate = recyclerView.computeHorizontalScrollOffset() / bacKgroundThreshold;
            LayoutParams params = (LayoutParams) gBackground.getLayoutParams();
            params.leftMargin = startBackgroundOffset - marginToAnimate;
            //No need to change values
            if (params.leftMargin > endOffsetMargin) {
                gBackground.setLayoutParams(params);
                int alpha = calculateAlpha(startOffset - recyclerView.computeHorizontalScrollOffset());
                gBackground.setAlpha(alpha);
            }
        }
    };

    private int calculateAlpha(float currentMargin) {
        int endOffset = (endOffsetMargin < 0 ? -endOffsetMargin : endOffsetMargin);
        float calc = ((currentMargin + endOffset) / (startOffset + endOffset));
        if (calc <= endBackgroundAlpha)
            return (int) (255 * endBackgroundAlpha);
        return (int) (255 * calc);
    }
}
