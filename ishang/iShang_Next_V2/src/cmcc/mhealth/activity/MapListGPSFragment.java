package cmcc.mhealth.activity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.RelativeLayout;
import cmcc.mhealth.R;
import cmcc.mhealth.adapter.GPSXListViewAdapter;
import cmcc.mhealth.basic.BaseFragment;
import cmcc.mhealth.bean.CommonBottomMenuItem;
import cmcc.mhealth.bean.DetailGPSData;
import cmcc.mhealth.bean.ListGPSData;
import cmcc.mhealth.bean.GPSListBean;
import cmcc.mhealth.bean.GPSListInfo;
import cmcc.mhealth.bean.GpsInfoDetail;
import cmcc.mhealth.common.AlertDialogs;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Constants;
import cmcc.mhealth.common.MenuDialog;
import cmcc.mhealth.common.PreferencesUtils;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.common.ShowProgressDialog;
import cmcc.mhealth.common.MenuDialog.onClickedItemPosition;
import cmcc.mhealth.common.UploadUtil;
import cmcc.mhealth.db.MHealthProviderMetaData;
import cmcc.mhealth.net.DataSyn;
import cmcc.mhealth.net.NetworkTool;
import cmcc.mhealth.slidingcontrol.MainCenterActivity;
import cmcc.mhealth.slidingcontrol.MapFragment;

public class MapListGPSFragment extends BaseFragment implements OnItemClickListener, OnClickListener {
	private static final int NODATA = 1001;
	protected static final int SUCCESS = 0;
	private ListView xListView;
	// private List<List<GPSInfo>> mListAllGpsInfo;
	private GPSXListViewAdapter myAdapter;
	private List<GPSListInfo> mListGPSInfo;
	private View mBack;
	private RelativeLayout mRLayoutGetHistoryData;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_map_history_orbit, container, false);
		super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cmcc.mhealth.basic.BaseFragment#findViews()
	 */
	@Override
	public void findViews() {
		mBack = findView(R.id.button_input_bg_back);
		mBack.setVisibility(View.VISIBLE);
		mBack.setOnClickListener(new backClick(new MapFragment()));
		mRLayoutGetHistoryData = findView(R.id.imageButton_title_add);
		mRLayoutGetHistoryData.setVisibility(View.VISIBLE);
		mRLayoutGetHistoryData.findViewById(R.id.textview_title_add).setBackgroundResource(R.drawable.top_menu_more);
		mRLayoutGetHistoryData.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.findViewById(R.id.textview_title_add).setBackgroundResource(R.drawable.top_menu_more_orange);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					v.findViewById(R.id.textview_title_add).setBackgroundResource(R.drawable.top_menu_more);
				}
				return false;
			}
		});
		
		mImageButtonBack.setVisibility(View.VISIBLE);
		mImageButtonBack.setBackgroundResource(R.drawable.my_button_back);
		mTextViewTitle.setText("��ʷ�켣");
		xListView = (ListView) findView(R.id.map_history_activity);
		
		xListView.setOnItemLongClickListener(new myOnItemlongClickListener());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		 xListView.startLoading(Common.getDensity(this) * 60);
//		 xListView.stopRefresh();
//		 xListView.stopLoadMore();
//		 xListView.setRefreshTime(Common.getDateAsM_d(new Date().getTime()));
	}

	private List<GPSListInfo> getStarttimeList() {
		return MHealthProviderMetaData.GetMHealthProvider(mActivity).getAllData();
	}

//	@Override
//	public void onRefresh() {
//		resetXListView();
//	}

//	@Override
//	public void onLoadMore() {
//		//����
//	}
	
