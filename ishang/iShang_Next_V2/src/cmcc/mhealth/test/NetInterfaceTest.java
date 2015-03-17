///********************************************
// * 文件名		：NetInterfaceTest.java
// * 版本信息	：1.00
// * 创建人：Gaofei - 高飞
// * 创建时间：2013-4-9 下午1:50:30   
// * 修改人：Gaofei - 高飞
// * 修改时间：2013-4-9 下午1:50:30  
// * 功能描述	：
// * 
// * CopyRight(c) China Mobile 2013   
// * 版权所有   All rights reserved
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
// * 项目名称：iShangTrunk 类名称：NetInterfaceTest 类描述： 创建人：Gaofei - 高飞 创建时间：2013-4-9
// * 下午1:50:30 修改人：Gaofei - 高飞 修改时间：2013-4-9 下午1:50:30 修改备注：
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
//		String strRet = "全部接口正常";
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
//				strRet = "getOrgnizeMembersPkInfoBySpan 网络读取存在错误" + strURL;
//				Log.e(TAG, "返回值为-1，网络读取存在错误！");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "getAvtivityMedalInfo 返回数据存在问题" + strURL;
//				Log.e(TAG, "返回值为1，返回数据存在问题！");
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
//				strRet = "getActivityInfo 网络读取存在错误" + strURL;
//				Log.e(TAG, "返回值为-1，网络读取存在错误！");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "getActivityInfo 返回数据存在问题" + strURL;
//				Log.e(TAG, "返回值为1，返回数据存在问题！");
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
//				strRet = "getGroupIdInfo 网络读取存在错误" + strURL;
//				Log.e(TAG, "返回值为-1，网络读取存在错误！");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "getGroupIdInfo 返回数据存在问题" + strURL;
//				Log.e(TAG, "返回值为1，返回数据存在问题！");
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
//				strRet = "getGroupMembersPkInfo 网络读取存在错误" + strURL;
//				Log.e(TAG, "返回值为-1，网络读取存在错误！");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "getGroupMembersPkInfo 返回数据存在问题" + strURL;
//				Log.e(TAG, "返回值为1，返回数据存在问题！");
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
//				strRet = "getGroupPkInfo 网络读取存在错误" + strURL;
//				Log.e(TAG, "返回值为-1，网络读取存在错误！");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "getGroupPkInfo 返回数据存在问题" + strURL;
//				Log.e(TAG, "返回值为1，返回数据存在问题！");
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
//				strRet = "getOrgnizeMembersPkInfo 网络读取存在错误" + strURL;
//				Log.e(TAG, "返回值为-1，网络读取存在错误！");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "getOrgnizeMembersPkInfo 返回数据存在问题" + strURL;
//				Log.e(TAG, "返回值为1，返回数据存在问题！");
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
//				strRet = "getPedoDetailInfo 网络读取存在错误" + strURL;
//				Log.e(TAG, "返回值为-1，网络读取存在错误！");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "getPedoDetailInfo 返回数据存在问题" + strURL;
//				Log.e(TAG, "返回值为1，返回数据存在问题！");
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
//				strRet = "getPedometorInfo 网络读取存在错误" + strURL;
//				Log.e(TAG, "返回值为-1，网络读取存在错误！");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "getPedometorInfo 返回数据存在问题" + strURL;
//				Log.e(TAG, "返回值为1，返回数据存在问题！");
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
//				strRet = "getPedOrgnizeMemberSeq 网络读取存在错误" + strURL;
//				Log.e(TAG, "返回值为-1，网络读取存在错误！");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "getPedOrgnizeMemberSeq 返回数据存在问题" + strURL;
//				Log.e(TAG, "返回值为1，返回数据存在问题！");
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
//				strRet = "getPedorgnizeMemberSum 网络读取存在错误" + strURL;
//				Log.e(TAG, "返回值为-1，网络读取存在错误！");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "getPedorgnizeMemberSum 返回数据存在问题" + strURL;
//				Log.e(TAG, "返回值为1，返回数据存在问题！");
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
//				strRet = "UpdatePassWord 网络读取存在错误" + strURL;
//				Log.e(TAG, "返回值为-1，网络读取存在错误！");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "UpdatePassWord 返回数据存在问题" + strURL;
//				Log.e(TAG, "返回值为1，返回数据存在问题！");
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
//				strRet = "getGroupMembersPkInfoBySpan 网络读取存在错误" + strURL;
//				Log.e(TAG, "返回值为-1，网络读取存在错误！");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "getGroupMembersPkInfoBySpan 返回数据存在问题" + strURL;
//				Log.e(TAG, "返回值为1，返回数据存在问题！");
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
//				strRet = "getGroupPkInfoBySpan 网络读取存在错误" + strURL;
//				Log.e(TAG, "返回值为-1，网络读取存在错误！");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "getGroupPkInfoBySpan 返回数据存在问题" + strURL;
//				Log.e(TAG, "返回值为1，返回数据存在问题！");
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
//				strRet = "getOrgnizeMembersPkInfoBySpan 网络读取存在错误" + strURL;
//				Log.e(TAG, "返回值为-1，网络读取存在错误！");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "getOrgnizeMembersPkInfoBySpan 返回数据存在问题" + strURL;
//				Log.e(TAG, "返回值为1，返回数据存在问题！");
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
//				strRet = "getOrgnizeMembersPkInfoBySpan 网络读取存在错误" + strURL;
//				Log.e(TAG, "返回值为-1，网络读取存在错误！");
//				return strRet;
//			} else if (result == 1) {
//				strRet = "getAvtivityMedalInfo 返回数据存在问题" + strURL;
//				Log.e(TAG, "返回值为1，返回数据存在问题！");
//				return strRet;
//			}
//			strRet = "getAvtivityMedalInfo OK";
//		}
//			break;
//		default:
//			strRet = "全部OK";
//			break;
//		}
//
//		return strRet;
//	}
//}
