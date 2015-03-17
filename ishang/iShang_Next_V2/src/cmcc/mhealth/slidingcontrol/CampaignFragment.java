package cmcc.mhealth.slidingcontrol;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import cmcc.mhealth.R;
import cmcc.mhealth.activity.CampaignContentActivity;
import cmcc.mhealth.basic.BaseFragment;
import cmcc.mhealth.bean.ActivityDetailData;
import cmcc.mhealth.bean.ActivityInfo;
import cmcc.mhealth.bean.ActivityMedalInfo;
import cmcc.mhealth.bean.ListActivity;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.db.ListActivityTableMetaData;
import cmcc.mhealth.db.MHealthProviderMetaData;
import cmcc.mhealth.net.DataSyn;

public class CampaignFragment extends BaseFragment implements OnClickListener {
	
	public static String TAG = "RaceListActivity";
	private TextView mTextViewTitle;
	private ExpandableListView mExpandableListview;

	private ActivityInfo mActivityInfo;

	private List<ArrayList<HashMap<String, Object>>> mChildrenData;
	private List<String> groupData;
	public boolean isExpandable = false;
	private String mCurrentTime;
	private String mPhonenum;
	private String mPassword;
	private ArrayList<HashMap<String, Object>> mArrayActivityNow = null;
	private ArrayList<HashMap<String, Object>> mArrayActivityOld = null;
	private ArrayList<HashMap<String, Object>> mArrayActivityFuture = null;
	private List<Integer> mGroupCount;
	private int mActivitynownum;
	private int mActivityOldNum;
	private int mActivityFutureNum;
	private MyExpandableListview mExpandableListviewAdapter;
	private int intIsExpanded = 0;
	
	private String mColorRes;
	private ImageButton mBack;
	
	public int ClubId = 1;
	public CampaignFragment() { 
	}

	public CampaignFragment(int ClubId) {
		this.ClubId = ClubId;
	}
	
	public CampaignFragment(String colorRes) {
		mColorRes = colorRes;
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (savedInstanceState != null)
			mColorRes = savedInstanceState.getString("mColorRes");
		View view = inflater.inflate(R.layout.activity_racelist, container, false);
		super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		return view;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("mColorRes", mColorRes);
	}
	
	private void loadData() {
		SharedPreferences sp = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		mPhonenum = sp.getString(SharedPreferredKey.PHONENUM, "");
		mPassword = sp.getString(SharedPreferredKey.PASSWORD, "");
		mActivitynownum = sp.getInt("activity_now_num", 0);
		mActivityOldNum = sp.getInt("activity_old_num", 0);
		mActivityFutureNum = sp.getInt("activity_future_num", 0);

		Date currentDate = new Date();
		if (mCurrentTime == null) {
			mCurrentTime = Common.getDateAsYYYYMMDD(currentDate.getTime());
		}

		autoUpdate();

	}
	
