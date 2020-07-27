package com.example.uikit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.example.widget.R;

public class ProgressView extends View {
    private boolean arrowEnabled = false;
    private float startTrim = 0.0f;
    private float endTrim = 0.0f;
    private int indicatorColor;

    private Paint paint;
    private CircularProgressDrawable progressDrawable;
    private final static int initialSize = 170;

    public ProgressView(Context context) {
        super(context);
        initialize(context, null);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        if (attrs != null) {
            initializeAttributes(attrs);
        }

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(ContextCompat.getColor(getContext(), android.R.color.white));
        paint.setStyle(Paint.Style.FILL);
        paint.setShadowLayer(10, 2, 2,
                ContextCompat.getColor(context, android.R.color.darker_gray));

        progressDrawable = new CircularProgressDrawable(context);
        progressDrawable.setArrowEnabled(arrowEnabled);
        progressDrawable.setStartEndTrim(startTrim, endTrim);
        progressDrawable.setColorSchemeColors(indicatorColor);
        progressDrawable.setStyle(CircularProgressDrawable.DEFAULT);
    }

    private void initializeAttributes(AttributeSet attrs) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs,
                R.styleable.ProgressView, 0, 0);
        try {
            arrowEnabled = typedArray.getBoolean(R.styleable.ProgressView_arrowEnabled,
                    false);
            startTrim = typedArray.getFloat(R.styleable.ProgressView_startTrim, 0.0f);
            endTrim = typedArray.getFloat(R.styleable.ProgressView_endTrim, 0.0f);
            indicatorColor = typedArray.getColor(R.styleable.ProgressView_indicatorColor,
                    R.attr.colorAccent);
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (progressDrawable != null) {
            progressDrawable.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (progressDrawable != null && progressDrawable.isRunning()) {
            progressDrawable.stop();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width, height;

        if ((getLayoutParams().width == ViewGroup.LayoutParams.MATCH_PARENT)
                || (getLayoutParams().width == ViewGroup.LayoutParams.FILL_PARENT)) {
            width = MeasureSpec.getSize(widthMeasureSpec);
        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            width = initialSize;
        } else {
            width = getLayoutParams().width;
        }

        if ((getLayoutParams().height == ViewGroup.LayoutParams.MATCH_PARENT)
                || (getLayoutParams().height == ViewGroup.LayoutParams.FILL_PARENT)) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            height = initialSize;
        } else {
            height = getLayoutParams().height;
        }

        setMeasuredDimension(
                resolveSize(width + getPaddingLeft() + getPaddingRight(), widthMeasureSpec),
                resolveSize(height + getPaddingTop() + getPaddingBottom(), heightMeasureSpec)
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int left = getPaddingLeft();
        int right = getWidth() - getPaddingRight();
        int top = getPaddingTop();
        int bottom = getHeight() - getPaddingBottom();

        canvas.drawCircle(
                (right - left) / 2 + left,
                (bottom - top) / 2 + top,
                (initialSize - 50) / 2,
                paint
        );

        progressDrawable.setBounds(0, 0, getWidth(), getHeight());
        progressDrawable.setCenterRadius(0);
        progressDrawable.setStyle(CircularProgressDrawable.DEFAULT);
        progressDrawable.draw(canvas);
        invalidate();
    }
}
