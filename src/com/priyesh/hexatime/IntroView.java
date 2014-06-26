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
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"ForLoopReplaceableByForEach", "UnusedDeclaration"})
public class IntroView extends View{

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
	private SvgHelper.SvgPath mWaitPath;
	private SvgHelper.SvgPath mDragPath;
	private Paint mArrowPaint;
	private int mArrowLength;
	private int mArrowHeight;
	private int mRadius;
	private ObjectAnimator mWaitAnimator;
	private float mWait;
	private float mDrag;
	static RectF button;
	String colorAlpha = "00";
	Rect bounds, subbounds;
	GestureDetector gestureDetector;
	Context mContext;  

	public IntroView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public IntroView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext= context;
		gestureDetector = new GestureDetector(context, new GestureListener());

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IntroView, defStyle, 0);
		try {
			if (a != null) {
				mPaint.setStrokeWidth(a.getFloat(R.styleable.IntroView_strokeWidth, 1.0f));
				mPaint.setColor(a.getColor(R.styleable.IntroView_strokeColor, 0xff000000));
				mPhase = a.getFloat(R.styleable.IntroView_phase, 0.0f);
				mDuration = a.getInt(R.styleable.IntroView_duration, 4000);
				mRadius = a.getDimensionPixelSize(R.styleable.IntroView_waitRadius, 50);
				mFadeFactor = a.getFloat(R.styleable.IntroView_fadeFactor, 10.0f);
			}
		} finally {
			if (a != null) a.recycle();
		}
		init();
	}

	private void init() {

		mPaint.setStyle(Paint.Style.STROKE);

		createWaitPath();

		setLayerType(LAYER_TYPE_SOFTWARE, null);
		mSvgAnimator = ObjectAnimator.ofFloat(this, "phase", mPhase, 0.0f).setDuration(mDuration);
		mSvgAnimator.addListener(new AnimatorListener() {
			@Override 
			public void onAnimationEnd(Animator animation) {
				stopWaitAnimation();
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

		mWaitAnimator = ObjectAnimator.ofFloat(this, "wait", 1.0f, 0.0f).setDuration(mDuration);
		mWaitAnimator.setRepeatMode(ObjectAnimator.RESTART);
		mWaitAnimator.setRepeatCount(ObjectAnimator.INFINITE);
		mWaitAnimator.setInterpolator(new LinearInterpolator());
		mWaitAnimator.start();
	}

	private void createWaitPath() {
		Paint paint = new Paint(mPaint);
		paint.setStrokeWidth(paint.getStrokeWidth() * 0.7f);

		Path p = new Path();
		p.moveTo(0.0f, 0.0f);
		p.lineTo(mRadius * 6.0f, 0.0f);

		mWaitPath = new SvgHelper.SvgPath(p, paint);
		mArrowPaint = new Paint(mWaitPath.paint);

		paint = new Paint(mWaitPath.paint);
		mDragPath = new SvgHelper.SvgPath(makeDragPath(mRadius), paint);
	}

	public void setSvgResource(int resource) {
		if (mSvgResource == 0) {
			mSvgResource = resource;
		}
	}

	public void stopWaitAnimation() {
		ObjectAnimator alpha = ObjectAnimator.ofInt(mWaitPath.paint, "alpha", 0);
		alpha.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mWaitAnimator.cancel();
				ObjectAnimator.ofFloat(IntroView.this, "drag",
						1.0f, 0.0f).setDuration(mDuration / 3).start();                
			}
		});
		alpha.start();
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
				int alpha = (int) (Math.min((1.0f - mPhase) * mFadeFactor, 1.0f) * 255.0f);
				svgPath.paint.setAlpha(alpha);
				canvas.drawPath(svgPath.path, svgPath.paint);
			}
			canvas.restore();
		}
		canvas.save();
		canvas.translate(0.0f, getHeight() - getPaddingBottom() - mRadius * 3.0f);
		if (mWaitPath.paint.getAlpha() > 0) {
			canvas.translate(getWidth() / 2.0f - mRadius * 3.0f, mRadius);
		} else {
			canvas.translate((getWidth() - mDragPath.bounds.width()) / 2.0f, 0.0f);
			canvas.drawPath(mDragPath.path, mDragPath.paint);
			canvas.drawPath(mDragPath.path, mArrowPaint);

			Paint text = new Paint();
			text.setColor(Color.parseColor("#" + colorAlpha + "FFFFFF"));
			text.setTextSize(40);
			text.setAntiAlias(true);
			text.setTypeface(Typeface.createFromAsset(getContext().getAssets(),"LatoLight.ttf"));
			String activate = "Activate";
			bounds = new Rect();
			text.getTextBounds(activate, 0, 8, bounds);

			float xText = button.centerX() - (bounds.width()/2.25f);
			float yText = button.centerY() + (bounds.height()/2.25f);			

			Paint subtext = new Paint();
			subtext.setColor(Color.parseColor("#" + colorAlpha + "FFFFFF"));
			subtext.setTextSize(22);
			subtext.setAntiAlias(true);
			subtext.setTypeface(Typeface.createFromAsset(getContext().getAssets(),"Lato.ttf"));
			String settings = "DOUBLE TAP FOR SETTINGS";
			subbounds = new Rect(); 
			subtext.getTextBounds(settings, 0, 23, subbounds);

			float xsubText = button.centerX() - (subbounds.width()/2f);
			float ysubText = button.centerY() + (subbounds.height()*8);		

			canvas.drawText(activate, xText, yText, text);
			canvas.drawText(settings, xsubText, ysubText, subtext);

			int colorValue = Integer.parseInt(colorAlpha, 16);
			colorValue += 20;
			if (colorValue < 255) {				
				colorAlpha = Integer.toHexString(colorValue);
				postInvalidateDelayed(50, bounds.left, bounds.top, bounds.right, bounds.bottom);
			}
		}
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

	public float getWait() {
		return mWait;
	}

	public void setWait(float wait) {
		mWait = wait;
		invalidate();
	}
	public float getDrag() {
		return mDrag;
	}

	public void setDrag(float drag) {
		mDrag = drag;

		mDragPath.paint.setPathEffect(createPathEffect(mDragPath.length, mDrag, mArrowLength));
		mArrowPaint.setPathEffect(createArrowPathEffect(mDragPath.length, mDrag, mArrowLength));

		int alpha = (int) (Math.min((1.0f - mDrag) * mFadeFactor, 1.0f) * 255.0f);
		mDragPath.paint.setAlpha(alpha);
		mArrowPaint.setAlpha(alpha);

		invalidate();
	}

	private static PathEffect createPathEffect(float pathLength, float phase, float offset) {
		return new DashPathEffect(new float[] { pathLength, pathLength },
				Math.max(phase * pathLength, offset));
	}
	
	private PathEffect createArrowPathEffect(float pathLength, float phase, float offset) {
		return new PathDashPathEffect(makeArrow(mArrowLength, mArrowHeight), pathLength,
				Math.max(phase * pathLength, offset), PathDashPathEffect.Style.ROTATE);
	}

	private static Path makeArrow(float length, float height) {
		Path p = new Path();
		p.moveTo(-2.0f, -height / 2.0f);
		p.lineTo(length, 0.0f);
		p.close();
		return p;
	}

	private static Path makeDragPath(int radius) {
		Path p = new Path();

		button = new RectF(0.0f, -150f, 250f, -75f);
		p.addRoundRect(button, 5f, 5f, Path.Direction.CW);
		return p;
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		return gestureDetector.onTouchEvent(e);
	}

	private class GestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			float x = e.getX();
			float y = e.getY();
			mContext.startActivity(new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
			.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(getContext(), HexatimeService.class))
			.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			mContext.startActivity(new Intent (getContext(), HexatimeSettings.class));
			((Activity) mContext).overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
			return true;
		}
		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			mContext.startActivity(new Intent (getContext(), HexatimeSettings.class));				
			return true;
		}
	}

}
