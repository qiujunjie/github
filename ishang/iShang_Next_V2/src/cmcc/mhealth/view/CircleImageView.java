package cmcc.mhealth.view;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import cmcc.mhealth.common.Common;

/**
 * Ô²ÐÎµÄImageview
 */
public class CircleImageView extends ImageView {
	private Paint paint = new Paint();

	public CircleImageView(Context context) {
		super(context);
	}

	public CircleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
	}

	@Override
	protected void onDraw(Canvas canvas) {

		Drawable drawable = getDrawable();
		if (null != drawable) {
			Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
			Bitmap b = toRoundCorner(bitmap);
			final Rect rect = new Rect(0, 0, b.getWidth(), b.getHeight());
			paint.reset();
//			float x = getMeasuredWidth();
//			paint.setStyle(Style.STROKE);
//			paint.setColor(Color.WHITE);
//			paint.setAlpha(190);
//			BlurMaskFilter mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.INNER); 
//			paint.setMaskFilter(mBlur); 
//			paint.setStrokeWidth((float)10*Common.getDensity(getContext()));
//			canvas.drawCircle(x / 2, x / 2, x / 2, paint);
//			paint.reset();
			canvas.drawBitmap(b, rect, rect, paint);
		} 
		else {
			super.onDraw(canvas);
		}
	}

	private Bitmap toRoundCorner(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(getMeasuredWidth(),
				getMeasuredHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		
		float sy = (float) getHeight() / bitmap.getHeight();
		float sx = (float) getHeight() / bitmap.getWidth();
		
		paint.setAntiAlias(true);
		final Rect rect = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
		float x = getMeasuredWidth();
		canvas.drawCircle(x / 2, x / 2, x / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(setScale(bitmap, sx, sy), rect, rect, paint);
		
		paint.setStyle(Style.STROKE);
		paint.setColor(Color.WHITE);
		paint.setAlpha(150);
		BlurMaskFilter mBlur = new BlurMaskFilter(10, BlurMaskFilter.Blur.INNER); 
		paint.setMaskFilter(mBlur); 
		paint.setStrokeWidth((float)15*Common.getDensity(getContext()));
		canvas.drawCircle(getWidth() / 2, getHeight() / 2, getHeight() / 2*0.95f, paint);
		return output;
	}
	
	public Bitmap setScale(Bitmap bitmap, float sx,float sy) {
		Matrix matrix = new Matrix();
		matrix.postScale(sx, sy);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

	}
}
