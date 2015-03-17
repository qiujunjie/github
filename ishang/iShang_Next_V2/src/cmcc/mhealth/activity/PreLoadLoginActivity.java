package cmcc.mhealth.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cmcc.mhealth.R;
import cmcc.mhealth.basic.BaseActivity;
import cmcc.mhealth.bean.ClubData;
import cmcc.mhealth.bean.ClubListInfo;
import cmcc.mhealth.bean.ContectData;
import cmcc.mhealth.bean.ContectGroupData;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Config;
import cmcc.mhealth.common.Constants;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.common.PreferencesUtils;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.db.MHealthProviderMetaData;
import cmcc.mhealth.net.DataSyn;
import cmcc.mhealth.net.NetworkTool;
import cmcc.mhealth.slidingcontrol.MainCenterActivity;

public class PreLoadLoginActivity extends BaseActivity implements OnClickListener {
	private static String TAG = "LoginActivity";

	private LinearLayout mLinearLayoutPhone;
	private RelativeLayout mLinearLayoutPassword;
	private TextView mTextViewTitle;
	private EditText mEditTelphone, mEditPassword;
	private CheckBox eCheckBoxRemember;
	private CheckBox mCheckBoxAuto;

	private TextView mTextViewForgetPassword;
	// private Editor mSharedata;

	private String mPhoneNum;
	private String mPassword;

	/**
	 * 0 ΪĬ�� ���˺Ų���Ȼ���Ϊ��һ�ε�¼ 1Ϊ�˻�������������ͬ
	 */
	private int mFlagLogin = 0;
	/**
	 * ����״̬�� 0�� �����磬 1��wifi 2:GPS/3G
	 */
	private int internet;

	private ImageView mBtnOk;

	private Boolean mCheckdRemember = false;
	private Boolean mCheckdAuto = false;

	private TextView mTextViewRegister;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		initViews();
		checkNetworkState();

