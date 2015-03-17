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
	 * 0 为默认 即账号不相等或者为第一次登录 1为账户密码与所输相同
	 */
	private int mFlagLogin = 0;
	/**
	 * 网络状态码 0： 无网络， 1：wifi 2:GPS/3G
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

		// 取得活动的preferences对象.
		SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);

		if (info != null) {
			mPhoneNum = info.getString(SharedPreferredKey.PHONENUM, null); // 拿到电话号码
			mPassword = info.getString(SharedPreferredKey.PASSWORD, null); // 拿到密码
		}

		if (info != null && mPhoneNum != null && mPassword != null) {
			boolean checkAuto = info.getBoolean("checkdAuto", false); // 自动登录
			boolean checkRem = info.getBoolean("checkdRemember", false); // 记住密码
			mEditTelphone.setText(mPhoneNum.toString().trim());
			mCheckBoxAuto.setChecked(mCheckdAuto);
			if (checkRem) {// 判断是否记住密码
				Logger.i(TAG, "telephone=" + mPhoneNum + "pwd=" + mPassword);
				mEditPassword.setText(mPassword.toString().trim());
			} else if (checkRem && checkAuto) {
				// 自动登陆
				Intent intent = new Intent();
				Logger.i(TAG, "login == auto");
				// 普通版
				// intent.setClass(LoginActivity.this, MainMenuActivity.class);
				// 滑动版
				intent.setClass(PreLoadLoginActivity.this, MainCenterActivity.class);

				startActivity(intent);
				PreLoadLoginActivity.this.finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
			} else {
				mEditPassword.setFocusable(true);// 密码为空，获取焦点
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

		// 设置手机号改变的监听事件 -->清空密码框密码
		if (mEditTelphone.getText().toString().trim() != null) {
			mEditTelphone.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if (mPassword != null) {
						// 当记住密码时，更改账号同时清除密码
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
			dlg.setTitle("提示");
			// 设置自定义对话框的样式
			dlg.setPositiveButton("设置网络", //
					new DialogInterface.OnClickListener() // 设置事件监听
					{
						public void onClick(DialogInterface dialog, int whichButton) {
							// 跳转到网络设置
							Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
							startActivity(wifiSettingsIntent);
						}
					}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}).create().show();// 创建
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
					clireqData.clublist.add(new ClubData(0, "公司"));
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
	 * HttpClient get请求方式访问网络.
	 * 
	 * @param strCreatTime
	 *            请求的时间
	 * @param phoneNum
	 *            电话号码
	 * @param password
	 *            密码
	 * @return
	 */
	public void getPedometorInfo(String strCreatTime, String phoneNum, String password) {// http://10.2.48.66:8000/accounts/13810411683/apps/pedometer?psw=wxf&date=20121002
		int res = DataSyn.getInstance().loginAuth(phoneNum, password, this);
		dismiss();
		Logger.i(TAG, "res == " + res);
		if (res == 0) {
			loginSuccess(phoneNum, password);
		} else if (res == -1) {
			// 给Handle发消息
			messagesManager(Constants.MESSAGE_INTERNET_ERROR);
		} else if (res == 1) {
			messagesManager(Constants.MESSAGE_SERVER_EXCEPTION);
		} else if (res == 2) {
			// 给Handle发消息
			messagesManager(Constants.MESSAGE_SERVER_EXCEPTION);
		} else if (res == 3) {
			// 用戶未激活
//			dismiss();

            Editor editorShare = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();
            /* 登录成功并且首次安装则清楚数据保存登录信息 */

            // 储存信息
            editorShare.putString(SharedPreferredKey.PHONENUM, phoneNum);
            editorShare.putString(SharedPreferredKey.PASSWORD, password);
            editorShare.commit();
            
            
			messagesManager(Constants.MESSAGE_NOT_ACTIVITY);
			String selectedserver = PreferencesUtils.getString(this, SharedPreferredKey.SERVER_NAME, null);
			String url = "http://"+selectedserver+"/account.do?action=PhoneActive&userPhone="+phoneNum+"&password="+password+"&" + new Random().nextInt();
			Intent intent = new Intent();
			intent.putExtra("UserInfo", url);
			intent.putExtra("title", "用户激活");
			intent.setClass(getApplicationContext(), WebViewActivity.class);
			startActivityForResult(intent, 200);
//			intentActivity(LoginActivity.this, WebViewActivity.class, bundle);
		} else if (res == 4) {
			// 没有账号
			Logger.i(TAG, "account didnt exist in this server,trying next");
		} else if (res == 5) {
			// 密码错误
			messagesManager(Constants.MESSAGE_PASSWORD_ERROE);
		} else if (res == 6) {
			//网络找不到
			messagesManager(Constants.MESSAGE_INTERNET_NONE);
		} else if (res == 7) {
			// 手机号或密码错误
			messagesManager(Constants.MESSAGE_PHONE_PASSWORD_EXCEPTION);
		} else if (res == 8) {
			// 获取服务器列表失败
			messagesManager(Constants.MESSAGE_GET_SERVERLIST_FAILED);
		} else {
			//网络找不到
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
				mPhoneNum = info.getString(SharedPreferredKey.PHONENUM, null); // 拿到电话号码
				mPassword = info.getString(SharedPreferredKey.PASSWORD, null); // 拿到密码
			}
			loginSuccess(mPhoneNum, mPassword);
		}
	}

	/**
	 * 登陆成功
	 * @param phoneNum
	 * @param password
	 */
	private void loginSuccess(String phoneNum, String password) {

		Editor editorShare = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();

		/* 登录成功并且首次安装则清楚数据保存登录信息 */
		SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		int install = info.getInt("INSTALL", 0);
		String strServerIP = info.getString(SharedPreferredKey.SERVER_NAME, null);
		String strServerVersion = info.getString(SharedPreferredKey.SERVER_VERSION, "2");
		loadContect(phoneNum, password , strServerVersion); // 更新班组联系人数据库
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
		// 首次登录或更换账号登录 先清空sharedpreference
		if (mFlagLogin == 0) {
			int newSetting = info.getInt("NEW_SETTING", 0);
			// //首次登录设置非首次安装
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

		// 储存信息
		editorShare.putString(SharedPreferredKey.PHONENUM, phoneNum);
		editorShare.putString(SharedPreferredKey.PASSWORD, password);

		editorShare.putBoolean("checkdAuto", mCheckdAuto);
		editorShare.putBoolean("checkdRemember", mCheckdRemember);
		editorShare.putInt("fromLogin", mFlagLogin);

		editorShare.commit();

//			dismiss();
		// 普通版
		// intentActivity(LoginActivity.this, MainMenuActivity.class);
		// 滑动版
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
			intent1.putExtra("title", "用户注册");
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
			} else if (telephone.equals(mPhoneNum) && pwd.equals(mPassword) && mCheckdRemember) { // 自动登录
				//换账号了 清除旧账号的私人数据
				Editor editorShare = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();
				// 有网络的本地登录
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
				// 匹配成功，直接本地登录
				// 普通版
				// intentActivity(LoginActivity.this, MainMenuActivity.class);
				// 滑动版
				intentActivity(PreLoadLoginActivity.this, MainCenterActivity.class, null,true);
			} else { // 非自动登录
				if (NetworkTool.getNetworkState(this) != 0) {
					// 有网在线登录
					// 登录
					if(!sp.getString(SharedPreferredKey.PHONENUM, "").equals(telephone)){
						clearDatabases();
					}
					mBtnOk.setClickable(false);
					showProgressDialog("请稍候", this);
					new Thread() {
						public void run() {
							// login 进入判断 0账号不等更新 1账号相等不更新
							if (mPhoneNum == null || mPassword == null) {
								// sharedprefences里去数据，如果都为null则认为是首次登录
								mFlagLogin = 0;
							} else if (mPhoneNum.equals(telephone) && mPassword.equals(pwd)) {
								// 账号相等
								mFlagLogin = 1;
							} else {
								// 账号不等
								mFlagLogin = 0;
							}
							// 有網絡直接登录
							String time = Common.getDateAsYYYYMMDD(new Date().getTime());
							getPedometorInfo(time, telephone, pwd);
						};
					}.start();
				} else {
					// 无网络，可以登录进入查看上一次的数据，但是不可以更新
					Logger.i(TAG, "没有网络" + telephone + "::" + mPhoneNum + "::" + pwd + "::" + mPassword);

					if (telephone.equals(mPhoneNum) && pwd.equals(mPassword)) {
						messagesManager(Constants.MESSAGE_INTERNET_NONE);
						// 跳转主界面
						// 普通版
						// intentActivity(LoginActivity.this,
						// MainMenuActivity.class);
						// 滑动版
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

	// 清理一些个人数据库中的数据
	private void clearDatabases() {
		MHealthProviderMetaData mhp = MHealthProviderMetaData.GetMHealthProvider(PreLoadLoginActivity.this);
		mhp.deleteMyFriend();
		mhp.MyRankDeleteData();
		mhp.deleteVitalSignValue();
		mhp.DeleteMyOldMsgs();
		mhp.deleteGPSData();
	}
}