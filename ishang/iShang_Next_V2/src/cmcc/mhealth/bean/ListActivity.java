/********************************************
 * �ļ���		��ActivityDetail.java
 * �汾��Ϣ	��1.00
 * �����ˣ�Gaofei - �߷�
 * ����ʱ�䣺2013-3-25 ����4:01:07   
 * �޸��ˣ�Gaofei - �߷�
 * �޸�ʱ�䣺2013-3-25 ����4:01:07  
 * ��������	��
 * 
 * CopyRight(c) China Mobile 2013   
 * ��Ȩ����   All rights reserved
 *******************************************/
package cmcc.mhealth.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

/**   
 *    
 * ��Ŀ���ƣ�iShangTrunk   
 * �����ƣ�ActivityDetail   
 * ��������   ���ϸ��Ϣ
 * �����ˣ�Gaofei - �߷�
 * ����ʱ�䣺2013-3-25 ����4:01:07   
 * �޸��ˣ�Gaofei - �߷�
 * �޸�ʱ�䣺2013-3-25 ����4:01:07   
 * �޸ı�ע��   
 * @version    
 *    
 */
public class ListActivity {
	public static String TAG = "ActivityDetail";
	
	public String activityid = "0";
	public String activityname = "�������˽�����";
	public String activityslogan = "��������һ";
	public String isfirstday = "1";
	public String activitytype = "-1";
	public String activitystart = "20130301";
	public String activityend = "20130401";
	public String company_name = "�й��ƶ�ͨ�����޹�˾�о�Ժ";
	public String aimstep = "8000";
	public String personnum = "30";
	public String personseq = "8";
	public String groupnum = "5";
	public String groupseq = "2";


	  /**
   * �Ƚ��������ݵ�˳��
   * 
   * @param compare
   * @return biger 1 small 0 error -1
   */
  public int compare(ListActivity compare) {
    long dayOfSecondsx, dayOfSecondsy;
    try {

      Date EndDate = df_yyyyMMdd.parse(activityend);
      dayOfSecondsx = EndDate.getTime();
      EndDate = df_yyyyMMdd.parse(compare.activityend);
      dayOfSecondsy = EndDate.getTime();

    } catch (Exception e) {
      Log.e(TAG, "parse error");
      return -1;
    }

    if (dayOfSecondsx > dayOfSecondsy)
      return 1;
    else if (dayOfSecondsx < dayOfSecondsy)
      return 0;
    else
      return -1;
  }

  // **
  private SimpleDateFormat df_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
}