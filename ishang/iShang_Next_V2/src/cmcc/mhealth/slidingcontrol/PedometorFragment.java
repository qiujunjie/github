package cmcc.mhealth.slidingcontrol;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cmcc.mhealth.R;
import cmcc.mhealth.basic.BaseFragment;
import cmcc.mhealth.bean.DataDetailPedo;
import cmcc.mhealth.bean.DataPedometor;
import cmcc.mhealth.bean.PedoDetailInfo;
import cmcc.mhealth.bean.PedometorInfo;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Constants;
import cmcc.mhealth.common.ConstantsBitmaps;
import cmcc.mhealth.common.GetWindowBitmap;
import cmcc.mhealth.common.ImageUtil;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.common.Rotate3dAnimation;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.db.MHealthProviderMetaData;
import cmcc.mhealth.net.DataSyn;
import cmcc.mhealth.net.NetworkTool;
import cmcc.mhealth.view.CenterRollingBall;
import cmcc.mhealth.view.DownFlashView;
import cmcc.mhealth.view.DownFlashView.RefreshListener;
import com.cmcc.ishang.lib.step.StepController;
import com.cmcc.ishang.lib.step.detector.StepService;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;
import com.umeng.socialize.bean.CustomPlatform;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.OnCustomPlatformClickListener;
import com.umeng.socialize.media.UMediaObject;

public class PedometorFragment extends BaseFragment implements OnClickListener, RefreshListener {

	private static String TAG = "PedoBriefActivity";
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat df_Mx_dx = new SimpleDateFormat("MM/dd");

	public static final int BRIEF_TODAY = 1;
	public static final int BRIEF_7DAY = 7;
	public static final int BRIEF_30DAY = 30;
	public static final int BRIEF_STEPNUM = 10;
	public static final int BRIEF_STRENGTH = 11;
	private static final int THUMB_SIZE = 150;

	private static int UPDATE_NUM = 40; // Ĭ�ϸ�������
	
	// 0 = ���� // 1 = �ֻ� ...
	private static int mEquipmentType = 0;
	private Button mEquipmentSwitcher;
	private Button mServiceActivite;

	private TextView mTextViewTitle;

	private ImageButton mImageButtonUpdate;
	

	private TextView mTextViewCal = null;
	private TextView mTextViewStepnum = null;
	private TextView mTextViewDistance = null;
	private TextView mTextViewDuration = null;
	
	private TextView mTextViewUpdateDate = null;

	private GraphicalView mHistogramChartview;
	private LinearLayout mLinearLayoumActivitytogram;

	private RadioGroup mRadioGroupTime, mRadioGroupDayDetail;
	private RelativeLayout mProgressbarStep = null;
	private ImageView mImageViewLeft = null;
	private ImageView mImageViewRight = null;

	private PedoDetailInfo mPedoDetailInfo;
	private DataPedometor mPedoInfo;
	private ArrayList<DataPedometor> mPedoInfoList;
	private String mCurrentDay;

	public int mPreDay = 0;
	public int mPreWeek = 0;
	public int mPreMonth = 0;

	private int mStepNumSumMax;
	private double mStrengthSumMax;

	private int mMostStepNum;
	private float mMostCal;

	private int mFlagUpdateStatus;
	private int mFlagLogin = 2; // login �����ж� 0�˺Ų��ȸ��� 1�˺���Ȳ����� 2 ����

	private int mFlagStepNum = BRIEF_STRENGTH;
	private int mFlagDayNum = BRIEF_TODAY;
	private IWXAPI mWeiXinAPI;

	private float firstX;
	private float secondX;

	private ImageButton mBack;

	public PedometorFragment() {
	}

	public PedometorFragment(String name) {
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_pedo_brief, container, false);
		super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		initStepController();
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	StepController mStepController;
	private void initStepController() {
		mStepController = new StepController();
		mStepController.setContext(mActivity);
		if (isServiceRunning()) {
			mEquipmentType = 1;
			mServiceActivite.setText("�ر��ֻ��Ʋ�");
		} else {
			mEquipmentType = 0;
			mServiceActivite.setText("�����ֻ��Ʋ�");
		}
	}

	@Override
	public void findViews() {
		
		mWeiXinAPI = WXAPIFactory.createWXAPI(mActivity, Constants.APP_ID, true);
		mWeiXinAPI.registerApp(Constants.APP_ID);

		mRelativeLayoutProgress = findView(R.id.Progress_center_rote1);
		initViews();

		SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		mFlagLogin = info.getInt("fromLogin", 2);

		// if (NetworkTool.getNetworkState(mActivity) != 0) {
		// new UpdateDataThread().update(0);
		// }
		Editor sharedata = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();
		sharedata.putInt("fromLogin", 2);
		sharedata.commit();
		mBack = findView(R.id.button_input_bg_back);
		mBack.setVisibility(View.VISIBLE);
	}

	@Override
	public void clickListner() {
		mBack.setOnClickListener(this);
	}

	@Override
	public void loadLogic() {
		mRefreshableView = (DownFlashView) findView(R.id.refresh_root);
		mRefreshableView.setRefreshListener(this);
		String string = Common.getDateAsYYYYMMDD(new Date().getTime());
		switch (mEquipmentType) {
		case 0:
			if (!string.equals(sp.getString("pedo_update_time", null))) {
				mRefreshableView.startRefreshDirectly();
			}
			break;
		case 1:
			getMobilePedDataFromDatabase();
			break;
		}
	}

	private void getMobilePedDataFromDatabase() {
		mPedoInfo = MHealthProviderMetaData.GetMHealthProvider(mActivity).getMobilePedometerLatest();
		SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		int max = Integer.valueOf(info.getString(SharedPreferredKey.TARGET_STEP, "10000"));
		int currentstep = 0;
		if (mPedoInfo!= null) {
			currentstep = Integer.valueOf(mPedoInfo.data.stepNum);
		}
		addProgressView(10000, currentstep);
	}

	private void addProgressView(int max, int value) {
		mCenterRollingBall = new CenterRollingBall(mActivity);
		mCenterRollingBall.setPics(ConstantsBitmaps.mBitmapBgCenterRound, ConstantsBitmaps.mBitmapPointRound);
		mCenterRollingBall.setMaxScore(max);
		mCenterRollingBall.setScore(value);
		mCenterRollingBall.invalidate();
		mRelativeLayoutProgress.removeAllViews();
		mRelativeLayoutProgress.addView(mCenterRollingBall);
		if (mTextViewStepPercent == null)
			mTextViewStepPercent = findView(R.id.textview_percentstep);
		int val = 0;
		if (max != 0) {
			val = value * 100 / max;
		}
		mTextViewStepPercent.setText(val + "%");
		mTextViewRunStep = findView(R.id.stepnumofday);
		mTextViewRunStep.setText(value + "");
	}

