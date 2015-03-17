package cmcc.mhealth.bean;

import cmcc.mhealth.common.Logger;

public class GroupIdInfo extends BaseNetItem {
	public static String TAG = "GroupIdInfo";

	//public String status = "SUCCESS";
	public String groupid = "null"; // ����ID--Ψһ��ʶ
	public String groupname = "null"; // ����ID--Ψһ��ʶ

	public void setValue(GroupIdInfo data) {
		this.status = data.status;
		this.groupid = data.groupid;
		this.groupname = data.groupname;
	}

	@Override
	public void setValue(BaseNetItem bni) {
		// TODO Auto-generated method stub
		if (null != bni)
			setValue((GroupIdInfo) bni);
	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		GroupIdInfo info = (GroupIdInfo)bni;
		if(info.groupid == null){
			Logger.e(TAG, "data is null");
			return false;
		}
		return true;
	}

}
