package cmcc.mhealth.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import cmcc.mhealth.net.SimplePost;

public class SettingHeightAvtivity extends BaseActivity {
	private Context mContext = SettingHeightAvtivity.this;
	private EditText mEditText;
	
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
				BaseToast("请输入50厘米与250厘米之间的数值", 8);
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_height);

		mEditText = (EditText) findViewById(R.id.edittext_input_your_height);
		onFocusChange(true, mEditText);
		Button button = (Button) findViewById(R.id.button_set_height);
		mEditText.setText(PreferencesUtils.getString(this, SharedPreferredKey.HEIGHT, ""));
		BaseBackKey("设置身高", this);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showProgressDialog("请稍后...", SettingHeightAvtivity.this);
				new Thread() {
					public void run() {
						List<String> list = PreferencesUtils.getStringArr(SettingHeightAvtivity.this, null, SharedPreferredKey.SERVER_NAME, SharedPreferredKey.PHONENUM, SharedPreferredKey.PASSWORD);
						for (String string : list) {
							if (string == null||"".equals(string))
								return;
						}

						int height = Integer.parseInt(mEditText.getText().toString().trim());
						if (height > 250 || height < 50) {
							mHandler.sendEmptyMessage(10001);
							return;
						}

						String URL = "http://" + list.get(0) + "/openClientApi.do?action=setpersonprofile";
						Map<String, String> map = new HashMap<String, String>();
						map.put("userid", list.get(1));
						map.put("psw", list.get(2));
						map.put("height", mEditText.getText().toString().trim());
						SimplePost.iploadData(URL, map,mContext, mHandler);
					};
				}.start();
			}
		});
	}

	public void UiView() {
		BaseToast("身高同步成功！");
		PreferencesUtils.putString(SettingHeightAvtivity.this, SharedPreferredKey.HEIGHT, mEditText.getText().toString().trim());
		SettingHeightAvtivity.this.finish();
		overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
	}
}
