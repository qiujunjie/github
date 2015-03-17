package cmcc.mhealth.net;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class NetworkTool {
	@SuppressWarnings("unused")
	private static final String TAG = "NetworkTool";

	public final static int NONE = 0;// ������
	public final static int WIFI = 1;// Wi-Fi
	public final static int MOBILE = 2;// 3G,GPRS

	/**
	 * ��ȡ��ַ����
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	// public static String getContent(String url) throws Exception {
	// StringBuilder sb = new StringBuilder();
	//
	// HttpClient client = new DefaultHttpClient();
	// HttpParams httpParams = client.getParams();
	// // �������糬ʱ����
	// HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
	// HttpConnectionParams.setSoTimeout(httpParams, 5000);
	// HttpResponse response = client.execute(new HttpGet(url + "?x="
	// + Math.random()));
	// HttpEntity entity = response.getEntity();
	// if (entity != null) {
	// BufferedReader reader = new BufferedReader(new InputStreamReader(
	// entity.getContent(), "UTF-8"), 8192);
	//
	// String line = null;
	// while ((line = reader.readLine()) != null) {
	// sb.append(line + "\n");
	// }
	// reader.close();
	// }
	// return sb.toString();
	// }

	public static boolean isOnline(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}

	/**
	 * ��ȡ����״�� 0 ������ 1 wifi 2 3G/GPS
	 * 
	 * @param context
	 * @return qjj 2012-12-12
	 */
	public static int getNetworkState(Context context) {
		if (context != null) {
			ConnectivityManager connManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			// �ֻ������ж�
			State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
			if (state == State.CONNECTED || state == State.CONNECTING) {
				return MOBILE;
			}
			// Wifi�����ж�
			state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
			if (state == State.CONNECTED || state == State.CONNECTING) {
				return WIFI;
			}
		}
		return NONE;
	}

	public static int getNetworkStateConnected(Context context) {
		if (context != null) {
			ConnectivityManager connManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			// �ֻ������ж�
			State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
			if (state == State.CONNECTED) {
				return MOBILE;
			}
			// Wifi�����ж�
			state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
			if (state == State.CONNECTED) {
				return WIFI;
			}
		}
		return NONE;
	}

	/**
	 * �ж�GPS�Ƿ���
	 * 
	 * @param context
	 * @return true ��ʾ����
	 */
	public static final boolean isGPSOPen(final Context context) {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		// ͨ��GPS���Ƕ�λ����λ������Ծ�ȷ���֣�ͨ��24�����Ƕ�λ��������Ϳտ��ĵط���λ׼ȷ���ٶȿ죩
		boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (gps) {
			return true;
		}
		return false;
	}

}
