package com.cmcc.ishang.lib.step;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

/*
 * 包括文件的存储、读取与上传
 */
public class FileProcess {
	
	static String mDir = Environment.getExternalStorageDirectory().toString()+"//pedometer//data";
	

	/*
	 * 初始化SD卡路径
	 *路径为/sdcard/pedometer/data
	 */
	public static boolean initFileDir()
	{
		 
		  File dFile=new File(mDir);
		  if(!dFile.exists())
		  {
			  dFile.mkdirs();
		  }
		return true;
		
	}
	/*
	 * 将每天的简要包内容存储在yyyymmdd命名的properties中，再存在相应的文件里。
	 * yyyymmdd_t.dat已上传文件
	 * yyyymmdd_f.dat未上传文件
	 */
	@SuppressLint({ "DefaultLocale", "WorldWriteableFiles" })
	public static boolean storeDataPerDay(DataStructPerDay data)
	{
		Properties properties=new Properties();
		String y=String.format("%d", data.year);
		String m=(data.month<10)?("0"+data.month):(""+data.month);
		String d=(data.day<10)?("0"+data.day):(""+data.day);
		String key=y+m+d;
		
		properties.put(key, data);
		try {
			@SuppressWarnings("deprecation")
			FileOutputStream stream=new FileOutputStream(new File(mDir+"//"+key+"_f.dat"));
			properties.store(stream, "");
			stream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/*
	 * 将每小时的详细包内容存储在yyyymmddhh命名的properties中，再存在相应的文件里。
	 * yyyymmddhh_t.dat已上传文件
	 * yyyymmddhh_f.dat未上传文件
	 */
	@SuppressLint({ "DefaultLocale", "WorldWriteableFiles" })
	public static boolean storeDataPerHour(DataStructPerHour data)
	{
		Properties properties=new Properties();
		String y=String.format("%d", data.year);
		String m=(data.month<10)?("0"+data.month):(""+data.month);
		String d=(data.day<10)?("0"+data.day):(""+data.day);
		String h=(data.hour<10)?("0"+data.hour):(""+data.hour);
		String key=y+m+d+h;
		properties.put(key, data);
		try {
			@SuppressWarnings("deprecation")
			FileOutputStream stream=new FileOutputStream(new File(mDir+"//"+key+"_f.dat"));
			properties.store(stream, "");
			stream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/*
	 * 通过日期获得当前日期的简单包文件
	 * date格式YYYYMMDD
	 */
	
	public static DataStructPerDay getDataPerDay(String date)
	{
		Properties properties=new Properties();
		FileInputStream stream=null;
		String key1=date+"_f.dat";
		String key2=date+"_t.dat";
		File f1=new File(mDir+"//"+key1);
		if(f1.exists())
		{
	
			try {
				stream=new FileInputStream(f1);
				properties.load(stream);
				stream.close();
				return (DataStructPerDay)properties.get(date);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		File f2=new File(mDir+"//"+key2);
		if(f2.exists())
		{
	
			try {
				stream=new FileInputStream(f2);
				properties.load(stream);
				stream.close();
				return (DataStructPerDay)properties.get(date);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		return null;
		
	}
	
	/*
	 * 通过日期小时获得当前小时的详细包文件
	 * date格式YYYYMMDDHH
	 */
	
	public static DataStructPerHour getDataPerHour(String date)
	{
		Properties properties=new Properties();
		FileInputStream stream=null;
		String key1=date+"_f.dat";
		String key2=date+"_t.dat";
		File f1=new File(mDir+"//"+key1);
		if(f1.exists())
		{
	
			try {
				stream=new FileInputStream(f1);
				properties.load(stream);
				stream.close();
				return (DataStructPerHour)properties.get(date);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		File f2=new File(mDir+"//"+key2);
		if(f2.exists())
		{
	
			try {
				stream=new FileInputStream(f2);
				properties.load(stream);
				stream.close();
				DataStructPerHour d=(DataStructPerHour)properties.get(date);
				d.sendflag=true;
				return d;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		return null;
		
	}
	
	/*
	 * 上传所有数据。
	 */
	public static int transmitAllData()
	{
		File dir=new File(mDir);
		int isSend=0;
		File[]listFile=dir.listFiles();
		if(listFile==null)
			return -1;
		for(int i=0;i<listFile.length;i++)
		{
			isSend=listFile[i].getName().indexOf("_f");
			if(isSend!=-1)//找到了字符串_f 说明该文件还没有传输过
			{
				
				if(transmitFile(listFile[i])==0)//传输成功
				{
					String str=listFile[i].getName().replace("_f", "_t");
					listFile[i].renameTo(new File(str));//修改文件名。
				}
			
			}
		}
		
		return 1;
	}
	
	/*
	 * 
	 */
	static int  transmitFile(File file)
	{
		//TODO 传输ListFile[i]
		return 0;
	}

}
