/********************************************
 * �ļ���		��ActivityMedalInfo.java
 * �汾��Ϣ	��1.00
 * �����ˣ�Gaofei - �߷�
 * ����ʱ�䣺2013-4-19 ����3:07:29   
 * �޸��ˣ�Gaofei - �߷�
 * �޸�ʱ�䣺2013-4-19 ����3:07:29  
 * ��������	��
 * 
 * CopyRight(c) China Mobile 2013   
 * ��Ȩ����   All rights reserved
 *******************************************/
package cmcc.mhealth.bean;

import java.util.ArrayList;
import java.util.List;

/**   
 *    
 * ��Ŀ���ƣ�iShangTrunk   
 * �����ƣ�ActivityMedalInfo   
 * ��������   
 * �����ˣ�Gaofei - �߷�
 * ����ʱ�䣺2013-4-19 ����3:07:29   
 * �޸��ˣ�Gaofei - �߷�
 * �޸�ʱ�䣺2013-4-19 ����3:07:29   
 * �޸ı�ע��   
 * @version    
 *    
 */
public class ActivityDetailData {
	public String myname;//= "�߷�";
	public String mygroup;//="ƽ̨����"; 
	public String avgstep;//= "8864"; 
	public String ratescore;//="80"; 
	public String hitduration;//="13"; 
	public String groupavgstep;//= "8864"; 
	public String groupratescore;//="80"; 
	

	public List<MedalInfo>  medalinfo;
	public ActivityDetailData(){
		medalinfo = new  ArrayList<MedalInfo>();
	}
	public void setValue(ActivityDetailData data) {
		this.myname = data.myname;
		this.mygroup = data.mygroup;
		this.avgstep = data.avgstep;
		this.ratescore = data.ratescore;
		this.hitduration = data.hitduration;
		this.groupavgstep = data.groupavgstep;
		this.groupratescore = data.groupratescore;
		this.medalinfo = data.medalinfo;
	}

}
