package cmcc.mhealth.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cmcc.mhealth.R;
import cmcc.mhealth.basic.BaseActivity;
import cmcc.mhealth.bean.GroupMemberInfo;
import cmcc.mhealth.bean.GroupMemberPkInfo;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Constants;
import cmcc.mhealth.common.ConstantsBitmaps;
import cmcc.mhealth.common.Encrypt;
import cmcc.mhealth.common.ImageUtil;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.db.GroupMemberInfoTableMetaData;
import cmcc.mhealth.db.MHealthProviderMetaData;
import cmcc.mhealth.db.OrgnizeInfoTableMetaData;
import cmcc.mhealth.net.DataSyn;
import cmcc.mhealth.slidingcontrol.MainCenterActivity;
import cmcc.mhealth.view.RoundAngleImageView;
import cmcc.mhealth.view.ScoreBarView;

@SuppressLint({ "SimpleDateFormat", "HandlerLeak" })
public class RankGroupDetailActivity extends BaseActivity {
	private static final String TAG = "RankSelectPKActivity";

	private String mGroupName;
	private TextView mTextViewTitleBar;
	private ListView mListViewGroupPK;
	private RadioGroup mImageButtonTitle;
	private TextView mTextView7Time;

	private ArrayList<HashMap<String, Object>> mArrayListRecords;

	private String mPhoneNum;
	private String mPassword;
	private String mGroupId;
	private int mCurrType;
	private String mUpdateTime;
	private String mUpdateTimeVer;
	private String strGroupUpdateVersion;
	private String strYYYYMMDDUpdate;
	
	private int ClubId;

	private MySimpleAdapter mAdapter;

	protected static final int UPDATE_SUCESS = 1;
	protected static final int UPDATE_FAILURE = 2;


	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE_SUCESS:
				setRankTitle(mCurrType);
				queryDataToRecords();
				mAdapter.notifyDataSetChanged();
				break;
			case UPDATE_FAILURE:
				BaseToast("更新失败！");
				setRankTitle(mCurrType);
				queryDataToRecords();
				mAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rank_select_pk);
		initView();
	}

  /** needUpdate: 
   * company: CMCC-CMRI
   * @author Gaofei
   * 2013-9-3 下午5:24:16 
   * version 1.0 
   * describe: 是否需要更新群组排名
   * @param strUpdateVersion
   * @return 
   */  
//	private GroupRankUpdateVersion mUpdateVersion;

	/**
	 * bigface相关
	 */
	private RelativeLayout mFaceRL;
	private LinearLayout mFaceLL;
	private RoundAngleImageView mFaceIV;
