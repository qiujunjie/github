package cmcc.mhealth.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import cmcc.mhealth.bean.AcceptFriendRequestInfo;
import cmcc.mhealth.bean.ActivityDetailMessageInfo;
import cmcc.mhealth.bean.ActivityInfo;
import cmcc.mhealth.bean.ActivityMedalInfo;
import cmcc.mhealth.bean.BackInfo;
import cmcc.mhealth.bean.BaseNetItem;
import cmcc.mhealth.bean.ClubListInfo;
import cmcc.mhealth.bean.ContectData;
import cmcc.mhealth.bean.ContectGroupData;
import cmcc.mhealth.bean.ContectGroupInfo;
import cmcc.mhealth.bean.ContectInfo;
import cmcc.mhealth.bean.DetailGPSData;
import cmcc.mhealth.bean.FindFriendInfo;
import cmcc.mhealth.bean.FriendPedometorSummary;
import cmcc.mhealth.bean.FriendsInfo;
import cmcc.mhealth.bean.ListGPSData;
import cmcc.mhealth.bean.GPSListInfo;
import cmcc.mhealth.bean.GpsInfoDetail;
import cmcc.mhealth.bean.GroupIdInfo;
import cmcc.mhealth.bean.GroupMemberPkInfo;
import cmcc.mhealth.bean.GroupPkInfo;
import cmcc.mhealth.bean.GroupRankUpdateVersion;
import cmcc.mhealth.bean.LoginInfo;
import cmcc.mhealth.bean.OrgnizeMemberPKInfo;
import cmcc.mhealth.bean.OrgnizeMemberSum;
import cmcc.mhealth.bean.OrgnizememSeq;
import cmcc.mhealth.bean.PedoDetailInfo;
import cmcc.mhealth.bean.PedometorInfo;
import cmcc.mhealth.bean.RaceInfo;
import cmcc.mhealth.bean.RaceMemberInfo;
import cmcc.mhealth.bean.RankingDate;
import cmcc.mhealth.bean.RequestListInfo;
import cmcc.mhealth.bean.ServersInfo;
import cmcc.mhealth.bean.TempCodeInfo;
import cmcc.mhealth.bean.UpdatePasswordInfo;
import cmcc.mhealth.bean.UpdateVersionJson;
import cmcc.mhealth.bean.UserRegInfo;
import cmcc.mhealth.bean.VitalSignInfo;
import cmcc.mhealth.bean.VitalSignInfoDataBean;
import cmcc.mhealth.bean.VitalSignUploadState;
import cmcc.mhealth.bean.WeightInfo;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Config;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.common.PreferencesUtils;
import cmcc.mhealth.common.SharedPreferredKey;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

public class DataSyn {
	public static String TAG = "DataSyn";
	public static String strHttpURL;
	public static String avatarHttpURL;

	public static String contactFileUrl;
	public static String contactGroupFileUrl;

	// public static String strHttpURL =
	// "http://phr.cmri.cn/sport/openClientApi.do?action=";
	// public static String strHttpURL =
	// "http://218.206.179.71/jk/openClientApi.do?action=";
	// public static String strHttpURL =
	// "http://218.206.179.193/CmccPhr/openClientApi.do?action=";

	private static DataSyn instance;
	private HttpClient mHttpClient;

	// private ThreadPoolManager threadPoolManager;

	private DataSyn() {
		/*
		 * SchemeRegistry schemeRegistry = new SchemeRegistry();
		 * PoolingClientConnectionManager cm = new
		 * PoolingClientConnectionManager(schemeRegistry); // Increase max total
		 * connection to 200 cm.setMaxTotal(200); // Increase default max
		 * connection per route to 20 cm.setDefaultMaxPerRoute(20); // Increase
		 * max connections for localhost:80 to 50 HttpHost localhost = new
		 * HttpHost("locahost", 80); cm.setMaxPerRoute(new HttpRoute(localhost),
		 * 50);
		 */

		/*
		 * HttpParams params = new BasicHttpParams(); HttpProtocolParamBean
		 * paramsBean = new HttpProtocolParamBean(params);
		 * paramsBean.setVersion(HttpVersion.HTTP_1_1);
		 * paramsBean.setContentCharset("UTF-8");
		 * paramsBean.setUseExpectContinue(true);
		 */
		// threadPoolManager = ThreadPoolManager.getInstance();
		if(mHttpClient == null){
			mHttpClient = new DefaultHttpClient();
			// ����8������ʱ
			mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			mHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		}

	}

	// public void getRunable(String url,BaseNetItem bni){
	// BaseTask baseTask = new BaseTask(url,bni);
	// threadPoolManager.addTask(baseTask);
	// }
	//
	// class BaseTask implements Runnable{
	//
	// private String mUrl;
	// public BaseNetItem baseNetItem;
	//
	// public BaseTask(String queryStr, BaseNetItem reqData){
	// this.mUrl = queryStr;
	// this.baseNetItem = reqData;
	// }
	//
	// @Override
	// public void run() {
	// if(getDataFromNet(mUrl,baseNetItem) == 0){
	//
	// }
	// }
	// }

	public static void setStrHttpURL(String strHttpURL) {
		DataSyn.strHttpURL = strHttpURL;
		// TODO ����
	}

	public static void setAvatarHttpURL(String avatarHttpURL) {
		DataSyn.avatarHttpURL = avatarHttpURL;
		// TODO ����
	}

	public synchronized static DataSyn getInstance() {
		if (null == instance)
			instance = new DataSyn();
		return instance;
	}

	private HttpEntity httpClientExecuteGet(String queryStr) {
		Logger.i(TAG, queryStr);
		HttpGet httpget = new HttpGet(queryStr);
		HttpEntity entity = null;

		try {
			HttpResponse response = mHttpClient.execute(httpget);
			entity = response.getEntity();
		} catch (UnknownHostException unknown) {
			Logger.e(TAG, "��ʱ");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			httpget.abort();
		}

		return entity;
	}

