package cmcc.mhealth.bean;

import java.util.ArrayList;
import java.util.List;

import cmcc.mhealth.common.Logger;

public class GroupPkInfo extends BaseNetItem {
	public static String TAG = "GroupPkInfo";
	//public String status = "SUCCESS";
	public List<GroupInfo> grouppkdata; // ������Ϣ�б�(����������Ϣ)

	public GroupPkInfo() {
		grouppkdata = new ArrayList<GroupInfo>();
	}

	public void setValue(GroupPkInfo data) {
		this.status = data.status;
		this.grouppkdata = data.grouppkdata;
	}

	public void initialDate() {
		// TODO �˶�ǿ���Ƿ���Ҫ��ʼ��
		sortGroupPkInfo();
	}

	private void sortGroupPkInfo() {
		int size = grouppkdata.size();
		if (size <= 0)
			return;

		for (int i = 0; i < size; i++) {
			int k = i;
			// find max
			for (int j = i + 1; j < size; j++) {
				int result = grouppkdata.get(j).compare(grouppkdata.get(k));
				if (result == 0)
					k = j;
			}
			if (k != i) {// change position
				GroupInfo data = grouppkdata.get(i);
				grouppkdata.set(i, grouppkdata.get(k));
				grouppkdata.set(k, data);
			}
		}
	}

	@Override
	public void setValue(BaseNetItem bni) {
		// TODO Auto-generated method stub
		if (null != bni)
			this.setValue((GroupPkInfo) bni);
	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		GroupPkInfo info = (GroupPkInfo)bni;
		if(info.grouppkdata == null){
			Logger.e(TAG, "data is null");
			return false;
		}
		return true;
	}
}
