package cmcc.mhealth.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cmcc.mhealth.R;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.view.wheel.OnWheelChangedListener;
import cmcc.mhealth.view.wheel.WheelView;
import cmcc.mhealth.view.wheel.adapters.AbstractWheelTextAdapter;
import cmcc.mhealth.view.wheel.adapters.ArrayWheelAdapter;
import cmcc.mhealth.view.wheel.adapters.NumericWheelAdapter;

public class MyWheelView {
	private WheelView hours;
	private WheelView mins;
	private WheelView ampm;
	private WheelView day;
	private String mStrHours;
	private String mStrMins;
	private String mStrDay;
	private String mStrAPM;
	private DayArrayAdapter mDayAdapter;

	public String getSelectDate() {
		Logger.e("dat --- ",mDayAdapter.getItemText(day.getCurrentItem()).toString());
		Logger.e("hours --- ",hours.getCurrentItem()+"");
		Logger.e("mins --- ",mins.getCurrentItem()+"");
		Logger.e("ampm --- ",ampm.getCurrentItem()+"");
		mStrDay = mDayAdapter.getItemText(day.getCurrentItem()).toString();
		mStrHours = hours.getCurrentItem()+"";
		mStrMins = mins.getCurrentItem()+"";
		mStrAPM = ampm.getCurrentItem()+"";
		return mStrDay+mStrHours+mStrMins+mStrAPM;
	}

	public View getview(Context context) {
		View viewDialog = View.inflate(context, R.layout.time2_layout, null);
		
//		OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
//			public void onChanged(WheelView wheel, int oldValue, int newValue) {
//				switch (wheel.getId()) {
//				case R.id.hour:
//					mStrHours = newValue+"";
//					break;
//				case R.id.mins:
//					mStrMins = newValue+"";
//					break;
//				case R.id.ampm:
//					mStrAPM = newValue+"";
//					break;
//				case R.id.day:
////					mStrDay = newValue+"";
//					break;
//				default:
//					break;
//				}
//			}
//		};
		
		hours = (WheelView) viewDialog.findViewById(R.id.hour);
		NumericWheelAdapter hourAdapter = new NumericWheelAdapter(context, 0, 23);
		hourAdapter.setItemResource(R.layout.wheel_text_item);
		hourAdapter.setItemTextResource(R.id.text);
		hours.setViewAdapter(hourAdapter);
//		hours.addChangingListener(wheelListener);

		mins = (WheelView) viewDialog.findViewById(R.id.mins);
		NumericWheelAdapter minAdapter = new NumericWheelAdapter(context, 0, 59, "%02d");
		minAdapter.setItemResource(R.layout.wheel_text_item);
		minAdapter.setItemTextResource(R.id.text);
		mins.setViewAdapter(minAdapter);
		mins.setCyclic(true);
//		mins.addChangingListener(wheelListener);

		ampm = (WheelView) viewDialog.findViewById(R.id.ampm);
		ArrayWheelAdapter<String> ampmAdapter = new ArrayWheelAdapter<String>(context, new String[] { "AM", "PM" });
		ampmAdapter.setItemResource(R.layout.wheel_text_item);
		ampmAdapter.setItemTextResource(R.id.text);
		ampm.setViewAdapter(ampmAdapter);
//		ampm.addChangingListener(wheelListener);

		// set current time
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		hours.setCurrentItem(calendar.get(Calendar.HOUR));
		mins.setCurrentItem(calendar.get(Calendar.MINUTE));
		ampm.setCurrentItem(calendar.get(Calendar.AM_PM));

		day = (WheelView) viewDialog.findViewById(R.id.day);
		mDayAdapter = new DayArrayAdapter(context, calendar);
		day.setViewAdapter(mDayAdapter);
		return viewDialog;
	}

	/**
	 * Day adapter
	 * 
	 */
	private class DayArrayAdapter extends AbstractWheelTextAdapter {
		// Count of days to be shown
		private final int daysCount = 20;

		// Calendar
		Calendar calendar;

		/**
		 * Constructor
		 */
		protected DayArrayAdapter(Context context, Calendar calendar) {
			super(context, R.layout.time2_day, NO_RESOURCE);
			this.calendar = calendar;

			setItemTextResource(R.id.time2_monthday);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			int day = -daysCount / 2 + index;
			Calendar newCalendar = (Calendar) calendar.clone();
			newCalendar.roll(Calendar.DAY_OF_YEAR, day);

			View view = super.getItem(index, cachedView, parent);
			TextView weekday = (TextView) view.findViewById(R.id.time2_weekday);
			if (day == 0) {
				weekday.setText("");
			} else {
				DateFormat format = new SimpleDateFormat("EEE");
				weekday.setText(format.format(newCalendar.getTime()));
			}

			TextView monthday = (TextView) view.findViewById(R.id.time2_monthday);
			if (day == 0) {
				monthday.setText("Today");
				monthday.setTextColor(0xFF0000F0);
			} else {
				DateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
				mStrDay = format.format(newCalendar.getTime());
				monthday.setText(mStrDay);
				monthday.setTextColor(0xFF111111);
			}

			return view;
		}

		@Override
		public int getItemsCount() {
			return daysCount + 1;
		}

		@Override
		protected CharSequence getItemText(int index) {
			int day = -daysCount / 2 + index;
			Calendar newCalendar = (Calendar) calendar.clone();
			newCalendar.roll(Calendar.DAY_OF_YEAR, day);
			DateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
			return format.format(newCalendar.getTime());
		}
	}
}
