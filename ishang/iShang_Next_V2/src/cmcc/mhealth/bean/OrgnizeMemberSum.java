package cmcc.mhealth.bean;

/**
 * ��Ŀ���ƣ�i-shang0130 �����ƣ�GroupMemberSum �������� ��ȡ�������������� �����ˣ�Qiujunjie - �񿡽�
 * ����ʱ�䣺2013-3-18 ����2:27:52 �޸��ˣ�Qiujunjie - �񿡽� �޸�ʱ�䣺2013-3-18 ����2:27:52 �޸ı�ע��
 * 
 * @version
 * 
 */
public class OrgnizeMemberSum extends BaseNetItem {
	public static String TAG = "GroupMemberSum";
	//public String status = "SUCCESS";
	public int orgnizememsum;

	public void setValue(OrgnizeMemberSum data) {
		this.status = data.status;
		this.orgnizememsum = data.orgnizememsum;
	}

	@Override
	public void setValue(BaseNetItem bni) {
		// TODO Auto-generated method stub
		if (null != bni)
			setValue((OrgnizeMemberSum) bni);
	}
	
	@Override
	public boolean isValueData(BaseNetItem bni) {
//		OrgnizeMemberSum info = (OrgnizeMemberSum)bni;
//		if(info.orgnizememsum == 0)
//			return false;
		return true;
	}
}
