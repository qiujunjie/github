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
	private EditTextPreference m_phonenum_editPreference; // �ֻ���
	private EditTextPreference m_password_editPreference; // ����
	private EditTextPreference m_updateday_editPreference; // ��������

	private ProgressDialog m_dialog;

	// ������ת����Activity
	private Handler m_handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == 0) {
				m_dialog.cancel();
				Toast.makeText(UserInfoPreferenceActivity.this, "����ͬ����ɣ�", 0)
						.show();
			} else if (msg.what == 1) {
				m_dialog.cancel();
				Toast.makeText(UserInfoPreferenceActivity.this,
						"��ȡ����ʧ��,����ϵ����Ա", 0).show();
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
			// �����������������ݣ�������ɺ�ִ�����еķ�����handlder�����ǰ�渲д��handleMessage������������رռ�����ʾ��...
			SimpleDateFormat df_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");

			String phonenum = m_phonenum_editPreference.getText().toString();
			String password = m_password_editPreference.getText().toString();
			int update_num = Integer.parseInt(m_updateday_editPreference
					.getText().toString());

			// �������ݵ����� ����update_num������
			Date date_now = new Date();
			long date_now_long = date_now.getTime();
			ArrayList<PedometorInfo> infos = new ArrayList<PedometorInfo>();
			for (int i = 0; i < update_num; i++) {
				long date_long = date_now_long - i * (1000L * 60 * 60 * 24); // ����һ��
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
			// һ�����ݲ���
			if (infos.size() > 0) {
				MHealthProviderMetaData.GetMHealthProvider(
						UserInfoPreferenceActivity.this).deletePedometerData();
				MHealthProviderMetaData.GetMHealthProvider(
						UserInfoPreferenceActivity.this).deletePedoDetailData();

				for (int i = infos.size() - 1; i >= 0; i--) {
					PedometorInfo reqData = infos.get(i);
					MHealthProviderMetaData.GetMHealthProvider(UserInfoPreferenceActivity.this).InsertPedometerData(reqData.datavalue, 0, false);

					// �����ϸ������
					String fromHour = "00", toHour = "23";

					String current_date_str = reqData.date;

					PedoDetailInfo detailData = new PedoDetailInfo();
					int result = DataSyn.getInstance().getPedoInfoDetail(
							phonenum, password, fromHour, toHour,
							current_date_str, detailData);
					if (result == -1) {
						Log.e(TAG, "����ֵΪ-1�������ȡ���ڴ���");
						return;
					} else if (result == 1) {
						Log.e(TAG, "����ֵΪ1���������ݴ������⣡");
					} else {
						MHealthProviderMetaData.GetMHealthProvider(
								UserInfoPreferenceActivity.this)
								.insertPedoDetailData(detailData);
					}

				}

				Logger.i(TAG, "ͬ�����");
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
				.setTitle("�˳�")
				.setMessage(
						"��ȷ��Ҫ�˳� " + this.getString(R.string.app_name) + " ��")
				// �����Զ���Ի������ʽ
				.setPositiveButton("ȷ��", // ����"ȷ��"��ť
						new DialogInterface.OnClickListener() // �����¼�����
						{
							public void onClick(DialogInterface dialog,
									int whichButton) {
								UserInfoPreferenceActivity.this.finish();
								overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
							}
						})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				}).create();// ����

		setNewUserInfo();
	}

	/**
	 * ���µ�¼����ֻ�������
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
			dlg.show();// ��ʾ
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

			m_dialog = ProgressDialog.show(this, "����ͬ��", "����ͬ���У����Եȣ�");

			Thread thread = new Thread(new loadDateThreah());
			thread.start();
		}
		return true; // ������º��ֵ
	}
}
