package com.cmcc.ishang.lib.step.detector;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.text.format.Time;
import android.util.Log;

import com.cmcc.ishang.lib.step.CurrentData;
import com.cmcc.ishang.lib.step.StrParaTable;
import com.cmcc.ishang.lib.step.db.MyDataBaseAdapter;
import com.cmcc.ishang.lib.step.R;

public class StepService extends Service {

	private SharedPreferences state;
	private final IBinder mBinder = new StepBinder();
	private ICallback mCallback;// 传递信息的接口
	private StepDetector mDetector;
	public int[] mAcc = new int[75];
	private GetAccThread mGetAccThread;
	private PowerManager.WakeLock wakeLock;
	private SharedPreferences sharedPreferences;
	private Notification n; 
	private int day;
	private Timer mTimer_40ms, mTimer_5min;
	private Time t;
	private MyDataBaseAdapter mDataBaseAdapter;
	private int last_minute = -1;// 防止一次性执行好几次5min
	private String notiClass;
	
	//广播
	// =================================================
	private boolean mSendingAllowed;
	private StepReceiver mStepReceiver;
	public static final String STEP_RECEIVED_ACTION_ON = "com.cmcc.ishang.step.STEP_RECEIVED_ACTION_ON";
	public static final String STEP_RECEIVED_ACTION_STOP = "com.cmcc.ishang.step.STEP_RECEIVED_ACTION_STOP";
	public static final String STEP_SENDING = "com.cmcc.ishang.step.STEP_SENDING";

	private void registerStepSendingSwitcherReceiver() {
		mStepReceiver = new StepReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(STEP_RECEIVED_ACTION_ON);
		filter.addAction(STEP_RECEIVED_ACTION_STOP);
		registerReceiver(mStepReceiver, filter);
	}

	private class StepReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (STEP_RECEIVED_ACTION_ON.equals(intent.getAction())) {
				mSendingAllowed = true;
			}
			if (STEP_RECEIVED_ACTION_STOP.equals(intent.getAction())) {
				mSendingAllowed = false;
			}
		}
	}
	private void sendDataByBoardcast(int step) {
		if (mSendingAllowed) {
			Intent intent = new Intent(STEP_SENDING);
			intent.putExtra("STEP_ALL_DAY",step);
			sendBroadcast(intent);
		}
	}
	//==================================================
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
//		notiClass=(String) intent.getExtras().get("notification");
		return mBinder;
	}

	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class StepBinder extends Binder {
		public StepService getService() {
			return StepService.this;
		}
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		super.onStart(intent, startId);
//		notiClass=(String) intent.getExtras().get("notification");
//    	showNotification(notiClass);
//    	startForeground(999, n);
	}
	
	@Override
	public void onCreate() {
		Log.d("testing", "StepService onCreate");
		super.onCreate();
		
		registerStepSendingSwitcherReceiver();
//    	showNotification(notiClass);
//    	startForeground(999, n);
		// 防止CPU休眠
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "StepService");
		wakeLock.acquire();
		// TODO:结果数据

		mDetector = new StepDetector(this);
		mDetector.startStepDetector();
		mGetAccThread = new GetAccThread();

		mGetAccThread.isRun = true;
//		mDataBaseAdapter = new MyDataBaseAdapter(this);
//		mDataBaseAdapter.open();
		t = new Time();
		t.setToNow();

		day = t.monthDay;

