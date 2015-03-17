package cmcc.mhealth.slidingcontrol;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.ProgressDialog;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cmcc.mhealth.R;
import cmcc.mhealth.basic.BaseFragment;
import cmcc.mhealth.bean.VitalSignInfo;
import cmcc.mhealth.bean.VitalSignInfoDataBean;
import cmcc.mhealth.bean.VitalSignUploadState;
import cmcc.mhealth.bean.WeightInfo;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.common.PreferencesUtils;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.db.MHealthProviderMetaData;
import cmcc.mhealth.db.VitalSignMetaData;
import cmcc.mhealth.net.DataSyn;
import cmcc.mhealth.view.Indirector;
import cmcc.mhealth.view.Indirector.OnChangedListener;

public class WeightFragment extends BaseFragment implements OnClickListener {
	protected static final String TAG = "WeightActivity";
	private TextView mTextViewTitle;
	private Button mAddWeight;
	private String[] titles;
	private List<double[]> x;
	private List<double[]> values;
	private View mChartView;
	private XYMultipleSeriesRenderer mRenderer;
	// 刻度字体
	private TextView mTextviewNum;

	private int mType = 0;
	int mArrayThresh[][] = { { 10, 33, 61, 70, 450 }, { 10, 39, 78, 111, 450 } };

	// 用户的电话以及密码
	private String mPhonenum;
	private String mPassword;
	// 用于上传服务器
	private DataSyn mDateSyn;
	private WeightInfo mWeightInfo;
	// 用于显示刷新时间
	private TextView mTextViewReflesh;
	// 数据库
	private MHealthProviderMetaData mHealthProvider;
	// 数据集合
	private List<VitalSignInfoDataBean> mWeightBeanList;
	// 年月日的按钮切换
	private RadioGroup mRadioGroup;
	// 切换的类型，30日 60日 半年 一年
	private int type = 30;
	// 状态码用于提示用户更新是否成功
	private int stateCode = 1;

	// BMI小指针
	private TextView mBMIDirector;
	private LinearLayout mBMILinLayout;
	private double bmi;
	private float nowPos;
	// BMI宽度
	private float bmiWidth;

	// 全局重量变量
	private String mWeight;
	private double mHeight;
//	private String mAge;
	private int mGender;
	// 全局时间变量
//	private String mDate;
	
	//同步时的进度条

	// 初始化页面重量数值。并初始化标尺位置
	private static final int SETPOS_INIT = 1;
	// 设定bmi位置，并做动画
	private static final int SETPOS_BMI = 2;
	// 拖拽停止后的数据更新以及bmi动画
	private static final int SETPOS_BMI_AFTERMOVED = 3;
	// toast同步的无网络的提示
	private static final int SHOWTOASTEQLFAIL = 4;
	// toast添加的无网络的提示
	private static final int SHOWTOASTADDFAIL = 5;
	// toast同步成功
	private static final int SHOWTOASTEQLSUCCESS = 6;

	// 各种天数的毫秒值
	private static final float DAY1TIMEMILL = 86400000l;
	private static final float DAY30TIMEMILL = 2592000000l;
	private static final float DAY60TIMEMILL = 5184000000l;
	private static final float DAY182TIMEMILL = 15724800000l;
	private static final float DAY365TIMEMILL = 31536000000l;

	@Override
	public void findViews() {
		initView();
	}

	
	public static final WeightFragment newInstance() {
		WeightFragment rcf = new WeightFragment();
		return rcf;
	}
	
	@Override
	public void onStart() {
		if (mAddWeight != null) {
			mAddWeight.setVisibility(View.VISIBLE);
		}

		if (mDateSyn == null) {
			mDateSyn = DataSyn.getInstance();
		}
		if (mWeightInfo == null) {
			mWeightInfo = new WeightInfo();
		}

		mPhonenum = sp.getString(SharedPreferredKey.PHONENUM, "");
		mPassword = sp.getString(SharedPreferredKey.PASSWORD, "");
		
		getInfos();
		super.onStart();
	}

