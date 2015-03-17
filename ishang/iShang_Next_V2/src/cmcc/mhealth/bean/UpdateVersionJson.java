package cmcc.mhealth.bean;

import cmcc.mhealth.common.Logger;

public class UpdateVersionJson extends BaseNetItem {

	private static final String TAG = "UpdateVersionJson";

	public UpdateVersionJson() {

	}

	public UpdateVersionJson(String verCode, String updateInfo, String verName,
			String download, String appName) {
		super();
		this.verCode = verCode;
		this.updateInfo = updateInfo;
		this.verName = verName;
		this.download = download;
		this.AppName = appName;
	}
	
	@Override
	public boolean isValueData(BaseNetItem bni) {
		UpdateVersionJson info = (UpdateVersionJson)bni;
		if(info.AppName == null){
			Logger.e(TAG, "data is null");
			return false;
		}
		return true;
	}

	public String verCode;
	public String updateInfo;
	public String verName;
	public String download;
	public String AppName;

	@Override
	public void setValue(BaseNetItem bni) {
		// TODO Auto-generated method stub
		if (null != bni) {
			setValue((UpdateVersionJson) bni);
		}
	}

	public void setValue(UpdateVersionJson uj) {
		this.status = uj.status;
		
		this.verCode = uj.verCode;
		this.updateInfo = uj.updateInfo;
		this.verName = uj.verName;
		this.download = uj.download;
		this.AppName = uj.AppName;
	}
}
