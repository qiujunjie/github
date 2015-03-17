package cmcc.mhealth.slidingcontrol;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cmcc.mhealth.R;
import cmcc.mhealth.activity.FriendPedometerInfoActivity;
import cmcc.mhealth.basic.BaseFragment;
import cmcc.mhealth.bean.AcceptFriendRequestInfo;
import cmcc.mhealth.bean.BackInfo;
import cmcc.mhealth.bean.FriendPedometorSummary;
import cmcc.mhealth.bean.FriendsInfo;
import cmcc.mhealth.bean.OrgnizeMemberInfo;
import cmcc.mhealth.bean.OrgnizeMemberPKInfo;
import cmcc.mhealth.bean.RankingDate;
import cmcc.mhealth.common.CatchUserDialog;
import cmcc.mhealth.common.CatchUserDialog.UserCapturedListener;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.ConstantsBitmaps;
import cmcc.mhealth.common.Encrypt;
import cmcc.mhealth.common.ImageUtil;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.common.SendingPushMessags;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.db.MHealthProviderMetaData;
import cmcc.mhealth.net.DataSyn;
import cmcc.mhealth.view.RoundAngleImageView;
import cmcc.mhealth.view.ScoreBarView;
import cmcc.mhealth.view.XListView;
import cmcc.mhealth.view.XListView.IXListViewListener;

public class FriendFragment extends BaseFragment implements OnClickListener, IXListViewListener {
	private final static String TAG = "FriendFragment";

	private ImageView mBack;
	private TextView mTextViewTitle;

	private boolean mRefresh = false;
	private SharedPreferences mSharedInfo;
	private String mPhoneNum;
	private String mPassword;
	private FriendsInfo mOrgnizeMemberPKInfo;
	private MHealthProviderMetaData mMHealthProvider;

	private XListView mMainListView;
	private MySimpleAdapter adapter;
	private View mHeadView;
	private LinearLayout mHeadViewLL;
	private TextView mTextViewMyGroup;
	private TextView mTextViewMyStep;
	private TextView mTextViewRanking;
	private ImageView mIcon;
	private ImageView mHorizonline;
	private RoundAngleImageView mImageViewAvater;

	private RankingDate mRankingDate;
	private String mAvaterName;
	private String mMembername;
	private String mSex;
	private List<OrgnizeMemberInfo> myFriends;
	private boolean hasnoFriend;
	private RelativeLayout mAddFriend;

	private String fp;
	private String membername;
	private FriendPedometorSummary fpsReqData;
	private int mPosition;

