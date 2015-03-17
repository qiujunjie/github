package cmcc.mhealth.activity.calendar;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout.LayoutParams;
import cmcc.mhealth.R;

/**
 * �����ؼ���Ԫ�������
 * 
 * @Description: �����ؼ���Ԫ�������
 * 
 * @FileName: DateWidgetDayCell.java
 * 
 * @Package com.calendar.demo
 * 
 * @Author Hanyonglu
 * 
 * @Date 2012-3-17 ����03:19:34
 * 
 * @Version V1.0
 */
public class CalendarWidgetDayCell extends View implements OnGestureListener {
	// �����С
	private static final int fTextSize = 28;

	// ����Ԫ��
	private OnItemClick itemClick = null;
	private Paint pt = new Paint();
	private RectF rect = new RectF();
	private String sDate = "";

	// ��ǰ����
	private int iDateYear = 0;
	private int iDateMonth = 0;
	private int iDateDay = 0;

	// ��������
	private boolean bSelected = false;
	private boolean bIsActiveMonth = false;
	private boolean bToday = false;
	private boolean bTouchedDown = false;
	private boolean bHoliday = false;
	private boolean hasRecord = false;

	public static int ANIM_ALPHA_DURATION = 100;

	private GestureDetector gestureDetector = null;

	private boolean bHandled;

	private boolean mFocused;

	public interface OnItemClick {
		public void OnClick(CalendarWidgetDayCell item);
	}

	// ���캯��
	@SuppressWarnings("deprecation")
	public CalendarWidgetDayCell(Context context, int iWidth, int iHeight) {
		super(context);
		gestureDetector = new GestureDetector(this);
		setFocusable(true);
		setLayoutParams(new LayoutParams(iWidth, iHeight));
	}

	// ȡ����ֵ
	public Calendar getDate() {
		Calendar calDate = Calendar.getInstance();
		calDate.clear();
		calDate.set(Calendar.YEAR, iDateYear);
		calDate.set(Calendar.MONTH, iDateMonth);
		calDate.set(Calendar.DAY_OF_MONTH, iDateDay);
		return calDate;
	}

	// ���ñ���ֵ
	public void setData(int iYear, int iMonth, int iDay, Boolean bToday,
			Boolean bHoliday, int iActiveMonth, boolean hasRecord) {
		iDateYear = iYear;
		iDateMonth = iMonth;
		iDateDay = iDay;

		this.sDate = Integer.toString(iDateDay);
		this.bIsActiveMonth = (iDateMonth == iActiveMonth);
		this.bToday = bToday;
		this.bHoliday = bHoliday;
		this.hasRecord = hasRecord;
	}

	// ���ػ��Ʒ���
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		rect.set(0, 0, this.getWidth(), this.getHeight());
		rect.inset(1, 1);

		mFocused = IsViewFocused();

