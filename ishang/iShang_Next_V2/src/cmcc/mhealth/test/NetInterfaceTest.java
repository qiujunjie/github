///********************************************
// * �ļ���		��NetInterfaceTest.java
// * �汾��Ϣ	��1.00
// * �����ˣ�Gaofei - �߷�
// * ����ʱ�䣺2013-4-9 ����1:50:30   
// * �޸��ˣ�Gaofei - �߷�
// * �޸�ʱ�䣺2013-4-9 ����1:50:30  
// * ��������	��
// * 
// * CopyRight(c) China Mobile 2013   
// * ��Ȩ����   All rights reserved
// *******************************************/
//package cmcc.mhealth.test;
//
//import android.util.Log;
//import cmcc.mhealth.ActivityInfo;
//import cmcc.mhealth.ActivityMedalInfo;
//import cmcc.mhealth.GroupIdInfo;
//import cmcc.mhealth.GroupMemberPkInfo;
//import cmcc.mhealth.GroupPkInfo;
//import cmcc.mhealth.OrgnizeMemberPKInfo;
//import cmcc.mhealth.OrgnizeMemberSum;
//import cmcc.mhealth.OrgnizememSeq;
//import cmcc.mhealth.PedoDetailInfo;
//import cmcc.mhealth.PedometorInfo;
//import cmcc.mhealth.UpdatePasswordInfo;
//import cmcc.mhealth.net.DataSyn;
//
///**
// * 
// * ��Ŀ���ƣ�iShangTrunk �����ƣ�NetInterfaceTest �������� �����ˣ�Gaofei - �߷� ����ʱ�䣺2013-4-9
// * ����1:50:30 �޸��ˣ�Gaofei - �߷� �޸�ʱ�䣺2013-4-9 ����1:50:30 �޸ı�ע��
// * 
// * @version
// * 
// */
//public class NetInterfaceTest {
//	public static String TAG = "NetInterfaceTest";
//	public static String strHttpURL = "";
//	static GroupIdInfo groupIdInfo = new GroupIdInfo();
//
//	// void NetInterfaceTest(){
//	// groupIdInfo = new GroupIdInfo();
//	// }
//	public static String testNet(String mPhoneNum, String mPassword,
//			String current_date_str, String before_date_str, int i) {
//
//		String strRet = "ȫ���ӿ�����";
//		String fromHour = "3", toHour = "23";
//		String strURL;
//		int result = 0;
//
//		switch (i) {
//		case 0: {
//			ActivityMedalInfo activityMedalInfo = new ActivityMedalInfo();
//			result = DataSyn.getInstance().getAvtivityMedalInfo(mPhoneNum,
//					mPassword, activityMedalInfo, "3");
//
//			strURL = strHttpURL + "memberactivityrank&userid=" + mPhoneNum // 13810411683
//					+ "&psw=" + mPassword + "&activityid=3";
//
//			if (result == -1) {
//				strRet = "getOrgnizeMembersPkInfoBySpan �����ȡ���ڴ���" + strURL;
//				Log.e(TAG, "����ֵΪ-1�������ȡ���ڴ���");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "getAvtivityMedalInfo �������ݴ�������" + strURL;
//				Log.e(TAG, "����ֵΪ1���������ݴ������⣡");
//				return strRet;
//			}
//			strRet = "getAvtivityMedalInfo OK";
//		}
//			break;
//		case 1: {
//			ActivityInfo mActivityInfo = new ActivityInfo();
//			result = DataSyn.getInstance().getActivityInfo("13552019273",
//					"123456", mActivityInfo);
//			strURL = strHttpURL + "activityinfo&userid=" + mPhoneNum // 13810411683
//					+ "&psw=" + mPassword;
//
//			if (result == -1) {
//				strRet = "getActivityInfo �����ȡ���ڴ���" + strURL;
//				Log.e(TAG, "����ֵΪ-1�������ȡ���ڴ���");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "getActivityInfo �������ݴ�������" + strURL;
//				Log.e(TAG, "����ֵΪ1���������ݴ������⣡");
//				return strRet;
//			}
//			strRet = "getActivityInfo OK";
//		}
//			break;
//		case 2: {
//			result = DataSyn.getInstance().getGroupIdInfo(mPhoneNum, mPassword,
//					groupIdInfo);
//			strURL = strHttpURL + "pedgroupid&userid=" + mPhoneNum // 13810411683
//					+ "&psw=" + mPassword;
//			if (result == -1) {
//				strRet = "getGroupIdInfo �����ȡ���ڴ���" + strURL;
//				Log.e(TAG, "����ֵΪ-1�������ȡ���ڴ���");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "getGroupIdInfo �������ݴ�������" + strURL;
//				Log.e(TAG, "����ֵΪ1���������ݴ������⣡");
//				return strRet;
//			}
//			strRet = "getGroupIdInfo OK";
//		}
//			break;
//		case 3: {
//			GroupMemberPkInfo groupMemberPkInfo = new GroupMemberPkInfo();
//			result = DataSyn.getInstance().getGroupMembersPkInfo7Day(mPhoneNum,
//					mPassword, groupIdInfo.groupid, 1, 100, groupMemberPkInfo);
//			strURL = strHttpURL + "pedgroupmember&userid="
//					+ mPhoneNum // 13810411683
//					+ "&psw=" + mPassword + "&groupid=" + groupIdInfo.groupid
//					+ "&startseqnum=1&endseqnum=100";
//
//			if (result == -1) {
//				strRet = "getGroupMembersPkInfo �����ȡ���ڴ���" + strURL;
//				Log.e(TAG, "����ֵΪ-1�������ȡ���ڴ���");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "getGroupMembersPkInfo �������ݴ�������" + strURL;
//				Log.e(TAG, "����ֵΪ1���������ݴ������⣡");
//				return strRet;
//			}
//
//			strRet = "getGroupMembersPkInfo OK";
//		}
//			break;
//		case 4: {
//			GroupPkInfo groupPkInfo = new GroupPkInfo();
//			result = DataSyn.getInstance().getGroupPkInfo7(mPhoneNum, mPassword,
//					groupIdInfo.groupid, 1, 20, groupPkInfo);
//			strURL = strHttpURL + "pedgrouppk&userid="
//					+ mPhoneNum // 13810411683
//					+ "&psw=" + mPassword + "&groupid=" + groupIdInfo.groupid
//					+ "&startseqnum=1&endseqnum=100";
//			if (result == -1) {
//				strRet = "getGroupPkInfo �����ȡ���ڴ���" + strURL;
//				Log.e(TAG, "����ֵΪ-1�������ȡ���ڴ���");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "getGroupPkInfo �������ݴ�������" + strURL;
//				Log.e(TAG, "����ֵΪ1���������ݴ������⣡");
//				return strRet;
//			}
//
//			strRet = "getGroupPkInfo OK";
//		}
//			break;
//		case 5: {
//			OrgnizeMemberPKInfo orgnizeMemberPKInfo = new OrgnizeMemberPKInfo();
//			result = DataSyn.getInstance().getOrgnizeMembersPkInfo7Day(mPhoneNum,
//					mPassword, 1, 20, orgnizeMemberPKInfo);
////			result = DataSyn.getInstance().getOrgnizeMembersPkInfo(mPhoneNum,
////					mPassword, 1, 20, orgnizeMemberPKInfo);
//			strURL = strHttpURL + "pedorgnizemember&userid=" + mPhoneNum // 13810411683
//					+ "&psw=" + mPassword + "&startseqnum=1&endseqnum=100";
//
//			if (result == -1) {
//				strRet = "getOrgnizeMembersPkInfo �����ȡ���ڴ���" + strURL;
//				Log.e(TAG, "����ֵΪ-1�������ȡ���ڴ���");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "getOrgnizeMembersPkInfo �������ݴ�������" + strURL;
//				Log.e(TAG, "����ֵΪ1���������ݴ������⣡");
//				return strRet;
//			}
//			strRet = "getOrgnizeMembersPkInfo OK";
//		}
//			break;
//		case 6: {
//
//			PedoDetailInfo detailData = new PedoDetailInfo();
//			result = DataSyn.getInstance().getPedoInfoDetail(mPhoneNum,
//					mPassword, fromHour, toHour, current_date_str, detailData);
//			strURL = strHttpURL + "pedalldata&userid="
//					+ mPhoneNum// 13810411683
//					+ "&psw="
//					+ mPassword// wxf
//					+ "&fromHour=" + fromHour + "&toHour=" + toHour + "&date="
//					+ current_date_str;// 20121002
//
//			if (result == -1) {
//				strRet = "getPedoDetailInfo �����ȡ���ڴ���" + strURL;
//				Log.e(TAG, "����ֵΪ-1�������ȡ���ڴ���");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "getPedoDetailInfo �������ݴ�������" + strURL;
//				Log.e(TAG, "����ֵΪ1���������ݴ������⣡");
//				return strRet;
//			}
//
//			strRet = "getPedoDetailInfo OK";
//		}
//			break;
//		case 7: {
//
//			PedometorInfo reqData = new PedometorInfo();
//			result = DataSyn.getInstance().getPedoInfo(mPhoneNum, mPassword,
//					current_date_str, reqData);
//			strURL = strHttpURL + "pedometer&userid=" + mPhoneNum + "&psw="
//					+ mPassword + "&date=" + current_date_str;
//			if (result == -1) {
//				strRet = "getPedometorInfo �����ȡ���ڴ���" + strURL;
//				Log.e(TAG, "����ֵΪ-1�������ȡ���ڴ���");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "getPedometorInfo �������ݴ�������" + strURL;
//				Log.e(TAG, "����ֵΪ1���������ݴ������⣡");
//				return strRet;
//			}
//
//			strRet = "getPedometorInfo OK";
//		}
//			break;
//		case 8: {
//			//
//			OrgnizememSeq orgnizememSeq = new OrgnizememSeq();
//			result = DataSyn.getInstance().getPedOrgnizeMemberSeq(mPhoneNum, mPassword,
//					orgnizememSeq);
//			strURL = strHttpURL + "pedorgnizememberseq&userid=" + mPhoneNum
//					+ "&psw=" + mPassword;
//			if (result == -1) {
//				strRet = "getPedOrgnizeMemberSeq �����ȡ���ڴ���" + strURL;
//				Log.e(TAG, "����ֵΪ-1�������ȡ���ڴ���");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "getPedOrgnizeMemberSeq �������ݴ�������" + strURL;
//				Log.e(TAG, "����ֵΪ1���������ݴ������⣡");
//				return strRet;
//			}
//
//			strRet = "getPedOrgnizeMemberSeq OK";
//		}
//			break;
//		case 9: {
//			OrgnizeMemberSum mOrgnizeMemSum = new OrgnizeMemberSum();
//			result = DataSyn.getInstance().getPedorgnizeMemberSum(mPhoneNum, mPassword,
//					mOrgnizeMemSum);
//			strURL = strHttpURL + "pedorgnizemembersum&userid=" + mPhoneNum
//					+ "&psw=" + mPassword;
//			if (result == -1) {
//				strRet = "getPedorgnizeMemberSum �����ȡ���ڴ���" + strURL;
//				Log.e(TAG, "����ֵΪ-1�������ȡ���ڴ���");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "getPedorgnizeMemberSum �������ݴ�������" + strURL;
//				Log.e(TAG, "����ֵΪ1���������ݴ������⣡");
//				return strRet;
//			}
//
//			strRet = "getPedorgnizeMemberSum OK";
//		}
//			break;
//		case 10: {
//			UpdatePasswordInfo changePwd = new UpdatePasswordInfo();
//			result = DataSyn.getInstance().updatePassWord(mPhoneNum, mPassword, mPassword,
//					mPassword, changePwd);
//			strURL = strHttpURL + "passwordchange&userid="
//					+ mPhoneNum // 13810411683
//					+ "&old_psw=" + mPassword + "&new_psw=" + mPassword
//					+ "&new_psw_ag=" + mPassword;
//			if (result == -1) {
//				strRet = "UpdatePassWord �����ȡ���ڴ���" + strURL;
//				Log.e(TAG, "����ֵΪ-1�������ȡ���ڴ���");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "UpdatePassWord �������ݴ�������" + strURL;
//				Log.e(TAG, "����ֵΪ1���������ݴ������⣡");
//				return strRet;
//			}
//			strRet = "UpdatePassWord OK";
//		}
//			break;
//		case 11: {
//			GroupMemberPkInfo groupMemberPkInfo = new GroupMemberPkInfo();
//			result = DataSyn.getInstance().getGroupMembersPkInfoYestoday(mPhoneNum, mPassword,
//					groupIdInfo.groupid, 1, 100, groupMemberPkInfo,
//					current_date_str, before_date_str);
//			strURL = strHttpURL + "pedgroupmember&userid="
//					+ mPhoneNum // 13810411683
//					+ "&psw=" + mPassword + "&groupid=" + groupIdInfo.groupid
//					+ "&startseqnum=1&endseqnum=100";
//
//			if (result == -1) {
//				strRet = "getGroupMembersPkInfoBySpan �����ȡ���ڴ���" + strURL;
//				Log.e(TAG, "����ֵΪ-1�������ȡ���ڴ���");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "getGroupMembersPkInfoBySpan �������ݴ�������" + strURL;
//				Log.e(TAG, "����ֵΪ1���������ݴ������⣡");
//				return strRet;
//			}
//
//			strRet = "getGroupMembersPkInfoBySpan OK";
//		}
//			break;
//		case 12: {
//			GroupPkInfo groupPkInfo = new GroupPkInfo();
//			result = DataSyn.getInstance().getGroupPkInfoYestoday(mPhoneNum, mPassword,
//					groupIdInfo.groupid, 1, 20, groupPkInfo, current_date_str,
//					before_date_str);
//			strURL = strHttpURL
//					+ "pedgrouppkbyspan&userid="
//					+ mPhoneNum // 13810411683
//					+ "&psw="
//					+ mPassword
//					+ "&groupid="
//					+ groupIdInfo.groupid
//					+ "&startseqnum=1&endseqnum=100&startdate=2013-03-17&enddate=2013-03-15";
//			if (result == -1) {
//				strRet = "getGroupPkInfoBySpan �����ȡ���ڴ���" + strURL;
//				Log.e(TAG, "����ֵΪ-1�������ȡ���ڴ���");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "getGroupPkInfoBySpan �������ݴ�������" + strURL;
//				Log.e(TAG, "����ֵΪ1���������ݴ������⣡");
//				return strRet;
//			}
//
//			strRet = "getGroupPkInfoBySpan OK";
//		}
//			break;
//		case 13: {
//			OrgnizeMemberPKInfo orgnizeMemberPKInfo = new OrgnizeMemberPKInfo();
//			result = DataSyn.getInstance().getOrgnizeMembersPkInfoYestoday(mPhoneNum,
//					mPassword, 1, 20, orgnizeMemberPKInfo, current_date_str,
//					before_date_str);
////			result = DataSyn.getInstance().getOrgnizeMembersPkInfoBySpan(mPhoneNum,
////					mPassword, 1, 20, orgnizeMemberPKInfo, current_date_str,
////					before_date_str);
//
//			strURL = strHttpURL
//					+ "pedorgnizememberbyspan&userid="
//					+ mPhoneNum
//					+ "&psw="
//					+ mPassword
//					+ "&startseqnum=1&endseqnum=100&startdate=2013-03-17&enddate=2013-03-15";
//
//			if (result == -1) {
//				strRet = "getOrgnizeMembersPkInfoBySpan �����ȡ���ڴ���" + strURL;
//				Log.e(TAG, "����ֵΪ-1�������ȡ���ڴ���");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "getOrgnizeMembersPkInfoBySpan �������ݴ�������" + strURL;
//				Log.e(TAG, "����ֵΪ1���������ݴ������⣡");
//				return strRet;
//			}
//			strRet = "getOrgnizeMembersPkInfoBySpan OK";
//		}
//			break;
//		case 14: {
//			ActivityMedalInfo activityMedalInfo = new ActivityMedalInfo();
//			result = DataSyn.getInstance().getAvtivityMedalInfo(mPhoneNum, mPassword,
//					activityMedalInfo, "3");
//
//			strURL = strHttpURL + "memberactivityrank&userid=" + mPhoneNum // 13810411683
//					+ "&psw=" + mPassword + "&activityid=3";
//
//			if (result == -1) {
//				strRet = "getOrgnizeMembersPkInfoBySpan �����ȡ���ڴ���" + strURL;
//				Log.e(TAG, "����ֵΪ-1�������ȡ���ڴ���");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "getAvtivityMedalInfo �������ݴ�������" + strURL;
//				Log.e(TAG, "����ֵΪ1���������ݴ������⣡");
//				return strRet;
//			}
//			strRet = "getAvtivityMedalInfo OK";
//		}
//			break;
//		default:
//			strRet = "ȫ��OK";
//			break;
//		}
//
//		return strRet;
//	}
//}