//	private void resetXListView() {
//		xListView.stopRefresh();
//		xListView.stopLoadMore();
//		xListView.setRefreshTime(Common.getDateAsM_d(new Date().getTime()));
//	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		MainCenterActivity fca = (MainCenterActivity) getActivity();
//		PreferencesUtils.putString(mActivity, SharedPreferredKey.HISTORY_START_TIME, mListGPSInfo.get(position-1));
		if(buildData.get(position).time != null)
			return;
		fca.switchFragment((BaseFragment) fca.getContent(), (BaseFragment) new MapViewOrbitFragment(buildData.get(position).listInfo));
	}

	@Override
	public void onDestroy() {
//		PreferencesUtils.removeSp(mActivity, SharedPreferredKey.HISTORY_START_TIME);
		super.onDestroy();
	}

	@Override
	public void clickListner() {
//		xListView.setXListViewListener(this);
		mRLayoutGetHistoryData.setOnClickListener(this);
	}

	@Override
	public void loadLogic() {
		mListGPSInfo = new ArrayList<GPSListInfo>();
//		uploadData();//�ϴ�����
		builderData();
	}

	private void builderData() {
		mListGPSInfo = getStarttimeList();//get date
		buildData = new ArrayList<GPSListBean>();
		if(mListGPSInfo.size() != 0)
			fitData(buildData);
		myAdapter = new GPSXListViewAdapter(buildData, mActivity);
		xListView.setAdapter(myAdapter);
		xListView.setOnItemClickListener(this);
		myAdapter.notifyDataSetChanged();
	}
	private void fitData(List<GPSListBean> gpsListInfos) {
        //TODO
        if(mListGPSInfo.size() == 1){
            String date = mListGPSInfo.get(0).getStarttime().toString();
            GPSListBean listBean = new GPSListBean();
            listBean.time = date.split(" ")[0];
            gpsListInfos.add(listBean);
            listBean = new GPSListBean();
            listBean.listInfo = mListGPSInfo.get(0);
            gpsListInfos.add(listBean);
        }else{
            for (int i = 0; i < mListGPSInfo.size()-1; i++) {
                GPSListBean listBean = new GPSListBean();
                String date = mListGPSInfo.get(i).getStarttime().toString();
                String date1 = mListGPSInfo.get(i+1).getStarttime().toString();
                if(i==0){
                    listBean.time = date.split(" ")[0];
                    gpsListInfos.add(listBean);
                    listBean = new GPSListBean();
                    listBean.listInfo = mListGPSInfo.get(0);
                    gpsListInfos.add(listBean);
                }
                if(!date.split(" ")[0].equals(date1.split(" ")[0])){
                    listBean = new GPSListBean();
                    listBean.time = date1.split(" ")[0];
                    gpsListInfos.add(listBean);
                    listBean = new GPSListBean();
                    listBean.listInfo = mListGPSInfo.get(i+1);
                }else{
                    listBean = new GPSListBean();
                    listBean.listInfo = mListGPSInfo.get(i+1);
                }
                gpsListInfos.add(listBean);
            }
        }
        
    }
