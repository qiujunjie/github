package cmcc.mhealth.bean;

import android.util.Log;

public class GroupMemberInfo {
	public static String TAG = "GroupMemberInfo";

	/**
	 * ��������
	 */
	public String membername = "֧����"; // 01
	/**
	 * ��Ա�ڰ����������
	 */
	public String memberseq;
	/**
	 * ��Ա�ڰ�����ƽ��7���˶����
	 */
	public String member7avgdist;
	/**
	 * ��Ա�ڰ�����ƽ��7���˶�����
	 */
	public String member7avgstep;
	/**
	 * ��Ա�ڰ��������˶����
	 */
	public String memberscore;
	/**
	 * ��Ա�ڰ�������Ϣ������1
	 */
	//TODO ����������
	public String memberinforev1 = "1";
	/**				
	 * ���ձ��,0Ϊ7�գ�1Ϊ����
	 */
	public String memberinforev2;
	
	public String avatar;
	
	/**
	 * �Ƚ��������ݵ�˳��
	 * 
	 * @param compare
	 * @return biger 1 small 0 error -1
	 */
	public int compare(GroupMemberInfo compare) {
		int x, y;
		try {
			x = Integer.parseInt(memberseq);
			y = Integer.parseInt(compare.memberseq);
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
