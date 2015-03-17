package com.cmcc.ishang.lib.step.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;





import com.cmcc.ishang.lib.step.DataStructPerHour;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDataBaseAdapter
{
	//数据库中的字段
	public static final String KEY_ID="_id";
	public static final String KEY_DATE="date";
	public static final String KEY_HOUR="hour";
	public static final String KEY_STEP="step";
	public static final String KEY_CALORY="calory";
//	public static final String KEY_DISTANCE="distance";
	public static final String KEY_LEVEL2P5="level2p5";
	public static final String KEY_LEVEL3P5="level3p5";
	public static final String KEY_LEVEL4P5="level4p5";
	public static final String KEY_YXBSSUM="yxbssum";
	public static final String KEY_SENDFLAG="sendflag";
	public static final String KEY_SUM_STEP="sum_step";
	public static final String KEY_SUM_CALORY="sum_calory";
	public static final String KEY_SUM_DISTANCE="sum_distance";
	public static final String KEY_SUM_LV2="sum_lv2";
	public static final String KEY_SUM_LV3="sum_lv3";
	public static final String KEY_SUM_LV4="sum_lv4";
	//数据库属性
	private static final String DB_NAME="pedometer_db";
	private static final String DB_TABLE="data_per_hour";
	private static final int DB_VERSION=1;
	//本地context对象
	private Context mContext=null;
	//创建一个表
	private static final String DB_CREATE="CREATE TABLE IF NOT EXISTS "+DB_TABLE+" ("+KEY_ID+" integer primary key autoincrement,"
			+KEY_SENDFLAG+" integer,"+KEY_DATE+" date,"+KEY_HOUR+" integer,"+KEY_SUM_STEP+" integer,"+KEY_SUM_CALORY+" integer,"+KEY_SUM_DISTANCE+" integer,"+KEY_SUM_LV2+" integer,"+KEY_SUM_LV3+" integer,"+KEY_SUM_LV4+" integer,"
			+KEY_STEP+" text,"+KEY_CALORY+" text,"+KEY_LEVEL2P5+" text,"
			+KEY_LEVEL3P5+" text,"+KEY_LEVEL4P5+" text,"+KEY_YXBSSUM+" text)";
	private SQLiteDatabase mSQLiteDatabase=null;
	private DatabaseHelper mDatabaseHelper=null;
	
	/*
	 * 内部类，包含了一个SQLiteOpenHelper用于控制database
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		/*
		 * 构造函数,创建数据库
		 */
		public DatabaseHelper(Context context)
		{
			super(context, DB_NAME, null, DB_VERSION);
			
		}

		/*
		 * 创建表格,数据库没有表格时创建一个新的表格
		 *在新建数据库时调用。
		 */
		@Override
		public void onCreate(SQLiteDatabase db) 
		{
			// TODO Auto-generated method stub
			db.execSQL(DB_CREATE);
		}


		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
		{
			db.execSQL("DROP TABLE IF EXISTS notes");
			onCreate(db);
			
		}		
	}
	/*
	 * 构造函数
	 *获得context 
	 */
	public MyDataBaseAdapter(Context context)
	{
		mContext=context;
	}
	
	/*
	 * 打开数据库，返回数据库对象
	 */
	public void open()throws SQLException
	{
		mDatabaseHelper=new DatabaseHelper(mContext);
		mSQLiteDatabase=mDatabaseHelper.getWritableDatabase();
	}
	/*
	 * 关闭数据库
	 */
	public void close()
	{
		mDatabaseHelper.close();
	}
	/*
	 * 插入数据
	 */
	public long insertData(DataStructPerHour data)
	{
		
		ContentValues value=new ContentValues();
		value.put(KEY_DATE, ""+data.year+"-"+data.month+"-"+data.day);
		value.put(KEY_HOUR,data.hour);
		value.put(KEY_SENDFLAG, 0);
		int sum=0;
		int sum_calory=0;
		int sum_lv2=0;
		int sum_lv3=0;
		int sum_lv4=0;
		for(int i=0;i<12;i++)
		{
			sum+=data.step_per5min[i];
			sum_calory+=data.calory_per5min[i];
			sum_lv2+=data.lightly[i];
			sum_lv3+=data.fairly[i];
			sum_lv4+=data.very[i];
			
		}
		value.put(KEY_SUM_STEP, sum);
		value.put(KEY_STEP, intArrayToString(data.step_per5min));
		value.put(KEY_CALORY, intArrayToString(data.calory_per5min));
		value.put(KEY_LEVEL2P5, intArrayToString(data.lightly));
		value.put(KEY_LEVEL3P5, intArrayToString(data.fairly));
		value.put(KEY_LEVEL4P5, intArrayToString(data.very));
		value.put(KEY_YXBSSUM, intArrayToString(data.yxbssum));
		value.put(KEY_SUM_CALORY, sum_calory);
		value.put(KEY_SUM_DISTANCE, data.distance_perHour);
		value.put(KEY_SUM_LV2, sum_lv2);
		value.put(KEY_SUM_LV3, sum_lv3);
		value.put(KEY_SUM_LV4, sum_lv4);
		Log.v("test","insert date="+data.year+"-"+data.month+"-"+data.day+data.hour+" step="+intArrayToString(data.step_per5min));
		return mSQLiteDatabase.insert(DB_TABLE, null, value);//返回ID
	}
	
	/*
	 *根据日期和小时删除数据 
	 */
	public boolean deleteData(int year,int month,int day,int hour)
	{
		String clause=KEY_DATE+"=\""+year+"-"+month+"-"+day+"\" and "+KEY_HOUR+"="+hour;
		return mSQLiteDatabase.delete(DB_TABLE, clause,null)>0;
	}
	
	/*
	 * 根据日期和小时查询数据
	 * 如果为空 则返回null
	 */
	public Cursor fetchData(int year,int month,int day,int hour)
	{
//		String clause=KEY_HOUR+"="+hour;
//		String clause=KEY_DATE+"="+year+"-"+month+"-"+day;
		String clause=KEY_DATE+"=\""+year+"-"+month+"-"+day+"\" and "+KEY_HOUR+"="+hour;
		Log.v("test","clause="+clause);
		
		Cursor cursor=mSQLiteDatabase.query(DB_TABLE, null, clause, null, null, null, null, null);
		if(cursor!=null)
		{
			boolean re=cursor.moveToFirst();
			if(!re)
			{
				return null;
			}
			
		}
		return cursor;
	}
	
	/*
	 * 根据日期和小时更新一条数据
	 */
	public boolean updateData(DataStructPerHour data)
	{
		String clause=KEY_DATE+"="+data.year+"-"+data.month+"-"+data.day+" and "+KEY_HOUR+"="+data.hour;
		ContentValues value=new ContentValues();
		value.put(KEY_DATE, ""+data.year+"-"+data.month+"-"+data.day);
		value.put(KEY_HOUR,data.hour);
		value.put(KEY_SENDFLAG, false);
		value.put(KEY_STEP, intArrayToString(data.step_per5min));
		value.put(KEY_CALORY, intArrayToString(data.calory_per5min));
		value.put(KEY_LEVEL2P5, intArrayToString(data.lightly));
		value.put(KEY_LEVEL3P5, intArrayToString(data.fairly));
		value.put(KEY_LEVEL4P5, intArrayToString(data.very));
		value.put(KEY_YXBSSUM, intArrayToString(data.yxbssum));
		return mSQLiteDatabase.update(DB_TABLE, value, clause, null)>0;
	}
	
	/*
	 * 查询每天总的数据
	 * 如果没有数据，就返回Null
	 */
	public Cursor fetchDailyData()
	{
		Cursor cursor=mSQLiteDatabase.query(DB_TABLE, new String[]{KEY_DATE,"sum("+KEY_SUM_STEP+")"}, null, null, KEY_DATE, null, KEY_DATE);
		if(cursor!=null)
		{
			boolean re=cursor.moveToFirst();
			if(!re)
			{
				return null;
			}
		}
		return cursor;
	}
	
	/*
	 * 将int[]数组转成string存在数据库中，每个数用“,”隔开.
	 */
	public String intArrayToString(int[] src)
	{
		String str="";
		for(int i=0;i<src.length-1;i++)
		{
			str+=src[i];
			str+=",";
		}
		str+=src[src.length-1];
		return str;
		
	}
	/*
	 * 将string转成int[]
	 */
	public int[] StringtoIntArray(String src)
	{
		String[] tmp=src.split(",");
		int[]res=new int[12];
		for(int i=0;i<tmp.length;i++)
		{
			res[i]=Integer.parseInt(tmp[i]);
		}
		return res;
	}
	
	/*
	 * 数据更新 发送详细包
	 * 将所有send为false(0)的数据发送，并将send置位为true(1)
	 */
	public String sendDataToServer(String username,String password,String apptype,String deviceid,String weight,String stride)
	{

		//TODO:这些参数需要传入进来
		final String company="cmcc";
		final String uploadType="10";
		
		
		//String url="http://218.206.179.60:8080/DataGW/service/uploadSportsDatas.json";
		String url="http://218.206.179.94:8080/WebGateWayMonitor/service/uploadSportsDatas.json";
		boolean flag = false;
		String test="";
		String hour="";
		String datavalue="";
		String strSendDate="";
		String strstepSum,strcalSum,strdistanceSum,strlv2Sum,strlv3Sum,strlv4Sum;
		
		List<String>datelist=new ArrayList<String>();
		int id;
		String strDate,strHour,strSnp5,strKnp5,strLevel2p5,strLevel3p5,strLevel4p5,strYxbssum;
		String clause=KEY_SENDFLAG+"=0";
		String strOrderby=MyDataBaseAdapter.KEY_DATE;
		Cursor cursor=mSQLiteDatabase.query(DB_TABLE, null, clause, null, null, null, strOrderby, null);
		if(cursor==null)
		{	
			return "获取数据失败！";
		}
		int totalNum=cursor.getCount();
		Log.v(test,"totalnum="+totalNum);
		cursor.moveToFirst();
		strSendDate=cursor.getString(cursor.getColumnIndex(MyDataBaseAdapter.KEY_DATE));
		datelist.add(strSendDate);
		for(;!cursor.isAfterLast();cursor.moveToNext())
		{
			//取数据
			id=cursor.getInt(cursor.getColumnIndex(MyDataBaseAdapter.KEY_ID));
			strDate=cursor.getString(cursor.getColumnIndex(MyDataBaseAdapter.KEY_DATE));
			strHour=cursor.getString(cursor.getColumnIndex(MyDataBaseAdapter.KEY_HOUR));
			strSnp5=cursor.getString(cursor.getColumnIndex(MyDataBaseAdapter.KEY_STEP));
			strKnp5=cursor.getString(cursor.getColumnIndex(MyDataBaseAdapter.KEY_CALORY));
			strLevel2p5=cursor.getString(cursor.getColumnIndex(MyDataBaseAdapter.KEY_LEVEL2P5));
			strLevel3p5=cursor.getString(cursor.getColumnIndex(MyDataBaseAdapter.KEY_LEVEL3P5));
			strLevel4p5=cursor.getString(cursor.getColumnIndex(MyDataBaseAdapter.KEY_LEVEL4P5));
			strYxbssum=cursor.getString(cursor.getColumnIndex(MyDataBaseAdapter.KEY_YXBSSUM));
			//检查是否是另一天的数据
			if(!strDate.equals(strSendDate))
			{
				//另一天数据，记录日期
				strSendDate=strDate;
				datelist.add(strSendDate);
			}
			
		//拼接成详细包json
			JSONArray json = new JSONArray();
			JSONObject job = new JSONObject();
			StringBuilder jsonData=new StringBuilder();
			jsonData.append("{\"company\":\"").append(company).append("\",\"password\":\"").append(password).append("\",\"data\":{")
				.append("\"apptype\":\"").append(apptype).append("\",")
				.append("\"deviceid\":\"").append(deviceid).append("\",")
				.append("\"date\":\"").append(strDate).append("\",")
				.append("\"datatype\":\"").append("STEPDETAIL").append("\",")
				.append("\"hour\":\"").append(strHour).append("\",")
				.append("\"datavalue\":")	
				.append("[").append("{\"snp5\":\"").append(strSnp5).append("\"}").append(",")
				.append("{\"knp5\":\"").append(strKnp5).append("\"}").append(",")
				.append("{\"level2p5\":\"").append(strLevel2p5).append("\"}").append(",")
				.append("{\"level3p5\":\"").append(strLevel3p5).append("\"}").append(",")
				.append("{\"level4p5\":\"").append(strLevel4p5).append("\"}").append(",")
				.append("{\"yuanp5\":\"").append(strYxbssum).append("\"}")
			.append("]").append("}").append("}");
			//TODO 发送数据
			Log.v("test","json detail string="+jsonData.toString());
			
			try {
				flag = sendData(url,jsonData.toString());
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//更改sendflag
			ContentValues args=new ContentValues();
			args.put(KEY_SENDFLAG, 1);
			mSQLiteDatabase.update(DB_TABLE, args, KEY_ID+"="+id, null);
		}
		
		for(int i=0;i<datelist.size();i++)
		{
			String strClause=KEY_DATE+"=\""+datelist.get(i)+"\"";
			cursor=mSQLiteDatabase.query(DB_TABLE, new String[]{KEY_DATE,"sum("+KEY_SUM_STEP+")","sum("+KEY_SUM_CALORY+")","sum("+KEY_SUM_DISTANCE+")","sum("+KEY_SUM_LV2+")","sum("+KEY_SUM_LV3+")","sum("+KEY_SUM_LV4+")"},strClause , null, null, null, null, null);
			cursor.moveToFirst(); 
			strstepSum=cursor.getString(cursor.getColumnIndex("sum("+KEY_SUM_STEP+")"));
			strcalSum=cursor.getString(cursor.getColumnIndex("sum("+KEY_SUM_CALORY+")"));
			strdistanceSum=cursor.getString(cursor.getColumnIndex("sum("+KEY_SUM_DISTANCE+")"));
			strlv2Sum=Integer.toString((cursor.getInt(cursor.getColumnIndex("sum("+KEY_SUM_LV2+")")))/60);//单位改为分钟
			strlv3Sum=Integer.toString((cursor.getInt(cursor.getColumnIndex("sum("+KEY_SUM_LV3+")")))/60);
			strlv4Sum=Integer.toString((cursor.getInt(cursor.getColumnIndex("sum("+KEY_SUM_LV4+")")))/60);
			strSendDate=cursor.getString(cursor.getColumnIndex(KEY_DATE));
			
			//拼接成简要包json
			JSONArray json = new JSONArray();
			JSONObject job = new JSONObject();
			StringBuilder jsonData=new StringBuilder();
			jsonData.append("{\"company\":\"").append(company).append("\",\"password\":\"").append(password).append("\",\"data\":{")
				.append("\"apptype\":\"").append(apptype).append("\",")
				.append("\"deviceid\":\"").append(deviceid).append("\",")
				.append("\"date\":\"").append(strSendDate).append("\",")
				.append("\"datatype\":\"").append("STEPCOUNT").append("\",")
				.append("\"stepSum\":\"").append(strstepSum).append("\",")
				.append("\"calSum\":\"").append(strcalSum).append("\",")
				.append("\"distanceSum\":\"").append(strdistanceSum).append("\",")
				.append("\"yxbssum\":\"").append(0).append("\",")//有效步数暂未计算
				.append("\"weight\":\"").append(weight).append("\",")
				.append("\"stride\":\"").append(stride).append("\",")
				.append("\"degreeOne\":\"").append(0).append("\",")//degree 1暂不计算
				.append("\"degreeTwo\":\"").append(strlv2Sum).append("\",")
				.append("\"degreeThree\":\"").append(strlv3Sum).append("\",")
				.append("\"degreeFour\":\"").append(strlv4Sum).append("\",")
				.append("\"uploadType\":\"").append(uploadType).append("\"")
			.append("}").append("}");
			//TODO 发送数据
			Log.v("test","json brief string="+jsonData.toString());
			try {
				flag = sendData(url,jsonData.toString());
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    //关闭cursor
			cursor.close();
		}
		
		
		
	return "共上传详细包"+totalNum+"个,简要包"+datelist.size()+"个";
	}


	/**
	 * post请求
	 * @param postEMR
	 * @param json
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	  public boolean sendData(String url,String json) throws HttpException, IOException
	  {

		  boolean flag = false;
		  String key = "datas";
		 HttpPost httpRequest=new HttpPost(url);
		 List<NameValuePair>params=new ArrayList<NameValuePair>();
		 params.add(new BasicNameValuePair(key,json));
//		 params.add(new BasicNameValuePair(key,test));
		 HttpEntity httpentity=new UrlEncodedFormEntity(params,"gb2312");
		 httpRequest.setEntity(httpentity);
		 HttpClient httpclient=new DefaultHttpClient();
		 HttpResponse httpResponse=httpclient.execute(httpRequest);
		 if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK)
		 {
			 Log.v("test","http传输成功");
		 }
		 else
		 {
			 Log.v("test","http传输失败+"+httpResponse.getStatusLine().getStatusCode());
		 }
	return flag;
	 } 
	
}