//	private void fitData(List<GPSListBean> gpsListInfos) {
//		//TODO
//		for (int i = 0; i < mListGPSInfo.size()-1; i++) {
//			GPSListBean listBean = new GPSListBean();
//			String date = mListGPSInfo.get(i).getStarttime().toString();
//			String date1 = mListGPSInfo.get(i+1).getStarttime().toString();
//			if(i==0){
//				listBean.time = date.split(" ")[0];
//				gpsListInfos.add(listBean);
//				listBean = new GPSListBean();
//				listBean.listInfo = mListGPSInfo.get(0);
//				gpsListInfos.add(listBean);
//			}
//			if(!date.split(" ")[0].equals(date1.split(" ")[0])){
//				listBean = new GPSListBean();
//				listBean.time = date1.split(" ")[0];
//				gpsListInfos.add(listBean);
//				listBean = new GPSListBean();
//				listBean.listInfo = mListGPSInfo.get(i+1);
//			}else{
//				listBean = new GPSListBean();
//				listBean.listInfo = mListGPSInfo.get(i+1);
//			}
//			gpsListInfos.add(listBean);
//		}
//	}

	class myOnItemlongClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
			AlertDialogs.showOKorNODialog("ȷ��ɾ��?", mActivity, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					//ɾ��
					String starttime ;
					toast("delete data success");
					if(buildData.get(position).time == null)
						starttime = buildData.get(position).listInfo.getStarttime();
					else
						starttime = buildData.get(position).time;
					// delete data from database by id
					MHealthProviderMetaData.GetMHealthProvider(mActivity).deleteGPSListData(starttime);
					builderData();
				}
				
			});
			return true;
		}
	}

	class backClick implements OnClickListener {

		BaseFragment to;

		public backClick(BaseFragment to) {
			super();
			this.to = to;
		}

		@Override
		public void onClick(View v) {
			MainCenterActivity fca = (MainCenterActivity) getActivity();
			fca.switchFragment((BaseFragment) fca.getContent(), to);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageButton_title_add:
			createDialog();
			break;

		default:
			break;
		}
	}

	private void createDialog() {
		final MenuDialog dialog = new MenuDialog();
		final List<CommonBottomMenuItem> items = new ArrayList<CommonBottomMenuItem>();
		items.add(new CommonBottomMenuItem(0, "ͬ������", R.drawable.img_map_update));
		dialog.showBottomMenuDialog(mActivity, items);
		dialog.setOnClickedItemPosition(new onClickedItemPosition() {

			@Override
			public void onClickedPosition(int position) {
				switch (position) {
				case 0:
					// ͬ������  �ϴ� --����,�ȱ������ݿ�δ�ϴ������ݣ�Ȼ���ϴ�
					// ����������ȥ��ǰʱ����ǰ5����Ȼ���ȡ������ƥ�����ݿ��Ƿ���ڣ�����Ӧ�ı������
					downLoadData();
					break;
				default:
					break;
				}
				dialog.dismiss();
			}
		});
	}

	protected void downLoadData() {
		if (NetworkTool.getNetworkState(mActivity) == 0) {
			handler.sendEmptyMessage(R.string.MESSAGE_INTERNET_NONE);
			return;
		}
		ShowProgressDialog.showProgressDialog("����ͬ��", getActivity());
		Thread thread = new Thread(new DownLoadData());
		thread.start();
	}
	
	class DownLoadData implements Runnable{
		private static final String PRE_COUNT = "5";

		@Override
		public void run() {
			//���ϴ�
			int upload = UploadUtil.upload(mActivity,MHealthProviderMetaData.GetMHealthProvider(mActivity).getAllData());
			//�ϴ��Ƿ�ɹ�
			if(upload == R.string.MESSAGE_UPLOAD_GPS_SUCCESS || upload == R.string.MESSAGE_GPS_NODATA){
				ListGPSData gpsData = new ListGPSData();
				try {
					//ժҪ��
					int res = DataSyn.getInstance().getListGpsData(gpsData, Common.FormatCharDay(),PRE_COUNT,mActivity);
					if(res == 0){
						for (GPSListInfo gpsListInfo : gpsData.datavalue) {
							//ժҪ��ʱ��ƥ�����ݿ�ʱ��
							String restime = MHealthProviderMetaData.GetMHealthProvider(mActivity).getGpsInfoByStarttime(gpsListInfo.getStarttime());
							if(restime == null || restime.length() == 0){
								DetailGPSData gpsInfoDetail = new DetailGPSData();
								//��ȡ��ϸ��
								int result = DataSyn.getInstance().getDetailsGpsData(gpsInfoDetail,gpsListInfo.getStarttime(),mActivity);
								if(result == 0){
									//û�д�������-->�����Ҫ��
									MHealthProviderMetaData.GetMHealthProvider(mActivity).insertGpsListInfo(gpsListInfo);
									//������ϸ��
									MHealthProviderMetaData.GetMHealthProvider(mActivity).insertAllPoints(gpsInfoDetail,gpsListInfo.getStarttime());
								}
							}
						}
						handler.sendEmptyMessage(R.string.MESSAGE_SYNCHRO_GPS_SUCCESS);
					}else{
						handler.sendEmptyMessage(R.string.MESSAGE_SYNCHRO_GPS_FAILED);
					}
//					
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
	}

//	private void uploadData() {
//		mTask = new UpLoadAsyncTask();
//		mTask.execute();
//	}
	
	private List<GPSListInfo> getListData() {
		return MHealthProviderMetaData.GetMHealthProvider(mActivity).getAllData();
	}
	
//	private boolean updatetData(String isload,String starttime) {
//		return MHealthProviderMetaData.GetMHealthProvider(mActivity).updateIsUploadData(isload, starttime);
//	}
//	
//	private List<GpsInfoDetail> getDetailData(String starttime) {
//		List<GpsInfoDetail> gpsInfoArr = MHealthProviderMetaData.GetMHealthProvider(mActivity).getGpsInfoDetails(
//				starttime);
//		return gpsInfoArr;
//	}

	class UpLoadAsyncTask extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPreExecute() {
			ShowProgressDialog.showProgressDialog("�����ϴ�", getActivity());
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			return UploadUtil.upload(mActivity,  getListData());
			
		}

		@Override
		protected void onPostExecute(Integer result) {
			handler.sendEmptyMessage(result);
			super.onPostExecute(result);
		}
	}
	

//	private int upload() {
//		String pwd = PreferencesUtils.getString(mActivity, SharedPreferredKey.PASSWORD, null);
//		List<GPSListInfo> list = getListData();
//		if(list != null && list.size() != 0){
//			for (GPSListInfo gpsListInfo : list) {
//				if(gpsListInfo.getIsUpload() == 1){
//					//δ�ϴ�
//					int res = DataSyn.getInstance().UpLoadMapData(getPhoneNum(), pwd, gpsListInfo, getDetailData(gpsListInfo.getStarttime()));
//					if(res !=0){
//						return res;
//					}else{
//						//�ϴ��ɹ�,�������ݿ��ֶ�Ϊ0
//						updatetData("0", gpsListInfo.getStarttime());
//						return SUCCESS;
//					}
//				}
//			}
//			return -1;
//		}else{
//			return NODATA;
//		}
//	}
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			ShowProgressDialog.dismiss();
			switch (msg.what) {
			case R.string.MESSAGE_UPLOAD_GPS_SUCCESS:
				BaseToast("�����ϴ��ɹ�");
				break;
			case R.string.MESSAGE_GPS_NODATA:
				BaseToast("����û�����ݣ��Ͽ쿪���˶��ɣ�");
				break;
			case R.string.MESSAGE_SYNCHRO_GPS_SUCCESS:
				BaseToast("����ͬ���ɹ�");
				break;
			case R.string.MESSAGE_SYNCHRO_GPS_FAILED:
				BaseToast("����ͬ��ʧ��");
				break;

			default:
				BaseToast("�ϴ�ʧ��");
				if(mTask!=null&&mTask.isCancelled())
					mTask.cancel(true);
				break;
			}
			builderData();
		};
	};
	private UpLoadAsyncTask mTask;
	private TextView mTitleRight;
	private List<GPSListBean> buildData;
}
