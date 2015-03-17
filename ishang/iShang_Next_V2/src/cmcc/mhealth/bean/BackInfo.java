package cmcc.mhealth.bean;

import android.os.Parcel;
import android.os.Parcelable;


public class BackInfo extends BaseNetItem implements Parcelable{
	public static String TAG = "AddFriendInfo";

	public BackInfo() {
	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		return reason.length() > 0;
	}

	public void setValue(BaseNetItem bni) {
		if (null == bni)
			return;
		BackInfo data = (BackInfo) bni;
		status = data.status;
		reason = data.reason;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
}
