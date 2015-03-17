package cmcc.mhealth.slidingcontrol;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cmcc.mhealth.R;
import cmcc.mhealth.adapter.CommonAutoCompleteAdapter;
import cmcc.mhealth.adapter.CommonFragmentPagerAdapter;
import cmcc.mhealth.basic.BaseFragment;
import cmcc.mhealth.bean.CommonUserSearchInfos;
import cmcc.mhealth.bean.ContectGroupData;
import cmcc.mhealth.bean.GroupIdInfo;
import cmcc.mhealth.bean.GroupInfo;
import cmcc.mhealth.bean.GroupMemberInfo;
import cmcc.mhealth.bean.GroupMemberPkInfo;
import cmcc.mhealth.bean.GroupPkInfo;
import cmcc.mhealth.bean.GroupRankUpdateVersion;
import cmcc.mhealth.bean.OrgnizeMemberInfo;
import cmcc.mhealth.bean.OrgnizeMemberPKInfo;
import cmcc.mhealth.bean.OrgnizeMemberSum;
import cmcc.mhealth.bean.RankingDate;
import cmcc.mhealth.bean.SearchDate;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Constants;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.db.MHealthProviderMetaData;
import cmcc.mhealth.net.DataSyn;
import cmcc.mhealth.slidingcontrol.RankChildFragment.OnArticleSelectedListener;
import cmcc.mhealth.slidingcontrol.RankChildFragment.OnDirectorStateChanged;
import cmcc.mhealth.view.MyAutoCompleteTextView;
import cmcc.mhealth.view.MyAutoCompleteTextView.OnNameReturnedListener;

import com.google.analytics.tracking.android.EasyTracker;

public class RankFragment extends BaseFragment implements OnArticleSelectedListener, OnClickListener {

	protected static final String TAG = "RankActivity";

	public static final int ALL_MEMBER_PK = 0;// ��������
	public static final int GROUP_PK = 1; // ��������
	public static final int GROUP_MEMBER_PK = 2;// ��������

	public final static int FIRSTITEM = 1; // ���ص���ʼλ��
	public final static int LASTITEM = 100; // ���ص�ĩβλ��
	public final static int ADD = 100; // �������

	private RadioGroup mRadioGroup;
	private RadioButton mRadioButtonMyInfo1, mRadioButtonMyInfo2, mRadioButtonMyInfo3;

	// ������Ϣ
	public static String mPhoneNum;
	public static String mPassword;
	public int ClubId = 1;
	public String mGroupId = "";
	public String mGroupName = "";
	// ��������
	private String mOrgnizemem7Seq;
	private String mOrgnizememSeq;
	// ������������
	private String mGroupmem7Seq;
	private String mGroupmemSeq;
	// ��������
	private String mGroup7Seq;
	private String mGroupSeq;
	// ��ǰID������Ա����
	private String mMembername;
	// ��ǰID������Ա7��ƽ������
	// private String mMember7avgstep;
	private String mSex;

	public static int mCurrIndex = 0;
	public static int mLastIndex = 0;
	public static int mCurrType = Constants.GROUP_YESTERDAY; // ����7��

	private ArrayList<Fragment> mFragmentsList = new ArrayList<Fragment>();

	// ��ǰ����ʱ��ֵ
	private String mUpdateTime; // �ϴθ���ʱ��
	private String mCurrentTime;// ���θ���ʱ��
	private static String sstrUpdateVersion;// ���θ���ʱ��
	// title
	private TextView mTextViewTitle;
	private RadioGroup mImageButtonTitle;
	// rank title
	private TextView mTextViewRankTitle;

	// ����
	private TextView mSearchButton;
	private boolean mIsBack;

	// rank content
	public static ViewPager mViewPagerRank;
	// update
	private LinearLayout mLinearLayoutProgressUpdate;

	private RankingDate mRankingDate;
	private GroupRankUpdateVersion mUpdateVersion;
	private SharedPreferences mSharedInfo;

	private boolean mRefresh = false;

	private String mAvatericonPath = null;

//	private ListView mSearchLV;

	private Dialog customDialog;

	// ��������===
	private LinearLayout mSYllMaster;// ����ͼ
	private LinearLayout mSYllMiddle;// ��ť����ͼ
	private HorizontalScrollView mSYhsv;
	private boolean mSYallowtoshow;

	private String mColorRes;

	public RankFragment() {
	}
	public RankFragment(int ClubId) {
		this.ClubId = ClubId;
	}

	public RankFragment(String colorRes) {
		mColorRes = colorRes;
		setRetainInstance(true);
	}

