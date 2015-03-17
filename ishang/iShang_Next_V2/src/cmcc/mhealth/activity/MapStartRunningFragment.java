package cmcc.mhealth.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import cmcc.mhealth.R;
import cmcc.mhealth.basic.BaseFragment;
import cmcc.mhealth.basic.MapApplication;
import cmcc.mhealth.basic.SampleFragment;
import cmcc.mhealth.bean.GPSListInfo;
import cmcc.mhealth.bean.GpsInfoDetail;
import cmcc.mhealth.common.AlertDialogs;
import cmcc.mhealth.common.AlertDialogs.onChoicedTeamListener;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Constants;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.common.PreferencesUtils;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.common.ShowProgressDialog;
import cmcc.mhealth.common.UploadUtil;
import cmcc.mhealth.db.MHealthProviderMetaData;
import cmcc.mhealth.service.StepService;
import cmcc.mhealth.slidingcontrol.MainCenterActivity;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.Symbol;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

public class MapStartRunningFragment extends SampleFragment implements OnClickListener {

	private static final String TAG = "Map";
	protected static final int TIME_CHANGE = 0;
	private MapView mMapView;
	private TextView mTextViewTimer;
	// private boolean mbCanDrawLine = false;
	// private List<GPSListInfo> mPoints = new ArrayList<GPSListInfo>();
	GeoPoint[] mLinePoints = null;
	// 构建线
	Geometry lineGeometry = new Geometry();
	private GraphicsOverlay graphicsOverlay;
	// double[][] mdoublePoints = { { 39.903517, 116.359538 }, { 39.904084,
	// 116.36221 }, { 39.90499, 116.365067 },
	// { 39.905136, 116.367277 }, { 39.905336, 116.369891 }, { 39.905519,
	// 116.369769 } };
	private MyOverlay mItemOverlay;
	private StepService mService;
	private Vibrator mVibrator;
	private MapController mMapController;
	private MyLocationOverlay myLocationOverlay;
	private LocationData mLocData;
	double[][] mdoublePoints = { { 39.903517, 116.359538 }, { 39.904084, 116.36221 }, { 39.90499, 116.365067 },
			{ 39.905136, 116.367277 }, { 39.905336, 116.369891 }, { 39.905519, 116.369769 } };
	private List<GpsInfoDetail> mListGpsDetails = new ArrayList<GpsInfoDetail>();
	List<GeoPoint> mGeoPoints = new ArrayList<GeoPoint>();
	private TextView mTextViewAltitede;
	private TextView mTextViewCal;
	private OverlayItem mOverlayEndItem;
	private String mTextviewTime;

