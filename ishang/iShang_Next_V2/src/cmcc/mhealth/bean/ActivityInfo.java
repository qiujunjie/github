/********************************************
 * �ļ���		��ActivityInfo.java
 * �汾��Ϣ	��1.00
 * �����ˣ�Gaofei - �߷�
 * ����ʱ�䣺2013-3-25 ����3:57:33   
 * �޸��ˣ�Gaofei - �߷�
 * �޸�ʱ�䣺2013-3-25 ����3:57:33  
 * ��������	��
 * 
 * CopyRight(c) China Mobile 2013   
 * ��Ȩ����   All rights reserved
 *******************************************/
package cmcc.mhealth.bean;

import java.util.ArrayList;
import java.util.List;

import cmcc.mhealth.common.Logger;

/**
 * 
 * ���Ϣ�б����
 * 
 * @version
 * 
 */
public class ActivityInfo extends BaseNetItem {

	public static String TAG = "ActivityInfo";

	//public String status = "SUCCESS";
	public String dataType = "activeinfo"; // ��������

	public List<ListActivity> activitynow; // �����еĻ��Ϣ�б�(���3����)

	public List<ListActivity> activityold; // �ս����Ļ��Ϣ(1��)
	public List<ListActivity> activityfuture; // �Ƽ�δ��ʼ�Ļ��Ϣ(1��)

	public int activitynownum = 0; // �����еĻ��Ŀ
	public int activityoldnum = 0; // �����μӹ��Ļ��Ŀ
	public int activityfuturenum = 0; // ���Բμӹ��Ļ��Ŀ

	public ActivityInfo() {
		activitynow = new ArrayList<ListActivity>();
		activityold = new ArrayList<ListActivity>();
		activityfuture = new ArrayList<ListActivity>();
	}

	public void setValue(ActivityInfo data) {
		this.status = data.status;
		this.activitynow = data.activitynow;
		this.activityold = data.activityold;
		this.activityfuture = data.activityfuture;

		this.activitynownum = data.activitynownum;
		this.activityoldnum = data.activityoldnum;
		this.activityfuturenum = data.activityfuturenum;
	}

	public void initialData() {
		// TODO �˶�ǿ���Ƿ���Ҫ��ʼ��
		sortGroupPkInfo();
	}

	private void sortGroupPkInfo() {
		int size = activitynow.size();
		if (size <= 0)
			return;

		for (int i = 0; i < size; i++) {
			int k = i;
			// find max
			for (int j = i + 1; j < size; j++) {
				int result = activitynow.get(j).compare(activitynow.get(k));
				if (result == 0)
					k = j;
			}
			if (k != i) {// change position
				ListActivity data = activitynow.get(i);
				activitynow.set(i, activitynow.get(k));
				activitynow.set(k, data);
			}
		}
	}

	@Override
	public void setValue(BaseNetItem bni) {
		// TODO Auto-generated method stub
		if (null != bni)
			this.setValue((ActivityInfo) bni);
	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		ActivityInfo info = (ActivityInfo)bni;
		if(info.activityfuture == null){
			Logger.e(TAG, "data is null");
			return false;
		}
		return true;
	}
}