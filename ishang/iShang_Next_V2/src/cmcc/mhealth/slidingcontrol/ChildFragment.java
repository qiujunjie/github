package cmcc.mhealth.slidingcontrol;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import cmcc.mhealth.R;
import cmcc.mhealth.basic.SampleFragment;
import cmcc.mhealth.bean.CommonBottomMenuItem;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.common.MenuDialog;
import cmcc.mhealth.common.MenuDialog.onClickedItemPosition;

/**
 * ͨ��SampleFragment����Fragmentʵ��<br>
 * @author zy
 *
 */
public class ChildFragment extends SampleFragment {
	private TextView mSampleTV;
	private MenuDialog menuDialog;
	private List<CommonBottomMenuItem> items;

	//onCreateView���ʹ��Ĭ�Ͽղ��ֿ�ʡ�Դ˷�������Ȼһ�㲻��Ҫ��ô��
	//inflate�²���֮������tag���趨"inflated"����fragment�н�����ʹ��Ĭ�ϲ���
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.sample_fragment_blank, container, false);
		view.setTag("inflated");
		super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		return view;
	}
	//baseFragment��ʼ��֮�����ȵ��ô˷���
	@Override
	public void findViews() {
		super.findViews();
		mTextViewTitle.setText("��׼Fragment");
		items = new ArrayList<CommonBottomMenuItem>();
		//ʵ�����˵�dialog
		menuDialog = new MenuDialog();
		//ע��˵����position�ķ��ؼ���
		menuDialog.setOnClickedItemPosition(new onClickedItemPosition() {
			@Override
			public void onClickedPosition(int position) {
				Logger.d("testing showing position", "p:" + items.get(position).getId());
				menuDialog.dismiss();
			}
		});
		mSampleTV = findView(R.id.mh_sample_textview);
		loadMenuItems();
	}
	//�ײ������˵������ݹ���
	private void loadMenuItems() {
		items.add(new CommonBottomMenuItem(1, "ѡ��1", R.drawable.avatar_female_middle));
		items.add(new CommonBottomMenuItem(2, "ѡ��2", R.drawable.avatar_group_middle));
		items.add(new CommonBottomMenuItem(3, "ѡ��3", R.drawable.avatar_male_middle));
		items.add(new CommonBottomMenuItem(4, "ѡ��4", R.drawable.avatar_female_middle));
	}
	//baseFragment��ʼ��֮����ε��ô˷���
	@Override
	public void clickListner() {
		super.clickListner();
		mSampleTV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//��������ײ��˵�
				menuDialog.showBottomMenuDialog(mActivity, items);
			}
		});
	}

	//baseFragment��ʼ��֮�������ô˷���
	@Override
	public void loadLogic() {
		super.loadLogic();
		mSampleTV.setText("mSampleTV");
	}
}
