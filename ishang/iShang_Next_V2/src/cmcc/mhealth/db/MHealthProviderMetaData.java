/********************************************
 * File Name  MHealthProviderMetaData.java
 * Version    1.0
 * @Author    DaiPengfei - 戴鹏飞
 * @Date      2012-7-23
 * Describe   功能描述
 * 
 * CopyRight(c) China Mobile 2012
 * All rights reserved
 *******************************************/
package cmcc.mhealth.db;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import cmcc.mhealth.bean.ActivityDetailData;
import cmcc.mhealth.bean.ActivityInfo;
import cmcc.mhealth.bean.ActivityMedalInfo;
import cmcc.mhealth.bean.CommonUserSearchInfos;
import cmcc.mhealth.bean.ContectData;
import cmcc.mhealth.bean.ContectGroupData;
import cmcc.mhealth.bean.DataDetailPedo;
import cmcc.mhealth.bean.DataPedometor;
import cmcc.mhealth.bean.DetailGPSData;
import cmcc.mhealth.bean.ListGPSData;
import cmcc.mhealth.bean.GPSListInfo;
import cmcc.mhealth.bean.GpsInfoDetail;
import cmcc.mhealth.bean.GroupInfo;
import cmcc.mhealth.bean.GroupMemberInfo;
import cmcc.mhealth.bean.GroupMemberPkInfo;
import cmcc.mhealth.bean.GroupPkInfo;
import cmcc.mhealth.bean.ListActivity;
import cmcc.mhealth.bean.MedalInfo;
import cmcc.mhealth.bean.OrgnizeMemberInfo;
import cmcc.mhealth.bean.OrgnizeMemberPKInfo;
import cmcc.mhealth.bean.PedoDetailInfo;
import cmcc.mhealth.bean.RaceInfo;
import cmcc.mhealth.bean.RankUserBean;
import cmcc.mhealth.bean.RequestData;
import cmcc.mhealth.bean.VitalSignInfoDataBean;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Constants;
import cmcc.mhealth.common.Logger;

public class MHealthProviderMetaData {
	private static final String TAG = "MHealthProviderMetaData";

	public static final String DATABASE_NAME = "pedometer_db";
	public static String SEARCH_DATABASE_PATH = "";
	// public static final int DATABASE_VERSION = 1;

	private static MHealthProviderMetaData mHealthProviderMetaData = null;
	private static DatabaseHelper dbHelper;

	private MHealthProviderMetaData(Context context) {
		dbHelper = new DatabaseHelper(context, DATABASE_NAME);
//		String path = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/" + context.getPackageName() + "/" + "contectsshort.db";
	}

	public static MHealthProviderMetaData GetMHealthProvider(Context context) {
		if (mHealthProviderMetaData == null)
			mHealthProviderMetaData = new MHealthProviderMetaData(context);
		return mHealthProviderMetaData;
	}

	// *********************PedometerTableMetaData******************************
	public Cursor GetPedometerData() {
		return PedometerTableMetaData.GetAllValueCursor(dbHelper);
	}

	public DataPedometor getPedometerLatest() {
		Cursor cursor = PedometerTableMetaData.GetAllValueCursor(dbHelper);
		DataPedometor pedoInfo = null;
		if (null == cursor)
			return null;
		// 向后移一次取昨天的数据
		try {
			if (cursor.moveToNext()) {
				pedoInfo = PedometerTableMetaData.GetPedometerFromCursor(cursor);
			}
		} finally {
			cursor.close();
		}
		return pedoInfo;
	}
	//手机计步
	public DataPedometor getMobilePedometerLatest() {
		Cursor cursor = MobilePedometerTableMetaData.GetAllValueCursor(dbHelper);
		DataPedometor pedoInfo = null;
		if (null == cursor)
			return null;
		// 向后移一次取昨天的数据
		try {
			if (cursor.moveToNext()) {
				pedoInfo = MobilePedometerTableMetaData.GetPedometerFromCursor(cursor);
			}
		} finally {
			cursor.close();
		}
		return pedoInfo;
	}

	/**
	 * 
	 * @param searchDate
	 *            格式为20121212
	 * @return
	 */
	public DataPedometor getPedometerByDate(String searchDate) {
		searchDate = Common.getYYYYMMDDToYYYY_MM_DD(searchDate);
		Cursor cursor = PedometerTableMetaData.GetAllValueCursor(dbHelper, searchDate);
		DataPedometor pedoInfo = null;
		if (null == cursor)
			return null;
		// 向后移一次取昨天的数据
		try {
			if (cursor.moveToNext()) {
				pedoInfo = PedometerTableMetaData.GetPedometerFromCursor(cursor);
			}
		} finally {
			cursor.close();
		}
		return pedoInfo;
	}

	public Cursor GetPedometerData(long id) {
		return PedometerTableMetaData.GetValueCursor(dbHelper, id);
	}

	public long getStartDateOfPedoInfo() {
		Cursor cursor = PedometerTableMetaData.GetALLValueCursorByASC(dbHelper);
		long startTime = 0;
		try {
			if (cursor.moveToNext()) {
				String startTimeStr = cursor.getString(cursor.getColumnIndex(PedometerTableMetaData.DATE)).substring(0, 10);
				startTime = Common.getDateFromYYYY_MM_DD(startTimeStr);
			}
		} finally {
			cursor.close();
		}
		return startTime;
	}

	/**
	 * 
	 * @param dayNum
	 * @param preDayNum
	 * @return
	 */
	public ArrayList<DataPedometor> getPeriodPedoInfoFromLatest(int dayNum, int preDayNum) {
		Cursor cursor = PedometerTableMetaData.GetAllValueCursor(dbHelper);
		ArrayList<DataPedometor> pedoList = new ArrayList<DataPedometor>();

		long currentDateLong = 0;
		try {
			while (cursor.moveToNext()) {
				String createtimeStr = cursor.getString(cursor.getColumnIndex(PedometerTableMetaData.DATE));
				String createDateStr = createtimeStr.substring(0, 10); // 比较日期
				long createDateLong = Common.getDateFromStr(createDateStr);

				if (preDayNum > 0) {
					if (0 == currentDateLong) {
						currentDateLong = createDateLong;
						--preDayNum;
					} else {
						long diff = (currentDateLong - createDateLong) / Constants.DAY_MILLSECONDS;
						if (diff > 0) {
							preDayNum = preDayNum - (int) diff;
							if (preDayNum < 0) {
								int border = preDayNum;
								if ((dayNum + preDayNum) < 0) {
									border = -1 * dayNum;
								}
								for (int i = -1; i > border; i--) {
									pedoList.add(new DataPedometor(currentDateLong + i * Constants.DAY_MILLSECONDS));
								}
								dayNum = dayNum + preDayNum;
								if (dayNum >= 0) {
									pedoList.add(PedometerTableMetaData.GetPedometerFromCursor(cursor));
								}
							}
							currentDateLong = createDateLong;
						}
					}
				} else { // preDayNum <=0
					if (0 == currentDateLong) {
						currentDateLong = createDateLong;
						pedoList.add(PedometerTableMetaData.GetPedometerFromCursor(cursor));
						--dayNum;
					} else {
						if (dayNum <= 0)
							break;
						long diff = (currentDateLong - createDateLong) / Constants.DAY_MILLSECONDS;
						if (diff > 0) {
							int border = (int) diff;

							if ((dayNum - border) < 0) {
								border = dayNum;
							}

							for (int i = 1; i < border; i++) {
								pedoList.add(new DataPedometor(currentDateLong - i * Constants.DAY_MILLSECONDS));
							}
							if (dayNum >= diff) // 判断最后一项是否添加
								pedoList.add(PedometerTableMetaData.GetPedometerFromCursor(cursor));

							currentDateLong = createDateLong;

							dayNum = dayNum - (int) diff;
						}
					}
				}
			}
		} finally {
			cursor.close();
		}
		return pedoList;
	}

