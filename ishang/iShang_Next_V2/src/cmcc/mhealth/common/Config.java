/********************************************
 * �ļ���		��Config.java
 * �汾��Ϣ	��1.00
 * ����		��Gaofei - �߷�
 * ��������	��2013-1-8
 * �޸�����	��2013-3-12
 * ��������	��
 * 
 * CopyRight(c) China Mobile 2013   
 * ��Ȩ����   All rights reserved
 *******************************************/
package cmcc.mhealth.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.util.Log;
import cmcc.mhealth.R;
/**   
*    
* ��Ŀ���ƣ�iShangTrunk   
* �����ƣ�Config   
* ��������   
* �����ˣ�Gaofei - �߷�   
* ����ʱ�䣺2012-1-8 ����6:49:14   
* �޸��ˣ�Gaofei - �߷�   
* �޸�ʱ�䣺2012-1-8 ����6:49:14   
* �޸ı�ע��   
* @version    
*    
*/
public class Config {
	private static final String TAG = SharedPreferredKey.SHARED_NAME;
	
	//�Զ��������ó�������	
	public static final String UPDATE_SERVER = "http://mhealth.cmri.cn/iactivity/app/"; //����Ŀ¼
	public static final String RACE_PIC_SERVER_ROOT = "http://mhealth.cmri.cn/gddgsns/"; //����ͼ����Ŀ¼
	public static final String RACE_TITLE_PIC = "image/cimg/";//temp����ͼĿ¼
	public static final String SERVER_DESTINY = "http://mhealth.cmri.cn/iactivity/app/"; //����Ŀ¼
//  public static final String UPDATE_SERVER = "http://mhealth.cmri.cn/sport/app/"; //����Ŀ¼
//  public static final String UPDATE_SERVER = "http://10.1.5.191/sport/app/"; //����Ŀ¼

//	public static final String UPDATE_SERVER = "http://218.206.179.71/jk/app/";	//����Ŀ¼
	public static final String UPDATE_APKNAME = "iShang.apk";					//�����ļ���
	public static final String UPDATE_VERJSON = "versioninfo.json";				//��ѯ�ı� uft-8
	public static final String SERVER_LIST = "serverlist.json";
	public static final String CONTACT_LIST = "contact_cmri.json";
	public static final String UPDATE_SAVENAME = "updateapksamples.apk";		//�����ļ�����
	
	
	public static final String DATAS_URL = Environment.getExternalStorageDirectory() + "/ishang_image/Datas.txt";// +MD5.getMD5(url));
	public static final String RECORD_URL = Environment.getExternalStorageDirectory() + "/ishang_image/CallSms.txt";// +MD5.getMD5(url));
	public static final String ERRORLOG_URL = Environment.getExternalStorageDirectory() + "/ishang_image/Errorlogs/";// +MD5.getMD5(url));
	
	
	/**   
	* getVerCode(��ȡ�����̰汾��) 
	* @param  	Context  ������   
	* @return 	int �����̰汾��   
	*/
	public static int getVerCode(Context context) {
		int verCode = -1;
		if (context != null) {
			try {
				PackageInfo info = context.getPackageManager().getPackageInfo(
						"cmcc.mhealth", 0);
				if (info != null)
					verCode = info.versionCode;
			} catch (NameNotFoundException e) {
				Log.e(TAG, e.getMessage());
			}
		}
		return verCode;
	}
	
	/**   
	* getVerName(��ȡ��ϸ�汾�ţ�1.1.0.130313) 
	* @param  	Context  ������   
	* @return 	String ��ϸ�汾��   
	*/
	public static String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(
					"cmcc.mhealth", 0).versionName;
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage());
		}
		return verName;	

	}
	

	/**   
	* getAppName(��ȡ��������1.1.0.130313) 
	* @param  	Context  ������   
	* @return 	int ������   
	*/
	public static String getAppName(Context context) {
		String verName = context.getResources().getText(R.string.app_name)
				.toString();
		return verName;
	}     
}
