package cmcc.mhealth.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;
import cmcc.mhealth.R;
import cmcc.mhealth.bean.PedoDetailInfo;
import cmcc.mhealth.bean.PedometorInfo;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.db.MHealthProviderMetaData;
import cmcc.mhealth.net.DataSyn;

@SuppressLint("HandlerLeak")
public class UserInfoPreferenceActivity extends PreferenceActivity implements
		OnPreferenceChangeListener {
	private static String TAG = "UserInfoPreferenceActivity";

	private static final String PASSWORD = SharedPreferredKey.PASSWORD;
	private static final String PHONENUMBER = SharedPreferredKey.PHONENUM;
	private static final String UPDATE_DAY = "update_day";
	private static final String SPORT_PLAN = "sport_plan";
	private static final String SPORT_PLAN_DEF = "1000";

	private AlertDialog dlg;
	private EditTextPreference m_phonenum_editPreference; // 手机号
	private EditTextPreference m_password_editPreference; // 密码
	private EditTextPreference m_updateday_editPreference; // 更新天数

	private ProgressDialog m_dialog;

	// 处理跳转到主Activity
	private Handler m_handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == 0) {
				m_dialog.cancel();
				Toast.makeText(UserInfoPreferenceActivity.this, "数据同步完成！", 0)
						.show();
			} else if (msg.what == 1) {
				m_dialog.cancel();
				Toast.makeText(UserInfoPreferenceActivity.this,
						"获取数据失败,请联系管理员", 0).show();
			}
		}
	};

	private SharedPreferences mUserInfo;

	private String mTelephone;

	private String mTelephonepwd;

	@SuppressLint("SimpleDateFormat")
	public class loadDateThreah implements Runnable {
		@Override
		public void run() {
			// 这里是联网下载数据，下载完成后执行下列的方法，handlder会调用前面覆写的handleMessage方法，在那里关闭加载提示框...
			SimpleDateFormat df_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");

			String phonenum = m_phonenum_editPreference.getText().toString();
			String password = m_password_editPreference.getText().toString();
			int update_num = Integer.parseInt(m_updateday_editPreference
					.getText().toString());

			// 更新数据到昨天 更新update_num天数据
			Date date_now = new Date();
			long date_now_long = date_now.getTime();
			ArrayList<PedometorInfo> infos = new ArrayList<PedometorInfo>();
			for (int i = 0; i < update_num; i++) {
				long date_long = date_now_long - i * (1000L * 60 * 60 * 24); // 后移一天
				String date_str = df_yyyyMMdd.format(new Date(date_long));
				PedometorInfo reqData = new PedometorInfo();
				int result = DataSyn.getInstance().getPedoInfo(phonenum,
						password, date_str, reqData);

				if (result == 0 && reqData.status.equals("SUCCESS")) {
					infos.add(reqData);
				} else {
					Log.w(TAG, "Get " + date_str + " data error");
				}
			}
			// 一天数据插入
			if (infos.size() > 0) {
				MHealthProviderMetaData.GetMHealthProvider(
						UserInfoPreferenceActivity.this).deletePedometerData();
				MHealthProviderMetaData.GetMHealthProvider(
						UserInfoPreferenceActivity.this).deletePedoDetailData();

				for (int i = infos.size() - 1; i >= 0; i--) {
					PedometorInfo reqData = infos.get(i);
					MHealthProviderMetaData.GetMHealthProvider(UserInfoPreferenceActivity.this).InsertPedometerData(reqData.datavalue, 0, false);

					// 添加详细包数据
					String fromHour = "00", toHour = "23";

					String current_date_str = reqData.date;

					PedoDetailInfo detailData = new PedoDetailInfo();
					int result = DataSyn.getInstance().getPedoInfoDetail(
							phonenum, password, fromHour, toHour,
							current_date_str, detailData);
					if (result == -1) {
						Log.e(TAG, "返回值为-1，网络读取存在错误！");
						return;
					} else if (result == 1) {
						Log.e(TAG, "返回值为1，返回数据存在问题！");
					} else {
						MHealthProviderMetaData.GetMHealthProvider(
								UserInfoPreferenceActivity.this)
								.insertPedoDetailData(detailData);
					}

				}

				Logger.i(TAG, "同步完成");
				m_handler.sendEmptyMessage(0);
			} else {
				Logger.i(TAG, "get no data");
				m_handler.sendEmptyMessage(1);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.userinfo_preference);

		m_phonenum_editPreference = (EditTextPreference) findPreference(PHONENUMBER);
		m_password_editPreference = (EditTextPreference) findPreference(PASSWORD);
		m_password_editPreference.setOnPreferenceChangeListener(this);
		m_updateday_editPreference = (EditTextPreference) findPreference(UPDATE_DAY);

		dlg = new AlertDialog.Builder(this)
				.setTitle("退出")
				.setMessage(
						"您确认要退出 " + this.getString(R.string.app_name) + " 吗？")
				// 设置自定义对话框的样式
				.setPositiveButton("确定", // 设置"确定"按钮
						new DialogInterface.OnClickListener() // 设置事件监听
						{
							public void onClick(DialogInterface dialog,
									int whichButton) {
								UserInfoPreferenceActivity.this.finish();
								overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
							}
						})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				}).create();// 创建

		setNewUserInfo();
	}

	/**
	 * 更新登录后的手机号密码
	 */
	private void setNewUserInfo() {
		mUserInfo = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		int flag = mUserInfo.getInt("fromLogin", 0);
		if (flag == 0) {
			mTelephone = mUserInfo.getString(PHONENUMBER, null);
			mTelephonepwd = mUserInfo.getString(PASSWORD, null);
			if (mTelephone != null && mTelephonepwd != null) {
				m_phonenum_editPreference.setText(mTelephone);
				m_password_editPreference.setText(mTelephonepwd);
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			dlg.show();// 显示
			return true;
		}
		return false;
	}

	public static String getPhoneNum(Context context) {
		String phonenum_str = PreferenceManager.getDefaultSharedPreferences(
				context).getString(PHONENUMBER, "");
		return phonenum_str;
	}

	public static String getPassword(Context context) {
		String password_str = PreferenceManager.getDefaultSharedPreferences(
				context).getString(PASSWORD, "");
		return password_str;
	}

	public static int getSportPlan(Context context) {
		String sport_plan_str = PreferenceManager.getDefaultSharedPreferences(
				context).getString(SPORT_PLAN, SPORT_PLAN_DEF);
		return Integer.parseInt(sport_plan_str);
	}

	public static void setPassword(Context context, String value) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putString(PASSWORD, value).commit();
	}

	public static void setPhoneNum(Context context, String value) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putString(PHONENUMBER, value).commit();
	}

	public static void setSportPlan(Context context, String value) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putString(SPORT_PLAN, value).commit();
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		if (preference.getKey().equals(SharedPreferredKey.PASSWORD)) {
			Logger.i(TAG, "change" + String.valueOf(newValue));
			// PreferenceManager.getDefaultSharedPreferences(this).edit()
			// .putString(PASSWORD, String.valueOf(newValue)).commit();

			m_dialog = ProgressDialog.show(this, "数据同步", "数据同步中，请稍等！");

			Thread thread = new Thread(new loadDateThreah());
			thread.start();
		}
		return true; // 保存更新后的值
	}
}
