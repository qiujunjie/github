package cmcc.mhealth.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cmcc.mhealth.R;
import cmcc.mhealth.activity.MapStartRunningFragment;
import cmcc.mhealth.activity.PreLoadActivity;
import cmcc.mhealth.bean.GpsInfoDetail;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Constants;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.common.PreferencesUtils;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.db.MHealthProviderMetaData;
import cmcc.mhealth.slidingcontrol.MainCenterActivity;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

public class StepService extends Service implements BDLocationListener {
	protected static final int TIME_CHANGE = 0;
	private static final String TAG = "StepService";
	protected static final int NOTIFY_ID = 1;
	boolean threadDisable;
	private Binder mbinder = new myBind();
	private LocationClient mLocClient;
	private Timer mTimer;
	private TimerTask mTask = null;
	// private List<GpsInfoDetail> mListInfo;
	private GpsInfoDetail mGpsInfoDetail;
	private String mGender;
	private String mAge;
	private String mHeight;
	private String mWeight;
	public int mCount;
	private String mStartTime;
	private float BMR;
	private NotificationManager mNotificationManager; 

	// private int mType = -1;

	@Override
	public void onCreate() {
		mGender = PreferencesUtils.getString(this, SharedPreferredKey.GENDER, "35");
		mAge = PreferencesUtils.getString(this, SharedPreferredKey.BIRTHDAY, "1980");
		int born = Integer.valueOf(String.valueOf(mAge).substring(0, 4));
		int now = Integer.valueOf(Common.getDate2Time(new Date(), "yyyy"));
		int age = now - born;
		mHeight = PreferencesUtils.getString(this, SharedPreferredKey.HEIGHT, "165");
		mHeight = mHeight.split("\\.")[0];
		mWeight = PreferencesUtils.getString(this, SharedPreferredKey.WEIGHT, "55");
		mWeight = mWeight.split("\\.")[0];
		BMR = Common.calBMR(Common.String2Int(mGender), age, Common.String2Int(mHeight), Common.String2Int(mWeight));
		System.out.println("StepService onCreate ");
		mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//		shownewNotifition();
		oldNotification();
		super.onCreate();
	}

	@Override
	public void onLowMemory() {
		Logger.d(TAG, "stepservice onLowMemory");
		super.onLowMemory();
	}
	
	/**
     * Show a notification while this service is running.
     */
	private void shownewNotifition(){
		RemoteViews views = new RemoteViews(getPackageName(), R.layout.notification_gps);
		Intent intent = new Intent(this, MainCenterActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,intent,0);
		mNotification = new Notification.Builder(this)
		.setContentTitle("�����˶�")
		.setContentIntent(contentIntent)
		.setSmallIcon(R.drawable.i_shang)
		.setWhen(System.currentTimeMillis())
		.setAutoCancel(false)
		.setContent(views)
		.getNotification();
		startForeground(NOTIFY_ID, mNotification);
	}
	