	public int getDataFromNet(String queryStr, BaseNetItem reqData) {
		if (!queryStr.startsWith(Config.SERVER_DESTINY)) {
			if (queryStr.split("action=").length == 1) {
				Logger.e(TAG, "URL is NULL");
				return -1;
			}
		}
		synchronized (mHttpClient) {
			HttpEntity entity = httpClientExecuteGet(queryStr);
			if (null == entity) {
				Logger.e(TAG, "entity is null");
				return -1;
			}

			InputStream instream = null;
			try {	 
				instream = entity.getContent();
				if (null == instream) {
					Logger.e(TAG, "instream is null");
					return -1;
				}

				JsonReader reader = new JsonReader(new InputStreamReader(instream, "UTF-8"));
				// reader.beginObject();
				Gson gson = new Gson();
				BaseNetItem rawData = gson.fromJson(reader, reqData.getClass());
				// reader.endObject();

				reader.close();

				if (null == rawData) {
					Logger.e(TAG, "rawData error");
					return 1;
				} else {
//					Logger.e(TAG, "status:" + rawData.status + "\tReason:" + rawData.Reason);
					if("FAILURE".equals(rawData.status) && "No User Or Password is Worng".equals(rawData.reason)){
						return 500;
					}
				}

				if (!rawData.isValueData(rawData)) {
					Logger.e(TAG, "valueData is null");
					return 1;
				}
				
				reqData.setValue(rawData);

//				if (!rawData.status.equals("SUCCESS"))
//					return 1;
				
				Logger.d(TAG, rawData.toString());

				reqData.initialDate();

			} catch (JsonSyntaxException ex) {
				ex.printStackTrace();
				return 1;
			} catch (IOException ex) {
				// In case of an IOException the connection will be released
				// back to the connection manager automatically
				// throw ex;
				ex.printStackTrace();
				return -1;
			} finally {
				// Closing the input stream will trigger connection release
				try {
					instream.close();
				} catch (Exception ignore) {
					ignore.printStackTrace();
					return -1;
				}
			}
		}
		return 0;
	}

	public int sendTempCode(String phoneNum, TempCodeInfo resultreqData, Context context) {
		Random rnd = new Random();
		LoginInfo loginData = new LoginInfo();
		String str = Config.SERVER_DESTINY + Config.SERVER_LIST + "?code=" + rnd.nextInt();// Ҫ���û�ȡ�������б�ķ�����������
		getDataFromNet(str, loginData);
		if ("SUCCESS".equals(loginData.status)) {
			ArrayList<ServersInfo> servers = loginData.datavalue;
			TempCodeInfo reqData = new TempCodeInfo();
			for (ServersInfo serversInfo : servers) {
				String strinner = "http://" + serversInfo.getServerName() + "openClientApi.do?action=sendTempCode&userid=" + phoneNum;

				int dataFromNet = getDataFromNet(strinner, reqData);
				Logger.d(TAG, "reqData.status == " + reqData.status);
				if (dataFromNet == 0 && "SUCCESS".equals(reqData.status)) {
					reqData.selectserver = serversInfo.getServerName();

					SharedPreferences info = context.getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
					Editor edit = info.edit();
					edit.putString(SharedPreferredKey.SERVER_NAME, serversInfo.getServerName());
					edit.putString(SharedPreferredKey.SELECTED_SERVER, serversInfo.getServerName());
					edit.putString(SharedPreferredKey.SERVER_VERSION, serversInfo.getServerversion());
					edit.commit();

					strHttpURL = "http://" + serversInfo.getServerName() + "openClientApi.do?action=";
					avatarHttpURL = "http://" + serversInfo.getServerName() + "UserAvatar/";
					return dataFromNet;
				}
				if ("��ͬһ�ֻ�����ÿ���������3�ζ�����֤��".equals(reqData.result)) {
					return 5;
				}
			}
			return 2;
		} else {
			return -1;
		}
	}

	/**
	 * function: company: CMCC-CMRI
	 * 
	 * @author Gaofei 2013-9-3 ����4:57:54 version 1.0 describe: ��ȡȺ����°汾��
	 * @param phoneNum
	 * @param Pwd
	 * @param reqData
	 * @return
	 */
	public int getGroupRankUpdateVersion(String phoneNum, String Pwd,int ClubId, GroupRankUpdateVersion reqData) {
		String str = strHttpURL + "getPrepareDataVersion&userid=" + phoneNum + "&psw=" + Pwd + "&clubid=" + ClubId;
		return getDataFromNet(str, reqData);
	}

	public int passwordReset(String phoneNum, String tempCode, String newPwd, UpdatePasswordInfo reqData) {
		String str = strHttpURL + "passwordreset&userid=" + phoneNum + "&tempcode=" + tempCode + "&new_psw=" + newPwd;
		return getDataFromNet(str, reqData);
	}

	public int verifyTempCode(String phoneNum, String tempCode, String newPwd, UpdatePasswordInfo reqData, String selectedserver) {
		String str;
		if (selectedserver == null || "".equals(selectedserver)) {
			str = strHttpURL + "passwordreset&userid=" + phoneNum + "&tempcode=" + tempCode;
		} else {
			str = "http://" + selectedserver + "openClientApi.do?action=passwordreset&userid=" + phoneNum + "&tempcode=" + tempCode;
			Logger.i(TAG, str);
		}
		if ("".equals(strHttpURL)) {
			strHttpURL = "http://" + selectedserver + "openClientApi.do?action=";
			avatarHttpURL = "http://" + selectedserver + "UserAvatar/";
		}
		return getDataFromNet(str, reqData);
	}

	public ArrayList<ContectData> getContactList(Context context, String PhoneNum, String Password, int clubid) {
		ContectInfo ci = new ContectInfo();
		String queryStr = strHttpURL + "getclubmember&userid=" + PhoneNum + "&psw=" + Password + "&clubid=" + clubid;
		getDataFromNet(queryStr, ci);
		if (ci.datavalue.size() > 0) {
			return ci.datavalue;
		}
		return null;
	}

	public ArrayList<ContectGroupData> getContactGroupList(Context context, String PhoneNum, String Password, int clubid) {
		ContectGroupInfo cgi = new ContectGroupInfo();
		String queryStr = strHttpURL + "getclubgroup&userid=" + PhoneNum + "&psw=" + Password + "&clubid=" + clubid;
		getDataFromNet(queryStr, cgi);
		if (cgi.datavalue.size() > 0) {
			return cgi.datavalue;
		}
		return null;
	}

