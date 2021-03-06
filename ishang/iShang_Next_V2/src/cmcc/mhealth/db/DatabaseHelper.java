package cmcc.mhealth.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	public static final int VERSION = 26;

	public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public DatabaseHelper(Context context, String name) {
		this(context, name, VERSION);
	}

	public DatabaseHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL(MobilePedometerTableMetaData.CREATE_TABLE_SQL);
		db.execSQL(PedometerTableMetaData.CREATE_TABLE_SQL);
		db.execSQL(PedoDetailTableMetaData.CREATE_TABLE_SQL);
		db.execSQL(GroupPkInfoTableMetaData.CREATE_TABLE_SQL);
		db.execSQL(GroupMemberInfoTableMetaData.CREATE_TABLE_SQL);
		db.execSQL(OrgnizeInfoTableMetaData.CREATE_TABLE_SQL);
		db.execSQL(GroupInPKTableMetaData.CREATE_TABLE_SQL);
		db.execSQL(ListActivityTableMetaData.CREATE_TABLE_SQL);
		
		db.execSQL(VitalSignMetaData.CREATE_TABLE_SQL);
		db.execSQL(MyRankMetaData.CREATE_TABLE_SQL);
		db.execSQL(RelatedMetaData.CREATE_TABLE_SQL);
		db.execSQL(RelatedGroupMetaData.CREATE_TABLE_SQL);
		
		db.execSQL(ActivityListDetailTableMetaData.CREATE_TABLE_SQL);
		db.execSQL(ActivityMyDetailTableMetaData.CREATE_TABLE_SQL);
		db.execSQL(FriendMetaData.CREATE_TABLE_SQL);
		
		db.execSQL(RaceMetaData.CREATE_TABLE_SQL);
		db.execSQL(HistroyMessageMetaData.CREATE_TABLE_SQL);
		
		db.execSQL(GPSListMetaData.CREATE_TABLE);
		db.execSQL(GpsInfoDetailMetaData.CREATE_TABLE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		//数据库版本升级调用
		if(oldVersion < 20){
			db.execSQL("DROP TABLE IF EXISTS " + PedoDetailTableMetaData.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + ActivityListDetailTableMetaData.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + OrgnizeInfoTableMetaData.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + GroupMemberInfoTableMetaData.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + GroupPkInfoTableMetaData.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + GroupInPKTableMetaData.TABLE_NAME);

			db.execSQL(PedoDetailTableMetaData.CREATE_TABLE_SQL);
			db.execSQL(ActivityListDetailTableMetaData.CREATE_TABLE_SQL);
			db.execSQL(OrgnizeInfoTableMetaData.CREATE_TABLE_SQL);
			db.execSQL(GroupMemberInfoTableMetaData.CREATE_TABLE_SQL);
			db.execSQL(GroupPkInfoTableMetaData.CREATE_TABLE_SQL);
			db.execSQL(GroupInPKTableMetaData.CREATE_TABLE_SQL);
			
			db.execSQL("DROP TABLE IF EXISTS " + FriendMetaData.TABLE_NAME);
			db.execSQL(FriendMetaData.CREATE_TABLE_SQL);
			
			db.execSQL("DROP TABLE IF EXISTS " + VitalSignMetaData.TABLE_NAME);
			db.execSQL(VitalSignMetaData.CREATE_TABLE_SQL);
			
			db.execSQL("DROP TABLE IF EXISTS " + ActivityListDetailTableMetaData.TABLE_NAME);
			db.execSQL(ActivityListDetailTableMetaData.CREATE_TABLE_SQL);

			db.execSQL("DROP TABLE IF EXISTS " + ActivityMyDetailTableMetaData.TABLE_NAME);
			db.execSQL(ActivityMyDetailTableMetaData.CREATE_TABLE_SQL);
			db.execSQL("DROP TABLE IF EXISTS " + ListActivityTableMetaData.TABLE_NAME);
			db.execSQL(ListActivityTableMetaData.CREATE_TABLE_SQL);
			
			db.execSQL("DROP TABLE IF EXISTS " + RelatedMetaData.TABLE_NAME);
			db.execSQL(RelatedMetaData.CREATE_TABLE_SQL);
			db.execSQL("DROP TABLE IF EXISTS " + RelatedGroupMetaData.TABLE_NAME);
			db.execSQL(RelatedGroupMetaData.CREATE_TABLE_SQL);
			
			db.execSQL("DROP TABLE IF EXISTS " + MyRankMetaData.TABLE_NAME);
			db.execSQL(MyRankMetaData.CREATE_TABLE_SQL);
			
			db.execSQL("DROP TABLE IF EXISTS " + GroupInPKTableMetaData.TABLE_NAME);
			db.execSQL(GroupInPKTableMetaData.CREATE_TABLE_SQL);
		}
		if(oldVersion < 22){
			db.execSQL("DROP TABLE IF EXISTS " + RaceMetaData.TABLE_NAME);
			db.execSQL(RaceMetaData.CREATE_TABLE_SQL);
		}
		if(oldVersion < 23){
			db.execSQL("DROP TABLE IF EXISTS " + HistroyMessageMetaData.TABLE_NAME);
			db.execSQL(HistroyMessageMetaData.CREATE_TABLE_SQL);
		}
		if(oldVersion<24){
			db.execSQL("DROP TABLE IF EXISTS " + GPSListMetaData.TABLE_NAME);
			db.execSQL(GPSListMetaData.CREATE_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + MobilePedometerTableMetaData.TABLE_NAME);
			db.execSQL(MobilePedometerTableMetaData.CREATE_TABLE_SQL);
		}
		if(oldVersion<26){
			db.execSQL("DROP TABLE IF EXISTS " + GpsInfoDetailMetaData.TABLE_NAME);
			db.execSQL(GpsInfoDetailMetaData.CREATE_TABLE);
		}

	}

}
