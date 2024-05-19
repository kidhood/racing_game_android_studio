package com.fu.duckracing.animation;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.CycleInterpolator;
import android.widget.SeekBar;

import androidx.appcompat.widget.AppCompatSeekBar;

public class AnimatedSeekBar extends AppCompatSeekBar {
    private ValueAnimator shakeAnimator;

    public AnimatedSeekBar(Context context) {
        super(context);
        init();
    }

    public AnimatedSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnimatedSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        shakeAnimator = ValueAnimator.ofFloat(0, 2);
        shakeAnimator.setDuration(800);
        shakeAnimator.setInterpolator(new CycleInterpolator(2));
        shakeAnimator.addUpdateListener(animation -> {
            float translationY = (float) animation.getAnimatedValue();
            getThumb().setBounds(getThumb().getBounds().left,
                    getThumb().getBounds().top + (int) translationY,
                    getThumb().getBounds().right,
                    getThumb().getBounds().bottom + (int) translationY);
            invalidate();
        });

        setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 100 && !shakeAnimator.isRunning()) {
                    shakeAnimator.start();
                } else if (progress >= 100) {
                    stopAnimation();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void stopAnimation() {
        if (shakeAnimator != null && shakeAnimator.isRunning()) {
            shakeAnimator.cancel();
            resetThumbPosition();
        }
    }

    private void resetThumbPosition() {
        getThumb().setBounds(getThumb().getBounds().left,
                0,
                getThumb().getBounds().right,
                getThumb().getBounds().height());
        invalidate();
    }
}