//  private boolean needUpdate(String strUpdateVersion) {
//    if(mUpdateVersion==null)
//    {
//      mUpdateVersion = new GroupRankUpdateVersion();
//    }
//    try {
//      int res = DataSyn.getInstance().getGroupRankUpdateVersion(mPhoneNum, mPassword, mUpdateVersion);
//      if (res == 0) {
//        if(mUpdateVersion != null && mUpdateVersion.result != null){
//
//          mCurrentTime = Common.getRankUpdateYYYYMMDD();
//          if(!strUpdateVersion.equals(mCurrentTime+"_"+mUpdateVersion.result)){
////            strUpdateVersion = mUpdateVersion.result;
//            return true;
//          }
//        }else{
//          return false;
//        }
//      } else{
//        
//      }
//    }catch (Exception e) {
//      // TODO: handle exception
//      Logger.e(TAG, e.getMessage());
//      return false;
//    }
//    return false;
//  }
	private void updateData() {

		// 根据updateTime 判断是否需要更新数据
		SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE);
		mUpdateTimeVer = info.getString("INPK_UPDATE_TIME_Z" + mGroupId, null);
		if(mUpdateTimeVer!=null && mUpdateTimeVer.length()>9){
		mUpdateTime = mUpdateTimeVer.substring(0, 8);
		}else{
			mUpdateTime = null;
		}

		strYYYYMMDDUpdate = Common.getRankUpdateYYYYMMDD_Tmp(mUpdateTime);
		strGroupUpdateVersion= info.getString("GROUP_UPDATE_VERSION", "");
		if((strGroupUpdateVersion.equals(mUpdateTimeVer))){
			strYYYYMMDDUpdate=null; // 不更新
		}else if (strYYYYMMDDUpdate==null){
			strYYYYMMDDUpdate = mUpdateTime;
		}
		
		if (null == strYYYYMMDDUpdate) {
			// 刷新不加载
			Message message = Message.obtain();
			message.what = UPDATE_SUCESS;
			handler.sendMessage(message);
		} else {
			// 先加载后刷新
			showProgressDialog("正在加载数据...", this);
			new Thread() {
				public void run() {
					Message message = Message.obtain();
					if (updateGroupMemeberInfo() == 0) {
						// Logger.i(TAG, "currentTime==" + mUpdateTime
						// + ",updateTime==" + mUpdateTime);
//						Editor sharedata = getSharedPreferences(SharedPreferredKey.SharedPrefenceName, Context.MODE_PRIVATE).edit();
//						sharedata.putString("INPK_UPDATE_TIME_Z" + mGroupId, strYYYYMMDDUpdate);
//						mUpdateTime = strYYYYMMDDUpdate;
//						sharedata.commit();
						

						Editor sharedata = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();
						sharedata.putString("INPK_UPDATE_TIME_Z" + mGroupId, strGroupUpdateVersion);
						sharedata.commit();
						
						
						message.what = UPDATE_SUCESS;
						handler.sendMessage(message);
					} else {
						message.what = UPDATE_FAILURE;
						handler.sendMessage(message);
					}

					dismiss();
				};
			}.start();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		Intent intent = getIntent();
		String groupName = intent.getStringExtra("groupName");
		String groupid = intent.getStringExtra("groupid");
		ClubId = intent.getIntExtra("clubid", 0);

		mCurrType = intent.getIntExtra("currType", -1);

		Logger.i(TAG, "groupName = " + groupName);
		Logger.i(TAG, "groupid = " + groupid);

		if (!TextUtils.isEmpty(groupName) && !TextUtils.isEmpty(groupid)) {
			mGroupName = groupName;
			mGroupId = groupid;
		} else {
			Logger.i(TAG, "groupName & mGroupId == null");
			return;
		}

		SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		mPhoneNum = info.getString(SharedPreferredKey.PHONENUM, null); // 拿到电话号码
		mPassword = info.getString(SharedPreferredKey.PASSWORD, null); // 拿到密码
		
		updateData();

		mTextViewTitleBar.setText(groupName);

		mAdapter = new MySimpleAdapter(this);
		mListViewGroupPK.setAdapter(mAdapter);
		setRankTitle(mCurrType);
		
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
		ConstantsBitmaps.initRunPics(RankGroupDetailActivity.this);
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		//bigface相关：
		mFaceLL = findView(R.id.activity_rank_ll_face);
		mFaceRL = findView(R.id.activity_rank_rl_bf);
		mFaceIV = findView(R.id.activity_rank_raiv_face);
		mFaceRL.setVisibility(View.INVISIBLE);
		mFaceRL.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mFaceRL.setVisibility(View.GONE);
			}
		});
		
		// 昨日 7日 排名切换按钮
		mArrayListRecords = new ArrayList<HashMap<String, Object>>();
		
		mImageButtonTitle = findView(R.id.radio_title_right);
		mImageButtonTitle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.radio_yestoday:
					mCurrType = Constants.GROUP_YESTERDAY;
					break;
				case R.id.radio_7day:
					mCurrType = Constants.GROUP_7DAY;
					break;
				default:
					mCurrType = Constants.GROUP_YESTERDAY;
					break;
				}

				setRankTitle(mCurrType);
				queryDataToRecords();
				if (mAdapter != null) {
					mAdapter.notifyDataSetChanged();
				}
			}

		});

		mTextViewTitleBar = findView(R.id.textView_title_run_spk);
		mTextView7Time = findView(R.id.textview_rankset_7time);

		ImageButton back = findView(R.id.button_input_bg_back);
