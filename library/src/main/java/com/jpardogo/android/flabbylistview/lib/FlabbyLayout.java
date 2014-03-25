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
    private boolean isUserTouching = false;
    private float mFingerX = 0;
    private boolean isSelectedView = false;

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
        mWidth = getWidth();
        mHeight = getHeight();
        mOneFifthWidth = mWidth / 5;
        mFourFifthWith = mOneFifthWidth * 4;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        mRect = canvas.getClipBounds();
        mRect.inset(0, -mHeight / 2);
        canvas.clipRect(mRect, Region.Op.REPLACE);

        if (!isUserTouching) {
            if (mDeltaY > -MAX_CURVATURE && mDeltaY < MAX_CURVATURE) mCurvature = mDeltaY * 2;
            topCellPath(mOneFifthWidth, mFourFifthWith, mCurvature);
            bottomCellPath(mFourFifthWith, mOneFifthWidth, mHeight + mCurvature);
        } else {
            float curvature = isSelectedView?-mCurvature:mCurvature;
            topCellPath(mFingerX,mFingerX,curvature);
            curvature = isSelectedView?mHeight-curvature:mHeight;
            bottomCellPath(mFingerX,mFingerX,curvature);
        }

        canvas.drawPath(mPath, mPaint);
    }

    private Path topCellPath(float x1, float x2, float curvature) {
        mPath.reset();
        mPath.moveTo(0, 0);
        mPath.cubicTo(x1, curvature, x2, curvature, mWidth, 0);
        mPath.lineTo(mWidth, mHeight);
        return mPath;
    }

    private Path bottomCellPath(float x1, float x2, float curvature) {
        mPath.cubicTo(x1,  curvature, x2, curvature, 0, mHeight);
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

    public void setAsSelected(boolean isSelected) {
        isSelectedView = isSelected;
    }
}