package cmcc.mhealth.slidingcontrol;

import java.util.Date;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import cmcc.mhealth.R;
import cmcc.mhealth.activity.RaceDetialActivity;
import cmcc.mhealth.adapter.RaceAdapter;
import cmcc.mhealth.bean.RaceData;
import cmcc.mhealth.bean.RaceInfo;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.db.MHealthProviderMetaData;
import cmcc.mhealth.net.DataSyn;
import cmcc.mhealth.view.XListView;
import cmcc.mhealth.view.XListView.IXListViewListener;

/**
 * 竞赛Fragment子Fragment，处于三viewpager中<br>
 * 
 * @author zy
 * 
 */
public class RaceChildFragment extends Fragment implements OnItemClickListener, IXListViewListener {
	private static final String TAG = "RaceChildFragment";
	// =====基本参数
	private Activity mActivity;
	private String mPhoneNum;
	private String mPassword;
	private int mState;
	private int mRacenum;
	private int mStartID;
	private RaceAdapter mRaceAdapter;
	private RaceInfo mRaceInfo;
	private boolean isLoading;
	// =====Views
	private XListView mMainListView;

	public static final RaceChildFragment newInstance(String phonenum, String password, int state, int racenum) {
		RaceChildFragment rcf = new RaceChildFragment();
		Bundle data = new Bundle();
		data.putString(SharedPreferredKey.PHONENUM, phonenum);
		data.putString(SharedPreferredKey.PASSWORD, password);
		data.putInt("racenum", racenum);
		data.putInt("state", state);
		rcf.setArguments(data);
		return rcf;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.listview_race, container, false);
		getArgs();
		initView(view);
		handler.sendEmptyMessage(RACE_SQL_LOAD);
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

	private void getArgs() {
		mActivity = getActivity();
		mStartID = 0;
		mPhoneNum = getArguments().getString(SharedPreferredKey.PHONENUM);
		mPassword = getArguments().getString(SharedPreferredKey.PASSWORD);
		mRacenum = getArguments().getInt("racenum");
		mState = getArguments().getInt("state");
	}

	// 初始化
	private void initView(View view) {
		mRaceInfo = new RaceInfo();
		mMainListView = (XListView) view.findViewById(R.id.lr_listview_race);
		mMainListView.setXListViewListener(this);
	}
	
