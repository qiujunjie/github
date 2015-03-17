package cmcc.mhealth.view;

import cmcc.mhealth.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * 使用时务必将bitmap传递进来，view内部不会decode bitmap
 * 
 * @author zy
 * 
 */
public class CenterRollingBall extends View{
	private int[] colorArray = new int[] { 
			Color.rgb(203, 203, 203), 
			Color.rgb(168, 211, 59), 
			Color.rgb(253, 230, 34),
			Color.rgb(244, 153, 17),

//            Color.rgb(255, 200, 14),
//            Color.rgb(200, 85, 80),
			};
	
	private Bitmap mBallPic;
	private Bitmap mYelPoint;
	// 分数
	private int score = 0;
	private int maxScore = 0;

	/**
	 * 设置分数
	 * 
	 * @param score
	 */
	public void setScore(int score) {
		this.score = score;
	}
	public void setMaxScore(int maxScore) {
		this.maxScore = maxScore;
	}

	public void setPics(Bitmap ball, Bitmap point) {
		this.mYelPoint = point;
		this.mBallPic = ball;
	}

	/**
	 * 重绘
	 */
	public void reDraw() {
		postInvalidate();
	}

	public CenterRollingBall(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CenterRollingBall(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/** 
	 * 普通构造函数，需要手动设置setPics 以及分数setScore，然后执行reDraw
	 * 
	 * @param context
	 */
	public CenterRollingBall(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mBallPic == null || mYelPoint == null) {
			return;
		}
		if (maxScore == 0) maxScore = 10000;
		int nanbai = score / maxScore;
		float bfb = score / (float)maxScore;
		bfb = nanbai > 2 ? 1 : bfb - nanbai;
		nanbai = nanbai > 2 ? 2 : nanbai;
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);

		float ScaleX = (float) getHeight() / mBallPic.getHeight() * 0.9f;
		float ScaleY = (float) getWidth() / mBallPic.getWidth() * 0.9f;
		
		float finalX = mBallPic.getWidth() * 1.05f * ScaleX; 
		float finalY = mBallPic.getHeight() * 1.05f * ScaleY; 

		paint.setColor(colorArray[nanbai + 1]);
		canvas.drawArc(new RectF(0, 0, finalX, finalY), -90, 360 * bfb, true, paint);
		paint.setColor(colorArray[nanbai]);
		canvas.drawArc(new RectF(0, 0, finalX, finalY), 360 * bfb - 90, 360 - 360 * bfb, true, paint);
		// canvas.drawArc(new RectF(0,0,300,300),60, 360, true, paint);
		
		float PicX = mBallPic.getWidth() * 0.025f * ScaleX;
		float PicY = mBallPic.getHeight() * 0.025f * ScaleY;
		float yPicX = finalX / 2 - mYelPoint.getHeight() / 2 * 0.55f;
		float yPicY = finalY / 2 - mYelPoint.getWidth() / 2 * 0.55f;
		
		float sx = (float) (finalX / 2 * Math.sin((bfb * 360 -180) / 180 * 3.14159));
		float sy = (float) (finalY / 2 * Math.cos((bfb * 360 -180) / 180 * 3.14159));

		canvas.drawBitmap(setScale(mBallPic, ScaleX, ScaleY), PicX, PicY, paint);
		canvas.drawBitmap(setScale(mYelPoint,0.55f,0.55f), yPicX - sx * 0.94f, yPicY + sy * 0.94f, paint);
	}

	public Bitmap setScale(Bitmap bitmap, float scalex, float scaley) {
		Matrix matrix = new Matrix();
		matrix.postScale(scalex, scaley);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

	}
}
