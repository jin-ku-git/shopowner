package com.youwu.shopowner.toast;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.youwu.shopowner.R;


public class CustomView extends View {

    private Paint mPaint = new Paint();
    private Path mPath = new Path();
    private float mProgress;

    //画圆
    private ValueAnimator mAnimatorCircle;
    //画勾
    private ValueAnimator mAnimatorHook;

    public CustomView(Context context) {
        super(context);
        init();
    }


    public CustomView(Context context, @Nullable AttributeSet  attrs) {
        super(context, attrs);
        init();
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10f);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(getResources().getColor(R.color.color_main));
        canvas.drawPath(mPath,mPaint);
    }

    //画圆
    public void circleAnimation() {

        if (mAnimatorCircle != null || mAnimatorHook != null){
            mAnimatorCircle.cancel();
            mAnimatorHook.cancel();
        }

        mPath.reset();
        mAnimatorCircle = ValueAnimator.ofFloat(0, 360);
        mAnimatorCircle.setInterpolator(new LinearInterpolator());
        mAnimatorCircle.setDuration(300);
        mAnimatorCircle.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgress = (float) animation.getAnimatedValue();
                mPath.addArc(20, 20, getWidth() - 20, getHeight() - 20, 90, mProgress);
                postInvalidate();
            }
        });
        mAnimatorCircle.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                hookAnimation();
            }
        });

        mAnimatorCircle.start();
    }

    //画钩
    public void hookAnimation() {

        mPath.moveTo(getWidth() / 4, getHeight() / 2);
        mAnimatorHook = ValueAnimator.ofFloat(3, 8);
        mAnimatorHook.setInterpolator(new LinearInterpolator());
        mAnimatorHook.setDuration(600);
        mAnimatorHook.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                if(progress < 5) {
                    mPath.rLineTo(progress, progress);
                }else{
                    mPath.rLineTo(progress, -progress);
                }
                postInvalidate();
            }
        });
        mAnimatorHook.start();
    }

}