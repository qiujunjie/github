package cmcc.mhealth.bean;


public class WeightInfo extends BaseNetItem {
	// �ļ�����
	public WeightInfoData datavalue;
	public String dataType = "";

	public void setValue(WeightInfo date) {
		this.status = date.status;
		this.dataType = date.dataType;
		this.datavalue = date.datavalue;
	}

	@Override
	public void setValue(BaseNetItem bni) {
		if (null != bni) {
			setValue((WeightInfo) bni);
		}
	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		//**
		return !(datavalue==null || "".equals(datavalue.weight));
	}

}
