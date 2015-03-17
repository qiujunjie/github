package com.cmcc.ishang.lib.step.detector;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class StepDetector implements SensorEventListener {
	private SensorManager mSensorManager;
	private Context context;
	private float[]mAcc;
	
	/*
	 * 构造函数
	 */
	public StepDetector(Context context)
	{
		mAcc=new float[3];
		this.context=context;
		mSensorManager=(SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		
		
	}
	
	
	public void startStepDetector()
	{
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);  
	}
	/*
	 * 停止监听加速度变化
	 */
	public void stopStepDetector()
	{
		mSensorManager.unregisterListener(this);  
	}
	
	/*
	 *获得三轴加速度
	 */
	public List<Integer> getAcc()
	{
		List<Integer>result=new ArrayList<Integer>();
		float[]temp=mAcc.clone();
		for(int i=0;i<3;i++)
		{
			int b=(int)(temp[i]/9.8/2*128);
//			int b=(int)(temp[i]<0?(256-(temp[i]/-9.8/2*128)):(temp[i]/9.8/2*128));
			result.add(b);
//			result.add(temp[i]);
		}
		return result;
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float[] values=event.values;  
		for(int i=0;i<3;i++)
		{
			if(values[i]>2*9.8)
			{
				mAcc[i]=(float) (2*9.8);
			}
			else if(values[i]<-2*9.8)
			{
				mAcc[i]=(float)(-2*9.8);
			}
			else
			{
				mAcc[i]=values[i];
			}
		}

	}

}