	private void autoUpdate() {
		// TODO Auto-generated method stub
		new AsyncTask<Void, Void, Void>() {
			SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
			@Override
			protected Void doInBackground(Void... params) {
				String currentTime = Common.getDateAsYYYYMMDD(new Date().getTime());// yyyyMMdd
				String updateTime = info.getString("INPK_UPDATE_TIME_RACE", null);
				if (TextUtils.isEmpty(updateTime) || !updateTime.equals(currentTime)) {
					isExpandable = false;
					mActivityInfo = new ActivityInfo();
					if (DataSyn.getInstance().getActivityInfo(mPhonenum, mPassword, ClubId, mActivityInfo) == 0) {
						displayRankInfo();

						// update mediainfo
						UpdateMediaInfo();

						mActivityFutureNum = mActivityInfo.activityfuturenum;
						mActivityOldNum = mActivityInfo.activityoldnum;
						mActivitynownum = mActivityInfo.activitynownum;
						// 存更新时间
						Editor sharedata = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();
						sharedata.putInt("activity_now_num", mActivitynownum);
						sharedata.putInt("activity_old_num", mActivityOldNum);
						sharedata.putInt("activity_future_num", mActivityFutureNum);

						sharedata.putString("INPK_UPDATE_TIME_RACE", currentTime);
						sharedata.commit();

						isExpandable = true;
						// 数据填充到集合
					} else {
						mActivityInfo = null;
						Logger.i(TAG, "groupIdInfo 获取错误");
					}
				} else {
					isExpandable = true;

				}

				return null;
			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(Void result) {
				mActivitynownum = info.getInt("activity_now_num", 0);
				mActivityOldNum = info.getInt("activity_old_num", 0);
				mActivityFutureNum = info.getInt("activity_future_num", 0);
				displayRankInfo();
				mExpandableListview.expandGroup(0);
				mExpandableListview.expandGroup(1);
				mExpandableListview.expandGroup(2);
				mExpandableListviewAdapter.notifyDataSetChanged();
			}

		}.execute();
	}

	protected void UpdateMediaInfo() {
		// TODO Auto-generated method stub
		ArrayList<String> ids = new ArrayList<String>();
		for (int i = 0; i < mChildrenData.size(); i++) {
			ArrayList<HashMap<String, Object>> arr = mChildrenData.get(i);
			for (int j = 0; j < arr.size(); j++) {
				HashMap<String, Object> map = (HashMap<String, Object>) arr.get(j);
				String id = (String) map.get(ListActivityTableMetaData.ACTIVITYID);
				ids.add(id);
			}
		}

		// 删除数据库
//		MHealthProviderMetaData.GetMHealthProvider(mActivity).deleteActivityMediaInPkInfo();
		
		MHealthProviderMetaData.GetMHealthProvider(mActivity).deleteListActivity(ClubId);
		MHealthProviderMetaData.GetMHealthProvider(mActivity).deleteActivityMediaInPkInfo(ClubId);		

		ActivityMedalInfo mActivityMedalInfo = new ActivityMedalInfo();
		for (int i = 0; i < ids.size(); i++) {
			if (DataSyn.getInstance().getAvtivityMedalInfo(mPhonenum, mPassword, ClubId, mActivityMedalInfo, ids.get(i)) == 0) {
				mActivityMedalInfo.activityid =  ids.get(i);
				MHealthProviderMetaData.GetMHealthProvider(mActivity).insertActivityDetail(mActivityMedalInfo, ids.get(i) , ClubId);
			}
		}
//		MHealthProviderMetaData.GetMHealthProvider(mActivity).deleteActivityInPkInfo();
		// 添加数据库
		MHealthProviderMetaData.GetMHealthProvider(mActivity).insertListActivity(mActivityInfo.activitynow , ClubId);
		MHealthProviderMetaData.GetMHealthProvider(mActivity).insertListActivity(mActivityInfo.activityold , ClubId);
		MHealthProviderMetaData.GetMHealthProvider(mActivity).insertListActivity(mActivityInfo.activityfuture , ClubId);

	}

	private void displayRankInfo() {
		if (mActivityInfo != null) {
			// 直接拿网络返回的数据填充
			addActivityInfo(mActivityInfo,false);
		} else {
			// 数据库取数据
			ActivityInfo activityInfo = MHealthProviderMetaData.GetMHealthProvider(mActivity).getListActivity(mActivitynownum, mActivityOldNum, mActivityFutureNum, ClubId);
			addActivityInfo(activityInfo,true);
		}
	}

	private void addActivityInfo(ActivityInfo activityInfo,boolean fromdatabase) {
		mChildrenData.clear();
		mGroupCount.clear();
		mArrayActivityNow.clear();
		mArrayActivityOld.clear();
		mArrayActivityFuture.clear();

		for (int i = 0; i < activityInfo.activitynow.size(); i++) {
			ListActivity info = activityInfo.activitynow.get(i);
			HashMap<String, Object> record = new HashMap<String, Object>();
			record.put(ListActivityTableMetaData.ACTIVITYID, info.activityid);
			record.put(ListActivityTableMetaData.ACTIVITYTYPE, info.activitytype);
			record.put(ListActivityTableMetaData.ACTIVITYNAME, info.activityname);
			record.put(ListActivityTableMetaData.ACTIVITYSLOGAN, info.activityslogan);
			record.put(ListActivityTableMetaData.ACTIVITYSTART, info.activitystart);
			record.put(ListActivityTableMetaData.ACTIVITYEND, info.activityend);
			record.put(ListActivityTableMetaData.COMPANYNAME, info.company_name);
			record.put(ListActivityTableMetaData.AIMSTEP, info.aimstep);
			record.put(ListActivityTableMetaData.PERSONNUM, info.personnum);
			record.put(ListActivityTableMetaData.PERSONSEQ, info.personseq);
			record.put(ListActivityTableMetaData.GROUPNUM, info.groupnum);
			record.put(ListActivityTableMetaData.GROUPSEQ, info.groupseq);
			if (fromdatabase) {
				record.put("isfirstday", "0");
			} else {
				record.put("isfirstday", info.isfirstday);
			}
			mArrayActivityNow.add(record);
		}
		mChildrenData.add(mArrayActivityNow);

		for (int i = 0; i < activityInfo.activityold.size(); i++) {
			ListActivity info = activityInfo.activityold.get(i);
			HashMap<String, Object> record = new HashMap<String, Object>();
			record.put(ListActivityTableMetaData.ACTIVITYID, info.activityid);
			record.put(ListActivityTableMetaData.ACTIVITYTYPE, info.activitytype);
			record.put(ListActivityTableMetaData.ACTIVITYNAME, info.activityname);
			record.put(ListActivityTableMetaData.ACTIVITYSLOGAN, info.activityslogan);
			record.put(ListActivityTableMetaData.ACTIVITYSTART, info.activitystart);
			record.put(ListActivityTableMetaData.ACTIVITYEND, info.activityend);
			record.put(ListActivityTableMetaData.COMPANYNAME, info.company_name);
			record.put(ListActivityTableMetaData.AIMSTEP, info.aimstep);
			record.put(ListActivityTableMetaData.PERSONNUM, info.personnum);
			record.put(ListActivityTableMetaData.PERSONSEQ, info.personseq);
			record.put(ListActivityTableMetaData.GROUPNUM, info.groupnum);
			record.put(ListActivityTableMetaData.GROUPSEQ, info.groupseq);
			if (fromdatabase) {
				record.put("isfirstday", "0");
			} else {
				record.put("isfirstday", info.isfirstday);
			}
			mArrayActivityOld.add(record);
		}
		mChildrenData.add(mArrayActivityOld);

		for (int i = 0; i < activityInfo.activityfuture.size(); i++) {
			ListActivity info = activityInfo.activityfuture.get(i);
			HashMap<String, Object> record = new HashMap<String, Object>();
			record.put(ListActivityTableMetaData.ACTIVITYID, info.activityid);
			record.put(ListActivityTableMetaData.ACTIVITYTYPE, info.activitytype);
			record.put(ListActivityTableMetaData.ACTIVITYNAME, info.activityname);
			record.put(ListActivityTableMetaData.ACTIVITYSLOGAN, info.activityslogan);
			record.put(ListActivityTableMetaData.ACTIVITYSTART, info.activitystart);
			record.put(ListActivityTableMetaData.ACTIVITYEND, info.activityend);
			record.put(ListActivityTableMetaData.COMPANYNAME, info.company_name);
			record.put(ListActivityTableMetaData.AIMSTEP, info.aimstep);
			record.put(ListActivityTableMetaData.PERSONNUM, info.personnum);
			record.put(ListActivityTableMetaData.PERSONSEQ, info.personseq);
			record.put(ListActivityTableMetaData.GROUPNUM, info.groupnum);
			record.put(ListActivityTableMetaData.GROUPSEQ, info.groupseq);
			if (fromdatabase) {
				record.put("isfirstday", "0");
			} else {
				record.put("isfirstday", info.isfirstday);
			}
			mArrayActivityFuture.add(record);
		}
		mChildrenData.add(mArrayActivityFuture);

		mGroupCount.add(mActivitynownum);
		mGroupCount.add(mActivityOldNum);
		mGroupCount.add(mActivityFutureNum);

	}

	@SuppressWarnings("deprecation")
	private void initView() {
		mBack = findView(R.id.button_input_bg_back);
		mBack.setVisibility(View.VISIBLE);
		mBack.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.slidemenu_button));
		mBack.setOnClickListener(this);
		
