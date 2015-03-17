package cmcc.mhealth.bean;

import android.util.Log;

public class DataDetailPedo {
	public static String TAG = "DataDetailPedo";
	/**
	 * ��¼��ʼʱ��
	 */
	public String start_time = "11"; // 01
	/**
	 * ÿ����ӵĲ���
	 */
	public String snp5;
	/**
	 * ÿ����ӵĿ�·������
	 */
	public String knp5;
	/**
	 * ÿ����ӵ��˶�ǿ��2ʱ�䣨�룩
	 */
	public String level2p5;
	/**
	 * ÿ����ӵ��˶�ǿ��3ʱ�䣨�룩
	 */
	public String level3p5;
	/**
	 * ÿ����ӵ��˶�ǿ��4ʱ�䣨�룩
	 */
	public String level4p5;
	/**
	 * ÿ����ӵļ��ٶȣ��룩
	 */
	public String yuanp5;

	public String snyxp5;

	public int getStepNumSum() {
		int sum = 0;
		String[] stepNums = snp5.split(",");
		for (int i = 0; i < stepNums.length; i++) {
			sum += Integer.parseInt(stepNums[i]);
		}
		return sum;
	}

	public int getSNYXP5Sum() {
		int sum = 0;
		if (snyxp5 == null || snyxp5.equals(""))
			return 0;
		String[] snyxp5s = snyxp5.split(",");
		for (int i = 0; i < snyxp5s.length; i++) {
			sum += Integer.parseInt(snyxp5s[i]);
		}
		return sum;
	}

	/**
	 * ��ȡһСʱ�˶�ǿ��ʱ��� ��
	 * 
	 * @param level
	 *            �˶�ǿ�� ��2��ʼ
	 * @return
	 */
	public int getStrengthSum(int level) {
		String levelp;
		switch (level) {
		case 2:
			levelp = level2p5;
			break;
		case 3:
			levelp = level3p5;
			break;
		case 4:
			levelp = level4p5;
			break;
		default:
			levelp = level2p5;
			break;
		}
		int sum = 0;
		String[] levels = levelp.split(",");
		for (int i = 0; i < levels.length; i++) {
			sum += Integer.parseInt(levels[i]);
		}
		return sum;
	}

	/**
	 * �Ƚ��������ݵĿ�ʼʱ��
	 * 
	 * @param compare
	 * @return biger 1 small 0 error -1
	 */
	public int compareStartTime(DataDetailPedo compare) {
		int this_hour, compare_hour;
		try {
			this_hour = Integer.parseInt(this.start_time);
			compare_hour = Integer.parseInt(compare.start_time);
		} catch (Exception e) {
			Log.e(TAG, "parse error");
			return -1;
		}

		if (this_hour > compare_hour)
			return 1;
		else if (this_hour < compare_hour)
			return 0;
		else
			return -1;
	}
}