	// private LinearLayout mLlayoutNull;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.acticity_running, container, false);
		view.setTag("inflated");
		super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		return view;
	}

	public void setType(String type) {
		// this.mType = type;
	}

	@Override
	public void clickListner() {
		super.clickListner();
		mMapView.setOnClickListener(this);
		mStopRunnnig.setOnClickListener(this);
		mFinishRunnnig.setOnClickListener(this);
		mBack.setOnClickListener(this);
		mButtonOpenMap.setOnClickListener(this);
		mButtonMoveToLocation.setOnClickListener(this);
	}

	@Override
	public void findViews() {
		super.findViews();
		mMapView = (MapView) findView(R.id.map_start);
		// initMap(mMapView);
		mTextViewTitle.setText("正在运动");
		mFinishRunnnig = (Button) findView(R.id.button_finish_running);
		mStopRunnnig = (Button) findView(R.id.button_stop_running);
		mTextViewkilometre = findView(R.id.textview_kilometre);
		mBack = findView(R.id.button_input_bg_back);
		mBack.setVisibility(View.VISIBLE);
		mLlayoutMap = findView(R.id.LLayout_map);

		mTextViewTimer = findView(R.id.startmap_timer);
		if(mTextviewTime != null && Common.isServiceRunning(getActivity(), Constants.SERVICE_RUNNING_NAME)){
			mTextViewTimer.setText(mTextviewTime);
			if(mStart_Stop){
				mStopRunnnig.setText("继续");
			}else{
				mStopRunnnig.setText("暂停");
			}
		}
			
		mButtonMoveToLocation = findView(R.id.button_get_location);
		mButtonOpenMap = findView(R.id.button_open_map);

		mTextViewSpeed = findView(R.id.textview_speed);
		mTextViewAltitede = findView(R.id.textview_altitude);
		mTextViewCal = findView(R.id.textview_cal);

		/**
		 * 获取地图控制器
		 */
		mMapController = mMapView.getController();
		/**
		 * 设置地图是否响应点击事件 .
		 */
		mMapController.enableClick(true);

		/**
		 * 显示内置缩放控件
		 */
		mMapView.setBuiltInZoomControls(false);
		/**
		 * 是否启用旋转手势
		 */
		mMapController.setRotationGesturesEnabled(true);
		// 缩放手势
		mMapController.setZoomGesturesEnabled(true);
		// 双击方大
		mMapView.setDoubleClickZooming(true);
		/**
		 * 是否启用平移手势
		 */
		mMapController.setScrollGesturesEnabled(true);
		/**
		 * 设定地图中心点
		 */
		GeoPoint p = new GeoPoint((int) (39.933859 * 1E6), (int) (116.400191 * 1E6));
		myLocationOverlay = new MyLocationOverlay(mMapView);// 定位点图层
		mLocData = new LocationData();
		mMapView.getOverlays().add(myLocationOverlay);
		graphicsOverlay = new GraphicsOverlay(mMapView);
		mMapView.getOverlays().add(graphicsOverlay);
		mMapController.setCenter(p);
		// 经过测试，图层添加上去就不需要再次添加了，只需要更改数据！！
		mItemOverlay = new MyOverlay(getResources().getDrawable(R.drawable.img_map_start), mMapView);// 节点图层
		mMapView.getOverlays().add(mItemOverlay);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MapApplication app = (MapApplication) getActivity().getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(getActivity());
			/**
			 * 如果BMapManager没有初始化则初始化BMapManager
			 */
			app.mBMapManager.init(MapApplication.strKey, new MapApplication.MyGeneralListener());
		}
		mVibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
		getActivity().bindService(new Intent(getActivity(), StepService.class), mConnection,
				Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
		startservice();
		getprefence();
	}

	private void getprefence() {
		mTextviewTime = PreferencesUtils.getString(getActivity(), SharedPreferredKey.TIMER, null);
		mStart_Stop = PreferencesUtils.getBoolean(getActivity(), SharedPreferredKey.START_STOP, false);
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	public void loadLogic() {
		// LinearLayout.LayoutParams lp = new
		// LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
		// LinearLayout.LayoutParams.MATCH_PARENT);
		// lp.setMargins(20, 10, 20, 20);
		// mLlayoutMap.setLayoutParams(lp);
	};

	StepService.OnMyLocationChangeListener changeListener = new StepService.OnMyLocationChangeListener() {

		@Override
		public void timer(String timer) {
			Message message = new Message();
			message.what = TIME_CHANGE;
			message.obj = timer;
			handler.sendMessage(message);
		}

		@Override
		public void change(GpsInfoDetail GPSInfo) {
			UIchange(GPSInfo);
		}

		@Override
		public void changeAltitude(double altitude) {
            if (altitude != 0) {
                if (altitude > 10) {
                    mTextViewAltitede.setText(String.format("%.1f", altitude));
                } else {
                    mTextViewAltitede.setText(String.format("%.2f", altitude));
                }
            }
		}
	};

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = ((StepService.myBind) service).getService();
			mService.registerCallback(changeListener);
			// mService.getType(mType);
			mService.requeatNotify();
			mMapController.setZoom(18);
		}

		public void onServiceDisconnected(ComponentName className) {
			mService = null;
		}
	};
	private Button mFinishRunnnig;
	private Button mStopRunnnig;

	protected void startservice() {
		Intent intent = new Intent();
		intent.setClass(getActivity(), StepService.class);
		getActivity().startService(intent);
	}

	/**
	 * 绘制折线，该折线状态随地图状态变化
	 * 
	 * @return 折线对象
	 */
	public Graphic drawLine(List<GpsInfoDetail> mAllPoints) {
		GeoPoint[] linePoints = new GeoPoint[mAllPoints.size()];// 数组不能存null！！
		Log.e("size", "mPoints 的 大小 = " + mAllPoints.size());

		for (int i = 0; i < mAllPoints.size(); i++) {
			GeoPoint geoPoint = new GeoPoint((int) (mAllPoints.get(i).getLatitude() * 1E6), (int) (mAllPoints.get(i)
					.getLongtitude() * 1E6));
			mGeoPoints.add(geoPoint);
			linePoints[i] = geoPoint;
		}
		if(mOverlayEndItem != null)
			mItemOverlay.removeItem(mOverlayEndItem);
		mOverlayEndItem = new OverlayItem(linePoints[mAllPoints.size()-1], "item", "items");
		mOverlayEndItem.setMarker(getResources().getDrawable(R.drawable.img_map_end));
		mItemOverlay.addItem(mOverlayEndItem);
		
		lineGeometry.setPolyLine(linePoints);
		// 设定样式
		Symbol lineSymbol = new Symbol();
		Symbol.Color lineColor = lineSymbol.new Color();
		lineColor.red = 255;
		lineColor.green = 0;
		lineColor.blue = 0;
		lineColor.alpha = 255;
		lineSymbol.setLineSymbol(lineColor, 10);
		// 生成Graphic对象
		Graphic lineGraphic = new Graphic(lineGeometry, lineSymbol);
		graphicsOverlay.setData(lineGraphic);
		return lineGraphic;
	}

	public void changeText(List<GpsInfoDetail> points) {
		double mDistance = 0;
		double avgCal = 0;
//		BaseToast(" 坐标点数量 = " + points.size());
		// for (int i = 0; i < points.size() - 1; i++) {
		// GeoPoint firstpos = points.get(i);
		// GeoPoint endpos = points.get(i + 1);
		// mKilometre += DistanceUtil.getDistance(firstpos, endpos);
		// }
		for (int i = 0; i < points.size(); i++) {
			mDistance += points.get(i).getDistance();
			avgCal += points.get(i).getCal();
		}
		// if (mPoints.size() > 1 && mPos + 1 < mPoints.size()) {
		// GeoPoint firstpos = points.get(mPos++);
		// GeoPoint endpos = points.get(mPos);
		// mKilometre += DistanceUtil.getDistance(firstpos, endpos);
		// }
		;
        if(mDistance/1000f>10){
            mTextViewkilometre.setText(String.format("%.1f", mDistance / 1000f));                 
        }else{
            mTextViewkilometre.setText(String.format("%.2f", mDistance / 1000f));
        }
        
		if (mService != null && mService.mCount != 0) {
	        if(((mDistance / 1000 / mService.mCount) * 3600)>10){
	            mTextViewSpeed.setText(String.format("%.1f", (mDistance / 1000 / mService.mCount) * 3600));              
	        }else{
	            mTextViewSpeed.setText(String.format("%.2f", (mDistance / 1000 / mService.mCount) * 3600));
	        }
		}
        if(avgCal>10){
            mTextViewCal.setText(String.format("%.1f", avgCal));                
        }else{
            mTextViewCal.setText(String.format("%.2f", avgCal));
        }
		// mTextView.setText("");
		// mTextView.setText(str + "\n 坐标点数量 = " + mPoints.size());
	}

	public void UIchange(GpsInfoDetail arrGPSInfo) {
		if (arrGPSInfo == null)
			return;
		// mPoints = arrGPSInfo;
		// if (arrGPSInfo.size() == 1) {
		// /**
		// * 设置地图缩放级别
		// */
		// mMapController.setZoom(18);
		// }
//		mVibrator.vibrate(new long[] { 100, 100 }, -1);// //add point
		mListGpsDetails.add(arrGPSInfo);
		drawOveray(arrGPSInfo);
	}

	public GeoPoint getGeopoint(double lat, double lte) {
		return new GeoPoint((int) (lat * 1E6), (int) (lte * 1E6));
	}

	private void drawOveray(GpsInfoDetail arrGPSInfo) {

		setMyLocation(arrGPSInfo);
		refresh(arrGPSInfo);
	}

	private void setMyLocation(GpsInfoDetail arrGPSInfo) {
		mNowPoint = new GeoPoint((int) (arrGPSInfo.getLatitude() * 1E6), (int) (arrGPSInfo.getLongtitude() * 1E6));

		mLocData.latitude = mNowPoint.getLatitudeE6() / 1e6;
		mLocData.longitude = mNowPoint.getLongitudeE6() / 1e6;
		mLocData.direction = 2.0f;
		myLocationOverlay.setData(mLocData);
	}

	private void refresh(GpsInfoDetail arrGPSInfo) {
		// 添加折线
		drawLine(mListGpsDetails);
		if (mListGpsDetails.size() == 1) {
			mMapView.getController().animateTo(mNowPoint);
			OverlayItem item1 = new OverlayItem(mNowPoint, "item", "items");
			mItemOverlay.addItem(item1);
		}
		changeText(mListGpsDetails);
		double tmpAltitude =arrGPSInfo.getAltitude();
        if (tmpAltitude != 0) {
            if (tmpAltitude > 10) {
                mTextViewAltitede.setText(String.format("%.1f", tmpAltitude));
            } else {
                mTextViewAltitede.setText(String.format("%.2f", tmpAltitude));
            }
        }
		Common.fitPoints(mGeoPoints, mMapController);
		mMapView.refresh();
	}

	/*
	 * 要处理overlay点击事件时需要继承ItemizedOverlay 不处理点击事件时可直接生成ItemizedOverlay.
	 */
	class MyOverlay extends ItemizedOverlay<OverlayItem> {
		private PopupOverlay pop;
		private double juli;
		private Button button;

		// 用MapView构造ItemizedOverlay
		public MyOverlay(Drawable mark, MapView mapView) {
			super(mark, mapView);
		}

		protected boolean onTap(int index) {
			// 在此处理item点击事件
			GeoPoint endpos = null;
			OverlayItem item = getItem(index);
			Log.e(TAG, "index = " + index);
			juli = 0.0;
			// for (int i = 0; i < index; i++) {
			// GeoPoint firstpos = mPoints.get(i);
			// if(mPoints.size() != i+1)
			// endpos = mPoints.get(i+1);
			// juli += DistanceUtil.getDistance(mAllPoints.get(0),
			// mAllPoints.get(index));
			// System.out.println(juli + "");
			// }

			BaseToast("item " + index + "click");
			PopWndLayer(item.getPoint(), juli);
			return true;
		}

		public boolean onTap(GeoPoint pt, MapView mapView) {
			if (pop != null) {
				pop.hidePop();
				mMapView.removeView(button);
			}
			return false;
		}

		public void PopWndLayer(GeoPoint point, double walk) {
			pop = new PopupOverlay(mMapView, new PopupClickListener() {
				@Override
				public void onClickedPopup(int index) {
					// 在此处理pop点击事件，index为点击区域索引,点击区域最多可有三个
				}
			});
			/**
			 * 准备pop弹窗资源，根据实际情况更改 弹出包含三张图片的窗口，可以传入三张图片、两张图片、一张图片。
			 * 弹出的窗口，会根据图片的传入顺序，组合成一张图片显示. 点击到不同的图片上时，回调函数会返回当前点击到的图片索引index
			 */
			Bitmap[] bmps = new Bitmap[3];
			try {
				bmps[0] = BitmapFactory.decodeStream(mActivity.getAssets().open("marker1.png"));
				bmps[1] = BitmapFactory.decodeStream(mActivity.getAssets().open("marker2.png"));
				bmps[2] = BitmapFactory.decodeStream(mActivity.getAssets().open("marker3.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			button = new Button(mActivity);
			button.setBackgroundResource(R.drawable.popup);
			button.setText("到此处您走了" + (int) walk + "米");
			// 弹出pop,隐藏pop
			pop.showPopup(button, point, 32);
			// 隐藏弹窗
			// pop.hidePop();

		}

		// 自2.1.1 开始，使用 add/remove 管理overlay , 无需重写以下接口
		/*
		 * @Override protected OverlayItem createItem(int i) { return
		 * mGeoList.get(i); }
		 * 
		 * @Override public int size() { return mGeoList.size(); }
		 */
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	// showDialog();
	// mAlertDialog.show();
	// return true;
	// }
	// return false;
	// }

	@Override
	public void onDestroy() {
		Log.e(TAG, "onDestroy -- >");
		mService.unRegisterCallback(changeListener);
		getActivity().unbindService(mConnection);
		if(Common.isServiceRunning(mActivity, Constants.SERVICE_RUNNING_NAME)){
			PreferencesUtils.putString(mActivity, SharedPreferredKey.TIMER, mTextViewTimer.getText().toString());
		}else{
			PreferencesUtils.removeSp(mActivity, SharedPreferredKey.TIMER);
		}
		mMapView.destroy();
		// save list
		// mPos
		super.onDestroy();
	}

	@Override
	public void onPause() {
		mMapView.onPause();
		// mbCanDrawLine = false;
		Log.e(TAG, "onPause() -- >");
		// if (mBMapMan != null) {
		// mBMapMan.stop();
		// }
		super.onPause();
	}

	@Override
	public void onResume() {
		mMapView.onResume();
		// mbCanDrawLine = true;
		Log.e(TAG, "onResume() -- >");
		// if (mBMapMan != null) {
		// mBMapMan.start();
		// change点标记
		if (mListGpsDetails.size() != 0) {
			// mMapController.setZoom(16);
		} else {
			if (Common.isServiceRunning(mActivity, Constants.SERVICE_RUNNING_NAME)) {
				// 从数据库取出数据
				String starttime = PreferencesUtils.getString(mActivity, SharedPreferredKey.START_TIME, null);
				if (starttime != null) {
					mListGpsDetails = MHealthProviderMetaData.GetMHealthProvider(mActivity)
							.getGpsInfoDetails(starttime);
					if (mListGpsDetails != null && mListGpsDetails.size() != 0)
						// refresh(mAllPoints.get(mAllPoints.size()-1));
						drawOveray(mListGpsDetails.get(mListGpsDetails.size() - 1));
				}

				// 坐标点处图重绘
				// OverlayItem item1 = new OverlayItem(nowPoint, "item",
				// "items");
				// mItemOverlay.addItem(item1);
			}
		}
		super.onResume();
	}

	private TextView mTextViewkilometre;
	private int mKilometre;
	private Button mButtonOpenMap;
	private boolean mOpenMap;
	private LinearLayout mLlayoutMap;
	private Button mButtonMoveToLocation;
	private boolean mStart_Stop;
	private TextView mTextViewSpeed;
	private GeoPoint mNowPoint;

	@Override
	public void onClick(View v) {
		ScaleAnimation sa = null;
		int mWidth = mLlayoutMap.getWidth();
		int mHeight = mLlayoutMap.getHeight();
		int windowWidth = mActivity.getWindowManager().getDefaultDisplay().getWidth();
		int windowheight = mActivity.getWindowManager().getDefaultDisplay().getHeight();
		switch (v.getId()) {
		// case R.id.btn_jd:
		// if (mPoints.size() != 0)
		// mMapView.getController().animateTo(mPoints.get(mPoints.size() - 1));
		// break;
		// case R.id.btn_jl:
		// for (int i = 0; i < mPoints.size() - 1; i++) {
		// GeoPoint firstpos = mPoints.get(i);
		// GeoPoint endpos = mPoints.get(i + 1);
		// juli += DistanceUtil.getDistance(firstpos, endpos);
		// }
		// Toast.makeText(getApplicationContext(), juli + "",
		// Toast.LENGTH_LONG).show();
		// break;
		case R.id.button_finish_running:
			saveData();
			break;
		case R.id.button_stop_running:
			// if (Common.isServiceRunning(mActivity,
			// Constants.SERVICE_RUNNING_NAME)) {
			if (mStart_Stop) {
				PreferencesUtils.putBoolean(mActivity, SharedPreferredKey.START_STOP, false);
				mStart_Stop = false;
				mStopRunnnig.setText("暂停");
				mService.startTimer();
				// stopService();
			} else {
				PreferencesUtils.putBoolean(mActivity, SharedPreferredKey.START_STOP, true);
				mStart_Stop = true;
				mStopRunnnig.setText("继续");
				mService.stopTimer();
				// restartService();
			}
			// // TODO ?
			// PreferencesUtils.putLong(mActivity, SharedPreferredKey.TIMER, new
			// Date().getTime());
			break;
		case R.id.button_input_bg_back:
			MainCenterActivity m = (MainCenterActivity) mActivity;
			m.showMenu();
			break;
		case R.id.map_start:
			mMapView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			break;
		case R.id.button_open_map:
			openOrCloseMap(mWidth, mHeight, windowWidth, windowheight);
			break;
		case R.id.button_get_location:
			if (mGeoPoints != null && mGeoPoints.size() > 1)
				Common.fitPoints(mGeoPoints, mMapController);
			else
				if(mGeoPoints.size() > 0)
					mMapController.animateTo(mGeoPoints.get(0));
			break;
		default:
			break;
		}
	}
	
	private void finishRunning() {
		stopService();
		// 返回计划运动界面 TODO
		MainCenterActivity fca = (MainCenterActivity) getActivity();
		fca.switchFragment((BaseFragment) fca.getContent(), (BaseFragment) new MapListGPSFragment());
	}

	private void saveData() {
		final String starttime = PreferencesUtils.getString(mActivity, SharedPreferredKey.START_TIME, null);
		if (mListGpsDetails.size() < 2) {
			AlertDialogs.creatSingleChoiceDialog("你还没有运动轨迹，确认要退出吗？", mActivity, null, new onChoicedTeamListener() {

				@Override
				public void onChoicedTeam(int team) {
					if (team == 0) {
						// true
						if (starttime != null) {
							MHealthProviderMetaData.GetMHealthProvider(mActivity).deleteDetailData(starttime);
							handler.sendEmptyMessage(R.string.MESSAGE_UN_SAVE_DATA);
						}
						finishRunning();
					}
				}
			});

		} else {
			Dialog dialog = new AlertDialog.Builder(mActivity)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("提示").setMessage("保存数据吗?")
			.setPositiveButton("好的", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {

					// MHealthProviderMetaData.GetMHealthProvider(mActivity).insertAllPoints(mPoints);
					// MHealthProviderMetaData.GetMHealthProvider(mActivity).insertAllPoints(mAllPoints,
					// Common.FormatCharDay());
					if (starttime != null && mListGpsDetails.size() > 0) {
						// MHealthProviderMetaData.GetMHealthProvider(mActivity).AddEndTimeAndDuration(
						// mTextViewTimer.getText().toString(),
						// Common.getDate2Time(new Date(),
						// Common.COMMON_DATE_YYYY_MM_DD_MID_CREATETIME),
						// starttime);
						final GPSListInfo gpsListInfo = new GPSListInfo();
						gpsListInfo.setStarttime(starttime);

						gpsListInfo.setDuration(mTextViewTimer.getText().toString());
						float cal = 0;
						float speed = 0;
						float distance = 0;
						for (GpsInfoDetail gpsInfoDetail : mListGpsDetails) {
							cal += gpsInfoDetail.getCal();
							distance += gpsInfoDetail.getDistance();
						}

						gpsListInfo.setDistance(distance);
						gpsListInfo.setCal(cal);
						if (mService != null && mService.mCount != 0) {
							gpsListInfo.setSpeed((distance / 1000 / mService.mCount) * 3600);
						}
						gpsListInfo.setSporttype(Constants.RUNTYPE);
						gpsListInfo.setIsUpload(1);
						// 上传数据
						final List<GPSListInfo> infos = new ArrayList<GPSListInfo>();
						infos.add(gpsListInfo);
						ShowProgressDialog.showProgressDialog("正在上传", mActivity);
						new Thread() {
							public void run() {
								Message msg = new Message();
								msg.obj = gpsListInfo;
								msg.what = UploadUtil.upload(mActivity, infos);
								handler.sendMessage(msg);
							};
						}.start();
					}
				
				}
			}).setNegativeButton("不保存", new  DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (starttime != null) {
						MHealthProviderMetaData.GetMHealthProvider(mActivity).deleteDetailData(starttime);
						handler.sendEmptyMessage(R.string.MESSAGE_UN_SAVE_DATA);
					}
				}
			}).setNeutralButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			}).create();
			dialog.show();
//			
//			
//			// 保存到数据库
//			AlertDialogs.creatSingleChoiceDialog("保存数据吗?", mActivity, null, new onChoicedTeamListener() {
//
//				@Override
//				public void onChoicedTeam(int team) {
//					mService.stopTimer();
//					if (team == 0) {} else {
//						// 删除本地存储的数据
//						
//					}
//				}
//			});
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ShowProgressDialog.dismiss();
			switch (msg.what) {
			case R.string.MESSAGE_UPLOAD_GPS_SUCCESS:
				BaseToast(String.valueOf(getString(R.string.MESSAGE_UPLOAD_GPS_SUCCESS)));
				updateDataBase(msg,0);
				break;
			case R.string.MESSAGE_UN_SAVE_DATA:
				BaseToast(String.valueOf(getString(R.string.MESSAGE_UN_SAVE_DATA)));
				removeSp();
				finishRunning();
				break;
			case TIME_CHANGE:
				mTextViewTimer.setText(msg.obj.toString());
				break;
			default:
				BaseToast("上传失败");
				updateDataBase(msg,1);
				break;
			}
		}

		private void updateDataBase(android.os.Message msg,int flag) {
			GPSListInfo gpsListInfo = new GPSListInfo();
			gpsListInfo = (GPSListInfo) msg.obj;
			gpsListInfo.setIsUpload(flag);
			MHealthProviderMetaData.GetMHealthProvider(mActivity).insertGpsListInfo(gpsListInfo);
			removeSp();
			ShowProgressDialog.dismiss();
			finishRunning();
		};
	};
	
	private void removeSp(){
		PreferencesUtils.removeSp(mActivity, SharedPreferredKey.START_TIME);
		PreferencesUtils.removeSp(mActivity, SharedPreferredKey.TIMER);
		PreferencesUtils.removeSp(mActivity, SharedPreferredKey.START_STOP);
	}

	private void openOrCloseMap(int mWidth, int mHeight, int wWidth, int wHeight) {
		ScaleAnimation sa;
		mLlayoutMap.clearAnimation();
		AnimationSet set = new AnimationSet(true);
		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) mLlayoutMap.getLayoutParams(); // 取控件mGrid当前的布局参数
		// if (mButtonOpenMap.isChecked()) {
		//
		// } else {
		//
		// }
		if (mOpenMap) {
		    mButtonOpenMap.setBackgroundResource(R.drawable.button_openmap);
	        
			mOpenMap = false;
			// linearParams.weight = 1.0f;
			// linearParams.setMargins(20, 10, 20, 10);
			sa = new ScaleAnimation((float) wWidth / wWidth, 1.0f, (float) wHeight / mHeight, 1.0f,
					Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.1f);
			// Animation animation = AnimationUtils.loadAnimation(mActivity,
			// R.anim.map_close_anim);
			// mLlayoutMap.setAnimation(animation);
			// mLlayoutMap.setLayoutParams(linearParams);
		} else {
            mButtonOpenMap.setBackgroundResource(R.drawable.button_closemap);
            
			mOpenMap = true;
			linearParams.weight = 0.0f;
			float fx = (float) mWidth / wWidth;
			float fy = (float) mLlayoutMap.getTop() / wHeight;
			// 展开
			linearParams.setMargins(0, 0, 0, 0);
			sa = new ScaleAnimation(fx, 1.0f, fy, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
					fy);
			// Animation animation = AnimationUtils.loadAnimation(mActivity,
			// R.anim.map_open_anim);
			// mLlayoutMap.setAnimation(animation);
			mLlayoutMap.setLayoutParams(linearParams);
		}
		sa.setDuration(500);
		set.addAnimation(sa);
		set.setInterpolator(new AccelerateDecelerateInterpolator());
		set.setInterpolator(new OvershootInterpolator(1f));
		set.setAnimationListener(new myAnimationListener(mLlayoutMap));
		mLlayoutMap.startAnimation(set);
	}

	class myAnimationListener implements AnimationListener {
		LinearLayout view;

		public myAnimationListener(LinearLayout view) {
			super();
			this.view = view;
		}

		@Override
		public void onAnimationStart(Animation animation) {
			if (!mOpenMap) {
				RelativeLayout layout = findView(R.id.linearlayout_textview);
				Animation animation1 = AnimationUtils.loadAnimation(mActivity, R.anim.map_close_anim);
				layout.startAnimation(animation1);

				LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) mLlayoutMap.getLayoutParams(); // 取控件mGrid当前的布局参数
				linearParams.weight = 1.0f;
				linearParams.setMargins(20, 10, 20, 10);
				mLlayoutMap.setLayoutParams(linearParams);
			}
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// toast("animatino finish");
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

	}

	/** stop service */
	private void stopService() {
		mService.unRegisterCallback(changeListener);
		if (Common.isServiceRunning(mActivity, Constants.SERVICE_RUNNING_NAME)) {
			// stopTimer();
			mActivity.stopService(new Intent().setClass(mActivity, StepService.class));
		}
	}

	/** restart service */
	private void restartService() {
		startservice();
	}
}
