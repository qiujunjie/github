package cmcc.mhealth.activity;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import cmcc.mhealth.R;
import cmcc.mhealth.basic.BaseActivity;
import cmcc.mhealth.bean.DataDetailPedo;
import cmcc.mhealth.bean.DataPedometor;
import cmcc.mhealth.bean.PedoDetailInfo;
import cmcc.mhealth.bean.PedometorInfo;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Constants;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.db.MHealthProviderMetaData;
import cmcc.mhealth.net.DataSyn;

public class HistoryPedometorDetailActivity extends BaseActivity {
	private static String TAG = "PedoDetailActivity";

	private static final int STRENGTH = 2;
	private static final int STEPNUM = 1;

	private LinearLayout mLinearLayoutHistogram = null;
	private GraphicalView mHistogramChartview = null;
	private TextView mTextViewDate = null;
	private TextView mTextViewDateTime = null;
	private TextView mTextViewTitle = null;
	// private TextView mTextViewPlan = null;
	// private TextView mTextViewAdvice = null;

	private ImageButton mImageButtonBack = null;
	private RadioGroup mRadioGroupState;

	private PedoDetailInfo mPedoDetail;
	private DataPedometor mPedoInfo;

	private int mStepNumSumMax;
	private int mStrengthSumMax;

	private String mSearchDate;

