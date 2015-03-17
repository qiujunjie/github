package cmcc.mhealth.basic;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cmcc.mhealth.R;
import cmcc.mhealth.common.ConstantsBitmaps;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.net.DataSyn;

public class SampleActivity extends Activity {
	protected TextView mTextViewTitle;// ������
	protected ImageView mBack;// �໬��ť
	protected SharedPreferences sp;
	
	protected String mPhoneNum; //�ֻ���
	protected String mPassword;//����
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		sp = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		super.onCreate(savedInstanceState);
		initSampleViews();
		initClickers();
	}
	//��ʼ��ͨ��view
	private void initSampleViews() {
		mTextViewTitle = (TextView) findViewById(R.id.textView_title);
		mTextViewTitle.setText(getIntent().getExtras().getString("sampletitle"));
		mBack = (ImageView) findViewById(R.id.button_input_bg_back);
		mBack.setBackgroundResource(R.drawable.my_button_back);
		mBack.setVisibility(View.VISIBLE);
	}
	//��ʼ������¼�
	protected void initClickers() {
		mBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SampleActivity.this.finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
			}
		});
	}
	
	private Toast mToast = null;

	protected void BaseToast(String msg) {
		BaseToast(msg, 0);
	}
	protected void BaseToast(String msg, int l) {
		if (mToast == null) {
			mToast = Toast.makeText(getApplicationContext(), msg, l);
		} else {
			mToast.setText(msg);
			mToast.setDuration(l);
		}
		mToast.show();
	}
	
	private void loadNessesaryInfo() {
		mPhoneNum = sp.getString(SharedPreferredKey.PHONENUM, null); // �õ��绰����
		mPassword = sp.getString(SharedPreferredKey.PASSWORD, null); // �õ�����
	}
	
	@Override
	protected void onResume() {
		loadNessesaryInfo();
		String selectedserver = sp.getString(SharedPreferredKey.SERVER_NAME, "");
		if (null != selectedserver && !"".equals(selectedserver)) {
			DataSyn.setStrHttpURL("http://" + selectedserver + "openClientApi.do?action=");
			DataSyn.setAvatarHttpURL("http://" + selectedserver + "UserAvatar/");
		}
		ConstantsBitmaps.initRunPics(SampleActivity.this);
		super.onResume();
	}

}
