package cmcc.mhealth.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.baidu.mapapi.map.MapController;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.google.gson.annotations.Until;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Common {
	public final static String TAG = Common.class.getSimpleName();
	public static final String COMMON_DATE_YYYYMMDD = "yyyyMMdd"; 
	private static final String COMMON_DATE_YYYY_MM_DD_MID = "yyyy-MM-dd"; 
	private static final String COMMON_DATE_YYYYdMMdDDdMID = "yyyy.MM.dd"; 
	private static final String COMMON_DATE_YYYY_M_D_DOT = "M.d"; 
	private static final String COMMON_DATE_YYYY_HH_mm_MID = "HH:mm"; 
	private static final String COMMON_DATE_H_m = "H时m分";  
	private static final String COMMON_DATE_M_D_CN = "M月d日"; 
	private static final String COMMON_DATE_YYYYMMDDHHmmss = "yyyyMMddHHmmss"; 
	private static final String COMMON_DATE_YYYYMMDDHH = "yyyyMMddHH"; 
	public static final String COMMON_DATE_YYYY_MM_DD_MID_CREATETIME = "yyyy-MM-dd HH:mm:ss"; 
	private static final String COMMON_DATE_YYYY_MM_DD_MID_SERVERTIME = "yyyy-MM-dd_HH:mm:ss"; 
	public static final String COMMON_DATE_HHmmss_SERVERTIME = "HH:mm:ss"; 

	public static long TIME_NUMBER = 1000L * 60 * 60 * 24;
	
	/**
	 * 判断存储卡是否存在
	 * 
	 * @return
	 */
	public static boolean existSDcard() {
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			return true;
		} else
			return false;
	}
	
	public static boolean isServiceRunning(Context context,String serviceName) {
		ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceName.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Data 转换 指定 格式
	 * @param date
	 * @param format
	 * @return
	 */
	public static String getDate2Time(Date date,String format){
		return new SimpleDateFormat(format).format(date);
	}

	/**
	 * 将秒转成时分秒
	 * 
	 * @param time
	 * @return
	 */
	public static String FormatTimeHHmmss(int seconds) {
		MyTime time = new MyTime(0, 0, 0, seconds);

		int hour = time.fields[MyTime.HOUR] + time.fields[MyTime.DAY] * 24;
		int minute = time.fields[MyTime.MINUTE];
		int second = time.fields[MyTime.SECOND];

		StringBuilder sb = new StringBuilder(16);
		sb.append(hour).append(":");
		if (minute < 10) {
			sb.append("0");
		}
		sb.append(minute).append(":");
		if (second < 10) {
			sb.append("0");
		}
		sb.append(second);

		return sb.toString();
	}

	/**
	 * 将数字转化成周一，周二等形式
	 * 
	 * @param weekNum
	 * @return
	 */
	public static String GetWeekStr(int weekNum) {
		String dayOfWeekStr = "";
		switch (weekNum) {
		case 0:
			dayOfWeekStr = "周日";
			break;
		case 1:
			dayOfWeekStr = "周一";
			break;
		case 2:
			dayOfWeekStr = "周二";
			break;
		case 3:
			dayOfWeekStr = "周三";
			break;
		case 4:
			dayOfWeekStr = "周四";
			break;
		case 5:
			dayOfWeekStr = "周五";
			break;
		case 6:
			dayOfWeekStr = "周六";
			break;
		default:
			break;
		}
		return dayOfWeekStr;
	}
	

	/**
	 * 将数字转化成周一，周二等形式,适用于Calendar获取的day of week
	 * 
	 * @param weekNum
	 * @return
	 */
	public static String TimeFormatter(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int dayinweek = cal.get(Calendar.DAY_OF_WEEK);
		String weekname = "";
		switch (dayinweek) {
		case 0:
			weekname = "周六";
			break;
		case 1:
			weekname = "周日";
			break;
		case 2:
			weekname = "周一";
			break;
		case 3:
			weekname = "周二";
			break;
		case 4:
			weekname = "周三";
			break;
		case 5:
			weekname = "周四";
			break;
		case 6:
			weekname = "周五";
			break;
		case 7:
			weekname = "周六";
			break;
		}

		return year + "-" + (month + 1) + "-" + day + " " + weekname;
	}

	public static long getDateTimeFromTime(long time) {

//		getDateAsYYYYMMDD(long time);
		return getDateFromYYYYMMDD(getDateAsYYYYMMDD(time));
		
//		time = time / TIME_NUMBER;
//
//		return time * TIME_NUMBER;
	}

	/**
	 * 时间格式转换
	 * 
	 * @return 20130109
	 */
	public static String getDateAsYYYYMMDD(long time) {
		String sysDatetime = new SimpleDateFormat(COMMON_DATE_YYYYMMDD).format(time);
		return sysDatetime;
	}
	

	/**   
	* getRankUpdateYYYYMMDD(获取需要更新的日期) 
	* 	a）6点前 刷前一天
	* 	b）6点后 刷当天
	* 	c）刷过同一天则返回 null
	* 	否则返回 刷新日期。
	* @param @return    刷新时间 20130713
	* 创建人：Gaofei - 高飞
	* 创建时间：2013-7-13 上午11:47:32   
	* 修改人：Gaofei - 高飞
	* 修改时间：2013-7-13 上午11:47:32
	* @since CodingExample　Ver(编码范例查看) 1.1   
	*/
	@SuppressWarnings("deprecation")
	public static String getRankUpdateYYYYMMDD() {
		
		Date mCurrentTime = new Date();
		String strYYYYMMDDNewUpdate;

		//6点前 刷前一天
		if (mCurrentTime.getHours() <= 5) {
			Calendar date = Calendar.getInstance();
			date.setTime(mCurrentTime);
			date.set(Calendar.DATE, date.get(Calendar.DATE) - 1);
			mCurrentTime = date.getTime();
		}
		
		strYYYYMMDDNewUpdate = Common.getDateAsYYYYMMDD(mCurrentTime.getTime());

		//刷新过 同一天 则不刷新
//		if (!TextUtils.isEmpty(strYYYYMMDDOldUpdate) && 
//				strYYYYMMDDOldUpdate.equals(strYYYYMMDDNewUpdate)) {
//			strYYYYMMDDNewUpdate = null;
//		}
		return strYYYYMMDDNewUpdate;
	}
	

  /**   
  * getRankUpdateYYYYMMDD(获取需要更新的日期) 
  *   a）6点前 刷前一天
  *   b）6点后 刷当天
  *   c）刷过同一天则返回 null
  *   否则返回 刷新日期。
  * @param @return    刷新时间 20130713
  * 创建人：Gaofei - 高飞
  * 创建时间：2013-7-13 上午11:47:32   
  * 修改人：Gaofei - 高飞
  * 修改时间：2013-7-13 上午11:47:32
  * @since CodingExample　Ver(编码范例查看) 1.1   
  */
  @SuppressWarnings("deprecation")
public static String getRankUpdateYYYYMMDD_Tmp(String mUpdateTime) {
    
    Date mCurrentTime = new Date();
    String strYYYYMMDDNewUpdate;

    //6点前 刷前一天
    if (mCurrentTime.getHours() <= 5) {
      Calendar date = Calendar.getInstance();
      date.setTime(mCurrentTime);
      date.set(Calendar.DATE, date.get(Calendar.DATE) - 1);
      mCurrentTime = date.getTime();
    }
    
    strYYYYMMDDNewUpdate = Common.getDateAsYYYYMMDD(mCurrentTime.getTime());

//    刷新过 同一天 则不刷新
    if (!TextUtils.isEmpty(mUpdateTime) && 
        mUpdateTime.equals(strYYYYMMDDNewUpdate)) {
      strYYYYMMDDNewUpdate = null;
    }
    return strYYYYMMDDNewUpdate;
  }
  
	/**   
	* isRankUpdate(是否更新群组排名数据，时间不为空，并且过了5点。) 
	* 1）刷新过 同一天 则不刷新
	* 2）其他都刷新
	* @param strYYYYMMDDOldUpdate 	上次刷新时间 20130713
	* @param strYYYYMMDDNewUpdate	准备刷新时间 20130713
	* 创建人：Gaofei - 高飞
	* 创建时间：2013-7-13 上午11:30:59   
	* 修改人：Gaofei - 高飞
	* 修改时间：2013-7-13 上午11:30:59
	* @since CodingExample　Ver(编码范例查看) 1.1   
	*/
	public static boolean isRankUpdate(String strYYYYMMDDOldUpdate, String strYYYYMMDDNewUpdate) {
		//刷新过 同一天 则不刷新
		if (!TextUtils.isEmpty(strYYYYMMDDOldUpdate) && 
				strYYYYMMDDOldUpdate.equals(strYYYYMMDDNewUpdate)) {
			return false;
		}else{
			return true;			
		}
	}
	/**
	 * 时间格式转换
	 * 
	 * @return 01.09 格式
	 */
	public static String getDateAsMMDD(long time) {
		String sysDatetime = new SimpleDateFormat(COMMON_DATE_YYYY_M_D_DOT).format(time);
		return sysDatetime;
	}

	/**
	 * 时间格式转换
	 * 
	 * @return 11:11 格式
	 */
	public static String getDateAsHHmm(long time) {
		String sysDatetime = new SimpleDateFormat(COMMON_DATE_YYYY_HH_mm_MID).format(time);
		return sysDatetime;
	}
	
	/**
	 * 时间格式转换
	 * 
	 * @param time
	 * @return 2月3日
	 */
	public static String getDateAsM_d(long time) {
		String sysDatetime = new SimpleDateFormat(COMMON_DATE_M_D_CN).format(time);
		return sysDatetime;
	}

	/**
	 * 时间格式转换 （2012-09-12）
	 * 
	 * @param timeStr
	 * @return 时间(long)
	 */
	public static long getDateFromStr(String timeStr) {
		Date timeDate = new Date();
		try {
			timeDate = new SimpleDateFormat(COMMON_DATE_YYYY_MM_DD_MID).parse(timeStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return timeDate.getTime();
	}
	public static String getCurrentDayLongTime(long time) {
		String sysDatetime = new SimpleDateFormat(COMMON_DATE_YYYY_MM_DD_MID).format(time);
		return sysDatetime;
	}
	
	public static long getCurrentDayFirstTimeMills(long nowtime){
		return getDateFromStr(getCurrentDayLongTime(nowtime));
	}
	public static long getDateFromStrDot(String timeStr) {
		Date timeDate = new Date();
		try {
			timeDate = new SimpleDateFormat(COMMON_DATE_YYYYdMMdDDdMID).parse(timeStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return timeDate.getTime();
	}
	public static String getCurrentDayLongTimeDot(long time) {
//		Date timeDate = null;
		String sysDatetime = new SimpleDateFormat(COMMON_DATE_YYYYdMMdDDdMID).format(time);
//		try {
//			timeDate = new SimpleDateFormat(COMMON_DATE_YYYY_MM_DD_MID).parse(sysDatetime);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
		return sysDatetime;
	}
	/**
	 * 时间格式转换 （2012-09-12）
	 * 
	 * @param timeStr
	 * @return 时间(long)
	 */
	public static long getDateFromStrFromServel(String timeStr) {
		Date timeDate = new Date();
		try {
			timeDate = new SimpleDateFormat(COMMON_DATE_YYYY_MM_DD_MID_SERVERTIME).parse(timeStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return timeDate.getTime();
	}

	/**
	 * 时间格式转换
	 * 
	 * @param time
	 * @return 2时3分
	 */
	public static String getDateAsH_m(long time) {
		String sysDatetime = new SimpleDateFormat(COMMON_DATE_H_m).format(time);
		//H_m_DateFormat.format(time);  
		return sysDatetime;
	}

	public static String Formatyyyy_MM_dd(String strDateyyyyMMdd)
			throws ParseException {

		Date dateTmp = new SimpleDateFormat(COMMON_DATE_YYYYMMDD).parse(strDateyyyyMMdd);
		String strDateyyyy_MM_dd = new SimpleDateFormat(COMMON_DATE_YYYY_MM_DD_MID).format(dateTmp);
		return strDateyyyy_MM_dd;
	}
	
	/**
	 * yyyy-MM-dd_HH:mm:ss
	 */
	public static String FormatCharDay()
			throws ParseException {
//		Date dateTmp = new SimpleDateFormat(COMMON_DATE_YYYY_MM_DD_MID_CREATETIME).parse(strDateyyyyMMdd);
//		String strDateyyyy_MM_dd = new SimpleDateFormat(COMMON_DATE_YYYY_MM_DD_MID).format(dateTmp);
		return new SimpleDateFormat(COMMON_DATE_YYYY_MM_DD_MID_CREATETIME).format(new Date());
	}

	public static long getDateFromYYYYMMDD(String time) {
		if (time == null || time.length() != 8)
			return 0;

		long sysDatetime = 0;
		try {//**
			sysDatetime = new SimpleDateFormat(COMMON_DATE_YYYYMMDD).parse(time).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sysDatetime;
	}

	public static long getDateFromYYYY_MM_DD(String time) {
		if (time == null || time.length() != 10)
			return 0;

		long sysDatetime = 0;
		try {
			sysDatetime = new SimpleDateFormat(COMMON_DATE_YYYY_MM_DD_MID).parse(time).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sysDatetime;
	}

	public static long getDateFromYYYYMMDDHHMMSS(String time) {
		if (time == null || time.length() != 14)
			return 0;

		long sysDatetime = 0;
		try {
			sysDatetime = new SimpleDateFormat(COMMON_DATE_YYYYMMDDHHmmss).parse(time).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sysDatetime;
	}

	public static long getDateFromYYYYMMDDHHMMSSCreateTime(String time) {
		if (time == null || time.length() != 19)
			return 0;

		long sysDatetime = 0;
		try {
			sysDatetime = new SimpleDateFormat(COMMON_DATE_YYYY_MM_DD_MID_CREATETIME).parse(time)
					.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sysDatetime;
	}

	/**
	 * 
	 * 时间格式转换
	 * 
	 * @param time
	 * @return 20120203 格式
	 * 
	 */
	public static String getYesterdayAsYYYYMMDD(long time) {
		time = time - 24 * 60 * 60 * 1000L;
		String sysDatetime = new SimpleDateFormat(COMMON_DATE_YYYYMMDD).format(time);
		return sysDatetime;
	}

	/**
	 * 时间格式转换
	 * 
	 * @return 20130109123312 格式
	 */
	public static String getDateAsYYYYMMDDHHMMSS(long time) {
		String sysDatetime = new SimpleDateFormat(COMMON_DATE_YYYYMMDDHHmmss).format(time);
		return sysDatetime;
	}
	
	/**
	 * 时间格式转换
	 * 
	 * @return 2013010912 格式
	 */
	public static String getDateAsYYYYMMDDHH(long time) {
		String sysDatetime = new SimpleDateFormat(COMMON_DATE_YYYYMMDDHH).format(time);
		return sysDatetime;
	}

	/**
	 * 时间格式转换
	 * 
	 * @return 20130109123312 格式
	 */
	public static String getDateAsYYYYMMDDHHMMSSCreateTime(long time) {
		String sysDatetime = new SimpleDateFormat(COMMON_DATE_YYYY_MM_DD_MID_CREATETIME).format(time);
		return sysDatetime;
	}
	public static String getDateFromLongToStr(long time) {
		String sysDatetime = new SimpleDateFormat(COMMON_DATE_YYYY_MM_DD_MID_SERVERTIME).format(time);
		return sysDatetime;
	}

	public static String getDateFromLongToStr(String time) {
		try {
			return getDateFromLongToStr(Long.parseLong(time));
		} catch (NumberFormatException e) {
			return time;
		}
	}

	public static long getYesterday(long time) {
		return time - 24 * 60 * 60 * 1000L;
	}

	public static String getDateFromTime(long time, SimpleDateFormat dfTime) {
		String sysDatetime = dfTime.format(time);
		return sysDatetime;
	}

	/**
	 * 
	 * @param strDate
	 *            格式 20121223
	 * @return 2012-12-12
	 */
	public static String getYYYYMMDDToYYYY_MM_DD(String strDate) {
		char[] charArr = new char[10];
		charArr[0] = strDate.charAt(0);
		charArr[1] = strDate.charAt(1);
		charArr[2] = strDate.charAt(2);
		charArr[3] = strDate.charAt(3);
		charArr[4] = '-';
		charArr[5] = strDate.charAt(4);
		charArr[6] = strDate.charAt(5);
		charArr[7] = '-';
		charArr[8] = strDate.charAt(6);
		charArr[9] = strDate.charAt(7);

		return new String(charArr);
	}

	/**
	 * dip 转成 px
	 * 
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * px 转成 dip
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 字符串转成整型
	 * 
	 * @param strCal
	 * @return
	 */
	public static int calstrToInt(String strCal) {
		if (strCal == null) {
			Logger.e(TAG, "strCal is null");
			return 0;
		}
		float fCal = 0;
		try {
			fCal = Float.valueOf(strCal);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			Logger.e(TAG, "strCal format error!");
		}
		return (int) fCal;
	}

	/**
	 * 米转成千米字符串 格式为#.#
	 * 
	 * @param sumDistance
	 * @return
	 */
	public static String m2km(int sumDistance) {
		String parten = "#.#";
		DecimalFormat decimal = new DecimalFormat(parten);
		String str = decimal.format(sumDistance / 1000.0f);
		return str;
	}

	/**
	 * 米转成千米字符串 格式为#.#
	 * 
	 * @param sumDistance
	 * @return
	 */
	public static String m2km(String distance) {
		String str = "0";
		try {
			int disStr = Integer.valueOf(distance);
			String parten = "#.#";
			DecimalFormat decimal = new DecimalFormat(parten);
			str = decimal.format(disStr / 1000.0f);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		return str;
	}
	
	public static float getDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}
	
	@SuppressWarnings("resource")
	public static void copyUseChannel(File srcFile, File destFile) throws IOException {
		if ((!srcFile.exists()) || (srcFile.isDirectory())) {
			return;
		}
		if (!destFile.exists()) {
			destFile.createNewFile();
		}
		FileChannel out = null;
		FileChannel in = null;
		try {
			out = new FileOutputStream(destFile).getChannel();
			in = new FileInputStream(srcFile).getChannel();
			ByteBuffer buffer = ByteBuffer.allocate(102400);
			int position = 0;
			int length = 0;
			while (true) {
				length = in.read(buffer, position);
				if (length <= 0) {
					break;
				}
				System.out.println("after read:" + buffer);
				buffer.flip();
				System.out.println("after flip:" + buffer);
				out.write(buffer, position);
				position += length;
				buffer.clear();
				System.out.println("after clear:" + buffer);
			}
		} finally {
			if (out != null) {
				out.close();
			}
			if (in != null) {
				in.close();
			}
			srcFile.deleteOnExit();
		}
	}
	
	public static ProgressDialog initProgressDialog(String content,Context context) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(content);
		progressDialog.show();
		return progressDialog;
	}
	
	public static long getFileSizes(File f){// 取得文件大小
		long s = 0;
		if (f.exists()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(f);
				s = fis.available();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return s;
	}
	
	public static String getNumber(String str) {
		str = str.trim();
		StringBuilder sb = new StringBuilder();
		if (str != null && !"".equals(str)) {
			for (int i = 0; i < str.length(); i++) {
				if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
					sb.append(str.charAt(i));
				}
			}
		}
		return sb.toString();
	}
	
	@SuppressWarnings("deprecation")
	public static int getAndroidSDKVersion() {
		int version = 0;
		try {
			version = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return version;
    }

	public static String InputToStr(InputStream is) {
		StringBuilder sb = new StringBuilder();
		String readline = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			while (br.ready()) {
				readline = br.readLine();
				sb.append(readline);
			}
			br.close();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
		return sb.toString();
	}
	
	public static void wirteStringToSdAfterCreateDirs(String path, String filename, String content) {
		File file = new File(path);
		file.mkdirs();
		file = null;
		wirteStringToSd(path + filename, content);
	}
	public static void wirteStringToSd(String path,String content) {
		try {
			content = content + "\r\n";
			RandomAccessFile raf = new RandomAccessFile(new File(path), "rw");
			raf.seek(raf.length());
			raf.writeBytes(content);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 隐藏软键盘
	public static void collapseSoftInputMethod(Context context,View view) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
//	1.      RMR
//	v是速度单位是千米/小时
//	走  0.75*v
//	跑 1.25*v-3
//	骑车 0.42*v
//	2. BMR计算公式

	/**   
	* calBMR(根据人的生理体征计算BMR系数，用于卡路里计算) 
	* @param gender 性别男1女0
	* @param age   年龄
	* @param height 身高cm
	* @param weight 体重kg
	* @return    BMR系数
	* @修改人：gaofei - 高飞
	* @修改时间：2014-1-7 下午5:50:35
	* @since CodingExample　Ver(编码范例查看) 1.1   
	*/
	public static float calBMR(int gender,int age,int height,int weight)	//性别男1女0；年龄；身高cm，体重kg
    {
        float bmr = 0;
        float bsa = 0;
        if (gender == 1) {//男
            bmr = 134 * weight + 48 * height - 57 * age + 883;
            bsa = 61 * weight + 127 * height - 698;
        }else{//女
            bmr = 92 * weight + 31 * height - 43 * age + 4476;
            bsa = 59 * weight + 126 * height - 461;
        }
        bmr = bmr * bsa / 48000;// 这是60秒的数据
        if(bmr<0) bmr =0;
        return bmr;
        //min 40KG 50old 150 女
        //max 120KG 20old 210 男
    }
	
	/**   
	* calCalorie(根据个人体征系数，速度和运动时间以及运动类型计算当期时段卡路里消耗) 
	* @param bmr 个人体征系数
	* @param velocity 速度（公里每小时，km/hr）
	* @param duration 运动时间 （秒）
	* @param type 1 走，2跑，3骑行
	* @return    
	* @修改人：gaofei - 高飞
	* @修改时间：2014-1-7 下午6:03:31
	* @since CodingExample　Ver(编码范例查看) 1.1   
	*/
	public	static float calRunCalorie(float bmr, float velocity, float duration, int type){
        float fCalorie = 0.0f;
        switch (type) {
            case 1:
                fCalorie= (float)(bmr*(velocity*0.75)*duration/60000);  
                break;
            case 2:
                if((velocity*1.25-3)>0){
                    fCalorie= (float)(bmr*(velocity*1.25-3)*duration/60000); 
                }else{
                    fCalorie=0;
                }     
                break;
            case 3:
                fCalorie= (float)(bmr*(velocity*0.42)*duration/60000);        
                break;

            default:
                break;
        }       
        return fCalorie;
	}
	
	
	public static int String2Int(String str){
		try {
			return Integer.valueOf(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * 坐标点屏幕全展示
	 * @param points
	 * @param mapController
	 */
	public static void fitPoints(List<GeoPoint> points,MapController mapController) {
	    // set min and max for two points
	    int nwLat = -90 * 1000000;
	    int nwLng = 180 * 1000000;
	    int seLat = 90 * 1000000;
	    int seLng = -180 * 1000000;
	    // find bounding lats and lngs
	    for (GeoPoint point : points) {
	        nwLat = Math.max(nwLat, point.getLatitudeE6());//比较最大的纬度
	        nwLng = Math.min(nwLng, point.getLongitudeE6());//比较最小经度
	        seLat = Math.min(seLat, point.getLatitudeE6());//比较最小纬度
	        seLng = Math.max(seLng, point.getLongitudeE6());//比较最大经度
	    }
	    //
	    GeoPoint center = new GeoPoint((nwLat + seLat) / 2, (nwLng + seLng) / 2);
	    // add padding in each direction
	    int spanLatDelta = (int) (Math.abs(nwLat - seLat) * 1.1);
	    int spanLngDelta = (int) (Math.abs(seLng - nwLng) * 1.1);

	    // fit map to points
	    mapController.zoomToSpan(spanLatDelta, spanLngDelta);
	    mapController.animateTo(center);
	}
	
	/**
	 * 秒转换时间格式 00:01:20
	 * @param second 秒
	 * @return
	 */
	public static String sec2Time(int second){
		int totalSec = 0;
		totalSec = second;
		// Set time display
		int hor = (totalSec / 3600);
		int min = ((totalSec % 3600) / 60);
		int sec = (totalSec % 60);
		return String.format("%1$02d:%2$02d:%3$02d", hor, min, sec);
	}
}