	// 载入界面的时候的数据交换同步
	private void getInfos() {
		new Thread() {
			public void run() {
				Editor edit = sp.edit();
				String DLStarttime = sp.getString("DLStarttime", "2000-01-01_10:10:10");
				String ULStarttime = sp.getString("ULStarttime", "2000-01-01_10:10:10");
				//下载的数据
				VitalSignInfo vital = new VitalSignInfo();
				int suc = mDateSyn.getVitalSignAfterLastDownloadSuccessTime(mPhonenum, mPassword, DLStarttime, vital);
				if (suc == 0) {
					// 上传的数据，下载数据前先将上拉时间之后的数据从数据库里拿出来用于上传
					VitalSignUploadState state = new VitalSignUploadState();
					List<VitalSignInfoDataBean> weightInRange = mHealthProvider.getWeightInRange(ULStarttime, VitalSignMetaData.EDITTIMEBYLONGSTYLE);// ←上拉时间
					if (vital.datavalue != null) {
						List<VitalSignInfoDataBean> listVsb = vital.datavalue.getDataArray();
						if (listVsb != null && listVsb.size() > 0) {
							// 存入手机数据库
							mHealthProvider.addWeightValue(listVsb);
							// 最后一个数据
							mWeight = listVsb.get(listVsb.size() - 1).getValue();
							edit.putString("DLStarttime", vital.datavalue.getDownloadTime());
						}
					}
					// 将 上拉时间后的数据上传到服务器
					mDateSyn.postVitalSign(mPhonenum, mPassword, weightInRange, state, VitalSignMetaData.TYPE_STR_WEIGHT);
					if (state.datavalue != null) {
						edit.putString("ULStarttime", state.datavalue.getUpdateTime());
					}
					handle.sendEmptyMessage(SHOWTOASTEQLSUCCESS);
				} else {
					handle.sendEmptyMessage(SHOWTOASTEQLFAIL);
				}
				// 取出规定天数内的所有数据
				mWeightBeanList = mHealthProvider.getWeightInRange(new Date().getTime() - (long) getTimeMillByType(type),VitalSignMetaData.MEASUREDATEBYLONGSTYLE);
				Logger.i(TAG, "mWeightBeanList.size == " + mWeightBeanList.size());
				if (mWeightBeanList.size() > 0) {
					mWeight = mWeightBeanList.get(mWeightBeanList.size() - 1).getValue();
				} else {
					mWeight = "50.0";
				}
				edit.commit();
				handleForAcharView.sendEmptyMessage(type);
			};
		}.start();
	}

