package com.jpardogo.android.flabbylistview.lib;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

public class FlabbyListView extends ListView {

    private static final String TAG = FlabbyListView.class.getSimpleName();
    private View mTrackedChild;
    private View mDownView;
    private Rect mRect = new Rect();
    private int[] mListViewCoords;
    private int mChildCount;
    private int mTrackedChildPrevPosition;
    private int mTrackedChildPrevTop;
    private float OldDeltaY;
    private float mDownXValue;
    private float mDownYValue;

    public FlabbyListView(Context context) {
        super(context);
        init(context);
    }

    public FlabbyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FlabbyListView(Context context, AttributeSet attrs, int defStyle) {
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

    /**
     * Calculate the scroll distance comparing the distance with the top of the list of the current
     * child and the last one tracked
     *
     * @param l - Current horizontal scroll origin.
     * @param t - Current vertical scroll origin.
     * @param oldl - Previous horizontal scroll origin.
     * @param oldt - Previous vertical scroll origin.
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (mTrackedChild == null) {

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
                mTrackedChildPrevTop = top;
            } else {
                mTrackedChild = null;
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "ACTION_DOWN");
                mDownXValue = ev.getX();
                mDownYValue = ev.getY();
                mDownView = findChildTouched(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                actionMove(ev);

                break;
            case MotionEvent.ACTION_UP:
                sendDownViewEvent(ev);
                Log.d(TAG, "ACTION_UP");
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void actionMove(MotionEvent event) {
        float currentX = event.getX();
        float currentY = event.getY();
        float OffsetX =mDownXValue - currentX;
        float OffsetY =mDownYValue-currentY;
        Log.d(TAG, "Offset Y: "+OffsetY);
        if (Math.abs(OffsetX) > Math.abs(OffsetY)) {
            sendDownViewEvent(event);
        }else if(Math.abs(OffsetY)>100){
            Log.d(TAG, "Offset Y, SUCCESS!!: "+OffsetY);
            sendDownViewEvent(MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
        }
    }

    private void sendDownViewEvent(MotionEvent ev) {
        if(mDownView!=null) {
            mDownView.onTouchEvent(ev);
        }
    }

    private View findChildTouched(MotionEvent event) {
        View downView = null;
        mChildCount = getChildCount();
        mListViewCoords = new int[2];
        getLocationOnScreen(mListViewCoords);
        int x = (int) event.getRawX() - mListViewCoords[0];
        int y = (int) event.getRawY() - mListViewCoords[1];
        View child;
        for (int i = 0; i < mChildCount; i++) {
            child = getChildAt(i);
            child.getHitRect(mRect);
            if (mRect.contains(x, y)) {
                downView=child;
                return downView;
            }
        }
        return downView;
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
            if (child instanceof FlabbyLayout) {
                flabbyChild = (FlabbyLayout) child;
                if (child != null) {
                    flabbyChild.updateControlPoints(deltaY);
                }
            }
        }
    }
}