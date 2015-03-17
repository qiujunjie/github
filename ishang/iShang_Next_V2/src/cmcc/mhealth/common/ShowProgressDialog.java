package cmcc.mhealth.common;

import android.app.ProgressDialog;
import android.content.Context;

public class ShowProgressDialog {
	private static ProgressDialog mProgressDialog;
	
	public static void showProgressDialog(String msg,final Context context) {
	    mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setMessage(msg);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.show();
	}
	
	/**
	 * È¡Ïû dialog
	 */
	public static void dismiss() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
	}
}
