package cmcc.mhealth.slidingcontrol;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import cmcc.mhealth.R;
import cmcc.mhealth.activity.MapListGPSFragment;
import cmcc.mhealth.activity.MapSelectTRunType;
import cmcc.mhealth.activity.MapStartRunningFragment;
import cmcc.mhealth.basic.BaseFragment;
import cmcc.mhealth.basic.BaseMapFragment;
import cmcc.mhealth.basic.MapApplication;
import cmcc.mhealth.bean.CommonBottomMenuItem;
import cmcc.mhealth.bean.GPSListInfo;
import cmcc.mhealth.bean.GpsInfoDetail;
import cmcc.mhealth.bean.RunType;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Constants;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.common.MenuDialog;
import cmcc.mhealth.common.PreferencesUtils;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.common.MenuDialog.onClickedItemPosition;
import cmcc.mhealth.db.MHealthProviderMetaData;
import cmcc.mhealth.net.NetworkTool;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MapFragment extends BaseMapFragment implements BDLocationListener, OnClickListener {
	private static final int HISTORY_DATA = 0;
	private RelativeLayout mRelatYStartRunning;
	private RelativeLayout mRLayoutGetHistoryData;
	private View mBack;
	private RelativeLayout mRelativeLayoutSelectType;
	private int mTypeID = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * 使用地图sdk前需先初始化BMapManager. BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
		 * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
		 */
		MapApplication app = (MapApplication) getActivity().getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(getActivity());
			/**
			 * 如果BMapManager没有初始化则初始化BMapManager
			 */
			app.mBMapManager.init(MapApplication.strKey, new MapApplication.MyGeneralListener());
		}
		unSaveData();
	}

	private void showDialog() {
		AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
		dlg.setTitle("提示");
		dlg.setMessage("GPS未开启,轨迹功能使用GPS,请开启GPS后,再点击开始运动按钮。");
		// 设置自定义对话框的样式
		dlg.setPositiveButton("开启GPS", //
				new DialogInterface.OnClickListener() // 设置事件监听
				{
					public void onClick(DialogInterface dialog, int whichButton) {
						// 跳转到网络设置
						Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(intent);
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).create().show();// 创建
	}

	private void unSaveData() {
		float cal = 0;
		float distance = 0;
		String firsttime = null;
		String endttime = null;
		String duration;
		int sec = 0;
		String starttime = PreferencesUtils.getString(getActivity(), SharedPreferredKey.START_TIME, null);
		if(starttime != null){//异常崩溃未保存的数据
			//获取所有详细包
			final GPSListInfo gpsListInfo = new GPSListInfo();
			List<GpsInfoDetail> mListGpsDetails = MHealthProviderMetaData.GetMHealthProvider(getActivity())
					.getGpsInfoDetails(starttime);
			if(mListGpsDetails.size() < 2){
				//一条数据,不保存
				PreferencesUtils.removeSp(getActivity(), SharedPreferredKey.START_TIME);
				return;
			}
			for (int i = 0; i < mListGpsDetails.size(); i++) {
				if(i == 0){
					firsttime = mListGpsDetails.get(i).getDetailtime();
				}else if(i == mListGpsDetails.size()-1){
					endttime = mListGpsDetails.get(i).getDetailtime();
				}
				cal += mListGpsDetails.get(i).getCal();
				distance += mListGpsDetails.get(i).getDistance();
			}
			if(firsttime == null && endttime == null) return;
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				java.util.Date parsef = dateFormat.parse(firsttime);
				java.util.Date parsee = dateFormat.parse(endttime);
				sec = (int) ((parsee.getTime()-parsef.getTime())/1000);
				duration = Common.sec2Time(sec);
				gpsListInfo.setDuration(duration);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			gpsListInfo.setStarttime(starttime);
			gpsListInfo.setCal(cal);
			gpsListInfo.setDistance(distance);
			gpsListInfo.setSporttype(Constants.RUNTYPE);//TODO  数据库添加类型字段
			gpsListInfo.setIsUpload(1);
			gpsListInfo.setSpeed((distance / 1000 / sec) * 3600);
			MHealthProviderMetaData.GetMHealthProvider(getActivity()).insertGpsListInfo(gpsListInfo);
			PreferencesUtils.removeSp(getActivity(), SharedPreferredKey.START_TIME);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.map_fragment, container, false);
		view.setTag("inflated");
		super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		return view;
	}

	@Override
	public void findViews() {
		super.findViews();
		mBack = findView(R.id.button_input_bg_back);
		mBack.setVisibility(View.VISIBLE);
		mBack.setOnClickListener(new backClick(new MapListGPSFragment()));
		mImageButtonBack.setVisibility(View.VISIBLE);
		mImageButtonBack.setBackgroundResource(R.drawable.slidemenu_button);
		mMapView = (MapView) findView(R.id.simpleMap);
		mRelatYStartRunning = findView(R.id.relativelaouut_start_runnnig);
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
		mRelativeLayoutSelectType = findView(R.id.rl_select_type);

		initMap(mMapView);
		/**
		 * 是否启用旋转手势
		 */
		mMapController.setRotationGesturesEnabled(false);
		// 缩放手势
		mMapController.setZoomGesturesEnabled(false);
		// 双击方大
		mMapView.setDoubleClickZooming(false);
		/**
		 * 是否启用平移手势
		 */
		mMapController.setScrollGesturesEnabled(false);
	}

	@Override
	public void clickListner() {
		super.clickListner();
		mTextViewTitle.setText("轨迹");
		mRelativeLayoutSelectType.setOnClickListener(this);
		mRLayoutGetHistoryData.setOnClickListener(this);
		mRelatYStartRunning.setOnClickListener(this);
	}

	@Override
	public void loadLogic() {
		super.loadLogic();
	}

	class backClick implements OnClickListener {

		BaseFragment to;

		public backClick(BaseFragment to) {
			super();
			this.to = to;
		}

		@Override
		public void onClick(View v) {
			showMenu();
		}

	}

	@Override
	public void onClick(View v) {
		final MainCenterActivity fca = (MainCenterActivity) getActivity();
        switch (v.getId()) {
            case R.id.relativelaouut_start_runnnig:
                if (!NetworkTool.isGPSOPen(getActivity())) {
                    showDialog();
                } else {
                    MapStartRunningFragment fragment = new MapStartRunningFragment();
                    fragment.setType(((TextView)findView(R.id.textviewview_run_type)).getText()
                            .toString());
                    fca.switchFragment((BaseFragment)fca.getContent(), fragment);
                }
                // PreferencesUtils.putBoolean(mActivity,
                // SharedPreferredKey.RUNNING, true);
                break;
            case R.id.imageButton_title_add:
                // Intent intent = new Intent();
                // intent.setClass(mActivity, MapGPSHistoryActivity.class);
                // startActivity(intent);
                final MenuDialog dialog = new MenuDialog();
                final List<CommonBottomMenuItem> items = new ArrayList<CommonBottomMenuItem>();
                // items.add(new CommonBottomMenuItem(UPLOAD, "上传数据",
                // R.drawable.race_func_buildrace));
                // items.add(new CommonBottomMenuItem(DOWNLOAD_DATA, "同步数据",
                // R.drawable.race_func_buildrace));
                items.add(new CommonBottomMenuItem(HISTORY_DATA, "历史轨迹", R.drawable.img_map_history));
                dialog.showBottomMenuDialog(mActivity, items);
                dialog.setOnClickedItemPosition(new onClickedItemPosition() {

                    @Override
                    public void onClickedPosition(int position) {
                        switch (position) {
                        // case UPLOAD:
                        // // 上传数据
                        // uploadData(position);
                        // break;
                        // case DOWNLOAD_DATA:
                        //
                        // break;
                            case HISTORY_DATA:
                                fca.switchFragment((BaseFragment)fca.getContent(),
                                        (BaseFragment)new MapListGPSFragment());
                                break;

                            default:
                                break;
                        }
                        dialog.dismiss();
                    }
                });
                break;
            case R.id.rl_select_type:
                Intent intent = new Intent();
                intent.setClass(mActivity, MapSelectTRunType.class);
                startActivityForResult(intent, 200);
                mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.silde_out_left);
                break;

            default:
                break;
        }
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
			// do something
			Bundle bundle = data.getExtras();
			RunType runType = bundle.getParcelable("type");
			int img_id = runType.getImage_type();
			String text = runType.getTextview_type();
			mTypeID = runType.getId();
			Constants.RUNTYPE = mTypeID;
			((ImageView) findView(R.id.imageview_run_type)).setImageResource(img_id);
			((TextView) findView(R.id.textviewview_run_type)).setText(text);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mMapView != null) {
			mMapView.onResume();
			mMapView.setVisibility(View.VISIBLE);
		}
		if (mLocClient != null) {
			location();
			mLocClient.start();
			BDLocation location = mLocClient.getLastKnownLocation();
			if (location != null)
				mMapView.getController().animateTo(
						new GeoPoint((int) (mLocData.latitude * 1e6), (int) (mLocData.longitude * 1e6)));
		}
	}

	@Override
	public void onDestroy() {
		Logger.i("Mapfragment", "onDestroy()");
		if (mMapView != null) {
			try {
				mMapView.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			//TODO
			if (!Common.isServiceRunning(mActivity, Constants.SERVICE_RUNNING_NAME)) {
				MapApplication app = (MapApplication) mActivity.getApplication();
				if (app.mBMapManager != null) {
					app.mBMapManager.destroy();
					app.mBMapManager = null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		super.onDestroy();
	}

	@Override
	public void onPause() {
		if (mMapView != null) {
			mMapView.onPause();
			mMapView.setVisibility(View.VISIBLE);
		}

		if (mLocClient != null) {
			mLocClient.stop();
			mLocClient.unRegisterLocationListener(this);
		}
		super.onPause();
	}

	private void location() {
		mLocClient.registerLocationListener(this);
	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		if (location == null)
			return;
		System.out.println("map fragment change");
		// mLocationPoint = new GeoPoint((int) (location.getLatitude() * 1e6),
		// (int) (location.getLongitude() * 1e6));
		initLocationData(location);
	}

	private void initLocationData(BDLocation location) {
		/**
		 * 设置地图缩放级别
		 */
		mMapController.setZoom(18);
		// 经过测试，图层添加上去就不需要再次添加了，只需要更改数据！！
		mLocData.latitude = location.getLatitude();
		mLocData.longitude = location.getLongitude();
		// 如果不显示定位精度圈，将accuracy赋值为0即可
		mLocData.accuracy = location.getRadius();
		// 此处可以设置 locData的方向信息, 如果定位 SDK 未返回方向信息，用户可以自己实现罗盘功能添加方向信息。
		mLocData.direction = location.getDerect();
		myLocationOverlay.setData(mLocData);
		mMapView.refresh();
		mMapView.getController().animateTo(
				new GeoPoint((int) (mLocData.latitude * 1e6), (int) (mLocData.longitude * 1e6)));
	}

	@Override
	public void onReceivePoi(BDLocation arg0) {

	}
}
