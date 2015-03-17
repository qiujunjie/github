package cmcc.mhealth.basic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cmcc.mhealth.R;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class BaseMapFragment extends BaseFragment {

	public MapView mMapView;
	public MapController mMapController;
	public MyLocationOverlay myLocationOverlay;
	public LocationData mLocData;
	public LocationClient mLocClient;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return container;
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mLocClient!=null)
			mLocClient.stop();
	}
	
	protected  void initMap(MapView mMapView){
		/**
		 * ��ȡ��ͼ������
		 */
		mMapController = mMapView.getController();
		/**
		 * ���õ�ͼ�Ƿ���Ӧ����¼� .
		 */
		mMapController.enableClick(true);
		/**
		 * ��ʾ�������ſؼ�
		 */
		mMapView.setBuiltInZoomControls(false);
		/**
		 * �趨��ͼ���ĵ�
		 */
		GeoPoint p = new GeoPoint((int) (39.933859 * 1E6), (int) (116.400191 * 1E6));
		// ��λ��ͼ��
		myLocationOverlay = new MyLocationOverlay(mMapView);
		mLocData = new LocationData();
		mMapView.getOverlays().add(myLocationOverlay);
		mMapController.setCenter(p);
		mLocClient = new LocationClient(mActivity);
		setLocationOption();
	}
	
	protected void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setPoiExtraInfo(true);
		option.setAddrType("all");
		option.setScanSpan(30000);
		option.setPriority(LocationClientOption.NetWorkFirst);
		option.setPoiNumber(10);
		option.disableCache(true);
		mLocClient.setLocOption(option);
	}
	@Override
	public void findViews() {
		
	}
	@Override
	public void clickListner() {
		
	}
	@Override
	public void loadLogic() {
		
	}

}
