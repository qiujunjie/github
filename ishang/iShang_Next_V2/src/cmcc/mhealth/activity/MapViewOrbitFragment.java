/**
 * 历史运动轨迹
 */

package cmcc.mhealth.activity;
import java.util.ArrayList;
import java.util.List;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.Symbol;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cmcc.mhealth.R;
import cmcc.mhealth.basic.BaseFragment;
import cmcc.mhealth.basic.BaseMapFragment;
import cmcc.mhealth.basic.MapApplication;
import cmcc.mhealth.bean.GPSListInfo;
import cmcc.mhealth.bean.GpsInfoDetail;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.common.ShowProgressDialog;
import cmcc.mhealth.common.UploadUtil;
import cmcc.mhealth.db.MHealthProviderMetaData;
import cmcc.mhealth.slidingcontrol.MainCenterActivity;

public class MapViewOrbitFragment extends BaseMapFragment implements OnClickListener {
	private MapController mMapController;
	private MapView mMapView;
	private MyOverlay mStartItemOverlay;
	private List<GeoPoint> mAllPoints;
	private Geometry mLineGeometry = new Geometry();
	private GraphicsOverlay graphicsOverlay;
	private View mBack;
	private Button mButtonOpenMap;
	private boolean mOpenMap;
	private View mLlayoutMap;
	private Button mButton;
	private TextView mTextViewTimer;
	private TextView mTextViewkilometre;
	private TextView mTextViewSpeed;
	private TextView mTextViewAltitede;
	private TextView mTextViewCal;
	private GPSListInfo mGpsListInfo;
	private MyOverlay mEndItemOverlay;
	private Button mButtonMoveToLocation;
	private LinearLayout mLinearBottom;
	private RelativeLayout mRLayoutGetHistoryData;
	private UpLoadAsyncTask mTask;
	