	private CatchUserDialog mCatchDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_friend, container, false);
		super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		return view;
	}

	private void initView() {
		hasnoFriend = false;

		mMHealthProvider = MHealthProviderMetaData.GetMHealthProvider(mActivity);

		mBack = findView(R.id.button_input_bg_back);
		mBack.setVisibility(View.VISIBLE);
		mBack.setOnClickListener(this);

		mTextViewTitle = findView(R.id.textView_title);
		mTextViewTitle.setText(R.string.friend_title);

		mMainListView = findView(R.id.af_listview_rank);
		mMainListView.setXListViewListener(this);
		mMainListView.setPullLoadEnable(false);
		
		// bigface相关：
		mFaceRL = findView(R.id.af_listview_rl_face);
		mFaceLL = findView(R.id.af_listview_ll_face);
		mFaceIV = findView(R.id.af_listview_iv_face);
		mFaceRL.setVisibility(View.INVISIBLE);
		mFaceRL.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mFaceRL.setVisibility(View.GONE);
				isFaceShowing = false;
			}
		});
		
		mHeadView = View.inflate(mActivity, R.layout.mygroup_ranking, null);
		mHeadViewLL = (LinearLayout) mHeadView.findViewById(R.id.mgr_stepshowingll);
		mHeadViewLL.setVisibility(View.GONE);
		mTextViewMyGroup = (TextView) mHeadView.findViewById(R.id.textview_myrank_myname);
		mTextViewMyStep = (TextView) mHeadView.findViewById(R.id.textview_myStep);
		mImageViewAvater = (RoundAngleImageView) mHeadView.findViewById(R.id.iamgeview_myrank_avater);
		mTextViewRanking = (TextView) mHeadView.findViewById(R.id.textview_myrank_id);
		mTextViewRanking.setVisibility(View.GONE);
		mIcon = (ImageView) mHeadView.findViewById(R.id.imageview_aikon);
		mIcon.setVisibility(View.GONE);
		mHorizonline = (ImageView) mHeadView.findViewById(R.id.imageview_horizonline);
		mHorizonline.setVisibility(View.GONE);
		mMainListView.addHeaderView(mHeadView);

		mAddFriend = (RelativeLayout) findView(R.id.imageButton_title_add);
		mAddFriend.setOnClickListener(this);
		mAddFriend.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.findViewById(R.id.textview_title_add).setBackgroundResource(R.drawable.addfriendbutton_orange);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					v.findViewById(R.id.textview_title_add).setBackgroundResource(R.drawable.addfriendbutton);
				}
				return false;
			}
		});
	}

	@Override
	public void onResume() {
		if (mAddFriend != null) {
			mAddFriend.setVisibility(View.VISIBLE);
		}
		if (!initMySeq()) {
			getMySeq();
		}
		super.onResume();
	}

	private void autoUpdate() {
		if (mRefresh) {
			return;
		}
		new AsyncTask<Integer, Integer, String>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mRefresh = true;
			}

			protected String doInBackground(Integer... params) {
				updateGroupData();
				getMySeq();
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				Editor edit = sp.edit();
				edit.putLong(SharedPreferredKey.FRIEND_GETTIME, Common.getCurrentDayFirstTimeMills(new Date().getTime()));
				edit.commit();
				getFriendFromDB();
				showDatas();
				if (hasnoFriend) {
					BaseToast("您还没有好友，快添加好友吧！", 5);
				}
				mRefresh = false;
			}
		}.execute();
	}

	public void updateGroupData() {
		getFriendInfos();
	}

	private void getFriendInfos() {
		mOrgnizeMemberPKInfo = new FriendsInfo();
		try {
			int res = DataSyn.getInstance().getFriendsList(mPhoneNum, mPassword, mOrgnizeMemberPKInfo);
			if (res == 0) {
				mMHealthProvider.deleteMyFriend();
				mMHealthProvider.FriendInsertValue(mOrgnizeMemberPKInfo.friendslist);
				hasnoFriend = mOrgnizeMemberPKInfo.friendslist.size() == 0;
			} else {
				hasnoFriend = mMHealthProvider.getFriendCount() == 0;
				handle.sendEmptyMessage(NET_PROBLEM);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void findViews() {
		initView();
		getMySharedInfo();
		int friendNum = getFriendFromDB();
		showDatas();
		if (friendNum == 0 || 
				new Date().getTime() - sp.getLong(SharedPreferredKey.FRIEND_GETTIME, 0) > 86400000l) {
			mMainListView.startLoading(Common.getDensity(mActivity) * 60);
		} 
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
			break;
		case R.id.imageButton_title_add:// 添加好友
			if (mRefresh) {
				BaseToast("还在获取好友列表，请稍后再添加~", 5);
				return;
			}
			mCatchDialog = new CatchUserDialog(mActivity, mPhoneNum, mPassword);
			mCatchDialog.startCapture("添加好友");
			mCatchDialog.setOnUserCapturedListener(new UserCapturedListener() {
				@Override
				public void onCapturedUser(int state, String reason, String name, String avatar, String targetphone) {
					switch (state) {
					case CatchUserDialog.CAPTURE_SUCCESS:
						addFriend(targetphone);
						break;
					case CatchUserDialog.CAPTURE_FAIL:
						BaseToast(reason, 5);
						break;
					}
				}
			});
			break;
		}
	}

	protected void showMenu() {
		MainCenterActivity m = (MainCenterActivity) mActivity;
		m.showMenu();
	}

	private void showDatas() {
		adapter = new MySimpleAdapter(myFriends);
		mMainListView.setAdapter(adapter);
		mMainListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				getFriendDetailPedometers(position);
			}
		});
		mMainListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if(position == 1)return true;
				showDeleteAlertDialog(position);
				return true;
			}
		});
		resetXListView();
	}

	private int getFriendFromDB() {
		myFriends = mMHealthProvider.getMyFriends();
		if (myFriends == null)
			myFriends = new ArrayList<OrgnizeMemberInfo>();
		return myFriends.size();
	}

	// 得到朋友的简报
	private void getFriendDetailPedometers(final int position) {
		new Thread() {
			public void run() {
				if (myFriends == null)
					return;
				if (position <= 1)
					return;
				if (position > myFriends.size() + 1)
					return;

				fp = myFriends.get(position - 2).friendphone;
				membername = myFriends.get(position - 2).membername;

				fpsReqData = new FriendPedometorSummary();
				int suc = DataSyn.getInstance().getFriendInfo(mPhoneNum, mPassword, fp, fpsReqData);
				if (suc == 0) {
					handle.sendEmptyMessage(GET_DETAIL_PEDOMETERS_SUCCESS);
				} else {
					handle.sendEmptyMessage(NET_PROBLEM);
				}
			};
		}.start();
	}

	private boolean initMySeq() {
		String name = sp.getString(SharedPreferredKey.NAME, null);
		if (!TextUtils.isEmpty(mSex)) {
			if (mSex.equals("0")) {
				int name2Int = Encrypt.getIntFromName(name == null ? "0" : name);
				mImageViewAvater.setImageResource(MainCenterActivity.BASE_ATATAR[name2Int + 7]);
			} else if (mSex.equals("1")) {
				int name2Int = Encrypt.getIntFromName(name == null ? "0" : name);
				mImageViewAvater.setImageResource(MainCenterActivity.BASE_ATATAR[name2Int]);
			}
		}else {
			return false;
		}

		if (!TextUtils.isEmpty(mAvaterName)) {
			String url = DataSyn.avatarHttpURL + mAvaterName + ".jpg";
			getImageAsync(mImageViewAvater, url);
			((MainCenterActivity) mActivity).refleshMenuAvatar();
		} 
		if (!TextUtils.isEmpty(mMembername)) {
			mTextViewMyGroup.setText(mMembername);
		} else {
			return false;
		}
		String str = mRankingDate.member1info.step;
		if (!TextUtils.isEmpty(str)) {
			mTextViewMyStep.setText(str + "步");
		} else {
			mTextViewMyStep.setText("0");
			return false;
		}
		mHeadViewLL.setVisibility(View.VISIBLE);
		return true;
	}

	private void getMySharedInfo() {
		if (mRankingDate == null) {
			mRankingDate = new RankingDate();
		}
		mSharedInfo = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		mAvaterName = mSharedInfo.getString(SharedPreferredKey.AVATAR, "");
		mMembername = mSharedInfo.getString(SharedPreferredKey.NAME, "");
		mRankingDate.member1seq = mSharedInfo.getString("member1seq", "");
		mRankingDate.member1info.step = mSharedInfo.getString("memberi1Info_step", "");
		mSex = mSharedInfo.getString(SharedPreferredKey.GENDER, "0");

		mPhoneNum = mSharedInfo.getString(SharedPreferredKey.PHONENUM, null); // 拿到电话号码
		mPassword = mSharedInfo.getString(SharedPreferredKey.PASSWORD, null); // 拿到密码

		if (mPassword == null || mPhoneNum == null) {
			BaseToast("账号问题，请重新登录!");
			return;
		}
	}

	// TODO 设定clubid
	private void getMySeq() {
		new Thread() {
			public void run() {
				String club = sp.getString(SharedPreferredKey.CLUB_ID, "-1");
				int res = DataSyn.getInstance().getPedGroupSeq(mPhoneNum, mPassword, Integer.valueOf(club), mRankingDate);
				if (res == 0) {
					if (mMembername == null || "".equals(mMembername)) {
						OrgnizeMemberPKInfo orgnizeMemberPKInfoYesterDay = new OrgnizeMemberPKInfo();
						int suc = DataSyn.getInstance().getOrgnizeMembersPkInfoYestoday(mPhoneNum, mPassword, 0, Integer.parseInt(mRankingDate.member1seq), Integer.parseInt(mRankingDate.member1seq), orgnizeMemberPKInfoYesterDay, "", "");
						if (suc == 0 && orgnizeMemberPKInfoYesterDay.orgnizemember.size() > 0) {
							mMembername = orgnizeMemberPKInfoYesterDay.orgnizemember.get(0).membername;
						}
					}
					mAvaterName = mRankingDate.avatar;
					Editor sharedata = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();
					sharedata.putString("member1seq", mRankingDate.member1seq);
					sharedata.putString("memberi1Info_step", mRankingDate.member1info.step);
					sharedata.commit();
					handle.sendEmptyMessage(GET_MY_SEQ_SUCCESS);
				}else{
					handle.sendEmptyMessage(NET_PROBLEM);
				}
			}
		}.start();
	}

	class MySimpleAdapter extends BaseAdapter {
		private List<OrgnizeMemberInfo> friends;
		private int maxStep = 10000;

		public void setFriends(List<OrgnizeMemberInfo> friends) {
			this.friends = friends;
		}

		public MySimpleAdapter(List<OrgnizeMemberInfo> myFriends) {
			friends = myFriends;
			if (friends.size() > 0) {
				maxStep = Integer.parseInt(friends.get(0).member7avgstep);
			}
		}

		@Override
		public int getCount() {
			return friends.size();
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
			final ViewHolder holder;
			if (convertView == null || convertView.getTag() == null) {
				holder = new ViewHolder();
				convertView = View.inflate(getActivity(), R.layout.list_item_rank, null);
				holder.mScorebar = (ScoreBarView) convertView.findViewById(R.id.regularprogressbar);
				holder.mScorebar.setPics(ConstantsBitmaps.mLeftPic, ConstantsBitmaps.mRunPicYellow);
				holder.mItemLayoutList = (LinearLayout) convertView.findViewById(R.id.linearLayout_list_item_rank);
				holder.mTextViewRankId = (TextView) convertView.findViewById(R.id.textview_rank_seq);
				holder.mImageViewRankIcon = (RoundAngleImageView) convertView.findViewById(R.id.rank_icon_name);
				holder.mImageViewRankID = (ImageView) convertView.findViewById(R.id.imageview_rankid_bg);
				holder.mImageViewRankFirst = (ImageView) convertView.findViewById(R.id.imageview_rankidfirst);
				holder.mTextViewMemberName = (TextView) convertView.findViewById(R.id.textview_member_name);
				holder.mTextViewGroupName = (TextView) convertView.findViewById(R.id.textview_group_name);
				holder.mTextViewGroupName.setVisibility(View.GONE);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
				holder.mScorebar.setPics(ConstantsBitmaps.mLeftPic, ConstantsBitmaps.mRunPicYellow);
			}

			OrgnizeMemberInfo friend = friends.get(position);

			// 前三名黄色 + 交替颜色的背景↓↓↓↓↓↓======================
			holder.mImageViewRankFirst.setVisibility(View.GONE);
			if (position < 3) {
				// 三名以后显示绿色
				holder.mImageViewRankID.setBackgroundResource(R.drawable.rank_id_green);
				holder.mScorebar.setPics(ConstantsBitmaps.mLeftPic, ConstantsBitmaps.mRunPicGreen);
			} else {
				holder.mImageViewRankID.setBackgroundResource(R.drawable.rank_id_org);
			}
			if ((position & 1) == 1)
				holder.mItemLayoutList.setBackgroundResource(R.drawable.listitem_click_bg);// 186,216,255
			else
				holder.mItemLayoutList.setBackgroundResource(R.drawable.listitem_click_bg2);
			holder.mTextViewRankId.setText((position + 1) + "");

			// 第一名王冠 前三名黄色 + 交替颜色的背景↑↑↑↑↑↑ =================

			// 头像部分↓↓↓↓↓↓======================
			String avater = friend.memberinforev1;
			String member = friend.membername;
			if (!TextUtils.isEmpty(avater) && avater.equals("1")) {
				int name2Int = Encrypt.getIntFromName(member == null ? "0" : member);
				holder.mImageViewRankIcon.setImageResource(MainCenterActivity.BASE_ATATAR[name2Int]);
			} else {
				int name2Int = Encrypt.getIntFromName(member == null ? "0" : member);
				holder.mImageViewRankIcon.setImageResource(MainCenterActivity.BASE_ATATAR[name2Int+7]);
			}
			
			holder.mImageViewRankIcon.setTag(position + "0");

			String imageName = friend.avatar;
			if (!TextUtils.isEmpty(imageName)) {
				getImageAsync(holder.mImageViewRankIcon, DataSyn.avatarHttpURL + imageName + ".jpg", position + "0");
			}
			// 头像部分↑↑↑↑↑↑=================

			// 名字，组名部分↓↓↓↓↓↓======================
			holder.mTextViewMemberName.setText(friend.membername);
			// 名字，组名部分↑↑↑↑↑↑=================

			// 分数条↓↓↓↓↓↓======================
			holder.mScorebar.setMaxValue(maxStep);
			holder.mScorebar.setScore(Integer.parseInt(friend.member7avgstep));
			holder.mScorebar.setTypeface(Typeface.DEFAULT_BOLD);// 字体类型加粗
			holder.mScorebar.reDraw();
			// 分数条↑↑↑↑↑↑=================

			// 大头像↓↓↓↓↓↓======================
			final View tempview = convertView;
			holder.mImageViewRankIcon.setOnClickListener(new OnClickListener() {
				int pos = position;

				@Override
				public void onClick(View arg0) {
					// 弹出大头像
					int oy = tempview.getTop();
					int ox = tempview.getLeft();
					Logger.i(TAG, "tempview.getTop() ---> " + oy);
					Logger.i(TAG, "tempview.getLeft() ---> " + ox);
					createPWforFace(pos, ox, oy);
				}

			});
			// 大头像↑↑↑↑↑↑=================
			return convertView;
		}
	}

	private class ViewHolder {
		TextView mTextViewRankId, mTextViewMemberName, mTextViewGroupName;
		ImageView mImageViewRankFirst, mImageViewRankID;
		RoundAngleImageView mImageViewRankIcon;
		LinearLayout mItemLayoutList;
		cmcc.mhealth.view.ScoreBarView mScorebar;
//		String fp;
	}

	/**
	 * bigface相关
	 */
	private static RelativeLayout mFaceRL;
	public static boolean isFaceShowing;
	private LinearLayout mFaceLL;
	private RoundAngleImageView mFaceIV;

	private void createPWforFace(int pos, int offsetX, int offsetY) {
		if (myFriends == null)
			return;
		OrgnizeMemberInfo record = myFriends.get(pos);
		if (record == null)
			return;
		Object icon = null;
		Object imageName = null;

		icon = record.memberinforev1;
		imageName = record.avatar;

		if (icon == null || imageName == null)
			return;
		if (!TextUtils.isEmpty(icon.toString()) && icon.toString().equals("1")) {
			mFaceIV.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.avatar_male_middle));
		} else {
			mFaceIV.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.avatar_female_middle));
		}
		mFaceIV.setTag("pos" + 4);

		getImageAsync(mFaceIV, DataSyn.avatarHttpURL + imageName.toString() + ".jpg", "pos" + 4);
		getImageAsync(mFaceIV, DataSyn.avatarHttpURL + imageName.toString() + ".jpg", "pos" + 4, 1);

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
		isFaceShowing = true;
	}

	public static void closebigFace() {
		if (mFaceRL.getVisibility() == View.VISIBLE) {
			mFaceRL.setVisibility(View.GONE);
			isFaceShowing = false;
		}
	}

	// 静态int
	private static final int ADD_FRIEND_SUCCESS = 0;// 添加好友成功
	private static final int ADD_FRIEND_FAIL = 1;// 添加好友失败
	private static final int NET_PROBLEM = 2;// 网络问题
	private static final int GET_DETAIL_PEDOMETERS_SUCCESS = 3;// 简报获取成功
	private static final int DELETE_RIEND_SUCCESS = 4;// 删除好友成功
	private static final int DELETE_RIEND_FAIL = 5;// 删除好友失败
	private static final int GET_MY_SEQ_SUCCESS = 6;

	Handler handle = new Handler() {
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case GET_MY_SEQ_SUCCESS:
				initMySeq();
				break;
			case ADD_FRIEND_SUCCESS:
				BaseToast("请求已经发出，请等待对方同意！", 7);
				break;
			case ADD_FRIEND_FAIL:
				BaseToast("请求发送失败，请重试", 5);
				break;
			case GET_DETAIL_PEDOMETERS_SUCCESS:
				if (fp != null) {
					Intent intent = new Intent(mActivity, FriendPedometerInfoActivity.class);
					intent.putExtra("friendphone", fp);
					intent.putExtra("membername", membername);
					intent.putExtra(SharedPreferredKey.PHONENUM, mPhoneNum);
					intent.putExtra(SharedPreferredKey.PASSWORD, mPassword);
					int size = fpsReqData.friendsinfo.size();
					for (int i = 0; i < size - 32; i++) {
						fpsReqData.friendsinfo.remove(0);
					}
					intent.putExtra("fpsReqData", fpsReqData);
					mActivity.startActivity(intent);
					mActivity.overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
				}
				break;
			case NET_PROBLEM:
				resetXListView();
				BaseToast("请确认网络畅通", 5);
				break;
			case DELETE_RIEND_FAIL:
				BaseToast("移除好友失败，请重试..", 5);
				break;
			case DELETE_RIEND_SUCCESS:
				BaseToast("已将该好友移除", 8);
				if (adapter == null) {
					autoUpdate();
				} else {
					adapter.setFriends(myFriends);
					adapter.notifyDataSetChanged();
				}
				break;
			}
		};
	};

	// 添加好友
	private void addFriend(final String targetphone) {
		new Thread() {
			public void run() {
				BackInfo AfireqData = new BackInfo();
				int suc = DataSyn.getInstance().addFriendById(mPhoneNum, mPassword, targetphone, AfireqData);
				if (suc == 0 && "等待对方同意".equals(AfireqData.reason)) {
					handle.sendEmptyMessage(ADD_FRIEND_SUCCESS);
				} else {
					handle.sendEmptyMessage(ADD_FRIEND_FAIL);
				}
			};
		}.start();
	}

	private void showDeleteAlertDialog(int position) {
		mPosition = position;
		Builder builder = new Builder(mActivity);
		builder.setTitle("您要移除这位好友吗？");
		builder.setPositiveButton("是的", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteFirend();
			}

		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}

	// 删除好友
	private void deleteFirend() {
		new Thread() {
			public void run() {
				if (myFriends == null)
					myFriends = mMHealthProvider.getMyFriends();
				if (mPosition < 2 ||mPosition > myFriends.size() + 1) {
					handle.sendEmptyMessage(DELETE_RIEND_FAIL);
					return;
				}
				AcceptFriendRequestInfo afri = new AcceptFriendRequestInfo();
				int suc = DataSyn.getInstance().deleteFriend(mPhoneNum, mPassword, myFriends.get(mPosition - 2).friendphone, afri);
				if (suc == 0) {
					mMHealthProvider.deleteMyFriend(myFriends.get(mPosition - 2).friendphone);
					myFriends.remove(mPosition - 2);
					handle.sendEmptyMessage(DELETE_RIEND_SUCCESS);
				} else {
					handle.sendEmptyMessage(NET_PROBLEM);
				}
			};
		}.start();
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

	@Override
	public void onRefresh() {
		autoUpdate();
	}
	
	private void resetXListView() {
		mMainListView.stopRefresh();
		mMainListView.stopLoadMore();
		mMainListView.setRefreshTime(Common.getDateAsM_d(new Date().getTime()));
	}

	@Override
	public void onLoadMore() {
	}
}
