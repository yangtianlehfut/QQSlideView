package com.example.yang.qqslideview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.Scroller;
import android.widget.TextView;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;


public class SlideView extends FrameLayout implements View.OnClickListener {
    TextView leftView, centerView, rightView;
    Context context;
    Scroller scroller;
    int leftBorder, rightBorder;
    OnClickListener listener;
    int msgCount = 1;

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        float centerX = getWidthPixels() * 0.95f;
        float centerY = getMeasuredHeight() / 2;
        Log.i("ytl", "X: " + centerX + " Y: " + centerY);
        canvas.drawCircle(centerX, centerY, 20, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(30);
        String msg = msgCount + "";
        float msgLen = paint.measureText(msg);
        Paint.FontMetrics metrics = paint.getFontMetrics();
        canvas.drawText("1", centerX - msgLen/2, centerY - (metrics.descent + metrics.ascent) / 2, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
        setMeasuredDimension(leftView.getMeasuredWidth() + getWidthPixels() + rightView.getMeasuredWidth(), MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (leftView != null) {
            leftView.layout(-leftView.getMeasuredWidth(), 0, 0, leftView.getMeasuredHeight());
        }

        centerView.layout(0, 0, getWidthPixels(), getMeasuredHeight());

        if (rightView != null) {
            rightView.layout(getWidthPixels(), 0, getWidthPixels() + rightView.getMeasuredWidth(), rightView.getMeasuredHeight());
        }

        leftBorder = leftView.getLeft();
        rightBorder = rightView.getRight();
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    public SlideView(Context context) {
        super(context);

    }

    public SlideView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        scroller = new Scroller(context, new DecelerateInterpolator());

        setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        leftView = new TextView(context);
        leftView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        leftView.setBackgroundColor(Color.RED);
        leftView.setText("LEFT");

        centerView = new TextView(context);
        centerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        centerView.setBackgroundColor(Color.GREEN);
        centerView.setWidth(getWidthPixels());
        centerView.setText("CENTER");


        rightView = new TextView(context);
        rightView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        rightView.setBackgroundColor(Color.YELLOW);
        rightView.setText("RIGHT");

        addView(leftView);
        addView(centerView);
        addView(rightView);

        if (leftView != null) {
            leftView.setOnClickListener(this);
        }
        centerView.setOnClickListener(this);
        if (rightView != null) {
            rightView.setOnClickListener(this);
        }

    }

    public SlideView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public SlideView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
    }

    public int getWidthPixels() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    @Override
    public void computeScroll() {
        Log.i("ytl", scroller.computeScrollOffset() + "");
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
    }

    float lastX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case ACTION_DOWN:
                Log.i("ytl", "down");
                lastX = event.getRawX();
                break;
            case ACTION_MOVE:
                Log.i("ytl", "getScrollX" + getScrollX());
                float distance = event.getRawX() - lastX;
                lastX = event.getRawX();
                if (getScrollX() - distance < leftBorder) {
                    scrollTo(leftBorder, 0);
                    return true;
                } else if (getScrollX() - distance > rightView.getMeasuredWidth()) {
                    scrollTo(rightView.getMeasuredWidth(), 0);
                    return true;
                } else {
                    scrollBy((int) -distance, 0);
                }

                break;
            case ACTION_UP:
                Log.i("ytl", "up");
                if (Math.abs(getScrollX()) < leftView.getMeasuredWidth() / 2) {
                    scroller.startScroll(getScrollX(), 0, -getScrollX(), 0);
                    invalidate();
                } else {
                    if (getScrollX() < 0) {
                        scroller.startScroll(getScrollX(), 0, -leftView.getMeasuredWidth() - getScrollX(), 0);
                    } else {
                        scroller.startScroll(getScrollX(), 0, rightView.getMeasuredWidth() - getScrollX(), 0);
                    }
                    invalidate();
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case ACTION_DOWN:
                lastX = ev.getRawX();
                break;
            case ACTION_MOVE:
                float curX = ev.getRawX();
                if (Math.abs(curX - lastX) > 5) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {
        if (listener == null) {
            return;
        }

        if (v == leftView) {
            listener.onLeftClick(v);
        } else if (v == centerView) {
            listener.onCenterClick(v);
        } else {
            listener.onRightClick(v);
        }
    }

    interface OnClickListener {
        void onLeftClick(View v);

        void onCenterClick(View v);

        void onRightClick(View v);
    }
}
