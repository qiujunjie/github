package cmcc.mhealth.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cmcc.mhealth.R;
import cmcc.mhealth.R.color;
import cmcc.mhealth.basic.BaseActivity;
import cmcc.mhealth.bean.TempCodeInfo;
import cmcc.mhealth.bean.UpdatePasswordInfo;
import cmcc.mhealth.common.Constants;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.net.DataSyn;

//**
@SuppressLint("HandlerLeak")
public class PreLoadForgetPwdActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = "ForgetPwdActivity";

	// private LinearLayout mLinearLayoutNewPwd;
	private EditText mEditTextPhoneNum;
	private EditText mEditTextPassword;
	private EditText mEditTextPasswordAgain;
	private EditText mEditTextTempCode;

	private TextView mTextViewTitleBar;

	private Button mButtonSend;
	private Button mButtonOk;

	// private Button mButtonSetPwd;

	private String mPhoneNum;

	private String mStatus;
//**	private String mResultStatus;
	private String mTempCode;
	private String mTempCodeForIntent;
//**	private String mLimitTime;
	private String mNewPwd;

	private int mResultNet;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			//验证成功时执行
			case 0:
				Intent intent = new Intent();
				intent.setClass(PreLoadForgetPwdActivity.this, PreLoadResetPwdActivity.class);
				
				Bundle bundle = new Bundle();
				bundle.putString("mTempCode", mTempCodeForIntent);
				bundle.putString("mPhoneNum", mPhoneNum);
				intent.putExtra("fpa_infos", bundle);
				
				startActivity(intent);
				PreLoadForgetPwdActivity.this.finish();
				overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_forgetpwd);
		SharedPreferences data = getSharedPreferences(SharedPreferredKey.SHARED_NAME, MODE_PRIVATE);
		mPhoneNum = data.getString(SharedPreferredKey.PHONENUM, null); // 拿到电话号码
		mEditTextPhoneNum = findView(R.id.edittext_phonenum);
		if (mPhoneNum != null)
			mEditTextPhoneNum.setText(mPhoneNum);
	}

	@SuppressWarnings("deprecation")
	private void initViews() {
		// mLinearLayoutNewPwd = findView(R.id.linearlayout_newpwd);
		// mLinearLayoutNewPwd.setVisibility(View.INVISIBLE);

		mEditTextPhoneNum = findView(R.id.edittext_phonenum);
		mEditTextTempCode = findView(R.id.edittext_tempcode);
		mEditTextPassword = findView(R.id.newpwd_edit_setting);
		mEditTextPasswordAgain = findView(R.id.newpwd_ag_edit_setting);

		mButtonSend = findView(R.id.button_send);
		mButtonOk = findView(R.id.button_ok);
		// mButtonSetPwd = findView(R.id.UpdatePwd_btn_setting);

		mButtonSend.setOnClickListener(this);
		mButtonOk.setOnClickListener(this);
		// mButtonSetPwd.setOnClickListener(this);

		mTextViewTitleBar = (TextView) findViewById(R.id.textView_title);
		mTextViewTitleBar.setText("忘记密码");
		ImageButton back = (ImageButton) findViewById(R.id.button_input_bg_back);
		back.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_button_back));
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
			}
		});
	}

	@SuppressWarnings("unused")
	private void PasswordResetMessage() {
		mPhoneNum = mEditTextPhoneNum.getText().toString();
		if (mPhoneNum == null || "".equals(mPhoneNum)) {
			BaseToast("手机号不能为空");
			return;
		}

		if (mTempCode == null || "".equals(mTempCode)) {
			BaseToast("请重新获取验证码");
			return;
		}
		String tempCode = mEditTextTempCode.getText().toString();
		if (!mTempCode.equals(tempCode)) {
			BaseToast("验证码错误");
			return;
		}

		String pwd = mEditTextPassword.getText().toString();
		String pwdAgain = mEditTextPasswordAgain.getText().toString();

		if (pwd == null || pwd.equals("")) {
			BaseToast("密码不能为空");
			return;
		}
		if (!pwd.equals(pwdAgain)) {
			BaseToast("两次密码不一致");
			return;
		}
		mNewPwd = pwd;

		new AsyncTask<Integer, Integer, String>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			protected String doInBackground(Integer... params) {
				UpdatePasswordInfo reqData = new UpdatePasswordInfo();
				mResultNet = DataSyn.getInstance().passwordReset(mPhoneNum, mTempCode, mNewPwd, reqData);
				if (mResultNet == 0) {
					mStatus = reqData.status;
//**					mResultStatus = reqData.result;
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				switch (mResultNet) {
				case 0:
					if (mStatus != null && mStatus.equals("SUCCESS")) {
						BaseToast("密码修改成功");
					} else
						BaseToast("密码修改失败,请重新获取验证码");
					finish();
					overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
					break;
				case 1:
					messagesManager(Constants.MESSAGE_INTERNET_ERROR);
					break;
				case 2:
					messagesManager(Constants.MESSAGE_SERVER_EXCEPTION);
					break;
				case -1:
					messagesManager(Constants.MESSAGE_INTERNET_ERROR);
					break;
				default:
					break;
				}
			}
		}.execute();
	}

	private void sendPwdMessage() {
		if (mPhoneNum == null || "".equals(mPhoneNum)) {
			BaseToast("手机号不能为空");
			return;
		}

		new AsyncTask<Integer, Integer, String>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			protected String doInBackground(Integer... params) {
				TempCodeInfo reqData = new TempCodeInfo();
				mResultNet = DataSyn.getInstance().sendTempCode(mPhoneNum, reqData, PreLoadForgetPwdActivity.this);
				if (mResultNet == 0 || mResultNet == 1) {
					mStatus = reqData.status;
					// ** mResultStatus = reqData.result;
					mTempCode = reqData.tempcode;
					// ** mLimitTime = reqData.limittime;
					Log.i(TAG, "mTempCode is " + mTempCode);
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				switch (mResultNet) {
				case 0:
					if (mStatus != null && mStatus.equals("SUCCESS")) {
						// mLinearLayoutNewPwd.setVisibility(View.VISIBLE);
						mEditTextTempCode.setText(mTempCode);
						mButtonOk.setClickable(true);
						BaseToast("获取验证码成功");
						
						mButtonSend.setClickable(false);
						mButtonSend.setTextColor(color.gray3);
					}
					break;
				case 1:
					messagesManager(Constants.MESSAGE_INTERNET_ERROR);
					break;
				case 2:
					messagesManager(Constants.MESSAGE_SERVER_EXCEPTION2);
					break;
				case -1:
					messagesManager(Constants.MESSAGE_INTERNET_ERROR);
					break;
				case 5:
					messagesManager(Constants.MESSAGE_SERVER_EXCEPTION3);
					break;
				default:
					break;
				}
			}
		}.execute();
	}

	@Override
	public synchronized void onClick(View view) {
		switch (view.getId()) {
		// 密码重设部分
		// case R.id.UpdatePwd_btn_setting:
		// PasswordResetMessage();
		// break;
		case R.id.button_send:
			mEditTextPhoneNum.invalidate();
			mPhoneNum = mEditTextPhoneNum.getText().toString().trim();
			Logger.d(TAG, "mEditTextPhoneNum==" + mPhoneNum);
			sendPwdMessage();
			break;
		case R.id.button_ok:
			//判断短信验证码
			mButtonSend.setClickable(true);
			mButtonSend.setTextColor(Color.rgb(0, 0, 0));
			verifyCodeAndJump();
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onResume() {
		initViews();
		mEditTextPhoneNum.invalidate();
		mPhoneNum = mEditTextPhoneNum.getText().toString().trim();
		super.onResume();
	}

	private void verifyCodeAndJump() {
		if (mPhoneNum == null || "".equals(mPhoneNum)) {
			BaseToast("请先输入手机号并获取验证码");
			return;
		}

		new Thread() {
			@Override
			public void run() {
				super.run();

				mTempCodeForIntent = mEditTextTempCode.getText().toString().trim();
				int verifyTempCode = 0;
				if ("".equals(mTempCodeForIntent)) {
					messagesManager(Constants.MESSAGE_PLS_INPUT_CODE);
					return;
				} else {
					SharedPreferences sp = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
					String selectedserver = sp.getString(SharedPreferredKey.SERVER_NAME, "");
					UpdatePasswordInfo reqData = new UpdatePasswordInfo();
					verifyTempCode = DataSyn.getInstance().verifyTempCode(mPhoneNum, mTempCodeForIntent, "", reqData,selectedserver);
					
				}
				if (verifyTempCode == 0) {
					Logger.i(TAG, "验证成功");
					handler.sendEmptyMessage(0);
				} else {
					messagesManager(Constants.MESSAGE_COMFIRM_FAIL);
					Logger.i(TAG, "验证失败,verifyTempCode=" + verifyTempCode);
				}

			}
		}.start();
	}
}
