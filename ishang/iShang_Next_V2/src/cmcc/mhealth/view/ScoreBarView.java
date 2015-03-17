package cmcc.mhealth.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import cmcc.mhealth.common.Common;

/**
 * ʹ��ʱ��ؽ�bitmap���ݽ�����view�ڲ�����decode bitmap
 * 
 * @author zy
 * 
 */
public class ScoreBarView extends View {

	private Bitmap mLeftPic;
	private Bitmap mRunPic;
	private int maxValue = 100000;
	// ����
	private int score = 0;
	private int acturallyscore = 0;
	// ������ʽ
	private Typeface typeface;

	public void setTypeface(Typeface typeface) {
		this.typeface = typeface;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * ���÷���
	 * 
	 * @param score
	 */
	public void setScore(int score) {
		this.score = score;
		this.acturallyscore = score;
	}

	/**
	 * �ֶ�����ͼƬ������������setScore() ����ֱ��ʹ��reDraw()
	 * 
	 * @param leftPic
	 *            ��ߵ�ͼƬ
	 * @param runPic
	 *            С��ͼƬ
	 */
	public void setPics(Bitmap leftPic, Bitmap runPic) {
		this.mRunPic = runPic;
		this.mLeftPic = leftPic;
	}

	/**
	 * �ػ�
	 */
	public void reDraw() {
		invalidate();
	}

	public ScoreBarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ScoreBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * ��ͨ���캯������Ҫ�ֶ�����setPics �Լ�����setScore��Ȼ��ִ��reDraw
	 * 
	 * @param context
	 */
	public ScoreBarView(Context context) {
		super(context);
	}

	/**
	 * ���������ֱ������ͼƬ���ͣ��Լ�������
	 * 
	 * @param context
	 *            ������
	 * @param score
	 *            ����
	 * @param leftPic
	 *            ��ߵ�ͼƬ
	 * @param runPic
	 *            С��ͼƬ
	 */
	public ScoreBarView(Context context, int score, Bitmap leftPic, Bitmap runPic) {
		super(context);
		this.mRunPic = runPic;
		this.mLeftPic = leftPic;
		this.score = score;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// make the view the original height + indicator height size
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mLeftPic == null || mRunPic == null) {
			return;
		}

		float leftScale = (float) getHeight() / mLeftPic.getHeight();
		float rightScale = (float) getHeight() / mRunPic.getHeight();

		float Width = this.getWidth() - mLeftPic.getWidth() * leftScale - mRunPic.getWidth() * rightScale;
		float Bl1 = 0;
		float Bl2 = 0;
		
		maxValue = maxValue >100000 ? 100000 : maxValue;
		maxValue = maxValue < 1000 ? 1000 : maxValue;

		score = score > maxValue ? maxValue : score;
		score = score < 0 ? 0 : score;

		// 10000���ڵı���Ϊ60% ����ֵ��ı���Ϊ40%
		
		float divideten = maxValue/10f;
		
		if (score <= divideten) {
			Bl1 = score / divideten * 0.2f + 0.2f;
		} else {
			Bl1 = 0.4f;
			Bl2 = (score - divideten) / (maxValue - divideten) * 0.5f;
		}

		Paint paint = new Paint();
		paint.setColor(Color.rgb(245, 186, 4));// �����м�ͼ����ɫ f6ba04
		float density = Common.getDensity(getContext());
		paint.setTextSize((int) ((24 * rightScale + 20) * density / 2f));// �����С

		canvas.drawBitmap(setScale(mLeftPic, rightScale), 0, 0, paint);// �����ͼƬ
		canvas.drawBitmap(setScale(mRunPic, rightScale), (Bl1 + Bl2) * Width + mLeftPic.getWidth() * leftScale, 0, paint);// ��С��ͼƬ

		// �м���εĻ���
		RectF r = new RectF(mLeftPic.getWidth() * leftScale, mRunPic.getHeight() * rightScale*5f/46f+0.5f, (Bl1 + Bl2) * Width + mLeftPic.getWidth() * leftScale, mRunPic.getHeight() * rightScale);
		canvas.drawRect(r, paint);

		// ��������Ļ���
		paint.setColor(Color.rgb(255, 255, 255));
		paint.setAntiAlias(true);

		if (typeface != null) {
			paint.setTypeface(typeface);
		}
		String StepScore = acturallyscore + "";
		canvas.drawText(StepScore, mLeftPic.getWidth() * leftScale - 8, (mRunPic.getHeight() * rightScale + 18 * leftScale + 8) / 2f+4, paint);

	}

	public Bitmap setScale(Bitmap bitmap, float scale) {
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

	}
}
