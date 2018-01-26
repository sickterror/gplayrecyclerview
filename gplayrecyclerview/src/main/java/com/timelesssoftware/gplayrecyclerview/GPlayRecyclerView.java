package com.timelesssoftware.gplayrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Luka on 24.1.2018.
 */
public class GPlayRecyclerView extends RelativeLayout {

    private static final String TAG = "gPlayRv";
    private int backgroundColor;

    private float imageScale;
    /**
     * The end of image movement.
     * When this threshold is reached the @gBackground stops moving
     */
    public int backgroundThreshold;
    /**
     * The End background backgroundImageAlpha.
     * This is the backgroundImageAlpha of the image on the end of its path
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
     * @gBackround stargin offset. The scrollState from which the image stars moving
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
    private GPlayScrollListener gPlayScrollListener;
    private LinearLayoutManager layout;
    private Drawable gDrawable;
    private int scrollOfset = 0;
    private int backgroundImageAlpha;
    private boolean enableBacgroundAlpha = true;
    private boolean enableBackgroundMove = true;

    private boolean readFromState = false;

    /**
     * Instantiates a new Google play recycler view.
     *
     * @param context the context
     */
    public GPlayRecyclerView(Context context) {
        super(context);
        init(context);
    }

    /**
     * Instantiates a new Google play recycler view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public GPlayRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSaveEnabled(true);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.GPlayRecyclerView,
                0, 0);
        try {
            int startingOffsetAttr = typedArray.getDimensionPixelSize(R.styleable.GPlayRecyclerView_start_offset, 0);
            int endOffsetAttr = typedArray.getDimensionPixelSize(R.styleable.GPlayRecyclerView_end_background_offset, 0);
            int startBackgroundAttr = typedArray.getDimensionPixelSize(R.styleable.GPlayRecyclerView_start_background_offset, 0);
            imageScale = (typedArray.getFloat(R.styleable.GPlayRecyclerView_image_scale, 1) > 1) ? 1 : typedArray.getFloat(R.styleable.GPlayRecyclerView_image_scale, 1);
            gDrawable = typedArray.getDrawable(R.styleable.GPlayRecyclerView_image);
            endBackgroundAlpha = typedArray.getFloat(R.styleable.GPlayRecyclerView_end_background_alpha, 0);
            backgroundThreshold = typedArray.getInt(R.styleable.GPlayRecyclerView_background_step, 2);
            backgroundColor = typedArray.getColor(R.styleable.GPlayRecyclerView_background_color, 2);
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
    }

    private void drawImage() {
        gBackground = new ImageView(getContext());
        gBackground.setId(generateViewId());
        gBackground.setImageDrawable(gDrawable);
        gBackground.setScaleX(imageScale);
        gBackground.setScaleY(imageScale);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        mainLayout.addView(gBackground, lp);
    }

    private void drawRecylcerView() {

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
     * @param gPlayScrollListener the google play recycler view scroll listener
     */
    public void setgPlayScrollListener(GPlayScrollListener gPlayScrollListener) {
        this.gPlayScrollListener = gPlayScrollListener;
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
     * Sets left scrollState to child
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
            if (gPlayScrollListener != null)
                gPlayScrollListener.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (gPlayScrollListener != null)
                gPlayScrollListener.onScrolled(recyclerView, dx, dy);
            int marginToAnimate = scrollOfset / backgroundThreshold;
            //No need to change values
            int leftThreshold = Math.abs(endOffsetMargin - startOffset);
            if (scrollOfset < leftThreshold) {
                if (enableBackgroundMove) {
                    LayoutParams params = (LayoutParams) gBackground.getLayoutParams();
                    params.leftMargin = startBackgroundOffset - marginToAnimate;
                    gBackground.setLayoutParams(params);
                }
                if (enableBacgroundAlpha) {
                    backgroundImageAlpha = calculateAlpha(startOffset - recyclerView.computeHorizontalScrollOffset());
                    gBackground.setAlpha(backgroundImageAlpha);
                }
            }
            //Ge the scroll offset of the first itemViewHolder
            scrollOfset += dx;
        }
    };

    /**
     * Calculates the alpha from the distance of scroll
     *
     * @param currentMargin
     * @return
     */
    private int calculateAlpha(float currentMargin) {
        int endOffset = (endOffsetMargin < 0 ? -endOffsetMargin : endOffsetMargin);
        float calc = ((currentMargin + endOffset) / (startOffset + endOffset));
        if (calc <= endBackgroundAlpha)
            return (int) (255 * endBackgroundAlpha);
        return (int) (255 * calc);
    }


    /**
     * Padding decorator
     */
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

    /**
     * The interface Google play recycler view scroll listener.
     */
    public interface GPlayScrollListener {

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
     * Debug method
     */
    public void getState() {
        Log.d(TAG, "state -> " +
                "startOffset: " + startBackgroundOffset +
                " endOffset: " + endOffsetMargin +
                " startBackgroundOffset: " + startBackgroundOffset +
                " endBackgroundAlpha: " + endBackgroundAlpha +
                " currentBackgroundLeft " + gBackground.getLeft() +
                " scrollOffset " + scrollOfset
        );
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.childrenStates = new SparseArray();
        State state = new State();
        state.scrollState = scrollOfset;
        state.backgroundOffset = gBackground.getLeft();
        state.backgroundAlpha = backgroundImageAlpha;
        for (int i = 0; i < getChildCount(); i++) {
            ss.childrenStates.append(i, state);
            getChildAt(i).saveHierarchyState(ss.childrenStates);
        }
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        for (int i = 0; i < getChildCount(); i++) {
            State stateO = (State) ss.childrenStates.get(i);
            scrollOfset = stateO.scrollState;
            this.setgBackgroundPosition(stateO.backgroundOffset);
            gBackground.setAlpha(stateO.backgroundAlpha);
            getChildAt(i).restoreHierarchyState(ss.childrenStates);
        }
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        dispatchFreezeSelfOnly(container);
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    static class SavedState extends BaseSavedState {
        SparseArray childrenStates;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in, ClassLoader classLoader) {
            super(in);
            childrenStates = in.readSparseArray(classLoader);
        }

        public static final ClassLoaderCreator<SavedState> CREATOR
                = new ClassLoaderCreator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source, ClassLoader loader) {
                return new SavedState(source, loader);
            }

            @Override
            public SavedState createFromParcel(Parcel source) {
                return createFromParcel(source, null);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    private void setgBackgroundPosition(int left) {
        LayoutParams params = (LayoutParams) gBackground.getLayoutParams();
        params.leftMargin = left;
        gBackground.setLayoutParams(params);
    }

    /**
     * Enables or disables the alpha animation
     *
     * @param enableBacgroundAlpha
     */
    public void setEnableBacgroundAlpha(boolean enableBacgroundAlpha) {
        this.enableBacgroundAlpha = enableBacgroundAlpha;
    }

    /**
     * Enables or disables the background animation
     *
     * @param enableBackgroundMove
     */
    public void setEnableBackgroundMove(boolean enableBackgroundMove) {
        this.enableBackgroundMove = enableBackgroundMove;
    }

    /**
     * Sets the background color of the main view
     *
     * @param color
     */
    public void setViewBackgroundColor(int color) {
        this.mainLayout.setBackgroundColor(color);
    }

    /**
     * Sets a custom snap helper to recyclerView
     *
     * @param snapHelper
     */
    public void setSnapHelper(SnapHelper snapHelper) {
        snapHelper.attachToRecyclerView(gRecyclerView);
    }

    /**
     * Enables the default snap helper, that mimics the google play recylcer view
     */
    public void enableDefaultSnapHelper() {
        GLinearSnapHelper gLinearSnapHelper = new GLinearSnapHelper();
        gLinearSnapHelper.startingOffset = startOffset;
        gLinearSnapHelper.itemPadding = dpToPixels(16);
        setSnapHelper(gLinearSnapHelper);
            }

    private class State {
        public int scrollState;
        public int backgroundOffset;
        public int backgroundAlpha;
    }
}