	private void oldNotification(){
        notification = new Notification(R.drawable.i_shang,"�����˶�", System.currentTimeMillis()); 
        //FLAG_AUTO_CANCEL   ��֪ͨ�ܱ�״̬���������ť�������
        //FLAG_NO_CLEAR      ��֪ͨ���ܱ�״̬���������ť�������
        //FLAG_ONGOING_EVENT ֪ͨ��������������
        //FLAG_INSISTENT     �Ƿ�һֱ���У���������һֱ���ţ�֪���û���Ӧ
        notification.flags |= Notification.FLAG_ONGOING_EVENT; // ����֪ͨ�ŵ�֪ͨ����"Ongoing"��"��������"����   
        notification.flags |= Notification.FLAG_NO_CLEAR; // �����ڵ����֪ͨ���е�"���֪ͨ"�󣬴�֪ͨ�������������FLAG_ONGOING_EVENTһ��ʹ��   
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;   
        //DEFAULT_ALL     ʹ������Ĭ��ֵ�������������𶯣������ȵ�
        //DEFAULT_LIGHTS  ʹ��Ĭ��������ʾ
        //DEFAULT_SOUNDS  ʹ��Ĭ����ʾ����
        //DEFAULT_VIBRATE ʹ��Ĭ���ֻ��𶯣������<uses-permission android:name="android.permission.VIBRATE" />Ȩ��
        notification.defaults = Notification.DEFAULT_LIGHTS; 
        //����Ч������
        //notification.defaults=Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND;
        notification.ledARGB = Color.BLUE;   
        notification.ledOnMS =5000; //����ʱ�䣬����
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.notification_gps);
        notification.contentView = views;
        // ����֪ͨ���¼���Ϣ   
//        CharSequence contentTitle ="�����˶�"; // ֪ͨ������   
//        CharSequence contentText ="����������: 0��"; // ֪ͨ������   
        Intent notificationIntent =new Intent(this, MainCenterActivity.class); // �����֪ͨ��Ҫ��ת��Activity   
        PendingIntent contentItent = PendingIntent.getActivity(this, 0, notificationIntent, 0);   
        notification.contentIntent = contentItent;
        // ��Notification���ݸ�NotificationManager   
        mNotificationManager.notify(NOTIFY_ID, notification);  
        startForeground(NOTIFY_ID, notification);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Logger.d(TAG, "stepservice onStartCommand");
//		intent.putExtra("restart", mCount);
		return super.onStartCommand(intent, START_REDELIVER_INTENT, startId);
	}

	public interface OnMyLocationChangeListener {
		void change(GpsInfoDetail GPSInfo);

		void timer(String timer);
		
		void changeAltitude(double altitude);
	}

	public void registerCallback(OnMyLocationChangeListener cb) {
		ICallback = cb;
		location();
	}

	// public void getType(String type){
	// if(!TextUtils.isEmpty(type) && type.equals("����"))
	// mType = 1;
	// else if(!TextUtils.isEmpty(type) && type.equals("����")){
	// mType = 3;
	// }else if(!TextUtils.isEmpty(type) && type.equals("�ܲ�")){
	// mType = 2;
	// }
	// }

	public void unRegisterCallback(OnMyLocationChangeListener cb) {
		ICallback = cb;
		ICallback = null;
	}

	OnMyLocationChangeListener ICallback;
	private LocationManager manager;

	private void location() {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		// ������Ҫ��ȡ���η�������
		criteria.setAltitudeRequired(true);
		criteria.setBearingRequired(false);
		// ������������ʷ�
		criteria.setCostAllowed(true);
		// Ҫ��ͺĵ�
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(this);
		setLocationOption();
		mLocClient.start();
		
		getaltitude();
	}

	private void getaltitude() {
		manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		// ����һ��criteria����
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		// ������Ҫ��ȡ���η�������
		criteria.setAltitudeRequired(true);
//		criteria.setBearingRequired(false);
		// ������������ʷ�
		criteria.setCostAllowed(true);
		// Ҫ��ͺĵ�
		criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
		String provider = manager.getBestProvider(criteria, true);
//		GpsStatus status = GpsStatus.GPS_EVENT_SATELLITE_STATUS
//		manager.getGpsStatus(status)
		Log.i("a----------", "we choose " + provider);
		// ��Ҫ�������������ݲ���
		manager.requestLocationUpdates(provider, 1000, 10,new MyGPSLinster());		
	}
	
	private class MyGPSLinster implements LocationListener {
		StringBuilder builder = new StringBuilder();
		// �û�λ�øı��ʱ�� �Ļص�����
		public void onLocationChanged(Location location) {
			if(location != null && ICallback != null){
				// location
//				// ��ȡ���û���γ��
//				double latitude = location.getLatitude();
//				double longitude = location.getLongitude();
				double alitude = location.getAltitude();
				Editor  editor =  getSharedPreferences("config", 0).edit();
	            editor.putString("lastlocation", alitude+"");
	            editor.commit();
	            ICallback.changeAltitude(alitude);
//				builder.append(latitude).append(longitude).append(alitude);
//				TextView textView = (TextView) findViewById(R.id.textview);
//				textView.setText(builder.toString());
				Log.e("----------", "alitude = " +alitude /*+" latitude = "+latitude +" longitude" +longitude*/);
			}
			
		}
		// ״̬�ı�
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
		// gps ,��
		public void onProviderEnabled(String provider) {
		}

		// �ر�
		public void onProviderDisabled(String provider) {
		}
	}

	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setPoiExtraInfo(true);
		option.setAddrType("all");
		option.setScanSpan(30000);
		option.setPriority(LocationClientOption.GpsFirst);
		option.setPoiNumber(10);
		option.disableCache(true);
		mLocClient.setLocOption(option);
	}

	@Override
	public IBinder onBind(Intent intent) {
		System.out.println("StepService onBind ");
		return mbinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}

	@Override
	public void onDestroy() {
		Logger.d(TAG, "stepservice onDestroy");
		stopTimer();
		if(mNotificationManager != null)
			mNotificationManager.cancel(0);
		if (mLocClient != null) {
			mLocClient.unRegisterLocationListener(this);
			mLocClient.stop();
		}
		super.onDestroy();
	}

	public class myBind extends Binder {
		public StepService getService() {
			return StepService.this;
		}
	}

	double[][] mdoublePoints = { { 39.903517, 116.359538 }, { 39.904084, 116.36221 }, { 39.90499, 116.365067 },
			{ 39.905136, 116.367277 }, { 39.905336, 116.369891 }, { 39.905519, 116.369769 } };
	private boolean bFirstChange = true;
	BDLocation mLocation = null;
	private List<String> mListInfo = new ArrayList<String>();
	GeoPoint mOldGeoPoint;
	private Notification mNotification;
	double mDistances = 0;
	private Notification notification;

	@Override
	public void onReceiveLocation(BDLocation location) {
		if (location == null)
			return;
		// ============filter======================
		// 0�����ж�
		if (Math.abs(((int) (location.getLatitude() * 1e6))) == 0
				|| Math.abs((int) ((location.getLongitude() * 1e6))) == 0)
			return;
		// ֻ����������
		if ((location.getLatitude() + "").contains("E") || (location.getLongitude() + "").contains("e"))
			return;
		if (bFirstChange) {// �״ο�ʼ��ʱ
			startTimer();
			mStartTime = Common.getDate2Time(new Date(), Common.COMMON_DATE_YYYY_MM_DD_MID_CREATETIME);
			PreferencesUtils.putString(getApplicationContext(), SharedPreferredKey.START_TIME, mStartTime);
			bFirstChange = false;
		}
		if (mLocation != null && location.getLatitude() == mLocation.getLatitude()
				&& location.getLongitude() == mLocation.getLongitude()) {
			// �����������ͬ
			Logger.i(TAG, "location λ��û��");
//			Toast.makeText(this, "λ��û��", 0).show();
			return;
		}
		//
		// if (mListInfo.size() != 0 && mPoint != null) {
		// double distance = DistanceUtil.getDistance(mPoint, new GeoPoint((int)
		// (location.getLatitude() * 1e6), (int) (location.getLongitude() *
		// 1e6)));
		// if (distance < 10) {
		// // ����С��10��
		// Toast.makeText(this, "����10��", 0).show();
		// return;
		// }
		//
		// }
		// ============filter======================

		System.out.println("change");
		// StringBuffer sb = new StringBuffer(256);
		// mPoints.add(mPoint);
		mGpsInfoDetail = new GpsInfoDetail();
		// =======================Test======================
		// if (mListInfo.size() == 0) {
		// mGpsInfoDetail.setLatitude(location.getLatitude());
		// mGpsInfoDetail.setLongitude(location.getLongitude());
		// } else if (mListInfo.size() == 1) {
		// mGpsInfoDetail.setLatitude(mdoublePoints[0][0]);
		// mGpsInfoDetail.setLongitude(mdoublePoints[0][1]);
		// } else if (mListInfo.size() == 2) {
		// mGpsInfoDetail.setLatitude(mdoublePoints[1][0]);
		// mGpsInfoDetail.setLongitude(mdoublePoints[2][1]);
		// } else if (mListInfo.size() == 3) {
		// mGpsInfoDetail.setLatitude(mdoublePoints[2][0]);
		// mGpsInfoDetail.setLongitude(mdoublePoints[2][1]);
		// } else if (mListInfo.size() == 4) {
		// mGpsInfoDetail.setLatitude(mdoublePoints[3][0]);
		// mGpsInfoDetail.setLongitude(mdoublePoints[3][1]);
		// } else if (mListInfo.size() == 5) {
		// mGpsInfoDetail.setLatitude(mdoublePoints[4][0]);
		// mGpsInfoDetail.setLongitude(mdoublePoints[4][1]);
		// } else if (mListInfo.size() == 6) {
		// mGpsInfoDetail.setLatitude(mdoublePoints[5][0]);
		// mGpsInfoDetail.setLongitude(mdoublePoints[5][1]);
		// }else{
		// mGpsInfoDetail.setLatitude(location.getLatitude());
		// mGpsInfoDetail.setLongitude(location.getLongitude());
		// }
		// mListInfo.add("1");
		// =======================Test======================
		mGpsInfoDetail.setLatitude(location.getLatitude());
		mGpsInfoDetail.setLongtitude(location.getLongitude());
//		double altitude = location.getAltitude();
//		mGpsInfoDetail.setAlitude((altitude + "").contains("E") ? 0 : altitude);// ����
		// mGpsInfoDetail.setSpeed(location.getSpeed());// ʱ��
		mGpsInfoDetail.setStarttime(mStartTime);
		mGpsInfoDetail.setDetailtime(Common.getDate2Time(new Date(), Common.COMMON_DATE_YYYY_MM_DD_MID_CREATETIME));
		GeoPoint newpoint = new GeoPoint((int) (mGpsInfoDetail.getLatitude() * 1e6),
				(int) (mGpsInfoDetail.getLongtitude() * 1e6));
		double distance = 0;
		if (mLocation != null) {
			mOldGeoPoint = new GeoPoint((int) (mLocation.getLatitude() * 1e6), (int) (mLocation.getLongitude() * 1e6));
			distance = DistanceUtil.getDistance(mOldGeoPoint, newpoint);
//			distance = distance == 0? distance : (Math.round(distance));
			mGpsInfoDetail.setDistance(distance);
			if(notification!=null){
				mDistances += distance;
				RemoteViews contentView = notification.contentView;
				if(mDistances/1000f>10){
	                contentView.setTextViewText(R.id.notifition_distance,String.format("%.1f", mDistances/1000f));				    
				}else{
	                contentView.setTextViewText(R.id.notifition_distance,String.format("%.2f", mDistances/1000f));
				}
				mNotificationManager.notify(NOTIFY_ID, notification);
			}
		} else {
			mGpsInfoDetail.setDistance(0);
		}
		if (Math.abs(distance) != 0) {
			double sd = distance/1000/ mCount;// km/s
			double speed = sd * 3600;// km/h
//			speed = (Math.round(speed));
			mGpsInfoDetail.setSpeed((float) speed);
			float cal = Common.calRunCalorie(BMR, (float) speed, mCount, Constants.RUNTYPE);
//			cal = cal == 0? cal : (Math.round(cal));
			mGpsInfoDetail.setCal(cal);
			Logger.e(TAG, "distance = " + distance + " speed = " + speed + " cal = " + cal);
		}
		// mListInfo.add(mGpsInfoDetail);
		MHealthProviderMetaData.GetMHealthProvider(this).insertDetail(mGpsInfoDetail);
		// if (location.getLocType() == BDLocation.TypeGpsLocation) {
		// sb.append("\nspeed : ");
		// sb.append(location.getSpeed());
		// sb.append("\nsatellite : ");
		// sb.append(location.getSatelliteNumber());
		// } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
		// sb.append("\naddr : ");
		// sb.append(location.getAddrStr());
		// }
		if (ICallback != null) {
			ICallback.change(mGpsInfoDetail);
			// ICallback.locationInfo(sb.toString());
		}
		mLocation = location;
		// mGeoPoint = geoPoint1;
	}

	public void requeatNotify() {
		if (mLocClient != null) {
			mLocClient.requestNotifyLocation();
		}
	}

	public void stopTimer() {
		if (null != mTimer) {
			mTask.cancel();
			mTask = null;
			mTimer.cancel(); // Cancel timer
			mTimer.purge();
			mTimer = null;
			// mHandler.removeMessages(mMessage.what);
		}
		if (mLocClient != null) {
			mLocClient.unRegisterLocationListener(this);
			mLocClient.stop();
		}
	}

	public void startTimer() {
		if (null == mTimer) {
			if (null == mTask) {
				mTask = new TimerTask() {

					@Override
					public void run() {
						mCount++;
						String time = Common.sec2Time(mCount);
						Logger.i(TAG, time);
						if (ICallback != null) {
							ICallback.timer(time);
						}
//						int totalSec = 0;
//						totalSec = (int) (mCount);
//						// Set time display
//						int hor = (totalSec / 3600);
//						int min = ((totalSec % 3600) / 60);
//						int sec = (totalSec % 60);
//						if (ICallback != null) {
//							ICallback.timer(String.format("%1$02d:%2$02d:%3$02d", hor, min, sec));
//						}
//						Logger.d(TAG, String.format("%1$02d:%2$02d", min, sec));
					}
				};
			}

			mTimer = new Timer(true);
			mTimer.schedule(mTask, 1000, 1000); // set timer duration
		}
		if (!mLocClient.isStarted()) {
			mLocClient.registerLocationListener(this);
			mLocClient.start();
		}
	}

	public GeoPoint getGeopoint(double lat, double lte) {
		return new GeoPoint((int) (lat * 1E6), (int) (lte * 1E6));
	}

	@Override
	public void onReceivePoi(BDLocation arg0) {
		// TODO Auto-generated method stub

	}

}
