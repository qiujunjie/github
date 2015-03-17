package cmcc.mhealth.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import cmcc.mhealth.R;
import cmcc.mhealth.basic.BaseActivity;
import cmcc.mhealth.common.PreferencesUtils;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.net.SimplePost;

public class SettingWeightActivity extends BaseActivity {
	private EditText mEditText;
	private Context mContext = this;
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			dismiss();
			switch (msg.what) {
			case SimplePost.SIMPLENET_SUCCESS:
				UiView();
				break;
			case SimplePost.SIMPLENET_FAIL:
				BaseToast(msg.obj + "");
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_weight);
		mEditText = (EditText) findViewById(R.id.edittext_input_your_weight);
		Button button = (Button) findViewById(R.id.button_set_weight);
		mEditText.setText(PreferencesUtils.getString(this, SharedPreferredKey.WEIGHT, ""));
		BaseBackKey("设置我的体重", this);
		onFocusChange(true, mEditText);

		button.setOnClickListener(new OnClickListener() {

			private String input;

			@Override
			public void onClick(View v) {
				input = mEditText.getText().toString().trim();
				if (TextUtils.isEmpty(input))
					return;
				if (input.matches("^[A-Za-z]+$")) {
					messagesManager(R.string.string_message_1);
					return;
				}
				// int weight = Integer.parseInt(input);
				// if (weight > 120 || weight < 30) {
				// mHandler.sendEmptyMessage(10001);
				// return;
				// }
				showProgressDialog("请稍后...",mContext);
				new Thread() {
					public void run() {
						List<String> list = PreferencesUtils.getStringArr(mContext, null,
								SharedPreferredKey.SERVER_NAME, SharedPreferredKey.PHONENUM,
								SharedPreferredKey.PASSWORD);
						for (String string : list) {
							if (string == null||"".equals(string))
								return;
						}
//						String URL = "http://phr.cmri.cn/datav2/openClientApi.do?action=setpersonprofile";
						String URL = "http://" + list.get(0) + "/openClientApi.do?action=setpersonprofile";
						Map<String, String> map = new HashMap<String, String>();
						map.put("userid", list.get(1));
						map.put("psw", list.get(2));
						map.put("weight", input);
						SimplePost.iploadData(URL, map,mContext, mHandler);
					};
				}.start();
			}
		});
	}

	public void UiView() {
		BaseToast("体重同步成功！");
		PreferencesUtils.putString(SettingWeightActivity.this, SharedPreferredKey.WEIGHT, mEditText.getText()
				.toString().trim());
		SettingWeightActivity.this.finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.silde_out_right);
	}

}
