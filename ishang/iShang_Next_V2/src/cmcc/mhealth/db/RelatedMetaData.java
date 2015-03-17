package cmcc.mhealth.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import cmcc.mhealth.bean.CommonUserSearchInfos;
import cmcc.mhealth.bean.ContectData;
import cmcc.mhealth.common.PinYin4JCn;

public final class RelatedMetaData implements BaseColumns {
	public static final String TABLE_NAME = "related_infos";

	public static final String PHONE = "phone";
	public static final String NAME = "name";
	public static final String GROUPNAME = "groupname";
	public static final String EMAIL = "email";
	public static final String PINYIN = "pinyin";
	public static final String SEX = "sex";
	public static final String CLUBID = "clubid";

//	private static final String TAG = "RelatedMetaData";

	public static String CREATE_TABLE_SQL = "create table " + TABLE_NAME + "(" + _ID + " integer primary key autoincrement,"
			+ NAME + " text,"
			+ PHONE + " text,"
			+ GROUPNAME + " text,"
			+ EMAIL + " text,"
			+ PINYIN + " text,"
			+ CLUBID + " int,"
			+ SEX + " text" + ")";

	public static String DELETE_TABLE_SQL = "drop table  IF EXISTS " + TABLE_NAME;

	public static void CreateTable(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL(DELETE_TABLE_SQL);
		db.execSQL(CREATE_TABLE_SQL);
	}

	public static List<CommonUserSearchInfos> getAllSearchInfos(DatabaseHelper dbHelper,int clubid) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.query(TABLE_NAME, null, " clubid = ? ", new String[] { clubid + "" }, null, null, null);
			List<CommonUserSearchInfos> listm = new ArrayList<CommonUserSearchInfos>();
			while (cursor.moveToNext()) {
				CommonUserSearchInfos si = new CommonUserSearchInfos();
				String name = cursor.getString(cursor.getColumnIndex(NAME));
				si.setName(name);
				si.setPhone(cursor.getString(cursor.getColumnIndex(PHONE)));
				si.setPinyin(PinYin4JCn.converterToFirstSpell(name));
				si.setQuanpin(PinYin4JCn.converterToSpell(name));
				si.setSex(cursor.getString(cursor.getColumnIndex(SEX)));
				si.setEmail(cursor.getString(cursor.getColumnIndex(EMAIL)));
				si.setGroupname(cursor.getString(cursor.getColumnIndex(GROUPNAME)));
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
	
	public static void InsertContactValue(DatabaseHelper dbHelper, ArrayList<ContectData> list,int clubid) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(list == null || list.size()==0)return;
		db.beginTransaction();       //手动设置开始事务
		//数据插入操作循环
		ContentValues values = new ContentValues();
		for (ContectData cd : list) {
			values.put(NAME, cd.name);
			values.put(PHONE, cd.phone);
			values.put(PINYIN, cd.pinyin);
			values.put(SEX, cd.sex);
			values.put(EMAIL, cd.email);
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
