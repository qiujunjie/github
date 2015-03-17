/********************************************
 * File Name  MHealthProviderMetaData.java
 * Version    1.0
 * @Author    DaiPengfei - ������
 * @Date      2012-7-23
 * Describe   ��������
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
		// �����һ��ȡ���������
		try {
			if (cursor.moveToNext()) {
				pedoInfo = PedometerTableMetaData.GetPedometerFromCursor(cursor);
			}
		} finally {
			cursor.close();
		}
		return pedoInfo;
	}
	//�ֻ��Ʋ�
	public DataPedometor getMobilePedometerLatest() {
		Cursor cursor = MobilePedometerTableMetaData.GetAllValueCursor(dbHelper);
		DataPedometor pedoInfo = null;
		if (null == cursor)
			return null;
		// �����һ��ȡ���������
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
	 *            ��ʽΪ20121212
	 * @return
	 */
	public DataPedometor getPedometerByDate(String searchDate) {
		searchDate = Common.getYYYYMMDDToYYYY_MM_DD(searchDate);
		Cursor cursor = PedometerTableMetaData.GetAllValueCursor(dbHelper, searchDate);
		DataPedometor pedoInfo = null;
		if (null == cursor)
			return null;
		// �����һ��ȡ���������
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
				String createDateStr = createtimeStr.substring(0, 10); // �Ƚ�����
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
							if (dayNum >= diff) // �ж����һ���Ƿ����
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

	// �������м�����
	public void updatePedometerData(long id, DataPedometor pedoInfo) {
		PedometerTableMetaData.UpdateValue(dbHelper, id, pedoInfo.createtime, pedoInfo.data.power + "", pedoInfo.data.weight + "", pedoInfo.data.step + "", pedoInfo.data.cal + "", pedoInfo.data.stepNum + "", pedoInfo.data.distance + "", pedoInfo.data.strength1 + "", pedoInfo.data.strength2 + "",
				pedoInfo.data.strength3 + "", pedoInfo.data.strength4 + "", pedoInfo.data.transType + "", pedoInfo.data.yxbssum + "");
	}

	// ���¼���־����
	public void updatePedometerData(long id, int mood_level, String mood_text, int sports_type) {
		PedometerTableMetaData.UpdateData(dbHelper, id, mood_level, mood_text, sports_type);
	}

	// *********************PedoDetailTableMetaData******************************
	/**
	 * ������ϸ������
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
			// ��ֹ�ظ���������
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
				// ʱ������ ���� ����
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
	 * ��ȡ�������ݿ�����
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
	 * ���� ��ID��ȡ����
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
	 * �������ݿ�
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
	 * �h�����ݿ� qjj 2013-1-31
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
	 * insertActivityInfo(�����б�)
	 * 
	 * @param List
	 *            <ActivityDetail> activityList
	 * @return void
	 * @Exception �쳣����
	 * @�����ˣ�Qiujunjie - �񿡽�
	 * @����ʱ�䣺2013-9-23 ����12:02:35
	 * @�޸��ˣ�Qiujunjie - �񿡽�
	 * @�޸�ʱ�䣺2013-9-23 ����12:02:35
	 */
	public void insertListActivity(List<ListActivity> activityList , int clubid) {
		for (int i = 0; i < activityList.size(); i++) {
			ListActivity listActivity = activityList.get(i);
			ListActivityTableMetaData.InsertValue(dbHelper, listActivity.activityid, listActivity.activityname, listActivity.activityslogan, listActivity.activitystart, listActivity.activitytype, listActivity.activityend, listActivity.company_name, listActivity.aimstep, listActivity.personnum,
					listActivity.personseq, listActivity.groupnum, listActivity.groupseq , clubid);
		}
	}

	/**
	 * deleteActivityInPkInfo(ɾ����б���Ϣ)
	 * 
	 * @return void
	 * @�����ˣ�Qiujunjie - �񿡽�
	 * @����ʱ�䣺2013-9-23 ����12:03:18
	 * @�޸��ˣ�Qiujunjie - �񿡽�
	 * @�޸�ʱ�䣺2013-9-23 ����12:03:18
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
	 * createActivityMedailInfoTable(�����ҵĻ��Ϣ�;�����Ϣ)
	 * 
	 * @return void
	 * @Exception �쳣����
	 * @�����ˣ�Qiujunjie - �񿡽�
	 * @����ʱ�䣺2013-9-23 ����11:59:52
	 * @�޸��ˣ�Qiujunjie - �񿡽�
	 * @�޸�ʱ�䣺2013-9-23 ����11:59:52
	 */
	public void createActivityDetailTable() {
		ActivityListDetailTableMetaData.CreateTable(dbHelper);
		ActivityMyDetailTableMetaData.CreateTable(dbHelper);
		// ActivityMedailInfoTableMetaData.CreateTable(dbHelper);
	}

	/**
	 * 
	 * getActivityMyMediaInfo(������б�)
	 * 
	 * @param activityid
	 *            �ID
	 * @return ActivityMedalInfo
	 * @Exception �쳣����
	 * @�����ˣ�Qiujunjie - �񿡽�
	 * @����ʱ�䣺2013-9-22 ����2:56:48
	 * @�޸��ˣ�Qiujunjie - �񿡽�
	 * @�޸�ʱ�䣺2013-9-22 ����2:56:48
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
	 * getActivityMediaInfo(����б�)
	 * 
	 * @param
	 * @return ArrayList<MedalInfo>
	 * @Exception �쳣����
	 * @�����ˣ�Qiujunjie - �񿡽�
	 * @����ʱ�䣺2013-9-22 ����3:12:30
	 * @�޸��ˣ�Qiujunjie - �񿡽�
	 * @�޸�ʱ�䣺2013-9-22 ����3:12:30
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
	 * insertActivityMediaInfo(��ӻ���飬�����ҵĻ�ź;�����Ϣ)
	 * 
	 * @param ActivityMedalInfo
	 *            activityMedalInfos
	 * @param activityid
	 *            �ID
	 * @return void
	 * @�����ˣ�Qiujunjie - �񿡽�
	 * @����ʱ�䣺2013-9-23 ����11:57:56
	 * @�޸��ˣ�Qiujunjie - �񿡽�
	 * @�޸�ʱ�䣺2013-9-23 ����11:57:56
	 */
	public void insertActivityDetail(ActivityMedalInfo activityMedalInfos, String activityid,int clubid) {
		ActivityMedalInfo medalInfoData = activityMedalInfos;
		List<MedalInfo> arr = medalInfoData.datavalue.medalinfo;
		for (MedalInfo medalInfo : arr) {
			if (medalInfo != null) {
				medalInfo.activityid = medalInfoData.activityid;
			}
		}
		// �ҵĻ����
		ActivityMyDetailTableMetaData.InsertValue(dbHelper, medalInfoData.datavalue.myname, medalInfoData.datavalue.mygroup, medalInfoData.datavalue.avgstep, medalInfoData.datavalue.ratescore, activityid, medalInfoData.datavalue.hitduration, medalInfoData.datavalue.groupavgstep,
				medalInfoData.datavalue.groupratescore,clubid);
		// ���������
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
	
	// ��������===========
	// �������ݿ��к�����Ŀ
	public int getFriendCount() {
		return FriendMetaData.getFriendCount(dbHelper);
	}
	// �洢��������-------����
	public void FriendInsertValue(List<OrgnizeMemberInfo> friendinfos) {
		FriendMetaData.MyFriendInsertValue(dbHelper, friendinfos, "1");
	}
	// ��ȡ��������
	public List<OrgnizeMemberInfo> getMyFriends() {
		return FriendMetaData.getMyFriends(dbHelper);
	}

	// ɾ����������-------
	public void deleteMyFriend() {
		FriendMetaData.DeleteMyFriendData(dbHelper);
	}

	public void deleteMyFriend(String friendphone) {
		FriendMetaData.DeleteMyFriendData(dbHelper, friendphone);
	}

	// ===========

	// �洢��������-------
	public void MyRankInsertValue(String memberseq, String membername, String groupname, String member7avgstep, String type, String imageurl, int clubid) {
		MyRankMetaData.MyRankInsertValue(dbHelper, memberseq, membername, groupname, member7avgstep, type, imageurl, clubid);
	}

	// �洢��������-------����
	public void MyRankInsertValue(List<OrgnizeMemberInfo> orgnizemember, String type, int clubid) {
		MyRankMetaData.MyRankInsertValue(dbHelper, orgnizemember, type, clubid);
	}

	public void MyRankInsertValueGP(List<GroupMemberInfo> groupmember, String type, int clubid) {
		MyRankMetaData.MyRankInsertValueGP(dbHelper, groupmember, type, clubid);
	}

	// �洢��������-------����
	public void MyRankInsertValueGroup(List<GroupInfo> grouppkdata, String type, int clubid) {
		MyRankMetaData.MyRankInsertValueGroup(dbHelper, grouppkdata, type, clubid);
	}

	// ɾ����������-------
	public void MyRankDeleteData(int clubid) {
		MyRankMetaData.deleteMyRankData(dbHelper, clubid);
	}
	// ɾ����������-------
	public void MyRankDeleteData() {
		MyRankMetaData.deleteMyRankData(dbHelper);
	}

	// ��ȡ��������-------
	public List<RankUserBean> getMyRankByType(int type,int clubid) {
		return MyRankMetaData.getMyRankByType(dbHelper, type,clubid);
	}

	// ��ѯ��ϵ������==============================================

	public List<CommonUserSearchInfos> getAllSearchInfos(int clubid) {
		return RelatedMetaData.getAllSearchInfos(dbHelper, clubid);
	}

	public void insertContacts(ArrayList<ContectData> list,int clubid) {
		RelatedMetaData.InsertContactValue(dbHelper, list, clubid);
	}

	public void removeContactAllDatas() {
		RelatedMetaData.DeleteAllData(dbHelper);
	}

	// ��ѯ������===========================================================
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
	 * ��ȡָ������֮����������ݼ���
	 * 
	 * @param timemill
	 *            ʱ��� longֵ
	 * @return
	 */
	public List<VitalSignInfoDataBean> getWeightInRange(long timemill ,String timetype) {
		return VitalSignMetaData.getVitalSignInRange(dbHelper, timemill, VitalSignMetaData.TYPE_WEIGHT,timetype);
	}

	/**
	 * ��ȡָ������֮����������ݼ���
	 * 
	 * @param timemill
	 *            ʱ��� Stringֵ ��ʽΪyyyy-MM-dd_HH:mm:ss
	 * @return
	 */
	public List<VitalSignInfoDataBean> getWeightInRange(String time ,String timetype) {
		return VitalSignMetaData.getVitalSignInRange(dbHelper, Common.getDateFromStrFromServel(time), VitalSignMetaData.TYPE_WEIGHT,timetype);
	}

	// д����������===============
	public void addWeightValue(List<VitalSignInfoDataBean> listVsb) {
		VitalSignMetaData.InsertValue(dbHelper, listVsb, VitalSignMetaData.TYPE_WEIGHT);
	}

	// д����������===============
	public void addWeightValue(VitalSignInfoDataBean vitalSignBean) {
		VitalSignMetaData.InsertValue(dbHelper, vitalSignBean, VitalSignMetaData.TYPE_WEIGHT);
	}
	// ɾ����������===============
	public void deleteVitalSignValue() {
		VitalSignMetaData.deleteVitalSignData(dbHelper);
	}
	// д�뾺������
	public void insertRaceValue(RaceInfo raceInfos, int state) {
		RaceMetaData.RaceInsertValue(dbHelper, raceInfos.racelistinfo, state);
	}
	// ��ȡ�뾺������
	public RaceInfo getRaces(int num, int startid, int state) {
		return RaceMetaData.getRaces(dbHelper, num, startid, state);
	}
	// �޸ľ�����Ա��Ŀ����
	public int modifySingleRaceMemberNum(int membernum, String raceid) {
		return RaceMetaData.modifySingleRace(dbHelper, membernum, raceid);
	}
	// ��ȡ�뾺������
	public void DeleteRaceData(int state) {
		RaceMetaData.DeleteRaceData(dbHelper,state);
	}

	// ================
	// ����Ϣ
	// ================
	// д�����Ϣ
	public void OldMsgsInsertValue(RequestData rd) {
		HistroyMessageMetaData.OldMsgsInsertValue(dbHelper, rd);
	}

	// ��ȡ����Ϣ
	public List<RequestData> getMyOldMsgs() {
		return HistroyMessageMetaData.getMyOldMsgs(dbHelper);
	}
	// ��ȡ����Ϣ
	public void DeleteMyOldMsgs() {
		HistroyMessageMetaData.DeleteMyOldMsgs(dbHelper);
	}
	
	/**
	 * ����������ϸ��
	 * @param gpsInfoDetail
	 */
	public void insertAllPoints(DetailGPSData gpsInfoDetail,String starttime){
		GpsInfoDetailMetaData.insertData(dbHelper, gpsInfoDetail,starttime);
	}
	/**
	 * ����һ����ϸ����
	 * @param gpsInfoDetail
	 */
	public void insertDetail(GpsInfoDetail gpsInfoDetail){
		GpsInfoDetailMetaData.insertDetail(dbHelper, gpsInfoDetail);
	}
	
	/**
	 * ɾ����ϸ��
	 * @param starttime
	 */
	public boolean deleteDetailData(String starttime){
		String sql = GpsInfoDetailMetaData.deleteSql(starttime);
		return GpsInfoDetailMetaData.deleteData(dbHelper, sql);
	}
	
	/**
	 * ɾ����Ҫ��
	 * @param starttime
	 */
	public boolean deleteGPSListData(String starttime){
		String sql = GPSListMetaData.deleteSql(starttime);
		deleteDetailData(starttime);
		return GpsInfoDetailMetaData.deleteData(dbHelper, sql);
	}
	
	/**
	 * ɾ��GPS��
	 * @param starttime
	 * @return
	 */
	public void deleteGPSData(){
		GPSListMetaData.DeleteListGPSData(dbHelper);
		GpsInfoDetailMetaData.DeleteDetailGPSData(dbHelper);
	}
	
	/**
	 * ��ȡ��ϸ��
	 * @param starttime
	 * @return
	 */
	public List<GpsInfoDetail> getGpsInfoDetails(String starttime){
		return GpsInfoDetailMetaData.getGpsInfoDetails(dbHelper, starttime);
	}
	
	/**
	 * �����Ҫ������
	 * @param gpsListInfo
	 */
	public void insertGpsListInfo(GPSListInfo gpsListInfo){
		GPSListMetaData.AllPointInsert(dbHelper, gpsListInfo);
	}
	
	/**
	 * ��ȡ���м�Ҫ������
	 * @param starttime ʱ��(yyyy-MM-dd HH:mm:ss)
	 */
	public  List<GPSListInfo> getAllData(){
		return GPSListMetaData.getListGPSData(dbHelper);
	}
	
	/**
	 * ���ݿ�ʼʱ���ѯ�Ƿ���ڸ���
	 * @param starttime
	 * @return
	 */
	public String getGpsInfoByStarttime(String starttime){
		return GPSListMetaData.getDataByStartTime(dbHelper, starttime);
	}
	
	/**
	 * �����Ƿ��ϴ��ֶ�
	 * @param isload ����ı��  0 or 1
	 * @param starttime ��ʼʱ��
	 * @return
	 */
	public boolean updateIsUploadData(String isload,String starttime){
		return GPSListMetaData.updateIsLoadData(dbHelper, isload, starttime);
	}
	
}