	public MapViewOrbitFragment(GPSListInfo listInfo){
		this.mGpsListInfo = listInfo;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.acticity_running, container, false);
		super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		return view;
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
	}
	
	@Override
	public void loadLogic() {
		super.loadLogic();
//		String starttime = PreferencesUtils.getString(mActivity, SharedPreferredKey.HISTORY_START_TIME, null);
		String starttime = mGpsListInfo.getStarttime();
		mGpsInfoArr = MHealthProviderMetaData.GetMHealthProvider(mActivity).getGpsInfoDetails(starttime);
		mAllPoints = new ArrayList<GeoPoint>();
		if(mGpsInfoArr != null && mGpsInfoArr.size() != 0){
			for (GpsInfoDetail gpsInfo : mGpsInfoArr) {
				mAllPoints.add(new GeoPoint((int)(gpsInfo.getLatitude()*1E6),(int)(gpsInfo.getLongtitude()*1E6)));
			}
			Graphic graphic = drawLine(mAllPoints);
			graphicsOverlay.setData(graphic);
//			mMapView.getController().animateTo(
//					new GeoPoint((int) (mLocData.latitude * 1e6), (int) (mLocData.longitude * 1e6)));
		}
		mTextViewTimer.setText(mGpsListInfo.getDuration());
		float tmpDistance =  mGpsListInfo.getDistance()/1000f;
        if(tmpDistance>10){
            mTextViewkilometre.setText(String.format("%.1f", tmpDistance));              
        }else{
            mTextViewkilometre.setText(String.format("%.2f", tmpDistance));
        }

        float tmpCal = mGpsListInfo.getCal();
        if(tmpCal>10){
            mTextViewCal.setText(String.format("%.1f", tmpCal));            
        }else{
            mTextViewCal.setText(String.format("%.2f", tmpCal));
        }
        float tmpSpeed = mGpsListInfo.getSpeed();
        if(tmpCal>10){
            mTextViewSpeed.setText(String.format("%.1f", tmpSpeed));         
        }else{
            mTextViewSpeed.setText(String.format("%.2f", tmpSpeed));
        }
//		mTextViewAltitede.setText(mGpsListInfo.get);
	}

	private Graphic drawLine(List<GeoPoint> mAllPoints) {
		GeoPoint[] linePoints = new GeoPoint[mAllPoints.size()];// 数组不能存null！！
		Log.e("size", "mPoints 的 大小 = " + mAllPoints.size());

		for (int i = 0; i < mAllPoints.size(); i++) {
			linePoints[i] = mAllPoints.get(i);
			if(i == 0){
				OverlayItem item1 = new OverlayItem(linePoints[i], "item", "items");
				mStartItemOverlay.addItem(item1);
			}
			if(i == mAllPoints.size()-1){
				OverlayItem item1 = new OverlayItem(linePoints[i], "item", "items");
				item1.setMarker(getResources().getDrawable(R.drawable.img_map_end));
				mStartItemOverlay.addItem(item1);
			}
		}
		mLineGeometry.setPolyLine(linePoints);
		// 设定样式
		Symbol lineSymbol = new Symbol();
		Symbol.Color lineColor = lineSymbol.new Color();
		lineColor.red = 255;
		lineColor.green = 0;
		lineColor.blue = 0;
		lineColor.alpha = 255;
		lineSymbol.setLineSymbol(lineColor, 5);
		// 生成Graphic对象
		Graphic lineGraphic = new Graphic(mLineGeometry, lineSymbol);
//		 mMapView.getController().animateTo(mAllPoints.get(0));
		return lineGraphic;
	}

	@Override
	public void findViews() {
		super.findViews();
		initMap();
		mBack = findView(R.id.button_input_bg_back);
		mBack.setVisibility(View.VISIBLE);
		mBack.setOnClickListener(new backClick(new MapListGPSFragment()));
		mImageButtonBack.setVisibility(View.VISIBLE);
		mImageButtonBack.setBackgroundResource(R.drawable.my_button_back);
		mButtonOpenMap = findView(R.id.button_open_map);
		mLlayoutMap = findView(R.id.LLayout_map);
		mButton = findView(R.id.button_stop_running);
		mTextViewTimer = findView(R.id.startmap_timer);
		mTextViewkilometre = findView(R.id.textview_kilometre);
		mTextViewSpeed = findView(R.id.textview_speed);
		mTextViewAltitede = findView(R.id.textview_altitude);
		mTextViewCal= findView(R.id.textview_cal);
		mButtonMoveToLocation = findView(R.id.button_get_location);
		mButtonMoveToLocation.setOnClickListener(this);
		mLinearBottom = findView(R.id.linearlayout_null);
		mLinearBottom.setVisibility(View.GONE);
		if(mGpsListInfo.getIsUpload() == 1){
			mRLayoutGetHistoryData = findView(R.id.imageButton_title_add);
			mRLayoutGetHistoryData.setVisibility(View.VISIBLE);
			mRLayoutGetHistoryData.setOnClickListener(this);
			mRLayoutGetHistoryData.findViewById(R.id.textview_title_add).setVisibility(View.INVISIBLE);
			mRLayoutGetHistoryData.setBackgroundResource(R.drawable.img_upload_gps);
			mRLayoutGetHistoryData.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						mRLayoutGetHistoryData.setBackgroundResource(R.drawable.img_upload_gps_on);
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						mRLayoutGetHistoryData.setBackgroundResource(R.drawable.img_upload_gps);
					}
					return false;
				}
			});
		}
		
		
		mButtonOpenMap.setOnClickListener(this);
		mTextViewTitle.setText("轨迹");
		mStartItemOverlay = new MyOverlay(getResources().getDrawable(R.drawable.img_map_start), mMapView);// 节点图层
		mMapView.getOverlays().add(mStartItemOverlay);
		mEndItemOverlay = new MyOverlay(getResources().getDrawable(R.drawable.img_map_end), mMapView);// 节点图层
		mMapView.getOverlays().add(mEndItemOverlay);
		if (graphicsOverlay == null) {
			graphicsOverlay = new GraphicsOverlay(mMapView);
			mMapView.getOverlays().add(graphicsOverlay);
		}
	}
	
	class backClick implements OnClickListener{
		
		BaseFragment to;

		public backClick(BaseFragment to) {
			super();
			this.to = to;
		}

		@Override
		public void onClick(View v) {
			MainCenterActivity fca = (MainCenterActivity) getActivity();
			fca.switchFragment((BaseFragment) fca.getContent(),to);
		}
		
	}
	
	private void initMap() {
		mMapView = (MapView) findView(R.id.map_start);
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
//		mMapController.setZoom(15);
		mMapController.setRotationGesturesEnabled(true);
		// 缩放手势
		mMapController.setZoomGesturesEnabled(true);
		// 双击方大
		mMapView.setDoubleClickZooming(true);
		mMapView.showScaleControl(true);
		/**
		 * 是否启用平移手势
		 */
		mMapController.setScrollGesturesEnabled(true);
	}
	
	class MyOverlay extends ItemizedOverlay<OverlayItem> {

		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {
			// TODO Auto-generated method stub
			return super.onTap(arg0, arg1);
		}

		@Override
		protected boolean onTap(int arg0) {
			// TODO Auto-generated method stub
			return super.onTap(arg0);
		}

		public MyOverlay(Drawable arg0, MapView arg1) {
			super(arg0, arg1);
		}
		
	}
	
	@Override
	public void onResume() {
		if(mMapView != null){
			mMapView.onResume();
		}
		new Thread(){
			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendEmptyMessage(500);
			};
		}.start();
		super.onResume();
	}
	
	@Override
	public void onPause() {
		if(mMapView != null){
			mMapView.onPause();
		}
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		if(mMapView != null){
			mMapView.destroy();
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_open_map:
			openOrCloseMap();
			break;
		case R.id.button_get_location:
			Common.fitPoints(mAllPoints, mMapController);
			mMapView.refresh();
			break;
		case R.id.imageButton_title_add:
			uploadData();
			break;

		default:
			break;
		}
	}
	
	private void uploadData() {
		mTask = new UpLoadAsyncTask();
		mTask.execute();
	}
	
	class UpLoadAsyncTask extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPreExecute() {
			ShowProgressDialog.showProgressDialog("正在上传", getActivity());
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			if(mGpsInfoArr == null){
				return -1;
			}else{
				List<GPSListInfo> gpsListInfos = new ArrayList<GPSListInfo>();
				gpsListInfos.add(mGpsListInfo);
				return UploadUtil.upload(mActivity,gpsListInfos);
			}
			
		}

		@Override
		protected void onPostExecute(Integer result) {
			handler.sendEmptyMessage(result);
			super.onPostExecute(result);
		}
	}
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			ShowProgressDialog.dismiss();
			switch (msg.what) {
			case R.string.MESSAGE_UPLOAD_GPS_SUCCESS:
				BaseToast("数据上传成功");
				break;
			case R.string.MESSAGE_GPS_NODATA:
				BaseToast("您还没有数据，赶快开启运动吧！");
				break;
			case R.string.MESSAGE_SYNCHRO_GPS_SUCCESS:
				BaseToast("数据同步成功");
				break;
			case 500:
				Logger.i("REF", " ref -----------");
				Common.fitPoints(mAllPoints, mMapController);
				mMapView.refresh();
				break;

			default:
				BaseToast("上传失败");
				if(mTask!=null&&mTask.isCancelled())
					mTask.cancel(true);
				break;
			}
		};
	};
	private List<GpsInfoDetail> mGpsInfoArr;
	
	private void openOrCloseMap() {
		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) mLlayoutMap.getLayoutParams();
		if (mOpenMap) {
            mButtonOpenMap.setBackgroundResource(R.drawable.button_openmap);
			mOpenMap = false;
			 linearParams.weight = 1.0f;
			 linearParams.setMargins(20, 10, 20, 10);
			 mLlayoutMap.setLayoutParams(linearParams);
		} else {
            mButtonOpenMap.setBackgroundResource(R.drawable.button_closemap);
			mOpenMap = true;
			linearParams.weight = 0.0f;
			// 展开
			linearParams.setMargins(0, 0, 0, 0);
			// Animation animation = AnimationUtils.loadAnimation(mActivity,
			// R.anim.map_open_anim);
			// mLlayoutMap.setAnimation(animation);
			mLlayoutMap.setLayoutParams(linearParams);
		}
	}
}