	private Handler handle = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
			switch (msg.what) {
			case SETPOS_BMI_AFTERMOVED:
				mTextviewNum.setText(mWeight);
				CaculateBMI(mWeight);
				handle.sendEmptyMessage(SETPOS_BMI);
				break;
			case SETPOS_INIT:
				SetTestViewAndCurIndicator();
			case SETPOS_BMI:// 动态指定BMI图标位置
				String st = SetBMIPos();
				mBMIDirector.setText(st);
 				break;
			case SHOWTOASTEQLSUCCESS:
				BaseToast("体重同步成功");
				break;
			case SHOWTOASTEQLFAIL:
				BaseToast("体重同步失败，请确认网络可用。");
				break;
			case SHOWTOASTADDFAIL:
				BaseToast("体重无法添加到服务器，已存在本地，请确认网络可用。");
				break;
			}
		}

	};

	private Handler handleForAcharView = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
			// 绘制图表
			AcharView(msg.what);
			handle.sendEmptyMessageDelayed(SETPOS_INIT, 0);
		}
	};
	private ImageButton mBack;

	@Override
	public void loadLogic() {
		mTextviewNum.setText(sp.getString(SharedPreferredKey.WEIGHT,""));
		mTextviewTargetWeight.setText(sp.getString(SharedPreferredKey.TARGET_WEIGHT,""));
		String strTmpTargetWeight = sp.getString(SharedPreferredKey.TARGET_WEIGHT,"");
        if (strTmpTargetWeight != null && !"".equals(strTmpTargetWeight)) {
            iTargetWeight = (int)Double.parseDouble(strTmpTargetWeight);
        } else {
            iTargetWeight = 65;
        }
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_weight, container, false);
		super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// outState.putString("mColorRes", mFlag);
	}

	@Override
	public void onResume() {
		// UMFeedbackService.enableNewReplyNotification(getMyActivity(),
		// NotificationType.NotificationBar);
		super.onResume();
	}

	private void SetTestViewAndCurIndicator() {
		if (mIndirector == null || mWeight == null) {
			Logger.d("WhatTerribleFailure", "mIndirector or mWeight is null at" + TAG + "'s SetTestViewAndCurIndicator()");
		} else {
			mIndirector.setWeight(Float.parseFloat(mWeight));
			mTextviewNum.setText(mWeight);
			CaculateBMI(mWeight);
		}
	}

	// 计算bmi
	private double CaculateBMI(String weight) {
	    
		double height = Double.valueOf(PreferencesUtils.getString(mActivity, SharedPreferredKey.HEIGHT, "175.0"));
		if (mWeightInfo.datavalue != null) {
			height = Double.parseDouble(mWeightInfo.datavalue.height);
		}
		height /= 100;
		double weightD = Double.parseDouble(weight);
		bmi = weightD / (height * height);
		return height;
	};

	// 重画图表
	protected void OnlyDraw() {
		new Thread() {
			public void run() {
				mWeightBeanList = mHealthProvider.getWeightInRange(new Date().getTime() - (long) getTimeMillByType(type),VitalSignMetaData.MEASUREDATEBYLONGSTYLE);
				Logger.i(TAG, "mWeightBeanList.size == " + mWeightBeanList.size());
				handleForAcharView.sendEmptyMessage(type);
			};
		}.start();
	}

	// 设定bmi位置，并返回bmi文本值
	private String SetBMIPos() {
		double bl = 0d;
		double tampbmi = bmi;

		bmiWidth = mBMILinLayout.getWidth();
		Logger.i(TAG, "bmiWidth==" + bmiWidth);

		if (tampbmi < 16d) {
			tampbmi = 16d;
		} else if (tampbmi > 31d) {
			tampbmi = 31d;
		}

		bl = (tampbmi - 16d) / 15d;

		if (bl > 0.95d) {
			bl = 0.95d;
		} else if (bl < 0.05d) {
			bl = 0.05d;
		}

		float pos = (float) (bmiWidth * bl) - bmiWidth / 15.2f;

		DecimalFormat df = new DecimalFormat(".#");
		String st = df.format(bmi);

		nowPos = MoveWithAnim(nowPos, pos);
		return st;
	}

	// 添加体重到数据库并重画图表(同时上传服务器)
	protected void addWeightAndDraw() {
		new Thread() {
			public void run() {
				String timemill = Common.getCurrentDayLongTime(new Date().getTime());
				String editime = Common.getDateFromLongToStr(new Date().getTime());

				mHealthProvider.addWeightValue(WeightPushToServer(editime, timemill));
				double height = CaculateBMI(mWeight);
				// 18.5 24 27 29
				mArrayThresh[mType][0] = (int) (18.5 * (height * height));
				mArrayThresh[mType][1] = (int) (24 * (height * height));
				mArrayThresh[mType][2] = (int) (27 * (height * height));
				mArrayThresh[mType][3] = (int) (29 * (height * height));
				handle.sendEmptyMessage(SETPOS_BMI);
				OnlyDraw();
			}
		}.start();
	}

	// 将在线添加的体重上传到服务器
	private VitalSignInfoDataBean WeightPushToServer(String editime, String timemill) {
		List<VitalSignInfoDataBean> weightInRange = new ArrayList<VitalSignInfoDataBean>();
		VitalSignInfoDataBean vsb = new VitalSignInfoDataBean();
		vsb.setEditTime(editime);
		vsb.setMeasureDate(timemill);
		vsb.setValue(mWeight);
		weightInRange.add(vsb);
		VitalSignUploadState state = new VitalSignUploadState();
		int pvs = mDateSyn.postVitalSign(mPhonenum, mPassword, weightInRange, state, VitalSignMetaData.TYPE_STR_WEIGHT);
		if (pvs == 0) {
			if (state.datavalue != null) {
				Editor edit = sp.edit();
				edit.putString("ULStarttime", state.datavalue.getUpdateTime());
				edit.commit();
			}
		} else {
			handle.sendEmptyMessage(SHOWTOASTEQLFAIL);
		}
		return vsb;
	}

	// 移动动画
	private float MoveWithAnim(float nowpos, float pos) {
		Animation animation = new TranslateAnimation(nowPos, pos, 0f, 0F);
		animation.setFillAfter(true);
		animation.setDuration(400);
		mBMILinLayout.startAnimation(animation);
		return pos;
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		mWeightInfo = new WeightInfo();
		mHealthProvider = MHealthProviderMetaData.GetMHealthProvider(mActivity);
		
		mBMILinLayout = findView(R.id.ll_bmi_director);
		mBMIDirector = findView(R.id.tv_bmi_director);
		
//		getParent().requestDisallowInterceptTouchEvent(true);
		
		// 体重数据textview
		mTextviewNum = (TextView) findView(R.id.textview_overloads);
		mTextviewTargetWeight = findView(R.id.target_weight);
		mTextViewReflesh = (TextView) findView(R.id.tv_reflesh_time);

		mRadioGroup = (RadioGroup) findView(R.id.rg_weight_switcher);
//		mRadioButton30days = (RadioButton) findView(R.id.rb_weight_switch1);
//		mRadioButto60days = (RadioButton) findView(R.id.rb_weight_switch2);
//		mRadioButtonHalfYears = (RadioButton) findView(R.id.rb_weight_switch3);
//		mRadioButtonOneYear = (RadioButton) findView(R.id.rb_weight_switch4);
		// 点击不同的按钮
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_weight_switch1:
					type = 30;
					break;
				case R.id.rb_weight_switch2:
					type = 60;
					break;
				case R.id.rb_weight_switch3:
					type = 182;
					break;
				case R.id.rb_weight_switch4:
					type = 365;
					break;
				}
				OnlyDraw();
			}
		});

