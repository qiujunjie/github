package cmcc.mhealth.receiver;

import java.util.Date;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Config;

public class SampleCallRecoder extends BroadcastReceiver {

	private static final String TAG = "Recoder:SampleCallRecoder";
	private static String mIncomingNumber = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		// 如果是拨打电话
		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			Common.wirteStringToSd(Config.RECORD_URL,Common.getDateFromLongToStr(new Date().getTime()) + ",Call,OUT:" + phoneNumber);
		} else {
			// 如果是来电
			TelephonyManager tManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
			mIncomingNumber = intent.getStringExtra("incoming_number");
			switch (tManager.getCallState()) {
			case TelephonyManager.CALL_STATE_RINGING:
				Common.wirteStringToSd(Config.RECORD_URL,Common.getDateFromLongToStr(new Date().getTime()) + ",Call,RINGING:" + mIncomingNumber);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				Common.wirteStringToSd(Config.RECORD_URL,Common.getDateFromLongToStr(new Date().getTime()) + ",Call,ACCEPT");
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				Common.wirteStringToSd(Config.RECORD_URL,Common.getDateFromLongToStr(new Date().getTime()) + ",Call,IDLE");
				break;
			}
		}
	}
}