//		back.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_button_back));
		mListViewGroupPK = findView(R.id.activity_rank_selectitempk);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("currType", mCurrType);

				RankGroupDetailActivity.this.setResult(RESULT_OK, intent);
				RankGroupDetailActivity.this.finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
			}
		});
	}

	private void setRankTitle(int timeType) {
		if (mUpdateTime == null) {
			SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
			mUpdateTime = info.getString("GROUP_UPDATE_TIME", null); // 更新时间
			
			// mUpdateTime = info.getString("INPK_UPDATE_TIME_Z" + mGroupId,
			// null); // 更新时间
		}

		if (mUpdateTime != null) {
			long updateTime = Common.getDateFromYYYYMMDD(mUpdateTime);
			String title_content = "";
			String before7day = "";
			String strDateTmp = "";

			long yesterdayTime = Common.getYesterday(updateTime);
			long before7updateTime = updateTime - 7 * (1000L * 60 * 60 * 24L);

			before7day = Common.getDateFromTime(before7updateTime, new SimpleDateFormat("M月d日"));
			strDateTmp = Common.getDateFromTime(yesterdayTime, new SimpleDateFormat("M月d日"));

			switch (timeType) {
			case Constants.GROUP_7DAY:
				if (mImageButtonTitle.getCheckedRadioButtonId() != R.id.radio_7day)
					((RadioButton) findViewById(R.id.radio_7day)).setChecked(true);
				mCurrType = Constants.GROUP_7DAY;
				// mImageButtonTitle
				// .setBackgroundResource(R.drawable.button_rank_yesterday);

				title_content = before7day + "---" + strDateTmp + "(平均步数)";
				break;
			case Constants.GROUP_YESTERDAY:
				mCurrType = Constants.GROUP_YESTERDAY;
				if (mImageButtonTitle.getCheckedRadioButtonId() != R.id.radio_yestoday)
					((RadioButton) findViewById(R.id.radio_yestoday)).setChecked(true);
				// mImageButtonTitle
				// .setBackgroundResource(R.drawable.button_rank_today);
				title_content = strDateTmp + "(单日步数)";//
				break;
			}
			mTextView7Time.setText(title_content);
		}
	}

	private void queryDataToRecords() {
		GroupMemberPkInfo memberPkInfo = MHealthProviderMetaData.GetMHealthProvider(this).getGroupInPkByIdInfo(mGroupId, mCurrType + "", ClubId);
		displayMemberPkInfo(memberPkInfo);
	}

	private void displayMemberPkInfo(GroupMemberPkInfo memberPkInfo) {
		mArrayListRecords.clear();
		for (int i = 0; i < memberPkInfo.groupmember.size(); i++) {
			GroupMemberInfo memberInfo = memberPkInfo.groupmember.get(i);

			HashMap<String, Object> record = new HashMap<String, Object>();
			record.put(GroupMemberInfoTableMetaData.MEMBER_NAME, memberInfo.membername);
			record.put(GroupMemberInfoTableMetaData.MEMBER_SCORE, memberInfo.memberscore);
			record.put(GroupMemberInfoTableMetaData.MEMBER_SEQ, memberInfo.memberseq);
			record.put(GroupMemberInfoTableMetaData.MEMBER7AVGDIST, memberInfo.member7avgdist);
			record.put(GroupMemberInfoTableMetaData.MEMBER7AVGSTEP, memberInfo.member7avgstep);
			record.put(GroupMemberInfoTableMetaData.RES1, memberInfo.memberinforev1);
			record.put(GroupMemberInfoTableMetaData.RES2, memberInfo.memberinforev2);
			record.put(GroupMemberInfoTableMetaData.RES3, memberInfo.avatar);

			mArrayListRecords.add(record);
		}
	}

	class MySimpleAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public MySimpleAdapter(Context c) {
			super();
			mInflater = LayoutInflater.from(c);
		}

		@Override
		public int getCount() {
			return mArrayListRecords.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		private ViewHolder mHolder;

		@SuppressWarnings("deprecation")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			mHolder = new ViewHolder();
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_item_rank, null);
				mHolder.mItemLayoutList = (LinearLayout) convertView.findViewById(R.id.linearLayout_list_item_rank);
				mHolder.mTextViewRankId = (TextView) convertView.findViewById(R.id.textview_rank_seq);
				mHolder.mImageViewRankIcon = (ImageView) convertView.findViewById(R.id.rank_icon_name);
				mHolder.mImageViewRankID = (ImageView) convertView.findViewById(R.id.imageview_rankid_bg);
				mHolder.mImageViewRankFirst = (ImageView) convertView.findViewById(R.id.imageview_rankidfirst);
				mHolder.mTextViewMemberName = (TextView) convertView.findViewById(R.id.textview_member_name);
