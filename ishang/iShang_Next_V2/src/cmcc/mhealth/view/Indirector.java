package cmcc.mhealth.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;
import cmcc.mhealth.common.Common;

public class Indirector extends View implements OnGestureListener {
	private GestureDetector mGestureDetector;
	private Scroller mScroller;

	private int indirector = 1 << 8;
	private float weight = 50f;
	private float tempx = 0f;
	private float tempxnew = 0f;
	private float per5;
	private float per50;
	
	private boolean actionup;
	
	private Context context;

	private OnChangedListener listener;

	private int mLastX;

	public void setWeight(float weight) {
		this.weight = weight;
		if (per5 != 0 && per50 != 0) {
			indirector = (int) (-weight % 1 * 10 / 1 * per50 + 5 * per50);
			reDraw();
		}
		if (mScroller != null && !mScroller.isFinished()) {
			mScroller.forceFinished(true);
		}
	}
	/**
	 * 重绘
	 */
	public void reDraw() {
		postInvalidate();
	}

	public Indirector(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public Indirector(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public Indirector(Context context) {
		super(context);
		init(context);
	}

	@SuppressWarnings("deprecation")
	private void init(Context context) {
		this.context = context;
		mGestureDetector = new GestureDetector(this);
		mScroller = new Scroller(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (indirector == 1 << 8) {
			indirector = (int) (-weight % 1 * 10 / 1 * per50 + 5 * per50);
		}
		
		int width = getWidth();
		if (per5 == 0 || per50 == 0) {
			per5 = width / 5f;
			per50 = width / 50f;
		}

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTextSize(12 + (float) (12 * (Common.getDensity(context) - 0.5)));// 字体大小

		while (indirector < -per5) {
			indirector += per5;
			weight++;
		}
		while (indirector > per5) {
			indirector -= per5;
			weight--;
		}

		setbound();

		for (int i = -1; i < 7; i++) {
			canvas.drawLine(per5 * i + indirector, 0, per5 * i + indirector, getHeight() / 6f, paint);
			canvas.drawText("" + ((int) weight + i - 2), per5 * i + indirector - 3, getHeight() / 3f, paint);
			for (int j = 1; j < 10; j++) {
				float yl = getHeight() / 12f;
				if (j == 5)
					yl *= 1.5;
				canvas.drawLine(per50 * j + per5 * i + indirector, 0, per50 * j + per5 * i + indirector, yl, paint);
			}
		}
	}

	public Bitmap setScale(Bitmap bitmap, float scalex, float scaley) {
		Matrix matrix = new Matrix();
		matrix.postScale(scalex, scaley);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		actionup = false;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			tempx = event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			tempxnew = event.getX();
			indirector += (tempxnew - tempx);
			tempx = event.getX();
			break;
		case MotionEvent.ACTION_UP:
			tempxnew = event.getX();
			indirector += (tempxnew - tempx);
			tempx = event.getX();
			setbound();
			actionup = true;
			break;
		}
		reDraw();
		if (!mScroller.isFinished()) {
			mScroller.forceFinished(true);
		}
		mGestureDetector.onTouchEvent(event);
		return true;
	}

	private void setbound() {
		float aw = getActuallyWeight();
		if (aw < 30) {
			setWeight(30);
			if (!mScroller.isFinished()) {
				mScroller.forceFinished(true);
			}
		}
		if (aw > 120) {
			setWeight(120);
			if (!mScroller.isFinished()) {
				mScroller.forceFinished(true);
			}
		}
	}

	private float getActuallyWeight() {
		float tempw = 0;
		tempw = (int) weight - Math.round(indirector / per50) / 10f;
		return tempw + 0.5f;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		mScroller.fling(0, 0, (int) velocityX, 0, -10000, 10000, 0, 0);
		mLastX = 0;
		return true;
	}

	@Override
	public void computeScroll() {
		if (mScroller != null) {
			if (mScroller.computeScrollOffset()) {
				indirector += (mScroller.getCurrX() - mLastX);
				mLastX = mScroller.getCurrX();
				reDraw();
			}else{
				if (actionup && listener != null) {
					listener.onChanged(getActuallyWeight());
				}
			}
		}
		super.computeScroll();
	}

	public interface OnChangedListener {
		abstract void onChanged(float num);
	}

	public void setOnChangedListener(OnChangedListener listener) {
		this.listener = listener;
	}
}