		mArrayActivityNow = new ArrayList<HashMap<String, Object>>();
		mArrayActivityOld = new ArrayList<HashMap<String, Object>>();
		mArrayActivityFuture = new ArrayList<HashMap<String, Object>>();
		// title
		mTextViewTitle = findView(R.id.textView_title);
		mTextViewTitle.setText(R.string.racelist_title);

		mExpandableListview =  findView(R.id.expandablelistview_now);
		mExpandableListviewAdapter = new MyExpandableListview();
		mChildrenData = new ArrayList<ArrayList<HashMap<String, Object>>>();
		mExpandableListview.setAdapter(mExpandableListviewAdapter);
		mExpandableListview.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				if (intIsExpanded != 1) {
					intIsExpanded = 1;
				}
				return false;
			}
		});

		mExpandableListview.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				if (groupPosition != 2) {
					Intent intent = new Intent();
					HashMap<String, Object> has = mChildrenData.get(groupPosition).get(childPosition);
					String titlName = has.get(ListActivityTableMetaData.ACTIVITYNAME).toString();
					String actividId = has.get(ListActivityTableMetaData.ACTIVITYID).toString();
					String actividType = has.get(ListActivityTableMetaData.ACTIVITYTYPE).toString();
					Logger.d("testing", "actividType==" + actividType);
					String isfirstday = has.get("isfirstday").toString();
					intent.putExtra("ACTIVITYTITLE", titlName);
					intent.putExtra("ACTIVITYID", actividId);
					intent.putExtra("ACTIVITYTYPE", actividType);
					intent.putExtra("PHONENUM", mPhonenum);
					intent.putExtra("PASSWORD", mPassword);
					intent.putExtra("CLUBID", ClubId);
					ActivityDetailData medalInfo = MHealthProviderMetaData.GetMHealthProvider(mActivity).getActivityMyDetail(actividId, ClubId);
					if("1".equals(isfirstday)){
						BaseToast("活动第一天暂无数据排名");
						return false;
					}
					if (medalInfo.avgstep == null) {
						BaseToast("您未参加该活动,无法查看详情");
						return false;
					}
					intent.setClass(mActivity, CampaignContentActivity.class);
					startActivity(intent);
					mActivity.overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
					return false;
				} else {
					BaseToast("活动尚未开始，无法查看详情！");
					return true;
				}

			}
		});

		mExpandableListview.setOnGroupExpandListener(new OnGroupExpandListener() {
			@Override
			public void onGroupExpand(int groupPosition) {
				Logger.d(TAG, "展开Group: " + (groupPosition + 1));
			}
		});

		mExpandableListview.setOnGroupCollapseListener(new OnGroupCollapseListener() {
			@Override
			public void onGroupCollapse(int groupPosition) {
				Logger.d(TAG, "合拢Group: " + (groupPosition + 1));
			}
		});

		View childView = View.inflate(mActivity, R.layout.racelist_emptyview, null);
		childView.setVisibility(View.GONE);
		((ViewGroup) mExpandableListview.getParent()).addView(childView);
		mExpandableListview.setEmptyView(childView);
		// mExpandableListviewOver = (ExpandableListView)
		// findViewById(R.id.expandablelistview_over);
		// mExpandableListviewRightNow = (ExpandableListView)
		// findViewById(R.id.expandablelistview_rightnow);
	}

	class MyExpandableListview extends BaseExpandableListAdapter {

		public MyExpandableListview() {
			super();
			groupData = new ArrayList<String>(3);
			mGroupCount = new ArrayList<Integer>();
			groupData.add("正在进行");
			groupData.add("已经结束");
			groupData.add("即将开始");

		}

		@Override
		public int getGroupCount() {
			// 组条数
			return groupData.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// 子条目条数
			if (mChildrenData != null && mChildrenData.size() > 0)
				return mChildrenData.get(groupPosition).size();
			return 0;
		}

		@Override
		public Object getGroup(int groupPosition) {
			// 当前组指针
			return groupData.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			if (mChildrenData != null)
				return mChildrenData.get(groupPosition).get(childPosition);
			return 0;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		private ViewHolder mHolder;
		private ViewHolder mHolderChild;

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			mHolder = new ViewHolder();
			if (convertView != null) {
				mHolder = (ViewHolder) convertView.getTag();
			} else {
				convertView = LayoutInflater.from(mActivity).inflate(R.layout.racelist_grouplayout, null);
				mHolder.tvTitle = (TextView) convertView.findViewById(R.id.textview_racelist_grouptitle);
				mHolder.ivGroup = (ImageView) convertView.findViewById(R.id.imageview_racelist_group_now);
				mHolder.tvCount = (TextView) convertView.findViewById(R.id.textview_racelist_groupcount);
				convertView.setTag(mHolder);
			}

			if (isExpanded) {
				mHolder.ivGroup.setImageResource(R.drawable.imageview_racelist_open);
			} else {
				mHolder.ivGroup.setImageResource(R.drawable.imageview_racelist_close);
			}
			mHolder.tvTitle.setText(groupData.get(groupPosition).toString());

			if (mGroupCount.size() != 0) {
				mHolder.tvCount.setText(mGroupCount.get(groupPosition).toString());
			}

			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			mHolderChild = new ViewHolder();
			if (convertView != null) {
				mHolderChild = (ViewHolder) convertView.getTag();
			} else {
				convertView = LayoutInflater.from(mActivity).inflate(R.layout.racelist_expandablelist, null);
				mHolderChild.tvNowDate = (TextView) convertView.findViewById(R.id.textview_racelist_nowdate);
				mHolderChild.tvNowTitle = (TextView) convertView.findViewById(R.id.textview_racelist_nowTitle);
				mHolderChild.tvNowPerson = (TextView) convertView.findViewById(R.id.textview_racelist_nowperson);
				convertView.setTag(mHolderChild);
			}
			@SuppressWarnings("unchecked")
			HashMap<String, Object> activityDetail = (HashMap<String, Object>) getChild(groupPosition, childPosition);

			String start = activityDetail.get(ListActivityTableMetaData.ACTIVITYSTART).toString();
			String time = start.substring(4, start.length());
			String end = activityDetail.get(ListActivityTableMetaData.ACTIVITYEND).toString();
			String endtime = end.substring(4, start.length());
			mHolderChild.tvNowDate.setText(time + "-" + endtime);

			try {
				Date currentDate = df_yyyyMMdd.parse(start);
				String strStarDate = df_M_d.format(currentDate);

				currentDate = df_yyyyMMdd.parse(end);
				String strEndDate = df_M_d.format(currentDate);
				mHolderChild.tvNowDate.setText(strStarDate + "-" + strEndDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			mHolderChild.tvNowTitle.setText(activityDetail.get(ListActivityTableMetaData.ACTIVITYNAME).toString());
			// mHolder.tvNowTime.setText(time+"");
			mHolderChild.tvNowPerson.setText(activityDetail.get(ListActivityTableMetaData.PERSONNUM).toString() + "人参加");

			if (intIsExpanded == 0) {
				Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.activityinfo_in_tween);
				convertView.startAnimation(animation);
			}
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}

	}

	private static class ViewHolder {
		TextView tvTitle, tvCount, tvNowDate, tvNowTitle, tvNowPerson;
		ImageView ivGroup;
	}

	private SimpleDateFormat df_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat df_M_d = new SimpleDateFormat("M月d日");
	

	@Override
	public void findViews() {
		initView();
		loadData();
	}

	@Override
	public void clickListner() {
	}

	@Override
	public void loadLogic() {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_input_bg_back:
			showMenu();

		default:
			break;
		}
	}
}
