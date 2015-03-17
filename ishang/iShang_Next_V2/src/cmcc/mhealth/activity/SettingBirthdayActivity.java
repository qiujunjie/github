package cmcc.mhealth.activity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import cmcc.mhealth.R;
import cmcc.mhealth.basic.BaseActivity;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.common.PreferencesUtils;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.net.SimplePost;

public class SettingBirthdayActivity extends BaseActivity implements OnClickListener {
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
	private Button mButtonYear;
	private Button mButtonMonth;
	private Button mButtonDay;
	private PopupWindow mPopupWindow;
	private String mBirthday;
	private ScrollView mScrollView;
	private String mInput;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_birthday);
		mButtonYear = findView(R.id.btn_year);
		mButtonYear.setOnClickListener(this);
		mButtonMonth = findView(R.id.btn_month);
		mButtonMonth.setOnClickListener(this);
		mButtonDay = findView(R.id.btn_day);
		mButtonDay.setOnClickListener(this);
//		mEditText = (EditText) findViewById(R.id.edittext_input_your_weight);
		Button button = (Button) findViewById(R.id.button_set_weight);
		mBirthday = PreferencesUtils.getString(this, SharedPreferredKey.BIRTHDAY,"");
		if(!TextUtils.isEmpty(mBirthday)){
			String strs[] = mBirthday.split("-");
			mButtonYear.setText(strs[0]);
			mButtonMonth.setText(strs[1]);
			mButtonDay.setText(strs[2]);
		}
		BaseBackKey("设置我的体重", this);
//		onFocusChange(true, mEditText);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mInput = mButtonYear.getText().toString().trim()+"-"+mButtonMonth.getText().toString().trim()+"-"+mButtonDay.getText().toString().trim();
				if (TextUtils.isEmpty(mInput))
					return;
				if (mInput.matches("^[A-Za-z]+$")) {
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
							if (string == null)
								return;
						}
						String URL = "http://" + list.get(0) + "/openClientApi.do?action=setpersonprofile";
//						String URL = "http://phr.cmri.cn/datav2/openClientApi.do?action=setpersonprofile";
						Map<String, String> map = new HashMap<String, String>();
						map.put("userid", list.get(1));
						map.put("psw", list.get(2));
						map.put("birth", mInput);
						SimplePost.iploadData(URL, map,mContext, mHandler);
					};
				}.start();
			}
		});
	}

	public void UiView() {
		BaseToast("体重同步成功！");
		PreferencesUtils.putString(SettingBirthdayActivity.this, SharedPreferredKey.BIRTHDAY, mInput);
		SettingBirthdayActivity.this.finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.silde_out_right);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_year:
			int datay[] = new int[70];
			if(mBirthday != null && !mBirthday.equals("")){
				String date = Common.getCurrentDayLongTime(new Date().getTime());
				String str[] = date.split("-");
				int year = Integer.valueOf(str[0]);
				for (int i = 0; i < 70; i++) {
					datay[i] = year -i;
				}
			}
			creatPopWindow(datay, findView(R.id.btn_year),mButtonYear);
			break;
		case R.id.btn_month:
			int datam[] = new int[12];
			if(mBirthday != null && !mBirthday.equals("")){
//				String date = Common.getCurrentDayLongTime(new Date().getTime());
//				String str[] = date.split("-");
//				int year = Integer.valueOf(str[1]);
				for (int i = 0; i < 12; i++) {
					datam[i] = i+1;
				}
			}
			creatPopWindow(datam, findView(R.id.btn_month),mButtonMonth);
			break;
		case R.id.btn_day:
			int data[] = new int[31];
//			int numDate[]={31,29,};
			if(mBirthday != null && !mBirthday.equals("")){
//				String date = Common.getCurrentDayLongTime(new Date().getTime());
//				String str[] = date.split("-");
//				int year = Integer.valueOf(str[1]);
				for (int i = 0; i < 31; i++) {
					data[i] = i+1;
				}
			}
			creatPopWindow(data, findView(R.id.btn_day),mButtonDay);
			break;

		default:
			break;
		}
	}
	private void creatPopWindow(int[] strs,View location,final Button button) {
		View view = View.inflate(this, R.layout.popup_scroll, null);
		mScrollView =(ScrollView) view.findViewById(R.id.scroll);
		LinearLayout layout = (LinearLayout)view.findViewById(R.id.linear_scroll);
		for (int i = 0; i < strs.length; i++) {
			final TextView textView = new TextView(mContext);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);  // , 1是可选写的
			textView.setLayoutParams(lp);
			lp.setMargins(0, 5, 0, 0);
			lp.gravity = Gravity.CENTER;
			textView.setTextColor(getResources().getColor(R.color.black));
			textView.setText(strs[i]+"");
			textView.setBackgroundResource(R.drawable.bg_calender_nextmonth);
			textView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					TextView textView = (TextView) v;
					String year = textView.getText().toString();
					button.setText(year);
					mPopupWindow.dismiss();
				}
			});
			layout.addView(textView);
		}
		mPopupWindow = new PopupWindow(view, Common.dip2px(this, 120), Common.dip2px(this, 200));
		mPopupWindow.setFocusable(false);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setAnimationStyle(R.style.PopupAnimation2);
		mPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_frame));
		mPopupWindow.showAsDropDown(location);
	}
}
