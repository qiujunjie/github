package cmcc.mhealth.db;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import cmcc.mhealth.bean.RaceData;
import cmcc.mhealth.bean.RaceInfo;

public final class RaceMetaData implements BaseColumns {
	public static final String TABLE_NAME = "the_race";

	public static final String RACENAME = "racename";
	public static final String FOUNDERPHONE = "founderphone";
	public static final String FOUNDERNAME = "foundername";
	public static final String STARTTIME = "starttime";
	public static final String ENDTIME = "endtime";
	public static final String TYPE = "type";
	public static final String STARTED = "started";
	public static final String RACEID = "raceid";
	public static final String RACEDETAIL = "racedetail";
	public static final String MEMBERNUM = "membernum";
	public static final String TITLEPICURL = "titlepicurl";
	public static final String STATE = "state";

	public static String CREATE_TABLE_SQL = "create table " + TABLE_NAME + "(" + _ID + " integer primary key autoincrement," + RACENAME + " text," + FOUNDERPHONE + " text," + FOUNDERNAME + " text," + STARTTIME + " integer," + ENDTIME + " integer," + TYPE + " text," + RACEID + " integer,"
			+ MEMBERNUM + " integer,"
			+ STARTED + " integer,"
			+ STATE + " integer,"
			+ RACEDETAIL + " text,"
			+ TITLEPICURL + " text" + ")";

	public static String DELETE_TABLE_SQL = "drop table  IF EXISTS " + TABLE_NAME;

	public static void CreateTable(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL(DELETE_TABLE_SQL);
		db.execSQL(CREATE_TABLE_SQL);
	}

	public static void RaceInsertValue(DatabaseHelper dbHelper, List<RaceData> raceInfos, int state) {
		ContentValues values = new ContentValues();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		for (RaceData raceInfo : raceInfos) {
			values.put(RACENAME, raceInfo.racename);
			values.put(FOUNDERPHONE, raceInfo.founderphone);
			values.put(FOUNDERNAME, raceInfo.foundername);
			values.put(STARTTIME, raceInfo.starttime);
			values.put(ENDTIME, raceInfo.endtime);
			values.put(TYPE, raceInfo.type);
			values.put(STARTED, raceInfo.started);
			values.put(RACEID, raceInfo.raceid);
			values.put(RACEDETAIL, raceInfo.racedetail);
			values.put(MEMBERNUM, raceInfo.membernum);
			values.put(TITLEPICURL, raceInfo.titlepicurl);
			values.put(STATE, state);
			db.insert(TABLE_NAME, null, values);
			values.clear();
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	/**
	 * 获取指定类型的所有数据
	 * 
	 * @param dbHelper
	 * @param state
	 * @return List
	 */
	public static RaceInfo getRaces(DatabaseHelper dbHelper, int num, int startid, int state) {
		Cursor cursor = null;
		RaceInfo ri = null;

		try {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			if (startid > 0) {
				cursor = db.query(TABLE_NAME, null, STATE + " = ? and " + RACEID + " < ?", new String[] { state + "", startid + "" }, null, null, RACEID + " desc");
			} else {
				cursor = db.query(TABLE_NAME, null, STATE + " = ? ", new String[] { state + "" }, null, null, RACEID + " desc");
			}
			ri = new RaceInfo();
			while (num-- > 0 && cursor.moveToNext()) {
				RaceData rd = new RaceData();
				String racename = cursor.getString(cursor.getColumnIndex(RaceMetaData.RACENAME));
				String foundername = cursor.getString(cursor.getColumnIndex(RaceMetaData.FOUNDERNAME));
				String founderphone = cursor.getString(cursor.getColumnIndex(RaceMetaData.FOUNDERPHONE));
				String starttime = cursor.getString(cursor.getColumnIndex(RaceMetaData.STARTTIME));
				String endtime = cursor.getString(cursor.getColumnIndex(RaceMetaData.ENDTIME));
				String type = cursor.getString(cursor.getColumnIndex(RaceMetaData.TYPE));
				String started = cursor.getString(cursor.getColumnIndex(RaceMetaData.STARTED));
				String raceid = cursor.getString(cursor.getColumnIndex(RaceMetaData.RACEID));
				String racedetail = cursor.getString(cursor.getColumnIndex(RaceMetaData.RACEDETAIL));
				String membernum = cursor.getString(cursor.getColumnIndex(RaceMetaData.MEMBERNUM));
				String titlepicurl = cursor.getString(cursor.getColumnIndex(RaceMetaData.TITLEPICURL));
				rd.racename = racename;
				rd.foundername = foundername;
				rd.founderphone = founderphone;
				rd.starttime = starttime;
				rd.endtime = endtime;
				rd.started = started;
				rd.type = type;
				rd.raceid = raceid;
				rd.racedetail = racedetail;
				rd.membernum = membernum;
				rd.titlepicurl = titlepicurl;
				ri.racelistinfo.add(rd);
				ri.lastid = raceid;
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return ri;
	}

	public static int modifySingleRace(DatabaseHelper dbHelper, int membernum, String raceid) {
		int lines = 0;
		try {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			ContentValues contentvalue = new ContentValues();
			contentvalue.put(MEMBERNUM, "" + membernum);
			lines = db.update(TABLE_NAME, contentvalue, RACEID + " = ? ", new String[] { raceid });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lines;
	}

	public static void DeleteRaceData(DatabaseHelper dbHelper, int state) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME + " where state = " + state);
	}
}
