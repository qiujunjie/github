package cmcc.mhealth.slidingcontrol;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cmcc.mhealth.R;
import cmcc.mhealth.activity.RankGroupDetailActivity;
import cmcc.mhealth.bean.GroupInfo;
import cmcc.mhealth.bean.GroupMemberInfo;
import cmcc.mhealth.bean.GroupMemberPkInfo;
import cmcc.mhealth.bean.GroupPkInfo;
import cmcc.mhealth.bean.OrgnizeMemberInfo;
import cmcc.mhealth.bean.OrgnizeMemberPKInfo;
import cmcc.mhealth.bean.OrgnizeMemberSum;
import cmcc.mhealth.bean.RankUserBean;
import cmcc.mhealth.bean.RankingDate;
import cmcc.mhealth.bean.SearchDate;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Constants;
import cmcc.mhealth.common.ConstantsBitmaps;
import cmcc.mhealth.common.Encrypt;
import cmcc.mhealth.common.ImageUtil;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.db.GroupMemberInfoTableMetaData;
import cmcc.mhealth.db.GroupPkInfoTableMetaData;
import cmcc.mhealth.db.MHealthProviderMetaData;
import cmcc.mhealth.db.OrgnizeInfoTableMetaData;
import cmcc.mhealth.net.DataSyn;
import cmcc.mhealth.view.ListViewDropDownRefresh;
import cmcc.mhealth.view.ListViewDropDownRefresh.OnRefreshListener;
import cmcc.mhealth.view.RoundAngleImageView;
import cmcc.mhealth.view.ScoreBarView;

import com.google.analytics.tracking.android.EasyTracker;

public class RankChildFragment extends Fragment implements OnScrollListener {
	private static final String TAG = "ListViewFragment";

	private static Fragment mRankFragment;

	private ArrayList<HashMap<String, Object>> mArrayListRecords = new ArrayList<HashMap<String, Object>>();
	public MySimpleAdapter mAdapter;

	private int mFlag;
	public int mCurrType = RankFragment.mCurrType;

	private String mGroupName = "";

	// **private int mVisibleLastIndex = 0; // 最后的可视项索引

	private ListViewDropDownRefresh mListview;
	private RelativeLayout mFaceRL;
	private RoundAngleImageView mFaceIV;
	private LinearLayout mFaceLL;

	private View mFootview;
	private View mHeadView;
	private static OnArticleSelectedListener mArticleSelectedListener;

	private int mFirstItem = 1;
	private int mAdd = 100;
	private int mLastItem;

	private OrgnizeMemberSum mOrgnizeMemSum;
	private int mOrgnizeMemSumNum = 0;
	private boolean mLoadOver = true;

	private TextView mTextViewMyGroup;
	private TextView mTextViewMyStep;
	private TextView mTextViewRanking;
	private RankingDate mRankingDate;

	private String mMembername;

	// private String mSex;
	private String mAvaterName;
	// private String mMyAvatar;
	private Activity mActivity;

	// 搜索相关数据
	private SearchDate mReqData = null;

	private int mMaxValue;

	private mBaseAdapter mmBaseApater;

	// ===
	private OnDirectorStateChanged listener;

	private static int mClubId = 0;

	public ListViewDropDownRefresh getmListview() {
		return mListview;
	}

	/**
	 * 从数据库获取人数的总数
	 */
	private int mGetSumCount;
	/**
	 * 排名向前移动10
	 */
	// **private static final int MOVE_TO = 20;
	/**
	 * 图片储存本地名称
	 */
	private String mImageName;

	private Dialog customDialog;

	public void setmRankFragment(Fragment mRankFragment) {
		RankChildFragment.mRankFragment = mRankFragment;
	}

	public static RankChildFragment newInstance(int i, Fragment fragment, int ClubId) {
		mClubId = ClubId;
		RankChildFragment newFragment = new RankChildFragment();
		try {
			mArticleSelectedListener = (OnArticleSelectedListener) fragment;
		} catch (Exception e) {
			throw new ClassCastException(fragment.toString() + "must implement OnArticleSelectedListener");
		}
		mRankFragment = fragment;
		Bundle bundle = new Bundle();
		bundle.putInt("current", i);
		newFragment.setArguments(bundle);
		return newFragment;
	}

	public interface OnArticleSelectedListener {
		public void onArticleSelected(Object obj);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
		// try {
		// mArticleSelectedListener = (OnArticleSelectedListener) activity;
		// } catch (Exception e) {
		// throw new ClassCastException(activity.toString() +
		// "must implement OnArticleSelectedListener");
		// }
	}

