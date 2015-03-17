package cmcc.mhealth.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cmcc.mhealth.R;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.slidingcontrol.MainCenterActivity;
import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class SamplePushReceiver extends BroadcastReceiver {
	private static final String TAG = "SamplePushReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
			Logger.d(TAG, "用户点击打开了通知");
			String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
			String content = bundle.getString(JPushInterface.EXTRA_ALERT);
			Intent msgIntent = new Intent(MainCenterActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent.putExtra("message_on_receiving", title + "\n" + content);
			context.sendBroadcast(msgIntent);
			Intent i = new Intent(context, MainCenterActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		}
	}
}
