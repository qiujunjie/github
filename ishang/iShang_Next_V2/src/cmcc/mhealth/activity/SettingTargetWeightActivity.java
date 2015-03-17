package cmcc.mhealth.activity;

import java.util.List;

import android.content.Context;
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

public class SettingTargetWeightActivity extends BaseActivity {
	private EditText mEditText;
	private Context mContext = this;
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			dismiss();
			switch (msg.what) {
			case SimpleNet.SIMPLENET_SUCCESS:
				UiView();
				break;
			case SimpleNet.SIMPLENET_FAIL:
				BaseToast(msg.obj + "");
				break;
			case 10001:
				BaseToast("请输入30到120千克之间的数值", 8);
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_targetweight);
		mEditText = (EditText) findViewById(R.id.edittext_input_your_weight);
		Button button = (Button) findViewById(R.id.button_set_weight);
		mEditText.setText(PreferencesUtils.getString(this, SharedPreferredKey.TARGET_WEIGHT, ""));
		BaseBackKey("设置目标体重", this);
		onFocusChange(true, mEditText);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showProgressDialog("请稍后...", SettingTargetWeightActivity.this);
				new Thread() {
					public void run() {
						List<String> list = PreferencesUtils.getStringArr(SettingTargetWeightActivity.this, null, SharedPreferredKey.SERVER_NAME, SharedPreferredKey.PHONENUM, SharedPreferredKey.PASSWORD);
						for (String string : list) {
							if (string == null||"".equals(string))
								return;
						}

						int weight = Integer.parseInt(mEditText.getText().toString().trim());
						if (weight > 120 || weight < 30) {
							mHandler.sendEmptyMessage(10001);
							return;
						}

						String URL = "http://" + list.get(0) + "/openClientApi.do?action=settargetvalue&userid=" + list.get(1) + "&psw=" + list.get(2) + "&value=" + mEditText.getText().toString().trim() + "&type=target_weight";
						SimpleNet.simpleGet(URL, mHandler, mContext);
					};
				}.start();
			}
		});
	}

	public void UiView() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				BaseToast("体重同步成功！");
				PreferencesUtils.putString(SettingTargetWeightActivity.this, SharedPreferredKey.TARGET_WEIGHT, mEditText.getText().toString().trim());
				SettingTargetWeightActivity.this.finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
			}
		});
	}

}