	@Override
	public void onResume() {
		super.onResume();
		// mImageButtonUpdate.startAnimation(animation)
		if(mEquipmentType == 1){
			registerStepReceiver();
			sendAllowToReceiver(mActivity,true);
		}else{
			getOldStatusOfDayWeekMonth();
			restoreDisplayStatus();
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
		if(mEquipmentType == 1){
			unregisterStepReceiver();
			sendAllowToReceiver(mActivity,false);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterStepReceiver();
	}
	
	private void unregisterStepReceiver() {
		try {
			if (mMHStepReceiver != null) {
				mActivity.unregisterReceiver(mMHStepReceiver);
			}
		} catch (Exception e) {
		}
	}

	private void getOldStatusOfDayWeekMonth() {
		String pdwms = sp.getString("PreDayWeekMonth", null);
		if (pdwms != null) {
			Editor edit = sp.edit();
			String[] pdwmArray = pdwms.split("#");
			mPreDay = Integer.parseInt(pdwmArray[0]);
			mPreWeek = Integer.parseInt(pdwmArray[1]);
			mPreMonth = Integer.parseInt(pdwmArray[2]);
			edit.remove("PreDayWeekMonth");
			edit.commit();
		}
	}

	private void restoreDisplayStatus() {
		// mPreDay = 0;
		// mPreWeek = 0;
		// mPreMonth = 0;

		mImageViewLeft.setVisibility(View.VISIBLE);
		mImageViewRight.setVisibility(View.INVISIBLE);

		queryDayData();

		mFlagDayNum = BRIEF_TODAY;
		((RadioButton) findView(R.id.radio_today)).setChecked(true);

		if (mFlagStepNum == BRIEF_STRENGTH) {
			// ��ʾǿ������
			showStrength();
			if (mRadioGroupDayDetail.getCheckedRadioButtonId() != R.id.radio_strength)
				((RadioButton) findView(R.id.radio_strength)).setChecked(true);
		} else {
			// ��ʾ��������
			showStepNum();
			if (mRadioGroupDayDetail.getCheckedRadioButtonId() != R.id.radio_step)
				((RadioButton) findView(R.id.radio_step)).setChecked(true);
			mFlagStepNum = BRIEF_STEPNUM;
		}

		switch (mFlagDayNum) {
		case BRIEF_TODAY:
			if (mRadioGroupTime.getCheckedRadioButtonId() != R.id.radio_today)
				((RadioButton) findView(R.id.radio_today)).setChecked(true);
			break;
		case BRIEF_7DAY:
			if (mRadioGroupTime.getCheckedRadioButtonId() != R.id.radio_week)
				((RadioButton) findView(R.id.radio_week)).setChecked(true);
			break;
		case BRIEF_30DAY:
			if (mRadioGroupTime.getCheckedRadioButtonId() != R.id.radio_month)
				((RadioButton) findView(R.id.radio_month)).setChecked(true);
			break;
		default:
			if (mRadioGroupDayDetail.getCheckedRadioButtonId() != R.id.radio_step)
				((RadioButton) findView(R.id.radio_step)).setChecked(true);
			mFlagDayNum = BRIEF_TODAY;
			break;
		}
	}

	// private int mUpdateAnimationFlag;
	// ** private ScrollView mScrollView;

	private class UpdateDataThread {
		public synchronized void update(int flag) {
			// mUpdateAnimationFlag = flag;
			// if(flag == 0 && NetworkTool.getNetworkState(mActivity) == 0)
			// return;
			try {
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected void onPreExecute() {
						super.onPreExecute();
						mImageButtonUpdate.setVisibility(View.INVISIBLE);
						BaseToast("����ͬ����...");
						if (mPedoInfo == null) {
							String stepPlan = sp.getString(SharedPreferredKey.TARGET_STEP, "10000");
							addProgressView(Integer.valueOf(stepPlan), 0);
						}
					}

					@Override
					protected void onPostExecute(Void result) {
						super.onPostExecute(result);
						// mImageButtonUpdate.clearAnimation();
						// mImageButtonUpdate.setClickable(true);

						if (Constants.MESSAGE_UPDATE_SUCCESS == mFlagUpdateStatus) {
							// restoreDisplayStatus();
							// �������غͲ���
							if (null != mPedoInfo) {
								Editor sharedata = sp.edit();
								sharedata.putString("WEIGHT", mPedoInfo.data.weight);
								sharedata.putString("STEPLENGTH", mPedoInfo.data.step);
								sharedata.putString("pedo_update_time", Common.getDateAsYYYYMMDD(new Date().getTime()));
								sharedata.commit();
							}
						}
						mPreDay = 0;
						mPreWeek = 0;
						mPreMonth = 0;
						restoreDisplayStatus();
						// ˢ����Ϻ�
						mRefreshableView.finishRefresh();
						mImageButtonUpdate.setVisibility(View.VISIBLE);
					}

					@Override
					protected Void doInBackground(Void... params) {
						updateData();
						messagesManager(mFlagUpdateStatus);
						return null;
					}

				}.execute();
			} finally {

			}

		}
	}

	private void updateData() {
		int internet = NetworkTool.getNetworkState(mActivity);
		if (internet == 0) {
			mFlagUpdateStatus = Constants.MESSAGE_INTERNET_NONE;
			return;
		}

		// �ֻ��ź�����
		SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		String phonenum = info.getString(SharedPreferredKey.PHONENUM, null);
		String password = info.getString(SharedPreferredKey.PASSWORD, null);

		if (mFlagLogin == 0) {// �����˻���¼ �������
			MHealthProviderMetaData.GetMHealthProvider(mActivity).deletePedometerData();
			MHealthProviderMetaData.GetMHealthProvider(mActivity).deletePedoDetailData();
			// Group Info
			/*
			 * MHealthProviderMetaData.GetMHealthProvider(mActivity)
			 * .deleteGroupInPkInfo();
			 */
			MHealthProviderMetaData.GetMHealthProvider(mActivity).deleteGroupPkInfo();
			MHealthProviderMetaData.GetMHealthProvider(mActivity).deleteMemberPkInfo();
			MHealthProviderMetaData.GetMHealthProvider(mActivity).deleteOrgizePkInfo();
			// Race
			MHealthProviderMetaData.GetMHealthProvider(mActivity).deleteListActivity();
			MHealthProviderMetaData.GetMHealthProvider(mActivity).deleteActivityMediaInPkInfo();
		}

		DataPedometor pedoInfo = MHealthProviderMetaData.GetMHealthProvider(mActivity).getPedometerLatest();

		int result = 0;
		long toDate = new Date().getTime();
		long fromDate = 0;

		if (null == pedoInfo) {// ���ݿ���û������
			// if (phonenum.equals("13811029472"))
			// UPDATE_NUM = 60;
			fromDate = toDate - UPDATE_NUM * (1000L * 60 * 60 * 24);
		} else {
			// ��ȡ�������¼�¼
			fromDate = Common.getDateFromYYYYMMDDHHMMSSCreateTime(pedoInfo.createtime);
		}

		result = updatePeriodDayData(phonenum, password, fromDate, toDate);

		switch (result) {
		case 0:
			mFlagUpdateStatus = Constants.MESSAGE_UPDATE_SUCCESS;
			break;
		case 1:
			mFlagUpdateStatus = Constants.MESSAGE_INTERNET_ERROR;
			break;
		case -1:
			mFlagUpdateStatus = Constants.MESSAGE_SERVER_EXCEPTION;
			break;
		default:
			mFlagUpdateStatus = Constants.MESSAGE_SERVER_EXCEPTION;
		}

		mFlagLogin = 2;
	}

	/**
	 * ����ָ��ʱ�����������
	 * 
	 * @param phonenum
	 * @param password
	 * @param fromDate
	 *            ��ʼʱ�䣨ͨ��Ϊ���¼�¼���ϴ�ʱ�䣩
	 * @param toDate
	 *            ��ֹʱ�� ��ͨ��Ϊ�ֻ���ǰʱ�䣩
	 * @return
	 */
	private int updatePeriodDayData(String phonenum, String password, long fromDate, long toDate) {
		long currrentDate = fromDate;
		currrentDate = Common.getDateTimeFromTime(currrentDate);
		// ��ֹʱ�� Ҫ���� ��ʼʱ��
		while (toDate >= currrentDate) {
			String currentDateStr = Common.getDateAsYYYYMMDD(currrentDate);
			PedometorInfo reqData = new PedometorInfo();
			int result = DataSyn.getInstance().getPedoInfo(phonenum, password, currentDateStr, reqData);
			if (result == -1) {
				Log.e(TAG, "����ֵΪ-1�������ȡ���ڴ���");
				return -1;
			} else if (result == 1) {
				Log.e(TAG, "����ֵΪ1���������������ݴ������⣡");
				return 1;
			} else {
				List<DataPedometor> data_list = reqData.datavalue;
				int flag = 0;// �жϼ�������Ƿ���¹�
				for (int i = 0; i < data_list.size(); i++) {
					long date = Common.getDateFromYYYYMMDDHHMMSSCreateTime(data_list.get(i).createtime);
					if (date == 0) {
						Log.e(TAG, "����ֵΪ1���������������ݴ������⣡");
						return 1;
					}
				}
				MHealthProviderMetaData.GetMHealthProvider(mActivity).InsertPedometerData(data_list, fromDate, true);
				flag = 1;
				// ��ȡ��ϸ��
				if (flag == 1 && data_list.size() > 0) {
					String fromHour = "00", toHour = "23";
					PedoDetailInfo detailData = new PedoDetailInfo();
					result = DataSyn.getInstance().getPedoInfoDetail(phonenum, password, fromHour, toHour,
							currentDateStr, detailData);
					if (result == -1) {
						Log.e(TAG, "����ֵΪ-1�������ȡ���ڴ���");
						return -1;
					} else if (result == 1) {
						Log.e(TAG, "����ֵΪ1���������ݴ������⣡");
						return 1;
					} else {
						MHealthProviderMetaData.GetMHealthProvider(mActivity).insertPedoDetailData(detailData);
					}
				}
			}
			currrentDate = currrentDate + (1000L * 60 * 60 * 24); // ����һ��
		}
		return 0;
	}

	private void initViews() {
		mRelativeLayoutCenterprogress = findView(R.id.rel_center_progress);
		mRelativeLayoutCenterprogress.setVisibility(View.VISIBLE);
		mRelativeLayoutCenterprogressPeriod = findView(R.id.rel_center_progress_period);
		mRelativeLayoutCenterprogressPeriod.setVisibility(View.INVISIBLE);
		ImageView mImageViewShare = findView(R.id.imageview_share);
		mImageViewShare.setOnClickListener(this);
		// title
		mTextViewTitle = findView(R.id.textView_title);
		mTextViewTitle.setText(R.string.pedometer_title);

		// ** mScrollView = findView(R.id.scrollView1);;

		// ���°�ť
		mImageButtonUpdate = (ImageButton) findView(R.id.imageButton_title);
		mImageButtonUpdate.setBackgroundResource(R.drawable.bg_share_select);
		//
		mImageButtonUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public synchronized void onClick(View arg0) {
				SharedWeiXin();
			}
		});
		mImageButtonUpdate.setVisibility(View.VISIBLE);

