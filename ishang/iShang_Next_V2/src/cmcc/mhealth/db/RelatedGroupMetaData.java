package cmcc.mhealth.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import cmcc.mhealth.bean.ContectGroupData;
import cmcc.mhealth.common.PinYin4JCn;

public final class RelatedGroupMetaData implements BaseColumns {
	public static final String TABLE_NAME = "relatedgroup_infos";

	public static final String GROUPNAME = "groupname";
	public static final String QUANPIN = "quanpin";
	public static final String PINYIN = "pinyin";
	public static final String GROUPID = "groupid";
	public static final String CLUBID = "clubid";

	public static String CREATE_TABLE_SQL = "create table " + TABLE_NAME + "(" + _ID + " integer primary key autoincrement,"
			+ GROUPNAME + " text,"
			+ PINYIN + " text,"
			+ GROUPID + " text,"
			+ CLUBID + " int,"
			+ QUANPIN + " text" + ")";

	public static String DELETE_TABLE_SQL = "drop table  IF EXISTS " + TABLE_NAME;

	public static void CreateTable(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL(DELETE_TABLE_SQL);
		db.execSQL(CREATE_TABLE_SQL);
	}

	public static List<ContectGroupData> getAllSearchGroupInfos(DatabaseHelper dbHelper ,int clubid) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.query(TABLE_NAME, null, " clubid=? ", new String[] { clubid + "" }, null, null, null);
			List<ContectGroupData> listm = new ArrayList<ContectGroupData>();
			while (cursor.moveToNext()) {
				ContectGroupData si = new ContectGroupData();
				String gname = cursor.getString(cursor.getColumnIndex(GROUPNAME));
				si.setQuanpin(PinYin4JCn.converterToSpell(gname));
				si.setPinyin(PinYin4JCn.converterToFirstSpell(gname));
				si.setGroupname(cursor.getString(cursor.getColumnIndex(GROUPNAME)));
				si.setGroupid(cursor.getString(cursor.getColumnIndex(GROUPID)));
				listm.add(si);
			}
			return listm;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}
	
	public static void InsertContactValue(DatabaseHelper dbHelper, ArrayList<ContectGroupData> list,int clubid) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(list == null || list.size()==0)return;
		db.beginTransaction();       //手动设置开始事务
		//数据插入操作循环
		ContentValues values = new ContentValues();
		for (ContectGroupData cd : list) {
			values.put(QUANPIN, cd.quanpin);
			values.put(PINYIN, cd.pinyin);
			values.put(GROUPID, cd.groupid);
			values.put(GROUPNAME, cd.groupname);
			values.put(CLUBID, clubid);
			db.insert(TABLE_NAME, null, values);
			values.clear();
		}
		db.setTransactionSuccessful();       //设置事务处理成功，不设置会自动回滚不提交
		db.endTransaction();       //处理完成
	}
	
	public static void DeleteAllData(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME);
	}

	
}
