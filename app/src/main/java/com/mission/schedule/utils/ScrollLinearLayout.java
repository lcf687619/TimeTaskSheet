package com.mission.schedule.utils;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class ScrollLinearLayout extends LinearLayout {

	private VelocityTracker mVelocityTracker; // 用于判断甩动手势
	private static final int SNAP_VELOCITY = 600;
	private Scroller mScroller;
	private float mLastMotionX; // 记住上次触摸屏的位置
	private int mCurScreen;
	private boolean isDirection;
	private int pageCount;
	private Handler handler;

	public ScrollLinearLayout(Context context) {
		super(context);
		initView(context);
	}

	public ScrollLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private GestureDetector mGD;

	private void initView(Context context) {
		final Context cx = getContext();
		// 设置滚动减速器
		mScroller = new Scroller(cx, new DecelerateInterpolator(0.5f));
		mGD = new GestureDetector(context,
				new GestureDetector.SimpleOnGestureListener() {

					@Override
					public boolean onSingleTapConfirmed(MotionEvent ev) {
						isCutOff = false;
						ev.setAction(MotionEvent.ACTION_DOWN);
						dispatchTouchEvent(ev);
						ev.setAction(MotionEvent.ACTION_UP);
						dispatchTouchEvent(ev);
						return true;
					}
				});
	}

	public boolean getDirection() {
		return isDirection;
	}

	public int getmCurScreen() {
		return mCurScreen;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	/**
	 * 此方法为最后机会来修改mScrollX,mScrollY. 这方法后将根据mScrollX,mScrollY来偏移Canvas已实现内容滚动
	 */
	@Override
	public void computeScroll() {
		super.computeScroll();
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}

	}

	private boolean isCutOff = true;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (isCutOff) {
			if (ev.getAction() == MotionEvent.ACTION_DOWN) {
				return true;
			}
			return super.onInterceptTouchEvent(ev);
		} else {
			isCutOff = true;
			return super.onInterceptTouchEvent(ev);
		}
	}

	public void snapToDestination() {
		final int screenWidth = getWidth();
		final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
		snapToScreen(destScreen);
	}

	public void snapToScreen(int whichScreen) {
		int count = getChildCount();
		if (count % pageCount == 0) {
			count = count / pageCount;
		} else {
			count = (count / pageCount) + 1;
		}
		whichScreen = Math.max(0, Math.min(whichScreen, count - 1));
		if (getScrollX() != (whichScreen * getWidth())) {
			final int delta = whichScreen * getWidth() - getScrollX();
			mScroller.startScroll(getScrollX(), 0, delta, 0,
					Math.abs(delta/5));

			mCurScreen = whichScreen;
			invalidate(); // Redraw the layout
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		// 触摸点
		float x = event.getX();
		mGD.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();
				mVelocityTracker.addMovement(event);
			}
			// 如果屏幕的动画还没结束，你就按下了，我们就结束上一次动画，即开始这次新ACTION_DOWN的动画
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mLastMotionX = x;

			break;

		case MotionEvent.ACTION_MOVE:
			int deltaX = (int) (mLastMotionX - x);
			// if (IsCanMove(deltaX)) {
			// if (mVelocityTracker != null) {
			// mVelocityTracker.addMovement(event);
			// }
			// mLastMotionX = x;
			// scrollBy(deltaX, 0);
			// }

			if (mVelocityTracker != null) {
				mVelocityTracker.addMovement(event);
			}
			mLastMotionX = x;
			scrollBy(deltaX, 0);

			break;

		case MotionEvent.ACTION_UP:
			int velocityX = 0;
			if (mVelocityTracker != null) {
				mVelocityTracker.addMovement(event);
				mVelocityTracker.computeCurrentVelocity(1000);
				velocityX = (int) mVelocityTracker.getXVelocity();
			}
			if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {
				// Fling enough to move left
				isDirection = false;
				snapToScreen(mCurScreen - 1);
			} else if (velocityX < -SNAP_VELOCITY
					&& mCurScreen < getChildCount() - 1) {
				// Fling enough to move right
				isDirection = true;
				snapToScreen(mCurScreen + 1);
			} else {
				snapToDestination();
			}

			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}

			break;
		}

		return true;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			final int childCount = getChildCount();
			int childLeft = 0;
			for (int i = 0; i < childCount; i++) {
				final View childView = getChildAt(i);
				if (childView.getVisibility() != View.GONE) {
					final int childWidth = childView.getMeasuredWidth();
					childView.layout(childLeft, 0, childLeft + childWidth,
							childView.getMeasuredHeight());
					childLeft += childWidth;
				}
				if (i == childCount - 1 && handler != null)
					handler.sendEmptyMessage(0);
			}
		}

	}

}
