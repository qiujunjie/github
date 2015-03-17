package cmcc.mhealth.bean;

import cmcc.mhealth.common.Logger;

public class RankingDate extends BaseNetItem {

	private static final String TAG = "RankingDate";
	public String member7seq = "12"; // 7���������
	public Member7Info member7info; // 7�����������ϸ��Ϣ
	public String member1seq; // �����������
	public Member1Info member1info; // �������������ϸ��Ϣ

	public String group7seq; // 7��������
	public Group7Info group7info; // 7����������ϸ��Ϣ
	public String group1seq; // ����������
	public Group1Info group1info; // ������������ϸ��Ϣ

	public String groupmember7seq; // 7��������������ϸ��Ϣ��Member7Info��ͬ
	public String groupmember1seq; // �������������� Member1Info
	public String avatar;
	public String membername;
	
	public RankingDate(){
		group7info = new Group7Info();
		member1info = new Member1Info();
		group1info = new Group1Info();
		member7info = new Member7Info();
	}
	
	@Override
	public boolean isValueData(BaseNetItem bni) {
		RankingDate info = (RankingDate)bni;
		if(info.group1info == null){
			Logger.e(TAG, "data is null");
			return false;
		}
		return true;
	}
	
	
	@Override
	public void setValue(BaseNetItem bni) {
		if (null == bni)
			return;
		RankingDate date = (RankingDate)bni;
		this.status = date.status;
		this.member7seq = date.member7seq;
		this.member1seq = date.member1seq;
		this.member1info = date.member1info;
		this.group7seq = date.group7seq;
		this.group7info = date.group7info;
		this.group1seq = date.group1seq;
		this.group1info = date.group1info;
		this.groupmember7seq = date.groupmember7seq;
		this.groupmember1seq = date.groupmember1seq;
		this.member7info = date.member7info;
		this.avatar = date.avatar;
		this.membername = date.membername;
	}

}