//				mHolder.mTextViewGroupName = (TextView) convertView.findViewById(R.id.textview_group_name);
				mHolder.mScorebar = (ScoreBarView) convertView.findViewById(R.id.regularprogressbar);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}
			mHolder.mScorebar.setPics(ConstantsBitmaps.mLeftPic, ConstantsBitmaps.mRunPicYellow);
			
			if (mHolder.mItemLayoutList != null) {
				if ((position & 1) == 1)
					mHolder.mItemLayoutList.setBackgroundColor(Color.rgb(235, 235, 235));
				else
					mHolder.mItemLayoutList.setBackgroundColor(Color.WHITE);
			}
			HashMap<String, Object> record = mArrayListRecords.get(position);
			HashMap<String, Object> record0 = mArrayListRecords.get(0);
			String avater = record.get(GroupMemberInfoTableMetaData.RES1).toString();
			String groupName = record.get(OrgnizeInfoTableMetaData.MEMBER_NAME).toString();
			if (!TextUtils.isEmpty(avater) && avater.equals("1")) {
				int name2Int = Encrypt.getIntFromName(groupName == null ? "0" : groupName);
				mHolder.mImageViewRankIcon.setImageResource(MainCenterActivity.BASE_ATATAR[name2Int]);
			} else {
				int name2Int = Encrypt.getIntFromName(groupName == null ? "0" : groupName);
				mHolder.mImageViewRankIcon.setImageResource(MainCenterActivity.BASE_ATATAR[name2Int+7]);
			}

			// 自定义头像
			String mImageUrl="";
			if(GroupMemberInfoTableMetaData.RES3!=null){
				if(record.get(GroupMemberInfoTableMetaData.RES3)!=null){
			mImageUrl = record.get(GroupMemberInfoTableMetaData.RES3).toString();
				}
			}
			if (!(TextUtils.isEmpty(mImageUrl) || mImageUrl.equals(" "))) {
				String imgaeUrl =DataSyn.avatarHttpURL + mImageUrl + ".jpg";
				new ImageUtil().loadBitmap(mHolder.mImageViewRankIcon, imgaeUrl);
			}
			final View tempview = convertView;
			mHolder.mImageViewRankIcon.setOnClickListener(new OnClickListener() {
				int pos = position;
				@Override
				public void onClick(View arg0) {		
					// TODO 弹出大头像
					int oy = tempview.getTop();
					int ox = tempview.getLeft();
					Logger.i(TAG, "tempview.getTop() ---> " + oy);
					Logger.i(TAG, "tempview.getLeft() ---> " + ox);
					createPWforFace(pos, ox, oy);
				}

			});

			mHolder.mImageViewRankFirst.setVisibility(View.INVISIBLE);
			mHolder.mImageViewRankID.setBackgroundResource(R.drawable.rank_id_org);
			if (position < 3) {
				mHolder.mImageViewRankID.setBackgroundResource(R.drawable.rank_id_green);
				mHolder.mScorebar.setPics(ConstantsBitmaps.mLeftPic, ConstantsBitmaps.mRunPicGreen);
				if (position == 0) {
					mHolder.mImageViewRankFirst.setVisibility(View.VISIBLE);
				}
			}


			mHolder.mTextViewMemberName.setVisibility(View.VISIBLE);
//			mHolder.mTextViewGroupName.setVisibility(View.g);
			mHolder.mTextViewMemberName.setText(record.get(OrgnizeInfoTableMetaData.MEMBER_NAME).toString());
			mHolder.mTextViewRankId.setText(record.get(OrgnizeInfoTableMetaData.MEMBER_SEQ).toString());
			mHolder.mImageViewRankIcon.setBackgroundDrawable(null);