	public void loadDate(){
		loadDataFromNet(GET_STYLE_NEW);
	}
	// 从网络拿数据
	private void loadDataFromNet(final int getstyle) {
		new Thread() {
			public void run() {
				RaceInfo ri = new RaceInfo();
				int suc = -1;
				if (getstyle == GET_STYLE_NEW) {
					suc = DataSyn.getInstance().getRaceList(mPhoneNum, mPassword, mState, mRacenum, 0, ri);
				} else {
					suc = DataSyn.getInstance().getRaceList(mPhoneNum, mPassword, mState, mRacenum, mStartID, ri);
				}
				switch (suc) {
				case 0:
					Message msg = Message.obtain();
					msg.what = RACE_LOAD_SUCCESS;
					Bundle data = new Bundle();
					data.putParcelable("raceinfo", ri);
					data.putInt("getstyle", getstyle);
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

	private static final int RACE_SQL_LOAD = 0;// 初始本地快速载入
	private static final int RACE_LOAD_SUCCESS = 2;// 
	private static final int GET_STYLE_NEW = 3;//
	private static final int RACE_SQL_APPEND = 4;//
	private static final int NET_FAIL = 5;// 
	private static final int GET_STYLE_APPEND = 6;// 
	private Handler handler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			Bundle data = msg.getData();
			RaceInfo ri = null;
			MHealthProviderMetaData mhProvider = MHealthProviderMetaData.GetMHealthProvider(mActivity);
			switch (msg.what) {
			// 网络问题
			case NET_FAIL:
				BaseToast("请确认网络畅通", 5);
				isLoading = false;
				resetXListView();
				break;
			// 从本地分页读取
			case RACE_SQL_APPEND:
				ri = mhProvider.getRaces(mRacenum, mStartID, mState);
				if (ri.racelistinfo == null || ri.racelistinfo.size() == 0) {
					loadDataFromNet(GET_STYLE_APPEND);
				} else {
					notifyAdapter(ri, GET_STYLE_APPEND);
				}
				break;
			// 从本地数据库首次读取
			case RACE_SQL_LOAD:
				ri = mhProvider.getRaces(mRacenum, 0, mState);
				if (ri.racelistinfo == null || ri.racelistinfo.size() == 0) {
					mMainListView.startLoading(Common.getDensity(mActivity) * 60);
				} else {
					notifyAdapter(ri, GET_STYLE_NEW);
				}
				break;
			// 网上读取
			case RACE_LOAD_SUCCESS:
				ri = data.getParcelable("raceinfo");
				int getstyle = data.getInt("getstyle");
				if (ri != null && ri.racelistinfo != null && ri.racelistinfo.size() > 0) {
					switch (getstyle) {
					case GET_STYLE_NEW:
						mhProvider.DeleteRaceData(mState);
						break;
					}
					mhProvider.insertRaceValue(ri, mState);
				}
				notifyAdapter(ri, getstyle);
				break;
			}
		}
	};

	private void resetXListView() {
		Logger.d("linetest", RaceChildFragment.this.toString() + "resetXListView()");
		mMainListView.stopRefresh();
		mMainListView.stopLoadMore();
		mMainListView.setRefreshTime(Common.getDateAsM_d(new Date().getTime()));
	}

	// listview点击事件
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(position < 1) return;
		RaceData rd = (RaceData) ((Map<String, Object>) view.getTag()).get("racedata");
		if (rd == null) {
			BaseToast("获取详情信息失败", 5);
			return;
		}
		Intent intent = new Intent(mActivity, RaceDetialActivity.class);
		intent.putExtra("racedata", rd);
		intent.putExtra("sampletitle", "竞赛详情");
		intent.putExtra("position", position);
		startActivityForResult(intent, RaceFragment.SQL_RELOAD);
		mActivity.overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == RaceFragment.SQL_RELOAD){
			if(resultCode == Activity.RESULT_OK){
				int position = (Integer) data.getExtras().get("position");
				String membernum = (String) data.getExtras().get("membernum");
				mRaceInfo.racelistinfo.get(position - 1).membernum = membernum;
				setAdapter(mRaceInfo);
			}
		}
	}

	// 创建adapter
	private void setAdapter(RaceInfo ri) {
		mRaceAdapter = new RaceAdapter(mActivity, ri);
		mMainListView.setAdapter(mRaceAdapter);
		mMainListView.setOnItemClickListener(this);
		mMainListView.setPullLoadEnable(ri.racelistinfo.size() >= 10);
	}


	// 重刷adapter
	private void notifyAdapter(RaceInfo ri, int getstyle) {
		resetXListView();
		if (!(TextUtils.isEmpty(ri.lastid) || "null".equals(ri.lastid))) {
			int lastid = Integer.parseInt(ri.lastid);
			mStartID = lastid;
			if (getstyle == GET_STYLE_NEW) {
				mRaceInfo = new RaceInfo();
				mRaceInfo.racelistinfo = ri.racelistinfo;
			} else {
				mRaceInfo.racelistinfo.addAll(ri.racelistinfo);
			}
			mMainListView.setPullLoadEnable(mRaceInfo.racelistinfo.size() >= 10);
		} else {
			BaseToast("没有更多竞赛了", 5);
		}
		if (mRaceAdapter != null) {
			mRaceAdapter.setRi(mRaceInfo);
			mRaceAdapter.notifyDataSetChanged();
		} else {
			setAdapter(mRaceInfo);
		}
		isLoading = false;
	}

	@Override
	public void onStop() {
		if (mRaceAdapter != null)
			mRaceAdapter = null;
		super.onStop();
	}

	@Override
	public void onRefresh() {
		if (!isLoading) {
			isLoading = true;
			loadDataFromNet(GET_STYLE_NEW);
		}
	}

	@Override
	public void onLoadMore() {
		if (!isLoading) {
			isLoading = true;
			handler.sendEmptyMessage(RACE_SQL_APPEND);
		}
	}
	
	private Toast mToast = null;
	protected void BaseToast(String msg) {
		BaseToast(msg, 0);
	}
	protected void BaseToast(String msg, int duration) {
		if (mToast == null) {
			mToast = Toast.makeText(mActivity, msg, duration);
		} else {
			mToast.setText(msg);
		}
		mToast.show();
	}

}
