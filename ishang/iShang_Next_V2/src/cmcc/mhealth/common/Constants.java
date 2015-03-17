package cmcc.mhealth.common;

import cmcc.mhealth.R;

public class Constants {
	public static final String APP_NAME = "iShang";
	
//	public static final String APP_ID = "wxab1ab589130e63d5";// 发布
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
	
	//用于打开侧滑时不显示退出框
	public static boolean OPENED;
	//全局运动类型
	public static int RUNTYPE = 1;

	// message
	public static final int MESSAGE_LOGIN_SUCCESS = R.string.MESSAGE_LOGIN_SUCCESS;
	public static final int MESSAGE_LOGIN_FALSE = R.string.MESSAGE_LOGIN_FALSE;

	public static final int MESSAGE_CUSTOMER_INFO_EXCEPTION = R.string.MESSAGE_CUSTOMER_INFO_EXCEPTION;// 用户信息验证失败
	public static final int MESSAGE_PASSWORD_ERROE = R.string.MESSAGE_PASSWORD_ERROE;// 密码错误
	public static final int MESSAGE_NOT_ACTIVITY = R.string.MESSAGE_NOT_ACTIVITY;// 用户未激活
	public static final int MESSAGE_PHONE_PASSWORD_EXCEPTION = R.string.MESSAGE_PHONE_PASSWORD_EXCEPTION;// 手机号或密码不正确
	public static final int MESSAGE_PHONE_ERROR = R.string.MESSAGE_PHONE_ERROR;// 请输入正确的手机号码
	public static final int MESSAGE_PHONE_ISEMPTY = R.string.MESSAGE_PHONE_ISEMPTY;// 账号为空
	public static final int MESSAGE_PASSWORD_ISEMPTY = R.string.MESSAGE_PASSWORD_ISEMPTY;// 密码为空
	public static final int MESSAGE_PASSWORD_ISTOOLEN = R.string.MESSAGE_PASSWORD_ISTOOLEN;// 密码超长50字符
	public static final int MESSAGE_PHONE_ISTOOLEN = R.string.MESSAGE_PHONE_ISTOOLEN;// 账号超长30字符

	public static final int MESSAGE_NO_PEDO_DETAIL = R.string.MESSAGE_NO_PEDO_DETAIL;// 运动数据为空

	public static final int MESSAGE_INTERNET_NONE = R.string.MESSAGE_INTERNET_NONE;// 没有网络
	public static final int MESSAGE_INTERNET_ERROR = R.string.MESSAGE_INTERNET_ERROR; // 网络错误
	public static final int MESSAGE_SERVER_EXCEPTION = R.string.MESSAGE_SERVER_EXCEPTION;// 服务器繁忙
	public static final int MESSAGE_SERVER_EXCEPTION2 = R.string.MESSAGE_SERVER_EXCEPTION2;// 服务器繁忙
	public static final int MESSAGE_SERVER_EXCEPTION3 = R.string.MESSAGE_SERVER_EXCEPTION3;// 服务器繁忙

	public static final int MESSAGE_UPDATE_SUCCESS = R.string.MESSAGE_UPDATE_SUCCESS; // 更新完成
	public static final int MESSAGE_UPDATE_FALSE = R.string.MESSAGE_UPDATE_FALSE; // 更新失败
	public static final int MESSAGE_UPDATED_VERSION = R.string.MESSAGE_UPDATED_VERSION; // 已经是最新版本

	public static final int MESSAGE_UPDATE_PWD_SUCCESS = R.string.MESSAGE_UPDATE_PWD_SUCCESS; // 修改成功

	public static final int MESSAGE_CROP_FAILED = R.string.MESSAGE_CROP_FAILED; // 获取失败请重新选择
	public static final int MESSAGE_AVARAR_SET_SUCCESS = R.string.MESSAGE_AVARAR_SET_SUCCESS; // 头像设置完成
	
	public static final int MESSAGE_COMFIRM_FAIL = R.string.MESSAGE_COMFIRM_FAIL; // 验证失败，请重试
	public static final int MESSAGE_PLS_INPUT_CODE = R.string.MESSAGE_COMFIRM_PLSINPUTCODE; // 请输入验证码
	
	public static final int MESSAGE_GET_SERVERLIST_FAILED = R.string.MESSAGE_GET_SERVERLIST_FAILED; // 获取服务器列表失败
}
