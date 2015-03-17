/********************************************
 * �ļ���		��ActivityMedalInfo.java
 * �汾��Ϣ	��1.00
 * �����ˣ�Gaofei - �߷�
 * ����ʱ�䣺2013-4-19 ����3:17:16   
 * �޸��ˣ�Gaofei - �߷�
 * �޸�ʱ�䣺2013-4-19 ����3:17:16  
 * ��������	��
 * 
 * CopyRight(c) China Mobile 2013   
 * ��Ȩ����   All rights reserved
 *******************************************/
package cmcc.mhealth.bean;

import cmcc.mhealth.common.Logger;

/**
 * 
 * ��Ŀ���ƣ�iShangTrunk �����ƣ�ActivityMedalInfo �������� �����ˣ�Gaofei - �߷� ����ʱ�䣺2013-4-19
 * ����3:17:16 �޸��ˣ�Gaofei - �߷� �޸�ʱ�䣺2013-4-19 ����3:17:16 �޸ı�ע��
 * 
 * @version
 * 
 */
public class ActivityMedalInfo extends BaseNetItem {

	public static String TAG = "ActivityMedalInfo";

	//public String status = "SUCCESS";
	public String dataType = null;
	public String activityid = "-1";
	public ActivityDetailData datavalue = null;

	public void setValue(ActivityMedalInfo data) {
		this.status = data.status;
		this.dataType = data.dataType;
		this.datavalue = data.datavalue;
	}

	@Override
	public void setValue(BaseNetItem bni) {
		// TODO Auto-generated method stub
		if (null != bni)
			setValue((ActivityMedalInfo) bni);
	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		ActivityMedalInfo info = (ActivityMedalInfo)bni;
		if(info.datavalue == null){
			Logger.e(TAG, "data is null");
			return false;
		}
		return true;
	}

}
