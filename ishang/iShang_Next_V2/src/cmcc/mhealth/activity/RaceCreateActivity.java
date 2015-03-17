package cmcc.mhealth.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import cmcc.mhealth.R;
import cmcc.mhealth.adapter.RacePicTitleAdapter;
import cmcc.mhealth.basic.SampleActivity;
import cmcc.mhealth.bean.BackInfo;
import cmcc.mhealth.common.AlertDialogs;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.net.DataSyn;

public class RaceCreateActivity extends SampleActivity {
	protected static final String TAG = "RaceCreateActivity";
	// views
	private TextView mSelectType;// ѡ������
	private EditText mRaceTitleInput;// ���뾺������
	private ViewPager mMainPicVP;// ѡ��ͼ
	private EditText mTimeFrom;// �Ӻ�ʱ��ʼ
	private EditText mTimeTo;// ����ʱ����
	private TextView mInviting;// ��������
	private EditText mBetCoin;// ��ע����
	private RadioGroup mRewardType;// ��������
	private EditText mRaceDetail;// ��������
	private TextView mBtnCreate;// ������ť

	private ImageView mArrowLeft;
	private ImageView mArrowRight;

	private RadioButton mRadioZenichiBtn;// ǰһ
	private RadioButton mRadioZensanBtn;// ǰ��
	private TextView mRadioZensanText;// ǰ��text
	//
	private RacePicTitleAdapter mPicAdapter;

