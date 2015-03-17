package cmcc.mhealth.common;

import cmcc.mhealth.R;

public class Constants {
	public static final String APP_NAME = "iShang";
	
//	public static final String APP_ID = "wxab1ab589130e63d5";// ����
	public static final String APP_ID = "wx39f8a6359a25636e";// debug
	public static final int GROUP_YESTERDAY = 0;
	public static final int GROUP_7DAY = 1;
	public static final String IShangPath = "";
	
	public static final String PedoBriefActivity = "PedoBriefActivity";
	public static final String ListSportsHistoryActivity = "ListSportsHistoryActivity";
	public static final String RankActivity = "RankActivity";
	public static final String RaceContentActivity = "RaceContentActivity";
	public static final String SettingActivity = "SettingActivity"; 

	public static final long DAY_MILLSECONDS = 1000L * 24 * 60 * 60;
	/**
	 * KeyOfSharedPrefences
	 */
	public static final String ExtraHeight = "height";
	public static final String ExtraWtight = "weight";
	public static final String NOTIFI_FLAG = "fragment_flag";
	
	public static final String SERVICE_RUNNING_NAME = "cmcc.mhealth.service.StepService";
	// activity
	public static final int RANK_SELECT_PK = 110;
	
	public static final int SUCCESS = 200;
	public static final int FAIL = -404;
	public static final int NODATA = 101;
	
	//���ڴ򿪲໬ʱ����ʾ�˳���
	public static boolean OPENED;
	//ȫ���˶�����
	public static int RUNTYPE = 1;

	// message
	public static final int MESSAGE_LOGIN_SUCCESS = R.string.MESSAGE_LOGIN_SUCCESS;
	public static final int MESSAGE_LOGIN_FALSE = R.string.MESSAGE_LOGIN_FALSE;

	public static final int MESSAGE_CUSTOMER_INFO_EXCEPTION = R.string.MESSAGE_CUSTOMER_INFO_EXCEPTION;// �û���Ϣ��֤ʧ��
	public static final int MESSAGE_PASSWORD_ERROE = R.string.MESSAGE_PASSWORD_ERROE;// �������
	public static final int MESSAGE_NOT_ACTIVITY = R.string.MESSAGE_NOT_ACTIVITY;// �û�δ����
	public static final int MESSAGE_PHONE_PASSWORD_EXCEPTION = R.string.MESSAGE_PHONE_PASSWORD_EXCEPTION;// �ֻ��Ż����벻��ȷ
	public static final int MESSAGE_PHONE_ERROR = R.string.MESSAGE_PHONE_ERROR;// ��������ȷ���ֻ�����
	public static final int MESSAGE_PHONE_ISEMPTY = R.string.MESSAGE_PHONE_ISEMPTY;// �˺�Ϊ��
	public static final int MESSAGE_PASSWORD_ISEMPTY = R.string.MESSAGE_PASSWORD_ISEMPTY;// ����Ϊ��
	public static final int MESSAGE_PASSWORD_ISTOOLEN = R.string.MESSAGE_PASSWORD_ISTOOLEN;// ���볬��50�ַ�
	public static final int MESSAGE_PHONE_ISTOOLEN = R.string.MESSAGE_PHONE_ISTOOLEN;// �˺ų���30�ַ�

	public static final int MESSAGE_NO_PEDO_DETAIL = R.string.MESSAGE_NO_PEDO_DETAIL;// �˶�����Ϊ��

	public static final int MESSAGE_INTERNET_NONE = R.string.MESSAGE_INTERNET_NONE;// û������
	public static final int MESSAGE_INTERNET_ERROR = R.string.MESSAGE_INTERNET_ERROR; // �������
	public static final int MESSAGE_SERVER_EXCEPTION = R.string.MESSAGE_SERVER_EXCEPTION;// ��������æ
	public static final int MESSAGE_SERVER_EXCEPTION2 = R.string.MESSAGE_SERVER_EXCEPTION2;// ��������æ
	public static final int MESSAGE_SERVER_EXCEPTION3 = R.string.MESSAGE_SERVER_EXCEPTION3;// ��������æ

	public static final int MESSAGE_UPDATE_SUCCESS = R.string.MESSAGE_UPDATE_SUCCESS; // �������
	public static final int MESSAGE_UPDATE_FALSE = R.string.MESSAGE_UPDATE_FALSE; // ����ʧ��
	public static final int MESSAGE_UPDATED_VERSION = R.string.MESSAGE_UPDATED_VERSION; // �Ѿ������°汾

	public static final int MESSAGE_UPDATE_PWD_SUCCESS = R.string.MESSAGE_UPDATE_PWD_SUCCESS; // �޸ĳɹ�

	public static final int MESSAGE_CROP_FAILED = R.string.MESSAGE_CROP_FAILED; // ��ȡʧ��������ѡ��
	public static final int MESSAGE_AVARAR_SET_SUCCESS = R.string.MESSAGE_AVARAR_SET_SUCCESS; // ͷ���������
	
	public static final int MESSAGE_COMFIRM_FAIL = R.string.MESSAGE_COMFIRM_FAIL; // ��֤ʧ�ܣ�������
	public static final int MESSAGE_PLS_INPUT_CODE = R.string.MESSAGE_COMFIRM_PLSINPUTCODE; // ��������֤��
	
	public static final int MESSAGE_GET_SERVERLIST_FAILED = R.string.MESSAGE_GET_SERVERLIST_FAILED; // ��ȡ�������б�ʧ��
}
