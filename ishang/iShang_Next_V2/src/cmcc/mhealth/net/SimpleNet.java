package cmcc.mhealth.net;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.common.SharedPreferredKey;

public class SimpleNet {

	/**
	 * ��ȡ�򵥵ķ���ֵ�������ϴ�����
	 * 
	 * @param phonenum
	 * @param pwd
	 * @param URL
	 * @return
	 */
	public static final int SIMPLENET_SUCCESS = 0;
	public static final int SIMPLENET_FAIL = 1;
	public static void simpleGet(String URL, Handler handler, Context context) {
		InputStream in = null;
		Message msg = new Message();
		Object obj = null;
		try {
			URL url = new URL(URL);
			URLConnection conn = url.openConnection();
			conn.connect();
			conn.setConnectTimeout(8000);
			if (NetworkTool.getNetworkState(context) != 0) {
				in = conn.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String res = reader.readLine();
				JSONObject jsonObject = new JSONObject(res);
				obj = jsonObject.get("status");
				if (obj != null && obj.equals(SharedPreferredKey.SUCCESS)) {
					msg.what = SIMPLENET_SUCCESS;
				} else {
					msg.what = SIMPLENET_FAIL;
					obj = jsonObject.get("reason");
				}
			}else{
				msg.what = SIMPLENET_FAIL;
				obj = "�����쳣,��鿴��������";
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg.what = SIMPLENET_FAIL;
			obj = "�����쳣";
			Logger.e("SimpleNet", e.getMessage());
		} finally {
			msg.obj = obj;
			handler.sendMessage(msg);
		}
	}
}
