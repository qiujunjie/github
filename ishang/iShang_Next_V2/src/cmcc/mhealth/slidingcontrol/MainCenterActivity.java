package cmcc.mhealth.slidingcontrol;

import java.util.List;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.widget.Toast;
import cmcc.mhealth.R;
import cmcc.mhealth.basic.BaseFragment;
import cmcc.mhealth.basic.MapApplication;
import cmcc.mhealth.basic.BaseFragment.OnFragmentDestroyListener;
import cmcc.mhealth.bean.ClubData;
import cmcc.mhealth.bean.ClubListInfo;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Constants;
import cmcc.mhealth.common.ConstantsBitmaps;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.common.StringUtils;
import cmcc.mhealth.net.DataSyn;
import cmcc.mhealth.slidingcontrol.RankChildFragment.OnArticleSelectedListener;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.baidu.mapapi.BMapManager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenedListener;

public class MainCenterActivity extends BaseSildingActivity implements OnArticleSelectedListener, OnOpenedListener {
	public static final int[] BASE_ATATAR = { R.drawable.a, R.drawable.b, 
		R.drawable.c, R.drawable.d, 
		R.drawable.e, R.drawable.f, 
		R.drawable.g, R.drawable.h, 
		R.drawable.i, R.drawable.g, R.drawable.k,
			R.drawable.l, R.drawable.n, R.drawable.m, R.drawable.o, };
	private Fragment mContent;
	private static final String TAG = "MainCenterActivity";

	public MainCenterActivity() {
		super(R.string.brief_textview_timelong_text);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setPushActive();
		registerMessageReceiver();
		Logger.i(TAG, "MainCenterActivity is start");
		// set the Above View
		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
		if (mContent == null)
			mContent = new WebViewFragment("http://phr.cmri.cn/datav2/account.do?action=medicineList");
		// set the Above View
		setContentView(R.layout.content_frame);
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mContent).commit();

		// set the Behind View
		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, new MenuFragment(), "menuFragment").commit();

		getSlidingMenu().setSecondaryMenu(R.layout.menu_frame_two);
		getSlidingMenu().setSecondaryShadowDrawable(R.drawable.shadowright);
