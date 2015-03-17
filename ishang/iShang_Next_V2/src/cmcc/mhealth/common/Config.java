/********************************************
 * 文件名		：Config.java
 * 版本信息	：1.00
 * 作者		：Gaofei - 高飞
 * 创建日期	：2013-1-8
 * 修改日期	：2013-3-12
 * 功能描述	：
 * 
 * CopyRight(c) China Mobile 2013   
 * 版权所有   All rights reserved
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
* 项目名称：iShangTrunk   
* 类名称：Config   
* 类描述：   
* 创建人：Gaofei - 高飞   
* 创建时间：2012-1-8 下午6:49:14   
* 修改人：Gaofei - 高飞   
* 修改时间：2012-1-8 下午6:49:14   
* 修改备注：   
* @version    
*    
*/
public class Config {
	private static final String TAG = SharedPreferredKey.SHARED_NAME;
	
	//自动更新配置常量参数	
	public static final String UPDATE_SERVER = "http://mhealth.cmri.cn/iactivity/app/"; //下载目录
	public static final String RACE_PIC_SERVER_ROOT = "http://mhealth.cmri.cn/gddgsns/"; //竞赛图下载目录
	public static final String RACE_TITLE_PIC = "image/cimg/";//temp竞赛图目录
	public static final String SERVER_DESTINY = "http://mhealth.cmri.cn/iactivity/app/"; //下载目录
//  public static final String UPDATE_SERVER = "http://mhealth.cmri.cn/sport/app/"; //下载目录
//  public static final String UPDATE_SERVER = "http://10.1.5.191/sport/app/"; //下载目录

//	public static final String UPDATE_SERVER = "http://218.206.179.71/jk/app/";	//下载目录
	public static final String UPDATE_APKNAME = "iShang.apk";					//下载文件名
	public static final String UPDATE_VERJSON = "versioninfo.json";				//查询文本 uft-8
	public static final String SERVER_LIST = "serverlist.json";
	public static final String CONTACT_LIST = "contact_cmri.json";
	public static final String UPDATE_SAVENAME = "updateapksamples.apk";		//保存文件名称
	
	
	public static final String DATAS_URL = Environment.getExternalStorageDirectory() + "/ishang_image/Datas.txt";// +MD5.getMD5(url));
	public static final String RECORD_URL = Environment.getExternalStorageDirectory() + "/ishang_image/CallSms.txt";// +MD5.getMD5(url));
	public static final String ERRORLOG_URL = Environment.getExternalStorageDirectory() + "/ishang_image/Errorlogs/";// +MD5.getMD5(url));
	
	
	/**   
	* getVerCode(获取整数短版本号) 
	* @param  	Context  上下文   
	* @return 	int 整数短版本号   
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
	* getVerName(获取详细版本号：1.1.0.130313) 
	* @param  	Context  上下文   
	* @return 	String 详细版本号   
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
	* getAppName(获取程序名：1.1.0.130313) 
	* @param  	Context  上下文   
	* @return 	int 程序名   
	*/
	public static String getAppName(Context context) {
		String verName = context.getResources().getText(R.string.app_name)
				.toString();
		return verName;
	}     
}
