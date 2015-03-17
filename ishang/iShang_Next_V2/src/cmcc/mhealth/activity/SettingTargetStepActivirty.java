package cmcc.mhealth.activity;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import cmcc.mhealth.R;
import cmcc.mhealth.basic.BaseActivity;
import cmcc.mhealth.common.PreferencesUtils;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.net.SimpleNet;

public class SettingTargetStepActivirty extends BaseActivity {
	private Context mContext = this;
	private EditText mEditTextTargetNum;
	private Button mButtonOK;
	
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			dismiss();
			switch (msg.what) {
			case 0:
				UiView();
				break;
			case 1:
				BaseToast(msg.obj+"");
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settting_target);
		mEditTextTargetNum = (EditText) findViewById(R.id.setting_num);

		SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		String targetnum = sharedPreferences.getString(SharedPreferredKey.TARGET_STEP, "10000");
		mEditTextTargetNum.setText(targetnum);
		mEditTextTargetNum.setFocusable(true);
		mEditTextTargetNum.requestFocus();
		onFocusChange(true, mEditTextTargetNum);

		mButtonOK = (Button) findViewById(R.id.setting_targer_ok);

		BaseBackKey("设置运动目标", this);
		mButtonOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String updateTarget = mEditTextTargetNum.getText().toString();
				// Regex
				if (updateTarget.matches("^[1-9]\\d{3,4}$")) {
					showProgressDialog("请稍后...", SettingTargetStepActivirty.this);
					new Thread() {
						public void run() {
							try {
								List<String> list = PreferencesUtils.getStringArr(SettingTargetStepActivirty.this, null,
										SharedPreferredKey.SERVER_NAME, SharedPreferredKey.PHONENUM,
										SharedPreferredKey.PASSWORD);
								for (String string : list) {
									if (string == null||"".equals(string))
										return;
								}
								String URL = "http://" + list.get(0)
										+ "/openClientApi.do?action=settargetvalue&userid=" + list.get(1) + "&psw="
										+ list.get(2) + "&value=" + mEditTextTargetNum.getText().toString().trim()
										+ "&type=target_step";
								SimpleNet.simpleGet(URL,mHandler,mContext);
							} catch (Exception e) {
								e.printStackTrace();
								BaseToast("网络异常");
							} finally {

							}
						}
					}.start();
				} else {
					BaseToast("请输入1000-99999之间的整数");
				}
			}
		});
	}

	private void UiView() {
		BaseToast("目标步数同步成功！");
		Editor sharedata = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();
		sharedata.putString(SharedPreferredKey.TARGET_STEP, mEditTextTargetNum.getText().toString().trim());
		sharedata.commit();
		SettingTargetStepActivirty.this.finish();
		overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
	};
}