		drawDayView(canvas, mFocused);
		drawDayNumber(canvas);
	}

	public boolean IsViewFocused() {
		return (this.isFocused() || bTouchedDown);
	}

	// ������������
	private void drawDayView(Canvas canvas, boolean bFocused) {

		if (bSelected || bFocused) {
			// LinearGradient lGradBkg = null;

			// if (bFocused) {
			// lGradBkg = new LinearGradient(rect.left, 0, rect.right, 0,
			// Color.rgb(109, 148, 217), Color.rgb(109, 148, 217),
			// Shader.TileMode.CLAMP);
			// }

			if (bSelected) {// ѡ����
			// lGradBkg = new LinearGradient(rect.left, 0, rect.right, 0,
			// Color.rgb(0, 128, 0), Color.rgb(150, 186, 24),
			// Shader.TileMode.CLAMP);
			// pt.setShader(lGradBkg);
				Paint newpaint = new Paint();
				// newpaint.setColor(Color.WHITE);
				newpaint.setColor(getResources().getColor(R.color.blue));
				// newpaint.setColor(Color.WHITE);
				newpaint.setStyle(Style.FILL_AND_STROKE);
				rect.set(-2, -2, this.getWidth() - 2, this.getHeight() - 2);
				canvas.drawRect(rect, newpaint);

				newpaint.setColor(Color.WHITE);
				rect.set(2, 2, this.getWidth() - 4, this.getHeight() - 4);
				canvas.drawRect(rect, newpaint);
			}

			pt.setShader(null);

		} else {
			pt.setColor(getColorBkg(bHoliday, bToday));
			canvas.drawRect(rect, pt);
		}

		if (hasRecord) {
			CreateReminder(canvas, MyCalendarActivity.special_Reminder);
		}
		// else if (!hasRecord && !bToday && !bSelected) {
		// CreateReminder(canvas, Calendar_TestActivity.Calendar_DayBgColor);
		// }
	}

	// ���������е�����
	public void drawDayNumber(Canvas canvas) {
		// draw day number
		pt.setTypeface(null);
		pt.setAntiAlias(true);
		pt.setShader(null);
		pt.setFakeBoldText(true);
		// pt.setColor(MainActivity.isPresentMonth_FontColor);
		if (bSelected) {
			// ѡ�е�������ɫ
			pt.setTextSize(35);
			pt.setColor(getResources().getColor(R.color.gold_I));
		} else if (bToday) {
			pt.setTextSize(40);
			pt.setColor(getResources().getColor(R.color.white));
		} else {
			// ���³��������������������ɫ
			pt.setTextSize(fTextSize);
			pt.setColor(MyCalendarActivity.isPresentMonth_FontColor);
		}
		pt.setUnderlineText(false);

		if (!bIsActiveMonth)
			// pt.setColor(MainActivity.unPresentMonth_FontColor);
			pt.setColor(MyCalendarActivity.unPresentMonth_FontColor);

		if (bToday)
			pt.setUnderlineText(true);

		final int iPosX = (int) rect.left + ((int) rect.width() >> 1)
				- ((int) pt.measureText(sDate) >> 1);

		final int iPosY = (int) (this.getHeight()
				- (this.getHeight() - getTextHeight()) / 2 - pt
				.getFontMetrics().bottom);

		canvas.drawText(sDate, iPosX, iPosY, pt);

		pt.setUnderlineText(false);
	}

	// �õ�����߶�
	private int getTextHeight() {
		return (int) (-pt.ascent() + pt.descent());
	}

	// �����������ز�ͬ��ɫֵ
	public int getColorBkg(boolean bHoliday, boolean bToday) {
		if (bToday)
			return MyCalendarActivity.isToday_BgColor;
		// if (bHoliday) //������ĩ�����ⱳ��ɫ����ȥ��ע��
		// return Calendar_TestActivity.isHoliday_BgColor;

		if (!bIsActiveMonth) {
			return MyCalendarActivity.isHoliday_BgColor;
		}
		return MyCalendarActivity.Calendar_DayBgColor;
	}

	// �����Ƿ�ѡ��
	@Override
	public void setSelected(boolean bEnable) {
		if (this.bSelected != bEnable) {
			this.bSelected = bEnable;
			this.invalidate();
		}
	}

	public void setItemClick(OnItemClick itemClick) {
		this.itemClick = itemClick;
	}

	public void doItemClick() {
		if (itemClick != null)
			itemClick.OnClick(this);
	}

	// ����¼�
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean bResult = super.onKeyDown(keyCode, event);
		if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
				|| (keyCode == KeyEvent.KEYCODE_ENTER)) {
			doItemClick();
		}
		return bResult;
	}

	// ��͸���Ƚ���
	public static void startAlphaAnimIn(View view) {
		AlphaAnimation anim = new AlphaAnimation(0.5F, 1);
		anim.setDuration(ANIM_ALPHA_DURATION);
		anim.startNow();
		view.startAnimation(anim);
	}

	public void CreateReminder(Canvas canvas, int Color) {
		pt.setStyle(Paint.Style.FILL_AND_STROKE);
		pt.setColor(Color);
		Path path = new Path();
		path.moveTo(rect.right - rect.width() / 4, rect.top);
		path.lineTo(rect.right, rect.top);
		path.lineTo(rect.right, rect.top + rect.width() / 4);
		path.lineTo(rect.right - rect.width() / 4, rect.top);
		path.close();
		canvas.drawPath(path, pt);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		bHandled = false;
		// if (event.getAction() == MotionEvent.ACTION_DOWN) {
		// bHandled = true;
		// bTouchedDown = true;
		// invalidate();
		// startAlphaAnimIn(CalendarWidgetDayCell.this);
		// }
		
		bHandled = this.gestureDetector.onTouchEvent(event);
		if (!bHandled) {
			if (event.getAction() == MotionEvent.ACTION_CANCEL) {
				bHandled = true;
				bTouchedDown = false;
				invalidate();
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				bHandled = true;
				bTouchedDown = false;
				invalidate();
				doItemClick();
			}
		}
		return bHandled;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e1.getX() - e2.getX() > 100) {
			// next month
			((MyCalendarActivity) this.getContext()).nextMonth();
			return true;
		} else if (e1.getX() - e2.getX() < -100) {
			// preMonth
			((MyCalendarActivity) this.getContext()).preMonth();
			return true;
		}
		return false;
	}
}