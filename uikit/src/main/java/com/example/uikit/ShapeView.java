package com.example.uikit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.widget.R;

public class ShapeView extends View {
    private int color;
    private Shape shape;
    private int rectRadius;

    private Paint paint;
    private Path trianglePath;
    private final static int initialSize = 150;

    public ShapeView(Context context) {
        super(context);
        initialize(context, null);
    }

    public ShapeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs,
                    R.styleable.ShapeView, 0, 0);
            try {
                color = typedArray.getColor(R.styleable.ShapeView_color, R.attr.colorAccent);
                shape = Shape.values()[typedArray.getInt(R.styleable.ShapeView_shape, 0)];
                rectRadius = typedArray.getDimensionPixelSize(R.styleable.ShapeView_rectRadius, 0);
            } finally {
                typedArray.recycle();
            }
        }

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);

        trianglePath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getWidth() - getPaddingRight();
        int bottom = getHeight() - getPaddingBottom();
        switch (shape) {
            case RECTANGLE:
                canvas.drawRoundRect(left, top, right, bottom, rectRadius, rectRadius, paint);
                break;
            case CIRCLE:
                int radius = Math.min((right - left), (bottom - top));
                canvas.drawCircle(
                        (right - left) / 2 + left, (bottom - top) / 2 + top,
                        radius / 2, paint);
                break;
            case TRIANGLE:
                trianglePath.moveTo(left, bottom);
                trianglePath.lineTo(right, bottom);
                trianglePath.lineTo((right - left) / 2 + left, getPaddingTop());
                canvas.drawPath(trianglePath, paint);
                break;
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

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
        invalidate();
        requestLayout();
    }

    public Shape getShape() {
        return this.shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
        invalidate();
        requestLayout();
    }

    public int getRectRadius() {
        return this.rectRadius;
    }

    public void setRectRadius(int rectRadius) {
        this.rectRadius = rectRadius;
        invalidate();
        requestLayout();
    }

    public enum Shape {
        RECTANGLE, CIRCLE, TRIANGLE
    }
}
