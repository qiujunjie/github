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
 * 通用SampleFragment的子Fragment实例<br>
 * @author zy
 *
 */
public class ChildFragment extends SampleFragment {
	private TextView mSampleTV;
	private MenuDialog menuDialog;
	private List<CommonBottomMenuItem> items;

	//onCreateView如果使用默认空布局可省略此方法，当然一般不需要这么做
	//inflate新布局之后请在tag中设定"inflated"，父fragment中将不会使用默认布局
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.sample_fragment_blank, container, false);
		view.setTag("inflated");
		super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		return view;
	}
	//baseFragment初始化之后首先调用此方法
	@Override
	public void findViews() {
		super.findViews();
		mTextViewTitle.setText("标准Fragment");
		items = new ArrayList<CommonBottomMenuItem>();
		//实例化菜单dialog
		menuDialog = new MenuDialog();
		//注册菜单点击position的返回监听
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
	//底部弹出菜单的内容构建
	private void loadMenuItems() {
		items.add(new CommonBottomMenuItem(1, "选项1", R.drawable.avatar_female_middle));
		items.add(new CommonBottomMenuItem(2, "选项2", R.drawable.avatar_group_middle));
		items.add(new CommonBottomMenuItem(3, "选项3", R.drawable.avatar_male_middle));
		items.add(new CommonBottomMenuItem(4, "选项4", R.drawable.avatar_female_middle));
	}
	//baseFragment初始化之后其次调用此方法
	@Override
	public void clickListner() {
		super.clickListner();
		mSampleTV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//点击弹出底部菜单
				menuDialog.showBottomMenuDialog(mActivity, items);
			}
		});
	}

	//baseFragment初始化之后最后调用此方法
	@Override
	public void loadLogic() {
		super.loadLogic();
		mSampleTV.setText("mSampleTV");
	}
}