	private Handler handler = new Handler() {
		@SuppressWarnings("deprecation")
		@Override
		public void dispatchMessage(Message msg) {
			if (mProgressDialog != null)
				mProgressDialog.dismiss();
			Bundle data = msg.getData();
			switch (msg.what) {
			case 0:
				SearchDate reqData = data.getParcelable("reqData");
				customDialog.dismiss();
				RankChildFragment rlvf0 = (RankChildFragment) mViewPagerRank.getAdapter().instantiateItem(mViewPagerRank, mCurrIndex);
				if (rlvf0 == null)
					return;
				rlvf0.setSearchData(true, reqData);
//				mSearchButton.setBackgroundResource(R.drawable.back_button_bg);// ���óɷ��ذ�ť
				mBack.setBackgroundResource(R.drawable.back_button_bg);
				mSearchButton.setVisibility(View.INVISIBLE);
				mIsBack = true;
				mSYallowtoshow = false;
				handler.sendEmptyMessage(50002);
				break;
			case 50001:
			    //����������Ŀ
				int allcount = data.getInt("allcount");
				if (allcount < 100) {
					mSYallowtoshow = false;
					return;
				}
				mSYallowtoshow = true;
				int page = allcount / 100;
				mSYllMiddle.removeAllViews();
				for (int i = 1; i <= page; i++) {
					TextView tvX = new TextView(mActivity);
					tvX.setBackgroundResource(R.drawable.director_basebutton);
					tvX.setText(i * 100 + ">");
					tvX.setGravity(Gravity.CENTER);
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
					lp.setMargins(5, 0, 5, 0);
					tvX.setTextColor(Color.BLACK);
					tvX.setTag(i + "");
					tvX.setLayoutParams(lp);
					mSYllMiddle.addView(tvX);
					tvX.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							RankChildFragment rlvf0 = (RankChildFragment) mViewPagerRank.getAdapter().instantiateItem(mViewPagerRank, 0);
							rlvf0.setDirector(Integer.parseInt(v.getTag().toString()) * 100);
							handler.removeMessages(50002);
							handler.sendEmptyMessage(50002);
						}
					});
				}
				break;
			case 50002:
				if (!mSYllMaster.isShown())
					return;
				controlDirector(0f, -1f, true);
				break;
			case 001:
				// �ȼ��غ�ˢ��
				autoUpdate();
				break;
			case 65535:
				Toast.makeText(mActivity, "�����쳣,��������", 10).show();
				break;
			}
		}

	};

	@Override
	public void onStop() {
		Editor edit = sp.edit();
		edit.putInt("mCurrIndex", RankFragment.mCurrIndex);
		edit.putBoolean("application_is_back", false);
		edit.commit();
		Logger.d(TAG, "onStop!");
		super.onStop();
	}

	protected List<CommonUserSearchInfos> mResultlist = new ArrayList<CommonUserSearchInfos>();
	protected List<ContectGroupData> mGroupResultlist = new ArrayList<ContectGroupData>();

	private ImageView mBack;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_rank, container, false);
		super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		return view;
	}

	private void getSharedInfo() {
		mSharedInfo = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		mPhoneNum = mSharedInfo.getString(SharedPreferredKey.PHONENUM, null); // �õ��绰����
		mPassword = mSharedInfo.getString(SharedPreferredKey.PASSWORD, null); // �õ�����

		mMembername = mSharedInfo.getString(SharedPreferredKey.NAME, "");
		mSex = mSharedInfo.getString(SharedPreferredKey.GENDER, "1");

		mGroupId = mSharedInfo.getString("GROUPID", null); // ������ID
		mGroupName = mSharedInfo.getString(SharedPreferredKey.GROUP_NAME, ""); // �õ�����

		// mOrgnizememSeq = mSharedInfo.getString("ORGNIZE_SEQ", "");
		mOrgnizemem7Seq = mSharedInfo.getString("ORGNIZE_7SEQ", "");
		// mMember7avgstep = mSharedInfo.getString("7_AVGSTEP", "0");

		mUpdateTime = mSharedInfo.getString("GROUP_UPDATE_TIME", ""); // ����ʱ��
		sstrUpdateVersion = mSharedInfo.getString("GROUP_UPDATE_VERSION", mUpdateTime + "0"); // ����ʱ��

		if (mPassword == null || mPhoneNum == null) {
			BaseToast("�˺����⣬�����µ�¼!");
			return;
		}
	}

	private void setCurrType() {
		if (mCurrType == Constants.GROUP_YESTERDAY) {

			if (mImageButtonTitle.getCheckedRadioButtonId() != R.id.radio_yestoday)
				((RadioButton) findView(R.id.radio_yestoday)).setChecked(true);
			setRankTitle(Constants.GROUP_YESTERDAY);
		} else {

			if (mImageButtonTitle.getCheckedRadioButtonId() != R.id.radio_7day)
				((RadioButton) findView(R.id.radio_7day)).setChecked(true);
			setRankTitle(Constants.GROUP_7DAY);
		}
		closeBigFaces();
		mViewPagerRank.setCurrentItem(mCurrIndex);

		RankChildFragment currentFragment = (RankChildFragment) mViewPagerRank.getAdapter().instantiateItem(mViewPagerRank, mCurrIndex);
		if (currentFragment != null)
			currentFragment.initLogic();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.RANK_SELECT_PK) {
			if (resultCode == Activity.RESULT_OK) {
				Bundle bundle = data.getExtras();
				mCurrType = bundle.getInt("currType");

				setCurrType();
			}
		}
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			EasyTracker.getInstance().setContext(mActivity);
			switch (arg0) {
			case ALL_MEMBER_PK:
				mRadioButtonMyInfo1.setChecked(true);
				EasyTracker.getTracker().sendView("all_member_pk");
				break;
			case GROUP_PK:
				mRadioButtonMyInfo2.setChecked(true);
				EasyTracker.getTracker().sendView("Group_pk");
				break;
			case GROUP_MEMBER_PK:
				mRadioButtonMyInfo3.setChecked(true);
				EasyTracker.getTracker().sendView("Group_member_pk");
				break;
			}
			closeBigFaces();
			mCurrIndex = arg0;
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	private void initView() {
		mBack = findView(R.id.button_input_bg_back);
		mBack.setVisibility(View.VISIBLE);
		mBack.setOnClickListener(this);
		// title
		mTextViewTitle = findView(R.id.textView_title_rank);
		mTextViewTitle.setText("����");

		mSearchButton = findView(R.id.ar_search_button);
		// ic_menu_search
		mIsBack = false;
		mSearchButton.setOnClickListener(this);

		mUpdateVersion = new GroupRankUpdateVersion();

		// ����������ť
//		mImageButtonTitle = (RadioGroup) findViewById(R.id.imageButton_title_rank);
		// mImageButtonTitle.setVisibility(View.VISIBLE);
		// mImageButtonTitle.setBackgroundResource(R.drawable.button_rank_today);
		//
		// mImageButtonTitle.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// if (mCurrType == Constants.GROUP_7DAY) {
		// mCurrType = Constants.GROUP_YESTERDAY;
		// } else {
		// mCurrType = Constants.GROUP_7DAY;
		// }
		// setCurrType();
		// }
		// });
		getSearchDatas();

		// ���� 7�� �����л���ť
		mImageButtonTitle = findView(R.id.imageButton_title_rank);
		mImageButtonTitle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (!sp.getBoolean("application_is_back", true)) {
					((RadioButton) findView(R.id.radio_yestoday)).setChecked(true);
					((RadioButton) findView(R.id.radio_7day)).setChecked(false);
					return;
				}
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
				setCurrType();
			}

		});

		// ����״̬
		mLinearLayoutProgressUpdate =  findView(R.id.linearlayout_progress);

		// Rank ��ͷ��ǩ��ʾ
		mTextViewRankTitle = findView(R.id.textview_rank_title);
		// LayoutInflater mInflater = getLayoutInflater();
		// mInflater.inflate(R.layout.listview_rank, null);
		Fragment allMemberPKFragment = RankChildFragment.newInstance(ALL_MEMBER_PK, this, ClubId);

		((RankChildFragment) allMemberPKFragment).setOnDirectorStateChanged(new OnDirectorStateChanged() {
			@Override
			public void onChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					// ������ء�
					showSYMaster();
					break;
				case OnScrollListener.SCROLL_STATE_IDLE:
					// ������ء�
					sendDelayedCloseDirector();
					break;
				}
			}
		});

		Fragment groupFragment = RankChildFragment.newInstance(GROUP_PK, this, ClubId);
		Fragment groupMemberFragment = RankChildFragment.newInstance(GROUP_MEMBER_PK, this, ClubId);
		
		mFragmentsList.clear();
		mFragmentsList.add(allMemberPKFragment);
		mFragmentsList.add(groupFragment);
		mFragmentsList.add(groupMemberFragment);
		mViewPagerRank =  findView(R.id.rank_listcount);
		mViewPagerRank.setAdapter(new CommonFragmentPagerAdapter(((MainCenterActivity) mActivity).getSupportFragmentManager(), mFragmentsList));
		mViewPagerRank.setOnPageChangeListener(new MyOnPageChangeListener());
		
		mRadioGroup = findView(R.id.radio_group_rank);
		mRadioButtonMyInfo1 = findView(R.id.rank_radio0);
		mRadioButtonMyInfo2 = findView(R.id.rank_radio1);
		mRadioButtonMyInfo3 =findView(R.id.rank_radio2);

		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (mSYllMaster == null) {
					mSYllMaster =  findView(R.id.listview_rl_director);
				}
				Logger.i("ListViewFragment", "checkedId==" + checkedId);
				if (!sp.getBoolean("application_is_back", true))
					return;
				switch (checkedId) {
				case R.id.rank_radio0:
					mCurrIndex = ALL_MEMBER_PK;
					mSearchButton.setVisibility(View.VISIBLE);
					resetSearchButton();
					break;
				case R.id.rank_radio1:
					mCurrIndex = GROUP_PK;
					mSearchButton.setVisibility(View.VISIBLE);
					mSYllMaster.setVisibility(View.GONE);
					resetSearchButton();
					break;
				case R.id.rank_radio2:
					mCurrIndex = GROUP_MEMBER_PK;
					mSearchButton.setVisibility(View.GONE);
					mSYllMaster.setVisibility(View.GONE);
					resetSearchButton();
					break;
				}
				mViewPagerRank.setCurrentItem(mCurrIndex, true);
				
				RankChildFragment rlvf0 = (RankChildFragment) mViewPagerRank.getAdapter().instantiateItem(mViewPagerRank, mCurrIndex);
				if (rlvf0 != null) {
					if (mCurrIndex != 2)rlvf0.unActive();
					rlvf0.initLogic();
				}
			}

		});

		// Ĭ��
		mRadioButtonMyInfo1.setChecked(true);
	}
	private boolean isGetSearch;
	private void getSearchDatas() {
		if (isGetSearch) return;
		isGetSearch = true;
		new Thread() {
			public void run() {
				MHealthProviderMetaData mhp = MHealthProviderMetaData.GetMHealthProvider(mActivity);
				mResultlist = mhp.getAllSearchInfos(ClubId);
				mGroupResultlist = mhp.getAllSearchGroupInfos(ClubId);
				isGetSearch = false;
			};
		}.start();
	}

	private void showSYMaster() {
		if (mSYallowtoshow && !mSYllMaster.isShown()) {
			mSYllMaster.setVisibility(View.VISIBLE);
			controlDirector(-1f, 0f, false);
		}
	}

	private void sendDelayedCloseDirector() {
		Message msg = Message.obtain();
		msg.what = 50002;
		handler.removeMessages(50002);
		handler.sendMessageDelayed(msg, 1200);
	}

	private void controlDirector(float yfrom, float yto, boolean gone) {
		TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, yfrom, Animation.RELATIVE_TO_SELF, yto);
		ta.setDuration(150);
		mSYllMaster.startAnimation(ta);
		if (!gone)
			return;
		ta.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mSYllMaster.setVisibility(View.GONE);
			}
		});
	}

	@Override
	public void onResume() {
		Logger.d(TAG, "onResume!!");
		Editor edit = sp.edit();
		edit.putBoolean("application_is_back", true);
		edit.commit();

		if (mCurrIndex == 2) {
			mSearchButton.setVisibility(View.GONE);
		}
		// �������
		mSYhsv = findView(R.id.listview_rl_director_horizontalscroolview);
		mSYhsv.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Logger.i(TAG, "event.getAction()==" + event.getAction());
				switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE:
					if (handler.hasMessages(50002))
						handler.removeMessages(50002);
					break;
				case MotionEvent.ACTION_UP:
					Message msg = Message.obtain();
					msg.what = 50002;
					handler.sendMessageDelayed(msg, 2000);
					break;
				}
				return false;
			}
		});
		mSYllMaster =  findView(R.id.listview_rl_director);
		mSYllMaster.setVisibility(View.INVISIBLE);