//		getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_two, new RightListFragment()).commit();
		
		// customize the SlidingMenu
		getSlidingMenu().setMode(SlidingMenu.LEFT);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

		getSlidingMenu().setTouchmodeMarginThreshold(((int)Common.getDensity(this)) * 80);
		
		//更新menu列表
		loadClubList();
		
		getSlidingMenu().setOnOpenListener(new SlidingMenu.OnOpenListener() {
			@Override
			public void onOpen() {
				Logger.i(TAG, "opening");
				Constants.OPENED = true;
				MenuFragment.mCanBeClick = true;
//				((MenuFragment)MainCenterActivity.this.getContent()).getMenuAvater();
			}
		});
		
		getSlidingMenu().setOnCloseListener(new SlidingMenu.OnCloseListener() {
			@Override
			public void onClose() {
				Logger.i(TAG, "closing");
				Constants.OPENED = false;
			}
		});
		
		getSlidingMenu().setSecondaryOnOpenListner(new SlidingMenu.OnOpenListener() {
			@Override
			public void onOpen() {
				Logger.i(TAG, "opening");
				Constants.OPENED = true;
			}
		});
		
		getSlidingMenu().setOnOpenedListener(this);
		ConstantsBitmaps.initRunPics(MainCenterActivity.this);

	}
	
	// 推送注册
	private void setPushActive() {
		sp = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		String userphone = sp.getString(SharedPreferredKey.PHONENUM, null);
		if (!TextUtils.isEmpty(userphone)) {
			JPushInterface.setDebugMode(true); // 设置开启日志,发布时请关闭日志
			if (!JPushInterface.isPushStopped(this)) {
				JPushInterface.init(this); // 初始化 JPush
			} else {
				Logger.d("TS", "setPushActive --> already actived : resumePush");
				JPushInterface.resumePush(this);
			}
			Logger.d("TS Registerid", "reg = " + StringUtils.toMD5(userphone));
			JPushInterface.setAliasAndTags(this, StringUtils.toMD5(userphone), null, new TagAliasCallback() {
				@Override
				public void gotResult(int arg0, String arg1, Set<String> arg2) {
					Logger.d("TS", "returnd code = " + arg0);
				}
			});
		} else {
			Logger.d("TS", "setPushActive fail --> phonenum get fail");
		}
	}

	private SharedPreferences sp;
	private void loadClubList() {
		final ClubListInfo clireqData = new ClubListInfo();
		if (getClubsFromSP()) {
			new Thread() {
				public void run() {
					String serverversion = sp.getString(SharedPreferredKey.SERVER_VERSION, "2");
					String phonenum = sp.getString(SharedPreferredKey.PHONENUM, null);
					String password = sp.getString(SharedPreferredKey.PASSWORD, null);
					if ("2".equals(serverversion)) {
						int suc = DataSyn.getInstance().getClubList(phonenum, password, clireqData);
						if (suc == 0) {
							saveClubsToSP(clireqData.clublist);
						} else {
							handler.sendEmptyMessage(-1);
						}
					} else {
						clireqData.clublist.add(new ClubData(0, "公司"));
						saveClubsToSP(clireqData.clublist);
					}
				};
			}.start();
		}
	}
	//是否需要更新列表
	private boolean getClubsFromSP() {
		SharedPreferences sp = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		String clublist = sp.getString("ClubList", null);
		if (clublist == null || "".equals(clublist)) {
			return true;
		} else {
			return false;
		}
	}
	
	private void saveClubsToSP(List<ClubData> clublist) {
		Editor edit = sp.edit();
		StringBuilder sb = new StringBuilder();
		for (ClubData clubData : clublist) {
			sb.append("#").append(clubData.clubname).append("@").append(clubData.clubid);
		}
		edit.putString("ClubList", sb.substring(1));
		edit.commit();
	}
	
	@Override
	protected void onDestroy() {
		if(ConstantsBitmaps.mLeftPic !=null && !ConstantsBitmaps.mLeftPic.isRecycled()){
			ConstantsBitmaps.mLeftPic.recycle();
			ConstantsBitmaps.mLeftPic = null;
		}
		if(ConstantsBitmaps.mRunPicYellow !=null && !ConstantsBitmaps.mRunPicYellow.isRecycled()){
			ConstantsBitmaps.mRunPicYellow.recycle();
			ConstantsBitmaps.mRunPicYellow = null;
		}
		if(ConstantsBitmaps.mRunPicGreen !=null && !ConstantsBitmaps.mRunPicGreen.isRecycled()){
			ConstantsBitmaps.mRunPicGreen.recycle();
			ConstantsBitmaps.mRunPicGreen = null;
		}
		if(ConstantsBitmaps.mRunPicBlue !=null && !ConstantsBitmaps.mRunPicBlue.isRecycled()){
			ConstantsBitmaps.mRunPicBlue.recycle();
			ConstantsBitmaps.mRunPicBlue = null;
		}
		if(ConstantsBitmaps.mRunPicDouble !=null && !ConstantsBitmaps.mRunPicDouble.isRecycled()){
			ConstantsBitmaps.mRunPicDouble.recycle();
			ConstantsBitmaps.mRunPicDouble = null;
		}
		unregisterReceiver(mMessageReceiver);
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}

	public void switchContent(final BaseFragment oldContent, BaseFragment newContent) {
		mContent = newContent;
		FragmentManager sfm = getSupportFragmentManager();
		FragmentTransaction transaction = sfm.beginTransaction();
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.replace(R.id.content_frame, newContent);
		transaction.commitAllowingStateLoss();
		
		oldContent.setOnFragmentDestroyListener(new OnFragmentDestroyListener() {
			@Override
			public void onDestroy() {
				handler.sendEmptyMessageDelayed(0, 50);
			}
		});
	}
	
	private Handler handler = new Handler(){
		@Override
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case 0:
				getSlidingMenu().showContent();
				break;
			case -1:
				Toast.makeText(getApplicationContext(), "网络错误", 0).show();
				break;
			}
			super.dispatchMessage(msg);
		}
	};
	
	public Fragment getContent(){
		return mContent;
	}
	
	public void showContent(){
		getSlidingMenu().showContent();
	}

	@Override
	public void onArticleSelected(Object obj) {
		
	}
	
	public void switchFragment(final BaseFragment oldContent, BaseFragment newContent) {
		switchContent(oldContent , newContent);
	}
	
	public void refleshMenuAvatar(){
		MenuFragment menu = (MenuFragment) getSupportFragmentManager().findFragmentByTag("menuFragment");
		menu.getMenuAvater();
	}
   
	@Override
	public void onOpened() {
//		getSlidingMenu().showContent();
	}
	
	private MessageReceiver mMessageReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "cmcc.mhealth.MESSAGE_RECEIVED_ACTION";
	public static final String MESSAGE_GPS_ACTION = "cmcc.mhealth.MESSAGE_GPS_ACTION";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";
	private void registerMessageReceiver() {
		mMessageReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
	}

	private class MessageReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
				JPushInterface.clearAllNotifications(MainCenterActivity.this);
				String receivedstr = intent.getStringExtra("message_on_receiving");
				if (receivedstr != null && receivedstr.length() > 0) {
					MenuFragment menu = (MenuFragment) getSupportFragmentManager().findFragmentByTag("menuFragment");
					menu.setToMessageFragment();
				}
			}
		}
	}
}
