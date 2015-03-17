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
 * �Զ��������
 * 
 * ������������ Receiver���� 1) Ĭ���û���������� 2) ���ղ����Զ�����Ϣ
 */
public class SamplePushReceiver extends BroadcastReceiver {
	private static final String TAG = "SamplePushReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
			Logger.d(TAG, "�û��������֪ͨ");
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
