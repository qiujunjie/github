package cmcc.mhealth.basic;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import cmcc.mhealth.errorhandler.CrashHandler;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;

public class MapApplication extends Application {

	private static MapApplication mInstance = null;
	public boolean m_bKeyRight = true;
	public BMapManager mBMapManager = null;

//	 public static final String strKey = "ByBGoDGhjFZrkfKRbHTxEwel";//����
	public static final String strKey = "OXREGXM59xj5jaeSExwHviIG";// bebug

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		initEngineManager(this);
		CrashHandler crashHandler = CrashHandler.getInstance();
		// ע��crashHandler
		crashHandler.init(getApplicationContext());
		// ������ǰû���͵ı���(��ѡ)
		// crashHandler.sendPreviousReportsToServer();
	}

	public void initEngineManager(Context context) {
		if (mBMapManager == null) {
			mBMapManager = new BMapManager(context);
		}

		if (!mBMapManager.init(strKey, new MyGeneralListener())) {
			Toast.makeText(MapApplication.getInstance().getApplicationContext(), "BMapManager ��ʼ������", Toast.LENGTH_LONG)
					.show();
		}
	}

	public static MapApplication getInstance() {
		return mInstance;
	}

	public static class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				Toast.makeText(MapApplication.getInstance().getApplicationContext(), "���������������", Toast.LENGTH_LONG)
						.show();
			} else if (iError == MKEvent.ERROR_NETWORK_DATA) {
				Toast.makeText(MapApplication.getInstance().getApplicationContext(), "������ȷ�ļ���������", Toast.LENGTH_LONG)
						.show();
			}
			// ...
		}

		@Override
		public void onGetPermissionState(int iError) {
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				Toast.makeText(MapApplication.getInstance().getApplicationContext(), "��������ȷ����ȨKey��", Toast.LENGTH_LONG)
						.show();
				MapApplication.getInstance().m_bKeyRight = false;
			}
		}
	}
}