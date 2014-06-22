/**
 * Copyright 2013 Romain Guy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.priyesh.hexatime;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"ForLoopReplaceableByForEach", "UnusedDeclaration"})
public class IntroView extends View {

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final SvgHelper mSvg = new SvgHelper(mPaint);
    private int mSvgResource;
    private final Object mSvgLock = new Object();
    private List<SvgHelper.SvgPath> mPaths = new ArrayList<SvgHelper.SvgPath>(0);
    private Thread mLoader;
    private float mPhase;
    private int mDuration;
    private float mFadeFactor;
    private ObjectAnimator mSvgAnimator;

    public IntroView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IntroView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IntroView, defStyle, 0);
        try {
            if (a != null) {
                mPaint.setStrokeWidth(a.getFloat(R.styleable.IntroView_strokeWidth, 1.0f));
                mPaint.setColor(a.getColor(R.styleable.IntroView_strokeColor, 0xff000000));
                mPhase = a.getFloat(R.styleable.IntroView_phase, 0.0f);
                mDuration = a.getInt(R.styleable.IntroView_duration, 4000);
                mFadeFactor = a.getFloat(R.styleable.IntroView_fadeFactor, 10.0f);
            }
        } finally {
            if (a != null) a.recycle();
        }
        init();
    }

    private void init() {
        mPaint.setStyle(Paint.Style.STROKE);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mSvgAnimator = ObjectAnimator.ofFloat(this, "phase", mPhase, 0.0f).setDuration(mDuration);
        mSvgAnimator.addListener(new AnimatorListener() {
            @Override 
            public void onAnimationEnd(Animator animation) {
            	Toast.makeText(getContext(), "done",
            			   Toast.LENGTH_LONG).show();  
            }
			@Override
			public void onAnimationStart(Animator animation) {				
			}
			@Override
			public void onAnimationCancel(Animator animation) {				
			}
			@Override
			public void onAnimationRepeat(Animator animation) {				
			}
        });
    }

    public void setSvgResource(int resource) {
        if (mSvgResource == 0) {
            mSvgResource = resource;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        synchronized (mSvgLock) {
            canvas.save();
            canvas.translate(getPaddingLeft(), getPaddingTop() - getPaddingBottom());
            final int count = mPaths.size();
            for (int i = 0; i < count; i++) {
                SvgHelper.SvgPath svgPath = mPaths.get(i);

                // We use the fade factor to speed up the alpha animation
                int alpha = (int) (Math.min((1.0f - mPhase) * mFadeFactor, 1.0f) * 255.0f);
                svgPath.paint.setAlpha(alpha);

                canvas.drawPath(svgPath.path, svgPath.paint);
            }
            canvas.restore();
        }
        canvas.save();
        canvas.restore();
    }

    @Override
    protected void onSizeChanged(final int w, final int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mLoader != null) {
            try {
                mLoader.join();
            } catch (InterruptedException e) {
            }
        }

        mLoader = new Thread(new Runnable() {
            @Override
            public void run() {
                mSvg.load(getContext(), mSvgResource);
                synchronized (mSvgLock) {
                    mPaths = mSvg.getPathsForViewport(
                            w - getPaddingLeft() - getPaddingRight(),
                            h - getPaddingTop() - getPaddingBottom());
                    updatePathsPhaseLocked();
                }
                post(new Runnable() {
                    @Override
                    public void run() {
                        if (mSvgAnimator.isRunning()) mSvgAnimator.cancel();
                        mSvgAnimator.start();
                    }
                });
            }
        }, "SVG Loader");
        mLoader.start();
    }


    private void updatePathsPhaseLocked() {
        final int count = mPaths.size();
        for (int i = 0; i < count; i++) {
            SvgHelper.SvgPath svgPath = mPaths.get(i);
            svgPath.paint.setPathEffect(createPathEffect(svgPath.length, mPhase, 0.0f));
        }
    }

    public float getPhase() {
        return mPhase;
    }

    public void setPhase(float phase) {
        mPhase = phase;
        synchronized (mSvgLock) {
            updatePathsPhaseLocked();
        }
        invalidate();
    }
    
    private static PathEffect createPathEffect(float pathLength, float phase, float offset) {
        return new DashPathEffect(new float[] { pathLength, pathLength },
                Math.max(phase * pathLength, offset));
    }
}
