package cmcc.mhealth.basic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cmcc.mhealth.R;
import cmcc.mhealth.common.SharedPreferredKey;

public class SampleFragment extends BaseFragment implements OnClickListener{
	protected static final String TAG = "SampleFragment";
	
	protected TextView mTextViewTitle;// ������
	protected ImageView mBack;// �໬��ť
	protected String mTitle;
	
	protected String mPhoneNum; //�ֻ���
	protected String mPassword;//����

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(container == null || container.getTag() == null || !"inflated".equals(container.getTag().toString())){
			container = (ViewGroup) inflater.inflate(R.layout.sample_fragment_blank, container, false);
		}
		super.onCreateView(inflater, container, savedInstanceState);
		return container;
	}

	private void initView() {
		mTextViewTitle = findView(R.id.textView_title);
		mBack = findView(R.id.button_input_bg_back);
		mBack.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_input_bg_back:
			showMenu();
			break;
		}
	}
	
	@Override 
	public void findViews() {
		initView();
		loadNessesaryInfo();
	};
	
	private void loadNessesaryInfo() {
		mPhoneNum = sp.getString(SharedPreferredKey.PHONENUM, null); // �õ��绰����
		mPassword = sp.getString(SharedPreferredKey.PASSWORD, null); // �õ�����
	}

	@Override
	public void clickListner() {
		mBack.setOnClickListener(this);
	}
	@Override
	public void loadLogic() {
	}
}