	/**
	 * ��½��֤
	 * 
	 * @param phone
	 *            ��½���ֻ���
	 * @param password
	 *            ��½������
	 * @param data
	 *            ��½��ʱ��
	 * @return 0 ��½�ɹ�;-1 �쳣;1 ���ݴ��� 2 �˺Ż�������� 3δ���� 4�˺Ŵ��� 5������� 6��ȡ�������б�ʧ��
	 */
	public int loginAuth(String phoneNum, String password, Context context) {
		long after_day = new Date().getTime() + (1000L * 60 * 60 * 24); // ����һ��
		String date = Common.getDateAsYYYYMMDD(after_day);

		LoginInfo reqData = new LoginInfo();
		SharedPreferences info = context.getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		String selectedserver = info.getString(SharedPreferredKey.SERVER_NAME, "");

		// //���ԣ�--
		// selectedserver = strHttpURL;
		// //---

		if ("".equals(selectedserver)) {
			Random rnd = new Random();
			String str = Config.SERVER_DESTINY + Config.SERVER_LIST + "?code=" + rnd.nextInt();// Ҫ���û�ȡ�������б�ķ�����������
			getDataFromNet(str, reqData);
			if ("SUCCESS".equals(reqData.status)) {
				ArrayList<ServersInfo> servers = reqData.datavalue;
				LoginInfo reqDatainner = new LoginInfo();
				for (ServersInfo serversInfo : servers) {
					String strinner = "http://" + serversInfo.getServerName() + "openClientApi.do?action=pedometer&userid=" + phoneNum + "&psw=" + password + "&date=" + date;

					int suc = getDataFromNet(strinner, reqDatainner);
					if(suc != 0){
						return 2;
					}
//					Logger.e(TAG, reqDatainner.status + reqDatainner.Reason + reqDatainner.phoneNum + reqDatainner.date);

					if (reqDatainner.status.equals("FAILURE") && reqDatainner.reason.equals("NOACCOUNT OR ERRPASSWORD"))
						continue;
					if (reqDatainner.status.equals("FAILURE") && reqDatainner.reason.equals("NOACCOUNT"))
						continue;
					if (reqDatainner.status.equals("FAILURE") && reqDatainner.reason.equals("Worng User"))
						continue;
					if (reqDatainner.status.equals("FAILURE") && reqDatainner.reason.equals("NOACTIVITY")){
						servernamesavetosp(info, serversInfo);
						return 3;
					}
					if (reqDatainner.status.equals("SUCCESS")) {
						servernamesavetosp(info, serversInfo);
						Logger.i(TAG, "target server -- > " + serversInfo.getServerName());
						return 0;
					}
					if (reqDatainner.status.equals("FAILURE") && reqDatainner.reason.equals("ERRPASSWORD")) {
						servernamesavetosp(info, serversInfo);
						Logger.i(TAG, "target server -- > " + serversInfo.getServerName());
						return 5;
					}
				}
				return 7;
			} else {
//				if (NetworkTool.getNetworkStateConnected(context) == 0) {
//					return 6;
//				} else {
					return 6;
//				}
			}
		} else {
			LoginInfo reqDataElse = new LoginInfo();
			// TODO ����
			strHttpURL = "http://" + selectedserver + "openClientApi.do?action=";
			avatarHttpURL = "http://" + selectedserver + "UserAvatar/";
			String strinner = strHttpURL + "pedometer&userid=" + phoneNum + "&psw=" + password + "&date=" + date;
			int result = getDataFromNet(strinner, reqDataElse);
			if (reqDataElse.status.equals("FAILURE") && reqDataElse.reason.equals("NOACCOUNT OR ERRPASSWORD"))
				result = 2;
			if (reqDataElse.status.equals("FAILURE") && reqDataElse.reason.equals("NOACTIVITY"))
				result = 3;
			if (reqDataElse.status.equals("FAILURE") && reqDataElse.reason.equals("Worng User")) {
				result = 7;
			}
			Logger.i(TAG, "using old server -- > " + selectedserver);
			return result;
		}
	}

    private void servernamesavetosp(SharedPreferences info, ServersInfo serversInfo) {
        Editor edit = info.edit();
        edit.putString(SharedPreferredKey.SERVER_NAME, serversInfo.getServerName());
        edit.putString("contactfile", serversInfo.getContactfile());
        edit.putString("contactgroupfile", serversInfo.getGroupfile());
        edit.putString(SharedPreferredKey.SERVER_VERSION, serversInfo.getServerversion());
        edit.commit();
        contactFileUrl = serversInfo.getContactfile();
        contactGroupFileUrl = serversInfo.getGroupfile();
        strHttpURL = "http://" + serversInfo.getServerName() + "openClientApi.do?action=";
        avatarHttpURL = "http://" + serversInfo.getServerName() + "UserAvatar/";
    }

	/**
	 * �޸�����
	 * 
	 * @param phoneNum
	 * @param oldPassword
	 * @param newPassword
	 * @param newPasswordAg
	 * @param reqData
	 * @return int 0/1
	 */
	public int updatePassWord(String phoneNum, String oldPassword, String newPassword, String newPasswordAg, UpdatePasswordInfo reqData) {
		// ����GET������ʵ��
		String str = strHttpURL + "passwordchange&userid=" + phoneNum // 13810411683
				+ "&old_psw=" + oldPassword + "&new_psw=" + newPassword + "&new_psw_ag=" + newPasswordAg;

		return getDataFromNet(str, reqData);
	}

	public int updateVersion(UpdateVersionJson reqData) {
		Random tmprand = new Random();
		String str = Config.UPDATE_SERVER + Config.UPDATE_VERJSON + "?action=" + tmprand.nextInt();
		return getDataFromNet(str, reqData);
	}

	/**
	 * ��ȡ�Ʋ����ϴ�����
	 * 
	 * @param PhoneNum
	 * @param Password
	 * @return -1 ������� 1 ���ݴ��� 0 ��ȷ���
	 */
	public int getPedoInfo(String PhoneNum, String Password, String date, PedometorInfo reqData) {
		if (date != null) {
			if (date.length() < 8) {
				date = "20130122";
			}
		} else {
			date = "20130122";
		}

		// String str = strHttpURL + "queryUploadDetail&userid=" + PhoneNum//
		// 13810411683
		String str = strHttpURL + "pedometer&userid=" + PhoneNum// 13810411683
				+ "&psw=" + Password// wxf
				+ "&date=" + date;// 20121002

		return getDataFromNet(str, reqData);
	}

	/**
	 * ��ȡ�Ʋ�����ϸ����
	 * 
	 * @param PhoneNum
	 * @param Password
	 * @return -1 ������� 1 ���ݴ��� 0 ��ȷ���
	 */
	public int getPedoInfoDetail(String PhoneNum, String Password, String fromHour, String toHour, String date, PedoDetailInfo reqData) {
		if (date != null) {
			if (date.length() < 8) {
				date = "20130122";
			}
		} else {
			date = "20130122";
		}

		String str = strHttpURL + "pedalldata&userid=" + PhoneNum// 13810411683
				+ "&psw=" + Password// wxf
				+ "&fromHour=" + fromHour + "&toHour=" + toHour + "&date=" + date;// 20121002

		return getDataFromNet(str, reqData);
	}

