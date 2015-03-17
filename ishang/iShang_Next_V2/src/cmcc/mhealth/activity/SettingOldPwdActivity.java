package cmcc.mhealth.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cmcc.mhealth.R;
import cmcc.mhealth.basic.BaseActivity;
import cmcc.mhealth.common.SharedPreferredKey;

public class SettingOldPwdActivity extends BaseActivity {
	private TextView mTextViewTitle;
	private ImageButton mImageButtonBack;
	private EditText mEditTextPwd;
	private Button mProvingButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_settting_update_pwd);
		initView();
		initClick();
		setView();
	}

	private void setView() {
		// TODO Auto-generated method stub
		mTextViewTitle.setText("修改密码");
	}

	private void initClick() {
		// TODO Auto-generated method stub

		mImageButtonBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SettingOldPwdActivity.this.finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
			}
		});
		
		mProvingButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SharedPreferences sp = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
				String pwd = sp.getString(SharedPreferredKey.PASSWORD, null); // 拿到密码
				String editPwd = mEditTextPwd.getText().toString();//文本密码
				if(editPwd.equals(pwd)){
					//验证成功,跳转activity
					Intent intent = new Intent();
					intent.setClass(SettingOldPwdActivity.this,SettingNewPwdActivity.class);
					startActivity(intent);
					SettingOldPwdActivity.this.finish();
					overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
				}else{
					BaseToast("密码输入错误!");
				}
			}
		});

	}

	@SuppressWarnings("deprecation")
	private void initView() {
		mTextViewTitle = (TextView) findViewById(R.id.textView_title);
		mImageButtonBack = (ImageButton) findViewById(R.id.button_input_bg_back);
		mImageButtonBack.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_button_back));
		mImageButtonBack.setVisibility(View.VISIBLE);
		mEditTextPwd = (EditText) findViewById(R.id.updatepwd_edittext_setting);
		mEditTextPwd.setFocusable(true);
		mEditTextPwd.requestFocus();
		onFocusChange(mEditTextPwd.isFocusable());
		
		mProvingButton = (Button) findViewById(R.id.proving_btn_setting);
	}

	private void onFocusChange(boolean hasFocus){
		final boolean isFocus = hasFocus;
		(new Handler()).postDelayed(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager)mEditTextPwd.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				if(isFocus){
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				}else{
					imm.hideSoftInputFromWindow(mEditTextPwd.getWindowToken(),0);
				}
				
			}
		}, 100);
	}
}