	private void myRanking7days() {
		if (mRankingDate != null) {
			String str;
			if (mFlag == RankFragment.ALL_MEMBER_PK) {
				mTextViewMyGroup.setText(mMembername == null ? "" : mMembername);
				str = mRankingDate.member7info.step;
				mTextViewMyStep.setText(str == null ? "" : (str + "步"));
				str = mRankingDate.member7seq;
				mTextViewRanking.setText(str == null ? "" : ("第" + str + "位"));
			} else if (mFlag == RankFragment.GROUP_PK) {
				mImageViewAvater.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.avatar_group_middle));
				mTextViewMyGroup.setText(mGroupName);
				str = mRankingDate.group7info.step;
				mTextViewMyStep.setText(str == null ? "" : (str + "步"));
				str = mRankingDate.group7seq;
				mTextViewRanking.setText(str == null ? "" : ("第" + str + "位"));
			} else if (mFlag == RankFragment.GROUP_MEMBER_PK) {
				mTextViewMyGroup.setText(mMembername == null ? "" : mMembername);
				str = mRankingDate.member7info.step;
				mTextViewMyStep.setText(str == null ? "" : (str + "步"));
				str = mRankingDate.groupmember7seq;
				mTextViewRanking.setText(str == null ? "" : ("第" + str + "位"));
			}
		} else {
			Logger.e(TAG, "mRankingDate == null");
		}
	}

	private void myRanking1days() {
		if (mRankingDate != null) {
			String str;
			if (mFlag == RankFragment.ALL_MEMBER_PK) {
				Logger.d(TAG, "mTextViewMyGroup is null? --> " + (mTextViewMyGroup == null));
				Logger.d(TAG, "mMembername is null? --> " + (mMembername == null));
				if (mMembername != null)
					mTextViewMyGroup.setText(mMembername);
				str = mRankingDate.member1info.step;
				if (str != null)
					mTextViewMyStep.setText(str + "步");
				str = mRankingDate.member1seq;
				if (str != null)
					mTextViewRanking.setText("第" + str + "位");
			} else if (mFlag == RankFragment.GROUP_PK) {
				// if (mFootview != null) {
				// mListview.removeFooterView(mFootview);
				// }
				mImageViewAvater.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.avatar_group_middle));
				mListview.setOnItemClickListener(new MyOnItemClickListener());
				if (mGroupName != null)
					mTextViewMyGroup.setText(mGroupName);
				str = mRankingDate.group1info.step;
				if (str != null)
					mTextViewMyStep.setText(str + "步");
				str = mRankingDate.group1seq;
				if (str != null)
					mTextViewRanking.setText("第" + str + "位");
			} else if (mFlag == RankFragment.GROUP_MEMBER_PK) {
				if (mMembername != null)
					mTextViewMyGroup.setText(mMembername);
				str = mRankingDate.member1info.step;
				if (str != null)
					mTextViewMyStep.setText(str + "步");
				str = mRankingDate.groupmember1seq;
				if (str != null)
					mTextViewRanking.setText("第" + str + "位");
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mAdapter.notifyDataSetChanged();
		initMySeq();
		// Logger.e(TAG, "onResume ------ "+getArguments().getInt("current"));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Logger.e(TAG, "onCreate ------ "+getArguments().getInt("current"));
		Bundle args = getArguments();
		mFlag = (Integer) (args != null ? args.getInt("current") : RankFragment.mCurrIndex);

	}

	private void getSharedInfo() {
		if (mActivity == null) {
			Logger.e(TAG, "mActivity is null");
			return;
		}
		if (mRankingDate == null) {
			mRankingDate = new RankingDate();
		}
		mSharedInfo = mActivity.getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		mRankingDate.group1seq = mSharedInfo.getString("group1seq", "");
		mRankingDate.group7seq = mSharedInfo.getString("group7seq", "");
		mRankingDate.groupmember1seq = mSharedInfo.getString("groupmember1seq", "");
		mRankingDate.groupmember7seq = mSharedInfo.getString("groupmember7seq", "");
		mRankingDate.member1seq = mSharedInfo.getString("member1seq", "");
		mRankingDate.member7seq = mSharedInfo.getString("member7seq", "");
		mAvaterName = mSharedInfo.getString(SharedPreferredKey.AVATAR, "");

		mRankingDate.group1info.step = mSharedInfo.getString("group1Info_step", "");
		mRankingDate.group7info.step = mSharedInfo.getString("group7Info_step", "");
		mGroupName = mSharedInfo.getString(SharedPreferredKey.GROUP_NAME, ""); // 拿到组名
		mRankingDate.member1info.step = mSharedInfo.getString("memberi1Info_step", "");
		mRankingDate.member7info.step = mSharedInfo.getString("memberi7Info_step", "");
		// mSex = mSharedInfo.getString(SharedPreferredKey.gender, "0");
		mMembername = mSharedInfo.getString(SharedPreferredKey.NAME, "");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Logger.e(TAG, getClass().getSimpleName() + "onCreateView");
		View view = inflater.inflate(R.layout.listview_rank, container, false);
		Logger.d(TAG, "onCreateView is run");

		Logger.d("testing", "onCreateView:" + this.toString());
		// 大图
		mFaceIV = (RoundAngleImageView) view.findViewById(R.id.listview_iv_face);
		mFaceLL = (LinearLayout) view.findViewById(R.id.listview_ll_face);
		mFaceRL = (RelativeLayout) view.findViewById(R.id.listview_rl_face);
		mFaceRL.setVisibility(View.INVISIBLE);
		mFaceRL.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mFaceRL.setVisibility(View.GONE);
			}
		});
		Logger.d(TAG, "oncreatview ---- 1  <>  " + this.toString());
		mListview = (ListViewDropDownRefresh) view.findViewById(R.id.listview_rank);
		Logger.d(TAG, "mListview ---- 1.5" + mListview.toString() + " <>  " + this.toString());

		mHeadView = View.inflate(mActivity, R.layout.mygroup_ranking, null);
		mTextViewMyGroup = (TextView) mHeadView.findViewById(R.id.textview_myrank_myname);
		mTextViewMyStep = (TextView) mHeadView.findViewById(R.id.textview_myStep);
		mTextViewRanking = (TextView) mHeadView.findViewById(R.id.textview_myrank_id);
		mImageViewAvater = (RoundAngleImageView) mHeadView.findViewById(R.id.iamgeview_myrank_avater);
		mListview.addHeaderView(mHeadView);

		mAdapter = new MySimpleAdapter();
		if (mRankingDate == null) {
			mRankingDate = new RankingDate();
		}

		mListview.setAdapter(mAdapter);
		View childView = View.inflate(mActivity, R.layout.racelist_emptyview, null);
		// childView.setVisibility(View.GONE);
		((ViewGroup) mListview.getParent()).addView(childView);
		mListview.setEmptyView(childView);
		mListview.setOnScrollListener(this);
		mListview.setOnItemClickListener(new MyOnItemClickListener());
		// mListview.setonRefreshListener(new MyRefresh());
		EasyTracker.getInstance().setContext(mActivity);
		switch (mFlag) {
		case RankFragment.GROUP_MEMBER_PK:
			EasyTracker.getTracker().sendView("Group_member_pk");
			break;
		case RankFragment.GROUP_PK:
			EasyTracker.getTracker().sendView("Group_pk");
			// if (mFootview != null) {
			// mListview.removeFooterView(mFootview);
			// }
			break;
		case RankFragment.ALL_MEMBER_PK:
			EasyTracker.getTracker().sendView("All_member_pk");

			if (mFootview == null) {
				mFootview = View.inflate(mActivity, R.layout.rank_listviewfoot, null);
			}
			if (mLoadOver) {
				mListview.addFooterView(mFootview);
			}
			break;
		}
		initLogic();
		return view;
	}

	public void initLogic() {
		mFlag = getArguments().getInt("current");
		getSharedInfo();
		initMySeq();
		displayRankInfo(mFlag);
		mAdapter.notifyDataSetChanged();
	}

	private void initMySeq() {
		String name = mSharedInfo.getString(SharedPreferredKey.NAME, null);
		if (!TextUtils.isEmpty(name)) {
			mImageViewAvater.setImageDrawable(mActivity.getResources().getDrawable((mSharedInfo.getString(SharedPreferredKey.GENDER, "0").equals("0") ? MainCenterActivity.BASE_ATATAR[Encrypt.getIntFromName(name) + 7] : MainCenterActivity.BASE_ATATAR[Encrypt.getIntFromName(name)])));

			String url = DataSyn.avatarHttpURL + mAvaterName + ".jpg";
			getImageAsync(mImageViewAvater, url);
		}
		if (RankFragment.mCurrType == Constants.GROUP_7DAY) {
			myRanking7days();
		} else {
			myRanking1days();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		View view = getView();
		Logger.d(TAG, view == null ? "view is null" : view.toString());
	}

	/**
	 * selectActivity(跳转到选择组页面)
	 */
	public void selectActivity(String groupid, String groupName) {
		Intent intent = new Intent();
		intent.setClass(mActivity, RankGroupDetailActivity.class);
		// int currType = ((RankFragment) mActivity).mCurrType;
		intent.putExtra("currType", RankFragment.mCurrType);
		// Logger.i(TAG, " groupName == " + groupName + " groupid == " +
		// groupid);
		intent.putExtra("groupName", groupName);
		intent.putExtra("groupid", groupid);
		intent.putExtra("clubid", mClubId);

		mActivity.startActivityForResult(intent, Constants.RANK_SELECT_PK);
		mActivity.overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
	}

	class MyOnItemClickListener implements AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			onItemClick(position);
		}

		private void onItemClick(int position) {

			// TODO ads
			if (mAdapter.isActive()) {
				if (mReqData != null) {
					if (mFlag == RankFragment.GROUP_PK && position == 2) {
						String groupName = mReqData.groupname;
						String groupid = mReqData.groupid;
						if (mGroupName.equals(groupName)) {
							mArticleSelectedListener.onArticleSelected(RankFragment.GROUP_MEMBER_PK);
							return;
						}
						selectActivity(groupid, groupName);
					} else if (mFlag == RankFragment.ALL_MEMBER_PK && position == 2) {
						String groupName = mReqData.groupname;
						if (mGroupName.equals(groupName)) {
							mArticleSelectedListener.onArticleSelected(RankFragment.GROUP_MEMBER_PK);
							return;
						}
						String groupid = mHealthProviderMetaData.getGroupIdFromName1(groupName, mClubId);
						selectActivity(groupid, groupName);
					}
				}
				return;
			}
			if (mFlag == RankFragment.GROUP_PK && position > 1) {
				// 班组
				// if (position == 1) {
				// mArticleSelectedListener.onArticleSelected(RankFragment.GROUP_MEMBER_PK);
				// return;
				// }
				HashMap<String, Object> record = mArrayListRecords.get(position - 2);
				String groupName = (String) record.get(GroupPkInfoTableMetaData.GROUP_NAME);
				if (mGroupName.equals(groupName)) {
					mArticleSelectedListener.onArticleSelected(RankFragment.GROUP_MEMBER_PK);
					return;
				}
				String groupid = (String) record.get(GroupPkInfoTableMetaData.GROUP_ID);
				selectActivity(groupid, groupName);

				// startActivity(intent);
			}

			if (position != 1) {
				return;
			}

			switch (mFlag) {
			case RankFragment.ALL_MEMBER_PK:
				if (RankFragment.mCurrType == Constants.GROUP_7DAY) {
					mGetSumCount = MHealthProviderMetaData.GetMHealthProvider(mActivity).getOrgnizeMemberSumToday(mClubId);
					if (mGetSumCount < Integer.valueOf(mRankingDate.member7seq)) {
						List<RankUserBean> myRankByType = MHealthProviderMetaData.GetMHealthProvider(mActivity).getMyRankByType(7, mClubId);
						buildDialog(myRankByType, false, 0, null);
					} else {
						mListview.setSelection(Integer.valueOf(mRankingDate.member7seq));
					}
				} else {
					mGetSumCount = MHealthProviderMetaData.GetMHealthProvider(mActivity).getOrgnizeMemberSumYesterday(mClubId);
					if (mGetSumCount < Integer.valueOf(mRankingDate.member1seq)) {
						List<RankUserBean> myRankByType = MHealthProviderMetaData.GetMHealthProvider(mActivity).getMyRankByType(1, mClubId);
						buildDialog(myRankByType, false, 0, null);
					} else {
						mListview.setSelection(Integer.valueOf(mRankingDate.member1seq));
					}
				}
				break;
			case RankFragment.GROUP_PK:
				if (RankFragment.mCurrType == Constants.GROUP_7DAY) {
					mGetSumCount = MHealthProviderMetaData.GetMHealthProvider(mActivity).getGroupSum(Constants.GROUP_7DAY, mClubId);
					if (mGetSumCount < Integer.valueOf(mRankingDate.group7seq)) {
						List<RankUserBean> myRankByType = MHealthProviderMetaData.GetMHealthProvider(mActivity).getMyRankByType(27, mClubId);
						buildDialog(myRankByType, true, 0, null);
					} else {
						mListview.setSelection(Integer.valueOf(mRankingDate.group7seq));
					}
				} else {
					mGetSumCount = MHealthProviderMetaData.GetMHealthProvider(mActivity).getGroupSum(Constants.GROUP_YESTERDAY, mClubId);
					if (mGetSumCount < Integer.valueOf(mRankingDate.group1seq)) {
						List<RankUserBean> myRankByType = MHealthProviderMetaData.GetMHealthProvider(mActivity).getMyRankByType(21, mClubId);
						buildDialog(myRankByType, true, 0, null);
					} else {
						mListview.setSelection(Integer.valueOf(mRankingDate.group1seq));
					}
				}
				break;
			case RankFragment.GROUP_MEMBER_PK:
				if (RankFragment.mCurrType == Constants.GROUP_7DAY) {
					mGetSumCount = MHealthProviderMetaData.GetMHealthProvider(mActivity).getGroupMemberSum(Constants.GROUP_7DAY, mClubId);
					if (mGetSumCount < Integer.valueOf(mRankingDate.groupmember7seq)) {
						List<RankUserBean> myRankByType = MHealthProviderMetaData.GetMHealthProvider(mActivity).getMyRankByType(17, mClubId);
						buildDialog(myRankByType, false, 0, null);
					} else {
						mListview.setSelection(Integer.valueOf(mRankingDate.groupmember7seq));
					}
				} else {
					mGetSumCount = MHealthProviderMetaData.GetMHealthProvider(mActivity).getGroupMemberSum(Constants.GROUP_YESTERDAY, mClubId);
					if (mGetSumCount < Integer.valueOf(mRankingDate.groupmember1seq)) {
						List<RankUserBean> myRankByType = MHealthProviderMetaData.GetMHealthProvider(mActivity).getMyRankByType(11, mClubId);
						buildDialog(myRankByType, false, 0, null);
					} else {
						mListview.setSelection(Integer.valueOf(mRankingDate.groupmember1seq));
					}
				}
				break;
			}
		}

	}

	private void buildDialog(List<RankUserBean> myRankByType, boolean hidemembername, int mode, String title) {
		buildDialog(myRankByType, hidemembername, mode, title, 0);
	}

	private void buildDialog(List<RankUserBean> myRankByType, boolean hidemembername, int mode, String title, int startAt) {
		if (myRankByType.size() == 0) {
			Toast.makeText(mActivity, "网络异常，请稍后重试~", 0).show();
			return;
		}
		customDialog = new Dialog(mActivity, R.style.dialog_fullscreen);
		customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = View.inflate(mActivity, R.layout.alertdialog_view, null);
		TextView titlename = (TextView) view.findViewById(R.id.is_the_final_textview);
		if (title != null)
			titlename.setText(title);
		ImageButton button = (ImageButton) view.findViewById(R.id.is_adv_button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				customDialog.dismiss();
			}
		});

		Window window = customDialog.getWindow();
		window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
		window.setWindowAnimations(R.style.mystyle); // 添加动画

		ListView listviewddr = (ListView) view.findViewById(R.id.is_adv_listView1);
		mmBaseApater = new mBaseAdapter();
		mmBaseApater.setMode(mode);
		mmBaseApater.setStartAt(startAt);
		mmBaseApater.setOrgnizeMemberPKInfo7Day(myRankByType);
		mmBaseApater.setHidemembername(hidemembername);

		listviewddr.setAdapter(mmBaseApater);

		View listItem = mmBaseApater.getView(0, null, listviewddr);
		listItem.measure(0, 0);
		int measuredHeight = listItem.getMeasuredHeight();

		button.measure(0, 0);
		int lvheight = mActivity.getWindowManager().getDefaultDisplay().getHeight() - button.getMeasuredHeight() - Common.dip2px(mActivity, 50);
		int moveoffset = lvheight / measuredHeight;
		int ofs = 20 - moveoffset / 2;
		if (mode == 0)
			listviewddr.setSelection(ofs < 0 ? 0 : ofs);
		// listviewddr.smoothScrollToPosition(20 + moveoffset/2);

		customDialog.setContentView(view);
		customDialog.show();

	}

	class mBaseAdapter extends BaseAdapter {
		private List<RankUserBean> myRankByType;
		private boolean hidemembername;
		private int mode;
		private int startAt;

		public void setStartAt(int startAt) {
			this.startAt = startAt;
		}

		public void setMode(int mode) {
			this.mode = mode;
		}

		public void setOrgnizeMemberPKInfo7Day(List<RankUserBean> myRankByType) {
			this.myRankByType = myRankByType;
		}

		public void setHidemembername(boolean hidemembername) {
			this.hidemembername = hidemembername;
		}

		@Override
		public int getCount() {
			return myRankByType.size();
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			rHolder holder = null;
			View view = null;
			if (convertView == null) {
				holder = new rHolder();
				view = View.inflate(mActivity, R.layout.list_item_rank, null);
				holder.raiv = (RoundAngleImageView) view.findViewById(R.id.rank_icon_name);
				holder.iv_aikon = (ImageView) view.findViewById(R.id.imageview_rankidfirst);
				holder.rank_seq = (TextView) view.findViewById(R.id.textview_rank_seq);
				holder.member_name = (TextView) view.findViewById(R.id.textview_member_name);
				holder.rankball = (ImageView) view.findViewById(R.id.imageview_rankid_bg);
				holder.group_name = (TextView) view.findViewById(R.id.textview_group_name);
				holder.sbv = (ScoreBarView) view.findViewById(R.id.regularprogressbar);
				holder.llt = (LinearLayout) view.findViewById(R.id.linearLayout_list_item_rank);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (rHolder) view.getTag();
			}
			if (position == 20 && mode == 0) {
				holder.llt.setBackgroundColor(Color.rgb(200, 230, 255));
				holder.rankball.setBackgroundResource(R.drawable.rank_id_dark);
			} else {
				holder.rankball.setBackgroundResource(R.drawable.rank_id_green);
				if (holder.llt != null) {
					if ((position & 1) == 1)
						holder.llt.setBackgroundColor(Color.rgb(235, 235, 235));// 186,216,255
					else
						holder.llt.setBackgroundColor(Color.WHITE);
				}
			}

			if (hidemembername) {
				holder.member_name.setVisibility(View.GONE);
				holder.sbv.setPics(ConstantsBitmaps.mLeftPic, ConstantsBitmaps.mRunPicDouble);
			} else {
				holder.member_name.setVisibility(View.VISIBLE);
				holder.sbv.setPics(ConstantsBitmaps.mLeftPic, ConstantsBitmaps.mRunPicYellow);
			}
			holder.iv_aikon.setVisibility(View.INVISIBLE);
			holder.raiv.setImageResource(R.drawable.avatar_male_middle);
			holder.rank_seq.setText(myRankByType.get(position).getMemberseq());
			holder.member_name.setText(myRankByType.get(position).getMembername());
			holder.group_name.setText(myRankByType.get(position).getGroupname());
			holder.group_name.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String groupName = myRankByType.get(position).getGroupname();
					if (groupName.equals(mGroupName)) {
						mArticleSelectedListener.onArticleSelected(RankFragment.GROUP_MEMBER_PK);
						if (customDialog != null && customDialog.isShowing()) {
							customDialog.dismiss();
						}
						return;
					}
					String groupid = mHealthProviderMetaData.getGroupIdFromName(groupName, mClubId);
					if (customDialog != null && customDialog.isShowing()) {
						customDialog.dismiss();
					}
					selectActivity(groupid, groupName);
				}
			});

			if (position == 0)
				mMaxValue = Integer.parseInt(myRankByType.get(0).getMember7avgstep());
			holder.sbv.setMaxValue(mMaxValue);
			holder.sbv.setScore(Integer.parseInt(myRankByType.get(position).getMember7avgstep()));
			holder.sbv.reDraw();
			mImageName = myRankByType.get(position).getImageurl();
			if ((mode == 0 && position == 20)
					|| (mode == 1 && ((position + startAt) + "").equals((RankFragment.mCurrType == Constants.GROUP_7DAY ? mRankingDate.member7seq : mRankingDate.member1seq)))) {
				SharedPreferences sp = mActivity.getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
				String avatarname = sp.getString(SharedPreferredKey.AVATAR, null);
				holder.raiv.setTag(position + "");
				if (avatarname == null) {
					if (!(TextUtils.isEmpty(mImageName))) {
						getImageAsync(holder.raiv, DataSyn.avatarHttpURL + mImageName + ".jpg", position + "");
					} else {
						getImageAsync(holder.raiv, DataSyn.avatarHttpURL + RankFragment.mPhoneNum + ".jpg", position + "");
					}
				} else {
					getImageAsync(holder.raiv, DataSyn.avatarHttpURL + avatarname + ".jpg", position + "");
				}
			} else {
				if (!(TextUtils.isEmpty(mImageName) || mImageName.equals(""))) {
					getImageAsync(holder.raiv, DataSyn.avatarHttpURL + mImageName + ".jpg", position + "");
				}
			}
			return view;
		}

		class rHolder {
			public ImageView rankball;
			public RoundAngleImageView raiv;
			public ImageView iv_aikon;
			public TextView rank_seq;
			public TextView member_name;
			public TextView group_name;
			public ScoreBarView sbv;
			public LinearLayout llt;
		}
	}

	class MyRefresh implements OnRefreshListener {

		@Override
		public void onRefresh() {
			// refreshGroupPk();
		}

	}

	/**
	 * GroupMemberPKInfo 添加到集合
	 * 
	 * @param memberPkInfo
	 */
	private void addGroupMemberPKInfo(GroupMemberPkInfo memberPkInfo) {
		for (int i = 0; i < memberPkInfo.groupmember.size(); i++) {
			GroupMemberInfo memberInfo = memberPkInfo.groupmember.get(i);

			HashMap<String, Object> record = new HashMap<String, Object>();
			record.put(GroupMemberInfoTableMetaData.MEMBER_NAME, memberInfo.membername);
			record.put(GroupMemberInfoTableMetaData.MEMBER_SCORE, memberInfo.memberscore);
			record.put(GroupMemberInfoTableMetaData.MEMBER_SEQ, memberInfo.memberseq);
			record.put(GroupMemberInfoTableMetaData.MEMBER7AVGDIST, memberInfo.member7avgdist);
			record.put(GroupMemberInfoTableMetaData.MEMBER7AVGSTEP, memberInfo.member7avgstep);
			record.put(GroupMemberInfoTableMetaData.RES1, memberInfo.memberinforev1);
			record.put(GroupMemberInfoTableMetaData.RES3, memberInfo.avatar);
			mArrayListRecords.add(record);
		}
	}

	/**
	 * GROUP_PK_INFO 添加到集合
	 * 
	 * @param groupPkInfo
	 *            2013-1-18
	 */
	private void addGroupPkInfo(GroupPkInfo groupPkInfo) {
		for (int i = 0; i < groupPkInfo.grouppkdata.size(); i++) {
			GroupInfo groupInfo = groupPkInfo.grouppkdata.get(i);

			HashMap<String, Object> record = new HashMap<String, Object>();
			record.put(GroupPkInfoTableMetaData.GROUP_ID, groupInfo.groupid);
			record.put(GroupPkInfoTableMetaData.GROUP_NAME, groupInfo.groupname);
			record.put(GroupPkInfoTableMetaData.GROUP_SCORE, groupInfo.groupscore);
			record.put(GroupPkInfoTableMetaData.GROUP_SEQ, groupInfo.groupseq);
			record.put(GroupPkInfoTableMetaData.GROUP7AVGDIST, groupInfo.group7avgdist);
			record.put(GroupPkInfoTableMetaData.GROUP7AVGSTEP, groupInfo.group7avgstep);

			mArrayListRecords.add(record);
		}
	}

	/**
	 * AllMemeberPKInfo 添加到集合
	 * 
	 * @param orgnizeMemberPKInfo
	 */
	private void addOrgnizeMemberInfo(OrgnizeMemberPKInfo orgnizeMemberPKInfo) {
		for (int i = 0; i < orgnizeMemberPKInfo.orgnizemember.size(); i++) {
			OrgnizeMemberInfo orgnizeMemberInfo = orgnizeMemberPKInfo.orgnizemember.get(i);

			HashMap<String, Object> record = new HashMap<String, Object>();
			record.put(OrgnizeInfoTableMetaData.MEMBER_NAME, orgnizeMemberInfo.membername);
			record.put(OrgnizeInfoTableMetaData.GROUP_NAME, orgnizeMemberInfo.groupname);
			record.put(cmcc.mhealth.db.OrgnizeInfoTableMetaData.MEMBER_SCORE, orgnizeMemberInfo.memberscore);
			record.put(OrgnizeInfoTableMetaData.MEMBER_SEQ, orgnizeMemberInfo.memberseq);
			record.put(OrgnizeInfoTableMetaData.MEMBER7AVGDIST, orgnizeMemberInfo.member7avgdist);
			record.put(OrgnizeInfoTableMetaData.MEMBER7AVGSTEP, orgnizeMemberInfo.member7avgstep);
			record.put(OrgnizeInfoTableMetaData.MEMBER_FORVE1, orgnizeMemberInfo.memberinforev1);
			record.put(OrgnizeInfoTableMetaData.MEMBER_FORVE2, orgnizeMemberInfo.memberinforev2);
			record.put(OrgnizeInfoTableMetaData.AVATER, orgnizeMemberInfo.avatar == null ? " " : orgnizeMemberInfo.avatar);
			mArrayListRecords.add(record);
		}
	}

	// String url[] = {
	// "http://b.hiphotos.baidu.com/album/w%3D230/sign=96bc68afa1ec08fa260014a469ef3d4d/14ce36d3d539b6005b4cc3a7e850352ac65cb724.jpg",
	// };

	public void unActive() {
		if (mAdapter != null) {
			mAdapter.setActive(false);
		}
	}

	class MySimpleAdapter extends BaseAdapter {
		private boolean active = false;
		private HashMap<String, Object> mRecord;
		private int mPosition;

		public void setActive(boolean active) {
			this.active = active;
		}

		public boolean isActive() {
			return active;
		}

		public void setSearchmode(boolean searchmode, SearchDate reqData) {
			if (searchmode == true && reqData != null) {
				mReqData = reqData;
				this.active = true;
				this.notifyDataSetChanged();
			} else if (searchmode == false) {
				this.active = false;
				this.notifyDataSetChanged();
			}
		}

		@Override
		public int getCount() {
			// 搜索模式的话只会显示一个view+headview
			if (active)
				return 1;
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

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// 搜索模式的话只会显示一个view+headview
			if (active) {
				View view = View.inflate(mActivity, R.layout.mygroup_ranking, null);
				RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.relativelayout_myranking);
				rl.setBackgroundResource(R.drawable.bg_myrank_whitered);
				TextView tvname = (TextView) view.findViewById(R.id.textview_myrank_myname);
				TextView tvmystep = (TextView) view.findViewById(R.id.textview_myStep);
				TextView tvmyrank = (TextView) view.findViewById(R.id.textview_myrank_id);
				ImageView iv = (ImageView) view.findViewById(R.id.iamgeview_myrank_avater);
				String sex = mReqData.sex;
				String name = mReqData.membername;
				if (!TextUtils.isEmpty(sex) && sex.equals("1")) {
					int name2Int = Encrypt.getIntFromName(name == null ? "0" : name);
					iv.setImageResource(MainCenterActivity.BASE_ATATAR[name2Int]);
				} else {
					int name2Int = Encrypt.getIntFromName(name == null ? "0" : name);
					iv.setImageResource(MainCenterActivity.BASE_ATATAR[name2Int+7]);
				}
				switch (RankFragment.mCurrIndex) {
				case 0:
					tvname.setText(mReqData.membername);
					if (mReqData.avatar != null) {
						getImageAsync(iv, DataSyn.avatarHttpURL + mReqData.avatar + ".jpg");
					}
					if (RankFragment.mCurrType == Constants.GROUP_7DAY) {
						tvmystep.setText(mReqData.member7info.step + "步");
						tvmyrank.setText("第" + mReqData.member7seq + "位");
					} else {
						tvmystep.setText(mReqData.member1info.step + "步");
						tvmyrank.setText("第" + mReqData.member1seq + "位");
					}
					break;
				case 1:
					tvname.setText(mReqData.groupname);
					if (RankFragment.mCurrType == Constants.GROUP_7DAY) {
						tvmystep.setText(mReqData.group7info.step + "步");
						tvmyrank.setText("第" + mReqData.group7seq + "位");
					} else {
						tvmystep.setText(mReqData.group1info.step + "步");
						tvmyrank.setText("第" + mReqData.group1seq + "位");
					}
					break;
				}
				return view;
			}

			final ViewHolder holder;
			int[] memberType;
			int[] groupType;
			int[] groupMemberType;
			if (convertView == null || convertView.getTag() == null) {
				holder = new ViewHolder();
				convertView = View.inflate(mActivity, R.layout.list_item_rank, null);
				holder.mScorebar = (ScoreBarView) convertView.findViewById(R.id.regularprogressbar);
				holder.mScorebar.setPics(ConstantsBitmaps.mLeftPic, ConstantsBitmaps.mRunPicYellow);
				holder.mItemLayoutList = (LinearLayout) convertView.findViewById(R.id.linearLayout_list_item_rank);
				holder.mTextViewRankId = (TextView) convertView.findViewById(R.id.textview_rank_seq);
				holder.mImageViewRankIcon = (RoundAngleImageView) convertView.findViewById(R.id.rank_icon_name);
				holder.mImageViewRankID = (ImageView) convertView.findViewById(R.id.imageview_rankid_bg);
				holder.mImageViewRankFirst = (ImageView) convertView.findViewById(R.id.imageview_rankidfirst);
				holder.mTextViewMemberName = (TextView) convertView.findViewById(R.id.textview_member_name);
				holder.mTextViewGroupName = (TextView) convertView.findViewById(R.id.textview_group_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
				holder.mScorebar.setPics(ConstantsBitmaps.mLeftPic, ConstantsBitmaps.mRunPicYellow);
			}

			mRecord = mArrayListRecords.get(position);
			HashMap<String, Object> record0 = mArrayListRecords.get(0);

			holder.mImageViewRankFirst.setVisibility(View.INVISIBLE);

			holder.mImageViewRankID.setBackgroundResource(R.drawable.rank_id_org);

			if (position < 3) {
				if (position == 0) {// 第一名显示皇冠
					holder.mImageViewRankFirst.setVisibility(View.VISIBLE);
				}
				// 三名以后显示绿色
				holder.mImageViewRankID.setBackgroundResource(R.drawable.rank_id_green);
				holder.mScorebar.setPics(ConstantsBitmaps.mLeftPic, ConstantsBitmaps.mRunPicGreen);
			}

			int progress = 0;
			try {
				int progressMax;
				memberType = new int[] { Integer.parseInt(TextUtils.isEmpty(mRankingDate.member1seq)?"0":mRankingDate.member1seq), Integer.parseInt(TextUtils.isEmpty(mRankingDate.member7seq)?"0":mRankingDate.member7seq) };
				groupType = new int[] { Integer.parseInt(TextUtils.isEmpty(mRankingDate.group1seq)?"0":mRankingDate.group1seq), Integer.parseInt(TextUtils.isEmpty(mRankingDate.group7seq)?"0":mRankingDate.group7seq) };
				groupMemberType = new int[] { Integer.parseInt(TextUtils.isEmpty(mRankingDate.groupmember1seq)?"0":mRankingDate.groupmember1seq), Integer.parseInt(TextUtils.isEmpty(mRankingDate.groupmember7seq)?"0":mRankingDate.groupmember7seq) };

				switch (mFlag) {
				case RankFragment.ALL_MEMBER_PK:
					holder.mTextViewMemberName.setVisibility(View.VISIBLE);
					holder.mTextViewGroupName.setVisibility(View.VISIBLE);
					String name = mRecord.get(OrgnizeInfoTableMetaData.MEMBER_NAME).toString();
					holder.mTextViewMemberName.setText(name == null ? "null" : name);
					String seq = mRecord.get(OrgnizeInfoTableMetaData.MEMBER_SEQ).toString();
					holder.mTextViewRankId.setText(seq == null ? "null" : seq);
					// 设置头像
					String avater = mRecord.get(OrgnizeInfoTableMetaData.MEMBER_FORVE1).toString();
					if (!TextUtils.isEmpty(avater) && avater.equals("1")) {
						// 根据id表示头像
						int name2Int = Encrypt.getIntFromName(name == null ? "0" : name);
						holder.mImageViewRankIcon.setImageResource(MainCenterActivity.BASE_ATATAR[name2Int]);
						// holder.mImageViewRankIcon.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.avatar_male_middle));
					} else {
						int name2Int = Encrypt.getIntFromName(name == null ? "0" : name);
						holder.mImageViewRankIcon.setImageResource(MainCenterActivity.BASE_ATATAR[name2Int + 7]);
						// holder.mImageViewRankIcon.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.avatar_female_middle));
					}
					holder.mImageViewRankIcon.setTag(position + "0");

					SharedPreferences sp = mActivity.getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
					String avatarname = sp.getString(SharedPreferredKey.AVATAR, null);

					mImageName = mRecord.get(OrgnizeInfoTableMetaData.AVATER).toString();
					if (position == memberType[RankFragment.mCurrType] - 1) {
						if (avatarname == null) {
							if (!(TextUtils.isEmpty(mImageName))) {
								getImageAsync(holder.mImageViewRankIcon, DataSyn.avatarHttpURL + mImageName + ".jpg", position + "0");
							} else {
								getImageAsync(holder.mImageViewRankIcon, DataSyn.avatarHttpURL + RankFragment.mPhoneNum + ".jpg", position + "0");
							}
						} else {
							getImageAsync(holder.mImageViewRankIcon, DataSyn.avatarHttpURL + avatarname + ".jpg", position + "0");
						}
					} else {
						if (!TextUtils.isEmpty(mImageName)) {
							getImageAsync(holder.mImageViewRankIcon, DataSyn.avatarHttpURL + mImageName + ".jpg", position + "0");
						}
					}

					if (position == memberType[RankFragment.mCurrType] - 1) {
						holder.mItemLayoutList.setBackgroundColor(Color.rgb(200, 230, 255));
					} else {
						if ((position & 1) == 1)
							holder.mItemLayoutList.setBackgroundResource(R.drawable.listitem_click_bg2);// 186,216,255
						else
							holder.mItemLayoutList.setBackgroundResource(R.drawable.listitem_click_bg);
					}

					holder.mTextViewGroupName.setText(mRecord.get(OrgnizeInfoTableMetaData.GROUP_NAME).toString());
					holder.mTextViewGroupName.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mPosition = position;
							// holder.mTextViewGroupName.getParent().get
							// String groupname =
							// mRecord.get(OrgnizeInfoTableMetaData.GROUP_NAME).toString();
							// TODO
							HashMap<String, Object> record = mArrayListRecords.get(mPosition);
							String groupName = (String) record.get(OrgnizeInfoTableMetaData.GROUP_NAME);
							if (groupName.equals(mGroupName)) {
								mArticleSelectedListener.onArticleSelected(RankFragment.GROUP_MEMBER_PK);
								return;
							}
							String groupid = mHealthProviderMetaData.getGroupIdFromName(groupName, mClubId);
							selectActivity(groupid, groupName);
						}
					});

					progress = Integer.parseInt(new DecimalFormat("0").format(Double.valueOf(mRecord.get(GroupMemberInfoTableMetaData.MEMBER7AVGSTEP).toString())));
					progressMax = Integer.parseInt(new DecimalFormat("0").format(Double.valueOf(record0.get(GroupMemberInfoTableMetaData.MEMBER7AVGSTEP).toString())));
					mMaxValue = progressMax;
					// 七天小于100步设置为蓝色小人
					if (progress < 100) {
						holder.mScorebar.setPics(ConstantsBitmaps.mLeftPic, ConstantsBitmaps.mRunPicBlue);
						holder.mImageViewRankID.setBackgroundResource(R.drawable.rank_id_dark);
					} else if (position > 3) {
						holder.mImageViewRankID.setBackgroundResource(R.drawable.rank_id_org);
						holder.mScorebar.setPics(ConstantsBitmaps.mLeftPic, ConstantsBitmaps.mRunPicYellow);
					}
					holder.mScorebar.setScore(progress);
					break;
				case RankFragment.GROUP_MEMBER_PK:

					holder.mTextViewMemberName.setVisibility(View.VISIBLE);
					holder.mTextViewRankId.setText(mRecord.get(GroupMemberInfoTableMetaData.MEMBER_SEQ).toString());
					String icon = mRecord.get(GroupMemberInfoTableMetaData.RES1).toString();
					String groupName = mRecord.get(OrgnizeInfoTableMetaData.MEMBER_NAME).toString();
					if (!TextUtils.isEmpty(icon) && icon.equals("1")) {
						// // 设置头像
						// holder.mImageViewRankIcon.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.avatar_male_middle));
						int name2Int = Encrypt.getIntFromName(groupName == null ? "0" : groupName);
						holder.mImageViewRankIcon.setImageResource(MainCenterActivity.BASE_ATATAR[name2Int]);
					} else {
						int name2Int = Encrypt.getIntFromName(groupName == null ? "0" : groupName);
						holder.mImageViewRankIcon.setImageResource(MainCenterActivity.BASE_ATATAR[name2Int + 7]);
						// holder.mImageViewRankIcon.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.avatar_female_middle));
					}
					holder.mImageViewRankIcon.setTag(position + "1");

					SharedPreferences sp2 = mActivity.getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
					String avatarname2 = sp2.getString(SharedPreferredKey.AVATAR, null);

					String mImageUrl = "";
					if (GroupMemberInfoTableMetaData.RES3 != null) {
						if (mRecord.get(GroupMemberInfoTableMetaData.RES3) != null) {
							mImageUrl = mRecord.get(GroupMemberInfoTableMetaData.RES3).toString();
						}
					}

					if (position == groupMemberType[RankFragment.mCurrType] - 1) {
						if (avatarname2 == null) {
							if (!(TextUtils.isEmpty(mImageUrl))) {
								getImageAsync(holder.mImageViewRankIcon, DataSyn.avatarHttpURL + mImageUrl + ".jpg", position + "0");
							} else {
								getImageAsync(holder.mImageViewRankIcon, DataSyn.avatarHttpURL + RankFragment.mPhoneNum + ".jpg", position + "0");
							}
						} else {
							getImageAsync(holder.mImageViewRankIcon, DataSyn.avatarHttpURL + avatarname2 + ".jpg", position + "0");
						}
					} else {
						if (!TextUtils.isEmpty(mImageUrl)) {
							getImageAsync(holder.mImageViewRankIcon, DataSyn.avatarHttpURL + mImageUrl + ".jpg", position + "0");
						}
					}

					if (position == groupMemberType[RankFragment.mCurrType] - 1) {
						holder.mItemLayoutList.setBackgroundColor(Color.rgb(200, 230, 255));
					} else {
						if ((position & 1) == 1)
							holder.mItemLayoutList.setBackgroundColor(Color.rgb(235, 235, 235));// 186,216,255
						else
							holder.mItemLayoutList.setBackgroundColor(Color.WHITE);
					}

					holder.mTextViewMemberName.setText(mRecord.get(GroupMemberInfoTableMetaData.MEMBER_NAME).toString());
					// 设置组内组名
					// holder.mTextViewGroupName.setText(mGroupName);
					holder.mTextViewGroupName.setVisibility(View.INVISIBLE);

					progress = Integer.parseInt(new DecimalFormat("0").format(Double.valueOf(mRecord.get(GroupMemberInfoTableMetaData.MEMBER7AVGSTEP).toString())));
					progressMax = Integer.parseInt(new DecimalFormat("0").format(Double.valueOf(record0.get(GroupMemberInfoTableMetaData.MEMBER7AVGSTEP).toString())));
					mMaxValue = progressMax;
					holder.mScorebar.setScore(progress);

					if (progress < 100) {
						holder.mScorebar.setPics(ConstantsBitmaps.mLeftPic, ConstantsBitmaps.mRunPicBlue);
						holder.mImageViewRankID.setBackgroundResource(R.drawable.rank_id_dark);
					} else if (position > 3) {
						holder.mImageViewRankID.setBackgroundResource(R.drawable.rank_id_org);
						holder.mScorebar.setPics(ConstantsBitmaps.mLeftPic, ConstantsBitmaps.mRunPicYellow);
					}
					break;
				case RankFragment.GROUP_PK:

					if (position == groupType[RankFragment.mCurrType] - 1) {
						holder.mItemLayoutList.setBackgroundResource(R.drawable.listitem_click_bg_mypos);
					} else {
						if ((position & 1) == 1)
							holder.mItemLayoutList.setBackgroundResource(R.drawable.listitem_click_bg);
						else
							holder.mItemLayoutList.setBackgroundResource(R.drawable.listitem_click_bg2);
					}
					holder.mTextViewMemberName.setVisibility(View.GONE);
					holder.mTextViewRankId.setText(mRecord.get(GroupPkInfoTableMetaData.GROUP_SEQ).toString());
					holder.mImageViewRankIcon.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.avatar_group_middle));
					holder.mTextViewGroupName.setText(mRecord.get(GroupPkInfoTableMetaData.GROUP_NAME).toString());

					progress = Integer.parseInt(new DecimalFormat("0").format(Double.valueOf(mRecord.get(GroupPkInfoTableMetaData.GROUP7AVGSTEP).toString())));
					progressMax = Integer.parseInt(new DecimalFormat("0").format(Double.valueOf(record0.get(GroupPkInfoTableMetaData.GROUP7AVGSTEP).toString())));
					mMaxValue = progressMax;

					String strGroupNameTmp = mRecord.get(GroupPkInfoTableMetaData.GROUP_NAME).toString();

					holder.mTextViewGroupName.setText(strGroupNameTmp);
					holder.mScorebar.setScore(progress);

					holder.mScorebar.setPics(ConstantsBitmaps.mLeftPic, ConstantsBitmaps.mRunPicDouble);
					if (progress < 100) {
						holder.mImageViewRankID.setBackgroundResource(R.drawable.rank_id_dark);
					} else if (position > 3) {
						holder.mImageViewRankID.setBackgroundResource(R.drawable.rank_id_org);
					}
					break;
				}
				final View tempview = convertView;
				final int[] membertypeint = memberType;
				final int[] groupmembertypeint = groupMemberType;
				switch (mFlag) {
				case RankFragment.ALL_MEMBER_PK:
				case RankFragment.GROUP_MEMBER_PK:
					holder.mImageViewRankIcon.setOnClickListener(new OnClickListener() {
						int mPosition = position;

						@Override
						public void onClick(View arg0) {
							// TODO 弹出大头像
							int oy = tempview.getTop();
							int ox = tempview.getLeft();
							Logger.i(TAG, "tempview.getTop() ---> " + oy);
							Logger.i(TAG, "tempview.getLeft() ---> " + ox);
							createPWforFace(mPosition, ox, oy, mFlag, membertypeint, groupmembertypeint);
						}

					});
					break;
				}
			} catch (Exception e) {
				// Logger.d(TAG, "mFlag == " + mFlag);
				e.printStackTrace();
			}
			holder.mScorebar.setMaxValue(mMaxValue);
			holder.mScorebar.setTypeface(Typeface.DEFAULT_BOLD);// 字体类型加粗
			holder.mScorebar.reDraw();

			return convertView;
		}
	}

	private void createPWforFace(int pos, int offsetX, int offsetY, int type, int[] memberType, int[] groupMemberType) {
		if (mArrayListRecords == null)
			return;
		HashMap<String, Object> record = mArrayListRecords.get(pos);
		if (record == null || record.size() == 0)
			return;
		Object icon = null;
		Object imageName = null;
		String memberName = null;
		switch (mFlag) {
		case RankFragment.ALL_MEMBER_PK:
			icon = record.get(OrgnizeInfoTableMetaData.MEMBER_FORVE1);
			imageName = record.get(OrgnizeInfoTableMetaData.AVATER);
			memberName = (String) record.get(OrgnizeInfoTableMetaData.MEMBER_NAME);
			break;
		case RankFragment.GROUP_MEMBER_PK:
			icon = record.get(GroupMemberInfoTableMetaData.RES1);
			imageName = record.get(GroupMemberInfoTableMetaData.RES3);
			memberName = (String) record.get(OrgnizeInfoTableMetaData.MEMBER_NAME);
			break;
		}
		if (icon == null || imageName == null)
			return;
		if (!TextUtils.isEmpty(icon.toString()) && icon.toString().equals("1")) {
			int name2int = Encrypt.getIntFromName(memberName);
			mFaceIV.setImageDrawable(mActivity.getResources().getDrawable(MainCenterActivity.BASE_ATATAR[name2int]));
		} else {
			int name2int = Encrypt.getIntFromName(memberName);
			mFaceIV.setImageDrawable(mActivity.getResources().getDrawable(MainCenterActivity.BASE_ATATAR[name2int + 7]));
		}
		mFaceIV.setTag(pos + "");
		if ("".equals(imageName)) {
			if ((pos == memberType[RankFragment.mCurrType] - 1 && mFlag == RankFragment.ALL_MEMBER_PK) || (pos == groupMemberType[RankFragment.mCurrType] - 1 && mFlag == RankFragment.GROUP_MEMBER_PK)) {
				SharedPreferences sp = mActivity.getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
				String avatarname = sp.getString(SharedPreferredKey.AVATAR, null);
				if (avatarname == null)
					avatarname = RankFragment.mPhoneNum;
				getImageAsync(mFaceIV, DataSyn.avatarHttpURL + avatarname + ".jpg", pos + "");
				getImageAsync(mFaceIV, DataSyn.avatarHttpURL + avatarname + ".jpg", pos + "", 1);
			}
		} else {
			if (!TextUtils.isEmpty(imageName.toString())) {
				getImageAsync(mFaceIV, DataSyn.avatarHttpURL + imageName.toString() + ".jpg", pos + "");
				getImageAsync(mFaceIV, DataSyn.avatarHttpURL + imageName.toString() + ".jpg", pos + "", 1);
			}
		}

		float scaleSet = 5 / 27f;
		float rlxp = mFaceRL.getMeasuredWidth() / 2f;
		float rlyp = mFaceRL.getMeasuredHeight() / 2f;

		mFaceLL.clearAnimation();
		AnimationSet set = new AnimationSet(true);
		TranslateAnimation ta = new TranslateAnimation(offsetX - rlxp + Common.dip2px(mActivity, 184), 0, offsetY - rlyp + Common.dip2px(mActivity, 137), 0);
		ta.setDuration(400);
		ScaleAnimation sa = new ScaleAnimation(scaleSet, 1, scaleSet, 1);
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

	private Drawable getImageAsync(ImageView holder, String url) {
		return getImageAsync(holder, url, null);
	}

	private Drawable getImageAsync(ImageView holder, String url, String tag) {
		return getImageAsync(holder, url, null, 0);
	}

	private Drawable getImageAsync(ImageView holder, String url, String tag, int mode) {
		return ImageUtil.getInstance().loadBitmap(holder, url, tag, mode);
	}

	private static class ViewHolder {
		// Drawable mIndicator;
		TextView mTextViewRankId, mTextViewMemberName, mTextViewGroupName;
		ImageView mImageViewRankFirst, mImageViewRankID;
		RoundAngleImageView mImageViewRankIcon;
		// RankItemProgressBar mSaundProgressBarStep;
		LinearLayout mItemLayoutList;
		cmcc.mhealth.view.ScoreBarView mScorebar;
	}

	/**
	 * 数据展示
	 * 
	 * @param flag
	 */
	public void displayRankInfo(int flag) {
		if (mArrayListRecords != null) {
			mArrayListRecords.clear();
		}

		mHealthProviderMetaData = MHealthProviderMetaData.GetMHealthProvider(mActivity);
		// int currType = ((RankFragment) mActivity).mCurrType;
		switch (flag) {
		case RankFragment.GROUP_PK:
			GroupPkInfo groupPkInfo;
			if (RankFragment.mCurrType == Constants.GROUP_7DAY) {
				groupPkInfo = mHealthProviderMetaData.getGroupPkInfoToday(mClubId);
			} else {
				groupPkInfo = mHealthProviderMetaData.getGroupPkInfoYesterday(mClubId);
			}

			mImageViewAvater.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.avatar_group_middle));
			addGroupPkInfo(groupPkInfo);
			break;
		case RankFragment.GROUP_MEMBER_PK:
			GroupMemberPkInfo memberPkInfo;
			if (RankFragment.mCurrType == Constants.GROUP_7DAY) {
				memberPkInfo = mHealthProviderMetaData.getMemberPkInfoToday(mClubId);
			} else {
				memberPkInfo = mHealthProviderMetaData.getMemberPkInfoYesterday(mClubId);
			}
			addGroupMemberPKInfo(memberPkInfo);
			// if (!TextUtils.isEmpty(mAvaterIconPath)) {
			// getImageAsync(mImageViewAvater, mAvaterIconPath);
			// }
			// if(mSex == "0"){
			// mImageViewAvater.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.avatar_female_middle));
			//
			// }else{
			// mImageViewAvater.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.avatar_male_middle));
			// }
			break;
		case RankFragment.ALL_MEMBER_PK:
			OrgnizeMemberPKInfo allMemberPkInfo;
			if (RankFragment.mCurrType == Constants.GROUP_7DAY) {
				allMemberPkInfo = mHealthProviderMetaData.getOrgnizePkInfoToday(mClubId);
			} else {
				allMemberPkInfo = mHealthProviderMetaData.getOrgnizePkInfoYesterday(mClubId);
			}
			addOrgnizeMemberInfo(allMemberPkInfo);
			// if (!TextUtils.isEmpty(mAvaterIconPath)) {
			// getImageAsync(mImageViewAvater, mAvaterIconPath);
			// }
			// if(mSex == "0"){
			// mImageViewAvater.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.avatar_female_middle));
			//
			// }else{
			// mImageViewAvater.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.avatar_male_middle));
			// }
			break;
		default:
			Logger.i(TAG, "default trigger");
		}
	}

	Boolean mIsLoading = false; // 判断是否处在正在加载状态中

	private RoundAngleImageView mImageViewAvater;

	private MHealthProviderMetaData mHealthProviderMetaData;

	/**
	 * 图片懒加载标记
	 */
	// private boolean mBooleanImageIcon;

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (listener != null)
			listener.onChanged(view, scrollState);
		// int itemsLastIndex = mAdapter.getCount() - 1; // 数据集最后一项的索引
		// int lastIndex = itemsLastIndex; // 从最后一条数据的上一条开始加载数据
		// System.out.println("lastIndex==" + mAdapter.getCount()
		// + "  mVisibleLastIndex = " + mVisibleLastIndex);
		// 从数据库拿数据
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_FLING:
			// **mBooleanImageIcon = false;

			break;
		case OnScrollListener.SCROLL_STATE_IDLE:
			// **mBooleanImageIcon = true;
			// int start = mListview.getFirstVisiblePosition();
			int end = mListview.getLastVisiblePosition();
			// 40 - 43
			// Logger.i(TAG, "start = " + start + " end = " + end +
			// " mAdapter.getCount()" + mAdapter.getCount());
			if ((end - 5) == mAdapter.getCount() - 2) {
				loadMoreInfo();
			}
			// TODO loadImageIcon

			break;
		case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			// **mBooleanImageIcon = false;

			break;
		default:
			break;
		}

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	}

	private void loadMoreInfo() {
		if ((mFlag != RankFragment.ALL_MEMBER_PK || mIsLoading)) {
			return;
		}
		// mListview.addFooterView(mFootview);
		if (mLoadOver) {
			mFootview.setVisibility(View.VISIBLE);
		} else {
			mFootview.setVisibility(View.GONE);
			return;
		}
		new Thread() {

			public synchronized void run() {

				mIsLoading = true;
				mOrgnizeMemSum = new OrgnizeMemberSum();
				// 最底部，加载更多数据 getPedorgnizeMemberSum
				int res = -1;
				if (mOrgnizeMemSumNum == 0) {
					res = DataSyn.getInstance().getPedorgnizeMemberSum(RankFragment.mPhoneNum, RankFragment.mPassword, mClubId, mOrgnizeMemSum);
					if (res == 0) {
						mOrgnizeMemSumNum = mOrgnizeMemSum.orgnizememsum;// 获取数据库的总条数
					} else {
						// 网络请求错误
						mActivity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								mFootview.setVisibility(View.GONE);
							}
						});
						mIsLoading = false;
						return;
					}
				}
				mGetSumCount = 0;
				// if (res == 0) {
				// mOrgnizeMemSumNum = mOrgnizeMemSum.orgnizememsum;// 获取数据库的总条数
				if (RankFragment.mCurrType == Constants.GROUP_7DAY) {
					// 从数据库取今天的数据条数，便于更多加载的开始位置的确定
					mGetSumCount = MHealthProviderMetaData.GetMHealthProvider(mActivity).getOrgnizeMemberSumToday(mClubId);
				} else {
					mGetSumCount = MHealthProviderMetaData.GetMHealthProvider(mActivity).getOrgnizeMemberSumYesterday(mClubId);
				}

				if (mGetSumCount != 0 && mGetSumCount < mOrgnizeMemSumNum) {
					// 数据库有数据并且数据小于网络数据的总数
					mFirstItem = mGetSumCount + 1; // 第一条数据为当前数据库的总数+1
					mLastItem = mGetSumCount + mAdd; // 最后一条为当前数据库的总数+mAdd
					if (((RankFragment) mRankFragment).updateOrgnizeMemeberInfo(mFirstItem, mLastItem)) {
						// 填充数据
						mActivity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								displayRankInfo(RankFragment.ALL_MEMBER_PK);
								mAdapter.notifyDataSetChanged();
								mFootview.setVisibility(View.GONE);
							}
						});
					}
				} else {
					if (mLoadOver) {
						mLoadOver = false;
						mActivity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(mActivity, "数据全部加载完!", Toast.LENGTH_LONG).show();
								mFootview.setVisibility(View.GONE);
							}
						});
					}
				}
				// }

				mIsLoading = false;
			};
		}.start();
	}

	public void setTop() {
		if (mListview != null) {
			mListview.setSelection(0);
			mListview.smoothScrollBy(0, 0);
		}
	}

	/**
	 * 
	 * setSearchData(设置查找数据的data并立即显示)
	 * 
	 * @创建人：zhangyue - 张悦
	 * @创建时间：2013-9-11 上午11:05:02
	 */
	public void setSearchData(boolean searchmode, SearchDate reqData) {
		if (mAdapter != null)
			mAdapter.setSearchmode(searchmode, reqData);
		if (searchmode) {
			if (mListview != null)
				mListview.removeFooterView(mFootview);
		} else {
			if (mFootview == null) {
				mFootview = View.inflate(mActivity, R.layout.rank_listviewfoot, null);
			}
			if (mLoadOver && RankFragment.mCurrIndex == 0) {
				mListview.addFooterView(mFootview);
			}
		}
	}

	public void closeBigFace() {
		if (mFaceRL == null)
			return;
		mFaceRL.setVisibility(View.INVISIBLE);
	}

	public boolean isBigFaceShowing() {
		if (mFaceRL == null)
			return false;
		return mFaceRL.getVisibility() == View.VISIBLE;
	}

	public interface OnDirectorStateChanged {
		abstract void onChanged(AbsListView view, int scrollState);
	}

	public void setOnDirectorStateChanged(OnDirectorStateChanged listener) {
		this.listener = listener;
	}

	/**
	 * 引导的handler
	 */
	private Handler handler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			if (mProgressDialog != null)
				mProgressDialog.dismiss();
			Bundle data = msg.getData();
			int startAt = data.getInt("startAt");
			switch (msg.what) {
			case 0:
				List<RankUserBean> mrbList = new ArrayList<RankUserBean>();
				OrgnizeMemberPKInfo pkinfo = data.getParcelable("orgnizedata");
				for (int i = 0; i < pkinfo.orgnizemember.size(); i++) {
					RankUserBean mrb = new RankUserBean();
					mrb.setGroupname(pkinfo.orgnizemember.get(i).groupname);
					mrb.setImageurl(pkinfo.orgnizemember.get(i).avatar);
					mrb.setMember7avgstep(pkinfo.orgnizemember.get(i).member7avgstep);
					mrb.setMembername(pkinfo.orgnizemember.get(i).membername);
					mrb.setMemberseq(pkinfo.orgnizemember.get(i).memberseq);
					mrbList.add(mrb);
				}
				buildDialog(mrbList, false, 1, "段排名", startAt);
				break;
			}
		}
	};

	private ProgressDialog mProgressDialog;

	private SharedPreferences mSharedInfo;

	/**
	 * 弹出引导.startAt 100,200,300...
	 */
	public void setDirector(final int startAt) {
		int orgnizeMemberSumToday = MHealthProviderMetaData.GetMHealthProvider(mActivity).getOrgnizeMemberSumToday(mClubId);
		if (startAt <= orgnizeMemberSumToday) {
			mListview.setSelection(startAt);
			if (startAt == orgnizeMemberSumToday)
				loadMoreInfo();
			return;
		}

		mProgressDialog = Common.initProgressDialog("载入中...", mActivity);
		new Thread() {
			public void run() {
				SharedPreferences sp = mActivity.getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
				String password = sp.getString(SharedPreferredKey.PASSWORD, "0");
				String phonenum = sp.getString(SharedPreferredKey.PHONENUM, "0");

				Message msg = Message.obtain();
				msg.what = 0;
				Bundle data = new Bundle();
				data.putInt("startAt", startAt);
				switch (RankFragment.mCurrType) {
				case Constants.GROUP_7DAY:
					OrgnizeMemberPKInfo orgnizeMemberPKInfo = new OrgnizeMemberPKInfo();
					DataSyn.getInstance().getOrgnizeMembersPkInfo7Day(phonenum, password, mClubId, startAt, startAt + 99, orgnizeMemberPKInfo);
					data.putParcelable("orgnizedata", orgnizeMemberPKInfo);
					break;
				case Constants.GROUP_YESTERDAY:
					OrgnizeMemberPKInfo orgnizeMemberPKInfoYesterDay = new OrgnizeMemberPKInfo();
					DataSyn.getInstance().getOrgnizeMembersPkInfoYestoday(phonenum, password, mClubId, startAt, startAt + 99, orgnizeMemberPKInfoYesterDay, "", "");
					data.putParcelable("orgnizedata", orgnizeMemberPKInfoYesterDay);
					break;
				}
				msg.setData(data);
				handler.sendMessage(msg);
			};
		}.start();
	}
}