	/**
	 * getOrgnizeMembersPkInfoBySpan(��ȡĳ�칫˾��ȫ����Ա��Ϣ--ͨ������ID��ȡ��Ա������Ϣ)
	 * 
	 * @param PhoneNum
	 *            ������
	 * @param Password
	 *            ��������
	 * @param reqData
	 *            ���صİ����ڳ�Ա������Ϣ
	 * @param @return �趨�ļ�
	 * @return -1 ������� 1 json���ݴ��� 2 �������ڸ�ʽ���� 0 ��ȷ���
	 */
	// public int getOrgnizeMembersPkInfoBySpan(String PhoneNum, String
	// Password,
	// int startseqnum, int endseqnum, OrgnizeMemberPKInfo reqData,
	// String strStartDate, String strEndDate) {
	//
	// // ��֯��ѯ�ַ���
	// String str = strHttpURL + "pedorgnizememberbyspan&userid=" + PhoneNum
	// + "&psw=" + Password + "&startseqnum=" + startseqnum
	// + "&endseqnum=" + endseqnum + "&startdate=";
	//
	// try {
	// str += Common.Formatyyyy_MM_dd(strStartDate) + "&enddate="
	// + Common.Formatyyyy_MM_dd(strEndDate);
	// } catch (ParseException e2) {
	// e2.printStackTrace();
	// Logger.e(TAG, "���ڽ�������");
	// return 2;
	// }
	// return getDataFromNet(str, reqData);
	// }

	public int getOrgnizeMembersPkInfoYestoday(String PhoneNum, String Password,int ClubId, int startseqnum, int endseqnum, OrgnizeMemberPKInfo reqData, String strStartDate, String strEndDate) {

		// ��֯��ѯ�ַ���
		String str = strHttpURL + "getuserreadydata&userid=" + PhoneNum + "&psw=" + Password + "&startseqnum=" + startseqnum + "&endseqnum=" + endseqnum + "&daycount=1" + "&clubid=" + ClubId;
		return getDataFromNet(str, reqData);
	}

	public int getOrgnizeMembersPkInfo7Day(String PhoneNum, String Password,int ClubId, int startseqnum, int endseqnum, OrgnizeMemberPKInfo reqData) {
		// public int getOrgnizeMembersPkInfo7Day(String PhoneNum, String
		// Password,
		// int startseqnum, int endseqnum, OrgnizeMemberPKInfo reqData,
		// String strStartDate, String strEndDate) {

		// ��֯��ѯ�ַ���
		String str = strHttpURL + "getuserreadydata&userid=" + PhoneNum + "&psw=" + Password + "&startseqnum=" + startseqnum + "&endseqnum=" + endseqnum + "&daycount=7" + "&clubid=" + ClubId;
		return getDataFromNet(str, reqData);
	}

	/**
	 * ��ȡ��˾��ȫ����Ա��Ϣ--ͨ������ID��ȡ��Ա������Ϣ
	 * 
	 * @param PhoneNum
	 *            ������
	 * @param Password
	 *            ��������
	 * @param reqData
	 *            ���صİ����ڳ�Ա������Ϣ
	 * @return -1 ������� 1 ���ݴ��� 0 ��ȷ���
	 */
	// public int getOrgnizeMembersPkInfo(String PhoneNum, String Password,
	// int startseqnum, int endseqnum, OrgnizeMemberPKInfo reqData) {
	// // ����GET������ʵ��
	// String str = strHttpURL + "pedorgnizemember&userid="
	// + PhoneNum // 13810411683
	// + "&psw=" + Password + "&startseqnum=" + startseqnum
	// + "&endseqnum=" + endseqnum;
	//
	// return getDataFromNet(str, reqData);
	// }

	/**
	 * ��ȡ�ض�����ĳ�Ա��Ϣ--ͨ������ID��ȡ��Ա������Ϣ
	 * 
	 * @param PhoneNum
	 *            ������
	 * @param Password
	 *            ��������
	 * @param groupid
	 *            ��Ҫ���ʵİ���ID
	 * @param reqData
	 *            ���صİ����ڳ�Ա������Ϣ
	 * @return -1 ������� 1 ���ݴ��� 0 ��ȷ��� by gaofei
	 */
	// public int getGroupMembersPkInfoBySpan(String PhoneNum, String Password,
	// String groupid, int startseqnum, int endseqnum,
	// GroupMemberPkInfo reqData, String strStartDate, String strEndDate) {
	// // PhoneNum = "13811029472";
	// // Password = "123456";
	//
	// SimpleDateFormat df_yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
	// SimpleDateFormat df_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
	//
	// String str = "";
	// try {
	// Date dateTmp = df_yyyyMMdd.parse(strStartDate);
	// String strStartDateV2 = df_yyyy_MM_dd.format(dateTmp);
	//
	// dateTmp = df_yyyyMMdd.parse(strEndDate);
	// String strEndDateV2 = df_yyyy_MM_dd.format(dateTmp);
	//
	// // ����GET������ʵ��
	// str = strHttpURL
	// + "pedgroupmemberbyspan&userid="
	// + PhoneNum // 13810411683
	// + "&psw=" + Password + "&groupid=" + groupid
	// + "&startseqnum=" + startseqnum + "&endseqnum=" + endseqnum
	// + "&startdate=" + strStartDateV2 + "&enddate="
	// + strEndDateV2;
	// } catch (ParseException e2) {
	// e2.printStackTrace();
	// Logger.e(TAG, "���ڽ�������");
	// return 2;
	// }
	//
	// return getDataFromNet(str, reqData);
	// }

	public int getGroupMembersPkInfoYestoday(String PhoneNum, String Password,int ClubId, String groupid, int startseqnum, int endseqnum, GroupMemberPkInfo reqData, String strStartDate, String strEndDate) {
		// ����GET������ʵ��
		String str = strHttpURL + "getuseringroupreadydata&userid=" + PhoneNum // 13810411683
				+ "&psw=" + Password + "&groupid=" + groupid + "&startseqnum=" + startseqnum + "&endseqnum=" + endseqnum + "&daycount=1" + "&clubid=" + ClubId;
		return getDataFromNet(str, reqData);
	}

	public int getGroupMembersPkInfo7Day(String PhoneNum, String Password,int ClubId, String groupid, int startseqnum, int endseqnum, GroupMemberPkInfo reqData) {
		// public int getGroupMembersPkInfo7Day(String PhoneNum, String
		// Password,
		// String groupid, int startseqnum, int endseqnum,
		// GroupMemberPkInfo reqData, String strStartDate, String strEndDate) {
		// ����GET������ʵ��
		String str = strHttpURL + "getuseringroupreadydata&userid=" + PhoneNum // 13810411683
				+ "&psw=" + Password + "&groupid=" + groupid + "&startseqnum=" + startseqnum + "&endseqnum=" + endseqnum + "&daycount=7" + "&clubid=" + ClubId;
		return getDataFromNet(str, reqData);
	}

