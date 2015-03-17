package cmcc.mhealth.activity;

import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cmcc.mhealth.R;
import cmcc.mhealth.adapter.RaceAdapter;
import cmcc.mhealth.basic.SampleActivity;
import cmcc.mhealth.bean.RaceData;
import cmcc.mhealth.bean.RaceInfo;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.net.DataSyn;

public class RaceSearchActivity extends SampleActivity implements OnItemClickListener {
	private RelativeLayout mSearchButton;
	private TextView mSearchButtonBackground;
	private EditText mSearchInput;
	private RaceAdapter mRaceAdapter;
	private ListView mMainListView;
	private LinearLayout mProgress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_race_search);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initView();
	}
	//初始化
	private void initView() {
		findViews();
		setViews();
		setClickers();
	}


	//初始化页面
	private void findViews() {
		mSearchButton = (RelativeLayout) findViewById(R.id.imageButton_title_add);
		mSearchButtonBackground = (TextView) findViewById(R.id.textview_title_add);
		mSearchInput = (EditText) findViewById(R.id.ars_keyword);
		mMainListView = (ListView) findViewById(R.id.ars_searchedracelist);
		mProgress = (LinearLayout) findViewById(R.id.ars_linearlayout_progress);
	}
	//设置页面
	private void setViews() {
		mSearchButton.setVisibility(View.VISIBLE);
		mSearchButtonBackground.setBackgroundResource(R.drawable.white_search);
		mProgress.setVisibility(View.GONE);
	}
	//设置监听
	private void setClickers() {
		mSearchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String keyword = mSearchInput.getText().toString();
				if(TextUtils.isEmpty(keyword)){
					BaseToast("请输入关键字");
					return;
				}
				mProgress.setVisibility(View.VISIBLE);
				int type = getIntent().getExtras().getInt("searchtype");
				startSearch(keyword, type);
			}

		});
		
	}
	//开始搜索
	private void startSearch(final String keyword, final int type) {
		new Thread(){
			public void run() {
				RaceInfo raceinfo = new RaceInfo();
				switch (DataSyn.getInstance().getSearchedRace(mPhoneNum, mPassword, keyword, type, raceinfo)) {
				case 0:
					if(raceinfo.racelistinfo.size() == 0){
						handler.sendEmptyMessage(NOT_FOUND);
						return;
					}
					Message msg = Message.obtain();
					Bundle data = new Bundle();
					data.putParcelable("raceinfo", raceinfo);
					msg.setData(data);
					msg.what = SEARCH_SUCCESS;
					handler.sendMessage(msg);
					break;
				default:
					handler.sendEmptyMessage(SEARCH_FAIL);
					break;
				}
			};
		}.start();
		
	}
	private static final int SEARCH_SUCCESS = 0;
	private static final int SEARCH_FAIL = 1;
	private static final int NOT_FOUND = 2;
	private Handler handler = new Handler(){
		@Override
		public void dispatchMessage(Message msg) {
			Bundle data = msg.getData();
			if(mProgress != null)mProgress.setVisibility(View.GONE);
			switch (msg.what) {
			case SEARCH_SUCCESS:
				RaceInfo raceinfo = data.getParcelable("raceinfo");
				showResult(raceinfo);
				Common.collapseSoftInputMethod(RaceSearchActivity.this, mSearchInput);
				break;
			case NOT_FOUND:
				BaseToast("未找到任何竞赛哦", 5);
				break;
			case SEARCH_FAIL:
				BaseToast("请确认网络状态后重试", 5);
				break;
			}
		}

	};
	//显示列表数据
	private void showResult(RaceInfo ri) {
		mRaceAdapter = new RaceAdapter(this, ri);
		mMainListView.setAdapter(mRaceAdapter);
		mMainListView.setOnItemClickListener(this);
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		RaceData rd = (RaceData) ((Map<String, Object>) view.getTag()).get("racedata");
		if(rd == null){
			BaseToast("获取详情信息失败", 5);
			return;
		}
		Intent intent = new Intent(this, RaceDetialActivity.class);
		intent.putExtra("racedata", rd);
		intent.putExtra("sampletitle", "竞赛详情");
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
	}
}
