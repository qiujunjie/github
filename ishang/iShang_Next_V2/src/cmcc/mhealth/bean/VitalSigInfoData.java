package cmcc.mhealth.bean;

import java.util.List;
/**
 * �����������ݰ�
 * @author zy
 *
 */
public class VitalSigInfoData{
	public String DownloadTime; //��������ʱ��
	public List<VitalSignInfoDataBean> DataArray;
	public String getDownloadTime() {
		return DownloadTime;
	}
	public void setDownloadTime(String downloadTime) {
		DownloadTime = downloadTime;
	}
	public List<VitalSignInfoDataBean> getDataArray() {
		return DataArray;
	}
	public void setDataArray(List<VitalSignInfoDataBean> dataArray) {
		DataArray = dataArray;
	}

	
	
}
