package cmcc.mhealth.activity.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.GestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import cmcc.mhealth.R;
import cmcc.mhealth.activity.HistoryPedometorDetailActivity;
import cmcc.mhealth.basic.BaseActivity;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Constants;

public class MyCalendarActivity extends BaseActivity {
	// �����������������
	private LinearLayout layContent = null;
	private ArrayList<CalendarWidgetDayCell> days = new ArrayList<CalendarWidgetDayCell>();

	// ���ڱ���
	public static Calendar calStartDate = Calendar.getInstance();
	private Calendar calToday = Calendar.getInstance();
	private Calendar calCalendar = Calendar.getInstance();
	private Calendar calSelected = Calendar.getInstance();

	// ��ǰ��������
	private int iMonthViewCurrentMonth = 0;
	private int iMonthViewCurrentYear = 0;
	private int iFirstDayOfWeek = Calendar.MONDAY;

	private int Calendar_Width = 0;
	private int Cell_Width = 0;
	private int Cell_Height = 0;

	// ҳ��ؼ�
	TextView Top_Date = null;
	ImageButton btn_pre_month = null;
	ImageButton btn_next_month = null;

	private Button ButtonBack = null;
	private Button ButtonSure = null;
	// TextView arrange_text = null;
	LinearLayout mainLayout = null;
	LinearLayout arrange_layout = null;

	// ����Դ
	ArrayList<String> Calendar_Source = null;
	Hashtable<Integer, Integer> calendar_Hashtable = new Hashtable<Integer, Integer>();
	Boolean[] flag = null;
	Calendar startDate = null;
	Calendar endDate = null;
	int dayvalue = -1;

	public static int Calendar_WeekBgColor = 0;
	public static int Calendar_DayBgColor = 0;
	public static int isHoliday_BgColor = 0;
	public static int unPresentMonth_FontColor = 0;
	public static int isPresentMonth_FontColor = 0;
	public static int isToday_BgColor = 0;
	public static int special_Reminder = 0;
	public static int common_Reminder = 0;
	public static int Calendar_WeekFontColor = 0;

	String UserName = "";

