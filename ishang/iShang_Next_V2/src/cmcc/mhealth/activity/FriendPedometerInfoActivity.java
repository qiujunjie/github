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
	
	private FriendPedometorSummary fpsReqData;// ���Ѽ�����

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_pedo_brief);
		initview();
		initDatas();
		showDatas();
	}
	//չʾ����
	private void showDatas() {
		List<PedometorSummary> friendPSList = fpsReqData.friendsinfo;
		if (friendPSList.size() > 0) {
			mCurrentDay = friendPSList.get(friendPSList.size() - 1).date;
			initHistogram(friendPSList);
		}
	}

	private RadioGroup mRadioGroupDayDetail;

	// ��ʾͼ��
	private void initHistogram(final List<PedometorSummary> friendPSList) {
		mLinearLayouthistogram = (LinearLayout) findViewById(R.id.afpb_linearLayout_day_detail);
		showStrengthPeriodData(friendPSList.size(), friendPSList);
		// ���� ����ǿ���л���ť
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

	// ��ʼ��ҳ��
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
		BaseBackKey(membername + "���˶���", this);
	}

	private double mStrengthSumMax;
	private GraphicalView mHistogramChartview;
	private LinearLayout mLinearLayouthistogram;

	private String mCurrentDay;

	// 30��ǿ������
	private void showStrengthPeriodData(int periodNum,List<PedometorSummary> fpsList) {
		if (fpsList == null) {
			Logger.i(TAG, "mPedoInfoList is null");
			return;
		}

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		XYSeries seriesStrength2 = new XYSeries("��΢");
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

			// �������ֵ
			double strengthSum = 0;
			strengthSum += strength2;
			strengthSum += strength3;
			strengthSum += strength4;

			if (mStrengthSumMax < strengthSum)
				mStrengthSumMax = strengthSum;

			periodNumFlag[i] = 1;
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
		renderer.setShowLegend(false);//�ײ�������ɫͼ��ʾ
		renderer.setInScroll(true);
		renderer.setYAxisMin(0);
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

		renderer.setPanEnabled(false, false);// ��ס���� �����ƶ�
		renderer.setZoomEnabled(false, false);

		renderer.setLabelsColor(Color.BLACK);// x y ������ɫ
		renderer.setAxesColor(Color.BLACK); // x y ����ɫ
		renderer.setMarginsColor(Color.argb(0, 0xff, 0, 0)); // ����͸��
		renderer.setMargins(new int[] { Common.dip2px(this, 10), Common.dip2px(this, 5), Common.dip2px(this, 10), Common.dip2px(this, 5) });

		renderer.setAxisTitleTextSize(Common.dip2px(this, 8));
		renderer.setChartTitleTextSize(Common.dip2px(this, 8));
		renderer.setLegendTextSize(Common.dip2px(this, 13));
		renderer.setLabelsTextSize(Common.dip2px(this, 13));

		renderer.setShowGridX(true);
		renderer.setGridColor(Color.GRAY);

		renderer.setYLabels(Common.dip2px(this, 3));

		renderer.setYLabelsAlign(Align.LEFT); // ? y��������
		renderer.setShowAxes(false);

		return renderer;
	}

	// 30�첽������
	private int mMostStepNum;
	private float mMostCal;

	private void showStepNumPeriodData(int periodNum,List<PedometorSummary> fpsList) {
		if (fpsList == null || fpsList.size() == 0) {
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
		renderer.setShowLegend(false);//�ײ�������ɫͼ��ʾ
		renderer.setXAxisMin(-2);
		renderer.setXAxisMax(30.5);

		renderer.setXLabelsColor(Color.BLACK);// x������ɫ
		renderer.setYLabelsColor(0, Color.BLACK); // y������ɫ
		renderer.setLabelsColor(Color.BLACK);// x y title��ɫ
		renderer.setXLabels(0);// 0Ϊ����X�̶� �������ڱ�ʾx��ʾ��Ŀ

		float density = Common.getDensity(this);
		if (density <= 1.5f) {
			renderer.setMargins(new int[] { Common.dip2px(this, 5), Common.dip2px(this, 5), Common.dip2px(this, 5), Common.dip2px(this, 5) });
		} else if (density >= 2.0f) {
			renderer.setLegendHeight(80);
			renderer.setMargins(new int[] { Common.dip2px(this, 15), Common.dip2px(this, 5), Common.dip2px(this, 18), Common.dip2px(this, 5) });
		}

		renderer.setAxesColor(Color.BLACK); // x y ����ɫ

		renderer.setChartTitle("");
		renderer.setXTitle("");

		renderer.setBarSpacing(1); // bar֮��ľ���

		// ��30����ʾΪ5��
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
