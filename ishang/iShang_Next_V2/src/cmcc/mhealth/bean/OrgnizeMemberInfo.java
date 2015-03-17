package cmcc.mhealth.bean;

import android.util.Log;

public class OrgnizeMemberInfo {
	public static String TAG = "OrgnizeMemberInfo";

	/**
	 * ��Ա����
	 */
	public String membername = "֧�ų�Ա"; // 01
	/**
	 * ��������
	 */
	public String groupname = "֧����"; // 01
	/**
	 * ��Ա�ڰ����������
	 */
	public String memberseq;

	public String rankCountOfToday = "";
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
	// TODO ����������
	public String memberinforev1 = "1";// 0 Ů 1��

	/**
	 * ��Ա�ڰ�������Ϣ������2
	 */
	public String memberinforev2;
	/**
	 * ������ֻ��ţ��˺ţ�
	 */
	public String friendphone;
	/**
	 * ͷ��id
	 */
	public String avatar;

	/**
	 * �Ƚ��������ݵ�˳��
	 * 
	 * @param compare
	 * @return biger 1 small 0 error -1
	 */
	public int compare(OrgnizeMemberInfo compare) {
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
