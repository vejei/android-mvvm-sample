package com.example.uikit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.example.widget.R;
import com.google.android.material.button.MaterialButton;

public class ProgressButton extends MaterialButton {
    private boolean isProcessing = false;
    private String processingHint;
    private int progressBarColor;

    private CharSequence normalText;
    private CircularProgressDrawable progressDrawable;
    private VerticalImageSpan verticalImageSpan;
    private SpannableString processingSpannable;

    public ProgressButton(Context context) {
        super(context);
        initialize(context, null);
    }

    public ProgressButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs,
                    R.styleable.ProgressButton, 0, 0);
            try {
                isProcessing = typedArray.getBoolean(R.styleable.ProgressButton_processing, false);
                processingHint = typedArray.getString(R.styleable.ProgressButton_processingHint);
                progressBarColor = typedArray.getColor(R.styleable.ProgressButton_progressBarColor, Color.WHITE);
            } finally {
                typedArray.recycle();
            }
        }
        normalText = getText();

        progressDrawable = new CircularProgressDrawable(context);
        processingSpannable = new SpannableString(processingHint + "   ");
        verticalImageSpan = new VerticalImageSpan(progressDrawable);
        if (isProcessing) {
            startProcessing();
        } else {
            stopProcessing();
        }
        invalidate();
    }

    public boolean isProcessing() {
        return this.isProcessing;
    }

    public void setProcessing(boolean processing) {
        isProcessing = processing;
        if (processing) {
            startProcessing();
        } else {
            stopProcessing();
        }
    }

    private void startProcessing() {
        setEnabled(false);
        progressDrawable.setStartEndTrim(0.0f, 0.3f);
        progressDrawable.setStyle(CircularProgressDrawable.DEFAULT);
        progressDrawable.setColorSchemeColors(progressBarColor);
        progressDrawable.setCallback(new Drawable.Callback() {
            @Override
            public void invalidateDrawable(@NonNull Drawable who) {
                invalidate();
            }

            @Override
            public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {

            }

            @Override
            public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {

            }
        });
        processingSpannable.setSpan(
                verticalImageSpan,
                processingSpannable.length() - 1,
                processingSpannable.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        progressDrawable.start();
        setText(processingSpannable);
    }

    private void stopProcessing() {
        setEnabled(true);
        if (progressDrawable.isRunning()) {
            progressDrawable.stop();
        }
        setText(normalText);
    }
}