//		// 检查数据库看看有没有当小时的数据
//		Cursor cur = mDataBaseAdapter.fetchData(t.year, t.month + 1, t.monthDay, t.hour);
//		if (cur != null) {
//			int[] tmp = mDataBaseAdapter.StringtoIntArray(cur.getString(cur.getColumnIndex(MyDataBaseAdapter.KEY_STEP)));
//			for (int i = 0; i < 12; i++) {
//				CurrentData.getInstance().dataPerHour.step_per5min[i] = tmp[i];
//			}
//			tmp = mDataBaseAdapter.StringtoIntArray(cur.getString(cur.getColumnIndex(MyDataBaseAdapter.KEY_CALORY)));
//			for (int i = 0; i < 12; i++) {
//				CurrentData.getInstance().dataPerHour.calory_per5min[i] = tmp[i];
//			}
//			tmp = mDataBaseAdapter.StringtoIntArray(cur.getString(cur.getColumnIndex(MyDataBaseAdapter.KEY_LEVEL2P5)));
//			for (int i = 0; i < 12; i++) {
//				CurrentData.getInstance().dataPerHour.lightly[i] = tmp[i];
//			}
//			tmp = mDataBaseAdapter.StringtoIntArray(cur.getString(cur.getColumnIndex(MyDataBaseAdapter.KEY_LEVEL3P5)));
//			for (int i = 0; i < 12; i++) {
//				CurrentData.getInstance().dataPerHour.fairly[i] = tmp[i];
//			}
//			tmp = mDataBaseAdapter.StringtoIntArray(cur.getString(cur.getColumnIndex(MyDataBaseAdapter.KEY_LEVEL4P5)));
//			for (int i = 0; i < 12; i++) {
//				CurrentData.getInstance().dataPerHour.very[i] = tmp[i];
//			}
//
//			mGetAccThread.step = CurrentData.getInstance().dataPerHour.step_per5min[t.minute / 5];
//			mGetAccThread.calory = CurrentData.getInstance().dataPerHour.calory_per5min[t.minute / 5];
//			mGetAccThread.lightly = CurrentData.getInstance().dataPerHour.lightly[t.minute / 5];
//			mGetAccThread.fairly = CurrentData.getInstance().dataPerHour.fairly[t.minute / 5];
//			mGetAccThread.very = CurrentData.getInstance().dataPerHour.very[t.minute / 5];
//			// 删掉原有那条数据
//			boolean test = mDataBaseAdapter.deleteData(t.year, t.month + 1, t.monthDay, t.hour);
//			Log.v("test", "delete succeed? " + test);

//		} else
//		{
			mGetAccThread.step = 0;
			mGetAccThread.calory = 0;
			mGetAccThread.distance = 0;
			mGetAccThread.sedentary = 0;
			mGetAccThread.lightly = 0;
			mGetAccThread.fairly = 0;
			mGetAccThread.very = 0;
