package cmcc.mhealth.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import cmcc.mhealth.R;
import cmcc.mhealth.basic.BaseActivity;
import cmcc.mhealth.bean.ActivityDetailData;
import cmcc.mhealth.bean.ActivityDetailMessageInfo;
import cmcc.mhealth.bean.MedalInfo;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Encrypt;
import cmcc.mhealth.common.ImageUtil;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.db.ActivityListDetailTableMetaData;
import cmcc.mhealth.db.ActivityMyDetailTableMetaData;
import cmcc.mhealth.db.MHealthProviderMetaData;
import cmcc.mhealth.net.DataSyn;
import cmcc.mhealth.slidingcontrol.MainCenterActivity;

public class CampaignContentActivity extends BaseActivity {

	protected static final String TAG = "ActivityDetailsActivity";

	private ImageButton mImageButtonBack;
	// private ImageButton mImageButtonRight;
	// private TextView mTextViewTitle;

	private String mActivityTitle;
	private TextView mTextViewTitleRun;
	/**
	 * 传递过来的活动ID
	 */
	private String mActivityId;
	private RadioGroup mRadioGroup;
	private HashMap<String, Object> mRrecord = new HashMap<String, Object>();
	private HashMap<String, Object> mActivityDetail;
	private List<HashMap<String, Object>> mArrRecord = new ArrayList<HashMap<String, Object>>();
	private ImageView mImageViewAvater;
	private TextView mTextViewName;
	private TextView mTextViewGroup;
	private TextView mTextViewAvgStep;
	private TextView mTextViewDb;
	private TextView mTextViewDays;

	private TextView mTextViewlist1RankId;
	private TextView mTextViewlist1StepNum;
	private TextView mTextViewlist1JiBai;

	private RadioButton mRadioButtonPersonal;
	private TextView mTextViewTextStep;
	private TextView mTextViewRuWei1;
	private TextView mTextViewtextRankName;
	private TextView mTextViewtexttitle;
	private TextView mTextViewKickoffUnit1;
	private LinearLayout mIvInfo1;
	private PopupWindow popupWindow;
	private TextView mTextViewListTop;
	private ListView mListView;
	private MySimpleAdapter mSimpleAdapter;

	private ActivityDetailMessageInfo admReqData;

	protected int mFlag = 0;

	protected int mClubId = 0;

	private View mRaceDetails;

	private String mActivityType;

	private String mPhoneNum;
	private String mPassword;

	static final String[] tempStr = new String[] { "健步之星奖", "健走满分奖", "最佳团队奖", "个人奖", "团队奖" };
	static final String[] tempStrDetail = new String[] { "活动期间日平均步数排名前三十名", "活动期间累计完成四十万步的目标", "活动期间班组平均步数排名前十名", "活动期间达标率排名前三十名,达标率相同的按平均步数排序", "活动期间团队平均步数排名前三名" };