	public GestureDetector mGestureDetector;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);

		// �����Ļ��͸ߣ���Ӌ�����Ļ���ȷ��ߵȷݵĴ�С
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();
		Calendar_Width = screenWidth;
		Cell_Width = Calendar_Width / 7 + 1;

		// �ƶ������ļ�������������
		mainLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.activity_calendar, null);
		LinearLayout llthemain = (LinearLayout) mainLayout.findViewById(R.id.ll_themain_calendar);
		llthemain.measure(0, 0);
		Cell_Height = (screenHeight - llthemain.getMeasuredHeight() - Common.dip2px(MyCalendarActivity.this, 35)) / 6 - 5;
		// mainLayout.setPadding(2, 0, 2, 0);
		setContentView(mainLayout);

		// ����ʶ��
		// mGestureDetector = new GestureDetector(this);

		// ȷ���ͻ��˰�ť
		ButtonSure = (Button) findViewById(R.id.button_sure);
		ButtonBack = (Button) findViewById(R.id.button_back);

		ButtonSure.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MyCalendarActivity.this, HistoryPedometorDetailActivity.class);
				intent.putExtra("searchDate", Common.getDateAsYYYYMMDD(calSelected.getTime().getTime()));
				startActivity(intent);
				MyCalendarActivity.this.finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
			}
		});

		ButtonBack.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyCalendarActivity.this.finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
			}
		});

		// �����ؼ��������¼�
		Top_Date = (TextView) findViewById(R.id.Top_Date);
		btn_pre_month = (ImageButton) findViewById(R.id.btn_pre_month);
		btn_next_month = (ImageButton) findViewById(R.id.btn_next_month);
		btn_pre_month.setOnClickListener(new Pre_MonthOnClickListener());
		btn_next_month.setOnClickListener(new Next_MonthOnClickListener());

		// ���㱾�������еĵ�һ��(һ�������µ�ĳ��)������������
		calStartDate = getCalendarStartDate();
		mainLayout.addView(generateCalendarMain());
		CalendarWidgetDayCell daySelected = updateCalendar();

		if (daySelected != null)
			daySelected.requestFocus();

		LinearLayout.LayoutParams Param1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);

		ScrollView view = new ScrollView(this);
		arrange_layout = createLayout(LinearLayout.VERTICAL);
		arrange_layout.setPadding(5, 2, 0, 0);
		// arrange_text = new TextView(this);
		mainLayout.setBackgroundColor(Color.WHITE);
		// arrange_text.setTextColor(Color.BLACK);
		// arrange_text.setTextSize(18);
		// arrange_layout.addView(arrange_text);

		startDate = GetStartDate();
		calToday = GetTodayDate();

		endDate = GetEndDate(startDate);
		view.addView(arrange_layout, Param1);
		mainLayout.addView(view);

		// �½��߳�
		new Thread() {
			@Override
			public void run() {
				int day = GetNumFromDate(calToday, startDate);

				if (calendar_Hashtable != null && calendar_Hashtable.containsKey(day)) {
					dayvalue = calendar_Hashtable.get(day);
				}
			}

		}.start();

		Calendar_WeekBgColor = this.getResources().getColor(R.color.Calendar_WeekBgColor);
		Calendar_DayBgColor = this.getResources().getColor(R.color.Calendar_DayBgColor);
		isHoliday_BgColor = this.getResources().getColor(R.color.isHoliday_BgColor);
		unPresentMonth_FontColor = this.getResources().getColor(R.color.unPresentMonth_FontColor);
		isPresentMonth_FontColor = this.getResources().getColor(R.color.isPresentMonth_FontColor);
		isToday_BgColor = this.getResources().getColor(R.color.isToday_BgColor);
		special_Reminder = this.getResources().getColor(R.color.specialReminder);
		common_Reminder = this.getResources().getColor(R.color.commonReminder);
		Calendar_WeekFontColor = this.getResources().getColor(R.color.Calendar_WeekFontColor);
	}

	protected String GetDateShortString(Calendar date) {
		String returnString = date.get(Calendar.YEAR) + "/";
		returnString += date.get(Calendar.MONTH) + 1 + "/";
		returnString += date.get(Calendar.DAY_OF_MONTH);

		return returnString;
	}

	// �õ������������е����
	private int GetNumFromDate(Calendar now, Calendar returnDate) {
		Calendar cNow = (Calendar) now.clone();
		Calendar cReturnDate = (Calendar) returnDate.clone();
		setTimeToMidnight(cNow);
		setTimeToMidnight(cReturnDate);

		long todayMs = cNow.getTimeInMillis();
		long returnMs = cReturnDate.getTimeInMillis();
		long intervalMs = todayMs - returnMs;
		int index = millisecondsToDays(intervalMs);

		return index;
	}

	private int millisecondsToDays(long intervalMs) {
		return Math.round((float)(intervalMs / (1000 * 86400)));
	}

	private void setTimeToMidnight(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	}

	// ���ɲ���
	@SuppressWarnings("deprecation")
	private LinearLayout createLayout(int iOrientation) {
		LinearLayout lay = new LinearLayout(this);
		lay.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		lay.setOrientation(iOrientation);

		return lay;
	}

	// ��������ͷ��
	private View generateCalendarHeader() {
		LinearLayout layRow = createLayout(LinearLayout.HORIZONTAL);
		// layRow.setBackgroundColor(Color.argb(255, 207, 207, 205));

		for (int iDay = 0; iDay < 7; iDay++) {
			CalendarWidgetDayHeader day = new CalendarWidgetDayHeader(this, Cell_Width, Common.dip2px(MyCalendarActivity.this, 35));

			final int iWeekDay = CalendarDayStyle.getWeekDay(iDay, iFirstDayOfWeek);
			day.setData(iWeekDay);
			layRow.addView(day);
		}

		return layRow;
	}

	// ������������
	private View generateCalendarMain() {
		layContent = createLayout(LinearLayout.VERTICAL);
		// layContent.setPadding(1, 0, 1, 0);
		layContent.setBackgroundColor(Color.argb(255, 205, 205, 203));
		layContent.addView(generateCalendarHeader());
		days.clear();

		for (int iRow = 0; iRow < 6; iRow++) {
			layContent.addView(generateCalendarRow());
		}

		return layContent;
	}

	// ���������е�һ�У���������
	private View generateCalendarRow() {
		// TODO
		LinearLayout layRow = createLayout(LinearLayout.HORIZONTAL);

		for (int iDay = 0; iDay < 7; iDay++) {
			CalendarWidgetDayCell dayCell = new CalendarWidgetDayCell(this, Cell_Width, Cell_Height);
			dayCell.setItemClick(mOnDayCellClick);
			days.add(dayCell);
			layRow.addView(dayCell);
		}

		return layRow;
	}

	// ���õ������ںͱ�ѡ������
	private Calendar getCalendarStartDate() {
		calToday.setTimeInMillis(System.currentTimeMillis());
		calToday.setFirstDayOfWeek(iFirstDayOfWeek);

		if (calSelected.getTimeInMillis() == 0) {
			calStartDate.setTimeInMillis(System.currentTimeMillis());
			calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
		} else {
  
			calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
		}

		UpdateStartDateForMonth();
		return calStartDate;
	}

	// ���ڱ������ϵ����ڶ��Ǵ���һ��ʼ�ģ��˷���������������ڱ�����������ʾ������
	private void UpdateStartDateForMonth() {
		iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);
		iMonthViewCurrentYear = calStartDate.get(Calendar.YEAR);
		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		calStartDate.set(Calendar.HOUR_OF_DAY, 0);
		calStartDate.set(Calendar.MINUTE, 0);
		calStartDate.set(Calendar.SECOND, 0);
		// update days for week
		UpdateCurrentMonthDisplay();
		int iDay = 0;
		int iStartDay = iFirstDayOfWeek;

		if (iStartDay == Calendar.MONDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
			if (iDay < 0)
				iDay = 6;
		}

		if (iStartDay == Calendar.SUNDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
			if (iDay < 0)
				iDay = 6;
		}

		calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);
	}

	// ��������
	private CalendarWidgetDayCell updateCalendar() {
		CalendarWidgetDayCell daySelected = null;
		boolean bSelected = false;
		final boolean bIsSelection = (calSelected.getTimeInMillis() != 0);
		final int iSelectedYear = calSelected.get(Calendar.YEAR);
		final int iSelectedMonth = calSelected.get(Calendar.MONTH);
		final int iSelectedDay = calSelected.get(Calendar.DAY_OF_MONTH);
		calCalendar.setTimeInMillis(calStartDate.getTimeInMillis());

		for (int i = 0; i < days.size(); i++) {
			final int iYear = calCalendar.get(Calendar.YEAR);
			final int iMonth = calCalendar.get(Calendar.MONTH);
			final int iDay = calCalendar.get(Calendar.DAY_OF_MONTH);
			final int iDayOfWeek = calCalendar.get(Calendar.DAY_OF_WEEK);
			CalendarWidgetDayCell dayCell = days.get(i);

			// �ж��Ƿ���
			boolean bToday = false;

			if (calToday.get(Calendar.YEAR) == iYear) {
				if (calToday.get(Calendar.MONTH) == iMonth) {
					if (calToday.get(Calendar.DAY_OF_MONTH) == iDay) {
						bToday = true;
					}
				}
			}

			// check holiday
			boolean bHoliday = false;
			if ((iDayOfWeek == Calendar.SATURDAY) || (iDayOfWeek == Calendar.SUNDAY))
				bHoliday = true;
			if ((iMonth == Calendar.JANUARY) && (iDay == 1))
				bHoliday = true;

			// �Ƿ�ѡ��
			bSelected = false;

			if (bIsSelection)
				if ((iSelectedDay == iDay) && (iSelectedMonth == iMonth) && (iSelectedYear == iYear)) {
					bSelected = true;
				}

			dayCell.setSelected(bSelected);

			// �Ƿ��м�¼
			boolean hasRecord = false;

			if (flag != null && flag[i] == true && calendar_Hashtable != null && calendar_Hashtable.containsKey(i)) {
				// hasRecord = flag[i];
				hasRecord = Calendar_Source.get(calendar_Hashtable.get(i)).contains(UserName);
			}

			if (bSelected)
				daySelected = dayCell;

			dayCell.setData(iYear, iMonth, iDay, bToday, bHoliday, iMonthViewCurrentMonth, hasRecord);

			calCalendar.add(Calendar.DAY_OF_MONTH, 1);
			dayCell.invalidate();
		}

		layContent.invalidate();

		return daySelected;
	}

	// ����������������ʾ������
	private void UpdateCurrentMonthDisplay() {
		String date = calStartDate.get(Calendar.YEAR) + "��" + (calStartDate.get(Calendar.MONTH) + 1) + "��";
		Top_Date = (TextView) findViewById(R.id.Top_Date);
		Top_Date.setText(date);
	}

	// ������°�ť�������¼�
	class Pre_MonthOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// arrange_text.setText("");
			// calSelected.setTimeInMillis(0);
			preMonth();
		}

	}

	public void preMonth() {
		iMonthViewCurrentMonth--;

		if (iMonthViewCurrentMonth == -1) {
			iMonthViewCurrentMonth = 11;
			iMonthViewCurrentYear--;
		}

		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
		calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
		calStartDate.set(Calendar.HOUR_OF_DAY, 0);
		calStartDate.set(Calendar.MINUTE, 0);
		calStartDate.set(Calendar.SECOND, 0);
		calStartDate.set(Calendar.MILLISECOND, 0);
		UpdateStartDateForMonth();

		startDate = (Calendar) calStartDate.clone();
		endDate = GetEndDate(startDate);

		// �½��߳�
		new Thread() {
			@Override
			public void run() {

				int day = GetNumFromDate(calToday, startDate);

				if (calendar_Hashtable != null && calendar_Hashtable.containsKey(day)) {
					dayvalue = calendar_Hashtable.get(day);
				}

			}
		}.start();

    updateCalendar();
	}

	// ������°�ť�������¼�
	class Next_MonthOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// arrange_text.setText("");
			// calSelected.setTimeInMillis(0);
			nextMonth();
		}

	}

	public void nextMonth() {
		iMonthViewCurrentMonth++;

		if (iMonthViewCurrentMonth == 12) {
			iMonthViewCurrentMonth = 0;
			iMonthViewCurrentYear++;
		}

		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
		calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
		UpdateStartDateForMonth();

		startDate = (Calendar) calStartDate.clone();
		endDate = GetEndDate(startDate);

		// �½��߳�
		new Thread() {
			@Override
			public void run() {
				int day = 5;

				if (calendar_Hashtable != null && calendar_Hashtable.containsKey(day)) {
					dayvalue = calendar_Hashtable.get(day);
				}
			}
		}.start();

		updateCalendar();
	}

	/**
	 * ��������������¼�
	 */
	private CalendarWidgetDayCell.OnItemClick mOnDayCellClick = new CalendarWidgetDayCell.OnItemClick() {
		public void OnClick(CalendarWidgetDayCell item) {
			calSelected.setTimeInMillis(item.getDate().getTimeInMillis());
			/*
			 * int day = GetNumFromDate(calSelected, startDate);
			 * 
			 * if (calendar_Hashtable != null &&
			 * calendar_Hashtable.containsKey(day)) {
			 * arrange_text.setText(Calendar_Source.get(calendar_Hashtable
			 * .get(day))); } else { arrange_text.setText("�������ݼ�¼"); }
			 */

			item.setSelected(true);
			updateCalendar();

			// ѡ������
			long currentTime = System.currentTimeMillis();
			long selectedTime = calSelected.getTime().getTime();
			if (currentTime / Constants.DAY_MILLSECONDS > selectedTime / Constants.DAY_MILLSECONDS) {
				Intent intent = new Intent();
				intent.setClass(MyCalendarActivity.this, HistoryPedometorDetailActivity.class);
				intent.putExtra("searchDate", Common.getDateAsYYYYMMDD(selectedTime));
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
//				MyCalendarActivity.this.finish();
			}else{
				BaseToast("��ѡ����û�����ݣ�");
			}
		}
	};

	public Calendar GetTodayDate() {
		Calendar cal_Today = Calendar.getInstance();
		cal_Today.set(Calendar.HOUR_OF_DAY, 0);
		cal_Today.set(Calendar.MINUTE, 0);
		cal_Today.set(Calendar.SECOND, 0);
		cal_Today.setFirstDayOfWeek(Calendar.MONDAY);

		return cal_Today;
	}

	// �õ���ǰ�����еĵ�һ��
	public Calendar GetStartDate() {
		int iDay = 0;
		Calendar cal_Now = Calendar.getInstance();
		cal_Now.set(Calendar.DAY_OF_MONTH, 1);
		cal_Now.set(Calendar.HOUR_OF_DAY, 0);
		cal_Now.set(Calendar.MINUTE, 0);
		cal_Now.set(Calendar.SECOND, 0);
		cal_Now.setFirstDayOfWeek(Calendar.MONDAY);

		iDay = cal_Now.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;

		if (iDay < 0) {
			iDay = 6;
		}

		cal_Now.add(Calendar.DAY_OF_WEEK, -iDay);

		return cal_Now;
	}

	public Calendar GetEndDate(Calendar startDate) {
		// Calendar end = GetStartDate(enddate);
		Calendar endDate = Calendar.getInstance();
		endDate = (Calendar) startDate.clone();
		endDate.add(Calendar.DAY_OF_MONTH, 41);
		return endDate;
	}

}