package cmcc.mhealth.slidingcontrol;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cmcc.mhealth.R;
import cmcc.mhealth.activity.HistoryPedometorDetailActivity;
import cmcc.mhealth.activity.calendar.MyCalendarActivity;
import cmcc.mhealth.basic.BaseFragment;
import cmcc.mhealth.bean.DataPedometor;
import cmcc.mhealth.bean.PedoDetailInfo;
import cmcc.mhealth.bean.PedometorInfo;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.db.MHealthProviderMetaData;
import cmcc.mhealth.db.PedometerTableMetaData;
import cmcc.mhealth.net.DataSyn;
import cmcc.mhealth.view.XListView;
import cmcc.mhealth.view.XListView.IXListViewListener;

public class HistoryListFragment extends BaseFragment implements OnClickListener, IXListViewListener {
	private static String TAG = "ListSportsHistoryActivity";
	private static final int DATE_ID = 1;

	private MySimpleAdapter mAdapter;
	private ArrayList<HashMap<String, Object>> mArrayListRecords = new ArrayList<HashMap<String, Object>>();
	private XListView mListViewRecord;

	private TextView mTextViewTitle;
	private ImageButton mImageButtonTitle;

	private Calendar calendar = Calendar.getInstance();
	private boolean flagUpdateStatus;

	private ImageButton mBack;