//		mAddWeight = (Button) findView(R.id.button_add);
////		mAddWeight.setBackgroundResource(R.drawable.set_state_ok);
//		mAddWeight.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if(mWeightBeanList == null){
//					BaseToast("仍在载入数据请稍后~");
//					return;
//				}
//				Editor edit = sp.edit();
//				Date date = new Date();
//				edit.putString("weight_reflesh_time", Common.TimeFormatter(date));
//				mTextViewReflesh.setText("更新于 " + Common.TimeFormatter(date));
//				edit.commit();
//
//				addWeightAndDraw();
//			}
//		});
		// RuleView();
		NewRuleView();
	}

	private RelativeLayout mTvbmiruleview;
	private Indirector mIndirector;
	private TextView mTextviewTargetWeight;

	private void NewRuleView() {
		mTvbmiruleview = findView(R.id.tv_bmi_ruleview);
		mIndirector = new Indirector(mActivity);
		mIndirector.setWeight(50);
		mIndirector.setOnChangedListener(new OnChangedListener() {
			@Override
			public void onChanged(float num) {
				mWeight = num + "";
				handle.sendEmptyMessage(SETPOS_BMI_AFTERMOVED);
			}
		});
		mTvbmiruleview.addView(mIndirector);
	}

	protected Display getDisplayParems() {
		WindowManager windowManager = mActivity.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		return display;
	}

	private void AcharView(int days) {

        maxWeight=0;
        minWeight=120;
		if (mWeightBeanList.size() == 0) {
			BaseToast(days + "天内没有数据");
			return;
		}

		LinearLayout acharView = findView(R.id.acharView);
		titles = new String[] { "体重", "目标" };
		x = new ArrayList<double[]>();

		// x.add(new double[] { 1, 2});// 每个序列中点的X坐标
		double[] len = pointNum2DoubleArray(mWeightBeanList, days);
		double[] len2 = pointNum2DoubleArrayLine(mWeightBeanList, days); 
		x.add(len);
		x.add(len2);

		values = new ArrayList<double[]>();
		// 集合转换成数组
		double[] doubleArray = List2Array(mWeightBeanList);
		values.add(doubleArray);// 序列1中点的y坐标

		double[] pointNum2DoubleLine = pointNum2DoubleLine(mWeightBeanList, iTargetWeight);
		values.add(pointNum2DoubleLine);

		int[] colors = new int[] { Color.BLUE, Color.rgb(255, 128, 64) };// 每个序列的颜色设置

		PointStyle[] styles = null;
		if (type > 60) {
			styles = new PointStyle[] { PointStyle.POINT, PointStyle.POINT };// 每个序列中点的形状设置
		} else {
			styles = new PointStyle[] { PointStyle.DIAMOND, PointStyle.POINT };// 每个序列中点的形状设置
		}

		mRenderer = buildRenderer(colors, styles);
		mRenderer.setZoomEnabled(false, false);
		mRenderer.setExternalZoomEnabled(false);
		mRenderer.setPanEnabled(false, false);

		iminWeight = (((int)(minWeight/5)*5));
		imaxWeight = (((int)((maxWeight+4.9)/5)*5));
		if(iminWeight>iTargetWeight){
		    iminWeight = (iTargetWeight/5)*5;
		}
        if(imaxWeight <iTargetWeight){
            imaxWeight = ((iTargetWeight+4)/5)*5;
        }
		// int length = mRenderer.getSeriesRendererCount();
		// for (int i = 0; i < length; i++) {
		// mRenderer.setBackgroundColor(Color.argb(0, 0xff, 0, 0));
		// }
		double lend = len.length > 0 ? len[len.length - 1] : 0;
		VitalSignInfoDataBean bean = mWeightBeanList.size() > 0 ? mWeightBeanList.get(mWeightBeanList.size() - 1) : null;
		mRenderer = setChartSettings(mRenderer, "", "", "", 0, days, iminWeight, imaxWeight, Color.GRAY, Color.GRAY, lend, bean);// 调用AbstractDemoChart中的方法设置图表的rendere
		
		mRenderer.setLegendTextSize(28);
		
		mChartView = ChartFactory.getLineChartView(mActivity, buildDataset(titles, x, values), mRenderer);
		// if(renderer.isClickEnabled()){
		// Toast.makeText(this,"click",0 ).show();
		// }
		// mChartView.setBackgroundColor(getResources().getColor(R.color.blue));
		acharView.removeAllViews();
		acharView.addView(mChartView);
	}

	private XYMultipleSeriesDataset buildDataset(String[] titles2, List<double[]> xValues, List<double[]> yValues) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		addXYSeries(dataset, titles, xValues, yValues, 0);
		return dataset;
	}

	private void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles2, List<double[]> xValues, List<double[]> yValues, int scale) {
		if (xValues.size() > 0 && xValues.size() == yValues.size()) {
			int length = titles.length;
			double[] xV = xValues.get(0);
			for (int i = 0; i < length; i++) {
				XYSeries series = new XYSeries(titles[i], scale);
				double[] yV = yValues.get(i);
				double[] xV2 = xValues.get(i);
				int seriesLength = xV.length;
				for (int k = 0; k < seriesLength; k++) {
					series.add(xV2[k], yV[k]);
				}
				dataset.addSeries(series);
			}
		} else {
			throw new RuntimeException("addXYSeries x坐标数与y坐标数不一致，无法绘图");
		}
	}

	/**
	 * 
	 * setChartSettings(这里用一句话描述这个方法的作用)
	 * 
	 * @param renderer
	 * @param title
	 *            大标题
	 * @param xTitle
	 *            x轴描述
	 * @param yTitle
	 *            y轴描述
	 * @param xMin
	 * @param xMax
	 *            DOM对象
	 * @param yMin
	 *            DOM对象
	 * @param yMax
	 *            DOM对象
	 * @param atWhere
	 * @return 
	 */
	protected XYMultipleSeriesRenderer setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle, String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor, int labelsColor, double atWhere, VitalSignInfoDataBean lastWb) {
		renderer.setInScroll(true);
		renderer.setChartTitle(title);
//		renderer.setChartTitleTextSize(30);
		renderer.setYTitle(yTitle);// 设置Y轴名称
		// renderer.setChartValuesTextSize(20);
		renderer.setXLabelsColor(axesColor); // x轴坐标颜色
		renderer.setYLabelsColor(0, labelsColor);
		renderer.setApplyBackgroundColor(true);// 允许自定义背景
		renderer.setBackgroundColor(mActivity.getResources().getColor(R.color.lucensy));// 内部表格颜色
		renderer.setMargins(new int[] { Common.dip2px(mActivity, 25), Common.dip2px(mActivity, 25), Common.dip2px(mActivity, 15), Common.dip2px(mActivity, 5) });
		renderer.setMarginsColor(mActivity.getResources().getColor(R.color.lucensy));// 表格外边颜色
		renderer.setXTitle(xTitle);
		// renderer.setXLabels(12);//
		// 设置x轴显示12个点,根据setChartSettings的最大值和最小值自动计算点的间隔
		renderer.setYLabels(5);// 设置y轴显示10个点,根据setChartSettings的最大值和最小值自动计算点的间隔
		renderer.setShowGrid(true);// 是否显示网格
		renderer.setXLabelsAlign(Align.CENTER);// 刻度线与刻度标注之间的相对位置关系
		renderer.setYLabelsAlign(Align.RIGHT);// 刻度线与刻度标注之间的相对位置关系
		renderer.setZoomButtonsVisible(false);// 是否显示放大缩小按钮
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax * 1.1f);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(Color.BLACK); // xy轴坐标线颜色
		renderer.setLabelsColor(R.color.org_IIII); // 标签颜色
		
		// renderer.setClickEnabled(true);

		// 最新数据的时间
		
		
		long nowTimeMill = new Date().getTime();
		long lastTimeMill = 0;
		if (lastWb != null) {
			Date date = new Date();
			lastTimeMill = Long.parseLong(lastWb.getMeasureDateByLongStyle());
			date.setTime(lastTimeMill);
			mTextViewReflesh.setText("更新于 " + Common.TimeFormatter(date));
		}
		if(nowTimeMill < lastTimeMill){
			atWhere--;
		}
		int timeGrap = (int) ((nowTimeMill - lastTimeMill) / getTimeMillByType(1));
