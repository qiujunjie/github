/********************************************
 * File Name��Loading.java
 * Version	��1.00
 * Author	��Gaofei - �߷�
 * Date		��2012-7-31
 * LastModify��2012-7-31
 * Functon	����������
 * 
 * CopyRight(c) China Mobile 2012
 * All rights reserved
 *******************************************/
package cmcc.mhealth.activity;

/**�� Loading
 * company: �й��ƶ��о�Ժ���ʼ�����ͨ���о�����
 *@author by:gaofei-�߷�
 * @date 2012-7-31 ����5:31:06 
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
	private int mInternet;// 0δ�����磬1��2����
	// private Boolean mIfUpdate = false;//����Ƿ���¹���Ĭ��Ϊfalse
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
	// ====��ʱչ��====
	Timer tx = new Timer();

	// �ӳ�ʱ�䵽��ִ��
	class nextQuestX extends java.util.TimerTask {
		public void run() {
			// ���ڲ���һ���߳�
			mHandler.sendEmptyMessageDelayed(80, 0);
		}
	}

	// ���ڲ���һ���̣߳���view���̲߳���ȫ�����ԣ���Ҫ����Ϣ������������ʾ����
	private Handler mHandler = new Handler() {
		// private boolean mCheckdRemember;

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 10001) {
				// �Զ���½�������ڼ�ס�����ǰ���½���
				IntentActivity(mIntent);
			}

			Logger.i(TAG, "internet==" + mInternet);
			if (msg.what == 80 || msg.what == 0) {

				// �Ƿ�������
				SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
				final String mPassword = info.getString(SharedPreferredKey.PASSWORD, null);
				final String mPhoneNum = info.getString(SharedPreferredKey.PHONENUM, ""); // �õ��绰����
				// String NowTime = GetTime.getTime();
				boolean checkAuto = info.getBoolean("checkdAuto", false); // �Զ���¼
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

		/* ������ת */
		@SuppressLint("NewApi")
		private void IntentActivity(Intent intent) {
			if (mInternet == 0) {
				BaseToast("û�����磬��鿴���ã�");
			}

			// Editor editorShare = getSharedPreferences(SharedPreferredKey.SharedPrefenceName,
			// Context.MODE_PRIVATE).edit();

			// ��ͨ��
			// intent.setClass(LoadingActivity.this, MainMenuActivity.class);
			// ������
			intent.setClass(PreLoadLoadingActivity.this, MainCenterActivity.class);
			startActivity(intent);
			PreLoadLoadingActivity.this.finish();
			overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
		}

	};
	// ====��ʱչ��====
	// ///////////////////////////////////
}