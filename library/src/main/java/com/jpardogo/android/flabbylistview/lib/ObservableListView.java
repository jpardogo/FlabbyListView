package com.jpardogo.android.flabbylistview.lib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

public class ObservableListView extends ListView {

    private static final String TAG = ObservableListView.class.getSimpleName();
    /**
     * Delegate for the callback to the fragment/activity that the ListView is in
     */
    private ListViewObserverDelegate mObserver;

    private View mTrackedChild;
    private int mTrackedChildPrevPosition;
    private int mTrackedChildPrevTop;

    public ObservableListView(Context context) {
        super(context);
        init(context);
    }

    public ObservableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ObservableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        setOnScrollListener(mScrollListener);
    }

    public static interface ListViewObserverDelegate {
        public void onListScroll(View view, float deltaY);
    }


    private View getChildInTheMiddle() {
        return getChildAt(getChildCount() / 2);
    }

    public void setObserver(ListViewObserverDelegate observer) {
        mObserver = observer;
    }

    /**
     * Calculate the scroll distance comparing the distance with the top of the list of the current
     * child and the last one tracked
     *
     * @param l - Current horizontal scroll origin.
     * @param t - Current vertical scroll origin.
     * @param oldl - Previous horizontal scroll origin.
     * @param oldt - Previous vertical scroll origin.
     */
    private float OldDeltaY;

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (mTrackedChild == null) {

            //We want to continue scrolling the list when we don't find a valid child
            // so we use the last value of deltaY
            if (mObserver != null) mObserver.onListScroll(this, OldDeltaY);

            if (getChildCount() > 0) {
                mTrackedChild = getChildInTheMiddle();
                mTrackedChildPrevTop = mTrackedChild.getTop();
                mTrackedChildPrevPosition = getPositionForView(mTrackedChild);
            }
        } else {

            boolean childIsSafeToTrack = mTrackedChild.getParent() == this && getPositionForView(mTrackedChild) == mTrackedChildPrevPosition;
            if (childIsSafeToTrack) {
                int top = mTrackedChild.getTop();
                float deltaY = top - mTrackedChildPrevTop;

                if (deltaY == 0) {
                    //When we scroll so fast the list this value becomes 0 all the time
                    // so we don't want the other list stop, and we give it the last
                    //no 0 value we have
                    deltaY = OldDeltaY;
                } else {
                    OldDeltaY = deltaY;
                }

                updateChildrenControlPoints(deltaY);

                if (mObserver != null) mObserver.onListScroll(this, deltaY);

                mTrackedChildPrevTop = top;
            } else {
                mTrackedChild = null;
            }
        }

    }

    private OnScrollListener mScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView absListView, int scrollState) {
            if (scrollState == SCROLL_STATE_IDLE) {
                    updateChildrenControlPoints(0);
            }
        }

        @Override
        public void onScroll(AbsListView absListView, int i, int i2, int i3) {

        }
    };

    private void updateChildrenControlPoints(float deltaY) {
        View child;
        FlabbyLayout flabbyChild;
        for (int i = 0; i <= getLastVisiblePosition() - getFirstVisiblePosition(); i++) {
            child = getChildAt(i);
            if(child instanceof FlabbyLayout) {
                flabbyChild = (FlabbyLayout) child;
                if (child != null) {
                    flabbyChild.updateControlPoints(deltaY);
                }
            }
        }
    }
}