	public HistoryListFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Logger.e(TAG, getClass().getSimpleName() + "onCreateView");
		View view = inflater.inflate(R.layout.activity_list_sports_history, container, false);
		super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		return view;
	}

	@Override
	public void findViews() {
		initViews();
	}

	@Override
	public void clickListner() {

	}

	@Override
	public void loadLogic() {

	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_ID:
			return new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					Date date = new Date();
					// (year - 1900, monthOfYear,
					// dayOfMonth);
					SimpleDateFormat df_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");

					Intent intent = new Intent();
					intent.setClass(mActivity, HistoryPedometorDetailActivity.class);
					intent.putExtra("searchDate", df_yyyyMMdd.format(date));
					startActivity(intent);
				}
			}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	void initViews() {
		// showmenu
		mBack = findView(R.id.button_input_bg_back);
		mBack.setVisibility(View.VISIBLE);
		mBack.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.slidemenu_button));
		mBack.setOnClickListener(this);
		// title
		mTextViewTitle = (TextView) findView(R.id.textView_title);
		mTextViewTitle.setText(R.string.his_title);
		mImageButtonTitle = (ImageButton) findView(R.id.imageButton_title);
		mImageButtonTitle.setVisibility(View.VISIBLE);
		mImageButtonTitle.setBackgroundResource(R.drawable.his_list_detail);
		mImageButtonTitle.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 更改为按下时的背景图片
					v.setBackgroundResource(R.drawable.his_list_detail_on);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// 改为抬起时的图片
					v.setBackgroundResource(R.drawable.his_list_detail);
				}
				return false;
			}
		});
		mImageButtonTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// showDialog(DATE_ID);
				Intent intent = new Intent();
				intent.setClass(mActivity, MyCalendarActivity.class);
				startActivity(intent);
				mActivity.overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
			}
		});

		displaySpormActivitytory();
		// 初始化数据
		mAdapter = new MySimpleAdapter(mActivity);
		mListViewRecord = (XListView) findView(R.id.list_sports_history_input);
		mListViewRecord.setXListViewListener(this);
		mListViewRecord.setPullLoadEnable(false);
		mListViewRecord.setAdapter(mAdapter);
		mListViewRecord.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent();
				intent.setClass(mActivity, HistoryPedometorDetailActivity.class);
				if(position<1)
				    return;
				int his_id = Integer.parseInt(mArrayListRecords.get(position - 1).get("id").toString());
				String date = mArrayListRecords.get(position - 1).get(PedometerTableMetaData.DATE).toString();
				String[] str = date.split("-");
				date = str[0] + str[1] + str[2];
				date = date.substring(0, 8);
				intent.putExtra("his_id", his_id);
				intent.putExtra("searchDate", date);
				startActivity(intent);
				mActivity.overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
			}
		});
	}

	@Override
	public void onRefresh() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				updateData();
				displaySpormActivitytory();
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if (flagUpdateStatus) {
					BaseToast("刷新成功！");
				} else {
					BaseToast("刷新失败,请查看网络状况!");
				}
				resetXListView();
				mAdapter.notifyDataSetChanged();
				super.onPostExecute(result);
			}
		}.execute();
	}

	@Override
	public void onLoadMore() {
	}

	private void resetXListView() {
		mListViewRecord.stopRefresh();
		mListViewRecord.stopLoadMore();
		mListViewRecord.setRefreshTime(Common.getDateAsM_d(new Date().getTime()));
	}

	private void updateData() {

		try {
			// 手机号和密码
			SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
			String phonenum = info.getString(SharedPreferredKey.PHONENUM, null);
			String password = info.getString(SharedPreferredKey.PASSWORD, null);

			Cursor cursor = MHealthProviderMetaData.GetMHealthProvider(mActivity).GetPedometerData();
			if (!cursor.moveToNext()) {// 数据库中没有数据
				// 登录后初始化数据
				SimpleDateFormat df_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
				int update_num = 9; // 原账户登录
				if (phonenum.equals("13811029472"))// 13811029472
					update_num = 60;
				// 更新数据到昨天 更新update_num天数据
				Date date_now = new Date();
				long date_now_long = date_now.getTime();
				ArrayList<PedometorInfo> infos = new ArrayList<PedometorInfo>();
				for (int i = 0; i < update_num; i++) {
					long date_long = date_now_long - i * (1000L * 60 * 60 * 24L); // 后移一天
					String date_str = df_yyyyMMdd.format(new Date(date_long));
					PedometorInfo reqData = new PedometorInfo();
					int result = DataSyn.getInstance().getPedoInfo(phonenum, password, date_str, reqData);

					if (result == 0 && reqData.status.equals("SUCCESS"))
						infos.add(reqData);
					else
						Logger.w(TAG, "Get " + date_str + " data error");
				}
				// 一天数据插入
				if (infos.size() > 0) {
					for (int i = infos.size() - 1; i >= 0; i--) {
						PedometorInfo reqData = infos.get(i);
						MHealthProviderMetaData.GetMHealthProvider(mActivity).InsertPedometerData(reqData.datavalue, 0, false);
						// 添加详细包数据
						String fromHour = "00", toHour = "23";

						String current_date_str = reqData.date;

						PedoDetailInfo detailData = new PedoDetailInfo();
						int result = DataSyn.getInstance().getPedoInfoDetail(phonenum, password, fromHour, toHour, current_date_str, detailData);
						if (result == -1) {
							Log.e(TAG, "返回值为-1，网络读取存在错误！");
						} else if (result == 1) {
							Log.e(TAG, "返回值为1，返回数据存在问题！");
						} else {
							MHealthProviderMetaData.GetMHealthProvider(mActivity).insertPedoDetailData(detailData);
							flagUpdateStatus = true;
						}
					}
					Logger.i(TAG, "同步完成");
					// 标识数据更新成功
				}
				return;
			}

			SimpleDateFormat df_HH = new SimpleDateFormat("HH");
			SimpleDateFormat df_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat df_yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// 现在时间
			Date now_date = new Date();
			String now_date_str = df_yyyyMMdd.format(now_date);
			long now_date_long = df_yyyyMMdd.parse(now_date_str).getTime();

			DataPedometor pedoInfo = MHealthProviderMetaData.GetMHealthProvider(mActivity).getPedometerLatest();
			// 上次记录更新时间
			String current_date_str = pedoInfo.createtime;
			// 保存上次更新时间 (不要更改值)
			Date last_date = df_yyyyMMddHHmmss.parse(current_date_str);
			long last_date_long = last_date.getTime();

			// 之前的日期 形式为yyyyMMdd
			Date current = df_yyyyMMdd.parse(df_yyyyMMdd.format(last_date));
			long current_date_long = current.getTime();

			flagUpdateStatus = false;// 初始化更新标签
			while (now_date_long >= current_date_long) {
				current_date_str = df_yyyyMMdd.format(new Date(current_date_long));
				// 初始化
				PedometorInfo reqData = new PedometorInfo();
				int result = DataSyn.getInstance().getPedoInfo(phonenum, password, current_date_str, reqData);
				if (result == -1) {
					Log.e(TAG, "返回值为-1，网络读取存在错误！");
				} else if (result == 1) {
					Log.e(TAG, "返回值为1，返回数据存在问题！");
				} else {
					List<DataPedometor> data_list = reqData.datavalue;

					int flag = 0;// 判断简包数据是否更新过
					for (int i = 0; i < data_list.size(); i++) {
						// long date =
						// df_yyyyMMddHHmmss.parse(data_list.get(i).createtime).getTime();
						// 防止更新重复数据
						MHealthProviderMetaData.GetMHealthProvider(mActivity).InsertPedometerData(data_list, last_date_long, true);
						flag = 1;
					}

					// 获取详细包
					if (flag == 1 && data_list.size() > 0) {
						String fromHour = "00", toHour = "23";
						if (current_date_long == last_date_long) { // 当天
							fromHour = df_HH.format(last_date);
						}

						PedoDetailInfo detailData = new PedoDetailInfo();
						result = DataSyn.getInstance().getPedoInfoDetail(phonenum, password, fromHour, toHour, current_date_str, detailData);
						if (result == -1) {
							Log.e(TAG, "返回值为-1，网络读取存在错误！");
						} else if (result == 1) {
							Log.e(TAG, "返回值为1，返回数据存在问题！");
						} else {
							MHealthProviderMetaData.GetMHealthProvider(mActivity).insertPedoDetailData(detailData);
						}
					}
					// 标识数据更新成功
					flagUpdateStatus = true;
				}
				current_date_long = current_date_long + (1000L * 60 * 60 * 24L); // 后移一天
			}
		} catch (ParseException e) {
			Log.w(TAG, "current_date parse error");
			e.printStackTrace();
		}
	}

	// 显示列表
	class MySimpleAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public MySimpleAdapter(Context c) {
			mInflater = LayoutInflater.from(c);
		}

		public void setVisible() {
			notifyDataSetChanged();
		}

		public int getCount() {
			return mArrayListRecords.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		ViewHolder holder;

		// 这个方法在Activity载入时会调用两次?,而且是列表有几个项就循环几次.
		// 而convertView只有在Activity初始化时才会是空.
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_item_his, null);
				holder = new ViewHolder();

				holder.mItemLayoutList = (RelativeLayout) convertView.findViewById(R.id.linearlayout_history_list_item);
				holder.item_cal = (TextView) convertView.findViewById(R.id.history_item_calorie);
				holder.item_date = (TextView) convertView.findViewById(R.id.history_item_date);
				holder.item_stepnum = (TextView) convertView.findViewById(R.id.history_item_stepnum);
				holder.mItemDayDivide = (TextView) convertView.findViewById(R.id.textView_day_divide);
				holder.mItemLayoutDayDivide = (RelativeLayout) convertView.findViewById(R.id.linearLayout_day_divide);
				holder.mItemLayoutTop = (RelativeLayout) convertView.findViewById(R.id.relativelayout_history_top);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// Bind the data efficiently with the holder.
			if (holder.mItemLayoutList != null) {
				if ((position & 1) == 1)
					holder.mItemLayoutList.setBackgroundColor(Color.rgb(245, 245, 245));
				else
					holder.mItemLayoutList.setBackgroundColor(Color.WHITE);
			}
			convertView.setClickable(true);
			holder.mItemLayoutDayDivide.setVisibility(View.GONE);
			holder.item_date.setTextColor(Color.rgb(0, 0, 0));
			holder.item_cal.setTextColor(Color.rgb(0, 0, 0));
			holder.item_stepnum.setTextColor(Color.rgb(0, 0, 0));

			// 设置自动手动上传颜色
			String transType = mArrayListRecords.get(position).get(PedometerTableMetaData.TRANS_TYPE).toString();
			if (transType.equals("1")) {
				holder.item_date.setTextColor(Color.rgb(46, 176, 221));
				holder.item_cal.setTextColor(Color.rgb(46, 176, 221));
				holder.item_stepnum.setTextColor(Color.rgb(46, 176, 221));
			} else {
				holder.item_date.setTextColor(Color.rgb(254, 142, 0));
				holder.item_cal.setTextColor(Color.rgb(254, 142, 0));
				holder.item_stepnum.setTextColor(Color.rgb(254, 142, 0));
			}

			String item_date_str = mArrayListRecords.get(position).get(PedometerTableMetaData.DATE).toString();
			// Log.i(TAG, "postion:" + position);
			// 按照时间分隔
			SimpleDateFormat df_yyyyMMdd = new SimpleDateFormat("yyyy/MM/dd"); // 为了显示
			SimpleDateFormat df_HHmm = new SimpleDateFormat("HH:mm");
			SimpleDateFormat df_yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Date item_date = df_yyyyMMddHHmmss.parse(item_date_str);
				String item_date_yyyyMMdd = df_yyyyMMdd.format(item_date);
				String item_date_mmss = df_HHmm.format(item_date);

				holder.item_date.setText(item_date_mmss);

				Date today_date = new Date();
				long today_time = today_date.getTime();
				String today_date_yyyyMMdd = df_yyyyMMdd.format(today_date);

				long yesterday_time = today_time - 1000L * 60 * 60 * 24L;// 计算昨天
				Date yesterday_date = new Date(yesterday_time);
				String yesterday_date_yyyyMMdd = df_yyyyMMdd.format(yesterday_date);

				if (position == 0) {
					holder.mItemLayoutDayDivide.setVisibility(View.VISIBLE);

					if (item_date_yyyyMMdd.equals(today_date_yyyyMMdd)) {
						holder.mItemDayDivide.setText("今天");
						convertView.setClickable(false);
					} else if (item_date_yyyyMMdd.equals(yesterday_date_yyyyMMdd)) {
						holder.mItemDayDivide.setText("昨天");
						convertView.setClickable(false);
					} else {
						holder.mItemDayDivide.setText(item_date_yyyyMMdd);
						convertView.setClickable(false);
					}
				} else {
					// 和上一天比较
					String before_item_date_str = mArrayListRecords.get(position - 1).get(PedometerTableMetaData.DATE).toString();
					Date before_item_date = df_yyyyMMddHHmmss.parse(before_item_date_str);
					String before_item_date_yyyyMMdd = df_yyyyMMdd.format(before_item_date);
					// 比较日期不一样
					if (!before_item_date_yyyyMMdd.equals(item_date_yyyyMMdd)) {
						if (before_item_date_yyyyMMdd.equals(today_date_yyyyMMdd)) {
							holder.mItemLayoutDayDivide.setVisibility(View.VISIBLE);
							holder.mItemDayDivide.setText("昨天");
							convertView.setClickable(false);
						} else {
							holder.mItemLayoutDayDivide.setVisibility(View.VISIBLE);
							holder.mItemDayDivide.setText(item_date_yyyyMMdd);
							convertView.setClickable(false);
						}
					}
				}
				// holder.mItemLayoutList.setClickable(false);
				// //
				// holder.mItemDayDivide.setBackgroundDrawable(getResources().getDrawable(R.drawable.linearlayout_history_bgs));
				// holder.mItemLayoutTop.setBackgroundDrawable(getResources().getDrawable(R.drawable.linearlayout_history_bgs));
			} catch (ParseException e) {
				e.printStackTrace();
			}

			String cal_str = mArrayListRecords.get(position).get(PedometerTableMetaData.ENERGY_CONSUMPTION).toString();
			float cal_fl = Float.valueOf(cal_str);

			holder.item_cal.setText((int) cal_fl + "");
			holder.item_stepnum.setText(mArrayListRecords.get(position).get(PedometerTableMetaData.STEP_NUM).toString());

			return convertView;
		}

	}

	static class ViewHolder {
		TextView item_date, item_cal, item_stepnum; // bg_item_type
		TextView mItemDayDivide;
		ImageView itemIcon;
		RelativeLayout mItemLayoutDayDivide, mItemLayoutTop, mItemLayoutList;
	}

	// 显示列表
	public void displaySpormActivitytory() {
		// 清空之前数据
		mArrayListRecords.clear();

		Cursor cursor = MHealthProviderMetaData.GetMHealthProvider(mActivity).GetPedometerData();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			int id_index = cursor.getColumnIndex(PedometerTableMetaData._ID);
			int stepnum_index = cursor.getColumnIndex(PedometerTableMetaData.STEP_NUM);
			int cal_index = cursor.getColumnIndex(PedometerTableMetaData.ENERGY_CONSUMPTION);
			int date_index = cursor.getColumnIndex(PedometerTableMetaData.DATE);
			int transType = cursor.getColumnIndex(PedometerTableMetaData.TRANS_TYPE);

			HashMap<String, Object> record = new HashMap<String, Object>();

			record.put(PedometerTableMetaData.DATE, cursor.getString(date_index));
			record.put("id", cursor.getInt(id_index));
			record.put(PedometerTableMetaData.ENERGY_CONSUMPTION, cursor.getString(cal_index));
			record.put(PedometerTableMetaData.STEP_NUM, cursor.getString(stepnum_index));
			record.put(PedometerTableMetaData.TRANS_TYPE, cursor.getString(transType));

			mArrayListRecords.add(record);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_input_bg_back:
			showMenu();
			break;

		default:
			break;
		}
	}
}