		// ȡ�û��preferences����.
		SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);

		if (info != null) {
			mPhoneNum = info.getString(SharedPreferredKey.PHONENUM, null); // �õ��绰����
			mPassword = info.getString(SharedPreferredKey.PASSWORD, null); // �õ�����
		}

		if (info != null && mPhoneNum != null && mPassword != null) {
			boolean checkAuto = info.getBoolean("checkdAuto", false); // �Զ���¼
			boolean checkRem = info.getBoolean("checkdRemember", false); // ��ס����
			mEditTelphone.setText(mPhoneNum.toString().trim());
			mCheckBoxAuto.setChecked(mCheckdAuto);
			if (checkRem) {// �ж��Ƿ��ס����
				Logger.i(TAG, "telephone=" + mPhoneNum + "pwd=" + mPassword);
				mEditPassword.setText(mPassword.toString().trim());
			} else if (checkRem && checkAuto) {
				// �Զ���½
				Intent intent = new Intent();
				Logger.i(TAG, "login == auto");
				// ��ͨ��
				// intent.setClass(LoginActivity.this, MainMenuActivity.class);
				// ������
				intent.setClass(PreLoadLoginActivity.this, MainCenterActivity.class);

				startActivity(intent);
				PreLoadLoginActivity.this.finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
			} else {
				mEditPassword.setFocusable(true);// ����Ϊ�գ���ȡ����
				eCheckBoxRemember.setChecked(false);
			}
		}
		tipExit();
	}

	void initViews() {
		// title
//		mTextViewTitle = (TextView) findViewById(R.id.textView_title);
//		mTextViewTitle.setText(R.string.login_title);

		mLinearLayoutPhone = (LinearLayout) findViewById(R.id.linearLayout_phone);
		mLinearLayoutPassword = (RelativeLayout) findViewById(R.id.linearLayout_password);
		mEditTelphone = (EditText) findViewById(R.id.edittelphone);
		mEditTelphone.setFocusable(true);
		mEditTelphone.requestFocus();
		onFocusChange(mEditTelphone.isFocusable(), mEditTelphone);
		mEditPassword = (EditText) findViewById(R.id.editPassword);
		mEditTelphone.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus)
					mLinearLayoutPhone.setBackgroundResource(R.drawable.login_incur);
				else
					mLinearLayoutPhone.setBackgroundResource(R.drawable.login_input);
			}
		});

		mEditPassword.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus)
					mLinearLayoutPassword.setBackgroundResource(R.drawable.login_incur);
				else
					mLinearLayoutPassword.setBackgroundResource(R.drawable.login_input);
			}
		});

		eCheckBoxRemember = (CheckBox) findViewById(R.id.checkBoxRemember);
		mCheckBoxAuto = (CheckBox) findViewById(R.id.checkBoxAuto);
		mBtnOk = (ImageView) findViewById(R.id.bt_OK);
		// bar = (ProgressBar) findViewById(R.id.progressBar_login);

		mBtnOk.setOnClickListener(this);

		// �����ֻ��Ÿı�ļ����¼� -->������������
		if (mEditTelphone.getText().toString().trim() != null) {
			mEditTelphone.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if (mPassword != null) {
						// ����ס����ʱ�������˺�ͬʱ�������
						mEditPassword.setText(null);
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {

				}

				@Override
				public void afterTextChanged(Editable s) {

				}
			});
		}

		mTextViewForgetPassword = findView(R.id.textView_forgetpwd);
		mTextViewForgetPassword.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		mTextViewForgetPassword.setOnClickListener(this);
		
		mTextViewRegister = findView(R.id.textView_register);
		mTextViewRegister.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		mTextViewRegister.setOnClickListener(this);
	}

	private void checkNetworkState() {
		internet = NetworkTool.getNetworkState(PreLoadLoginActivity.this);
		Logger.i(TAG, "internet=" + internet);
		if (internet == 0) {
			messagesManager(Constants.MESSAGE_INTERNET_NONE);
			AlertDialog.Builder dlg = new AlertDialog.Builder(this);
			dlg.setTitle("��ʾ");
			// �����Զ���Ի������ʽ
			dlg.setPositiveButton("��������", //
					new DialogInterface.OnClickListener() // �����¼�����
					{
						public void onClick(DialogInterface dialog, int whichButton) {
							// ��ת����������
							Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
							startActivity(wifiSettingsIntent);
						}
					}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}).create().show();// ����
		}
	}

	private void loadContect(final String phoneNum, final String password, final String strServerVersion) {
		new Thread() {
			public void run() {
				ClubListInfo clireqData = new ClubListInfo();
				int suc = 0;
				if("2".equals(strServerVersion)){
					suc = DataSyn.getInstance().getClubList(phoneNum, password, clireqData);
				}else{
					suc = 0;
					clireqData.clublist.add(new ClubData(0, "��˾"));
				}
				if (suc == 0) {
					MHealthProviderMetaData provider = MHealthProviderMetaData.GetMHealthProvider(PreLoadLoginActivity.this);
					provider.removeContactGroupAllDatas();
					provider.removeContactAllDatas();
					for (ClubData clubdata : clireqData.clublist) {
						Logger.i(TAG, "===<<< Start Insert Contacts id= " + clubdata.getClubid() + " >>> ===");
						ArrayList<ContectGroupData> contactGroupList = DataSyn.getInstance().getContactGroupList(PreLoadLoginActivity.this, phoneNum, password, clubdata.getClubid());
						if (contactGroupList != null && contactGroupList.size() > 0) {
							provider.insertGroupContacts(contactGroupList, clubdata.getClubid());
							Logger.i(TAG, "===<<< complete! ,contactGroupNum = " + contactGroupList.size() + " >>> ===");
						} else {
							Logger.e(TAG, ">>>>>>>  contactGroupList is null!  <<<<<<<<");
						}
						ArrayList<ContectData> contactList = DataSyn.getInstance().getContactList(PreLoadLoginActivity.this, phoneNum, password, clubdata.getClubid());
						if (contactList == null || contactList.size() == 0) {
							Logger.e(TAG, ">>>>>>>  contactList is null!  <<<<<<<<");
							return;
						}
						provider.insertContacts(contactList, clubdata.getClubid());
						Logger.i(TAG, "===<<< complete! ,contactNum = " + contactList.size() + " >>> ===");
					}
				} else {
					Logger.e(TAG, ">>>>>>>  getClubList fail!  <<<<<<<<");
				}

			}
		}.start();
	}

	/**
	 * HttpClient get����ʽ��������.
	 * 
	 * @param strCreatTime
	 *            �����ʱ��
	 * @param phoneNum
	 *            �绰����
	 * @param password
	 *            ����
	 * @return
	 */
	public void getPedometorInfo(String strCreatTime, String phoneNum, String password) {// http://10.2.48.66:8000/accounts/13810411683/apps/pedometer?psw=wxf&date=20121002
		int res = DataSyn.getInstance().loginAuth(phoneNum, password, this);
		dismiss();
		Logger.i(TAG, "res == " + res);
		if (res == 0) {
			loginSuccess(phoneNum, password);
		} else if (res == -1) {
			// ��Handle����Ϣ
			messagesManager(Constants.MESSAGE_INTERNET_ERROR);
		} else if (res == 1) {
			messagesManager(Constants.MESSAGE_SERVER_EXCEPTION);
		} else if (res == 2) {
			// ��Handle����Ϣ
			messagesManager(Constants.MESSAGE_SERVER_EXCEPTION);
		} else if (res == 3) {
			// �Ñ�δ����
//			dismiss();

            Editor editorShare = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();
            /* ��¼�ɹ������״ΰ�װ��������ݱ����¼��Ϣ */

            // ������Ϣ
            editorShare.putString(SharedPreferredKey.PHONENUM, phoneNum);
            editorShare.putString(SharedPreferredKey.PASSWORD, password);
            editorShare.commit();
            
            
			messagesManager(Constants.MESSAGE_NOT_ACTIVITY);
			String selectedserver = PreferencesUtils.getString(this, SharedPreferredKey.SERVER_NAME, null);
			String url = "http://"+selectedserver+"/account.do?action=PhoneActive&userPhone="+phoneNum+"&password="+password+"&" + new Random().nextInt();
			Intent intent = new Intent();
			intent.putExtra("UserInfo", url);
			intent.putExtra("title", "�û�����");
			intent.setClass(getApplicationContext(), WebViewActivity.class);
			startActivityForResult(intent, 200);
//			intentActivity(LoginActivity.this, WebViewActivity.class, bundle);
		} else if (res == 4) {
			// û���˺�
			Logger.i(TAG, "account didnt exist in this server,trying next");
		} else if (res == 5) {
			// �������
			messagesManager(Constants.MESSAGE_PASSWORD_ERROE);
		} else if (res == 6) {
			//�����Ҳ���
			messagesManager(Constants.MESSAGE_INTERNET_NONE);
		} else if (res == 7) {
			// �ֻ��Ż��������
			messagesManager(Constants.MESSAGE_PHONE_PASSWORD_EXCEPTION);
		} else if (res == 8) {
			// ��ȡ�������б�ʧ��
			messagesManager(Constants.MESSAGE_GET_SERVERLIST_FAILED);
		} else {
			//�����Ҳ���
			messagesManager(Constants.MESSAGE_LOGIN_FALSE);
		}
		if (res != 0) {

			Editor infoEditor = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();
			infoEditor.putInt("INSTALL", 0);
			infoEditor.commit();
		}
		mBtnOk.setClickable(true);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == 200) {
			SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
			if (info != null) {
				mPhoneNum = info.getString(SharedPreferredKey.PHONENUM, null); // �õ��绰����
				mPassword = info.getString(SharedPreferredKey.PASSWORD, null); // �õ�����
			}
			loginSuccess(mPhoneNum, mPassword);
		}
	}

	/**
	 * ��½�ɹ�
	 * @param phoneNum
	 * @param password
	 */
	private void loginSuccess(String phoneNum, String password) {

		Editor editorShare = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();

		/* ��¼�ɹ������״ΰ�װ��������ݱ����¼��Ϣ */
		SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		int install = info.getInt("INSTALL", 0);
		String strServerIP = info.getString(SharedPreferredKey.SERVER_NAME, null);
		String strServerVersion = info.getString(SharedPreferredKey.SERVER_VERSION, "2");
		loadContect(phoneNum, password , strServerVersion); // ���°�����ϵ�����ݿ�
		int verCode = Config.getVerCode(this);
		if (install != verCode) {
			editorShare.putInt("INSTALL", verCode);
			editorShare.putString("GROUP_UPDATE_TIME", null);
			editorShare.putString("GROUP_UPDATE_VERSION", null);
			editorShare.putString("INPK_UPDATE_TIME_RACE", null);
		}

		if (eCheckBoxRemember.isChecked()) {
			mCheckdRemember = true;
		} else {
			mCheckdRemember = false;
		}
		if (mCheckBoxAuto.isChecked()) {
			mCheckdAuto = true;
		} else {
			mCheckdAuto = false;
		}
		// �״ε�¼������˺ŵ�¼ �����sharedpreference
		if (mFlagLogin == 0) {
			int newSetting = info.getInt("NEW_SETTING", 0);
			// //�״ε�¼���÷��״ΰ�װ
			// int verCode = Config.getVerCode(this);
			// Editor infoEditor2 = getSharedPreferences(SharedPreferredKey.SharedPrefenceName,
			// Context.MODE_PRIVATE).edit();
			// infoEditor2.putInt("INSTALL", verCode);
			// infoEditor2.commit();

			editorShare.clear();
			editorShare.putInt("NEW_SETTING", newSetting);
			editorShare.putString(SharedPreferredKey.SERVER_NAME, strServerIP);
			editorShare.putString(SharedPreferredKey.SERVER_VERSION, strServerVersion);
			editorShare.putBoolean("BSHOWGUID", false);
			editorShare.commit();

		}

		// ������Ϣ
		editorShare.putString(SharedPreferredKey.PHONENUM, phoneNum);
		editorShare.putString(SharedPreferredKey.PASSWORD, password);

		editorShare.putBoolean("checkdAuto", mCheckdAuto);
		editorShare.putBoolean("checkdRemember", mCheckdRemember);
		editorShare.putInt("fromLogin", mFlagLogin);

		editorShare.commit();

//			dismiss();
		// ��ͨ��
		// intentActivity(LoginActivity.this, MainMenuActivity.class);
		// ������
		intentActivity(PreLoadLoginActivity.this, MainCenterActivity.class, null,true);
		messagesManager(Constants.MESSAGE_LOGIN_SUCCESS);
	}

	@Override
	public synchronized void onClick(View view) {
		switch (view.getId()) {
		case R.id.textView_forgetpwd:
			Intent intent = new Intent(this, PreLoadForgetPwdActivity.class);
			startActivity(intent);
			break;
		case R.id.textView_register:
			String selectedserver = PreferencesUtils.getString(this, SharedPreferredKey.SERVER_NAME, null);
			String url = "http://183.224.40.209/iactivity/account.do?action=phoneRegister&userPhone="+mPhoneNum+"&password="+mPassword+"&" + new Random().nextInt();
			Intent intent1 = new Intent();
			intent1.putExtra("UserInfo", url);
			intent1.putExtra("title", "�û�ע��");
			intent1.setClass(getApplicationContext(), WebViewActivity.class);
			startActivity(intent1);
			break;
		case R.id.bt_OK:
			final String telephone = Common.getNumber(mEditTelphone.getText().toString().trim());
			final String pwd = mEditPassword.getText().toString();

			SharedPreferences sp = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
			Editor edit = sp.edit();
			edit.remove("selectedserver");
			edit.commit();
			DataSyn.strHttpURL = "";
			DataSyn.avatarHttpURL = "";

//			if (telephone.length() != 11) {
//				BaseToast(getString(R.string.MESSAGE_PHONE_ERROR));
//				mEditTelphone.setText(null);
//			} else
				if (TextUtils.isEmpty(telephone)) {
				messagesManager(Constants.MESSAGE_PHONE_ISEMPTY);
			} else if (TextUtils.isEmpty(pwd)) {
				messagesManager(Constants.MESSAGE_PASSWORD_ISEMPTY);
			} else if (telephone.length() > 30) {
				messagesManager(Constants.MESSAGE_PHONE_ISTOOLEN);
			} else if (pwd.length() > 50) {
				messagesManager(Constants.MESSAGE_PASSWORD_ISTOOLEN);
			} else if (telephone.equals(mPhoneNum) && pwd.equals(mPassword) && mCheckdRemember) { // �Զ���¼
				//���˺��� ������˺ŵ�˽������
				Editor editorShare = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();
				// ������ı��ص�¼
				editorShare.putString(SharedPreferredKey.PHONENUM, telephone);
				editorShare.putString(SharedPreferredKey.PASSWORD, pwd);

				if (eCheckBoxRemember.isChecked()) {
					mCheckdRemember = true;
				} else {
					mCheckdRemember = false;
				}
				if (mCheckBoxAuto.isChecked()) {
					mCheckdAuto = true;
				} else {
					mCheckdAuto = false;
				}
				editorShare.putBoolean("checkdAuto", mCheckdAuto);
				editorShare.putBoolean("checkdRemember", mCheckdRemember);
				editorShare.commit();
				// ƥ��ɹ���ֱ�ӱ��ص�¼
				// ��ͨ��
				// intentActivity(LoginActivity.this, MainMenuActivity.class);
				// ������
				intentActivity(PreLoadLoginActivity.this, MainCenterActivity.class, null,true);
			} else { // ���Զ���¼
				if (NetworkTool.getNetworkState(this) != 0) {
					// �������ߵ�¼
					// ��¼
					if(!sp.getString(SharedPreferredKey.PHONENUM, "").equals(telephone)){
						clearDatabases();
					}
					mBtnOk.setClickable(false);
					showProgressDialog("���Ժ�", this);
					new Thread() {
						public void run() {
							// login �����ж� 0�˺Ų��ȸ��� 1�˺���Ȳ�����
							if (mPhoneNum == null || mPassword == null) {
								// sharedprefences��ȥ���ݣ������Ϊnull����Ϊ���״ε�¼
								mFlagLogin = 0;
							} else if (mPhoneNum.equals(telephone) && mPassword.equals(pwd)) {
								// �˺����
								mFlagLogin = 1;
							} else {
								// �˺Ų���
								mFlagLogin = 0;
							}
							// �оW�jֱ�ӵ�¼
							String time = Common.getDateAsYYYYMMDD(new Date().getTime());
							getPedometorInfo(time, telephone, pwd);
						};
					}.start();
				} else {
					// �����磬���Ե�¼����鿴��һ�ε����ݣ����ǲ����Ը���
					Logger.i(TAG, "û������" + telephone + "::" + mPhoneNum + "::" + pwd + "::" + mPassword);

					if (telephone.equals(mPhoneNum) && pwd.equals(mPassword)) {
						messagesManager(Constants.MESSAGE_INTERNET_NONE);
						// ��ת������
						// ��ͨ��
						// intentActivity(LoginActivity.this,
						// MainMenuActivity.class);
						// ������
						intentActivity(PreLoadLoginActivity.this, MainCenterActivity.class, null,true);
					} else {
						Logger.i(TAG, "NOInternet_login=false");
						messagesManager(Constants.MESSAGE_PHONE_PASSWORD_EXCEPTION);
					}
				}
			}
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Editor infoEditor = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();
			infoEditor.putInt("INSTALL", 0);
			infoEditor.commit();
			this.finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
			return true;
		}
		return false;
	}

	// ����һЩ�������ݿ��е�����
	private void clearDatabases() {
		MHealthProviderMetaData mhp = MHealthProviderMetaData.GetMHealthProvider(PreLoadLoginActivity.this);
		mhp.deleteMyFriend();
		mhp.MyRankDeleteData();
		mhp.deleteVitalSignValue();
		mhp.DeleteMyOldMsgs();
		mhp.deleteGPSData();
	}
}