		// ���� ����ǿ���л���ť
		mRadioGroupDayDetail = findView(R.id.radioGroup_daydetail);
		mRadioGroupDayDetail.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.radio_step:
					mFlagStepNum = BRIEF_STEPNUM;
					showStepNum();
					((LinearLayout) findView(R.id.button_minute)).setVisibility(View.GONE);
					((LinearLayout) findView(R.id.button_step)).setVisibility(View.VISIBLE);
					break;
				case R.id.radio_strength:
					mFlagStepNum = BRIEF_STRENGTH;
					showStrength();
					((LinearLayout) findView(R.id.button_minute)).setVisibility(View.VISIBLE);
					((LinearLayout) findView(R.id.button_step)).setVisibility(View.GONE);
					break;
				default:
					break;
				}
				// applyRotation(1, 0, 90);
			}

		});

		// ���� 7�� 30�� RadioGroup
		mRadioGroupTime = findView(R.id.radioGroup_time);
		mRadioGroupTime.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.radio_today:
					mFlagDayNum = BRIEF_TODAY;
					radioTodayChanged();
					break;
				case R.id.radio_week:
					mFlagDayNum = BRIEF_7DAY;
					radioWeekChanged();
					break;
				case R.id.radio_month:
					mFlagDayNum = BRIEF_30DAY;
					radioMonthChanged();
					break;
				default:
					break;
				}
				// applyRotation(1, 0, 90);
			}
		});
		((RadioButton) findView(R.id.radio_today)).setChecked(true);

		// day detail
		mLinearLayoumActivitytogram = findView(R.id.linearLayout_day_detail);
		mTopLayout = findView(R.id.rel_progress);
		mLinearLayoumActivitytogram.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					firstX = event.getX();
					Logger.d("testing3", "firstX ACTION_DOWN ==" + firstX);
					break;
				case MotionEvent.ACTION_UP:
					secondX = event.getX();
					Logger.d("testing3", "secondX ACTION_UP ==" + secondX);
					caluateLastpostion();
					break;
				case MotionEvent.ACTION_CANCEL:
					secondX = event.getX();
					Logger.d("testing3", "secondX ACTION_CANCEL ==" + secondX);
					caluateLastpostion();
					break;
				}
				return true;
			}

			private boolean caluateLastpostion() {
				if (mFlagDayNum == BRIEF_7DAY) {
					boolean eventOccur = false;
					if (firstX > secondX) {
						// left
						mImageViewLeft.setVisibility(View.VISIBLE);
						eventOccur = true;
						if (0 == mPreWeek) {
							BaseToast("�Ѿ�������");
							return true;
						}
						--mPreWeek;
						// ���µ��ܸ�����ʾ
						if (0 == mPreWeek) {
							mImageViewRight.setVisibility(View.INVISIBLE);
						}

					} else if (firstX < secondX) {
						// right
						mImageViewRight.setVisibility(View.VISIBLE);
						eventOccur = true;

						long startDate = MHealthProviderMetaData.GetMHealthProvider(mActivity).getStartDateOfPedoInfo();
						long dayOfSeconds = Common.getDateFromYYYYMMDD(mCurrentDay);
						dayOfSeconds = dayOfSeconds - (mPreWeek + 1) * 7 * Constants.DAY_MILLSECONDS;
						if (startDate > dayOfSeconds) {
							BaseToast("�Ѿ�������");
							return true;
						}
						++mPreWeek;
						// ������ܸ�����ʾ
						dayOfSeconds = dayOfSeconds - 7 * Constants.DAY_MILLSECONDS;
						if (startDate > dayOfSeconds) {
							mImageViewLeft.setVisibility(View.INVISIBLE);
						}
					}

					if (eventOccur) {
						queryPeriodData(mCurrentDay, 7, 1 + (7 * mPreWeek));

						if (mFlagStepNum == BRIEF_STRENGTH) {
							showStrengthPeriodData(7);
							((RadioButton) findView(R.id.radio_strength)).setChecked(true);
						} else {
							showStepNumPeriodData(7);
							((RadioButton) findView(R.id.radio_step)).setChecked(true);
						}
						return true;
					}
				} else if (mFlagDayNum == BRIEF_30DAY) {
					boolean eventOccur = false;
					if (firstX > secondX) {
						// left
						mImageViewLeft.setVisibility(View.VISIBLE);

						eventOccur = true;
						if (0 == mPreMonth) {
							BaseToast("�Ѿ�������");
							return true;
						}
						--mPreMonth;
						// ���µ��¸�����ʾ
						if (0 == mPreMonth) {
							mImageViewRight.setVisibility(View.INVISIBLE);
						}
					} else if (firstX < secondX) {
						// right
						mImageViewRight.setVisibility(View.VISIBLE);
						eventOccur = true;
						long startDate = MHealthProviderMetaData.GetMHealthProvider(mActivity).getStartDateOfPedoInfo();
						long dayOfSeconds = Common.getDateFromYYYYMMDD(mCurrentDay);
						dayOfSeconds = dayOfSeconds - (mPreMonth + 1) * 30 * Constants.DAY_MILLSECONDS;
						if (startDate > dayOfSeconds) {
							BaseToast("�Ѿ�������");
							return true;
						}
						++mPreMonth;
						// ������¸�����ʾ
						dayOfSeconds = dayOfSeconds - 30 * Constants.DAY_MILLSECONDS;
						if (startDate > dayOfSeconds) {
							mImageViewLeft.setVisibility(View.INVISIBLE);
						}
					}
					if (eventOccur) {
						queryPeriodData(mCurrentDay, 30, 1 + (30 * mPreMonth));

						if (mFlagStepNum == BRIEF_STRENGTH) {
							showStrengthPeriodData(30);
							((RadioButton) findView(R.id.radio_strength)).setChecked(true);
						} else {
							showStepNumPeriodData(30);
							((RadioButton) findView(R.id.radio_step)).setChecked(true);
						}
						return true;
					}
				} else if (mFlagDayNum == BRIEF_TODAY) {
					boolean eventOccur = false;
					if (firstX > secondX) {
						// left
						mImageViewLeft.setVisibility(View.VISIBLE);
						eventOccur = true;
						if (0 == mPreDay) {
							BaseToast("�Ѿ�������");
							return true;
						}
						--mPreDay;
						// ���µ��ܸ�����ʾ
						if (0 == mPreDay) {
							mImageViewRight.setVisibility(View.INVISIBLE);
						}
					} else if (firstX < secondX) {
						// right
						mImageViewRight.setVisibility(View.VISIBLE);
						eventOccur = true;

						long startDate = MHealthProviderMetaData.GetMHealthProvider(mActivity).getStartDateOfPedoInfo();
						long dayOfSeconds = Common.getDateFromYYYYMMDD(mCurrentDay);
						dayOfSeconds = dayOfSeconds - (mPreDay + 1) * Constants.DAY_MILLSECONDS;
						if (startDate > dayOfSeconds) {
							BaseToast("�Ѿ�������");
							return true;
						}
						++mPreDay;
						// ������ܸ�����ʾ
						dayOfSeconds = dayOfSeconds - Constants.DAY_MILLSECONDS;
						if (startDate > dayOfSeconds) {
							mImageViewLeft.setVisibility(View.INVISIBLE);
						}
					}

					if (eventOccur) {
						queryDayData();

						if (mFlagStepNum == BRIEF_STRENGTH) {
							showStrengthDayData();
							((RadioButton) findView(R.id.radio_strength)).setChecked(true);
						} else {
							showStepNumDayData();
							((RadioButton) findView(R.id.radio_step)).setChecked(true);
						}
					}
				}
				return true;
			}
		});

		mTextViewCal = findView(R.id.textView_cal);
		mTextViewStepnum = findView(R.id.textView_stepnum);
		mTextViewDistance = findView(R.id.textView_distance);
		mTextViewDuration = findView(R.id.TextViewDuration);
		mTextViewPeriodSumStep = findView(R.id.stepnumofperiod);
		mEquipmentSwitcher = findView(R.id.apb_equipment_switcher);
		mServiceActivite = findView(R.id.apb_service_action_button);
		mServiceActivite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mEquipmentType = (mEquipmentType + 3) % 2;
				Logger.d("Equipment testing", "mEquipmentType == " + mEquipmentType);
				
				switch (mEquipmentType) {
				case 0:
					unregisterStepReceiver();
					mServiceActivite.setText("�����ֻ��Ʋ�");
					mStepController.stopStepServiceWithoutBindService();
					mRefreshableView.startRefreshDirectly();
					break;
				case 1:
					mServiceActivite.setText("�ر��ֻ��Ʋ�");
					registerStepReceiver();
					mStepController.startStepService();
					getMobilePedDataFromDatabase();
					break;
				}
				
			}
		});

		mProgressbarStep = findView(R.id.Progress_center_rote1);
		mTextViewUpdateDate = findView(R.id.textView_updatedate);

		mImageViewLeft = findView(R.id.imageview_leftday);
		mImageViewLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mFlagDayNum == BRIEF_7DAY) {
					mImageViewRight.setVisibility(View.VISIBLE);
					long startDate = MHealthProviderMetaData.GetMHealthProvider(mActivity).getStartDateOfPedoInfo();
					++mPreWeek;
					long dayOfSeconds = Common.getDateFromYYYYMMDD(mCurrentDay);
					dayOfSeconds = dayOfSeconds - (mPreWeek + 1) * 7 * Constants.DAY_MILLSECONDS;
					// ������ܸ�����ʾ
					if (startDate > dayOfSeconds) {
						mImageViewLeft.setVisibility(View.INVISIBLE);
					}

					queryPeriodData(mCurrentDay, 7, 1 + (7 * mPreWeek));

					if (mFlagStepNum == BRIEF_STRENGTH) {
						showStrengthPeriodData(7);
						((RadioButton) findView(R.id.radio_strength)).setChecked(true);
					} else {
						showStepNumPeriodData(7);
						((RadioButton) findView(R.id.radio_step)).setChecked(true);
					}

				} else if (mFlagDayNum == BRIEF_30DAY) {

					// right
					mImageViewRight.setVisibility(View.VISIBLE);
					long startDate = MHealthProviderMetaData.GetMHealthProvider(mActivity).getStartDateOfPedoInfo();
					++mPreMonth;
					long dayOfSeconds = Common.getDateFromYYYYMMDD(mCurrentDay);
					dayOfSeconds = dayOfSeconds - (mPreMonth + 1) * 30 * Constants.DAY_MILLSECONDS;
					// ������¸�����ʾ
					dayOfSeconds = dayOfSeconds - 30 * Constants.DAY_MILLSECONDS;
					if (startDate > dayOfSeconds) {
						mImageViewLeft.setVisibility(View.INVISIBLE);
					}

					queryPeriodData(mCurrentDay, 30, 1 + (30 * mPreMonth));

					if (mFlagStepNum == BRIEF_STRENGTH) {
						showStrengthPeriodData(30);
						((RadioButton) findView(R.id.radio_strength)).setChecked(true);
					} else {
						showStepNumPeriodData(30);
						((RadioButton) findView(R.id.radio_step)).setChecked(true);
					}
				} else if (mFlagDayNum == BRIEF_TODAY) {
					// right
					mImageViewRight.setVisibility(View.VISIBLE);
					long startDate = MHealthProviderMetaData.GetMHealthProvider(mActivity).getStartDateOfPedoInfo();
					++mPreDay;
					long dayOfSeconds = Common.getDateFromYYYYMMDD(mCurrentDay);
					dayOfSeconds = dayOfSeconds - (mPreDay + 1) * Constants.DAY_MILLSECONDS;

					if (startDate > dayOfSeconds) {
						mImageViewLeft.setVisibility(View.INVISIBLE);
					}

					queryDayData();

					if (mFlagStepNum == BRIEF_STRENGTH) {
						showStrengthDayData();
						((RadioButton) findView(R.id.radio_strength)).setChecked(true);
					} else {
						showStepNumDayData();
						((RadioButton) findView(R.id.radio_step)).setChecked(true);
					}
				}
			}
		});
		mImageViewRight = findView(R.id.imageview_rightday);
		mImageViewRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (mFlagDayNum == BRIEF_7DAY) {
					// left
					mImageViewLeft.setVisibility(View.VISIBLE);
					--mPreWeek;
					// ���µ��ܸ�����ʾ
					if (0 == mPreWeek) {
						mImageViewRight.setVisibility(View.INVISIBLE);
					}

					queryPeriodData(mCurrentDay, 7, 1 + (7 * mPreWeek));

					if (mFlagStepNum == BRIEF_STRENGTH) {
						showStrengthPeriodData(7);
						((RadioButton) findView(R.id.radio_strength)).setChecked(true);
					} else {
						showStepNumPeriodData(7);
						((RadioButton) findView(R.id.radio_step)).setChecked(true);
					}

				} else if (mFlagDayNum == BRIEF_30DAY) {

					// left
					mImageViewLeft.setVisibility(View.VISIBLE);
					--mPreMonth;
					// ���µ��¸�����ʾ
					if (0 == mPreMonth) {
						mImageViewRight.setVisibility(View.INVISIBLE);
					}

					queryPeriodData(mCurrentDay, 30, 1 + (30 * mPreMonth));

					if (mFlagStepNum == BRIEF_STRENGTH) {
						showStrengthPeriodData(30);
						((RadioButton) findView(R.id.radio_strength)).setChecked(true);
					} else {
						showStepNumPeriodData(30);
						((RadioButton) findView(R.id.radio_step)).setChecked(true);
					}
				} else if (mFlagDayNum == BRIEF_TODAY) {
					// left
					mImageViewLeft.setVisibility(View.VISIBLE);
					--mPreDay;
					// ���µ��¸�����ʾ
					if (0 == mPreDay) {
						mImageViewRight.setVisibility(View.INVISIBLE);
					}

					queryDayData();

					if (mFlagStepNum == BRIEF_STRENGTH) {
						showStrengthDayData();
						((RadioButton) findView(R.id.radio_strength)).setChecked(true);
					} else {
						showStepNumDayData();
						((RadioButton) findView(R.id.radio_step)).setChecked(true);
					}
				}
			}
		});
		mImageViewLeft.setVisibility(View.VISIBLE);
		mImageViewRight.setVisibility(View.INVISIBLE);
	}

	// @Override
	// public boolean dispatchTouchEvent(MotionEvent ev) {
	// mGestureDetector.onTouchEvent(ev);
	// return super.dispatchTouchEvent(ev);
	// }

	private void radioTodayChanged() {
		// mPreDay = 0;
		queryDayData();
		if (mFlagStepNum == BRIEF_STRENGTH) {
			showStrengthDayData();
			((RadioButton) findView(R.id.radio_strength)).setChecked(true);
		} else {
			showStepNumDayData();
			((RadioButton) findView(R.id.radio_step)).setChecked(true);
		}
		mRelativeLayoutCenterprogress.setVisibility(View.VISIBLE);
		mRelativeLayoutCenterprogressPeriod.setVisibility(View.INVISIBLE);
		mProgressbarStep.setVisibility(View.VISIBLE);
		mTextViewUpdateDate.setVisibility(View.VISIBLE); // ����ʱ��

		mImageViewLeft.setVisibility(View.VISIBLE);
		mImageViewRight.setVisibility(View.INVISIBLE);
	}

	private void radioWeekChanged() {
		// mPreWeek = 0;// ��ʼ��
		queryPeriodData(mCurrentDay, 7, 1 + (7 * mPreWeek));
		if (mFlagStepNum == BRIEF_STRENGTH) {
			showStrengthPeriodData(7);
			((RadioButton) findView(R.id.radio_strength)).setChecked(true);
		} else {
			showStepNumPeriodData(7);
			((RadioButton) findView(R.id.radio_step)).setChecked(true);
		}
		mRelativeLayoutCenterprogress.setVisibility(View.INVISIBLE);
		mRelativeLayoutCenterprogressPeriod.setVisibility(View.VISIBLE);
		mProgressbarStep.setVisibility(View.INVISIBLE);
		mTextViewUpdateDate.setVisibility(View.INVISIBLE);

		mImageViewLeft.setVisibility(View.VISIBLE);
		mImageViewRight.setVisibility(View.INVISIBLE);
	}

	private void radioMonthChanged() {
		// mPreMonth = 0;// ��ʼ��
		queryPeriodData(mCurrentDay, 30, 1 + (30 * mPreMonth));
		if (mFlagStepNum == BRIEF_STRENGTH) {
			showStrengthPeriodData(30);
			((RadioButton) findView(R.id.radio_strength)).setChecked(true);
		} else {
			showStepNumPeriodData(30);
			((RadioButton) findView(R.id.radio_step)).setChecked(true);
		}
		mRelativeLayoutCenterprogress.setVisibility(View.INVISIBLE);
		mRelativeLayoutCenterprogressPeriod.setVisibility(View.VISIBLE);
		mProgressbarStep.setVisibility(View.INVISIBLE);
		mTextViewUpdateDate.setVisibility(View.INVISIBLE);

		mImageViewLeft.setVisibility(View.VISIBLE);
		mImageViewRight.setVisibility(View.INVISIBLE);
	}

	/**
	 * ��ѯ���һ�����ϸ����
	 */
	private void queryDayData() {
		// �жϲ�ѯ
		mPedoInfo = MHealthProviderMetaData.GetMHealthProvider(mActivity).getPedometerLatest();
		if (mPedoInfo == null) {
			Logger.i(TAG, "mPedoInfo is null");
			return;
		}
		String createTime = mPedoInfo.createtime;
		mCurrentDay = createTime.substring(0, 10).replace("-", "");

		long createDateLong = Common.getDateFromYYYYMMDD(mCurrentDay);
		long searchDate = createDateLong - mPreDay * Constants.DAY_MILLSECONDS;

		String searchDateStr = Common.getDateAsYYYYMMDD(searchDate);

		if (mPreDay != 0) {
			mPedoInfoList = MHealthProviderMetaData.GetMHealthProvider(mActivity).getPeriodPedoInfoFromLatest(1, mPreDay);
			if (mPedoInfoList != null && mPedoInfoList.size() > 0)
				mPedoInfo = mPedoInfoList.get(0);
			else
				mPedoInfo = null;
		}

		mPedoDetailInfo = MHealthProviderMetaData.GetMHealthProvider(mActivity).getPedoDetailByDay(searchDateStr);
	}

	/**
	 * ��ѯ���7���30�����ϸ����
	 * 
	 * @param searchDate
	 *            yyyyMMdd
	 * @param dayNum
	 */
	private void queryPeriodData(String searchDate, int dayNum, int nPreDayNum) {
		mPedoInfoList = MHealthProviderMetaData.GetMHealthProvider(mActivity).getPeriodPedoInfoFromLatest(dayNum,
				nPreDayNum);
	}

	/**
	 * ��ʾ��������
	 */
	private void showStepNum() {
		switch (mFlagDayNum) {
		case BRIEF_TODAY:
			showStepNumDayData();
			break;
		case BRIEF_7DAY:
			showStepNumPeriodData(7);
			break;
		case BRIEF_30DAY:
			showStepNumPeriodData(30);
			break;
		}
	}

	/**
	 * ��ʾǿ������
	 */
	private void showStrength() {
		switch (mFlagDayNum) {
		case BRIEF_TODAY:
			showStrengthDayData();
			break;
		case BRIEF_7DAY:
			showStrengthPeriodData(7);
			break;
		case BRIEF_30DAY:
			showStrengthPeriodData(30);
			break;
		}
	}

	@SuppressWarnings("deprecation")
	private void setDayValue() {
		if (mPedoInfo == null || mPedoInfo.data == null) {
			Logger.w(TAG, "setDayValue: mPedoInfo is null!");

			// ��ʼ��
			mTextViewCal.setText("");
			mTextViewStepnum.setText("");
			mTextViewDistance.setText("");
			mTextViewDuration.setText("");
			mTextViewPeriodSumStep.setText("");

			// SharedPreferences info =
			// getSharedPreferences(SharedPreferredKey.SharedPrefenceName, 0);
			// String stepPlan = info.getString("TARGET", "10000");
			// addProgressView(Integer.valueOf(stepPlan), 0);
			// mProgressbarStep.setMaxScore();
			// mProgressbarStep.setScore(0);
			// mProgressbarStep.invalidate();

			if (mCurrentDay == null || mCurrentDay.equals("")) {
				mTextViewUpdateDate.setText("Ŀǰû���˶�����");
				return;
			}
			// �����ϴ�ʱ��
			long dayOfSeconds = Common.getDateFromYYYYMMDD(mCurrentDay);
			dayOfSeconds = dayOfSeconds - mPreDay * Constants.DAY_MILLSECONDS;

			String strUpdateDate = Common.getDateAsM_d(dayOfSeconds);
			String dayOfWeekStr = Common.GetWeekStr(new Date(dayOfSeconds).getDay());
			strUpdateDate = strUpdateDate + "(" + dayOfWeekStr + ")";
			// String strTime = Common.getDateAsH_m(dayOfSeconds);
			mTextViewUpdateDate.setText(strUpdateDate + "û���˶�����");

			return;
		}
		// mPedoInfo.data.cal
		mTextViewCal.setText(Common.calstrToInt(mPedoInfo.data.cal) + "");
		mTextViewStepnum.setText(mPedoInfo.data.yxbssum + "");
		mTextViewDistance.setText(Common.m2km(mPedoInfo.data.distance));
		int sumDuration = Integer.parseInt(mPedoInfo.data.strength2) + Integer.parseInt(mPedoInfo.data.strength3)
				+ Integer.parseInt(mPedoInfo.data.strength4);
		String[] sumDurationStr = Common.FormatTimeHHmmss(sumDuration).split(":");

		mTextViewDuration.setText(sumDurationStr[0].trim().replace(", ", "") + ":" + sumDurationStr[1]);
		// + "h" + sumDurationStr[1]+"'");

		SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		String stepPlan = info.getString(SharedPreferredKey.TARGET_STEP, "10000");

		addProgressView(Integer.valueOf(stepPlan), Integer.valueOf(mPedoInfo.data.stepNum));
		// mProgressbarStep.setMax(Integer.valueOf(stepPlan));
		// mProgressbarStep.setProgress(Integer.valueOf(mPedoInfo.data.stepNum));
		// mProgressbarStep.invalidate();

		// �����ϴ�ʱ��
		long updateTime = Common.getDateFromYYYYMMDDHHMMSSCreateTime(mPedoInfo.createtime);

		mStrUpdateDate = Common.getDateAsM_d(updateTime);
		String dayOfWeekStr = Common.GetWeekStr(new Date(updateTime).getDay());
		mStrUpdateDate = mStrUpdateDate + "(" + dayOfWeekStr + ")";
		String strTime = Common.getDateAsH_m(updateTime);

		if (mPreDay != 0) {
			mTextViewUpdateDate.setText(mStrUpdateDate);
		} else {
			mTextViewUpdateDate.setText("�ϴ��� " + mStrUpdateDate + strTime);
		}
	}

	private void showStepNumDayData() {
		setDayValue();

		if (mPedoDetailInfo == null || mPedoInfo == null) {
			Logger.i(TAG, "mPedoDetailInfo no data!");
			mLinearLayoumActivitytogram.removeAllViews();
			return;
		}
		// �������
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		XYSeries series = new XYSeries("��ɢ����");
		XYSeries seriesYX = new XYSeries("��Ч����");

		int[] hour24 = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int[] mStepNumSumMaxHour = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		mStepNumSumMax = 0;

		for (int i = 0; i < mPedoDetailInfo.datavalue.size(); i++) {

			DataDetailPedo data = mPedoDetailInfo.datavalue.get(i);

			int stepNumSum = data.getStepNumSum();
			int stepNumYXSum = data.getSNYXP5Sum();

			int startTime = Integer.parseInt(data.start_time);

			// ��ֵ���ھ�ֵʱ������ʾ
			if (mStepNumSumMaxHour[startTime] < stepNumSum) {
				mStepNumSumMaxHour[startTime] = stepNumSum;
				series.add(startTime + 0.5, stepNumSum);
				seriesYX.add(startTime + 0.5, stepNumYXSum);
			}

			if (mStepNumSumMax < stepNumSum) {
				mStepNumSumMax = stepNumSum;
			}

			Logger.i(TAG, "Delete GroupInPkInfo");

			hour24[startTime] = 1;
		}

		for (int i = 0; i < hour24.length; i++) {
			if (hour24[i] == 0) {
				series.add(i + 0.5, 0);
				seriesYX.add(i + 0.5, 0);
			}
		}

		dataset.addSeries(series);
		dataset.addSeries(seriesYX);

		// mHistogramChartview = ChartFactory.getBarChartView(mActivity,
		// dataset,
		// getStepNumBarRendererDay(), BarChart.Type.STACKED);
		mHistogramChartview = ChartFactory.getBarChartView(mActivity, dataset, getStepNumBarRendererDay(),
				BarChart.Type.STACKED);
		mLinearLayoumActivitytogram.removeAllViews();
		mLinearLayoumActivitytogram.addView(mHistogramChartview);
	}

	/**
	 * ��ȡ�����˶�ǿ������
	 */
	private void showStrengthDayData() {
		setDayValue();

		if (mPedoDetailInfo == null || mPedoInfo == null) {
			Logger.i(TAG, "mPedoDetailInfo no data!");
			mLinearLayoumActivitytogram.removeAllViews();
			return;
		}
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		XYSeries seriesStrength2 = new XYSeries("");
		XYSeries seriesStrength3 = new XYSeries("");
		XYSeries seriesStrength4 = new XYSeries("");
		// ��������
		int hour24[] = new int[24];
		for (int i = 0; i < 24; i++) {
			hour24[i] = -1;
		}

		int[] mStrengthSumMaxHour = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		for (int i = 0; i < mPedoDetailInfo.datavalue.size(); i++) {
			DataDetailPedo data = mPedoDetailInfo.datavalue.get(i);
			int startTime = Integer.parseInt(data.start_time);

			// �����ظ����� ʹ�ø�Сʱ���������
			int nStengthSum = data.getStrengthSum(2) + data.getStrengthSum(3) + data.getStrengthSum(4);
			if (mStrengthSumMaxHour[startTime] < nStengthSum) {
				mStrengthSumMaxHour[startTime] = nStengthSum;
				hour24[startTime] = i;
			}

		}

		// for (int i = 0; i < mPedoDetailInfo.datavalue.size(); i++) {
		// DataDetailPedo data = mPedoDetailInfo.datavalue.get(i);
		// int startTime = Integer.parseInt(data.start_time);
		// hour24[startTime] = i;
		// }
		mStrengthSumMax = 0;

		for (int i = 0; i < 24; i++) {
			double strength2 = 0;
			double strength3 = 0;
			double strength4 = 0;

			if (hour24[i] != -1) {
				DataDetailPedo data = mPedoDetailInfo.datavalue.get(hour24[i]);
				strength2 = data.getStrengthSum(2) / 60.0f;
				strength3 = data.getStrengthSum(3) / 60.0f;
				strength4 = data.getStrengthSum(4) / 60.0f;
			}

			seriesStrength4.add(i + 0.5, strength4);
			seriesStrength3.add(i + 0.5, strength3 + strength4);
			seriesStrength2.add(i + 0.5, strength4 + strength3 + strength2);

			// �������ֵ
			double strengthSum = 0;
			strengthSum += strength2;
			strengthSum += strength3;
			strengthSum += strength4;

			if (mStrengthSumMax < strengthSum)
				mStrengthSumMax = strengthSum;
			// stepNumDaySum += stepNumSum;
		}
		dataset.addSeries(seriesStrength2);
		dataset.addSeries(seriesStrength3);
		dataset.addSeries(seriesStrength4);

		mHistogramChartview = ChartFactory.getBarChartView(mActivity, dataset, getStrengthBarRendererDay(),
				BarChart.Type.STACKED);
		mLinearLayoumActivitytogram.removeAllViews();
		mLinearLayoumActivitytogram.addView(mHistogramChartview);

	}

	private void setPeriodValue(int sumCal, int sumStepNum, int sumDistance, int sumDurationPeriod, int sumYXStepNum) {
		mTextViewCal.setText(sumCal + "");
		mTextViewStepnum.setText(sumYXStepNum + "");

		mTextViewDistance.setText(Common.m2km(sumDistance));

		String[] sumDurationPeriodStr = Common.FormatTimeHHmmss(sumDurationPeriod).split(":");
		mTextViewDuration.setText(sumDurationPeriodStr[0].trim().replace(", ", "") + ":" + sumDurationPeriodStr[1]);
		mTextViewPeriodSumStep.setText(sumStepNum + "");

	}

	/**
	 * ��ȡ7���30��ǿ������
	 * 
	 * @param periodNum
	 */
	private void showStrengthPeriodData(int periodNum) {
		if (mPedoInfoList == null) {
			Logger.i(TAG, "mPedoInfoList is null");
			return;
		}

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		XYSeries seriesStrength2 = new XYSeries("���");
		XYSeries seriesStrength3 = new XYSeries("һ��");
		XYSeries seriesStrength4 = new XYSeries("����");

		int[] periodNumFlag = new int[periodNum + 1];
		for (int i = 1; i < periodNum + 1; i++) {
			periodNumFlag[i] = 0;
		}

		mStrengthSumMax = 0;

		int sumStepNum = 0;
		int sumYXStepNum = 0;
		int sumDistance = 0;
		float sumCal = 0;
		int sumDurationPeriod = 0;

		for (int i = 0; i < mPedoInfoList.size(); i++) {
			DataPedometor pedo = mPedoInfoList.get(i);
			// sum
			int tmpStemNum = Integer.parseInt(pedo.data.stepNum);
			int tmpDistance = Integer.parseInt(pedo.data.distance);
			float tmpCal = Float.parseFloat(pedo.data.cal);
			int tmpYXStepNum = 0;
			if (pedo.data.yxbssum == null || pedo.data.yxbssum.equals(""))
				tmpYXStepNum = 0;
			else
				tmpYXStepNum = Integer.parseInt(pedo.data.yxbssum);

			sumStepNum += tmpStemNum;
			sumYXStepNum += tmpYXStepNum;
			sumDistance += tmpDistance;
			sumCal += (tmpCal);

			sumDurationPeriod += Integer.parseInt(pedo.data.strength2);
			sumDurationPeriod += Integer.parseInt(pedo.data.strength3);
			sumDurationPeriod += Integer.parseInt(pedo.data.strength4);

			// chart display
			double strength2 = Integer.parseInt(pedo.data.strength2) / 60.0f;
			double strength3 = Integer.parseInt(pedo.data.strength3) / 60.0f;
			double strength4 = Integer.parseInt(pedo.data.strength4) / 60.0f;

			seriesStrength4.add(periodNum - i, strength4);
			seriesStrength3.add(periodNum - i, strength3 + strength4);
			seriesStrength2.add(periodNum - i, strength4 + strength3 + strength2);

			// �������ֵ
			double strengthSum = 0;
			strengthSum += strength2;
			strengthSum += strength3;
			strengthSum += strength4;

			if (mStrengthSumMax < strengthSum)
				mStrengthSumMax = strengthSum;

			periodNumFlag[periodNum - i] = 1;
		}

		// ��������
		for (int i = 1; i < periodNum + 1; i++) {
			if (periodNumFlag[i] == 0) {
				seriesStrength4.add(i, 0);
				seriesStrength3.add(i, 0);
				seriesStrength2.add(i, 0);
			}
		}

		dataset.addSeries(seriesStrength2);
		dataset.addSeries(seriesStrength3);
		dataset.addSeries(seriesStrength4);

		switch (mFlagDayNum) {
		case BRIEF_7DAY:
			mHistogramChartview = ChartFactory.getBarChartView(mActivity, dataset, getStrengthBarRendererWeek(),
					BarChart.Type.STACKED);
			break;
		case BRIEF_30DAY:
			mHistogramChartview = ChartFactory.getBarChartView(mActivity, dataset, getStrengthBarRendererMonth(),
					BarChart.Type.STACKED);
			break;
		}

		mLinearLayoumActivitytogram.removeAllViews();
		mLinearLayoumActivitytogram.addView(mHistogramChartview);

		setPeriodValue((int) sumCal, sumStepNum, sumDistance, sumDurationPeriod, sumYXStepNum);
	}

	/**
	 * ��ȡ7���30�첽������
	 * 
	 * @param periodNum
	 */
	private void showStepNumPeriodData(int periodNum) {
		if (mPedoInfoList == null || mPedoInfoList.size() == 0) {
			Logger.i(TAG, "mPedoInfoList is null");
			return;
		}

		// �������
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		XYSeries series = new XYSeries("��ɢ����");
		XYSeries seriesYX = new XYSeries("��Ч����");

		mMostStepNum = 0;
		mMostCal = 0;
		int sumStepNum = 0;
		int sumYXStepNum = 0;
		int sumDistance = 0;
		float sumCal = 0;
		int sumDurationPeriod = 0;
		// initial
		// mMostStepNumTime = mPedoInfoList.get(0).createtime;
		// mMostCalTime = mPedoInfoList.get(0).createtime;

		int[] periodNumFlag = new int[periodNum + 1];
		for (int i = 0; i < periodNum + 1; i++) {
			periodNumFlag[i] = 0;
		}

		for (int i = 0; i < mPedoInfoList.size(); i++) {
			DataPedometor pedo = mPedoInfoList.get(i);
			int tmpStemNum = Integer.parseInt(pedo.data.stepNum);
			int tmpDistance = Integer.parseInt(pedo.data.distance);
			int tmpYXStepNum = 0;
			if (pedo.data.yxbssum == null || pedo.data.yxbssum.equals(""))
				tmpYXStepNum = 0;
			else
				tmpYXStepNum = Integer.parseInt(pedo.data.yxbssum);

			float tmpCal = Float.parseFloat(pedo.data.cal);
			series.add(periodNum - i, (double) tmpStemNum);
			seriesYX.add(periodNum - i, (double) tmpYXStepNum);

			periodNumFlag[periodNum - i] = 1;
			// sum
			sumStepNum += tmpStemNum;
			sumYXStepNum += tmpYXStepNum;
			sumDistance += tmpDistance;
			sumCal += (tmpCal);

			sumDurationPeriod += Integer.parseInt(pedo.data.strength2);
			sumDurationPeriod += Integer.parseInt(pedo.data.strength3);
			sumDurationPeriod += Integer.parseInt(pedo.data.strength4);

			if (mMostStepNum < tmpStemNum) {
				mMostStepNum = tmpStemNum;
			}

			if (mMostCal < (tmpCal)) {
				mMostCal = (tmpCal);
			}
		}

		for (int i = 0; i < periodNum + 1; i++) {
			if (periodNumFlag[i] == 0)
				series.add(i, 0);
		}

		dataset.addSeries(series);
		dataset.addSeries(seriesYX);

		if (periodNum == 7)
			mHistogramChartview = ChartFactory.getBarChartView(mActivity, dataset, getStepNumBarRendererWeek(),
					BarChart.Type.STACKED);
		else
			mHistogramChartview = ChartFactory.getBarChartView(mActivity, dataset, getStepNumBarRendererMonth(),
					BarChart.Type.STACKED);

		mLinearLayoumActivitytogram.removeAllViews();
		mLinearLayoumActivitytogram.addView(mHistogramChartview);

		setPeriodValue((int) sumCal, sumStepNum, sumDistance, sumDurationPeriod, sumYXStepNum);
	}

	/***
	 * ǰ��ʮ�ȷ�ת
	 * 
	 * @param position
	 * @param start
	 * @param end
	 */
	@SuppressWarnings("unused")
	private void applyRotation(int position, float start, float end) {
		// �������ĵ�
		final float centerX = mLinearLayoumActivitytogram.getWidth() / 2.0f;
		final float centerY = mLinearLayoumActivitytogram.getHeight() / 2.0f;
		final Rotate3dAnimation rotation = new Rotate3dAnimation(start, end, centerX, centerY, 310.0f, true);
		rotation.setDuration(300);
		rotation.setFillAfter(true);
		// ���ٶ���������
		rotation.setInterpolator(new AccelerateInterpolator());
		// �ڶ���Ч������ʱ��һ���������磺������������ʲô�¼������������ظ�ִ�еĴ���
		rotation.setAnimationListener(new DisplayNextView(position));
		mLinearLayoumActivitytogram.startAnimation(rotation);
	}

	/**
	 * 
	 * ����������ִ�ж���
	 */
	private final class DisplayNextView implements Animation.AnimationListener {
		private final int mPosition;

		private DisplayNextView(int position) {
			mPosition = position;
		}

		// ������ʼ
		public void onAnimationStart(Animation animation) {

		}

		// ��������
		public void onAnimationEnd(Animation animation) {
			// Runnable����ӵ���Ϣ���У�ִ���û������߳�
			mLinearLayoumActivitytogram.post(new SwapViews(mPosition));
		}

		public void onAnimationRepeat(Animation animation) {

		}
	}

	/**
	 * <p>
	 * ����������������270�ȷ�ת������360��
	 * </p>
	 * <p>
	 * Create User: lixs
	 * </p>
	 */
	private final class SwapViews implements Runnable {
		private final int mPosition;

		public SwapViews(int position) {
			mPosition = position;
		}

		public void run() {
			final float centerX = mLinearLayoumActivitytogram.getWidth() / 2.0f;
			final float centerY = mLinearLayoumActivitytogram.getHeight() / 2.0f;
			Rotate3dAnimation rotation;
			if (mPosition > -1) {
				rotation = new Rotate3dAnimation(270, 360, centerX, centerY, 310.0f, false);
			} else {
				rotation = new Rotate3dAnimation(-270, -360, centerX, centerY, 310.0f, false);
			}
			rotation.setDuration(300);
			rotation.setFillAfter(true);
			// ���ٶ���������
			rotation.setInterpolator(new DecelerateInterpolator());
			// ��ʼ����
			mLinearLayoumActivitytogram.startAnimation(rotation);
		}
	}

	public XYMultipleSeriesRenderer getStrengthBarRendererDay() {
		XYMultipleSeriesRenderer renderer = getBarRenderer();
		renderer.setInScroll(true);
		renderer.setYAxisMin(0);
		renderer.setShowLegend(false);
		// **
		renderer.setYAxisMax(mStrengthSumMax * 1.1);
		renderer.setXAxisMin(-1);
		renderer.setXAxisMax(24.5);

		renderer.setChartTitle("");
		renderer.setXTitle("");

		float density = Common.getDensity(mActivity);
		if (density <= 1.5f) {
			renderer.setMargins(new int[] { Common.dip2px(mActivity, 5), Common.dip2px(mActivity, 5),
					Common.dip2px(mActivity, 13), Common.dip2px(mActivity, 5) });
		} else if (density >= 2.0f) {
			renderer.setMargins(new int[] { Common.dip2px(mActivity, 15), Common.dip2px(mActivity, 5),
					Common.dip2px(mActivity, 18), Common.dip2px(mActivity, 5) });
		}

		renderer.setBarSpacing(1); // bar֮��ľ���

		renderer.setXLabels(0);// 0Ϊ����X�̶� �������ڱ�ʾx��ʾ��Ŀ
		// ��24��ʾΪ6��
		renderer.addXTextLabel(1, "1");
		for (int i = 2; i <= 24; i++) {
			if (i % 4 == 0)
				renderer.addXTextLabel(i, i + "");
			else
				renderer.addXTextLabel(i, "");
		}

		renderer.setYLabelsColor(0, Color.BLACK);

		// renderer.setYLabelsAlign(Align.RIGHT); // y��������
		// renderer.setYTitle("�˶�ǿ��ʱ�䣨�룩");
		// renderer.setYTitle("          ����");
		// renderer.set

		// renderer.setYLabelsAlign(Align.LEFT); // y��������
		// renderer.setShowAxes(false);
		// renderer.setMargins(new int[] { 10, 25, 10, 10 });
		renderer.setAxisTitleTextSize(20);

		renderer.setXLabelsColor(Color.BLACK);

		SimpleSeriesRenderer r0 = new SimpleSeriesRenderer();
		r0.setColor(mActivity.getResources().getColor(R.color.color_qingwei));// 148,
																				// 195,
																				// 205
		SimpleSeriesRenderer r1 = new SimpleSeriesRenderer();
		r1.setColor(mActivity.getResources().getColor(R.color.color_putong));
		SimpleSeriesRenderer r2 = new SimpleSeriesRenderer();
		r2.setColor(mActivity.getResources().getColor(R.color.color_julie));

		// r0.setGradientStart(0, Color.rgb(208, 78, 0));
		// r0.setGradientStop(mStepNumSumMax, Color.rgb(254, 142, 0));

		renderer.addSeriesRenderer(r0);
		renderer.addSeriesRenderer(r1);
		renderer.addSeriesRenderer(r2);

		// renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
		// renderer.getSeriesRendererAt(1).setDisplayChartValues(true);
		// renderer.getSeriesRendererAt(2).setDisplayChartValues(true);

		return renderer;
	}

	public XYMultipleSeriesRenderer getStepNumBarRendererDay() {
		XYMultipleSeriesRenderer renderer = getBarRenderer();
		renderer.setInScroll(true);
		renderer.setShowLegend(false);
		renderer.setYAxisMin(0);
		renderer.setYAxisMax(mStepNumSumMax * 1.1f);
		renderer.setXAxisMin(-1);
		renderer.setXAxisMax(24.5);

		renderer.setZoomEnabled(false);
		renderer.setExternalZoomEnabled(false);
		renderer.setPanEnabled(false);

		float density = Common.getDensity(mActivity);
		if (density <= 1.5f) {
			renderer.setMargins(new int[] { Common.dip2px(mActivity, 5), Common.dip2px(mActivity, 5),
					Common.dip2px(mActivity, 18), Common.dip2px(mActivity, 5) });
		} else if (density >= 2.0f) {
			renderer.setMargins(new int[] { Common.dip2px(mActivity, 15), Common.dip2px(mActivity, 5),
					Common.dip2px(mActivity, 18), Common.dip2px(mActivity, 5) });
		}

		renderer.setXLabelsColor(Color.BLACK);// x������ɫ
		renderer.setYLabelsColor(0, Color.BLACK); // y������ɫ
		renderer.setLabelsColor(Color.BLACK);// x y title��ɫ

		renderer.setXLabels(0);// 0Ϊ����X�̶� �������ڱ�ʾx��ʾ��Ŀ

		renderer.setChartTitle("");
		renderer.setXTitle("");

		// ��24��ʾΪ6��
		renderer.addXTextLabel(1, "1");
		for (int i = 2; i <= 24; i++) {
			if (i % 4 == 0)
				renderer.addXTextLabel(i, i + "");
			else
				renderer.addXTextLabel(i, "");
		}

		renderer.setBarSpacing(1); // bar֮��ľ���

		SimpleSeriesRenderer r0 = new SimpleSeriesRenderer();
		r0.setColor(mActivity.getResources().getColor(R.color.color_putong));// (Color.rgb(143,
																				// 200,
																				// 35));//(46,
																				// 176,
																				// 221)
																				// 148,
																				// 195,
																				// 205
		SimpleSeriesRenderer r1 = new SimpleSeriesRenderer();
		r1.setColor(mActivity.getResources().getColor(R.color.color_julie));// (Color.rgb(239,
																			// 146,
																			// 50));

		renderer.addSeriesRenderer(r0);
		renderer.addSeriesRenderer(r1);
		renderer.addYTextLabel(0, "");

		return renderer;
	}

	@SuppressWarnings("deprecation")
	private void setXTextLabelWeek(XYMultipleSeriesRenderer renderer) {
		long dayOfSeconds = Common.getDateFromYYYYMMDD(mCurrentDay);
		dayOfSeconds = dayOfSeconds - mPreWeek * 7 * Constants.DAY_MILLSECONDS; // ������

		dayOfSeconds = dayOfSeconds - Constants.DAY_MILLSECONDS;

		String dayOfWeekStr = "";
		for (int i = 0; i < 7; i++) {
			String strDateTmp = Common.getDateFromTime(dayOfSeconds, df_Mx_dx);
			dayOfWeekStr = Common.GetWeekStr(new Date(dayOfSeconds).getDay()) + "\n" + strDateTmp;
			renderer.addXTextLabel(7 - i, dayOfWeekStr);
			dayOfSeconds = dayOfSeconds - (1000L * 60 * 60 * 24L);
		}
	}

	public XYMultipleSeriesRenderer getStrengthBarRendererWeek() {
		XYMultipleSeriesRenderer renderer = getBarRenderer();
		renderer.setInScroll(true);
		// renderer.setLegendTextSize(23);
		// renderer.setMargins(new int[] { 20, 25, 50, 10 });
		renderer.setShowLegend(false);
		renderer.setYAxisMin(0);
		renderer.setXAxisMin(0);
		renderer.setXAxisMax(7.5);

		renderer.setChartTitle("");
		renderer.setXTitle("");
		renderer.setBarSpacing(4); // bar֮��ľ���
		renderer.setXLabels(0);// 0Ϊ����X�̶� �������ڱ�ʾx��ʾ��Ŀ

		if (mCurrentDay != null && !mCurrentDay.equals("")) {
			setXTextLabelWeek(renderer);
		}

		renderer.setYLabelsColor(0, Color.BLACK);
		renderer.setAxisTitleTextSize(20);
		renderer.setXLabelsColor(Color.BLACK);

		float density = Common.getDensity(mActivity);
		if (density <= 1.5f) {
			renderer.setMargins(new int[] { Common.dip2px(mActivity, 5), Common.dip2px(mActivity, 5),
					Common.dip2px(mActivity, 27), Common.dip2px(mActivity, 5) });
		} else if (density >= 2.0f) {
			renderer.setMargins(new int[] { Common.dip2px(mActivity, 15), Common.dip2px(mActivity, 5),
					Common.dip2px(mActivity, 30), Common.dip2px(mActivity, 5) });
		}

		SimpleSeriesRenderer r0 = new SimpleSeriesRenderer();
		r0.setColor(mActivity.getResources().getColor(R.color.color_qingwei));// 148,
																				// 195,
																				// 205
		SimpleSeriesRenderer r1 = new SimpleSeriesRenderer();
		r1.setColor(mActivity.getResources().getColor(R.color.color_putong));
		SimpleSeriesRenderer r2 = new SimpleSeriesRenderer();
		r2.setColor(mActivity.getResources().getColor(R.color.color_julie));
		//
		// SimpleSeriesRenderer r0 = new SimpleSeriesRenderer();
		// r0.setColor(Color.rgb(46, 176, 221));// 148, 195, 205
		// SimpleSeriesRenderer r1 = new SimpleSeriesRenderer();
		// r1.setColor(Color.rgb(143, 200, 35));
		// SimpleSeriesRenderer r2 = new SimpleSeriesRenderer();
		// r2.setColor(Color.rgb(244, 117, 96));

		renderer.addSeriesRenderer(r0);
		renderer.addSeriesRenderer(r1);
		renderer.addSeriesRenderer(r2);

		return renderer;
	}

	public XYMultipleSeriesRenderer getStepNumBarRendererWeek() {
		XYMultipleSeriesRenderer renderer = getBarRenderer();
		renderer.setInScroll(true);
		renderer.setYAxisMin(0);
		// renderer.setYAxisMax(mStepNumSumMax + 10);
		renderer.setXAxisMin(0);
		renderer.setXAxisMax(7.5);
		renderer.setXLabelsColor(Color.BLACK);// x������ɫ
		renderer.setYLabelsColor(0, Color.BLACK); // y������ɫ
		renderer.setLabelsColor(Color.BLACK);// x y title��ɫ
		renderer.setXLabels(0);// 0Ϊ����X�̶� �������ڱ�ʾx��ʾ��Ŀ
		// renderer.setYLabelsAlign(Align.RIGHT); // y��������
		renderer.setAxesColor(Color.BLACK); // x y ����ɫ

		renderer.setChartTitle("");
		renderer.setShowLegend(false);
		renderer.setXTitle("");
		renderer.setYTitle("");

		float density = Common.getDensity(mActivity);
		if (density <= 1.5f) {
			renderer.setMargins(new int[] { Common.dip2px(mActivity, 5), Common.dip2px(mActivity, 5),
					Common.dip2px(mActivity, 32), Common.dip2px(mActivity, 5) });
		} else if (density >= 2.0f) {
			// renderer.setLegendHeight(80);
			renderer.setMargins(new int[] { Common.dip2px(mActivity, 15), Common.dip2px(mActivity, 5),
					Common.dip2px(mActivity, 30), Common.dip2px(mActivity, 5) });
		}

		if (mCurrentDay != null && !mCurrentDay.equals("")) {
			setXTextLabelWeek(renderer);
		}
		renderer.setBarSpacing(4); // bar֮��ľ���

		// SimpleSeriesRenderer r0 = new SimpleSeriesRenderer();
		// r0.setColor(Color.rgb(254, 142, 0));
		// renderer.addSeriesRenderer(r0);

		SimpleSeriesRenderer r0 = new SimpleSeriesRenderer();
		r0.setColor(mActivity.getResources().getColor(R.color.color_putong));// (Color.rgb(143,
																				// 200,
																				// 35));//(46,
																				// 176,
																				// 221)
																				// 148,
																				// 195,
																				// 205
		SimpleSeriesRenderer r1 = new SimpleSeriesRenderer();
		r1.setColor(mActivity.getResources().getColor(R.color.color_julie));// (Color.rgb(239,
																			// 146,
																			// 50));

		renderer.addSeriesRenderer(r0);
		renderer.addSeriesRenderer(r1);

		return renderer;
	}

	private void setXTextLabelMonth(XYMultipleSeriesRenderer renderer) {
		long dayOfSeconds = Common.getDateFromYYYYMMDD(mCurrentDay);

		dayOfSeconds = dayOfSeconds - mPreMonth * 30 * Constants.DAY_MILLSECONDS;

		dayOfSeconds = dayOfSeconds - (1000L * 60 * 60 * 24) * 2;// ����һ��
		int i = 1;
		while (i < 30) {
			String dayOfWeekStr = Common.getDateAsYYYYMMDD(dayOfSeconds);
			renderer.addXTextLabel(30 - i, dayOfWeekStr.substring(4, 6) + "/" + dayOfWeekStr.substring(6));
			i += 7;
			dayOfSeconds = dayOfSeconds - (1000L * 60 * 60 * 24) * 7;
		}
	}

	public XYMultipleSeriesRenderer getStrengthBarRendererMonth() {
		XYMultipleSeriesRenderer renderer = getBarRenderer();
		renderer.setInScroll(true);
		// renderer.setLegendTextSize(23); // ��΢ һ�� ���� �����С
		renderer.setYAxisMin(0);
		renderer.setShowLegend(false);
		// **
		// renderer.setYAxisMax(mStrengthSumMax + 10);
		renderer.setXAxisMin(-1);
		renderer.setXAxisMax(30.5);
		renderer.setChartTitle("");
		renderer.setXTitle("");
		renderer.setBarSpacing(1); // bar֮��ľ���
		renderer.setXLabels(0);// 0Ϊ����X�̶� �������ڱ�ʾx��ʾ��Ŀ

		// ��30����ʾΪ5��
		if (mCurrentDay != null && !mCurrentDay.equals("")) {
			setXTextLabelMonth(renderer);
		}

		float density = Common.getDensity(mActivity);
		if (density <= 1.5f) {
			renderer.setMargins(new int[] { Common.dip2px(mActivity, 5), Common.dip2px(mActivity, 5),
					Common.dip2px(mActivity, 13), Common.dip2px(mActivity, 5) });
		} else if (density >= 2.0f) {
			renderer.setMargins(new int[] { Common.dip2px(mActivity, 15), Common.dip2px(mActivity, 5),
					Common.dip2px(mActivity, 18), Common.dip2px(mActivity, 5) });
		}

		renderer.setYLabelsColor(0, Color.BLACK);
		renderer.setAxisTitleTextSize(20);
		renderer.setXLabelsColor(Color.BLACK);

		SimpleSeriesRenderer r0 = new SimpleSeriesRenderer();
		r0.setColor(mActivity.getResources().getColor(R.color.color_qingwei));// 148,
																				// 195,
																				// 205
		SimpleSeriesRenderer r1 = new SimpleSeriesRenderer();
		r1.setColor(mActivity.getResources().getColor(R.color.color_putong));
		SimpleSeriesRenderer r2 = new SimpleSeriesRenderer();
		r2.setColor(mActivity.getResources().getColor(R.color.color_julie));

		renderer.addSeriesRenderer(r0);
		renderer.addSeriesRenderer(r1);
		renderer.addSeriesRenderer(r2);

		return renderer;
	}

	public XYMultipleSeriesRenderer getStepNumBarRendererMonth() {
		XYMultipleSeriesRenderer renderer = getBarRenderer();
		renderer.setInScroll(true);
		renderer.setYAxisMin(0);

		renderer.setShowLegend(false);
		// renderer.setYAxisMax(mStepNumSumMax + 10);
		renderer.setXAxisMin(-2);
		renderer.setXAxisMax(30.5);

		renderer.setXLabelsColor(Color.BLACK);// x������ɫ
		renderer.setYLabelsColor(0, Color.BLACK); // y������ɫ
		renderer.setLabelsColor(Color.BLACK);// x y title��ɫ
		renderer.setXLabels(0);// 0Ϊ����X�̶� �������ڱ�ʾx��ʾ��Ŀ

		float density = Common.getDensity(mActivity);
		if (density <= 1.5f) {
			renderer.setMargins(new int[] { Common.dip2px(mActivity, 5), Common.dip2px(mActivity, 5),
					Common.dip2px(mActivity, 18), Common.dip2px(mActivity, 5) });
		} else if (density >= 2.0f) {
			// renderer.setLegendHeight(80);
			renderer.setMargins(new int[] { Common.dip2px(mActivity, 15), Common.dip2px(mActivity, 5),
					Common.dip2px(mActivity, 18), Common.dip2px(mActivity, 5) });
		}

		// renderer.setYLabelsAlign(Align.RIGHT); // y��������
		renderer.setAxesColor(Color.BLACK); // x y ����ɫ

		renderer.setChartTitle("");
		renderer.setXTitle("");
		// renderer.setYTitle("��������");

		renderer.setBarSpacing(1); // bar֮��ľ���

		// ��30����ʾΪ5��
		if (mCurrentDay != null && !mCurrentDay.equals("")) {
			setXTextLabelMonth(renderer);
		}

		SimpleSeriesRenderer r0 = new SimpleSeriesRenderer();
		r0.setColor(mActivity.getResources().getColor(R.color.color_putong));// (Color.rgb(143,
																				// 200,
																				// 35));//(46,
																				// 176,
																				// 221)
																				// 148,
																				// 195,
																				// 205
		SimpleSeriesRenderer r1 = new SimpleSeriesRenderer();
		r1.setColor(mActivity.getResources().getColor(R.color.color_julie));// (Color.rgb(239,
																			// 146,
																			// 50));

		renderer.addSeriesRenderer(r0);
		renderer.addSeriesRenderer(r1);

		// SimpleSeriesRenderer r0 = new SimpleSeriesRenderer();
		// r0.setColor(Color.rgb(208, 78, 0));
		// r0.setColor(Color.rgb(254, 142, 0));
		// r0.setGradientStart(0, Color.rgb(208, 78, 0));
		// r0.setGradientStop(mStepNumSumMax, Color.rgb(254, 142, 0));

		// renderer.addSeriesRenderer(r0);

		return renderer;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageview_share:
			// GetWindowBitmap.shoot(mActivity, mLinearLayoumActivitytogram);
			// ��������ҳ��
			// SharedApplication();
			// ΢�ŷ���
			SharedWeiXin();
			break;
		case R.id.button_input_bg_back:
			showMenu();
			break;
		case R.id.linear_null:
			if (mCustomDialog != null)
				mCustomDialog.dismiss();
			break;
		default:
			break;
		}

	}

	private String mShareContext;
	private String mStrUpdateDate;
	private boolean mCheckWinXin;
	private RelativeLayout mRelativeLayoutProgress;
	private CenterRollingBall mCenterRollingBall;
	private TextView mTextViewRunStep;
	private TextView mTextViewPeriodSumStep;
	private TextView mTextViewStepPercent;
	private RelativeLayout mRelativeLayoutCenterprogress;
	private RelativeLayout mRelativeLayoutCenterprogressPeriod;

	private DownFlashView mRefreshableView;

	@SuppressWarnings("unused")
	private void SharedApplication() {

		if (mPedoInfo == null) {
			BaseToast("����û�и�������,�����");
			return;
		}
		UMSocialService controller = UMServiceFactory.getUMSocialService("umeng", RequestType.SOCIAL);
		// ����Ĭ�Ϸ�������
		SimpleDateFormat YYMM = new SimpleDateFormat("yyyy��MM��dd��");
		SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		String stepLen = info.getString("STEPLENGTH", "");
		String step = mPedoInfo.data.stepNum;
		String cal = mPedoInfo.data.cal;

		int nBowl = (Common.calstrToInt(cal) + 57) / 115;
		mShareContext = "#�����˶��ڵ�#˵:�ҽ����ֹ" + mStrUpdateDate + ", ����" + step + "��, ȼ������ " + cal + "ǧ��, �൱��" + nBowl
				+ "���׷�����ӭ���������˶�, ʵʱ��������Ȥ�����ɵ�������ϸ��־, ׷��ͷ�����������������ᡣ";
		//
		// mShareContext = "#�����˶���¼��#������:�Ҵ�" + time + " 00:00 ��"
		// + mSstrUpdateDate + mStrTime + "), ����" + step + " ����"
		// + Integer.valueOf(stepLen) * Integer.valueOf(step)
		// / 100 + " �ף�ȼ�� " + cal + " ǧ����";

		controller.setShareContent(mShareContext);
		// ���÷���ͼƬ(֧��4�ַ�ʽ),һ��ActionBar���ֻ��ѡ��һ��
		// ע�⣺Ԥ��ͼƬ���������л�ͼƬ���󣬲���������ʹ�á�
		// Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(),
		// R.drawable.umeng_socialize_sina_on);
		// byte[] byte2 = BitmapArrayUtil.bmpToByteArray(bitmap1,
		// false);
		// controller.setShareMedia(new UMImage(mActivity,
		// byte2));

		SocializeConfig socializeConfig = new SocializeConfig();
		socializeConfig.setPlatforms(SHARE_MEDIA.QZONE, SHARE_MEDIA.TENCENT, SHARE_MEDIA.SINA, SHARE_MEDIA.RENREN,
				SHARE_MEDIA.DOUBAN);// ���÷���ƽ̨

		CustomPlatform mWXPlatform = new CustomPlatform("΢��", R.drawable.weixin_icon);
		// ΢�ŵ���¼�
		mWXPlatform.clickListener = new OnCustomPlatformClickListener() {

			@Override
			public void onClick(CustomPlatform customPlatform, String shareContent, UMediaObject shareImage) {
				SharedWeiXin();
			}
		};
		socializeConfig.addCustomPlatform(mWXPlatform);
		controller.setConfig(socializeConfig);
		controller.openShare(mActivity, false);

	}

	private boolean mimg_left = true;
	private boolean mimg_right = true;

	private RelativeLayout mTopLayout;
	private Bitmap mBitmapBig = null;

	private Bitmap mBottom;

	private Bitmap mTop;

	private Dialog mCustomDialog;

	private void SharedWeiXin() {
		if (mPedoInfo == null) {
			mPedoInfo = new DataPedometor();
			mPedoInfo = MHealthProviderMetaData.GetMHealthProvider(mActivity).getPedometerLatest();
		}
		mCustomDialog = new Dialog(mActivity, R.style.dialog_fullscreen);
		mCustomDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Window window = mCustomDialog.getWindow();
		window.setGravity(Gravity.BOTTOM); // �˴���������dialog��ʾ��λ��
		window.setWindowAnimations(R.style.mystyle); // ��Ӷ���
		View view = View.inflate(mActivity, R.layout.popwindow_wenxininfo, null);
		LinearLayout WxTop = (LinearLayout) view.findViewById(R.id.linear_null);
		WxTop.setOnClickListener(this);
		mCustomDialog.setContentView(view);
		mCustomDialog.show();
		((ImageView) view.findViewById(R.id.img_left_share)).setImageDrawable(mActivity.getResources().getDrawable(
				R.drawable.img_top));
		((ImageView) view.findViewById(R.id.img_right_share)).setImageDrawable(mActivity.getResources().getDrawable(
				R.drawable.img_bottom));

		mCustomDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface arg0) {
				if (mBitmapBig != null && !mBitmapBig.isRecycled()) {
					// ���ղ�����Ϊnull
					mBitmapBig.recycle();
					mBitmapBig = null;
				}
				if (mBottom != null && !mBottom.isRecycled()) {
					mBottom.recycle();
					mBottom = null;
				}

				if (mTop != null && !mTop.isRecycled()) {
					mTop.recycle();
					mTop = null;
				}
			}
		});

		final ImageView img_left = (ImageView) view.findViewById(R.id.img_shareleft_select);
		final LinearLayout share_left = (LinearLayout) view.findViewById(R.id.share_img_jb);
		mimg_left = true;
		mimg_right = true;
		share_left.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				if (mimg_left) {
					share_left.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.rect_green));
					img_left.setVisibility(View.VISIBLE);
					mimg_left = false;
				} else {
					share_left.setBackgroundDrawable(mActivity.getResources().getDrawable(
							R.drawable.imageview_round_while));
					img_left.setVisibility(View.INVISIBLE);
					mimg_left = true;
				}

			}
		});
		final ImageView img_right = (ImageView) view.findViewById(R.id.img_shareright_select);
		final LinearLayout share_right = (LinearLayout) view.findViewById(R.id.share_img_strong);
		share_right.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				if (mimg_right) {
					share_right.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.rect_green));
					img_right.setVisibility(View.VISIBLE);
					mimg_right = false;
				} else {
					share_right.setBackgroundDrawable(mActivity.getResources().getDrawable(
							R.drawable.imageview_round_while));
					img_right.setVisibility(View.INVISIBLE);
					mimg_right = true;
				}
			}
		});

		RelativeLayout textview_friends = (RelativeLayout) view.findViewById(R.id.textview_weixin_friends);
		textview_friends.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mCheckWinXin = true;
