/********************************************
 * File Name��UserRegInfo.java
 * Version	��1.00
 * Author	��Gaofei - �߷�
 * Date		��2012-4-16
 * LastModify��2012-4-16
 * Functon	����������
 * 
 * CopyRight(c) China Mobile 2012
 * All rights reserved
 *******************************************/
package cmcc.mhealth.bean;


/**
 * �� UserRegInfo company: �й��ƶ��о�Ժ���ʼ�����ͨ���о�����
 * 
 * @author by:gaofei-�߷�
 * @date 2012-4-16 ����8:29:44 version 1.0 describe:
 */
public class UserRegInfo extends BaseNetItem{
	public UserBaseInfo personprofile;
	
	public UserRegInfo() {
		super();
		personprofile = new UserBaseInfo();
	}
	@Override
	public void setValue(BaseNetItem bni) {
		UserRegInfo info = (UserRegInfo)bni;
		this.personprofile = info.personprofile;
	}
	@Override
	public boolean isValueData(BaseNetItem bni) {
		// TODO Auto-generated method stub
		return true;
	}
}