	// ��¼�Ĳ���
	private int mRecordSelectType;
	private int mRecordRewardType;
	private String invitedStrs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_create_race);
		super.onCreate(savedInstanceState);
		initView();
	}

	private void initView() {
		findViews();
		setViews();
		setClickers();
	}

	private void findViews() {
		mSelectType = (TextView) findViewById(R.id.acr_select_type);
		mRaceTitleInput = (EditText) findViewById(R.id.acr_et_title);
		mMainPicVP = (ViewPager) findViewById(R.id.acr_viewpager);
		mTimeFrom = (EditText) findViewById(R.id.acr_select_timefrom);
		mTimeTo = (EditText) findViewById(R.id.acr_select_timeto);
		mInviting = (TextView) findViewById(R.id.acr_inviting);
		mBetCoin = (EditText) findViewById(R.id.arc_bet_coin_num);
		mRewardType = (RadioGroup) findViewById(R.id.acr_radiogroup);
		mRaceDetail = (EditText) findViewById(R.id.arc_detail);
		mBtnCreate = (TextView) findViewById(R.id.arc_btn_create);
		mRadioZensanBtn = (RadioButton) findViewById(R.id.radio_zensan);
		mRadioZenichiBtn = (RadioButton) findViewById(R.id.radio_zenichi);
		mRadioZensanText = (TextView) findViewById(R.id.acr_radio_text_zensan);
		mArrowLeft = (ImageView) findViewById(R.id.acr_arrow_left);
		mArrowRight = (ImageView) findViewById(R.id.acr_arrow_right);
	}

	private void setViews() {

		mPicAdapter = new RacePicTitleAdapter(this);
		mMainPicVP.setAdapter(mPicAdapter);
		mInviting.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // �»���
		mInviting.getPaint().setAntiAlias(true);
		mRecordRewardType = 1;
		mRecordSelectType = -1;
		invitedStrs = "";

		mArrowLeft.setVisibility(View.INVISIBLE);
		mMainPicVP.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				if (arg0 == 0) {
					mArrowLeft.setVisibility(View.INVISIBLE);
				} else if (arg0 == 13) {
					mArrowRight.setVisibility(View.INVISIBLE);
				} else {
					mArrowLeft.setVisibility(View.VISIBLE);
					mArrowRight.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		Calendar mycalendar = Calendar.getInstance(Locale.CHINA);
		Date mydate = new Date();
		mycalendar.setTime(mydate);
		mycalendar.add(Calendar.DAY_OF_YEAR, 3);
		int year = mycalendar.get(Calendar.YEAR);
		int month = mycalendar.get(Calendar.MONTH);
		int day = mycalendar.get(Calendar.DAY_OF_MONTH);
		mTimeFrom.setText(year + "-" + (month + 1) + "-" + day);
		mycalendar.add(Calendar.DAY_OF_YEAR, 7);
		year = mycalendar.get(Calendar.YEAR);
		month = mycalendar.get(Calendar.MONTH);
		day = mycalendar.get(Calendar.DAY_OF_MONTH);
		mTimeTo.setText(year + "-" + (month + 1) + "-" + day);
	}
	
	private void setClickers() {
		mSelectType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String[] strs = new String[] { "����", "����", "�Ŷ�" };
				AlertDialogs.creatSingleChoiceDialog("ѡ������", RaceCreateActivity.this, strs, mRecordSelectType, new AlertDialogs.onChoicedTeamListener() {
					@Override
					public void onChoicedTeam(int team) {
						mRecordSelectType = team;
						mSelectType.setText(strs[team]);
						if (team == 0) {
							mRadioZensanBtn.setVisibility(View.GONE);
							mRadioZensanText.setVisibility(View.GONE);
							mRadioZenichiBtn.setChecked(true);
							mRecordRewardType = 1;
						} else {
							mRadioZensanBtn.setVisibility(View.VISIBLE);
							mRadioZensanText.setVisibility(View.VISIBLE);
						}
					}
				});
			}
		});

		mTimeFrom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar mycalendar = Calendar.getInstance(Locale.CHINA);
				Date mydate = new Date();
				mydate.setTime(Common.getDateFromStr(mTimeFrom.getText().toString()));
				mycalendar.setTime(mydate);
				int year = mycalendar.get(Calendar.YEAR);
				int month = mycalendar.get(Calendar.MONTH);
				int day = mycalendar.get(Calendar.DAY_OF_MONTH);
				DatePickerDialog dpd = new DatePickerDialog(RaceCreateActivity.this, new OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						mTimeFrom.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
					}
				}, year, month, day);
				dpd.show();// ��ʾDatePickerDialog���
			}
		});

		mTimeTo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar mycalendar = Calendar.getInstance(Locale.CHINA);
				Date mydate = new Date();
				mydate.setTime(Common.getDateFromStr(mTimeTo.getText().toString()));
				mycalendar.setTime(mydate);
				int year = mycalendar.get(Calendar.YEAR);
				int month = mycalendar.get(Calendar.MONTH);
				int day = mycalendar.get(Calendar.DAY_OF_MONTH);
				DatePickerDialog dpd = new DatePickerDialog(RaceCreateActivity.this, new OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						mTimeTo.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
					}
				}, year, month, day);
				dpd.show();// ��ʾDatePickerDialog���
			}
		});

		mInviting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mRecordSelectType == -1) {
					BaseToast("����δѡ��������", 5);
					return;
				}
				Intent intent = new Intent(RaceCreateActivity.this, RaceInviteActivity.class);
				intent.putExtra("sampletitle", "����");
				if (mRecordSelectType == 0) {
					intent.putExtra("getone", "true");
				} else {
					intent.putExtra("getone", "false");
				}
				startActivityForResult(intent, 1);
				overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
			}
		});

		mRewardType.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.radio_zenichi:
					mRecordRewardType = 1;
					break;
				case R.id.radio_zensan:
					mRecordRewardType = 2;
					break;
				}
			}
		});

		mBtnCreate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				varifyValuesAndPost();
			}

		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				invitedStrs = (String) extras.get("phoneStrs");
			}
		}
	}

	// У�����ݲ����ʹ�������
	private void varifyValuesAndPost() {
		if (mRecordSelectType == -1) {
			BaseToast("��ѡ������", 5);
			return;
		}
		if (mRaceTitleInput.getText().toString().length() == 0) {
			BaseToast("���������", 5);
			return;
		}

		long from = Common.getDateFromStr(mTimeFrom.getText().toString());
		long to = Common.getDateFromStr(mTimeTo.getText().toString());
		if (from > to) {
			BaseToast("��ʼ�����ʱ�����ô���", 5);
			return;
		}

		if (from < new Date().getTime()) {
			BaseToast("��ʼʱ����ֻ���ǰʱ����", 5);
			return;
		}

		if (mBetCoin.getText().toString().length() == 0) {
			BaseToast("���������", 5);
			return;
		}

		if (Integer.parseInt(mBetCoin.getText().toString()) < 1) {
			BaseToast("��������Ϊ1", 5);
			return;
		}
		
		if (mRaceDetail.getText().toString().length() == 0) {
			BaseToast("�����뾺������~", 5);
			return;
		}

		createRaceNow(from, to + 86399000l);
	}

	// �������󴴽�
	private void createRaceNow(final long from, final long to) {
		mBtnCreate.setClickable(false);
		mBtnCreate.setBackgroundResource(R.drawable.btn_cancel);
		new Thread() {
			public void run() {
				List<NameValuePair> listvp = new ArrayList<NameValuePair>();
				listvp.add(new BasicNameValuePair("selectType", "" + (mRecordSelectType + 1)));
				listvp.add(new BasicNameValuePair("raceTitleInput", mRaceTitleInput.getText().toString()));
				listvp.add(new BasicNameValuePair("mainPicVP", "" + (mMainPicVP.getCurrentItem() + 1)));
				listvp.add(new BasicNameValuePair("timeFrom", (from / 1000l) + ""));
				listvp.add(new BasicNameValuePair("timeTo", (to / 1000l) + ""));
				listvp.add(new BasicNameValuePair("inviting", invitedStrs));
				listvp.add(new BasicNameValuePair("betCoin", mBetCoin.getText().toString()));
				listvp.add(new BasicNameValuePair("raceDetail", mRaceDetail.getText().toString()));
				listvp.add(new BasicNameValuePair("rewardType", "" + mRecordRewardType));
				listvp.add(new BasicNameValuePair("ispublic", "1"));
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
				setResult(RESULT_OK);
				RaceCreateActivity.this.finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
				break;
			case NET_FAIL:
				mBtnCreate.setClickable(true);
				mBtnCreate.setBackgroundResource(R.drawable.sample_button_click_bg);
				BaseToast("��ȷ�����糩ͨ", 5);
				break;
			}
		};
	};

}
