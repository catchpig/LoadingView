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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.catchpig.loading.R;

/**
 * @author wangpeifeng
 * @date 2018/05/11 11:17
 */
public class LoadingView extends View {

    private static final int LINE_COUNT = 8;
    private static final int DEGREE_PER_LINE = 360 / LINE_COUNT;

    private int loadColor;
    private int loadSize;
    private int loadDuration = 800;
    private int animateValue = 0;

    private Paint paint;
    private ValueAnimator valueAnimator;

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
        loadColor = typedArray.getColor(R.styleable.LoadingView_loading_view_color, Color.WHITE);
        loadSize = typedArray.getDimensionPixelSize(R.styleable.LoadingView_loading_view_size,
                dpToPxInt(32));
        loadDuration = typedArray.getInteger(R.styleable.LoadingView_loading_view_duration,loadDuration);
        typedArray.recycle();
        initPaint();
    }

    private int dpToPxInt(float dp){
        return (int) (dp * getContext().getResources().getDisplayMetrics().density+0.5f);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        setMeasuredDimension(loadSize, loadSize);
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 保存图层
        int saveCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        drawLoading(canvas, animateValue * DEGREE_PER_LINE);
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
        paint = new Paint();
        paint.setColor(loadColor);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void startAnim() {
        if (valueAnimator == null) {
            valueAnimator = ValueAnimator.ofInt(0, LINE_COUNT - 1);
            valueAnimator.setDuration(loadDuration);
            valueAnimator.setRepeatMode(ValueAnimator.RESTART);
            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(updateListener);
        }
        if (!valueAnimator.isStarted()) {
            valueAnimator.start();
        }
    }

    private void stopAnim() {
        if (valueAnimator != null && valueAnimator.isStarted()) {
            valueAnimator.removeUpdateListener(updateListener);
            valueAnimator.removeAllUpdateListeners();
            valueAnimator.cancel();
            valueAnimator = null;
        }
    }

    private void drawLoading(Canvas canvas, int rotateDegrees) {
        int width = loadSize / 4;
        int height = loadSize / 4;
        int centerSize = getWidth() / 2;
        paint.setStrokeWidth(width / 2);
        canvas.rotate(rotateDegrees, centerSize, centerSize);
        canvas.translate(centerSize, centerSize);
        for (int i = 0; i < LINE_COUNT; i++) {
            canvas.rotate(DEGREE_PER_LINE);
            double radius = (7 + i) * height / 28.0;
            canvas.translate(0, -loadSize / 2 + width / 2);
            canvas.drawCircle(0, 0, (float) radius, paint);
            canvas.translate(0, loadSize / 2 - width / 2);
        }
    }

    private ValueAnimator.AnimatorUpdateListener updateListener = animation -> {
        animateValue = (int) animation.getAnimatedValue();
        invalidate();
    };
}