//			mHolder.mTextViewGroupName.setText(mGroupName);

      
			int progress = Integer.parseInt(record.get(GroupMemberInfoTableMetaData.MEMBER7AVGSTEP).toString());
			int progress0 = Integer.parseInt(record0.get(GroupMemberInfoTableMetaData.MEMBER7AVGSTEP).toString());
			if (progress < 100) {
				mHolder.mScorebar.setPics(ConstantsBitmaps.mLeftPic, ConstantsBitmaps.mRunPicBlue);
				mHolder.mImageViewRankID.setBackgroundResource(R.drawable.rank_id_dark);
			} else if (position > 3) {
				mHolder.mImageViewRankID.setBackgroundResource(R.drawable.rank_id_org);
				mHolder.mScorebar.setPics(ConstantsBitmaps.mLeftPic, ConstantsBitmaps.mRunPicYellow);
			}
			mHolder.mScorebar.setMaxValue(progress0);
			mHolder.mScorebar.setScore(progress);
			return convertView;
		}
	}

	private static class ViewHolder {
		TextView mTextViewRankId, mTextViewMemberName/*, mTextViewGroupName*/;
		ImageView mImageViewRankIcon, mImageViewRankFirst, mImageViewRankID;
		ScoreBarView mScorebar;
		LinearLayout mItemLayoutList;
	}

	/**
	 * 
	 * @return 0 更新成功 -1 更新失败
	 */
	private int updateGroupMemeberInfo() {
		GroupMemberPkInfo groupMemberPkInfo = new GroupMemberPkInfo();
		if (DataSyn.getInstance().getGroupMembersPkInfo7Day(mPhoneNum, mPassword, ClubId, mGroupId, 1, 100, groupMemberPkInfo) != 0) {
			Logger.i(TAG, "GroupMemberPkInfo 获取错误");
			return -1;
		}
		// 昨日
		GroupMemberPkInfo groupMemberPkInfoYesterday = new GroupMemberPkInfo();
		String yesterday = Common.getYesterdayAsYYYYMMDD(new Date().getTime());
		if (DataSyn.getInstance().getGroupMembersPkInfoYestoday(mPhoneNum, mPassword, ClubId, mGroupId, 1, 100, groupMemberPkInfoYesterday, yesterday, yesterday) != 0) {
			Logger.i(TAG, "GroupMemberPkInfoYesterday 获取错误");
			return -1;
		}

		MHealthProviderMetaData.GetMHealthProvider(this).deleteGroupInPkInfoByGroupId(mGroupId, ClubId);
		MHealthProviderMetaData.GetMHealthProvider(this).insertGroupInPkInfo(groupMemberPkInfo, mGroupId, mGroupName, Constants.GROUP_7DAY + "", ClubId);// 1
		MHealthProviderMetaData.GetMHealthProvider(this).insertGroupInPkInfo(groupMemberPkInfoYesterday, mGroupId, mGroupName, Constants.GROUP_YESTERDAY + "", ClubId);// 0
		return 0;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (mFaceRL.getVisibility() == View.VISIBLE) {
				mFaceRL.setVisibility(View.GONE);
			} else {
				Bundle bundle = new Bundle();
				bundle.putInt("currType", mCurrType);
				
				RankGroupDetailActivity.this.setResult(RESULT_CANCELED, RankGroupDetailActivity.this.getIntent().putExtras(bundle));
				RankGroupDetailActivity.this.finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
			}
			return true;
		}
		return false;
	}
	
	private void createPWforFace(int pos ,int offsetX,int offsetY){
		if(mArrayListRecords == null)return;
		HashMap<String, Object> record = mArrayListRecords.get(pos);
		if(record == null || record.size() == 0) return;
		Object icon =null;
		Object imageName =null;

		icon = record.get(GroupMemberInfoTableMetaData.RES1);
		imageName = record.get(GroupMemberInfoTableMetaData.RES3);

		if(icon==null || imageName==null)return;
		Object name = record.get(GroupMemberInfoTableMetaData.MEMBER_NAME);
		if (!TextUtils.isEmpty(icon.toString()) && icon.toString().equals("1") && !TextUtils.isEmpty(name.toString())) {
			int name2int = Encrypt.getIntFromName((String) name);
			mFaceIV.setImageDrawable(this.getResources().getDrawable(MainCenterActivity.BASE_ATATAR[name2int]));
		} else {
			int name2int = Encrypt.getIntFromName((String)name);
			mFaceIV.setImageDrawable(this.getResources().getDrawable(MainCenterActivity.BASE_ATATAR[name2int + 7]));
		}
		mFaceIV.setTag("pos" + 4);
		
		getImageAsync(mFaceIV,DataSyn.avatarHttpURL + imageName.toString() + ".jpg", "pos" + 4);
		getImageAsync(mFaceIV,DataSyn.avatarHttpURL + imageName.toString() + ".jpg", "pos" + 4, 1);

		float scaleSet = 5/27f;
		float rlxp = mFaceRL.getMeasuredWidth() / 2f;
		float rlyp = mFaceRL.getMeasuredHeight() / 2f;
	
		mFaceLL.clearAnimation();
		AnimationSet set = new AnimationSet(true);
		TranslateAnimation ta = new TranslateAnimation(
				offsetX - rlxp + Common.dip2px(RankGroupDetailActivity.this, 184), 0,
				offsetY - rlyp + Common.dip2px(RankGroupDetailActivity.this, 137), 0);
		ta.setDuration(400);
		ScaleAnimation sa = new ScaleAnimation(
				scaleSet, 1,
				scaleSet, 1);
		sa.setDuration(400);
		AlphaAnimation aa = new AlphaAnimation(0.5f, 1f);
		aa.setDuration(400);
		
		set.addAnimation(sa);
		set.addAnimation(ta);
		set.addAnimation(aa);
		set.setInterpolator(new AccelerateDecelerateInterpolator());
		set.setInterpolator(new OvershootInterpolator(1f));
		mFaceLL.startAnimation(set);
		
		mFaceRL.setVisibility(View.VISIBLE);
	}

	private Drawable getImageAsync(ImageView holder, String url, String tag) {
		return ImageUtil.getInstance().loadBitmap(holder, url, tag, 0);
	}
	private Drawable getImageAsync(ImageView holder, String url, String tag, int mode) {
		return ImageUtil.getInstance().loadBitmap(holder, url, tag, mode);
	}

}
