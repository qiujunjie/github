package cmcc.mhealth.receiver;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Config;

public class SampleMailRecoder extends BroadcastReceiver

{
	public static final String TAG = "Recoder:SampleMailRecoder";
	public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
			SmsMessage[] messages = getMessagesFromIntent(intent);
			for (SmsMessage message : messages) {
				Common.wirteStringToSd(Config.RECORD_URL, Common.getDateFromLongToStr(new Date().getTime()) + ",SMS,RECEIVED:" + message.getDisplayOriginatingAddress());
			}
		}
	}

	public final SmsMessage[] getMessagesFromIntent(Intent intent) {
		Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
		byte[][] pduObjs = new byte[messages.length][];
		for (int i = 0; i < messages.length; i++) {
			pduObjs[i] = (byte[]) messages[i];
		}
		byte[][] pdus = new byte[pduObjs.length][];
		int pduCount = pdus.length;
		SmsMessage[] msgs = new SmsMessage[pduCount];
		for (int i = 0; i < pduCount; i++) {
			pdus[i] = pduObjs[i];
			msgs[i] = SmsMessage.createFromPdu(pdus[i]);
		}
		return msgs;
	}
}