//		float grap = (float) (timeGrap / xMax);
		atWhere += timeGrap;

		if (atWhere < 0)
			atWhere = 0;
		if (atWhere > xMax)
			atWhere = xMax;

		renderer.setXLabels(0);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);

		int distance = (int) xMax / 5;
		renderer.addTextLabel(atWhere, (month + 1) + "/" + day + "*");

		for (int i = 0; i < (atWhere / xMax / 0.2); i++) {
			cal.setTime(new Date());
			cal.add(Calendar.DAY_OF_YEAR, -i * distance);
			month = cal.get(Calendar.MONTH);
			day = cal.get(Calendar.DAY_OF_MONTH);
			renderer.addTextLabel(atWhere - i * distance, (month + 1) + "/" + day);
		}
		for (int i = 0; i < (1 - atWhere / xMax) / 0.2; i++) {
			cal.setTime(new Date());
			cal.add(Calendar.DAY_OF_YEAR, i * distance);
			month = cal.get(Calendar.MONTH);
			day = cal.get(Calendar.DAY_OF_MONTH);
			renderer.addTextLabel(atWhere + i * distance, (month + 1) + "/" + day);
		}

		// renderer.setPanLimits(new double[] { 0, 20, 0, 40 }); //
		// 设置拖动时X轴Y轴允许的最大值最小值.
		// renderer.setZoomLimits(new double[] { -10, 20, -10, 40 });//
		// 设置放大缩小时X轴Y轴允许的最大最小值.
		
		return renderer;
	}

	private XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer = setRenderer(renderer, colors, styles);
		return renderer;
	}

	private XYMultipleSeriesRenderer setRenderer(XYMultipleSeriesRenderer renderer, int[] colors, PointStyle[] styles) {
		// renderer.setAxisTitleTextSize(16);
		// renderer.setChartTitleTextSize(20);
		renderer.setAxisTitleTextSize(Common.dip2px(mActivity, 8));
		renderer.setChartTitleTextSize(Common.dip2px(mActivity, 8));
//		renderer.setLegendTextSize(Common.dip2px(mActivity, 12));
		renderer.setLabelsTextSize(Common.dip2px(mActivity, 12));
		renderer.setClickEnabled(false);
		// renderer.setLabelsTextSize(12); // 标注字
		// renderer.setLegendTextSize(15);
		renderer.setPointSize(5f);
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors[i]);
			r.setPointStyle(styles[i]);
			renderer.addSeriesRenderer(r);
			if (1 == i) {
				r.setStroke(BasicStroke.DASHED);
			}
			if (i == 0) {
				r.setFillPoints(true);
				r.setFillBelowLine(true); // 线内填充颜色
				r.setFillBelowLineColor(mActivity.getResources().getColor(R.color.fenhong));
			}
		}
		
		return renderer;

	}

	// 设定折线图A的x坐标
	private double[] pointNum2DoubleArray(List<VitalSignInfoDataBean> wblist, int maxday) {
		double[] d = null;

		Date date = new Date();
		d = new double[wblist.size()];
		double firstplace = 0d;
		if (wblist.size() > 0) {
			long timemill = Common.getDateFromStr(wblist.get(0).getMeasureDate());
			firstplace = (date.getTime() - timemill) / getTimeMillByType(maxday);
		}

		for (int i = 0; i < wblist.size(); i++) {
			long timemill =  Common.getDateFromStr(wblist.get(i).getMeasureDate());
			double timebl = (date.getTime() - timemill) / getTimeMillByType(maxday);
			timebl = Math.abs(timebl - firstplace);
			timebl = timebl > 1 ? 1 : timebl;
			d[i] = maxday * timebl;
		}
		return d;
	}

	// 设定预期线x坐标
	private double[] pointNum2DoubleArrayLine(List<VitalSignInfoDataBean> wblist, int maxday) {
		double[] d = null;
		d = new double[wblist.size()];
		if (wblist.size() > 1) {
			d[wblist.size() - 1] = maxday * 1.1f;
		}
		return d;
	}

	// 设定折线图A的y坐标
	private double[] List2Array(List<VitalSignInfoDataBean> wblist) {
		double[] d = null;
		d = new double[wblist.size()];
		for (int i = 0; i < wblist.size(); i++) {
			String value = wblist.get(i).getValue();
			if (value == null)
				value = "50.0";
			d[i] = Double.parseDouble(value);
			if(d[i]>maxWeight){
			    maxWeight = d[i];
			}
            if(d[i]<minWeight){
                minWeight = d[i];
            }

		}
		return d;
	}

	// 设定预期线y坐标(按统一值来)
	private double[] pointNum2DoubleLine(List<VitalSignInfoDataBean> wblist, double standedNum) {
		double[] d = null;
		d = new double[wblist.size()];
		for (int i = 0; i < d.length; i++) {
			d[i] = standedNum;
		}
		return d;
	}

	private float getTimeMillByType(int type) {
		switch (type) {
		case 1:
			return DAY1TIMEMILL;
		case 30:
			return DAY30TIMEMILL;
		case 60:
			return DAY60TIMEMILL;
		case 182:
			return DAY182TIMEMILL;
		case 365:
			return DAY365TIMEMILL;
		}
		return DAY30TIMEMILL;
	}

	@Override
	public void clickListner() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		default:
			break;
		}
	}
double maxWeight=0, minWeight=120;
int imaxWeight = 0, iminWeight = 120;
int iTargetWeight=0;
}