//		mSYllLeft =  findView(R.id.listview_rl_director_leftbutton);
//		mSYtvLeft =  findView(R.id.listview_tv_director_leftbutton);
//		mSYllRight =  findView(R.id.listview_rl_director_rightbutton);
//		mSYtvRight = findView(R.id.listview_tv_director_rightbutton);
		mSYllMiddle =  findView(R.id.listview_ll_director);

		getOrgnizedMenSum();

		mRadioButtonMyInfo1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RankChildFragment rlvf0 = (RankChildFragment) mViewPagerRank.getAdapter().instantiateItem(mViewPagerRank, 0);
				if (mLastIndex == 0) {
					rlvf0.setTop();
				}

				rlvf0.setOnDirectorStateChanged(new OnDirectorStateChanged() {
					@Override
					public void onChanged(AbsListView view, int scrollState) {
						switch (scrollState) {
						case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
							// ������ء�
							showSYMaster();
							break;
						case OnScrollListener.SCROLL_STATE_IDLE:
							// ������ء�
							sendDelayedCloseDirector();
							break;
						}
					}
				});

				mLastIndex = 0;
			}
		});
		mRadioButtonMyInfo2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Logger.i(TAG, mLastIndex + "");
				if (mLastIndex == 1) {
					RankChildFragment rlvf1 = (RankChildFragment) mViewPagerRank.getAdapter().instantiateItem(mViewPagerRank, 1);
					rlvf1.setTop();
				}
				mLastIndex = 1;
			}
		});
		mRadioButtonMyInfo3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Logger.i(TAG, mLastIndex + "");
				if (mLastIndex == 2) {
					RankChildFragment rlvf2 = (RankChildFragment) mViewPagerRank.getAdapter().instantiateItem(mViewPagerRank, 2);
					rlvf2.setTop();
				}
				mLastIndex = 2;
			}
		});

		super.onResume();
	}
	private void getOrgnizedMenSum() {
		new Thread() {
			public void run() {
				OrgnizeMemberSum mOrgnizeMemSum = new OrgnizeMemberSum();
				DataSyn.getInstance().getPedorgnizeMemberSum(RankFragment.mPhoneNum, RankFragment.mPassword, ClubId, mOrgnizeMemSum);
				int allcount = mOrgnizeMemSum.orgnizememsum;
				Message msg = Message.obtain();
				msg.what = 50001;
				Bundle data = new Bundle();
				data.putInt("allcount", allcount);
				msg.setData(data);
				handler.sendMessage(msg);
			};
		}.start();
	}

	@SuppressLint("SimpleDateFormat")
	private void setRankTitle(int timeType) {
		if (mUpdateTime == null) {
			SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
			mUpdateTime = info.getString("GROUP_UPDATE_TIME", null); // ����ʱ��
		}
		if (mUpdateTime != null) {
			long updateTime = Common.getDateFromYYYYMMDD(mUpdateTime);
			String title_content = "";
			String before7day = "";
			String strDateTmp = "";

			long yesterdayTime = Common.getYesterday(updateTime);
			long before7updateTime = updateTime - 7 * (1000L * 60 * 60 * 24L);

			before7day = Common.getDateFromTime(before7updateTime, new SimpleDateFormat("M��d��"));
			strDateTmp = Common.getDateFromTime(yesterdayTime, new SimpleDateFormat("M��d��"));

			switch (timeType) {
			case Constants.GROUP_7DAY:
				title_content = before7day + "---" + strDateTmp + "(ƽ������)";
				break;
			case Constants.GROUP_YESTERDAY:
				title_content = strDateTmp + "(���ղ���)";//
				break;
			}
			mTextViewRankTitle.setText(title_content);
		}
	}

	private void updateAllGroupInfo() {
		// *** mLinearLayoutProgressUpdate.setVisibility(View.VISIBLE);

		SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		mUpdateTime = info.getString("GROUP_UPDATE_TIME", null); // ����ʱ��
		mCurrentTime = Common.getRankUpdateYYYYMMDD();
		if (null == mCurrentTime) {
			// ˢ�²�����
			// RankListViewFragment currentFragment = ((RankListViewFragment)
			// mFragmentsList
			// .get(mCurrIndex));
			// currentFragment.displayRankInfo(mCurrIndex);
			mLinearLayoutProgressUpdate.setVisibility(View.GONE);

		} else {
			//�Ƿ���Ҫˢ������
			new Thread(){
				public void run() {
					if (needUpdate(sstrUpdateVersion)) {
						Message msg = new Message();
						msg.what = 001;
						handler.sendMessage(msg);
					}
				};
			}.start();

		}
	}

	// private void updateAllGroupInfoOld() {
	// mLinearLayoutProgressUpdate.setVisibility(View.VISIBLE);
	// Date currentDate = new Date();
	//
	// // �ж�Ϊ�ڶ���5���
	// if (mCurrentTime == null) {
	// if (currentDate.getHours() <= 5) {
	// Calendar date = Calendar.getInstance();
	// date.setTime(currentDate);
	// date.set(Calendar.DATE, date.get(Calendar.DATE) - 1);
	// currentDate = date.getTime();
	// }
	// mCurrentTime = Common.getDateAsYYYYMMDD(currentDate.getTime());
	// }
	// SharedPreferences info = getSharedPreferences(SharedPreferredKey.SharedPrefenceName, 0);
	// mUpdateTime = info.getString("GROUP_UPDATE_TIME", null); // ����ʱ��
	//
	// if (TextUtils.isEmpty(mUpdateTime) || !mUpdateTime.equals(mCurrentTime))
	// {
	// autoUpdate();
	// } else {
	// RankListViewFragment currentFragment = ((RankListViewFragment)
	// mFragmentsList
	// .get(mCurrIndex));
	// currentFragment.displayRankInfo(mCurrIndex);
	// mLinearLayoutProgressUpdate.setVisibility(View.GONE);
	// }
	// }

	public void autoUpdate() {
		if (mRefresh) {
			return;
		}
		new AsyncTask<Integer, Integer, Boolean>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mRefresh = true;
				mLinearLayoutProgressUpdate.setVisibility(View.VISIBLE);
			}

			protected Boolean doInBackground(Integer... params) {
				return updateGroupData();
			}

			@Override
			protected void onPostExecute(Boolean b) {
				long beginTime = System.currentTimeMillis();
				if (b) {
					// ������
					putDataToSharedPreferences();
					mCurrIndex = mViewPagerRank.getCurrentItem();
					RankChildFragment currentFragment = (RankChildFragment) mViewPagerRank.getAdapter().instantiateItem(mViewPagerRank, mCurrIndex);
					// mViewPagerRank.setAdapter(new
					// MyFragmentPagerAdapter(getSupportFragmentManager(),
					// mFragmentsList));
					// RankListViewFragment currentFragment =
					// ((RankListViewFragment) mFragmentsList.get(mCurrIndex));
					mViewPagerRank.setCurrentItem(mCurrIndex);
					// Logger.i("TAG", "result == " +
					// currentFragment.toString());
					if (currentFragment != null)
						currentFragment.initLogic();
					BaseToast("���³ɹ�");
					mRefresh = false;
				} else {
					BaseToast("����ʧ��,���Ժ�����");
				}
				((MainCenterActivity)mActivity).refleshMenuAvatar();

				long endTime = System.currentTimeMillis();
				Logger.i(TAG, "************update time is " + (endTime - beginTime) + "*****************");

				// ������gone
				mLinearLayoutProgressUpdate.setVisibility(View.GONE);
			}
		}.execute();
	}

	private void putDataToSharedPreferences() {
		Editor sharedata = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();
		sharedata.putString("GROUP_UPDATE_TIME", mCurrentTime);// ʱ���ж�
		sharedata.putString("GROUP_UPDATE_VERSION", sstrUpdateVersion);// ʱ���ж�
		sharedata.putString(SharedPreferredKey.NAME, mMembername);// ��Ա����
		// sharedata.putString("7_AVGSTEP", mMember7avgstep);// ��Ա7��ƽ������
		sharedata.putString("ORGNIZE_7SEQ", mOrgnizemem7Seq);// ��Ա��������
		// sharedata.putString("ORGNIZE_SEQ", mOrgnizememSeq);// ��Ա7��ƽ������
		sharedata.putString(SharedPreferredKey.GROUP_NAME, mGroupName);// ������
		sharedata.putString(SharedPreferredKey.GENDER, mSex);// �Ա�
		if (!TextUtils.isEmpty(mAvatericonPath)) {
			// sharedata.putString("AvatericonPath",
			// BASE64.encryptBASE64(mAvatericonPath));// ͷ������
			sharedata.putString(SharedPreferredKey.AVATAR, mAvatericonPath);// ͷ������
		}

		sharedata.putString("group1seq", mRankingDate.group1seq);
		sharedata.putString("group7seq", mRankingDate.group7seq);
		sharedata.putString("groupmember1seq", mRankingDate.groupmember1seq);
		sharedata.putString("groupmember7seq", mRankingDate.groupmember7seq);
		sharedata.putString("member1seq", mRankingDate.member1seq);
		sharedata.putString("member7seq", mRankingDate.member7seq);

		sharedata.putString("group1Info_step", mRankingDate.group1info.step);
		sharedata.putString("group7Info_step", mRankingDate.group7info.step);
		sharedata.putString("memberi1Info_step", mRankingDate.member1info.step);
		sharedata.putString("memberi7Info_step", mRankingDate.member7info.step);

		sharedata.commit();

		mUpdateTime = mCurrentTime;
		setRankTitle(mCurrType);
	}

	/**
	 * getOrgnizememSeq(��ȡ��ǰID�ĸ�����������)
	 * 
	 * @return true or false
	 * @Exception �쳣����
	 * @�����ˣ�Qiujunjie - �񿡽�
	 * @����ʱ�䣺2013-3-19 ����9:32:50
	 * @�޸��ˣ�Qiujunjie - �񿡽�
	 * @�޸�ʱ�䣺2013-3-19 ����9:32:50
	 */
	private boolean getOrgnizememSeq() {
		// OrgnizememSeq orgnizememSeq = new OrgnizememSeq();
		mRankingDate = new RankingDate();
		try {
			int res = DataSyn.getInstance().getPedGroupSeq(mPhoneNum, mPassword, ClubId, mRankingDate);
			if (res == 0) {
				// ��ǰID��������

				mOrgnizemem7Seq = TextUtils.isEmpty(mRankingDate.member7seq)?"0":mRankingDate.member7seq;
				mOrgnizememSeq = TextUtils.isEmpty(mRankingDate.member1seq)?"0":mRankingDate.member1seq;
				mGroupmem7Seq = TextUtils.isEmpty(mRankingDate.member7seq)?"0":mRankingDate.groupmember7seq;
				mGroupmemSeq = TextUtils.isEmpty(mRankingDate.groupmember1seq)?"0":mRankingDate.groupmember1seq;
				mGroup7Seq = TextUtils.isEmpty(mRankingDate.group7seq)?"0":mRankingDate.group7seq;
				mGroupSeq = TextUtils.isEmpty(mRankingDate.group1seq)?"0":mRankingDate.group1seq;

				OrgnizeMemberPKInfo orgnizeMemberPKInfo = new OrgnizeMemberPKInfo();

				int resOrgmize = DataSyn.getInstance().getOrgnizeMembersPkInfo7Day(mPhoneNum, mPassword, ClubId, Integer.valueOf(mOrgnizemem7Seq), Integer.valueOf(mOrgnizemem7Seq), orgnizeMemberPKInfo);
				// int resOrgmize =
				// DataSyn.getInstance().getOrgnizeMembersPkInfo(
				// mPhoneNum, mPassword, Integer.valueOf(mOrgnizemem7Seq),
				// Integer.valueOf(mOrgnizemem7Seq), orgnizeMemberPKInfo);
				if (resOrgmize != 0) {
					return false;
				}
				OrgnizeMemberInfo stepnum = null;
				if(orgnizeMemberPKInfo.orgnizemember !=null && orgnizeMemberPKInfo.orgnizemember.size() > 0){
					stepnum = orgnizeMemberPKInfo.orgnizemember.get(0);
				}else{
					stepnum= new OrgnizeMemberInfo();
					stepnum.membername = mRankingDate.membername;
					stepnum.avatar = mRankingDate.avatar;
					stepnum.memberinforev1 = "0";
				}
				mMembername = stepnum.membername;
				// mMember7avgstep = stepnum.member7avgstep;
				mSex = stepnum.memberinforev1;
				if (!TextUtils.isEmpty(stepnum.avatar)) {
					// mAvatericonPath =
					// Environment.getExternalStorageDirectory() +
					// "/ishang_image/" + stepnum.avatar + ".jpg";
					mAvatericonPath = stepnum.avatar;
				}
			} else {
				Logger.i(TAG, "getOrgnizememSeq error");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	/**
	 * needUpdate: company: CMCC-CMRI
	 * 
	 * @author Gaofei 2013-9-3 ����5:24:16 version 1.0 describe: �Ƿ���Ҫ����Ⱥ������
	 * @param strUpdateVersion
	 * @return
	 */
	private boolean needUpdate(String strUpdateVersion) {
		if (mUpdateVersion == null) {
			mUpdateVersion = new GroupRankUpdateVersion();
		}
		try {
			int res = DataSyn.getInstance().getGroupRankUpdateVersion(mPhoneNum, mPassword, ClubId, mUpdateVersion);
			if (res == 0) {
				if (mUpdateVersion != null && mUpdateVersion.result != null) {

					mCurrentTime = Common.getRankUpdateYYYYMMDD();
					if (!strUpdateVersion.equals(mCurrentTime + "_" + mUpdateVersion.result)) {
						// strUpdateVersion = mUpdateVersion.result;
						return true;
					}
				} else {
					return false;
				}
			} else {

			}
		} catch (Exception e) {
			Logger.e(TAG, e.getMessage());
			return false;
		}
		return false;
	}

	public boolean updateGroupData() {
		mCurrentTime = Common.getRankUpdateYYYYMMDD();
		if (mUpdateVersion != null) {
			sstrUpdateVersion = mCurrentTime + "_" + mUpdateVersion.result;
		}

		if (!updateGroupIdAndGroupName())
			return false;
//			mBooleanGetMemeberInfo = false;
//			mBooleanGetPKInfo = false;
//			mBooleanGetOrgnizeInfo = false;
//			mBooleanGetOrgSeq = false;
			

		if(!getOrgnizememSeq())
			return false;
	
		if(!updateGroupPKInfo())
			return false;
		if(!updateOrgnizeMemeberInfo(FIRSTITEM, LASTITEM))
			return false;
		if(!updateGroupMemeberInfo())
			return false;

		updateMyRankInfo();
		return true;

		// if (mBooleanGetMemeberInfo&& mBooleanGetPKInfo &&
		// mBooleanGetOrgnizeInfo && mBooleanGetOrgSeq) {
		// // updateGroupMemeberInfo �ɹ�������±���id��ˢ��ʱ��
		// String currentTime = Common.getDateAsYYYYMMDD(new
		// Date().getTime());// yyyyMMdd
		// Logger.i(TAG, "currentTime==" + currentTime);
		// Editor sharedata = getSharedPreferences(SharedPreferredKey.SharedPrefenceName,
		// Context.MODE_PRIVATE).edit();
		// sharedata.putString("INPK_UPDATE_TIME_Z" + mGroupId, currentTime);
		// sharedata.putString("GROUP_UPDATE_VERSION", sstrUpdateVersion);//
		// ʱ���ж�
		// sharedata.commit();
		// }

	}

	// ��ȡ�ҵ�������Ϣ
	// private void getRankingSeq() {
	// mRankingDate = new RankingDate();
	// if (DataSyn.getInstance().getPedGroupSeq(mPhoneNum, mPassword,
	// mRankingDate) != 0) {
	// Logger.i(TAG, "getRankingSeq ��ȡ����");
	// return;
	// }
	// }

	/**
	 * �������������Id��Name
	 * 
	 * @return 0 ��ȡ�ɹ� -1 ��ȡʧ��
	 */
	private boolean updateGroupIdAndGroupName() {
		try {
			GroupIdInfo groupIdInfo = new GroupIdInfo();
			if (DataSyn.getInstance().getGroupIdInfo(mPhoneNum, mPassword, ClubId, groupIdInfo) != 0) {
				Logger.i(TAG, "groupIdInfo ��ȡ����");
				return false;
			}
			mGroupId = groupIdInfo.groupid;
			mGroupName = groupIdInfo.groupname;

		} catch (Exception e) {
			Logger.e(TAG, e.getMessage());
			return false;
		}
		return true;
	}

	private boolean updateGroupMemeberInfo() {
		try {
			GroupMemberPkInfo groupMemberPkInfo = new GroupMemberPkInfo();

			if (DataSyn.getInstance().getGroupMembersPkInfo7Day(mPhoneNum, mPassword, ClubId, mGroupId, FIRSTITEM, 100, groupMemberPkInfo) != 0) {
				Logger.i(TAG, "GroupMemberPkInfo ��ȡ����");
				return false;
			}

			// ����
			GroupMemberPkInfo groupMemberPkInfoYesterday = new GroupMemberPkInfo();
			String yesterday = Common.getYesterdayAsYYYYMMDD(new Date().getTime());
			if (DataSyn.getInstance().getGroupMembersPkInfoYestoday(mPhoneNum, mPassword, ClubId, mGroupId, FIRSTITEM, 100, groupMemberPkInfoYesterday, yesterday, yesterday) != 0) {
				Logger.i(TAG, "GroupMemberPkInfoYesterday ��ȡ����");
				return false;
			}

			// ������ݿ�
			MHealthProviderMetaData.GetMHealthProvider(mActivity).deleteMemberPkInfo(ClubId);
			MHealthProviderMetaData.GetMHealthProvider(mActivity).deleteGroupInPkInfoByGroupId(mGroupId, ClubId);
			// ������ݿ�
			for (int i = 0; i < groupMemberPkInfo.groupmember.size(); i++) {
				groupMemberPkInfo.groupmember.get(i).memberinforev2 = String.valueOf(Constants.GROUP_7DAY);
			}

			for (int i = 0; i < groupMemberPkInfoYesterday.groupmember.size(); i++) {
				groupMemberPkInfoYesterday.groupmember.get(i).memberinforev2 = String.valueOf(Constants.GROUP_YESTERDAY);
			}

			MHealthProviderMetaData.GetMHealthProvider(mActivity).insertMemberPkInfo(groupMemberPkInfo, ClubId);
			MHealthProviderMetaData.GetMHealthProvider(mActivity).insertMemberPkInfo(groupMemberPkInfoYesterday, ClubId);

			MHealthProviderMetaData.GetMHealthProvider(mActivity).insertGroupInPkInfo(groupMemberPkInfo, mGroupId, mGroupName, Constants.GROUP_7DAY + "", ClubId);// 1
			MHealthProviderMetaData.GetMHealthProvider(mActivity).insertGroupInPkInfo(groupMemberPkInfoYesterday, mGroupId, mGroupName, Constants.GROUP_YESTERDAY + "", ClubId);// 0

		} catch (Exception e) {
			Logger.e(TAG, e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * @ �����������ݲ��Ҳ������ݿ� mFirstItem ��ѯ�ĵ�һ������ mLastItem ��ѯ�����һ������
	 * 
	 * @author qjj 2013-1-18
	 */
	private boolean updateGroupPKInfo() {
		try {
			GroupPkInfo groupPkInfo = new GroupPkInfo();
			if (DataSyn.getInstance().getGroupPkInfo7(mPhoneNum, mPassword, ClubId, mGroupId, FIRSTITEM, 100, groupPkInfo) != 0) {
				Logger.i(TAG, "GroupPKInfo ��ȡ����");
				return false;
			}

			// ��ȡ��������
			String yesterday = Common.getYesterdayAsYYYYMMDD(new Date().getTime());
			GroupPkInfo groupPkInfoYesterday = new GroupPkInfo();
			if (DataSyn.getInstance().getGroupPkInfoYestoday(mPhoneNum, mPassword, ClubId, mGroupId, FIRSTITEM, 100, groupPkInfoYesterday, yesterday, yesterday) != 0) {
				Logger.i(TAG, "GroupPKInfoYesterday ��ȡ����");
				return false;
			}
			// ������ݿ�
			MHealthProviderMetaData.GetMHealthProvider(mActivity).deleteGroupPkInfo(ClubId);

			for (int i = 0; i < groupPkInfo.grouppkdata.size(); i++) {
				groupPkInfo.grouppkdata.get(i).groupinforev2 = String.valueOf(Constants.GROUP_7DAY);
			}

			for (int i = 0; i < groupPkInfoYesterday.grouppkdata.size(); i++) {
				groupPkInfoYesterday.grouppkdata.get(i).groupinforev2 = String.valueOf(Constants.GROUP_YESTERDAY);
			}

			// ������ݿ�
			MHealthProviderMetaData.GetMHealthProvider(mActivity).insertGroupPkInfo(groupPkInfo, ClubId);

			MHealthProviderMetaData.GetMHealthProvider(mActivity).insertGroupPkInfo(groupPkInfoYesterday, ClubId);

		} catch (Exception e) {
			Logger.e(TAG, e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * myrank���ݵĸ���
	 */
	private void updateMyRankInfo() {
		Logger.i(TAG, "myrank������");

		// ===================================================
		// ȫ����Ա������
		// ===================================================
		OrgnizeMemberPKInfo orgnizeMemberPKInfo = new OrgnizeMemberPKInfo();
		OrgnizeMemberPKInfo orgnizeMemberPKInfoYesterDay = new OrgnizeMemberPKInfo();

		Logger.i("TAG", "mOrgnizemem7Seq = " + mOrgnizemem7Seq + " mOrgnizemem7Seq " + mOrgnizemem7Seq);

		DataSyn.getInstance().getOrgnizeMembersPkInfo7Day(RankFragment.mPhoneNum, RankFragment.mPassword, ClubId, Integer.parseInt(mOrgnizemem7Seq) - 20, Integer.parseInt(mOrgnizemem7Seq) + 20, orgnizeMemberPKInfo);
		DataSyn.getInstance().getOrgnizeMembersPkInfoYestoday(RankFragment.mPhoneNum, RankFragment.mPassword, ClubId, Integer.parseInt(mOrgnizememSeq) - 20, Integer.parseInt(mOrgnizememSeq) + 20, orgnizeMemberPKInfoYesterDay, "", "");

		MHealthProviderMetaData getMHealthProvider = MHealthProviderMetaData.GetMHealthProvider(mActivity);
		getMHealthProvider.MyRankDeleteData(ClubId);

		if (orgnizeMemberPKInfo.orgnizemember != null) {
			List<OrgnizeMemberInfo> orgnizemember = orgnizeMemberPKInfo.orgnizemember;
			getMHealthProvider.MyRankInsertValue(orgnizemember, "7", ClubId);
		}

		if (orgnizeMemberPKInfoYesterDay.orgnizemember != null) {
			List<OrgnizeMemberInfo> orgnizemember = orgnizeMemberPKInfoYesterDay.orgnizemember;
			getMHealthProvider.MyRankInsertValue(orgnizemember, "1", ClubId);
		}

		// ===================================================
		// ȫ��Ⱥ�������
		// ===================================================

		String yesterday = Common.getYesterdayAsYYYYMMDD(new Date().getTime());

		GroupPkInfo groupPkInfo = new GroupPkInfo();
		GroupPkInfo groupPkInfoYesterDay = new GroupPkInfo();
		DataSyn.getInstance().getGroupPkInfo7(RankFragment.mPhoneNum, RankFragment.mPassword, ClubId, mGroupId, Integer.parseInt(mGroup7Seq) - 20, Integer.parseInt(mGroup7Seq) + 20, groupPkInfo);
		DataSyn.getInstance().getGroupPkInfoYestoday(RankFragment.mPhoneNum, RankFragment.mPassword, ClubId, mGroupId, Integer.parseInt(mGroupSeq) - 20, Integer.parseInt(mGroupSeq) + 20, groupPkInfoYesterDay, yesterday, yesterday);

		if (groupPkInfo.grouppkdata != null) {
			List<GroupInfo> grouppkdata = groupPkInfo.grouppkdata;
			getMHealthProvider.MyRankInsertValueGroup(grouppkdata, "27" , ClubId);
		}

		if (groupPkInfoYesterDay.grouppkdata != null) {
			List<GroupInfo> grouppkdata = groupPkInfoYesterDay.grouppkdata;
			getMHealthProvider.MyRankInsertValueGroup(grouppkdata, "21" , ClubId);
		}

		// ===================================================
		// ������Ա������
		// ===================================================

		GroupMemberPkInfo groupMemberPkInfo = new GroupMemberPkInfo();
		GroupMemberPkInfo groupMemberPkInfoYesterDay = new GroupMemberPkInfo();

		Logger.i("TAG", "mGroupmem7Seq = " + mGroupmem7Seq + " mGroupmemSeq " + mGroupmemSeq);
		DataSyn.getInstance().getGroupMembersPkInfo7Day(RankFragment.mPhoneNum, RankFragment.mPassword, ClubId, mGroupId, Integer.parseInt(mGroupmem7Seq) - 20, Integer.parseInt(mGroupmem7Seq) + 20, groupMemberPkInfo);
		DataSyn.getInstance().getGroupMembersPkInfoYestoday(RankFragment.mPhoneNum, RankFragment.mPassword, ClubId, mGroupId, Integer.parseInt(mGroupmemSeq) - 20, Integer.parseInt(mGroupmemSeq) + 20, groupMemberPkInfoYesterDay, yesterday, yesterday);

		if (groupMemberPkInfo.groupmember != null) {
			List<GroupMemberInfo> groupmember = groupMemberPkInfo.groupmember;
			getMHealthProvider.MyRankInsertValueGP(groupmember, "17", ClubId);
		}
		if (groupMemberPkInfoYesterDay.groupmember != null) {
			List<GroupMemberInfo> groupmember = groupMemberPkInfoYesterDay.groupmember;
			getMHealthProvider.MyRankInsertValueGP(groupmember, "11", ClubId);
		}

	}

	public boolean updateOrgnizeMemeberInfo(int firstItem, int lastItem) {
		try {
			OrgnizeMemberPKInfo orgnizeMemberPKInfo7Day = new OrgnizeMemberPKInfo();
			OrgnizeMemberPKInfo orgnizeMemberPKInfoYesterDay = new OrgnizeMemberPKInfo();

			if (DataSyn.getInstance().getOrgnizeMembersPkInfo7Day(mPhoneNum, mPassword, ClubId, firstItem, lastItem, orgnizeMemberPKInfo7Day) != 0) {
				Logger.i(TAG, "getOrgnizeMembersPkInfo ��ȡ����");
				return false;
			}

			String yesterday = Common.getYesterdayAsYYYYMMDD(new Date().getTime());
			if (DataSyn.getInstance().getOrgnizeMembersPkInfoYestoday(mPhoneNum, mPassword, ClubId, firstItem, lastItem, orgnizeMemberPKInfoYesterDay, yesterday, yesterday) != 0) {
				Logger.i(TAG, "getOrgnizeMembersPkInfo ��ȡ����");
				return false;
			}

			// ������ݿ�
			if (firstItem == 1) {
				// ����Ǹ������ݿ��е�һ�����ݣ�����ɾ�����ݿ�������ݡ�
				MHealthProviderMetaData.GetMHealthProvider(mActivity).deleteOrgizePkInfo(ClubId);
			}

			for (int i = 0; i < orgnizeMemberPKInfo7Day.orgnizemember.size(); i++) {
				orgnizeMemberPKInfo7Day.orgnizemember.get(i).memberinforev2 = String.valueOf(Constants.GROUP_7DAY);
			}

			for (int i = 0; i < orgnizeMemberPKInfoYesterDay.orgnizemember.size(); i++) {
				orgnizeMemberPKInfoYesterDay.orgnizemember.get(i).memberinforev2 = String.valueOf(Constants.GROUP_YESTERDAY);
			}

			// ������ݿ�
			MHealthProviderMetaData.GetMHealthProvider(mActivity).insertOrgnizeMemberPkInfo(orgnizeMemberPKInfo7Day, ClubId);
			MHealthProviderMetaData.GetMHealthProvider(mActivity).insertOrgnizeMemberPkInfo(orgnizeMemberPKInfoYesterDay, ClubId);

		} catch (Exception e) {
			Logger.e(TAG, e.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public void onArticleSelected(Object obj) {
		switch ((Integer) obj) {
		case RankFragment.GROUP_MEMBER_PK:
			mViewPagerRank.setCurrentItem(RankFragment.GROUP_MEMBER_PK);
			break;

		default:
			break;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// ����������
		case R.id.ar_search_button:
			if (mResultlist.size() == 0 || mGroupResultlist.size() == 0) {
				BaseToast("����׼���������ݣ����Ժ�����~");
				getSearchDatas();
				return;
			}
			buildSearchDialog();
			mImageButtonTitle.setVisibility(View.GONE);
			mBack.setVisibility(View.GONE);
			mSearchButton.setVisibility(View.GONE);

			break;
		case R.id.button_input_bg_back:
			if (mIsBack) {
				resetSearchButton();
			} else {
				showMenu();
			}
			break;
		}
	}

	private void resetSearchButton() {
		if (mCurrIndex < 2) {
			RankChildFragment rlvf0 = (RankChildFragment) mViewPagerRank.getAdapter().instantiateItem(mViewPagerRank, mCurrIndex);
			if (rlvf0 == null)
				return;
			rlvf0.setSearchData(false, null);
		}
		mIsBack = false;
		mBack.setBackgroundResource(R.drawable.slidemenu_button);
		if(!mSYallowtoshow){
			getOrgnizedMenSum();
		}
//		mSearchButton.setBackgroundResource(R.drawable.white_search);// ���ó�������ť
		mSearchButton.setVisibility(View.VISIBLE);
	}

	private ProgressDialog mProgressDialog;

	// ��ʾ������dialog
	private void buildSearchDialog() {
		closeBigFaces();

		customDialog = new Dialog(mActivity, R.style.dialog_fullscreen);
		customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		customDialog.setCanceledOnTouchOutside(true);
		final View view = View.inflate(mActivity, R.layout.alertdialog_searchbar, null);

		final MyAutoCompleteTextView acltv = (MyAutoCompleteTextView) view.findViewById(R.id.as_et1_keyword);
		acltv.setThreshold(0);

		final CommonAutoCompleteAdapter adapter = new CommonAutoCompleteAdapter(mActivity, mResultlist, mGroupResultlist, -1, mCurrIndex);
		acltv.setAdapter(adapter);
		acltv.setOnNameReturnedListener(new OnNameReturnedListener() {
			@Override
			public void onReturned(String name, final String phone) {
				// �����ҽ����dialog
				mProgressDialog = Common.initProgressDialog("������...", mActivity);
				new Thread() {
					public void run() {
						// TODO��Ⱥ�������ӿڡ�
						SearchDate reqData = new SearchDate();
						int rst = -1;
						switch (mCurrIndex) {
						case 0:
							rst = DataSyn.getInstance().getSeqsBySearchNumber(mPhoneNum, mPassword, ClubId, phone, reqData);
							break;
						case 1:
							rst = DataSyn.getInstance().getGroupSeqsBySearchNumber(mPhoneNum, mPassword, ClubId, phone, reqData);
							reqData.groupid = phone;
							break;
						}
						if(rst != 0){
							handler.sendEmptyMessage(65535);
						}

						Message msg = Message.obtain();
						Bundle data = new Bundle();
						data.putParcelable("reqData", reqData);
						msg.setData(data);
						msg.what = rst;
						handler.sendMessage(msg);
					};
				}.start();
			}
		});

		TextView cmtdsd = (TextView) view.findViewById(R.id.click_me_to_dismiss_search_dialog);
		cmtdsd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				customDialog.dismiss();
			}
		});
		customDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				mSearchButton.setVisibility(View.VISIBLE);
				mImageButtonTitle.setVisibility(View.VISIBLE);
				mBack.setVisibility(View.VISIBLE);
			}
		});

		Window window = customDialog.getWindow();
		window.setGravity(Gravity.TOP); // �˴���������dialog��ʾ00 ��λ��
		window.setWindowAnimations(R.style.mystyletop); // ��Ӷ���
		view.setBackgroundColor(Color.TRANSPARENT);
		customDialog.setContentView(view);
		customDialog.show();
	}

	private void closeBigFaces() {
		RankChildFragment rlvf0 = (RankChildFragment) mViewPagerRank.getAdapter().instantiateItem(mViewPagerRank, mCurrIndex);
		if (rlvf0 == null)
			return;
		rlvf0.closeBigFace();
	}

	@Override
	public void findViews() {
		initView();
		getSharedInfo();

		updateAllGroupInfo();
		setRankTitle(mCurrType);
	}

	@Override
	public void clickListner() {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadLogic() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void onDestroy() {
	    try{
            super.onDestroy();
	        destroyAllItem();
		}catch(Exception e){
   
		}
	}
	
	//���ڴ��е�fragment����������ٴν�������ʱ����������
	public void destroyAllItem() {
		for (int i = 0; i < 3; i++) {
			Object objectobject = (RankChildFragment) mViewPagerRank.getAdapter().instantiateItem(mViewPagerRank, i);
			if (objectobject != null)
				destroyItem(mViewPagerRank, i, objectobject);
		}
	}

	public void destroyItem(ViewGroup container, int position, Object object) {
		FragmentManager manager = ((Fragment) object).getFragmentManager();
		FragmentTransaction trans = manager.beginTransaction();
		trans.remove((Fragment) object);
		Logger.d("testing", "remove action =" + position);
		//
//        trans.commitAllowingStateLoss();
        try{
            trans.commit();
        }catch(Exception e){
   
        }
	}
}
