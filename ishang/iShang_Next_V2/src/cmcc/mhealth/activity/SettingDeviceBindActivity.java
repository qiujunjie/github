package cmcc.mhealth.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import cmcc.mhealth.R;
import cmcc.mhealth.adapter.SettingDeviceBindAdapter;
import cmcc.mhealth.basic.SampleActivity;
import cmcc.mhealth.bean.BackInfo;
import cmcc.mhealth.net.DataSyn;

public class SettingDeviceBindActivity extends SampleActivity {
	protected static final String TAG = "DeviceBindActivity";
	// views
	private ViewPager mMainPicVP;// 选择图
	private TextView mBtnCreate;// 创建按钮
	private TextView mTVDeviceDescript;// 设备描述
	
	private ImageView mArrowLeft;
	private ImageView mArrowRight;
	
	private RadioGroup mRadioGroup;
	
	//
	private SettingDeviceBindAdapter mPicAdapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_setting_devicebind);
		super.onCreate(savedInstanceState);
		initView();
	}

	private void initView() {
		findViews();
		setViews();
		setClickers();
	}

	private void findViews() {
		mMainPicVP = (ViewPager) findViewById(R.id.asd_viewpager);
		mBtnCreate = (TextView) findViewById(R.id.asd_btn_create);
		mTVDeviceDescript = (TextView) findViewById(R.id.asd_select_device_des);
		mArrowLeft = (ImageView) findViewById(R.id.asd_arrow_left);
		mArrowRight = (ImageView) findViewById(R.id.asd_arrow_right);
		mRadioGroup = (RadioGroup) findViewById(R.id.asd_bind_radiogroup);
	}

	private void setViews() {

		mPicAdapter = new SettingDeviceBindAdapter(this);
		mMainPicVP.setAdapter(mPicAdapter);
		
		mArrowLeft.setVisibility(View.INVISIBLE);

		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.asd_bind_item1:
					mMainPicVP.setCurrentItem(0, true);
					mTVDeviceDescript.setText(R.string.selectdevice0descript);
					break;
				case R.id.asd_bind_item2:
					mMainPicVP.setCurrentItem(1, true);
                    mTVDeviceDescript.setText(R.string.selectdevice1descript);
					break;
				case R.id.asd_bind_item3:
					mMainPicVP.setCurrentItem(2, true);
                    mTVDeviceDescript.setText(R.string.selectdevice2descript);
					break;
				case R.id.asd_bind_item4:
					mMainPicVP.setCurrentItem(3, true);
                    mTVDeviceDescript.setText(R.string.selectdevice3descript);
					break;
				}
			}
		});
		((RadioButton) mRadioGroup.findViewById(R.id.asd_bind_item1)).setChecked(true);
		mMainPicVP.setOnPageChangeListener(new OnPageChangeListener() {
			private int[] tempRs= new int[]{R.id.asd_bind_item1,R.id.asd_bind_item2,R.id.asd_bind_item3,R.id.asd_bind_item4};
			@Override
			public void onPageSelected(int arg0) {
				((RadioButton) mRadioGroup.findViewById(tempRs[arg0])).setChecked(true);
				arrowChanges(arg0);
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});
	}
	private void arrowChanges(int arg0) {
		if(arg0 ==0){
			mArrowLeft.setVisibility(View.INVISIBLE);
			mArrowRight.setVisibility(View.VISIBLE);
		}else if(arg0 == 3){
			mArrowRight.setVisibility(View.INVISIBLE);
			mArrowLeft.setVisibility(View.VISIBLE);
		}else{
			mArrowLeft.setVisibility(View.VISIBLE);
			mArrowRight.setVisibility(View.VISIBLE);
		}
	}

	private void setClickers() {
		mBtnCreate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}

		});

	}


	// 发送请求创建
	private void createRaceNow(final long from, final long to) {
		new Thread() {
			public void run() {
				List<NameValuePair> listvp = new ArrayList<NameValuePair>();
				listvp.add(new BasicNameValuePair("mainPicVP", "" + (mMainPicVP.getCurrentItem() + 1)));
				BackInfo reqData = new BackInfo();
				switch (DataSyn.getInstance().createRace(mPhoneNum, mPassword, listvp, reqData)) {
				case 0:
					Message msg = Message.obtain();
					msg.what = SUCCESS;
					Bundle data = new Bundle();
					data.putParcelable("backinfo", reqData);
					msg.setData(data);
					handler.sendMessage(msg);
					break;
				default:
					handler.sendEmptyMessage(NET_FAIL);
					break;
				}
			};
		}.start();
	}

	private final static int SUCCESS = 0;
	private final static int NET_FAIL = 1;
	private Handler handler = new Handler() {
		public void dispatchMessage(Message msg) {
			Bundle data = msg.getData();
			switch (msg.what) {
			case SUCCESS:
				BackInfo reqData = data.getParcelable("backinfo");
				BaseToast(reqData.reason);
				SettingDeviceBindActivity.this.finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
				break;
			case NET_FAIL:
				BaseToast("请确认网络畅通", 5);
				break;
			}
		};
	};

}