	// static final String tempStrDetail = "活动期间日平均步数排名前三十名";
	// static final String tempStr2 = "健走满分奖";
	// static final String tempStr2_1 = "活动期间累计完成四十万步的目标";
	// static final String tempStr3 = "最佳团队奖";
	// static final String tempStr3_1 = "活动期间班组平均步数排名前十名";
	//
	//
	// static final String temp2Str1 = "个人奖";
	// static final String temp2Str1_1 = "活动期间达标率排名前三十名,达标率相同的按平均步数排序";
	// static final String temp2Str3 = "团队奖";
	// static final String temp2Str3_1 = "活动期间团队平均步数排名前十名";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		initView();
		loadlogic();

	}

	private Handler handler = new Handler(){
		public void dispatchMessage(Message msg) {
			mRaceDetails.setEnabled(true);
		};
	};
	
	
	@Override
	protected void onResume() {
		super.onResume();
		mRadioButtonPersonal.setChecked(true);
		// loadlogic();
	}

	private void loadlogic() {
		ActivityDetailData medalInfo = MHealthProviderMetaData.GetMHealthProvider(this).getActivityMyDetail(mActivityId, mClubId);
		addMyActivityInfo(medalInfo);
		getActivityDetail();
		myRaceCentent();
		displayRankInfo();

	}

	private void getActivityDetail() {
		new Thread() {
			public void run() {
				admReqData = new ActivityDetailMessageInfo();
				DataSyn.getInstance().getActivityDetailMessage(mPhoneNum, mPassword, mActivityId, admReqData);
				handler.sendEmptyMessage(0);
			};
		}.start();
	}

	private void displayRankInfo() {
		// 直接拿网络返回的数据填充
		ArrayList<MedalInfo> arrMedalInfo = getMediaInfo();
		mArrRecord.clear();
		for (int i = 0; i < arrMedalInfo.size(); i++) {
			if (arrMedalInfo.get(i).medaltype.equals("0")) {
				addActivityDetails(arrMedalInfo.get(i));
			}
		}

		if (mSimpleAdapter == null) {
			mSimpleAdapter = new MySimpleAdapter(mArrRecord);
		}
		mListView.setAdapter(mSimpleAdapter);
		// 把数据填充到界面
		// personalRank();
	}

	private ArrayList<MedalInfo> getMediaInfo() {
		return MHealthProviderMetaData.GetMHealthProvider(this).getActivityListDetail(mActivityId, mClubId);
	}

	private void myRaceCentent() {
		// mTextViewDays.setVisibility(View.VISIBLE);
		// mTextViewtexttitle.setVisibility(View.VISIBLE);
		String textViewName = mRrecord.get(ActivityMyDetailTableMetaData.MYNAME).toString();
		mTextViewName.setText(textViewName == null ? "" : textViewName);

		String textviewAvgStep = mRrecord.get(ActivityMyDetailTableMetaData.AVGSTEP).toString();
		mTextViewAvgStep.setText(textviewAvgStep == null ? "" : textviewAvgStep);

		String textviewDb = mRrecord.get(ActivityMyDetailTableMetaData.RATESCORE) + "%";
		mTextViewDb.setText(textviewDb == null ? "" : textviewDb);

		String textviewGroup = mRrecord.get(ActivityMyDetailTableMetaData.MYGROUP).toString();
		mTextViewGroup.setText(textviewGroup == null ? "" : textviewGroup);

		String textviewDays = mRrecord.get(ActivityMyDetailTableMetaData.HITDURATION).toString();
		mTextViewDays.setText(textviewDays == null ? "" : textviewDays);

		SharedPreferences sp = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		String sex = sp.getString(SharedPreferredKey.GENDER, "");
		String name = sp.getString(SharedPreferredKey.NAME, null);
		if (!TextUtils.isEmpty(sex) && sex.equals("1")) {
			int name2Int = Encrypt.getIntFromName(name == null ? "0" : name);
			mImageViewAvater.setImageResource(MainCenterActivity.BASE_ATATAR[name2Int]);
		} else {
			int name2Int = Encrypt.getIntFromName(name == null ? "0" : name);
			mImageViewAvater.setImageResource(MainCenterActivity.BASE_ATATAR[name2Int + 7]);
		}
		String avatar = sp.getString(SharedPreferredKey.AVATAR, null);
		if (avatar != null) {
			ImageUtil.getInstance().loadBitmap(mImageViewAvater, avatar);
		}
	}

	class MySimpleAdapter extends BaseAdapter {
		List<HashMap<String, Object>> list;
		private ImageView mImageViewItemRank;

		public MySimpleAdapter(List<HashMap<String, Object>> arr) {
			this.list = arr;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null || convertView.getTag() == null) {
				convertView = View.inflate(CampaignContentActivity.this, R.layout.racecentent_item, null);
				mTextViewlist1RankId = (TextView) convertView.findViewById(R.id.textview_activitydetails_list1_rankid);
				mTextViewlist1StepNum = (TextView) convertView.findViewById(R.id.textview_activitydetails_list1_stepnum);
				mTextViewlist1JiBai = (TextView) convertView.findViewById(R.id.textview_activitydetails_list1_jibai);
				mTextViewRuWei1 = (TextView) convertView.findViewById(R.id.textview_activity_ruwei1);
				mTextViewListTop = (TextView) convertView.findViewById(R.id.race_listtop_text);

				mTextViewtextRankName = (TextView) convertView.findViewById(R.id.textview_activitydetails_textrankname);
				mTextViewKickoffUnit1 = (TextView) convertView.findViewById(R.id.textview_activitydetails_kickoffunit1);
				mTextViewTextStep = (TextView) convertView.findViewById(R.id.TextView_activity_textstep);
				mTextViewRuWei1 = (TextView) convertView.findViewById(R.id.textview_activity_ruwei1);
				mImageViewItemRank = (ImageView) convertView.findViewById(R.id.imageview_raceitem);
				mIvInfo1 = (LinearLayout) convertView.findViewById(R.id.ad_ll_infos1);
			}
			if (mFlag == 0) {
				mTextViewtextRankName.setText("您目前是第");
				mTextViewKickoffUnit1.setText("的人");
			} else {
				mTextViewtextRankName.setText("目前是第");
				mTextViewKickoffUnit1.setText("的组");
			}

			mIvInfo1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String medalName = list.get(position).get(ActivityListDetailTableMetaData.MEDAL_NAME).toString();
					String medalDetail = list.get(position).get(ActivityListDetailTableMetaData.MEDAL_DETAIL).toString();

					for (int i = 0; i < tempStr.length; i++) {
						if (medalName.contains(tempStr[i])) {
							medalDetail = tempStrDetail[i];
							break;
						}
					}
					creatPopWindow(medalName, medalDetail);
				}
			});

			if (position == 1) {
				mImageViewItemRank.setImageDrawable(getResources().getDrawable(R.drawable.imageview_activitydetails_second));
			} else if (position == 2) {
				mImageViewItemRank.setImageDrawable(getResources().getDrawable(R.drawable.imageview_activitydetails_third));
			}

			String s1 = list.get(position).get(ActivityListDetailTableMetaData.MEDAL_GAP).toString();
			if (!s1.equals(" ")) {
				if (s1.equals("0")) {
					mTextViewlist1StepNum.setVisibility(View.GONE);
					mTextViewTextStep.setVisibility(View.GONE);
					mTextViewRuWei1.setVisibility(View.VISIBLE);
				} else {
					mTextViewlist1StepNum.setVisibility(View.VISIBLE);
					mTextViewlist1StepNum.setText(s1);
					mTextViewTextStep.setVisibility(View.VISIBLE);
					mTextViewRuWei1.setVisibility(View.GONE);
				}
			}

			mTextViewListTop.setText(list.get(position).get(ActivityListDetailTableMetaData.MEDAL_NAME).toString());
			mTextViewlist1RankId.setText(list.get(position).get(ActivityListDetailTableMetaData.RANK).toString());
			mTextViewlist1JiBai.setText(list.get(position).get(ActivityListDetailTableMetaData.BEATPERCENT) + "%");

			return convertView;
		}

	}

	private void addMyActivityInfo(ActivityDetailData mActivityMedalInfo2) {

		mRrecord = new HashMap<String, Object>();
		mRrecord.put(ActivityMyDetailTableMetaData.MYNAME, mActivityMedalInfo2.myname);
		mRrecord.put(ActivityMyDetailTableMetaData.MYGROUP, mActivityMedalInfo2.mygroup);
		mRrecord.put(ActivityMyDetailTableMetaData.AVGSTEP, mActivityMedalInfo2.avgstep);
		mRrecord.put(ActivityMyDetailTableMetaData.RATESCORE, mActivityMedalInfo2.ratescore);
		mRrecord.put(ActivityMyDetailTableMetaData.HITDURATION, mActivityMedalInfo2.hitduration);
		mRrecord.put(ActivityMyDetailTableMetaData.GROUPAVGSTEP, mActivityMedalInfo2.groupavgstep);
		mRrecord.put(ActivityMyDetailTableMetaData.GROUP_RATESCORE, mActivityMedalInfo2.groupratescore);

		RadioButton rbgroup = findView(R.id.radiobutton_group);
		if (mActivityMedalInfo2.mygroup == null || "".equals(mActivityMedalInfo2.mygroup) || "null".equals(mActivityMedalInfo2.mygroup)) {
			rbgroup.setVisibility(View.GONE);
		}
	}

	private void addActivityDetails(MedalInfo medalInfo) {
		// mArrRecord = new ArrayList<HashMap<String, Object>>();
		mActivityDetail = new HashMap<String, Object>();
		mActivityDetail.put(ActivityListDetailTableMetaData.MEDAL_NAME, medalInfo.medalname == null ? "" : medalInfo.medalname);
		mActivityDetail.put(ActivityListDetailTableMetaData.MEDAL_TYPE, medalInfo.medaltype == null ? "" : medalInfo.medaltype);
		mActivityDetail.put(ActivityListDetailTableMetaData.RANK, medalInfo.rank == null ? "" : medalInfo.rank);
		mActivityDetail.put(ActivityListDetailTableMetaData.MEDAL_SUM, medalInfo.medalsnum == null ? "" : medalInfo.medalsnum);
		mActivityDetail.put(ActivityListDetailTableMetaData.MEDAL_DETAIL, medalInfo.medaldetail == null ? "" : medalInfo.medaldetail);
		mActivityDetail.put(ActivityListDetailTableMetaData.MEDAL_GAP, medalInfo.medalgap == null ? "" : medalInfo.medalgap);
		mActivityDetail.put(ActivityListDetailTableMetaData.BEATPERCENT, medalInfo.beatpercent == null ? "" : medalInfo.beatpercent);
		mActivityDetail.put(ActivityListDetailTableMetaData.SCORE, medalInfo.score == null ? "" : medalInfo.score);
		mArrRecord.add(mActivityDetail);
	}

	@SuppressWarnings("deprecation")
	private void initView() {

		Intent intent = getIntent();
		mActivityTitle = intent.getStringExtra("ACTIVITYTITLE");
		mActivityId = intent.getStringExtra("ACTIVITYID");
		mActivityType = intent.getStringExtra("ACTIVITYTYPE");
		mPhoneNum = intent.getStringExtra("PHONENUM");
		mPassword = intent.getStringExtra("PASSWORD");
		mClubId = intent.getIntExtra("CLUBID", 0);
		if (mActivityType != null && !"-1".equals(mActivityType)) {
			SharedPreferences sp1 = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
			Editor edit = sp1.edit();
			edit.putString("mActivityType", mActivityType);
			edit.commit();
		}
		if (mActivityTitle == null && mActivityId == null) {
			mActivityTitle = "";
			mActivityId = "";
			return;
		}

		// 设定点击==========设定奖项名称请在上面
		mIvInfo1 = findView(R.id.ad_ll_infos1);
		mListView = findView(R.id.listview_racecentent);

		// mTextViewTitle = findView(R.id.textView_title);
		// mTextViewTitle.setVisibility(View.GONE);

		mTextViewTitleRun = ((TextView) findView(R.id.textView_title));
		mTextViewTitleRun.setText(mActivityTitle);

		mImageButtonBack = (ImageButton) findView(R.id.button_input_bg_back);
		mImageButtonBack.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_button_back));
		mImageButtonBack.setVisibility(View.VISIBLE);
		mImageButtonBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CampaignContentActivity.this.finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
			}
		});
		// mImageButtonRight = (ImageButton)
		// findView(R.id.imagebutton_title_right);
		// mImageButtonRight.setVisibility(View.INVISIBLE);
		// mImageButtonRight.setImageResource(R.drawable.refresh_button_bg);

		mImageViewAvater = findView(R.id.iamgeview_activitydetails_avater);
		mTextViewName = findView(R.id.textview_activitydetails_name);
		mTextViewGroup = findView(R.id.textview_activitydetails_group);
		mTextViewAvgStep = findView(R.id.textview_activitydetails_avgstep);
		mTextViewDb = findView(R.id.textview_activitydetails_db);
		mTextViewDays = findView(R.id.textview_activitydetails_days);
		mTextViewtexttitle = findView(R.id.textview_activitydetails_days_title);

		mRadioButtonPersonal = findView(R.id.radiobutton_personal);

		mRadioGroup = findView(R.id.radiogroup_activitydetails);
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				ArrayList<MedalInfo> arrMedalInfo = getMediaInfo();
				mArrRecord.clear();
				switch (checkedId) {
				case R.id.radiobutton_personal:
					mFlag = 0;
					mTextViewDays.setVisibility(View.VISIBLE);
					mTextViewtexttitle.setVisibility(View.VISIBLE);
					mTextViewName.setVisibility(View.VISIBLE);
					mTextViewName.setText(mRrecord.get(ActivityMyDetailTableMetaData.MYNAME).toString());
					mTextViewAvgStep.setText(mRrecord.get(ActivityMyDetailTableMetaData.AVGSTEP).toString());
					mTextViewDb.setText(mRrecord.get(ActivityMyDetailTableMetaData.RATESCORE).toString() + "%");

					for (int i = 0; i < arrMedalInfo.size(); i++) {
						if (arrMedalInfo.get(i).medaltype.equals("0")) {
							addActivityDetails(arrMedalInfo.get(i));
						}
					}
					break;
				case R.id.radiobutton_group:
					mFlag = 1;
					mTextViewDays.setVisibility(View.INVISIBLE);
					mTextViewtexttitle.setVisibility(View.INVISIBLE);
					mTextViewName.setVisibility(View.INVISIBLE);
					mTextViewAvgStep.setText(mRrecord.get(ActivityMyDetailTableMetaData.GROUPAVGSTEP).toString());
					mTextViewDb.setText(mRrecord.get(ActivityMyDetailTableMetaData.GROUP_RATESCORE).toString() + "%");

					for (int i = 0; i < arrMedalInfo.size(); i++) {
						if (arrMedalInfo.get(i).medaltype.equals("1")) {
							addActivityDetails(arrMedalInfo.get(i));
						}
					}
					break;
				}
				mSimpleAdapter.notifyDataSetChanged();
			}
		});

		mRaceDetails = findView(R.id.button_racedetaile);
		mRaceDetails.setEnabled(false);
		mRaceDetails.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Dialog dialog = new Dialog(CampaignContentActivity.this);
				
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				View view = View.inflate(CampaignContentActivity.this, R.layout.racedetail_text, null);

				if (admReqData == null || admReqData.description.length() == 0) {
					BaseToast("抱歉，此活动暂无相关说明~");
					return;
				}
				((TextView) view.findViewById(R.id.racedetails_text)).setText(admReqData.description);
				dialog.setContentView(view);
				dialog.show();
			}
		});

	}

	private void creatPopWindow(String title, String content) {
		View view = View.inflate(this, R.layout.popup_1, null);
		TextView tvtitle = (TextView) view.findViewById(R.id.pop_tv_title);
		TextView tvcontent = (TextView) view.findViewById(R.id.pop_tv_content);
		tvtitle.setText(title);
		tvcontent.setText(content);

		popupWindow = new PopupWindow(view, Common.dip2px(this, 200), Common.dip2px(this, 150));
		popupWindow.setFocusable(false);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setAnimationStyle(R.style.PopupAnimation2);
		popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_myrankid));
		popupWindow.showAtLocation(findView(R.id.at_details_lin), Gravity.CENTER, 0, 0);
	}

	@Override
	protected void onStop() {
		if (popupWindow != null) {
			popupWindow.dismiss();
		}
		super.onStop();
	}
}
