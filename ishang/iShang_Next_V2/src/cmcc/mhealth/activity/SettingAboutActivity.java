package cmcc.mhealth.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import cmcc.mhealth.R;
import cmcc.mhealth.basic.BaseActivity;

public class SettingAboutActivity extends BaseActivity {
	private ImageView mImageView;
//	private TextView mTextViewTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		mImageView = findView(R.id.setting_about);
		mImageView.setVisibility(View.VISIBLE);
//		mTextViewTitle = (TextView) findViewById(R.id.textView_title);
//		mTextViewTitle.setText("关于我尚运动");

		mImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SettingAboutActivity.this.finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
			return true;
		}
		return false;
	}
}