	/**
	 * ��ȡ�ض�����ĳ�Ա��Ϣ--ͨ������ID��ȡ��Ա������Ϣ
	 * 
	 * @param PhoneNum
	 *            ������
	 * @param Password
	 *            ��������
	 * @param groupid
	 *            ��Ҫ���ʵİ���ID
	 * @param reqData
	 *            ���صİ����ڳ�Ա������Ϣ
	 * @return -1 ������� 1 ���ݴ��� 0 ��ȷ��� by gaofei
	 */
	// public int getGroupMembersPkInfo(String PhoneNum, String Password,
	// String groupid, int startseqnum, int endseqnum,
	// GroupMemberPkInfo reqData) {
	// // PhoneNum = "13811029472";
	// // Password = "123456";
	//
	// // ����GET������ʵ��
	// String str = strHttpURL + "pedgroupmember&userid="
	// + PhoneNum // 13810411683
	// + "&psw=" + Password + "&groupid=" + groupid + "&startseqnum="
	// + startseqnum + "&endseqnum=" + endseqnum;
	//
	// return getDataFromNet(str, reqData);
	// }

	/**
	 * ��ȡ�����еĸ���������Ϣ--ͨ���������ڰ���ID��ȡ�����еĸ������������Ϣ
	 * 
	 * @param PhoneNum
	 *            ������
	 * @param Password
	 *            ��������
	 * @param groupid
	 *            ���������ڵİ���ID
	 * @param reqData
	 *            ���ؾ����е��������������Ϣ
	 * @return -1 ������� 1 ���ݴ��� 0 ��ȷ��� by gaofei
	 */
	// public int getGroupPkInfoBySpan(String PhoneNum, String Password,
	// String groupid, int startseqnum, int endseqnum,
	// GroupPkInfo reqData, String strStartDate, String strEndDate) {
	// // PhoneNum = "13811029472";
	// // Password = "123456";
	// // ����HttpClient��ʵ��
	// String str = "";
	// try {
	//
	// SimpleDateFormat df_yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
	// SimpleDateFormat df_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
	//
	// Date dateTmp = df_yyyyMMdd.parse(strStartDate);
	// String strStartDateV2 = df_yyyy_MM_dd.format(dateTmp);
	//
	// dateTmp = df_yyyyMMdd.parse(strEndDate);
	// String strEndDateV2 = df_yyyy_MM_dd.format(dateTmp);
	//
	// // ����GET������ʵ��
	// str = strHttpURL
	// + "pedgrouppkbyspan&userid="
	// + PhoneNum // 13810411683
	// + "&psw=" + Password + "&groupid=" + groupid
	// + "&startseqnum=" + startseqnum + "&endseqnum=" + endseqnum
	// + "&startdate=" + strStartDateV2 + "&enddate="
	// + strEndDateV2;
	//
	// } catch (ParseException e2) {
	// e2.printStackTrace();
	// Logger.e(TAG, "���ڽ�������");
	// return 2;
	// }
	//
	// return getDataFromNet(str, reqData);
	// }

	public int getGroupPkInfoYestoday(String PhoneNum, String Password,int ClubId, String groupid, int startseqnum, int endseqnum, GroupPkInfo reqData, String strStartDate, String strEndDate) {
		String str = strHttpURL + "getgroupreadydata&userid=" + PhoneNum // 13810411683
				+ "&psw=" + Password + "&groupid=" + groupid + "&startseqnum=" + startseqnum + "&endseqnum=" + endseqnum + "&daycount=1" + "&clubid=" + ClubId;

		// Logger.e(TAG, str);
		return getDataFromNet(str, reqData);

	}

	public int getGroupPkInfo7(String PhoneNum, String Password,int ClubId, String groupid, int startseqnum, int endseqnum, GroupPkInfo reqData) {
		String str = strHttpURL + "getgroupreadydata&userid=" + PhoneNum // 13810411683
				+ "&psw=" + Password + "&groupid=" + groupid + "&startseqnum=" + startseqnum + "&endseqnum=" + endseqnum + "&daycount=7" + "&clubid=" + ClubId;

		// Logger.e(TAG, str);

		return getDataFromNet(str, reqData);
	}

	/**
	 * ͨ������ID��ȡ��������������Ϣ
	 * 
	 * @param PhoneNum
	 * @param Password
	 * @param groupid
	 * @param startseqnum
	 * @param endseqnum
	 * @param reqData
	 * @return -1 ������� 1 ���ݴ��� 0 ��ȷ���
	 */
	// public int getGroupPkInfo(String PhoneNum, String Password, String
	// groupid,
	// int startseqnum, int endseqnum, GroupPkInfo reqData) {
	// // PhoneNum = "13811029472";
	// // Password = "123456";
	//
	// // ����GET������ʵ��
	// String str = strHttpURL + "pedgrouppk&userid="
	// + PhoneNum // 13810411683
	// + "&psw=" + Password + "&groupid=" + groupid + "&startseqnum="
	// + startseqnum + "&endseqnum=" + endseqnum;
	//
	// return getDataFromNet(str, reqData);
	// }

	/**
	 * ��ȡ���ڵİ���ID--ͨ�������˺Ż�ȡ���ڵİ���ID
	 * 
	 * @param PhoneNum
	 *            ������
	 * @param Password
	 *            ��������
	 * @param reqData
	 *            �������ڵİ���ID
	 * @return -1 ������� 1 ���ݴ��� 0 ��ȷ���
	 */
	public int getGroupIdInfo(String PhoneNum, String Password,int ClubId,
			GroupIdInfo reqData) {
		// PhoneNum = "13811029472";
		// Password = "123456";
		// ����GET������ʵ��
		String str = strHttpURL + "pedgroupid&userid=" + PhoneNum // 13810411683
				+ "&psw=" + Password + "&clubid=" + ClubId;

		return getDataFromNet(str, reqData);
	}

	/**
	 * ��ȡ���г�Ա������������
	 * 
	 * @param PhoneNum
	 * @param Password
	 * @param reqData
	 * @return
	 */
	public int getPedorgnizeMemberSum(String PhoneNum, String Password,int ClubId, OrgnizeMemberSum reqData) {
		// ����GET������ʵ��
		String str = strHttpURL + "pedorgnizemembersum&userid=" + PhoneNum // 13810411683
				+ "&psw=" + Password + "&clubid=" + ClubId;
		return getDataFromNet(str, reqData);
	}

	/**
	 * ��ȡĳ����Χ�ڸ�������������
	 * 
	 * @param PhoneNum
	 * @param Password
	 * @param reqData
	 * @return 0Ϊ����
	 */
	// public int getPedOrgnizeMemberSeqBySpan(String PhoneNum, String Password,
	// OrgnizememSeq reqData, String strStartDate, String strEndDate) {
	// // ����GET������ʵ��
	// String str = strHttpURL + "pedorgnizememberseq&userid=" + PhoneNum //
	// 13810411683
	// + "&psw=" + Password;
	// return getDataFromNet(str, reqData);
	// }

