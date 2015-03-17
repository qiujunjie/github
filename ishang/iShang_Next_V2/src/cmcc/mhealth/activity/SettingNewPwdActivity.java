package cmcc.mhealth.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import cmcc.mhealth.R;
import cmcc.mhealth.basic.BaseActivity;
import cmcc.mhealth.bean.UpdatePasswordInfo;
import cmcc.mhealth.common.Constants;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.net.DataSyn;
import cmcc.mhealth.slidingcontrol.RankFragment;

public class SettingNewPwdActivity extends BaseActivity {
	private EditText mEditTextNewPwd;
	private EditText mEditTextNewPwdAg;
	private Button mButtom;
	private String mPwd;
	private String mOldPwd;
	private String mNewpwd;
	private String mNewpwdAg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settting_update_newpwd);
		initView();
		initClick();
	}

	private void initClick() {

		mButtom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mNewpwd = mEditTextNewPwd.getText().toString();
				mNewpwdAg = mEditTextNewPwdAg.getText().toString();
				
				if(mNewpwd.length() <6 || mNewpwd.length()>50){
					BaseToast("���볤��Ӧ����λ���ϣ���ʮλ����");
					return;
				}
				if (mNewpwd.equals(mNewpwdAg)) {
					/* �����ж� */
					new Thread() {
						public void run() {
							// �����޸�����
							UpdatePasswordInfo reqData = new UpdatePasswordInfo();
							int res = DataSyn.getInstance().updatePassWord(mPwd, mOldPwd, mNewpwd, mNewpwd, reqData);
							if (res == 0) {
								// �޸ĳɹ�
								Editor sharedata = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();
								sharedata.putString(SharedPreferredKey.PASSWORD, mNewpwd);
								RankFragment.mPassword = mNewpwd;
								sharedata.commit();
								messagesManager(Constants.MESSAGE_UPDATE_PWD_SUCCESS);
								SettingNewPwdActivity.this.finish();
								overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
								// toast("�޸ĳɹ�!");
							} else {
								// �������
								messagesManager(Constants.MESSAGE_INTERNET_ERROR);
								// toast("�������");
							}
						};
					}.start();
				} else {
					BaseToast("�����������벻��ȷ");
				}
			}
		});
	}

	private void initView() {
		// TODO Auto-generated method stub
		mEditTextNewPwd = (EditText) findViewById(R.id.newpwd_edit_setting);
		mEditTextNewPwd.setFocusable(true);
		mEditTextNewPwd.requestFocus();
		onFocusChange(true, mEditTextNewPwd);
		mEditTextNewPwdAg = (EditText) findViewById(R.id.newpwd_ag_edit_setting);
		mButtom = (Button) findViewById(R.id.UpdatePwd_btn_setting);

		BaseBackKey("�޸�����", this);

		SharedPreferences sp = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		mPwd = sp.getString(SharedPreferredKey.PHONENUM, "");
		mOldPwd = sp.getString(SharedPreferredKey.PASSWORD, "");
	}
}
