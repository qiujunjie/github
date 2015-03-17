package cmcc.mhealth.bean;

import android.util.Log;

public class GroupInfo {
	public static String TAG = "GroupInfo";
	public String groupid = "8";
	
	/**
	 * ��������
	 */
	public String groupname = "11"; // 01
	/**
	 * ���������
	 */
	public String groupseq;
	/**
	 * ����ƽ��7���˶����
	 */
	public String group7avgdist;
	/**
	 * ����ƽ��7���˶�����
	 */
	public String group7avgstep;
	/**
	 * �������˶����
	 */
	public String groupscore;
	/**
	 * ����Ϣ������1
	 */
	//TODO ����������
	public String groupinforev1;
	/**
	 * ����Ϣ������2
	 */
	public String groupinforev2;
	
	/**
	 * �Ƚ��������ݵ�˳��
	 * 
	 * @param compare
	 * @return biger 1 small 0 error -1
	 */
	public int compare(GroupInfo compare) {
		int x, y;
		try {
			x = Integer.parseInt(groupseq);
			y = Integer.parseInt(compare.groupseq);
		} catch (Exception e) {
			Log.e(TAG, "parse error");
			return -1;
		}

		if (x > y)
			return -1;
		else if (x < y)
			return 0;
		else
			return -1;
	}
	
}