	/**
	 * ��ȡ��������������
	 * 
	 * @param PhoneNum
	 * @param Password
	 * @param reqData
	 * @return 0Ϊ����
	 */
	public int getPedOrgnizeMemberSeq(String PhoneNum, String Password, OrgnizememSeq reqData) {
		// ����GET������ʵ��
		String str = strHttpURL + "pedorgnizememberseq&userid=" + PhoneNum // 13810411683
				+ "&psw=" + Password;
		return getDataFromNet(str, reqData);
	}

	/**
	 * ��ȡ��б�
	 * 
	 * @param phoneNum
	 *            ������
	 * @param password
	 *            ��������
	 * @param reqData
	 * 
	 * @return -1 ������� 1 ���ݴ��� 0 ��ȷ���
	 */
	public int getActivityInfo(String phoneNum, String password,int ClubId, ActivityInfo reqData) {
		// PhoneNum = "13811029472";
		// Password = "232304";
		// ����GET������ʵ��
		String str = strHttpURL + "activityinfo&userid=" + phoneNum // 13810411683
				+ "&psw=" + password + "&clubid=" + ClubId;
		return getDataFromNet(str, reqData);
	}
	
	public int getActivityDetailMessage(String phoneNum, String password,String activityid,ActivityDetailMessageInfo reqData){
		String str = strHttpURL+"activitydetail&userid=13552019273&psw=123456&activityid=6";
//		String str = "http://phr.cmri.cn/datav2/openClientApi.do?action=activitydetail&userid=13552019273&psw=123456&activityid=6";
//		String str = strHttpURL + "activitydetail&userid=" + phoneNum // 13810411683
//				+ "&psw=" + password + "&activityid=" + activityid;
		return getDataFromNet(str, reqData);
	}

	/**
	 * 
	 * ��ȡ�ض���еĽ�����Ϣ
	 * 
	 * @param PhoneNum
	 *            ,Password,GroupMemberSum
	 * @return int 0Ϊ����
	 */
	public int getAvtivityMedalInfo(String PhoneNum, String Password,int ClubId, ActivityMedalInfo reqData, String activityID) {
		// ����GET������ʵ��
		String str = strHttpURL + "memberactivityrank&userid=" + PhoneNum // 13810411683
				+ "&psw=" + Password + "&activityid=" + activityID + "&clubid=" + ClubId;
		return getDataFromNet(str, reqData);
	}

	public int getPedGroupSeq(String PhoneNum, String Password,int ClubId, RankingDate reqData) {
		String queryStr = strHttpURL + "pedgroupseq&userid=" + PhoneNum // 13810411683
				+ "&psw=" + Password + "&clubid=" + ClubId;
		return getDataFromNet(queryStr, reqData);
	}

	/**
	 * �ϴ��������ݲ���ȡ������������
	 */
	public int getLastestWeightInfo(String PhoneNum, String Password, String weight, double height, int agecode, int gender, String changetime, WeightInfo reqData) {
		String queryStr = strHttpURL + "personprofile&userid=" + PhoneNum + "&psw=" + Password + "&weight=" + weight + "&height=" + height + "&agecode=" + agecode + "&gender=" + gender + "&changetime=" + changetime;
		return getDataFromNet(queryStr, reqData);
	}

	/**
	 * ��ȡ������������������
	 */
	public int getLastestWeightInfoWithOutParams(String PhoneNum, String Password, WeightInfo reqData) {
		String queryStr = strHttpURL + "personprofile&userid=" + PhoneNum + "&psw=" + Password;
		Logger.i(TAG, queryStr);
		return getDataFromNet(queryStr, reqData);
	}

	/**
	 * �õ�Ŀ������������Ϣ
	 */
	public int getSeqsBySearchNumber(String PhoneNum, String Password, int ClubId, String targetNumber, RankingDate reqData) {
		String queryStr = strHttpURL + "pedgroupseq&userid=" + PhoneNum + "&psw=" + Password + "&targetnumber=" + targetNumber + "&clubid=" + ClubId;
		Logger.i(TAG, queryStr);
		return getDataFromNet(queryStr, reqData);
	}

	/**
	 * �õ�Ŀ��Ⱥ���������Ϣ
	 */
	public int getGroupSeqsBySearchNumber(String PhoneNum, String Password, int ClubId, String targetNumber, RankingDate reqData) {
		String queryStr = strHttpURL + "pedtargetgroupseq&userid=" + PhoneNum + "&psw=" + Password + "&targetgroupid=" + targetNumber + "&clubid=" + ClubId;
		Logger.i(TAG, queryStr);
		return getDataFromNet(queryStr, reqData);
	}
	
	/**
	 * ��ȡ�������ϣ��ϴ��ֻ�������ʱ��֮�����������
	 * 
	 * @param PhoneNum
	 * @param Password
	 * @param lastDownloadSuccessTime   longֵʱ�����
	 * @param reqData
	 * @return
	 */
	public int getVitalSignAfterLastDownloadSuccessTime(String PhoneNum, String Password, long lastDownloadSuccessTime, VitalSignInfo reqData) {
		String queryStr = strHttpURL + "downloadvitalsign&userid=" + PhoneNum + "&psw=" + Password + 
				"&nativetime=" + Common.getDateFromLongToStr(new Date().getTime()) + 
				"&newesdownload=" + Common.getDateFromLongToStr(lastDownloadSuccessTime);
		Logger.i(TAG, queryStr);
		return getDataFromNet(queryStr, reqData);
	}

