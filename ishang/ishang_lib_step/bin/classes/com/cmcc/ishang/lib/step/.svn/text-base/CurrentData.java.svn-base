package com.cmcc.ishang.lib.step;

import android.util.Log;

/*
 * 单例类，用于存放当前的数据，包括当前的简要包和详细包
 */
public class CurrentData {
	private static CurrentData instance = null;
	public DataStructPerDay dataPerDay = null;
	public DataStructPerHour dataPerHour = null;

	private CurrentData() {}
	public static CurrentData getInstance() {
		if (instance == null) {
			instance = new CurrentData();
			instance.dataPerDay = new DataStructPerDay();
			instance.dataPerHour = new DataStructPerHour();
			Log.v("test", "current data is null");
		}
		return instance;
	}
}
