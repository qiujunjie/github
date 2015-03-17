package cmcc.mhealth.bean;


public class ActivityDetailMessageInfo extends BaseNetItem {
	// 文件类型
	public String description = "";

	public void setValue(ActivityDetailMessageInfo date) {
		this.status = date.status;
		this.description = date.description;
	}

	@Override
	public void setValue(BaseNetItem bni) {
		if (null != bni) {
			setValue((ActivityDetailMessageInfo) bni);
		}
	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		//**
		return description.length()>0;
	}

}
