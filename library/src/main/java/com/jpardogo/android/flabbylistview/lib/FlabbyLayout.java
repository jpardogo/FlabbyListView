package com.jpardogo.android.flabbylistview.lib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

public class FlabbyLayout extends FrameLayout {
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
        mTextView = new TextView(context);
        mTextView.setGravity(Gravity.CENTER);
        addView(mTextView);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        if(mWidth==0) mWidth = getWidth();
        if(mHeight==0) mHeight = getHeight();
        if(mOneFifthWidth ==0) mOneFifthWidth = mWidth / 5;
        if(mFourFifthWith ==0) mFourFifthWith = mOneFifthWidth * 4;

        mRect = canvas.getClipBounds();
        mRect.inset(0, -mHeight / 2);
        canvas.clipRect(mRect, Region.Op.REPLACE);

        if (mDeltaY > -MAX_CURVATURE && mDeltaY < MAX_CURVATURE) mCurvature = mDeltaY * 2 * -1;
        canvas.drawPath(makeFlabberPath(), mPaint);
    }

    private Path makeFlabberPath() {
        mPath.reset();
        mPath.moveTo(0, 0);
        mPath.cubicTo(mOneFifthWidth, -mCurvature, mFourFifthWith, -mCurvature, mWidth, 0);
        mPath.lineTo(mWidth, mHeight);
        mPath.cubicTo(mFourFifthWith, mHeight - mCurvature, mOneFifthWidth, mHeight - mCurvature, 0, mHeight);
        mPath.lineTo(0, 0);
        return mPath;
    }

    public void updateControlPoints(float deltaY) {
        mTextView.setText("DeltaY: " + deltaY);
        mDeltaY = deltaY;
        invalidate();
    }

    public void setFlabbyColor(int color) {
        mPaint.setColor(color);
    }
}