	private int mState;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ���ô����ޱ���
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_pedo_detail);

		mSearchDate = getIntent().getStringExtra("searchDate");

		initialView();
		queryData(mSearchDate);
		showDayData(STEPNUM);
	}

	@SuppressWarnings("deprecation")
	public void initialView() {
		// title
		mTextViewTitle = (TextView) findViewById(R.id.textView_title);
		mTextViewTitle.setText(R.string.pedo_detail_title);
		mImageButtonBack = (ImageButton) findViewById(R.id.button_input_bg_back);
		mImageButtonBack.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_button_back));
		mImageButtonBack.setVisibility(View.VISIBLE);
		mImageButtonBack.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				HistoryPedometorDetailActivity.this.finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
			}
		});

		mLinearLayoutHistogram = (LinearLayout) findViewById(R.id.linearLayout_day_detail);
		mTextViewDate = (TextView) findViewById(R.id.textView_date);
		mTextViewDateTime = (TextView) findViewById(R.id.textView_daytime);
		// mTextViewPlan = (TextView) findViewById(R.id.textView_plan);
		// mTextViewAdvice = (TextView) findViewById(R.id.textView_advice);
		// radio button
		mRadioGroupState = (RadioGroup) findViewById(R.id.radioGroup_state);
		mRadioGroupState
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.radio_stepnum:
							radioStepNumChanged();
							break;
						case R.id.radio_strength:
							radioStrengthChanged();
							break;
						default:
							break;
						}
					}
				});
		((RadioButton) findViewById(R.id.radio_stepnum)).setChecked(true);
	}

	private void radioStepNumChanged() {
		loadView(STEPNUM);
	}

	private void radioStrengthChanged() {
		loadView(STRENGTH);
	}

	public XYMultipleSeriesRenderer getStrengthBarRenderer() {
		XYMultipleSeriesRenderer renderer = getBarRenderer();

		renderer.setYAxisMin(0);
		// renderer.setYAxisMax(mStrengthSumMax + 10);
		renderer.setXAxisMin(0);
		renderer.setXAxisMax(24.5);

		renderer.setChartTitle("");
		renderer.setXTitle("");

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

		renderer.setYLabelsAlign(Align.LEFT); // y��������
		renderer.setYTitle("");

		renderer.setAxisTitleTextSize(20);

		renderer.setXLabelsColor(Color.BLACK);

		SimpleSeriesRenderer r0 = new SimpleSeriesRenderer();
		r0.setColor(Color.rgb(244, 117, 96));// 148, 195, 205
		SimpleSeriesRenderer r1 = new SimpleSeriesRenderer();
		r1.setColor(Color.rgb(143, 200, 35));
		SimpleSeriesRenderer r2 = new SimpleSeriesRenderer();
		r2.setColor(Color.rgb(46, 176, 221));

		renderer.addSeriesRenderer(r2);
		renderer.addSeriesRenderer(r1);
		renderer.addSeriesRenderer(r0);

		return renderer;
	}

	public XYMultipleSeriesRenderer getBarDemoRenderer() {
		XYMultipleSeriesRenderer renderer = getBarRenderer();

		renderer.setYAxisMin(0);
		renderer.setYAxisMax(mStepNumSumMax + 10);
		renderer.setXAxisMin(0);
		renderer.setXAxisMax(24.5);

		renderer.setChartTitle("");
		renderer.setXTitle("");
		renderer.setYTitle("");

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

		renderer.setYLabelsAlign(Align.LEFT); // y��������
		renderer.setXLabelsColor(Color.BLACK);

		// SimpleSeriesRenderer r0 = new SimpleSeriesRenderer();
		// r0.setColor(Color.rgb(208, 78, 0));
		// r0.setColor(Color.rgb(254, 142, 0));
		// r0.setGradientStart(0, Color.rgb(208, 78, 0));
		// r0.setGradientStop(mStepNumSumMax, Color.rgb(254, 142, 0));

		// renderer.addSeriesRenderer(r0);

		SimpleSeriesRenderer r0 = new SimpleSeriesRenderer();
		r0.setColor(Color.rgb(143, 200, 35));//(46, 176, 221) 148, 195, 205
		SimpleSeriesRenderer r1 = new SimpleSeriesRenderer();
		r1.setColor(Color.rgb(239, 146, 50));

		renderer.addSeriesRenderer(r0);
		renderer.addSeriesRenderer(r1);

		return renderer;
	}

	public void queryData(String searchDate) {
		mPedoDetail = MHealthProviderMetaData.GetMHealthProvider(this)
				.getPedoDetailByDay(searchDate);
		mPedoInfo = MHealthProviderMetaData.GetMHealthProvider(this)
				.getPedometerByDate(searchDate);
	}

	public void showDayData(int state) {
		mState = state;
		if (mPedoDetail == null || mPedoInfo == null) {
			showProgressDialog("���Ե�...", HistoryPedometorDetailActivity.this);
			new Thread() {
				public void run() {
					SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
					String phonenum = info.getString(SharedPreferredKey.PHONENUM, null);
					String password = info.getString(SharedPreferredKey.PASSWORD, null);
					int result = DataSyn.getInstance().getPedoInfoDetail(
							phonenum, password, "00", "23", mSearchDate,
							mPedoDetail);
					PedometorInfo pedometorInfo = new PedometorInfo();
					int res = DataSyn.getInstance().getPedoInfo(phonenum,
							password, mSearchDate, pedometorInfo);
					dismiss();
					if (result == 0 && res == 0) {
						if (mPedoDetail == null
								|| pedometorInfo.datavalue == null
								|| pedometorInfo.datavalue.size() == 0) {
							messagesManager(Constants.MESSAGE_NO_PEDO_DETAIL);
							return;
						}

						mPedoInfo = pedometorInfo.datavalue.get(0);
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								loadView(mState);
							}
						});
					} else {
						Logger.i(TAG, "m_pedoDetail is null");
						return;
					}
				};
			}.start();
		} else {
			loadView(mState);
		}
	}

	private void loadView(int state) {
		if (mPedoDetail == null || mPedoInfo == null)
			return;

		mLinearLayoutHistogram.removeAllViews();
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

		String stateType = "";
		String stateContent = "";

		if (state == STEPNUM) {
			// �������
			XYSeries series = new XYSeries("��ɢ����");
			XYSeries seriesYX = new XYSeries("��Ч����");

			mStepNumSumMax = 0;
			int[] hour24 = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0 };
			int[] mStepNumSumMaxHour = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			int stepNumDaySum = 0; // ���˶��ܲ���
			for (int i = 0; i < mPedoDetail.datavalue.size(); i++) {
				DataDetailPedo data = mPedoDetail.datavalue.get(i);
				int stepNumSum = data.getStepNumSum();
				int stepNumYXSum = data.getSNYXP5Sum();

				int startTime = Integer.parseInt(data.start_time);

				//��ֵ���ھ�ֵʱ������ʾ
				if (mStepNumSumMaxHour[startTime] < stepNumSum)
				{
					mStepNumSumMaxHour[startTime] = stepNumSum;
				series.add(startTime + 0.5, stepNumSum);
				seriesYX.add(startTime + 0.5, stepNumYXSum);
				hour24[startTime] = 1;
				}
				if (mStepNumSumMax < stepNumSum)
					mStepNumSumMax = stepNumSum;
//				if (hour24[startTime] == 0) {
//					stepNumDaySum += stepNumSum;
//					hour24[startTime] = 1;
//				}
			}
			for (int i = 0; i < mStepNumSumMaxHour.length; i++) {
				stepNumDaySum += mStepNumSumMaxHour[i];
			}
			for (int i = 0; i < hour24.length; i++) {
				if (hour24[i] == 0) {
					series.add(i + 0.5, 0);
					seriesYX.add(i + 0.5, 0);
				}
			}

			dataset.addSeries(series);
			dataset.addSeries(seriesYX);

			mHistogramChartview = ChartFactory.getBarChartView(this, dataset,
					getBarDemoRenderer(), BarChart.Type.STACKED);

			// ���˶��ܲ���
			stateType = "��������";
			stateContent = String.valueOf(stepNumDaySum);

			stateContent = mPedoInfo.data.stepNum;
		} else if (state == STRENGTH) {

			XYSeries seriesStrength2 = new XYSeries("��΢");
			XYSeries seriesStrength3 = new XYSeries("һ��");
			XYSeries seriesStrength4 = new XYSeries("����");

			int[] mStrengthSumMaxHour = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			int strengthDaySum = 0; // ���˶���ʱ��
			// ��������
			int hour24[] = new int[24];
			for (int i = 0; i < 24; i++) {
				hour24[i] = -1;
			}

			for (int i = 0; i < mPedoDetail.datavalue.size(); i++) {
				DataDetailPedo data = mPedoDetail.datavalue.get(i);
				int startTime = Integer.parseInt(data.start_time);
				
				//�����ظ����� ʹ�ø�Сʱ���������
				int nStengthSum = data.getStrengthSum(2) + data.getStrengthSum(3) + data.getStrengthSum(4);			
				if(mStrengthSumMaxHour[startTime] < nStengthSum){
					mStrengthSumMaxHour[startTime] = nStengthSum;
					hour24[startTime] = i;
				}
			}

			mStrengthSumMax = 0;

			for (int i = 0; i < 24; i++) {
				double strength2 = 0;
				double strength3 = 0;
				double strength4 = 0;

				if (hour24[i] != -1) {
					DataDetailPedo data = mPedoDetail.datavalue.get(hour24[i]);
					strength2 = data.getStrengthSum(2) / 60.0f;
					strength3 = data.getStrengthSum(3) / 60.0f;
					strength4 = data.getStrengthSum(4) / 60.0f;

					strengthDaySum += (strength2 + strength3 + strength4);
				}

				seriesStrength4.add(i + 0.5, strength4);
				seriesStrength3.add(i + 0.5, strength3 + strength4);
				seriesStrength2.add(i + 0.5, strength4 + strength3 + strength2);

				// �������ֵ
				int strengthSum = 0;
				strengthSum += strength2;
				strengthSum += strength3;
				strengthSum += strength4;

				if (mStrengthSumMax < strengthSum)
					mStrengthSumMax = strengthSum;
			}
			dataset.addSeries(seriesStrength2);
			dataset.addSeries(seriesStrength3);
			dataset.addSeries(seriesStrength4);

			mHistogramChartview = ChartFactory.getBarChartView(this, dataset,
					getStrengthBarRenderer(), BarChart.Type.STACKED);

			stateType = "��ʱ����";
			// ���˶���ʱ��
			stateContent = Common.FormatTimeHHmmss(strengthDaySum * 60);

			strengthDaySum = Integer.valueOf(mPedoInfo.data.strength2)
					+ Integer.valueOf(mPedoInfo.data.strength3)
					+ Integer.valueOf(mPedoInfo.data.strength4);
			stateContent = Common.FormatTimeHHmmss(strengthDaySum);
		}

		mLinearLayoutHistogram.addView(mHistogramChartview);

		String year = mPedoDetail.date.substring(0, 4);
		String month = mPedoDetail.date.substring(4, 6);
		String day = mPedoDetail.date.substring(6, 8);

		mTextViewDate.setText(year + "-" + month + "-" + day + stateType);
		mTextViewDateTime.setText(stateContent);
	}
}
