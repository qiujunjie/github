package cmcc.mhealth.bean;


/**
 * �����������ݰ�
 * @author zy
 *
 */
public class VitalSignInfo extends BaseNetItem{
	public String dataType;
	public VitalSigInfoData datavalue;
	
	public void setValue(VitalSignInfo date) {
		this.dataType = date.dataType;
		this.datavalue = date.datavalue;
	}

	@Override
	public void setValue(BaseNetItem bni) {
		if (null != bni) {
			setValue((VitalSignInfo) bni);
		}
	}
	@Override
	public boolean isValueData(BaseNetItem bni) {
		return !(datavalue==null || "".equals(datavalue.getDownloadTime()));
		
	}
	
}