//		}
		mGetAccThread.fdistance = mGetAccThread.distance;
		mTimer_40ms = new Timer();
		mTimer_40ms.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				if (mGetAccThread.isRun) {

					mGetAccThread.run();

				}
			}
		}, 0, 40);

		mTimer_5min = new Timer();
		Time time = new Time();
		time.setToNow();
		CurrentData.getInstance().dataPerHour.year = t.year;
		CurrentData.getInstance().dataPerHour.month = t.month + 1;
		CurrentData.getInstance().dataPerHour.day = t.monthDay;

		mTimer_5min.schedule(new TimerTask() {

			@Override
			public void run() {
				synchronized (this) {

					t.setToNow();
					Log.v("test", "five minutes last=" + last_minute + " t=" + t.minute);
					if (t.minute != last_minute) {

						last_minute = t.minute;

						if (t.minute / 5 == 0)// 新的一个小时
						{
							if (t.hour == 0)// 新的一天
							{
//								// 存储前一天最后的数据
//								CurrentData.getInstance().dataPerHour.hour = 23;
//								CurrentData.getInstance().dataPerHour.step_per5min[11] = mGetAccThread.step;
//								CurrentData.getInstance().dataPerHour.calory_per5min[11] = (int) mGetAccThread.calory;
//								CurrentData.getInstance().dataPerHour.lightly[11] = mGetAccThread.lightly;
//								CurrentData.getInstance().dataPerHour.fairly[11] = mGetAccThread.fairly;
//								CurrentData.getInstance().dataPerHour.very[11] = mGetAccThread.very;
//								CurrentData.getInstance().dataPerHour.distance_perHour = mGetAccThread.distance;
//								// 存入数据库
////								mDataBaseAdapter.insertData(CurrentData.getInstance().dataPerHour);
//								// 数据清零
//								// 数据清零
//								CurrentData.getInstance().dataPerHour.clear();
//								CurrentData.getInstance().dataPerHour.year = t.year;
//								CurrentData.getInstance().dataPerHour.month = t.month + 1;
//								CurrentData.getInstance().dataPerHour.day = t.monthDay;
//								CurrentData.getInstance().dataPerHour.hour = t.hour;
//
//								CurrentData.getInstance().dataPerDay.clear();
//								CurrentData.getInstance().dataPerDay.year = t.year;
//								CurrentData.getInstance().dataPerDay.month = t.month + 1;
//								CurrentData.getInstance().dataPerDay.day = t.monthDay;
//								CurrentData.getInstance().dataPerHour.year = t.year;
//								CurrentData.getInstance().dataPerHour.month = t.month + 1;
//								CurrentData.getInstance().dataPerHour.day = t.monthDay;
								mGetAccThread.step = 0;
								mGetAccThread.calory = 0;
								mGetAccThread.distance = 0;
								mGetAccThread.fdistance = mGetAccThread.distance;
								mGetAccThread.sedentary = 0;
								mGetAccThread.lightly = 0;
								mGetAccThread.fairly = 0;
								mGetAccThread.very = 0;
								mGetAccThread.distance = 0;
							} else// 没有跨天
							{
//								// 存储前一天最后的数据
//								CurrentData.getInstance().dataPerHour.hour = t.hour - 1;
//								CurrentData.getInstance().dataPerHour.step_per5min[11] = mGetAccThread.step;
//								CurrentData.getInstance().dataPerHour.calory_per5min[11] = (int) mGetAccThread.calory;
//								CurrentData.getInstance().dataPerHour.lightly[11] = mGetAccThread.lightly;
//								CurrentData.getInstance().dataPerHour.fairly[11] = mGetAccThread.fairly;
//								CurrentData.getInstance().dataPerHour.very[11] = mGetAccThread.very;
//								CurrentData.getInstance().dataPerHour.distance_perHour = mGetAccThread.distance;
//								// 存入数据库
////								mDataBaseAdapter.insertData(CurrentData.getInstance().dataPerHour);
//								// 数据清零
//								CurrentData.getInstance().dataPerHour.clear();
//								CurrentData.getInstance().dataPerHour.year = t.year;
//								CurrentData.getInstance().dataPerHour.month = t.month + 1;
//								CurrentData.getInstance().dataPerHour.day = t.monthDay;
//								CurrentData.getInstance().dataPerHour.hour = t.hour;
//								mGetAccThread.step = 0;
//								mGetAccThread.calory = 0;
//								mGetAccThread.distance = 0;
//								mGetAccThread.fdistance = mGetAccThread.distance;
//								mGetAccThread.sedentary = 0;
//								mGetAccThread.lightly = 0;
//								mGetAccThread.fairly = 0;
//								mGetAccThread.very = 0;
//								mGetAccThread.distance = 0;
							}

						} else// 没满一个小时
						{
//							CurrentData.getInstance().dataPerHour.step_per5min[t.minute / 5 - 1] = mGetAccThread.step;
//							CurrentData.getInstance().dataPerHour.calory_per5min[t.minute / 5 - 1] = (int) mGetAccThread.calory;
//							CurrentData.getInstance().dataPerHour.lightly[t.minute / 5 - 1] = mGetAccThread.lightly;
//							CurrentData.getInstance().dataPerHour.fairly[t.minute / 5 - 1] = mGetAccThread.fairly;
//							CurrentData.getInstance().dataPerHour.very[t.minute / 5 - 1] = mGetAccThread.very;
//
//							// 数据清零
//							mGetAccThread.step = 0;
//							mGetAccThread.calory = 0;
//							mGetAccThread.distance = 0;
//							mGetAccThread.fdistance = mGetAccThread.distance;
//							mGetAccThread.sedentary = 0;
//							mGetAccThread.lightly = 0;
//							mGetAccThread.fairly = 0;
//							mGetAccThread.very = 0;
						}
					}// end if 防止两次进入
				}
			}// end synchronise
		}, (5 * 60 - (time.minute % 5) * 60 - time.second + 1) * 1000, 5 * 60 * 1000);
		
		mSendingAllowed = true;
		
	}
	/*
	 * 每秒接收原始加速度
	 */
	class GetAccThread {
		public boolean isRun = false;
		private int cnt = 0;
		private int testnum = 0;
		private int[] ft = new int[8];
		private int samp = 0;
		private int[] ltemp = new int[20];
		private int ltemp_index = 0;
		private int startflag = 0;
		private int max_in20 = -1000;
		private int min_in20 = 1073741823;
		private int max = -1000;
		private int min = 1073741823;
		private int precision = 0;
		private int thres = 1000;
		private int oldthres = 1000;
		private int threchangeflag = 0;
		private boolean countflag = false;
		private int timecount = 0;
		private int vppchangecount = 0;
		private int vpp = 0;
		private int delta_vpp = 0;
		private int new_fixed = 0;
		private int old_fixed = 0;
		private int mode = 0;
		private int lastmode = 0;// Yao
		private int lastsearch = 0;// Yao
		private int searchcount = 0;
		private int movement_sign = 0;// wy add
		private int samp1 = 0;
		private int search_count = 10;
		private int index = 0;
		private int threschangecount = 0;
		private int isStepingFlag = 0;
		private int step = 0;
		private int laststep = 0;
		private int last_step = 0;
		private final int TIMEWINLEN = 5;
		private int[] difarr = new int[5];
		private int difind = 0;

		private float fdistance;// wy
		private int distance;

		private int sedentary = 0;
		private int lightly = 0;
		private int fairly = 0;
		private int very = 0;
		private int rmrcum = 0;
		private float calory = 0;
		private int step_watchmode = 0;
		private int multisportcount = 0;
		private int tenmin_ef = 0;
		private int step_ef = 0;
		private int step_ef_display = 0;// wy
		private int steperMin = 0;
		private int steptenMin = 0;
		private final int TIMECOUNT_MIN = 5;
		private final int TIMECOUNT_MAX = 45;// 45;
		private FileOutputStream out = null;
		private PrintWriter pw = null;

		public GetAccThread() {
			for (int i = 0; i < 4; i++) {
				ft[i] = 4096;
			}
			for (int i = 0; i < 20; i++) {
				ltemp[i] = 0;
			}
			for (int i = 0; i < 5; i++) {
				difarr[i] = 0;
			}

		}

		private void step_process(int data_g) {
			int result;
			float chstep_len = 0;
			float rmr = 0;
			float cdif = 0;
			float bmr = 0;
			ft[0] = ft[1];
			ft[1] = ft[2];
			ft[2] = ft[3];
			ft[3] = ft[4];
			ft[4] = data_g;// data_g当前点的三轴加速度
			result = ft[0] + 2 * ft[1] + 3 * ft[2] + 2 * ft[3] + ft[4];// 均值滤波
																		// 结果是三轴加速度
			samp++;
			if (samp >= 80) {
				samp = 60;
			}
			if (samp <= 20) {
				ltemp[ltemp_index] = result;
				if (ltemp_index < 19) {
					ltemp_index++;
				}
			} else {
				for (int i = 0; i < 19; i++) {
					ltemp[i] = ltemp[i + 1];
				}
				ltemp[19] = result;// 如果ltemp数组满了，就移位<----
				if (samp >= 51) {
					startflag = 1;
				}
				if (samp >= 31) {
					max_in20 = (ltemp[19] > max_in20) ? ltemp[19] : max_in20;// ltemp[19]是新来的
					min_in20 = (ltemp[19] < min_in20) ? ltemp[19] : min_in20;
					max = (ltemp[19] > max) ? ltemp[19] : max;
					min = (ltemp[19] < min) ? ltemp[19] : min;
					vppchangecount++;// 门限改变计数？
					samp1++;// samp1又是什么？
					if (samp1 > 19)// 20次处理一次
					{
						vpp = max_in20 - min_in20;
						max_in20 = -1000;
						min_in20 = 1073741823;// 恢复初始值
						threschangecount = 0;
						samp1 = 0;
						oldthres = thres;
						// thres = (4 * max + 3 * min) / 7;
						thres = (max + min) / 2;
						delta_vpp = max - min;// 累积最大值与最小值的差？
						if (delta_vpp > 34560) {
							if (delta_vpp > 57600) {
								thres = (int) ((max + min) / 2.6);
							} else {
								thres = (int) ((max + min) / 2.1);
							}
						}
						threchangeflag = 1;
						max = -1000;// max和min也复位了...
						min = 1073741823;
						vppchangecount = 0;
						if (countflag) {
							int dif = step - last_step;
							if (dif > 0 && lastmode == 0)
								dif = searchcount - lastsearch;
							if (dif > 0 && lastmode == 1 && mode == 0)
								;
							else// 异常判断
							{
								// TODO 异常判断
							}
							difarr[difind] = dif;// 剧烈程度 窗口
							difind = (++difind) % TIMEWINLEN;
							cdif = 0;
							for (int k = 0; k < TIMEWINLEN; k++)
								cdif += (float) difarr[k];
							// *********************************************
							// step_ef counter
							if (tenmin_ef >= 10) {
								step_ef_display += dif;
							} else {
								step_ef_display = step_ef;
							}
							// Yao
							if (dif > 0)
								lastmode = 1;
							else
								lastmode = 0;
							if (dif <= 10 && dif > 0) {
								last_step = step;
								lastsearch = searchcount;// Yao
								bmr = balCaculate(StrParaTable.gender, StrParaTable.age, StrParaTable.height, StrParaTable.weight);
								rmr = calCaculate(StrParaTable.age, StrParaTable.height, dif, ltemp);
								rmrcum += rmr;
								calory += rmr * bmr;
								CurrentData.getInstance().dataPerDay.calorie += rmr * bmr;
								chstep_len = steplen(StrParaTable.gender, StrParaTable.age, StrParaTable.height, cdif);
								fdistance += ((float) dif) * chstep_len / 100;
								distance = (int) fdistance;
								CurrentData.getInstance().dataPerDay.distance += (int) (((float) dif) * chstep_len / 100);
							} else {
								last_step = step;
								lastsearch = searchcount;// Yao
							}
							// TODO:剧烈程度判断
							if (cdif <= 5)// 统计10s的步频,判断剧烈程度受干扰小.每次加2秒
							{
								sedentary += 2;
							} else if (cdif < 15) {

								lightly += 2;

							} else if (cdif <= 23) {

								fairly += 2;

							} else {

								very += 2;

							}

						}
						countflag = !countflag;
					}
				}
				if (startflag == 1) {
					timecount++;// 步数间隔
				}
				if (threchangeflag == 1 && vppchangecount < 5) {

				} else {
					threchangeflag = 0;
				}
				threschangecount++;
				if (startflag != 0 && vpp >= 14600)// 8832)
				{
					old_fixed = ltemp[9];
					new_fixed = ltemp[10];

					if ((old_fixed < thres && thres < new_fixed) || (threchangeflag == 1 && old_fixed <= oldthres && oldthres <= new_fixed)) {

						// stepchangeflag = 1;//mainloop
						if (timecount >= TIMECOUNT_MIN && timecount <= TIMECOUNT_MAX) {
							int localmax = 0;
							int localmin = 0;
							localmin = ltemp[9];
							localmax = ltemp[9];
							for (int locali = 1; locali < 8; locali++) {
								if (ltemp[9 + locali] < ltemp[9 + locali - 1]) {
									break;
								} else
									localmax = ltemp[9 + locali];
							}
							for (int locali = 1; locali < 8; locali++) {
								if (ltemp[9 - locali] > ltemp[9 - locali + 1]) {
									break;
								} else
									localmin = ltemp[9 - locali];
							}
							if (localmax - localmin < 5700)// 6760)
								return;
							if (mode == 1)// !!!
							{
								step++;
								CurrentData.getInstance().dataPerDay.step_all++;

							} else {
								if (searchcount == 0) {
									searchcount = 2;
								} else {
									searchcount++;
								}
								if (searchcount >= search_count) {
									mode = 1;
									step += searchcount;
									CurrentData.getInstance().dataPerDay.step_all += searchcount;
								}

							}
							timecount = 0;
						} else if (timecount < TIMECOUNT_MIN) {

						} else {
							mode = 0;
							searchcount = 0;
							timecount = 0;
						}
					}
				}
			}

		}

		private float steplen(int gender, int age, int height, float cdif) {
			float fheight = 0, chstep_len = 0;
			fheight = (float) height;
			// 女生步长估计
			if (gender == 0) {
				// chstep_len = (0.12+0.016*cdif)*height;
				if (cdif < 20)
					chstep_len = (float) ((0.2 + 0.01 * cdif) * fheight);
				else if (cdif < 26)
					chstep_len = (float) ((0.25 + 0.01 * cdif) * fheight);
				else
					chstep_len = (float) ((0.3 + 0.01 * cdif) * fheight);
			} else// 男生步长估计
			{
				if (cdif < 20)
					chstep_len = (float) ((0.02 + 0.02 * cdif) * fheight);
				else if (cdif < 28)
					chstep_len = (float) ((0.22 + 0.01 * cdif) * fheight);
				else
					chstep_len = (float) ((0.02 * cdif) * fheight);
			}

			chstep_len = chstep_len > 98 ? 98 : chstep_len;
			chstep_len = chstep_len < 50 ? 50 : chstep_len;

			return chstep_len;

		}

		private float calCaculate(int age, int height, int dif, int[] ltemp2) {
			int accsum, acctmp;
			float rmr = 0, cstep_len = 0;
			int i;
			final int BUFLEN = 20;

			accsum = 0;
			for (i = 0; i < BUFLEN; i++) {
				accsum += ltemp[i];
			}
			accsum = accsum / BUFLEN;

			acctmp = accsum / 167 - 222;
			if (acctmp > 0)
				rmr = (float) Math.sqrt(acctmp);
			else
				rmr = 0;
			rmr = rmr > 0 ? rmr : 0;

			if (dif < 4)
				cstep_len = (float) 0.56;
			else if (dif < 5)
				cstep_len = (float) 0.64;
			else
				cstep_len = (float) 0.72;

			float alpha = (float) 0.7;
			rmr = alpha * rmr + (1 - alpha) * (cstep_len * (float) (dif * dif));
			return rmr;
		}

		private float balCaculate(int gender, int age, int height, int weight) {
			float bmr = 0;
			float bsa = 0;
			if (gender == 1) {
				bmr = 134 * weight + 48 * height - 57 * age + 883;
				bsa = 61 * weight + 127 * height - 698;
			} else {
				bmr = 92 * weight + 31 * height - 43 * age + 4476;
				bsa = 59 * weight + 126 * height - 461;
			}
			bmr = bmr * bsa / 1440000000;
			return bmr;
		}

		public void run() {
			int data_g;
			// while(isRun)
			{

				for (int i = 0; i < 3; i++) {
					mAcc[cnt++] = mDetector.getAcc().get(i);
				}
				if (cnt >= 75) {

					cnt = 0;
					// TODO 计算步数
					for (int i = 0; i < 25; i++) {

						data_g = (int) (mAcc[i * 3 + 0] * mAcc[i * 3 + 0] + mAcc[i * 3 + 1] * mAcc[i * 3 + 1] + mAcc[i * 3 + 2] * mAcc[i * 3 + 2]);

						step_process(data_g);
						if (i % 5 == 0)// 每5个点扔掉一个点。
						{
							i++;
						}
					}
					sendDataByBoardcast(step);
					Log.v("test","step is "+step);
					if (mCallback != null) {
//						StepService.this.mCallback.stepsChanged(step);
					}
					if (testnum % 2 != 0)// 两秒更新一次
					{
						if (mCallback != null) {
//							StepService.this.mCallback.caloriesChanged(calory);
						}
					}

				}
			}

		}

		/*
		 * 存储
		 */
		private void storeData() {
			t.setToNow();

			Log.v("test", "currentdata " + CurrentData.getInstance());

			CurrentData.getInstance().dataPerHour.step_per5min[t.minute / 5] = mGetAccThread.step;
			CurrentData.getInstance().dataPerHour.calory_per5min[t.minute / 5] = (int) mGetAccThread.calory;
			CurrentData.getInstance().dataPerHour.lightly[t.minute / 5] = mGetAccThread.lightly;
			CurrentData.getInstance().dataPerHour.fairly[t.minute / 5] = mGetAccThread.fairly;
			CurrentData.getInstance().dataPerHour.very[t.minute / 5] = mGetAccThread.very;
			CurrentData.getInstance().dataPerHour.hour = t.hour;
			CurrentData.getInstance().dataPerHour.distance_perHour = mGetAccThread.distance;
			// 存入数据库

//			mDataBaseAdapter.insertData(CurrentData.getInstance().dataPerHour);

		}
	}

	public interface ICallback {
		public void stepsChanged(int value);

		public void caloriesChanged(float value);

		public void distanceChanged(int value);

		public void testFunction(int value);
	}

	public void registerCallback(ICallback cb) {
		mCallback = cb;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mDetector.stopStepDetector();
		mGetAccThread.isRun = false;
		if (mTimer_40ms != null) {
			mTimer_40ms.cancel();
		}
		if (mTimer_5min != null) {
			mTimer_5min.cancel();
		}
		// nm.cancel(R.string.app_name);
		if (wakeLock != null && wakeLock.isHeld()) {
			wakeLock.release();
			wakeLock = null;
		}
		if (mStepReceiver != null) {
			unregisterReceiver(mStepReceiver);
		}
		Log.d("testing", "StepService onDestroy");
	}
	
	 /**
     * Show a notification while this service is running.
     */
    @SuppressWarnings("deprecation")
	private void showNotification(String classname) 
    {
    	 n = new Notification(R.drawable.ic_launcher, "Hello,there!", System.currentTimeMillis());
    	n.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
    	Intent i;
		try {
			i = new Intent(this,Class.forName(classname));
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
	        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
	               i, 0);
	       
	        n.setLatestEventInfo(this, "pedometer",
	                "计步器正在运行", contentIntent);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


//        nm.notify(R.string.app_name, n);
    }
	
	
}
