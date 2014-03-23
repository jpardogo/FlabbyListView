package com.jpardogo.android.flabbylistview.lib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.TextView;

public class FlabbyLayout extends FrameLayout {
    private static final String TAG = FlabbyLayout.class.getSimpleName();
    private static final float MAX_CURVATURE = 100;
    private Path mPath;
    private Paint mPaint;
    private Rect mRect;
    private float mDeltaY = 0;
    private float mCurvature;
    private int mWidth;
    private int mHeight;
    private int mOneFifthWidth;
    private int mFourFifthWith;
    private TextView mTextView;
    private boolean isUserTouching = false;
    private float mFingerX = 0;


    public FlabbyLayout(Context context) {
        super(context);
        init(context);
    }

    public FlabbyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FlabbyLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);
        mPath = new Path();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTextView == null) {
            mTextView = (TextView) findViewWithTag("Text");
        }
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        if (mWidth == 0) mWidth = getWidth();
        if (mHeight == 0) mHeight = getHeight();
        mRect = canvas.getClipBounds();
        mRect.inset(0, -mHeight / 2);
        canvas.clipRect(mRect, Region.Op.REPLACE);

        float startX1;
        float startX2;
        float finishX1;
        float finishX2;
        float curvature;
        if (!isUserTouching) {
            if (mDeltaY > -MAX_CURVATURE && mDeltaY < MAX_CURVATURE) mCurvature = mDeltaY * 2 * -1;
            if (mOneFifthWidth == 0) mOneFifthWidth = mWidth / 5;
            if (mFourFifthWith == 0) mFourFifthWith = mOneFifthWidth * 4;
            startX1=mOneFifthWidth;
            startX2=mFourFifthWith;
            finishX1=mFourFifthWith;
            finishX2=mOneFifthWidth;
            curvature=mCurvature;
        } else {
            startX1=mFingerX;
            startX2=mFingerX;
            finishX1=mFingerX;
            finishX2=mFingerX;
            curvature=-mCurvature;
        }

        startFlabberPath(startX1, startX2);
        finishFlabberPath(finishX1, finishX2, curvature);
        canvas.drawPath(mPath, mPaint);
    }

    private Path startFlabberPath(float x1, float x2) {
        mPath.reset();
        mPath.moveTo(0, 0);
        mPath.cubicTo(x1, -mCurvature, x2, -mCurvature, mWidth, 0);
        mPath.lineTo(mWidth, mHeight);
        return mPath;
    }

    private Path finishFlabberPath(float x1, float x2, float curvature) {
        mPath.cubicTo(x1, mHeight - curvature, x2, mHeight - curvature, 0, mHeight);
        mPath.lineTo(0, 0);
        return null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                actionDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
               actionMove(event);
                break;
            case MotionEvent.ACTION_UP:
                actionUp();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void actionDown(MotionEvent event) {
        mCurvature = MAX_CURVATURE;
        mFingerX = event.getX();
        isUserTouching = true;
    }

    private void actionMove(MotionEvent event) {
        if (mFingerX != event.getX()) {
            requestLayout();
        }
        mFingerX = event.getX();
    }

    private void actionUp() {
        isUserTouching = false;
        mCurvature = 0;
        invalidate();
    }

    public void updateControlPoints(float deltaY) {
        mDeltaY = deltaY;
        invalidate();
    }

    public void setFlabbyColor(int color) {
        mPaint.setColor(color);
    }
}