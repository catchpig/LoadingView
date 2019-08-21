package com.catchpig.loading.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.catchpig.loading.R;

/**
 * @author wangpeifeng
 * @date 2018/05/11 11:17
 */
public class LoadingView extends View {

    private static final int LINE_COUNT = 8;
    private static final int DEGREE_PER_LINE = 360 / LINE_COUNT;

    private int mLoadColor;
    private int mLoadSize;
    private int mLoadDuration = 800;
    private int mAnimateValue = 0;

    private Paint mPaint;
    private ValueAnimator mValueAnimator;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.loading_view_style);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.LoadingView, defStyleAttr, 0);
        mLoadColor = typedArray.getColor(R.styleable.LoadingView_loading_view_color, Color.WHITE);
        mLoadSize = typedArray.getDimensionPixelSize(R.styleable.LoadingView_loading_view_size,
                dpToPxInt(32));
        mLoadDuration = typedArray.getInteger(R.styleable.LoadingView_loading_view_duration, mLoadDuration);
        typedArray.recycle();
        initPaint();
    }

    public void setLoadColor(@ColorRes int loadColor) {
        mLoadColor = ContextCompat.getColor(getContext(),loadColor);
        mPaint.setColor(mLoadColor);
        invalidate();
    }

    private int dpToPxInt(float dp){
        return (int) (dp * getContext().getResources().getDisplayMetrics().density+0.5f);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        setMeasuredDimension(mLoadSize, mLoadSize);
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 保存图层
        int saveCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        drawLoading(canvas, mAnimateValue * DEGREE_PER_LINE);
        canvas.restoreToCount(saveCount);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnim();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnim();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == View.VISIBLE) {
            startAnim();
        } else {
            stopAnim();
        }
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(mLoadColor);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void startAnim() {
        if (mValueAnimator == null) {
            mValueAnimator = ValueAnimator.ofInt(0, LINE_COUNT - 1);
            mValueAnimator.setDuration(mLoadDuration);
            mValueAnimator.setRepeatMode(ValueAnimator.RESTART);
            mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mValueAnimator.setInterpolator(new LinearInterpolator());
            mValueAnimator.addUpdateListener(updateListener);
        }
        if (!mValueAnimator.isStarted()) {
            mValueAnimator.start();
        }
    }

    private void stopAnim() {
        if (mValueAnimator != null && mValueAnimator.isStarted()) {
            mValueAnimator.removeUpdateListener(updateListener);
            mValueAnimator.removeAllUpdateListeners();
            mValueAnimator.cancel();
            mValueAnimator = null;
        }
    }

    private void drawLoading(Canvas canvas, int rotateDegrees) {
        int width = mLoadSize / 4;
        int height = mLoadSize / 4;
        int centerSize = getWidth() / 2;
        mPaint.setStrokeWidth(width / 2);
        canvas.rotate(rotateDegrees, centerSize, centerSize);
        canvas.translate(centerSize, centerSize);
        for (int i = 0; i < LINE_COUNT; i++) {
            canvas.rotate(DEGREE_PER_LINE);
            double radius = (7 + i) * height / 28.0;
            canvas.translate(0, -mLoadSize / 2 + width / 2);
            canvas.drawCircle(0, 0, (float) radius, mPaint);
            canvas.translate(0, mLoadSize / 2 - width / 2);
        }
    }

    private ValueAnimator.AnimatorUpdateListener updateListener = animation -> {
        mAnimateValue = (int) animation.getAnimatedValue();
        invalidate();
    };
}