//				sendUrl();
				Bitmap b = null;
				if (!mimg_left & !mimg_right) {
					LinearLayout l = findView(R.id.biref_main);
					b = getBigBitmap(l);
					sendTofriends(b);
					mCustomDialog.dismiss();
					return;
				} else if (!mimg_left) {
					mTop = GetWindowBitmap.takeScreenShot(mActivity, mTopLayout);
					b = mTop;
				} else {
					mBottom = GetWindowBitmap.takeScreenShot(mActivity, mLinearLayoumActivitytogram);
					b = mBottom;
				}
				Bitmap bIime = GetWindowBitmap.takeScreenShot(mActivity, findView(R.id.rl_update_time));
				sendTofriends(ImageUtil.toConformBitmap(b, bIime));
				mCustomDialog.dismiss();
			}

		});
		RelativeLayout textview_quanzi = (RelativeLayout) view.findViewById(R.id.textview_weixin_quan);
		textview_quanzi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mCheckWinXin = false;
//				sendUrl();
				Bitmap b = null;
				if (!mimg_left & !mimg_right) {
					LinearLayout l = findView(R.id.biref_main);
					b = getBigBitmap(l);
				} else if (!mimg_left) {
					mTop = GetWindowBitmap.takeScreenShot(mActivity, mTopLayout);
					b = mTop;
				} else {
					mBottom = GetWindowBitmap.takeScreenShot(mActivity, mLinearLayoumActivitytogram);
					b = mBottom;
				}
				Bitmap bIime = GetWindowBitmap.takeScreenShot(mActivity, findView(R.id.rl_update_time));
				sendTofriends(ImageUtil.toConformBitmap(b, bIime));
				mCustomDialog.dismiss();
			}
		});
		TextView cancel = (TextView) view.findViewById(R.id.textview_share_cancel);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mCustomDialog.dismiss();
			}
		});
		// Animation animation =
		// AnimationUtils.loadAnimation(mActivity,
		// R.anim.activityinfo_in_tween);
		// view.startAnimation(animation);
		// mPopupWindow.showAtLocation(popview, Gravity.BOTTOM, 0, 0);
	}

	public Bitmap getBigBitmap(LinearLayout view) {
		return GetWindowBitmap.takeScreenShot(mActivity, view);
	}
	
	private void sendUrl(){
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = "http://www.baidu.com";
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = "WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long";
		msg.description = "WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description Very Long Very Long Very Long Very Long Very Long Very Long Very Long";
		Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.a);
		msg.thumbData = Util.bmpToByteArray(thumb, true);
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		req.scene = mCheckWinXin == true ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
		// ����api�ӿڷ������ݵ�΢��
		mWeiXinAPI.sendReq(req);
	}

	private void sendTofriends(Bitmap bmp) {
		if (!mWeiXinAPI.isWXAppInstalled()) {
			BaseToast("�㻹û�а�װ΢��");
			return;
		} else if (!mWeiXinAPI.isWXAppSupportAPI()) {
			BaseToast("�㰲װ��΢�Ű汾��֧�ֵ�ǰAPI");
			return;
		}
		// WXTextObject textObj = new WXTextObject();
		// textObj.text = mShareContext;
		WXImageObject imageObject = new WXImageObject(bmp);
//		// �����õ�ͼƬ���ֽ���
//		imageObject.imageData = b;

		// ��WXTextObject�����ʼ��һ��WXMediaMessage����
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = imageObject;
		Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
		bmp.recycle();
		msg.thumbData = Util.bmpToByteArray(thumbBmp, true);  // ��������ͼ
		// msg.mediaObject = textObj;
		// �����ı����͵���Ϣʱ��title�ֶβ�������
		// msg.description = mShareContext;
		// msg.title = "�ҵ�Ӧ��";

		// ����һ��Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("img"); // transaction�ֶ�����Ψһ��ʶһ������
		req.message = msg;
		req.scene = mCheckWinXin == true ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;

		// ����api�ӿڷ������ݵ�΢��
		mWeiXinAPI.sendReq(req);
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

	@Override
	public void onRefresh(DownFlashView view) {
		// ˢ�´���
		if (NetworkTool.getNetworkState(mActivity) != 0) {
			new UpdateDataThread().update(0);
		}
	}
	

	private boolean isServiceRunning() {
		ActivityManager manager = (ActivityManager) mActivity.getSystemService(mActivity.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if ("com.cmcc.ishang.lib.step.detector.StepService".equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	private MHealthStepReceiver mMHStepReceiver;

	private void registerStepReceiver() {
		mMHStepReceiver = new MHealthStepReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(StepService.STEP_SENDING);
		mActivity.registerReceiver(mMHStepReceiver, filter);
	}

	private class MHealthStepReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (StepService.STEP_SENDING.equals(intent.getAction())) {
				Bundle data = intent.getExtras();
				if (mCenterRollingBall != null && mTextViewRunStep != null && mTextViewStepPercent != null)
				{
					String stepPlan = sp.getString(SharedPreferredKey.TARGET_STEP, "10000");
					mCenterRollingBall.setMaxScore(Integer.valueOf(stepPlan));
					mCenterRollingBall.setScore(data.getInt("STEP_ALL_DAY"));
					mCenterRollingBall.invalidate();
					int value = data.getInt("STEP_ALL_DAY");
					mTextViewRunStep.setText(value + "");
					mTextViewStepPercent.setText(((value * 100)/Integer.valueOf(stepPlan)) + "%");
					
					int second = data.getInt("DURATION_ALL_DAY");
					int hour = second /3600;
					int minute = second / 60 - hour * 60;
					
					mTextViewCal.setText("" + data.getInt("CALORIE_ALL_DAY"));
				    mTextViewStepnum.setText("" + data.getInt("YXBS_ALL_DAY"));
				    mTextViewDistance.setText("" + (data.getInt("DISTANCE_ALL_DAY") / 1000));
				    mTextViewDuration.setText(hour +":" + minute);
				}
			}
		}
	}

	public static void sendAllowToReceiver(Context context,boolean allowed) {
		String filter;
		if (allowed) {
			filter = StepService.STEP_RECEIVED_ACTION_ON;
		} else {
			filter = StepService.STEP_RECEIVED_ACTION_STOP;
		}
		Intent intent = new Intent(filter);
		context.sendBroadcast(intent);
	}

}