	public void InsertPedometerData(List<DataPedometor> pedoData, long currrentDate, boolean check) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		for (DataPedometor dataPedometor : pedoData) {
			long date = Common.getDateFromYYYYMMDDHHMMSSCreateTime(dataPedometor.createtime);
			if (check && date <= currrentDate)
				continue;
			PedometerTableMetaData.InsertValue(db, dataPedometor.createtime, dataPedometor.data.power + "", dataPedometor.data.weight + "", dataPedometor.data.step + "", dataPedometor.data.cal + "", dataPedometor.data.stepNum + "", dataPedometor.data.distance + "",
					dataPedometor.data.strength1 + "", dataPedometor.data.strength2 + "", dataPedometor.data.strength3 + "", dataPedometor.data.strength4 + "", dataPedometor.data.transType + "", dataPedometor.data.yxbssum + "");
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	public void deletePedometerData(String id) {
		PedometerTableMetaData.DeleteData(dbHelper, id);
	}

	public void deletePedometerData() {
		PedometerTableMetaData.DeleteData(dbHelper);
	}

	// 更新所有简报数据
	public void updatePedometerData(long id, DataPedometor pedoInfo) {
		PedometerTableMetaData.UpdateValue(dbHelper, id, pedoInfo.createtime, pedoInfo.data.power + "", pedoInfo.data.weight + "", pedoInfo.data.step + "", pedoInfo.data.cal + "", pedoInfo.data.stepNum + "", pedoInfo.data.distance + "", pedoInfo.data.strength1 + "", pedoInfo.data.strength2 + "",
				pedoInfo.data.strength3 + "", pedoInfo.data.strength4 + "", pedoInfo.data.transType + "", pedoInfo.data.yxbssum + "");
	}

	// 更新简报日志数据
	public void updatePedometerData(long id, int mood_level, String mood_text, int sports_type) {
		PedometerTableMetaData.UpdateData(dbHelper, id, mood_level, mood_text, sports_type);
	}

	// *********************PedoDetailTableMetaData******************************
	/**
	 * 插入详细报数据
	 * 
	 * @param pedoDetailData
	 */
	public void insertPedoDetailData(PedoDetailInfo pedoDetailData) {
		if (pedoDetailData == null || pedoDetailData.datavalue == null) {
			Logger.w(TAG, "pedoDetailData is null");
			return;
		}

		int size = pedoDetailData.datavalue.size();
		String phone_num = pedoDetailData.phoneNum;
		String date = pedoDetailData.date;

		int hour24[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		for (int i = 0; i < size; i++) {
			DataDetailPedo datavalue = pedoDetailData.datavalue.get(i);
			// 防止重复插入数据
			int time = Integer.valueOf(datavalue.start_time);
			if (hour24[time] == 0) {
				PedoDetailTableMetaData.InsertValue(db, phone_num, date, datavalue.start_time, datavalue.snp5, datavalue.knp5, datavalue.level2p5, datavalue.level3p5, datavalue.level4p5, datavalue.yuanp5, datavalue.snyxp5);
				hour24[time] = 1;
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	public void updatePedoDetailData(long id, String snyx) {
		PedoDetailTableMetaData.UpdateData(dbHelper, id, snyx);
	}

	public void deletePedoDetailData() {
		PedoDetailTableMetaData.DeleteData(dbHelper);
	}

	public void deletePedoDetailData(String id) {
		PedoDetailTableMetaData.DeleteData(dbHelper, id);
	}

	public PedoDetailInfo getPedoDetailLatest() {
		PedoDetailInfo pedoInfo = null;
		Cursor cursor = PedoDetailTableMetaData.GetValueCursor(dbHelper);
		try {
			if (cursor.moveToNext()) {
				pedoInfo = new PedoDetailInfo();
				String latestDay = cursor.getString(cursor.getColumnIndex(PedoDetailTableMetaData.DATE));
				pedoInfo = getPedoDetailByDay(latestDay);
			}
		} finally {
			cursor.close();
		}
		return pedoInfo;
	}

	/**
	 * 
	 * @param search_date
	 *            yyyyMMdd
	 * @return
	 */
	public PedoDetailInfo getPedoDetailByDay(String search_date) {
		Cursor cursor = PedoDetailTableMetaData.GetValueCursor(dbHelper, search_date);
		PedoDetailInfo pedoInfo = new PedoDetailInfo();
		pedoInfo.datavalue = new ArrayList<DataDetailPedo>();
		try {
			while (cursor.moveToNext()) {
				DataDetailPedo dataDetail = new DataDetailPedo();

				dataDetail.start_time = cursor.getString(cursor.getColumnIndex(PedoDetailTableMetaData.START_TIME));
				dataDetail.knp5 = cursor.getString(cursor.getColumnIndex(PedoDetailTableMetaData.CAL_PER_FIVE));
				dataDetail.snp5 = cursor.getString(cursor.getColumnIndex(PedoDetailTableMetaData.STEP_NUM_PER_FIVE));
				dataDetail.level2p5 = cursor.getString(cursor.getColumnIndex(PedoDetailTableMetaData.STRENGTH_TWO_PER_FIVE));
				dataDetail.level3p5 = cursor.getString(cursor.getColumnIndex(PedoDetailTableMetaData.STRENGTH_THREE_PER_FIVE));
				dataDetail.level4p5 = cursor.getString(cursor.getColumnIndex(PedoDetailTableMetaData.STRENGTH_FOUR_PER_FIVE));
				dataDetail.yuanp5 = cursor.getString(cursor.getColumnIndex(PedoDetailTableMetaData.ACC_PER_FIVE));
				dataDetail.snyxp5 = cursor.getString(cursor.getColumnIndex(PedoDetailTableMetaData.EFF_STEPNUM_PER_FIVE));

				pedoInfo.datavalue.add(dataDetail);
			}
		} finally {
			cursor.close();
		}

		pedoInfo.date = search_date;
		return pedoInfo;
	}

	// *********************GroupInfoTableMetaData************************************
	public void createGroupPkInfoTable() {
		GroupPkInfoTableMetaData.CreateTable(dbHelper);
	}

	private GroupPkInfo getGroupPkInfoFromCursor(Cursor cursor) {
		GroupPkInfo groupPkInfo = new GroupPkInfo();
		GroupInfo groupInfo;
		try {
			while (cursor.moveToNext()) {
				groupInfo = new GroupInfo();
				groupInfo.groupname = cursor.getString(cursor.getColumnIndex(GroupPkInfoTableMetaData.GROUP_NAME));
				groupInfo.groupid = cursor.getString(cursor.getColumnIndex(GroupPkInfoTableMetaData.GROUP_ID));
				groupInfo.groupseq = cursor.getString(cursor.getColumnIndex(GroupPkInfoTableMetaData.GROUP_SEQ));
				groupInfo.groupscore = cursor.getString(cursor.getColumnIndex(GroupPkInfoTableMetaData.GROUP_SCORE));
				groupInfo.group7avgstep = cursor.getString(cursor.getColumnIndex(GroupPkInfoTableMetaData.GROUP7AVGSTEP));
				groupInfo.group7avgdist = cursor.getString(cursor.getColumnIndex(GroupPkInfoTableMetaData.GROUP7AVGDIST));

				groupPkInfo.grouppkdata.add(groupInfo);
			}
		} finally {
			cursor.close();
		}
		return groupPkInfo;
	}

	public GroupPkInfo getGroupPkInfoToday(int clubid) {
		Cursor cursor = GroupPkInfoTableMetaData.GetValueCursorToday(dbHelper, clubid);
		return getGroupPkInfoFromCursor(cursor);
	}

	public String getGroupIdFromName(String name,int clubid) {
		String id = null;
		Cursor cursor = GroupPkInfoTableMetaData.GetGroupIdFromName(dbHelper, name, clubid);
		try {
			while (cursor.moveToNext()) {
				id = cursor.getString(cursor.getColumnIndex(GroupPkInfoTableMetaData.GROUP_ID));
			}
		} finally {
			cursor.close();
		}
		return id;
	}
	public String getGroupIdFromName1(String name,int clubid) {
		String id = null;
		Cursor cursor = GroupPkInfoTableMetaData.GetGroupIdFromName(dbHelper, name, clubid);
		try {
			if (cursor.moveToNext()) {
				id = cursor.getString(cursor.getColumnIndex(GroupPkInfoTableMetaData.GROUP_ID));
			}
		} finally {
			cursor.close();
		}
		return id;
	}

	public GroupPkInfo getGroupPkInfoYesterday(int clubid) {
		Cursor cursor = GroupPkInfoTableMetaData.GetValueCursorYesterday(dbHelper, clubid);
		return getGroupPkInfoFromCursor(cursor);
	}

	public void insertGroupPkInfo(GroupPkInfo groupPkInfo , int clubid) {
		SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
		writableDatabase.beginTransaction();
		for (int i = 0; i < groupPkInfo.grouppkdata.size(); i++) {
			GroupInfo groupInfo = groupPkInfo.grouppkdata.get(i);
			GroupPkInfoTableMetaData.InsertValue(writableDatabase, groupInfo.groupname, groupInfo.groupid, groupInfo.groupseq, groupInfo.group7avgdist, groupInfo.group7avgstep, groupInfo.groupscore, groupInfo.groupinforev2, clubid);
		}
		writableDatabase.setTransactionSuccessful();
		writableDatabase.endTransaction();
	}

	public void deleteGroupPkInfo() {
		GroupPkInfoTableMetaData.DeleteData(dbHelper);
	}
	public void deleteGroupPkInfo(int clubid) {
		GroupPkInfoTableMetaData.DeleteData(dbHelper, clubid);
	}

	// *********************GroupMemberInfoTableMetaData******************************
	public void createGroupMemberInfoTable() {
		GroupMemberInfoTableMetaData.CreateTable(dbHelper);
	}

	public GroupMemberPkInfo getMemberPkInfoToday(int clubid) {
		Cursor cursor = GroupMemberInfoTableMetaData.GetValueCursorToday(dbHelper , clubid);
		return getMemberPkInfoFromCursor(cursor);
	}

	public GroupMemberPkInfo getMemberPkInfoYesterday(int clubid) {
		Cursor cursor = GroupMemberInfoTableMetaData.GetValueCursorYesterday(dbHelper, clubid);
		return getMemberPkInfoFromCursor(cursor);
	}

	private GroupMemberPkInfo getMemberPkInfoFromCursor(Cursor cursor) {
		GroupMemberPkInfo groupMemberPkInfo = new GroupMemberPkInfo();
		GroupMemberInfo memberInfo;
		try {
			while (cursor.moveToNext()) {
				memberInfo = new GroupMemberInfo();

				memberInfo.membername = cursor.getString(cursor.getColumnIndex(GroupMemberInfoTableMetaData.MEMBER_NAME));
				memberInfo.memberseq = cursor.getString(cursor.getColumnIndex(GroupMemberInfoTableMetaData.MEMBER_SEQ));
				memberInfo.memberscore = cursor.getString(cursor.getColumnIndex(GroupMemberInfoTableMetaData.MEMBER_SCORE));
				memberInfo.member7avgstep = cursor.getString(cursor.getColumnIndex(GroupMemberInfoTableMetaData.MEMBER7AVGSTEP));
				memberInfo.member7avgdist = cursor.getString(cursor.getColumnIndex(GroupMemberInfoTableMetaData.MEMBER7AVGDIST));
				memberInfo.memberinforev1 = cursor.getString(cursor.getColumnIndex(GroupMemberInfoTableMetaData.RES1));
				// 时间类型 今日 昨日
				memberInfo.memberinforev2 = cursor.getString(cursor.getColumnIndex(GroupMemberInfoTableMetaData.RES2));
				memberInfo.avatar = cursor.getString(cursor.getColumnIndex(GroupMemberInfoTableMetaData.RES3));
				groupMemberPkInfo.groupmember.add(memberInfo);
			}
		} finally {
			cursor.close();
		}
		return groupMemberPkInfo;
	}

	public void insertMemberPkInfo(GroupMemberPkInfo groupMemberPkInfo,int clubid) {
		SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
		writableDatabase.beginTransaction();
		for (int i = 0; i < groupMemberPkInfo.groupmember.size(); i++) {
			GroupMemberInfo memberInfo = groupMemberPkInfo.groupmember.get(i);
			GroupMemberInfoTableMetaData.InsertValue(writableDatabase, memberInfo.membername, memberInfo.memberseq, memberInfo.member7avgdist, memberInfo.member7avgstep, memberInfo.memberscore, memberInfo.memberinforev1, memberInfo.memberinforev2, memberInfo.avatar, clubid);
		}
		writableDatabase.setTransactionSuccessful();
		writableDatabase.endTransaction();
	}

	public void deleteMemberPkInfo() {
		GroupMemberInfoTableMetaData.DeleteData(dbHelper);
	}

	public void deleteMemberPkInfo(int clubid) {
		GroupMemberInfoTableMetaData.DeleteData(dbHelper, clubid);
	}

	// -----------------------OrginzeInfoTable--------------------
	public void createOrginzeInfoTable() {
		OrgnizeInfoTableMetaData.CreateTable(dbHelper);
	}

	public int getOrgnizeMemberSumToday(int clubid) {
		return OrgnizeInfoTableMetaData.GetAllDataSum(dbHelper, Constants.GROUP_7DAY, clubid);
	}

	public int getOrgnizeMemberSumYesterday(int clubid) {
		return OrgnizeInfoTableMetaData.GetAllDataSum(dbHelper, Constants.GROUP_YESTERDAY, clubid);
	}

	public int getGroupSum(int timetype,int clubid) {
		return GetAllDataSumForAll(dbHelper, GroupPkInfoTableMetaData.TABLE_NAME, GroupPkInfoTableMetaData.RES2, timetype, clubid);
	}

	public int getGroupMemberSum(int timetype,int clubid) {
		return GetAllDataSumForAll(dbHelper, GroupMemberInfoTableMetaData.TABLE_NAME, GroupMemberInfoTableMetaData.RES2, timetype, clubid);
	}

	public OrgnizeMemberPKInfo getOrgnizePkInfoToday(int clubid) {
		Cursor cursor = OrgnizeInfoTableMetaData.GetValueCursor(dbHelper, Constants.GROUP_7DAY , clubid);
		return getOrgnizePkInfoFromCursor(cursor);
	}

	public OrgnizeMemberPKInfo getOrgnizePkInfoYesterday(int clubid) {
		Cursor cursor = OrgnizeInfoTableMetaData.GetValueCursor(dbHelper, Constants.GROUP_YESTERDAY, clubid);
		return getOrgnizePkInfoFromCursor(cursor);
	}

	private OrgnizeMemberPKInfo getOrgnizePkInfoFromCursor(Cursor cursor) {
		OrgnizeMemberPKInfo groupOrgnizeMemberPkInfo = new OrgnizeMemberPKInfo();
		OrgnizeMemberInfo orgnizeMemberInfo;
		try {
			while (cursor.moveToNext()) {
				orgnizeMemberInfo = new OrgnizeMemberInfo();

				orgnizeMemberInfo.membername = cursor.getString(cursor.getColumnIndex(OrgnizeInfoTableMetaData.MEMBER_NAME));
				orgnizeMemberInfo.groupname = cursor.getString(cursor.getColumnIndex(OrgnizeInfoTableMetaData.GROUP_NAME));
				orgnizeMemberInfo.memberseq = cursor.getString(cursor.getColumnIndex(OrgnizeInfoTableMetaData.MEMBER_SEQ));
				orgnizeMemberInfo.memberscore = cursor.getString(cursor.getColumnIndex(OrgnizeInfoTableMetaData.MEMBER_SCORE));
				orgnizeMemberInfo.memberinforev1 = cursor.getString(cursor.getColumnIndex(OrgnizeInfoTableMetaData.MEMBER_FORVE1));
				orgnizeMemberInfo.memberinforev2 = cursor.getString(cursor.getColumnIndex(OrgnizeInfoTableMetaData.MEMBER_FORVE2));
				orgnizeMemberInfo.member7avgstep = cursor.getString(cursor.getColumnIndex(OrgnizeInfoTableMetaData.MEMBER7AVGSTEP));
				orgnizeMemberInfo.member7avgdist = cursor.getString(cursor.getColumnIndex(OrgnizeInfoTableMetaData.MEMBER7AVGDIST));
				orgnizeMemberInfo.avatar = cursor.getString(cursor.getColumnIndex(OrgnizeInfoTableMetaData.AVATER));

				groupOrgnizeMemberPkInfo.orgnizemember.add(orgnizeMemberInfo);
			}
		} finally {
			cursor.close();
		}
		return groupOrgnizeMemberPkInfo;
	}

	public void insertOrgnizeMemberPkInfo(OrgnizeMemberPKInfo orgnizeMemberPkInfo , int clubid) {
		SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
		writableDatabase.beginTransaction();
		for (int i = 0; i < orgnizeMemberPkInfo.orgnizemember.size(); i++) {
			OrgnizeMemberInfo orgnizeMemberInfo = orgnizeMemberPkInfo.orgnizemember.get(i);
			OrgnizeInfoTableMetaData.InsertValue(writableDatabase, orgnizeMemberInfo.membername, orgnizeMemberInfo.groupname, orgnizeMemberInfo.memberseq, orgnizeMemberInfo.member7avgdist, orgnizeMemberInfo.member7avgstep, orgnizeMemberInfo.memberscore, orgnizeMemberInfo.memberinforev1,
					orgnizeMemberInfo.memberinforev2, orgnizeMemberInfo.avatar , clubid);
		}
		writableDatabase.setTransactionSuccessful();
		writableDatabase.endTransaction();
	}

	public void deleteOrgizePkInfo() {
		OrgnizeInfoTableMetaData.DeleteData(dbHelper);
	}

	public void deleteOrgizePkInfo(int clubid) {
		OrgnizeInfoTableMetaData.DeleteData(dbHelper, clubid);
	}

	// *******************GroupINPK****************

	public void createGroupInPkInfoTable() {
		GroupInPKTableMetaData.CreateTable(dbHelper);
	}

	/**
	 * 获取所有数据库数据
	 * 
	 * @return qjj 2013-1-31
	 */
	public GroupMemberPkInfo getGroupInPkInfo() {
		GroupMemberPkInfo groupMemberPkInfo = new GroupMemberPkInfo();
		GroupMemberInfo memberInfo;

		Cursor cursor = GroupInPKTableMetaData.GetAllValueCursor(dbHelper);
		try {
			while (cursor.moveToNext()) {
				memberInfo = new GroupMemberInfo();

				memberInfo.membername = cursor.getString(cursor.getColumnIndex(GroupInPKTableMetaData.MEMBER_NAME));
				memberInfo.memberseq = cursor.getString(cursor.getColumnIndex(GroupInPKTableMetaData.MEMBER_SEQ));
				memberInfo.memberscore = cursor.getString(cursor.getColumnIndex(GroupInPKTableMetaData.MEMBER_SCORE));
				memberInfo.member7avgstep = cursor.getString(cursor.getColumnIndex(GroupInPKTableMetaData.MEMBER7AVGSTEP));
				memberInfo.member7avgdist = cursor.getString(cursor.getColumnIndex(GroupInPKTableMetaData.MEMBER7AVGDIST));
				memberInfo.memberinforev1 = cursor.getString(cursor.getColumnIndex(GroupInPKTableMetaData.MEMBER_FORVE1));
				memberInfo.memberinforev2 = cursor.getString(cursor.getColumnIndex(GroupInPKTableMetaData.MEMBER_FORVE2));
				memberInfo.avatar = cursor.getString(cursor.getColumnIndex(GroupInPKTableMetaData.AVATER));

				groupMemberPkInfo.groupmember.add(memberInfo);
			}
		} finally {
			cursor.close();
		}
		return groupMemberPkInfo;
	}

	/**
	 * 根据 组ID获取数据
	 * 
	 * @param groupId
	 * @return qjj 2013-1-31
	 */
	public GroupMemberPkInfo getGroupInPkByIdInfo(String mGroupId, String mWhatToday,int clubid) {
		GroupMemberPkInfo groupMemberPkInfo = new GroupMemberPkInfo();
		GroupMemberInfo memberInfo;

		int goupId = 0;
		try {
			goupId = Integer.valueOf(mGroupId);
		} catch (Exception e) {
			Log.e(TAG, "groupid is not integer");
		}

		Cursor cursor = GroupInPKTableMetaData.GetYestodayCursor(dbHelper, goupId, mWhatToday, clubid);
		try {
			while (cursor.moveToNext()) {
				memberInfo = new GroupMemberInfo();

				memberInfo.membername = cursor.getString(cursor.getColumnIndex(GroupInPKTableMetaData.MEMBER_NAME));
				memberInfo.memberseq = cursor.getString(cursor.getColumnIndex(GroupInPKTableMetaData.MEMBER_SEQ));
				memberInfo.memberscore = cursor.getString(cursor.getColumnIndex(GroupInPKTableMetaData.MEMBER_SCORE));
				memberInfo.member7avgstep = cursor.getString(cursor.getColumnIndex(GroupInPKTableMetaData.MEMBER7AVGSTEP));
				memberInfo.member7avgdist = cursor.getString(cursor.getColumnIndex(GroupInPKTableMetaData.MEMBER7AVGDIST));
				memberInfo.memberinforev1 = cursor.getString(cursor.getColumnIndex(GroupInPKTableMetaData.MEMBER_FORVE1));
				memberInfo.memberinforev2 = cursor.getString(cursor.getColumnIndex(GroupInPKTableMetaData.MEMBER_FORVE2));
				memberInfo.avatar = cursor.getString(cursor.getColumnIndex(GroupInPKTableMetaData.AVATER));

				groupMemberPkInfo.groupmember.add(memberInfo);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return groupMemberPkInfo;
	}

	/**
	 * 插入数据库
	 * 
	 * @param groupMemberPkInfo
	 *            qjj 2013-1-31
	 */
	public void insertGroupInPkInfo(GroupMemberPkInfo groupMemberPkInfo, String groupid, String groupname, String yestoday , int clubid) {
		SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
		writableDatabase.beginTransaction();
		for (int i = 0; i < groupMemberPkInfo.groupmember.size(); i++) {
			GroupMemberInfo memberInfo = groupMemberPkInfo.groupmember.get(i);
			GroupInPKTableMetaData.InsertValue(writableDatabase, memberInfo.memberseq, memberInfo.membername, groupname, groupid, memberInfo.member7avgstep, memberInfo.member7avgdist, memberInfo.memberscore, memberInfo.memberinforev1, yestoday, memberInfo.avatar, clubid);
		}
		writableDatabase.setTransactionSuccessful();
		writableDatabase.endTransaction();
	}

	/**
	 * 刪除数据库 qjj 2013-1-31
	 */
	public void deleteGroupInPkInfo() {
		Logger.i(TAG, "Delete GroupInPkInfo");
		GroupInPKTableMetaData.DeleteData(dbHelper);
	}
	public void deleteGroupInPkInfoByGroupId(String groupid, int clubid) {
		Logger.i(TAG, "Delete GroupInPkInfo");
		GroupInPKTableMetaData.DeleteData(dbHelper, groupid, clubid);
	}

	// *******************MyActivityTableMetaData****************

	public void createListActivityTable() {
		ListActivityTableMetaData.CreateTable(dbHelper);
	}

	public ActivityInfo getListActivity(int nowNum, int oldNum, int futureNUm , int clubid) {
		ActivityInfo activityInfo = new ActivityInfo();
		ListActivity listActivity;

		Cursor cursor = ListActivityTableMetaData.GetAllValueCursor(dbHelper , clubid);
		try {
			while (cursor.moveToNext()) {
				listActivity = new ListActivity();

				listActivity.activityid = cursor.getString(cursor.getColumnIndex(ListActivityTableMetaData.ACTIVITYID));
				listActivity.activityname = cursor.getString(cursor.getColumnIndex(ListActivityTableMetaData.ACTIVITYNAME));
				listActivity.activityslogan = cursor.getString(cursor.getColumnIndex(ListActivityTableMetaData.ACTIVITYSLOGAN));
				listActivity.activitystart = cursor.getString(cursor.getColumnIndex(ListActivityTableMetaData.ACTIVITYSTART));
				listActivity.activitytype = cursor.getString(cursor.getColumnIndex(ListActivityTableMetaData.ACTIVITYTYPE));
				listActivity.activityend = cursor.getString(cursor.getColumnIndex(ListActivityTableMetaData.ACTIVITYEND));
				listActivity.company_name = cursor.getString(cursor.getColumnIndex(ListActivityTableMetaData.COMPANYNAME));
				listActivity.aimstep = cursor.getString(cursor.getColumnIndex(ListActivityTableMetaData.AIMSTEP));
				listActivity.personnum = cursor.getString(cursor.getColumnIndex(ListActivityTableMetaData.PERSONNUM));
				listActivity.personseq = cursor.getString(cursor.getColumnIndex(ListActivityTableMetaData.PERSONSEQ));
				listActivity.groupnum = cursor.getString(cursor.getColumnIndex(ListActivityTableMetaData.GROUPNUM));
				listActivity.groupseq = cursor.getString(cursor.getColumnIndex(ListActivityTableMetaData.GROUPSEQ));

				int pos = cursor.getPosition();
				if (pos < nowNum) {
					activityInfo.activitynow.add(listActivity);
				} else if (pos >= nowNum && pos < nowNum + oldNum) {
					activityInfo.activityold.add(listActivity);
				} else if (pos >= nowNum + oldNum) {
					activityInfo.activityfuture.add(listActivity);
				}

			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return activityInfo;
	}

	/**
	 * insertActivityInfo(插入活动列表)
	 * 
	 * @param List
	 *            <ActivityDetail> activityList
	 * @return void
	 * @Exception 异常对象
	 * @创建人：Qiujunjie - 邱俊杰
	 * @创建时间：2013-9-23 下午12:02:35
	 * @修改人：Qiujunjie - 邱俊杰
	 * @修改时间：2013-9-23 下午12:02:35
	 */
	public void insertListActivity(List<ListActivity> activityList , int clubid) {
		for (int i = 0; i < activityList.size(); i++) {
			ListActivity listActivity = activityList.get(i);
			ListActivityTableMetaData.InsertValue(dbHelper, listActivity.activityid, listActivity.activityname, listActivity.activityslogan, listActivity.activitystart, listActivity.activitytype, listActivity.activityend, listActivity.company_name, listActivity.aimstep, listActivity.personnum,
					listActivity.personseq, listActivity.groupnum, listActivity.groupseq , clubid);
		}
	}

	/**
	 * deleteActivityInPkInfo(删除活动列表信息)
	 * 
	 * @return void
	 * @创建人：Qiujunjie - 邱俊杰
	 * @创建时间：2013-9-23 下午12:03:18
	 * @修改人：Qiujunjie - 邱俊杰
	 * @修改时间：2013-9-23 下午12:03:18
	 */
	public void deleteListActivity() {
		Logger.i(TAG, "Delete ActivityInfo");
		ListActivityTableMetaData.DeleteData(dbHelper);
	}
	public void deleteListActivity(int clubid) {
		Logger.i(TAG, "Delete ActivityInfo");
		ListActivityTableMetaData.DeleteData(dbHelper, clubid);
	}

	// *******************MyActivityTableMetaData****************

	/**
	 * createActivityMedailInfoTable(创建我的活动信息和具体活动信息)
	 * 
	 * @return void
	 * @Exception 异常对象
	 * @创建人：Qiujunjie - 邱俊杰
	 * @创建时间：2013-9-23 上午11:59:52
	 * @修改人：Qiujunjie - 邱俊杰
	 * @修改时间：2013-9-23 上午11:59:52
	 */
	public void createActivityDetailTable() {
		ActivityListDetailTableMetaData.CreateTable(dbHelper);
		ActivityMyDetailTableMetaData.CreateTable(dbHelper);
		// ActivityMedailInfoTableMetaData.CreateTable(dbHelper);
	}

	/**
	 * 
	 * getActivityMyMediaInfo(活动详情列表)
	 * 
	 * @param activityid
	 *            活动ID
	 * @return ActivityMedalInfo
	 * @Exception 异常对象
	 * @创建人：Qiujunjie - 邱俊杰
	 * @创建时间：2013-9-22 下午2:56:48
	 * @修改人：Qiujunjie - 邱俊杰
	 * @修改时间：2013-9-22 下午2:56:48
	 */
	public ActivityDetailData getActivityMyDetail(String activityid, int clubid) {
		// ActivityMedalInfo activityMedalInfo = new ActivityMedalInfo();

		ActivityDetailData activityDetailData = new ActivityDetailData();
		// activityMedalInfoData.medalinfo = new ArrayList<MedalInfo>();

		Cursor cursor = ActivityMyDetailTableMetaData.GetValueCursorById(dbHelper, activityid , clubid);
		try {
			if (cursor == null)
				return null;
			while (cursor.moveToNext()) {

				activityDetailData.myname = cursor.getString(cursor.getColumnIndex(ActivityMyDetailTableMetaData.MYNAME));
				activityDetailData.mygroup = cursor.getString(cursor.getColumnIndex(ActivityMyDetailTableMetaData.MYGROUP));
				activityDetailData.avgstep = cursor.getString(cursor.getColumnIndex(ActivityMyDetailTableMetaData.AVGSTEP));
				activityDetailData.ratescore = cursor.getString(cursor.getColumnIndex(ActivityMyDetailTableMetaData.RATESCORE));
				activityDetailData.hitduration = cursor.getString(cursor.getColumnIndex(ActivityMyDetailTableMetaData.HITDURATION));
				activityDetailData.groupratescore = cursor.getString(cursor.getColumnIndex(ActivityMyDetailTableMetaData.GROUP_RATESCORE));
				activityDetailData.groupavgstep = cursor.getString(cursor.getColumnIndex(ActivityMyDetailTableMetaData.GROUPAVGSTEP));

				// activityMedalInfoData.medalinfo.clear();
				// for (int i = 0; i <
				// ActivityMedailInfoTableMetaData.mMedalArr.length; i++) {
				// MedalInfo medalInfo = new MedalInfo();
				//
				// medalInfo.medalname =
				// cursor.getString(cursor.getColumnIndex(ActivityMedailInfoTableMetaData.mMedalArr[i][0]));
				// medalInfo.medaltype =
				// cursor.getString(cursor.getColumnIndex(ActivityMedailInfoTableMetaData.mMedalArr[i][1]));
				// medalInfo.rank =
				// cursor.getString(cursor.getColumnIndex(ActivityMedailInfoTableMetaData.mMedalArr[i][2]));
				// medalInfo.medalsnum =
				// cursor.getString(cursor.getColumnIndex(ActivityMedailInfoTableMetaData.mMedalArr[i][3]));
				// medalInfo.medalgap =
				// cursor.getString(cursor.getColumnIndex(ActivityMedailInfoTableMetaData.mMedalArr[i][4]));
				// medalInfo.beatpercent =
				// cursor.getString(cursor.getColumnIndex(ActivityMedailInfoTableMetaData.mMedalArr[i][5]));
				// medalInfo.score =
				// cursor.getString(cursor.getColumnIndex(ActivityMedailInfoTableMetaData.mMedalArr[i][6]));
				// activityMedalInfoData.medalinfo.add(medalInfo);
				// }
				//
				// activityMedalInfo.datavalue = activityMedalInfoData;

			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return activityDetailData;
	}

	/**
	 * 
	 * getActivityMediaInfo(活动的列表)
	 * 
	 * @param
	 * @return ArrayList<MedalInfo>
	 * @Exception 异常对象
	 * @创建人：Qiujunjie - 邱俊杰
	 * @创建时间：2013-9-22 下午3:12:30
	 * @修改人：Qiujunjie - 邱俊杰
	 * @修改时间：2013-9-22 下午3:12:30
	 */
	public ArrayList<MedalInfo> getActivityListDetail(String activityid, int clubid) {
		ArrayList<MedalInfo> arr = new ArrayList<MedalInfo>();
		Cursor cursor = ActivityListDetailTableMetaData.GetAllValueCursor(dbHelper, activityid , clubid);
		try {
			while (cursor.moveToNext()) {
				MedalInfo medalInfo = new MedalInfo();
				medalInfo.medalname = cursor.getString(cursor.getColumnIndex(ActivityListDetailTableMetaData.MEDAL_NAME));
				medalInfo.medaltype = cursor.getString(cursor.getColumnIndex(ActivityListDetailTableMetaData.MEDAL_TYPE));
				medalInfo.rank = cursor.getString(cursor.getColumnIndex(ActivityListDetailTableMetaData.RANK));
				medalInfo.medalsnum = cursor.getString(cursor.getColumnIndex(ActivityListDetailTableMetaData.MEDAL_SUM));
				medalInfo.medalgap = cursor.getString(cursor.getColumnIndex(ActivityListDetailTableMetaData.MEDAL_GAP));
				medalInfo.beatpercent = cursor.getString(cursor.getColumnIndex(ActivityListDetailTableMetaData.BEATPERCENT));
				medalInfo.score = cursor.getString(cursor.getColumnIndex(ActivityListDetailTableMetaData.SCORE));
				arr.add(medalInfo);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return arr;
	}

	/**
	 * 
	 * insertActivityMediaInfo(添加活动详情，包括我的活动信和具体活动信息)
	 * 
	 * @param ActivityMedalInfo
	 *            activityMedalInfos
	 * @param activityid
	 *            活动ID
	 * @return void
	 * @创建人：Qiujunjie - 邱俊杰
	 * @创建时间：2013-9-23 上午11:57:56
	 * @修改人：Qiujunjie - 邱俊杰
	 * @修改时间：2013-9-23 上午11:57:56
	 */
	public void insertActivityDetail(ActivityMedalInfo activityMedalInfos, String activityid,int clubid) {
		ActivityMedalInfo medalInfoData = activityMedalInfos;
		List<MedalInfo> arr = medalInfoData.datavalue.medalinfo;
		for (MedalInfo medalInfo : arr) {
			if (medalInfo != null) {
				medalInfo.activityid = medalInfoData.activityid;
			}
		}
		// 我的活动详情
		ActivityMyDetailTableMetaData.InsertValue(dbHelper, medalInfoData.datavalue.myname, medalInfoData.datavalue.mygroup, medalInfoData.datavalue.avgstep, medalInfoData.datavalue.ratescore, activityid, medalInfoData.datavalue.hitduration, medalInfoData.datavalue.groupavgstep,
				medalInfoData.datavalue.groupratescore,clubid);
		// 活动排名详情
		ActivityListDetailTableMetaData.InsertValue(dbHelper, arr, clubid);
	}

	public void deleteActivityMediaInPkInfo() {
		Logger.i(TAG, "Delete ActivityInfo");  
		ActivityMyDetailTableMetaData.DeleteData(dbHelper);
		ActivityListDetailTableMetaData.DeleteData(dbHelper);
	}
	public void deleteActivityMediaInPkInfo(int clubid) {
		Logger.i(TAG, "Delete ActivityInfo");
		ActivityMyDetailTableMetaData.DeleteData(dbHelper, clubid);
		ActivityListDetailTableMetaData.DeleteData(dbHelper, clubid);
	}

	public int GetAllDataSumForAll(DatabaseHelper dbHelper, String tablename, String key, int timeType,int clubid) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT COUNT(*) AS CNT FROM " + tablename + " WHERE " + key + " = " + timeType + " and clubid = " + clubid, null);
		cursor.moveToNext();
		return cursor.getInt(cursor.getColumnIndex("CNT"));
	}
	
	// 好友数据===========
	// 返回数据库中好友数目
	public int getFriendCount() {
		return FriendMetaData.getFriendCount(dbHelper);
	}
	// 存储好友数据-------事物
	public void FriendInsertValue(List<OrgnizeMemberInfo> friendinfos) {
		FriendMetaData.MyFriendInsertValue(dbHelper, friendinfos, "1");
	}
	// 获取好友数据
	public List<OrgnizeMemberInfo> getMyFriends() {
		return FriendMetaData.getMyFriends(dbHelper);
	}

	// 删除排名数据-------
	public void deleteMyFriend() {
		FriendMetaData.DeleteMyFriendData(dbHelper);
	}

	public void deleteMyFriend(String friendphone) {
		FriendMetaData.DeleteMyFriendData(dbHelper, friendphone);
	}

	// ===========

	// 存储排名数据-------
	public void MyRankInsertValue(String memberseq, String membername, String groupname, String member7avgstep, String type, String imageurl, int clubid) {
		MyRankMetaData.MyRankInsertValue(dbHelper, memberseq, membername, groupname, member7avgstep, type, imageurl, clubid);
	}

	// 存储排名数据-------事物
	public void MyRankInsertValue(List<OrgnizeMemberInfo> orgnizemember, String type, int clubid) {
		MyRankMetaData.MyRankInsertValue(dbHelper, orgnizemember, type, clubid);
	}

	public void MyRankInsertValueGP(List<GroupMemberInfo> groupmember, String type, int clubid) {
		MyRankMetaData.MyRankInsertValueGP(dbHelper, groupmember, type, clubid);
	}

	// 存储排名数据-------事物
	public void MyRankInsertValueGroup(List<GroupInfo> grouppkdata, String type, int clubid) {
		MyRankMetaData.MyRankInsertValueGroup(dbHelper, grouppkdata, type, clubid);
	}

	// 删除排名数据-------
	public void MyRankDeleteData(int clubid) {
		MyRankMetaData.deleteMyRankData(dbHelper, clubid);
	}
	// 删除排名数据-------
	public void MyRankDeleteData() {
		MyRankMetaData.deleteMyRankData(dbHelper);
	}

	// 获取排名数据-------
	public List<RankUserBean> getMyRankByType(int type,int clubid) {
		return MyRankMetaData.getMyRankByType(dbHelper, type,clubid);
	}

	// 查询联系人数据==============================================

	public List<CommonUserSearchInfos> getAllSearchInfos(int clubid) {
		return RelatedMetaData.getAllSearchInfos(dbHelper, clubid);
	}

	public void insertContacts(ArrayList<ContectData> list,int clubid) {
		RelatedMetaData.InsertContactValue(dbHelper, list, clubid);
	}

	public void removeContactAllDatas() {
		RelatedMetaData.DeleteAllData(dbHelper);
	}

	// 查询组数据===========================================================
	public List<ContectGroupData> getAllSearchGroupInfos(int clubid) {
		return RelatedGroupMetaData.getAllSearchGroupInfos(dbHelper, clubid);
	}

	public void insertGroupContacts(ArrayList<ContectGroupData> list,int clubid) {
		RelatedGroupMetaData.InsertContactValue(dbHelper, list, clubid);
	}

	public void removeContactGroupAllDatas() {
		RelatedGroupMetaData.DeleteAllData(dbHelper);
	}

	/**
	 * 获取指定天数之后的体重数据集合
	 * 
	 * @param timemill
	 *            时间点 long值
	 * @return
	 */
	public List<VitalSignInfoDataBean> getWeightInRange(long timemill ,String timetype) {
		return VitalSignMetaData.getVitalSignInRange(dbHelper, timemill, VitalSignMetaData.TYPE_WEIGHT,timetype);
	}

	/**
	 * 获取指定天数之后的体重数据集合
	 * 
	 * @param timemill
	 *            时间点 String值 格式为yyyy-MM-dd_HH:mm:ss
	 * @return
	 */
	public List<VitalSignInfoDataBean> getWeightInRange(String time ,String timetype) {
		return VitalSignMetaData.getVitalSignInRange(dbHelper, Common.getDateFromStrFromServel(time), VitalSignMetaData.TYPE_WEIGHT,timetype);
	}

	// 写入体重数据===============
	public void addWeightValue(List<VitalSignInfoDataBean> listVsb) {
		VitalSignMetaData.InsertValue(dbHelper, listVsb, VitalSignMetaData.TYPE_WEIGHT);
	}

	// 写入体重数据===============
	public void addWeightValue(VitalSignInfoDataBean vitalSignBean) {
		VitalSignMetaData.InsertValue(dbHelper, vitalSignBean, VitalSignMetaData.TYPE_WEIGHT);
	}
	// 删除体重数据===============
	public void deleteVitalSignValue() {
		VitalSignMetaData.deleteVitalSignData(dbHelper);
	}
	// 写入竞赛数据
	public void insertRaceValue(RaceInfo raceInfos, int state) {
		RaceMetaData.RaceInsertValue(dbHelper, raceInfos.racelistinfo, state);
	}
	// 读取入竞赛数据
	public RaceInfo getRaces(int num, int startid, int state) {
		return RaceMetaData.getRaces(dbHelper, num, startid, state);
	}
	// 修改竞赛组员数目数据
	public int modifySingleRaceMemberNum(int membernum, String raceid) {
		return RaceMetaData.modifySingleRace(dbHelper, membernum, raceid);
	}
	// 读取入竞赛数据
	public void DeleteRaceData(int state) {
		RaceMetaData.DeleteRaceData(dbHelper,state);
	}

	// ================
	// 旧消息
	// ================
	// 写入旧消息
	public void OldMsgsInsertValue(RequestData rd) {
		HistroyMessageMetaData.OldMsgsInsertValue(dbHelper, rd);
	}

	// 读取旧消息
	public List<RequestData> getMyOldMsgs() {
		return HistroyMessageMetaData.getMyOldMsgs(dbHelper);
	}
	// 读取旧消息
	public void DeleteMyOldMsgs() {
		HistroyMessageMetaData.DeleteMyOldMsgs(dbHelper);
	}
	
	/**
	 * 插入所有详细包
	 * @param gpsInfoDetail
	 */
	public void insertAllPoints(DetailGPSData gpsInfoDetail,String starttime){
		GpsInfoDetailMetaData.insertData(dbHelper, gpsInfoDetail,starttime);
	}
	/**
	 * 插入一条详细数据
	 * @param gpsInfoDetail
	 */
	public void insertDetail(GpsInfoDetail gpsInfoDetail){
		GpsInfoDetailMetaData.insertDetail(dbHelper, gpsInfoDetail);
	}
	
	/**
	 * 删除详细包
	 * @param starttime
	 */
	public boolean deleteDetailData(String starttime){
		String sql = GpsInfoDetailMetaData.deleteSql(starttime);
		return GpsInfoDetailMetaData.deleteData(dbHelper, sql);
	}
	
	/**
	 * 删除简要包
	 * @param starttime
	 */
	public boolean deleteGPSListData(String starttime){
		String sql = GPSListMetaData.deleteSql(starttime);
		deleteDetailData(starttime);
		return GpsInfoDetailMetaData.deleteData(dbHelper, sql);
	}
	
	/**
	 * 删除GPS表
	 * @param starttime
	 * @return
	 */
	public void deleteGPSData(){
		GPSListMetaData.DeleteListGPSData(dbHelper);
		GpsInfoDetailMetaData.DeleteDetailGPSData(dbHelper);
	}
	
	/**
	 * 获取详细包
	 * @param starttime
	 * @return
	 */
	public List<GpsInfoDetail> getGpsInfoDetails(String starttime){
		return GpsInfoDetailMetaData.getGpsInfoDetails(dbHelper, starttime);
	}
	
	/**
	 * 插入简要包数据
	 * @param gpsListInfo
	 */
	public void insertGpsListInfo(GPSListInfo gpsListInfo){
		GPSListMetaData.AllPointInsert(dbHelper, gpsListInfo);
	}
	
	/**
	 * 获取所有简要包数据
	 * @param starttime 时间(yyyy-MM-dd HH:mm:ss)
	 */
	public  List<GPSListInfo> getAllData(){
		return GPSListMetaData.getListGPSData(dbHelper);
	}
	
	/**
	 * 依据开始时间查询是否存在该列
	 * @param starttime
	 * @return
	 */
	public String getGpsInfoByStarttime(String starttime){
		return GPSListMetaData.getDataByStartTime(dbHelper, starttime);
	}
	
	/**
	 * 更改是否上传字段
	 * @param isload 传入的标记  0 or 1
	 * @param starttime 开始时间
	 * @return
	 */
	public boolean updateIsUploadData(String isload,String starttime){
		return GPSListMetaData.updateIsLoadData(dbHelper, isload, starttime);
	}
	
}
