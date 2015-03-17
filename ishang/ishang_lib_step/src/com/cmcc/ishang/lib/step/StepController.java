package com.cmcc.ishang.lib.step;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.cmcc.ishang.lib.step.detector.StepService;

public class StepController {
	private Context context;
	private StepService mService;
	private Handler mHandler;
	
	private final static String SC_ACTION = "com.cmcc.ishang.lib.step.detector.StepService.FLITE"; 

	public static final int STEPS_MSG = 1;
	public static final int PACE_MSG = 2;
	public static final int DISTANCE_MSG = 3;
	public static final int SPEED_MSG = 4;
	public static final int CALORIES_MSG = 5;
	public static final int TEST_MSG = 99;
	
	public void setContext(Context context){
		this.context = context;
	}

	public void startStepService(String notification) {
		Intent i=new Intent(SC_ACTION);
		i.putExtra("notification", notification);
		context.startService(i);
		Log.d("testing", "trying startStepService");
	}
	
	public void startStepService() {
		Intent i=new Intent(SC_ACTION);

		context.startService(i);
		Log.d("testing", "trying startStepServiceWithoutNotification");
	}

	public void stopStepService() {
		if (mService != null) {
			unbindStepService();
			context.stopService(new Intent(SC_ACTION));
		}
		Log.d("testing", "trying stopStepService");
	}

	public void stopStepServiceWithoutBindService() {
		context.stopService(new Intent(SC_ACTION));
		Log.d("testing", "trying stopStepService");
	}
	
	private StepService.ICallback mCallback = new StepService.ICallback() {
		@Override
		public void stepsChanged(int value) {
			mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
		}

		@Override
		public void testFunction(int value) {
			mHandler.sendMessage(mHandler.obtainMessage(TEST_MSG, value, 0));
		}

		@Override
		public void caloriesChanged(float value) {
			CurrentData.getInstance().dataPerDay.calorie = (int) (value);
			mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG, (int) (value), 0));
		}

		@Override
		public void distanceChanged(int value) {
			CurrentData.getInstance().dataPerDay.distance = value;
			mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG, value, 0));
		}
	};

	public void bindStepService() {
		context.bindService(new Intent(SC_ACTION), mConnection, Context.BIND_AUTO_CREATE);
	}

	public void unbindStepService() {
		context.unbindService(mConnection);
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = ((StepService.StepBinder) service).getService();
//			mService.registerCallback(mCallback);

		}

		public void onServiceDisconnected(ComponentName className) {
			mService = null;
		}
	};
	
	
	
}
