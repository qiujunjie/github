/********************************************
 * File Name：Loading.java
 * Version	：1.00
 * Author	：Gaofei - 高飞
 * Date		：2012-7-31
 * LastModify：2012-7-31
 * Functon	：功能描述
 * 
 * CopyRight(c) China Mobile 2012
 * All rights reserved
 *******************************************/
package cmcc.mhealth.activity;

/**类 Loading
 * company: 中国移动研究院普适计算与通信研究中心
 *@author by:gaofei-高飞
 * @date 2012-7-31 下午5:31:06 
 * version 1.0 
 * describe: 
 */
import java.util.Timer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Window;
import cmcc.mhealth.R;
import cmcc.mhealth.basic.BaseActivity;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.net.DataSyn;
import cmcc.mhealth.net.NetworkTool;
import cmcc.mhealth.slidingcontrol.MainCenterActivity;

public class PreLoadLoadingActivity extends BaseActivity {
	public static final String TAG = PreLoadLoadingActivity.class.getSimpleName();
	public static int verCode = 0;// Config.getVerCode(this);
	public static String verName = "";// Config.getVerName(this);
	private int mInternet;// 0未无网络，1，2有网
	// private Boolean mIfUpdate = false;//标记是否更新过，默认为false
	// ////////////////
	private Intent mIntent = null;

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		PreLoadLoadingActivity.this.finish();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_loading);
		mInternet = NetworkTool.getNetworkState(PreLoadLoadingActivity.this);
		mIntent = new Intent();
		tx.schedule(new nextQuestX(), 2000);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	// ///////////////////////////////////
	// ====延时展开====
	Timer tx = new Timer();

	// 延迟时间到后执行
	class nextQuestX extends java.util.TimerTask {
		public void run() {
			// 由于不在一个线程
			mHandler.sendEmptyMessageDelayed(80, 0);
		}
	}

	// 由于不在一个线程，而view是线程不安全的所以，需要用消息机制来操作显示部分
	private Handler mHandler = new Handler() {
		// private boolean mCheckdRemember;

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 10001) {
				// 自动登陆必须是在记住密码的前提下进行
				IntentActivity(mIntent);
			}

			Logger.i(TAG, "internet==" + mInternet);
			if (msg.what == 80 || msg.what == 0) {

				// 是否有网络
				SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
				final String mPassword = info.getString(SharedPreferredKey.PASSWORD, null);
				final String mPhoneNum = info.getString(SharedPreferredKey.PHONENUM, ""); // 拿到电话号码
				// String NowTime = GetTime.getTime();
				boolean checkAuto = info.getBoolean("checkdAuto", false); // 自动登录
				// mCheckdRemember = info.getBoolean("checkdRemember", false);

				if (checkAuto && !TextUtils.isEmpty(mPassword) && !TextUtils.isEmpty(mPhoneNum)) {
					if (DataSyn.strHttpURL == null || "".equals(DataSyn.strHttpURL)) {
						Logger.d("testing", DataSyn.strHttpURL == null ? "null" : DataSyn.strHttpURL);
						String selectedserver = info.getString(SharedPreferredKey.SERVER_NAME, "");
						if (!"".equals(selectedserver)) {
							String shu = "http://" + selectedserver + "openClientApi.do?action=";
							String ahu = "http://" + selectedserver + "UserAvatar/";
							DataSyn.setStrHttpURL(shu);
							DataSyn.setAvatarHttpURL(ahu);
							mHandler.sendEmptyMessage(10001);
						} else {
							new Thread() {
								public void run() {
									DataSyn.getInstance().loginAuth(mPhoneNum, mPassword, PreLoadLoadingActivity.this);
									mHandler.sendEmptyMessage(10001);
								};
							}.start();
						}
					} else {
						mHandler.sendEmptyMessage(10001);
					}
				} else {
					mIntent.setClass(PreLoadLoadingActivity.this, PreLoadLoginActivity.class);
					startActivity(mIntent);
					PreLoadLoadingActivity.this.finish();
					overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
				}

			}

		}

		/* 界面跳转 */
		@SuppressLint("NewApi")
		private void IntentActivity(Intent intent) {
			if (mInternet == 0) {
				BaseToast("没有网络，请查看设置！");
			}

			// Editor editorShare = getSharedPreferences(SharedPreferredKey.SharedPrefenceName,
			// Context.MODE_PRIVATE).edit();

			// 普通版
			// intent.setClass(LoadingActivity.this, MainMenuActivity.class);
			// 滑动版
			intent.setClass(PreLoadLoadingActivity.this, MainCenterActivity.class);
			startActivity(intent);
			PreLoadLoadingActivity.this.finish();
			overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
		}

	};
	// ====延时展开====
	// ///////////////////////////////////
}