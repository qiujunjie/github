package cmcc.mhealth.activity;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cmcc.mhealth.R;
import cmcc.mhealth.basic.BaseActivity;
import cmcc.mhealth.bean.UpdatePasswordInfo;
import cmcc.mhealth.common.Constants;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.net.DataSyn;
//**import android.widget.TextView;
/**
 * �����趨����
 * @author zy
 *
 */
public class PreLoadResetPwdActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = "ResetPwdActivity";
	private String mPhoneNum;
	private String mNewPwd;
	
	private String mStatus;
	private String mTempCode;
	
	private EditText mEditTextPassword;
	private EditText mEditTextPasswordAgain;
	
	private Button forget_UpdatePwd_btn_setting;//ȷ�ϰ�ť
	private ImageButton button_input_bg_back;//���ذ�ť
	
	protected String mResultStatus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resetpwd);
	}
	//��ʼ��
	@SuppressWarnings("deprecation")
	private void initviews() {
		TextView mTextViewTitleBar = (TextView) findViewById(R.id.textView_title);
		mTextViewTitleBar.setText("��������");
		mEditTextPassword = (EditText) findViewById(R.id.newpwd_edit_setting);
		mEditTextPasswordAgain = (EditText) findViewById(R.id.newpwd_ag_edit_setting);

		forget_UpdatePwd_btn_setting = (Button) findViewById(R.id.forget_UpdatePwd_btn_setting);
		button_input_bg_back = (ImageButton) findViewById(R.id.button_input_bg_back);
		button_input_bg_back.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_button_back));
		forget_UpdatePwd_btn_setting.setOnClickListener(this);
		button_input_bg_back.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		initviews();
		Bundle bundle = getIntent().getBundleExtra("fpa_infos");
		
		mTempCode = bundle.getString("mTempCode");
		mPhoneNum = bundle.getString("mPhoneNum");
		
		Logger.i(TAG, "���ݹ�����mTempCode=="+mTempCode);
		Logger.i(TAG, "���ݹ�����mPhoneNum=="+mPhoneNum);
		super.onResume();
	}
	//�����趨����ķ���
	private void PasswordResetMessage() {
		String pwd = mEditTextPassword.getText().toString();
		String pwdAgain = mEditTextPasswordAgain.getText().toString();

		if (pwd == null || pwd.equals("")) {
			BaseToast("���벻��Ϊ��");
			return;
		}
		if (!pwd.equals(pwdAgain)) {
			BaseToast("�������벻һ��");
			return;
		}
		if(pwd.length()<6 || pwd.length() > 50){
			BaseToast("�����������С��λ�������ʮλ");
			return;
		}
		mNewPwd = pwd;

		new AsyncTask<Integer, Void, Integer>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			protected Integer doInBackground(Integer... params) {
				UpdatePasswordInfo reqData = new UpdatePasswordInfo();
				int result = DataSyn.getInstance().passwordReset(mPhoneNum, mTempCode, mNewPwd, reqData);
				if (result == 0) {
					mStatus = reqData.status;
					mResultStatus = reqData.reason;
					Editor sharedata = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();
					sharedata.putString(SharedPreferredKey.PASSWORD, mNewPwd);
					sharedata.commit();
				}
				return result;
			}

			@Override
			protected void onPostExecute(Integer result) {
				switch (result) {
				case 0:
					if (mStatus != null && mStatus.equals("SUCCESS")) {
						BaseToast("�����޸ĳɹ�");
					} else
						BaseToast("�����޸�ʧ��,�����»�ȡ��֤��");
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
	//�����Ӧ
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.forget_UpdatePwd_btn_setting:
			//��������
			PasswordResetMessage();
			break;
		case R.id.button_input_bg_back:
			//������ҳ
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
			break;
		}
		
	}
}
