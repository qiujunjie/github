package cmcc.mhealth.activity;

import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import cmcc.mhealth.R;
import cmcc.mhealth.basic.BaseActivity;
import cmcc.mhealth.bean.FriendPedometorSummary;
import cmcc.mhealth.bean.PedometorSummary;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Constants;
import cmcc.mhealth.common.Logger;

public class FriendPedometerInfoActivity extends BaseActivity {
	private final static String TAG = "FriendPedometerInfoActivity";

	private String membername;

	private TextView mDuration;
	private TextView mEnergy;
	private TextView mDistance;
	private TextView mStepnum;
	private TextView mAddupsteps;
	
	private FriendPedometorSummary fpsReqData;// 好友简报数据

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_pedo_brief);
		initview();
		initDatas();
		showDatas();
	}
	//展示数据
	private void showDatas() {
		List<PedometorSummary> friendPSList = fpsReqData.friendsinfo;
		if (friendPSList.size() > 0) {
			mCurrentDay = friendPSList.get(friendPSList.size() - 1).date;
			initHistogram(friendPSList);
		}
	}

	private RadioGroup mRadioGroupDayDetail;

	// 显示图表
	private void initHistogram(final List<PedometorSummary> friendPSList) {
		mLinearLayouthistogram = (LinearLayout) findViewById(R.id.afpb_linearLayout_day_detail);
		showStrengthPeriodData(friendPSList.size(), friendPSList);
		// 今天 步数强度切换按钮
		mRadioGroupDayDetail = (RadioGroup) findViewById(R.id.afpb_radioGroup_daydetail);
		mRadioGroupDayDetail.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.afpb_radio_step:
					showStepNumPeriodData(friendPSList.size(), friendPSList);
					mLinearLayoutSteps.setVisibility(View.VISIBLE);
					mLinearLayoutMinute.setVisibility(View.GONE);
					break;
				case R.id.afpb_radio_strength:
					showStrengthPeriodData(friendPSList.size(), friendPSList);
					mLinearLayoutMinute.setVisibility(View.VISIBLE);
					mLinearLayoutSteps.setVisibility(View.GONE);
					break;
				}
			}

		});
	};

	// 初始化页面
	private void initview() {
		mDuration = (TextView) findViewById(R.id.afpb_TextView_Duration);
		mEnergy = (TextView) findViewById(R.id.afpb_textView_cal);
		mDistance = (TextView) findViewById(R.id.afpb_textView_distance);
		mStepnum = (TextView) findViewById(R.id.afpb_textView_stepnum);
		mAddupsteps = (TextView) findViewById(R.id.afpb_stepnumofperiod);
		
		mLinearLayoutMinute = (LinearLayout) findViewById(R.id.lay_minute);
		mLinearLayoutSteps = (LinearLayout) findViewById(R.id.lay_step);
		
	}

	private void initDatas() {
		membername = (String) getIntent().getExtras().get("membername");
		fpsReqData = (FriendPedometorSummary) getIntent().getExtras().get("fpsReqData");
		BaseBackKey(membername + "的运动简报", this);
	}

	private double mStrengthSumMax;
	private GraphicalView mHistogramChartview;
	private LinearLayout mLinearLayouthistogram;

	private String mCurrentDay;

	// 30天强度数据
	private void showStrengthPeriodData(int periodNum,List<PedometorSummary> fpsList) {
		if (fpsList == null) {
			Logger.i(TAG, "mPedoInfoList is null");
			return;
		}

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		XYSeries seriesStrength2 = new XYSeries("轻微");
		XYSeries seriesStrength3 = new XYSeries("一般");
		XYSeries seriesStrength4 = new XYSeries("剧烈");

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

		for (int i = 0; i < fpsList.size(); i++) {
			PedometorSummary pedo = fpsList.get(i);
			// sum
			int tmpStemNum = Integer.parseInt(pedo.stepNum);
			int tmpDistance = Integer.parseInt(pedo.distance);
			float tmpCal = Float.parseFloat(pedo.cal);
			int tmpYXStepNum = 0;
			if (pedo.yxbs == null || pedo.yxbs.equals(""))
				tmpYXStepNum = 0;
			else
				tmpYXStepNum = Integer.parseInt(pedo.yxbs);

			sumStepNum += tmpStemNum;
			sumYXStepNum += tmpYXStepNum;
			sumDistance += tmpDistance;
			sumCal += (tmpCal);

			sumDurationPeriod += Integer.parseInt(pedo.strength1);
			sumDurationPeriod += Integer.parseInt(pedo.strength2);
			sumDurationPeriod += Integer.parseInt(pedo.strength3);

			// chart display
			double strength2 = Integer.parseInt(pedo.strength1) / 60.0f;
			double strength3 = Integer.parseInt(pedo.strength2) / 60.0f;
			double strength4 = Integer.parseInt(pedo.strength3) / 60.0f;

			seriesStrength4.add(i, strength4);
			seriesStrength3.add(i, strength3 + strength4);
			seriesStrength2.add(i, strength4 + strength3 + strength2);

			// 计算最大值
			double strengthSum = 0;
			strengthSum += strength2;
			strengthSum += strength3;
			strengthSum += strength4;

			if (mStrengthSumMax < strengthSum)
				mStrengthSumMax = strengthSum;

			periodNumFlag[i] = 1;
		}

		// 补充数据
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

		mHistogramChartview = ChartFactory.getBarChartView(this, dataset, getStrengthBarRendererMonth(), BarChart.Type.STACKED);

		mLinearLayouthistogram.removeAllViews();
		mLinearLayouthistogram.addView(mHistogramChartview);

		setPeriodValue((int) sumCal, sumStepNum, sumDistance, sumDurationPeriod, sumYXStepNum);
	}

	private void setPeriodValue(int sumCal, int sumStepNum, int sumDistance, int sumDurationPeriod, int sumYXStepNum) {
		mEnergy.setText(sumCal + "");
		mStepnum.setText(sumYXStepNum + "");
		mDistance.setText(Common.m2km(sumDistance));
		String[] sumDurationPeriodStr = Common.FormatTimeHHmmss(sumDurationPeriod).split(":");
		mDuration.setText(sumDurationPeriodStr[0].trim().replace(", ", "") + ":" + sumDurationPeriodStr[1]);
		mAddupsteps.setText(sumStepNum + "");
	}

	public XYMultipleSeriesRenderer getStrengthBarRendererMonth() {
		XYMultipleSeriesRenderer renderer = getBarRenderer();
		renderer.setShowLegend(false);//底部矩形颜色图显示
		renderer.setInScroll(true);
		renderer.setYAxisMin(0);
		renderer.setXAxisMin(-1);
		renderer.setXAxisMax(30.5);
		renderer.setChartTitle("");
		renderer.setXTitle("");
		renderer.setBarSpacing(1); // bar之间的距离
		renderer.setXLabels(0);// 0为隐藏X刻度 函数用于表示x显示数目

		// 将30天显示为5个
		if (mCurrentDay != null && !mCurrentDay.equals("")) {
			setXTextLabelMonth(renderer);
		}

		float density = Common.getDensity(this);
		if (density <= 1.5f) {
			renderer.setMargins(new int[] { Common.dip2px(this, 5), Common.dip2px(this, 5), Common.dip2px(this, 5), Common.dip2px(this, 5) });
		} else if (density >= 2.0f) {
			renderer.setLegendHeight(80);
			renderer.setMargins(new int[] { Common.dip2px(this, 15), Common.dip2px(this, 5), Common.dip2px(this, 18), Common.dip2px(this, 5) });
		}

		renderer.setYLabelsColor(0, Color.BLACK);
		renderer.setAxisTitleTextSize(20);
		renderer.setXLabelsColor(Color.BLACK);

        SimpleSeriesRenderer r0 = new SimpleSeriesRenderer();
        r0.setColor(getResources().getColor(R.color.color_qingwei));// 148, 195, 205
        SimpleSeriesRenderer r1 = new SimpleSeriesRenderer();
        r1.setColor(getResources().getColor(R.color.color_putong));
        SimpleSeriesRenderer r2 = new SimpleSeriesRenderer();
        r2.setColor(getResources().getColor(R.color.color_julie));

		renderer.addSeriesRenderer(r0);
		renderer.addSeriesRenderer(r1);
		renderer.addSeriesRenderer(r2);

		return renderer;
	}

	public XYMultipleSeriesRenderer getBarRenderer() {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

		renderer.setPanEnabled(false, false);// 锁住上下 左右移动
		renderer.setZoomEnabled(false, false);

		renderer.setLabelsColor(Color.BLACK);// x y 坐标颜色
		renderer.setAxesColor(Color.BLACK); // x y 轴颜色
		renderer.setMarginsColor(Color.argb(0, 0xff, 0, 0)); // 背景透明
		renderer.setMargins(new int[] { Common.dip2px(this, 10), Common.dip2px(this, 5), Common.dip2px(this, 10), Common.dip2px(this, 5) });

		renderer.setAxisTitleTextSize(Common.dip2px(this, 8));
		renderer.setChartTitleTextSize(Common.dip2px(this, 8));
		renderer.setLegendTextSize(Common.dip2px(this, 13));
		renderer.setLabelsTextSize(Common.dip2px(this, 13));

		renderer.setShowGridX(true);
		renderer.setGridColor(Color.GRAY);

		renderer.setYLabels(Common.dip2px(this, 3));

		renderer.setYLabelsAlign(Align.LEFT); // ? y轴标尺向右
		renderer.setShowAxes(false);

		return renderer;
	}

	// 30天步数数据
	private int mMostStepNum;
	private float mMostCal;

	private void showStepNumPeriodData(int periodNum,List<PedometorSummary> fpsList) {
		if (fpsList == null || fpsList.size() == 0) {
			Logger.i(TAG, "mPedoInfoList is null");
			return;
		}

		// 填充数据
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		XYSeries series = new XYSeries("零散步数");
		XYSeries seriesYX = new XYSeries("有效步数");

		mMostStepNum = 0;
		mMostCal = 0;
		int sumStepNum = 0;
		int sumYXStepNum = 0;
		int sumDistance = 0;
		float sumCal = 0;
		int sumDurationPeriod = 0;

		int[] periodNumFlag = new int[periodNum + 1];
		for (int i = 0; i < periodNum + 1; i++) {
			periodNumFlag[i] = 0;
		}

		for (int i = 0; i < fpsList.size(); i++) {
			PedometorSummary pedo = fpsList.get(i);
			int tmpStemNum = Integer.parseInt(pedo.stepNum);
			int tmpDistance = Integer.parseInt(pedo.distance);
			int tmpYXStepNum = 0;
			if (pedo.yxbs == null || pedo.yxbs.equals(""))
				tmpYXStepNum = 0;
			else
				tmpYXStepNum = Integer.parseInt(pedo.yxbs);

			float tmpCal = Float.parseFloat(pedo.cal);
			series.add(i, (double) tmpStemNum);
			seriesYX.add(i, (double) tmpYXStepNum);

			periodNumFlag[i] = 1;
			// sum
			sumYXStepNum += tmpYXStepNum;
			sumStepNum += tmpStemNum;
			sumDistance += tmpDistance;
			sumCal += (tmpCal);

			sumDurationPeriod += Integer.parseInt(pedo.strength1);
			sumDurationPeriod += Integer.parseInt(pedo.strength2);
			sumDurationPeriod += Integer.parseInt(pedo.strength3);

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

		mHistogramChartview = ChartFactory.getBarChartView(this, dataset, getStepNumBarRendererMonth(), BarChart.Type.STACKED);

		mLinearLayouthistogram.removeAllViews();
		mLinearLayouthistogram.addView(mHistogramChartview);

		setPeriodValue((int) sumCal, sumStepNum, sumDistance, sumDurationPeriod, sumYXStepNum);
	}

	private int mPreMonth = 0;

	private LinearLayout mLinearLayoutMinute;

	private LinearLayout mLinearLayoutSteps;

	private void setXTextLabelMonth(XYMultipleSeriesRenderer renderer) {
		long dayOfSeconds = Common.getDateFromYYYYMMDD(mCurrentDay);

		dayOfSeconds = dayOfSeconds - mPreMonth * 30 * Constants.DAY_MILLSECONDS;

		int i = 1;
		while (i < 30) {
			String dayOfWeekStr = Common.getDateAsYYYYMMDD(dayOfSeconds);
			renderer.addXTextLabel(30 - i, dayOfWeekStr.substring(4, 6) + "/" + dayOfWeekStr.substring(6));
			i += 7;
			dayOfSeconds = dayOfSeconds - (1000L * 60 * 60 * 24) * 7;
		}
	}

	public XYMultipleSeriesRenderer getStepNumBarRendererMonth() {
		XYMultipleSeriesRenderer renderer = getBarRenderer();

		renderer.setYAxisMin(0);
		renderer.setShowLegend(false);//底部矩形颜色图显示
		renderer.setXAxisMin(-2);
		renderer.setXAxisMax(30.5);

		renderer.setXLabelsColor(Color.BLACK);// x坐标颜色
		renderer.setYLabelsColor(0, Color.BLACK); // y坐标颜色
		renderer.setLabelsColor(Color.BLACK);// x y title颜色
		renderer.setXLabels(0);// 0为隐藏X刻度 函数用于表示x显示数目

		float density = Common.getDensity(this);
		if (density <= 1.5f) {
			renderer.setMargins(new int[] { Common.dip2px(this, 5), Common.dip2px(this, 5), Common.dip2px(this, 5), Common.dip2px(this, 5) });
		} else if (density >= 2.0f) {
			renderer.setLegendHeight(80);
			renderer.setMargins(new int[] { Common.dip2px(this, 15), Common.dip2px(this, 5), Common.dip2px(this, 18), Common.dip2px(this, 5) });
		}

		renderer.setAxesColor(Color.BLACK); // x y 轴颜色

		renderer.setChartTitle("");
		renderer.setXTitle("");

		renderer.setBarSpacing(1); // bar之间的距离

		// 将30天显示为5个
		if (mCurrentDay != null && !mCurrentDay.equals("")) {
			setXTextLabelMonth(renderer);
		}

        SimpleSeriesRenderer r0 = new SimpleSeriesRenderer();
        r0.setColor(getResources().getColor(R.color.color_putong));
        SimpleSeriesRenderer r1 = new SimpleSeriesRenderer();
        r1.setColor(getResources().getColor(R.color.color_julie));

		renderer.addSeriesRenderer(r0);
		renderer.addSeriesRenderer(r1);

		return renderer;
	}
	
	@Override
	protected void onStop() {
		this.finish();
		super.onStop();
	}

}