	/**
	 * ��ȡ�������ϣ��ϴ��ֻ�������ʱ��֮�����������
	 * 
	 * @param PhoneNum
	 * @param Password
	 * @param lastDownloadSuccessTime  Stringֵʱ�� ��ʽΪ yyyy-MM-dd_HH:mm:ss
	 * @param reqData
	 * @return
	 */
	public int getVitalSignAfterLastDownloadSuccessTime(String PhoneNum, String Password, String lastDownloadSuccessTime, VitalSignInfo reqData) {
		String queryStr = strHttpURL + "downloadvitalsign&userid=" + PhoneNum + "&psw=" + Password + 
				"&starttime=" + lastDownloadSuccessTime;
		return getDataFromNet(queryStr, reqData);
	}
	/**
	 * �ϴ����ϴ��ϴ��ɹ�ʱ��֮�����������
	 */
	public int postVitalSign(String PhoneNum, String Password, List<VitalSignInfoDataBean> listofweight, VitalSignUploadState reqData , String datetypeStr) {
		StringBuilder sb = new StringBuilder();
		if(listofweight ==null || listofweight.size() ==0){
			return 10;
		}
		for (VitalSignInfoDataBean vitalSignBean : listofweight) {
			sb.append("@" + datetypeStr + "#" + vitalSignBean.getMeasureDate() + "#" + Common.getDateFromLongToStr(vitalSignBean.getEditTime()) + "#" + vitalSignBean.getValue());
		}
		String vitalstring = sb.substring(1);
		
		String queryStr = strHttpURL + "uploadvitalsign";
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("userid", PhoneNum));
		list.add(new BasicNameValuePair("psw", Password));
		list.add(new BasicNameValuePair("vitalsignarray", vitalstring));
		Logger.d("VitalSignMetaData", vitalstring);
		return postDataFromNet(queryStr, reqData, list);
	}
	// =======
	// ���ѵĽӿ�
	//========
	// ɾ������
	public int deleteFriend(String PhoneNum, String Password, String friendid, AcceptFriendRequestInfo reqData) {
		String queryStr = strHttpURL + "deletefriend&userid=" + PhoneNum + "&psw=" + Password + "&friendid=" + friendid;
		return getDataFromNet(queryStr, reqData);
	}
	// ͬ���������
	public int acceptRequest(String PhoneNum, String Password, String friendid, BackInfo reqData) {
		String queryStr = strHttpURL + "acceptfriend&userid=" + PhoneNum + "&psw=" + Password + "&friendid=" + friendid;
		return getDataFromNet(queryStr, reqData);
	}
	// ��ú��������б�
	public int getFriendRequestList(String PhoneNum, String Password, RequestListInfo reqData) {
		String queryStr = strHttpURL + "getfriendrequestlist&userid=" + PhoneNum + "&psw=" + Password;
		return getDataFromNet(queryStr, reqData);
	}
	// ��ú����б�
	public int getFriendsList(String PhoneNum, String Password, FriendsInfo reqData) {
		String queryStr = strHttpURL + "getfriendslist&userid=" + PhoneNum + "&psw=" + Password;
		return getDataFromNet(queryStr, reqData);
	}

	// ��ȡ�ض����ѵļ���Ϣ
	public int getFriendInfo(String PhoneNum, String Password, String friendid, FriendPedometorSummary reqData) {
		String queryStr = strHttpURL + "getfriendsinfo&userid=" + PhoneNum + "&psw=" + Password + "&friendid=" + friendid;
		return getDataFromNet(queryStr, reqData);
	}
	// ���Һ���
	public int findFriendById(String PhoneNum, String Password, String friendid, FindFriendInfo reqData) {
		String queryStr = strHttpURL + "findfriendbyid&userid=" + PhoneNum + "&psw=" + Password + "&friendid=" + friendid;
		return getDataFromNet(queryStr, reqData);
	}
	// ��Ӻ���
	public int addFriendById(String PhoneNum, String Password, String friendid, BackInfo reqData) {
		String queryStr = strHttpURL + "addfriendbyid&userid=" + PhoneNum + "&psw=" + Password + "&friendid=" + friendid;
		return getDataFromNet(queryStr, reqData);
	}
	//========
	// ��ȡ��˾�б�
	public int getClubList(String PhoneNum, String Password, ClubListInfo reqData) {
		String queryStr = strHttpURL + "getclublist&userid=" + PhoneNum + "&psw=" + Password;
		return getDataFromNet(queryStr, reqData);
	}
	//========
	//========

	// ͬ�⾺������
	public int acceptRaceInvitingRequest(String PhoneNum, String Password, String raceid, String teamname, BackInfo reqData) {
		String queryStr = strHttpURL + "acceptrace&userid=" + PhoneNum + "&psw=" + Password + "&raceid=" + raceid + "&teamname=" + teamname;
		return getDataFromNet(queryStr, reqData);
	}

	// ��þ��������б�
	public int getRaceInvitedRequestList(String PhoneNum, String Password, RequestListInfo reqData) {
		String queryStr = strHttpURL + "getracerequestlist&userid=" + PhoneNum + "&psw=" + Password;
		return getDataFromNet(queryStr, reqData);
	}
		
	// ��ȡ�����б�
	public int getRaceList(String PhoneNum, String Password, int state, int num, int startid, RaceInfo reqData) {
		String queryStr = strHttpURL + "getracelist&userid=" + PhoneNum + "&psw=" + Password
				+ "&state=" + state + "&num=" + num + "&startid=" + startid;
		return getDataFromNet(queryStr, reqData);
	}
	// ��ȡ�������������б�
	public int getRaceInfo(String PhoneNum, String Password, String raceid, RaceMemberInfo reqData) {
		String queryStr = strHttpURL + "getraceinfo&userid=" + PhoneNum + "&psw=" + Password
				+ "&raceid=" + raceid;
		return getDataFromNet(queryStr, reqData);
	}
	// ��ȡ�������������б�
	public int getSearchedRace(String PhoneNum, String Password, String keyword, int type, RaceInfo reqData) {
		String queryStr = strHttpURL + "searchrace";
		List<NameValuePair> listvp = new ArrayList<NameValuePair>();
		listvp.add(new BasicNameValuePair("userid", PhoneNum));
		listvp.add(new BasicNameValuePair("psw", Password));
		listvp.add(new BasicNameValuePair("type", type + ""));
		listvp.add(new BasicNameValuePair("keyword", keyword));
		return postDataFromNet(queryStr, reqData, listvp);
	}
	// ��������
	public int createRace(String PhoneNum, String Password, List<NameValuePair> listvp, BackInfo reqData) {
		String queryStr = strHttpURL + "createrace";
		listvp.add(new BasicNameValuePair("userid", PhoneNum));
		listvp.add(new BasicNameValuePair("psw", Password));
		return postDataFromNet(queryStr, reqData, listvp);
	}
	// �μӾ���
	public int joinRace(String PhoneNum, String Password, String raceid, BackInfo reqData,String team) {
		String queryStr = strHttpURL + "joinrace&userid=" + PhoneNum + "&psw=" + Password
				+ "&raceid=" + raceid + "&teamname=" + team;
		return getDataFromNet(queryStr, reqData);
	}
	// �˳�����
	public int resignRace(String PhoneNum, String Password, String raceid, BackInfo reqData) {
		String queryStr = strHttpURL + "resignrace&userid=" + PhoneNum + "&psw=" + Password
				+ "&raceid=" + raceid;
		return getDataFromNet(queryStr, reqData);
	}
	// �޳���Ա 
	public int kickRaceMember(String PhoneNum, String Password, String raceid, String memberid, BackInfo reqData) {
		String queryStr = strHttpURL + "kickracemember&userid=" + PhoneNum + "&psw=" + Password
				+ "&raceid=" + raceid + "&memberid=" + memberid;
		return getDataFromNet(queryStr, reqData);
	}
	// �����Ա 
	public int inviteRaceMember(String PhoneNum, String Password, String raceid, String memberid, BackInfo reqData) {
		String queryStr = strHttpURL + "inviterace&userid=" + PhoneNum + "&psw=" + Password
				+ "&raceid=" + raceid + "&memberid=" + memberid;
		return getDataFromNet(queryStr, reqData);
	}
	//========
	
	/**
	 * һ��ʼ��ȡUser������Ϣ
	 * @return
	 */
	public int getUserInfo(String PhoneNum, String Password,UserRegInfo info){
		String queryStr = strHttpURL + "getpersonprofile&userid=" + PhoneNum + "&psw=" + Password;
		Logger.i(TAG, queryStr);
		return getDataFromNet(queryStr, info);
	}
	
	/**
	 * �˶��켣�����ϴ�
	 * @param PhoneNum
	 * @param Password
	 * @param gpsListInfo
	 * @param gpsInfoDetails
	 * @return
	 */
	public int UpLoadMapData(String PhoneNum, String Password, GPSListInfo gpsListInfo,List<GpsInfoDetail> gpsInfoDetails) {
		List<NameValuePair> listvp = new ArrayList<NameValuePair>();
		String queryStr = strHttpURL+"uploadtrackevent";
		listvp.add(new BasicNameValuePair("userid", PhoneNum));
		listvp.add(new BasicNameValuePair("psw", Password));
		listvp.add(new BasicNameValuePair("starttime", gpsListInfo.getStarttime()));
		listvp.add(new BasicNameValuePair("stepNum", gpsListInfo.getStepNum()));
		listvp.add(new BasicNameValuePair("distance", gpsListInfo.getDistance()+""));
		listvp.add(new BasicNameValuePair("sporttype", gpsListInfo.getSporttype()+""));
		listvp.add(new BasicNameValuePair("duration", gpsListInfo.getDuration()));
		listvp.add(new BasicNameValuePair("cal", gpsListInfo.getCal()+""));
		listvp.add(new BasicNameValuePair("speed", gpsListInfo.getSpeed()+""));
		listvp.add(new BasicNameValuePair("durationperkm", gpsListInfo.getDurationperkm()));
		listvp.add(new BasicNameValuePair("speedmax", gpsListInfo.getSpeedmax()));
		listvp.add(new BasicNameValuePair("speedmin", gpsListInfo.getSpeedmin()));
		listvp.add(new BasicNameValuePair("climbsum", gpsListInfo.getClimbsum()));
		StringBuilder sb = new StringBuilder();
		for (GpsInfoDetail gpsInfoDetail : gpsInfoDetails) {
			sb.append("@"+gpsInfoDetail.toString());
		}
		listvp.add(new BasicNameValuePair("gpstrackarray", sb.substring(1)));
		Logger.i(TAG, sb.substring(1));
		return postDataFromNet(queryStr, gpsListInfo, listvp);
	}
	
	/**
	 * ��ȡ��ʷ�켣ժҪ
	 * @param PhoneNum
	 * @param Password
	 * @param gpsData
	 * @param endtime 
	 * @param recordnum ĩβʱ����ǰ��N��
	 * @return
	 */
	public int getListGpsData(ListGPSData gpsData,String endtime,String recordnum,Context context){
		String PhoneNum = PreferencesUtils.getPhoneNum(context);
		String Password = PreferencesUtils.getPhonePwd(context);
		endtime = endtime.replaceAll(" ", "%20");
		String queryStr =strHttpURL+"gettrackevent&userid=" + PhoneNum + "&psw=" + Password +"&endtime="+endtime+"&recordnum="+recordnum;
		return getDataFromNet(queryStr, gpsData);
	}
	
	/**
	 * ��ȡ��ʷ�켣��ϸ
	 * @param gpsInfoDetail
	 * @param starttime
	 * @param context
	 * @return
	 */
	public int getDetailsGpsData(DetailGPSData gpsInfoDetail,String starttime,Context context){
		String PhoneNum = PreferencesUtils.getPhoneNum(context);
		String Password = PreferencesUtils.getPhonePwd(context);
		starttime = starttime.replaceAll(" ", "%20");
		String queryStr =strHttpURL+"gettrackeventdetail&userid=" + PhoneNum + "&psw=" + Password+"&starttime="+starttime;
		return getDataFromNet(queryStr, gpsInfoDetail);
	}
	
	public int postDataFromNet(String queryStr, BaseNetItem reqData, List<NameValuePair> list) {
		synchronized (mHttpClient) {
			
			HttpEntity entity = httpClientExecutePost(queryStr, list);

			if (null == entity) {
				Logger.e(TAG, "entity is null");
				return -1;
			}

			InputStream instream = null;
			try {
				instream = entity.getContent();
				JsonReader reader = new JsonReader(new InputStreamReader(instream, "UTF-8"));
				// reader.beginObject();
				Gson gson = new Gson();
//				Logger.d("JsonReader instream", "instream : " +  Common.InputToStr(instream));
				BaseNetItem rawData = gson.fromJson(reader, reqData.getClass());
				Logger.e(TAG, rawData.reason);
				// reader.endObject();
				reader.close();

				if (null == rawData) {
					Logger.e(TAG, "rawData error");
					return 1;
				}
				
				if(!rawData.isValueData(rawData)){
					Logger.e(TAG, "valueData is null");
					return 1;
				}
				
				reqData.setValue(rawData);

				reqData.initialDate();

			} catch (JsonSyntaxException ex) {
				ex.printStackTrace();
				return 1;
			} catch (IOException ex) {
				// In case of an IOException the connection will be released
				// back to the connection manager automatically
				// throw ex;
				ex.printStackTrace();
				return -1;
			} finally {
				// Closing the input stream will trigger connection release
				try {
					instream.close();
				} catch (Exception ignore) {
					ignore.printStackTrace();
					return -1;
				}
			}
		}
		return 0;
	}
	
	private HttpEntity httpClientExecutePost(String queryStr , List<NameValuePair> list) {
		Logger.i(TAG, queryStr);
		HttpEntity httpEntity = null;
		HttpPost httpPost = new HttpPost(queryStr);
		try {
			HttpEntity requestHttpEntity = new UrlEncodedFormEntity(list, "UTF-8");
			httpPost.setEntity(requestHttpEntity);
			HttpResponse response = mHttpClient.execute(httpPost);
			httpEntity = response.getEntity();

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (RuntimeException ex) {
			// In case of an unexpected exception you may want to abort
			// the HTTP request in order to shut down the underlying
			// connection immediately.
			ex.printStackTrace();
			httpPost.abort();
		}
		
		return httpEntity;
	}
}
