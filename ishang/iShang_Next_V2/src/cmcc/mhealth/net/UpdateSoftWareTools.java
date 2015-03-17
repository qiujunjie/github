package cmcc.mhealth.net;

import cmcc.mhealth.bean.UpdateVersionJson;

public class UpdateSoftWareTools {
	public static String newVerInfo = "";
	public static String newVerName = "";
	public static int newVerCode = 0;
	public static String download = "";

	public static boolean getServerVerCode() {
		UpdateVersionJson reqData = new UpdateVersionJson();
		if (0 != DataSyn.getInstance().updateVersion(reqData)) {
			newVerCode = -1;
			newVerName = "";
			return false;
		}

		if (null != reqData) {
			newVerCode = Integer.valueOf(reqData.verCode);
			newVerName = reqData.verName;
			newVerInfo = reqData.updateInfo;
			download = reqData.download;
		}

		return true;
	}

}
