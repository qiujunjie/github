package cmcc.mhealth.slidingcontrol;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import cmcc.mhealth.R;
import cmcc.mhealth.activity.RaceCreateActivity;
import cmcc.mhealth.activity.RaceSearchActivity;
import cmcc.mhealth.adapter.CommonFragmentPagerAdapter;
import cmcc.mhealth.basic.SampleFragment;
import cmcc.mhealth.bean.CommonBottomMenuItem;
import cmcc.mhealth.common.MenuDialog;
import cmcc.mhealth.common.MenuDialog.onClickedItemPosition;
import cmcc.mhealth.common.SharedPreferredKey;

/**
 * ͨ��SampleFragment�ľ���Fragmentʵ��<br>
 * 
 * @author zy
 * 
 */
public class RaceFragment extends SampleFragment {
	public final static int NET_RELOAD = 10000;
	public final static int SQL_RELOAD = 10001;
	
	private ViewPager mViewPagerRace;
	
	private RadioGroup mRadioGroup;
	private RadioButton mRadioButtonRace0, mRadioButtonRace1, mRadioButtonRace2;
	private RelativeLayout mFunctions;//���϶�ѡ����
	private MenuDialog menuDialog;//��ѡ���ܵ���

	// onCreateView���ʹ��Ĭ�Ͽղ��ֿ�ʡ�Դ˷�������Ȼһ�㲻��Ҫ��ô��
	// inflate�²���֮������tag���趨"inflated"����fragment�н�����ʹ��Ĭ�ϲ���
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.sample_fragment_race, container, false);
		view.setTag("inflated");
		super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		return view;
	}

	// baseFragment��ʼ��֮�����ȵ��ô˷���
	@Override
	public void findViews() {
		super.findViews();
		mRadioGroup = findView(R.id.radio_group_race);
		mRadioButtonRace0 = findView(R.id.race_radio0);
		mRadioButtonRace1 = findView(R.id.race_radio1);
		mRadioButtonRace2 = findView(R.id.race_radio2);
		mFunctions = findView(R.id.imageButton_title_add);
		mFunctions.setVisibility(View.VISIBLE);
		mFunctions.findViewById(R.id.textview_title_add).setBackgroundResource(R.drawable.top_menu_more);
		mFunctions.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.findViewById(R.id.textview_title_add).setBackgroundResource(R.drawable.top_menu_more_orange);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					v.findViewById(R.id.textview_title_add).setBackgroundResource(R.drawable.top_menu_more);
				}
				return false;
			}
		});
		menuDialog = new MenuDialog();
	}

	// baseFragment��ʼ��֮����ε��ô˷���
	@Override
	public void clickListner() {
		super.clickListner();
		mTextViewTitle.setText(R.string.race_title);
		initViewPager();
		initRadioButton();
		initFunctionButton();
	}
	
	//�ӹ���ģ��
	private void initFunctionButton() {
		final List<CommonBottomMenuItem> items = new ArrayList<CommonBottomMenuItem>();
		mFunctions.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				items.clear();
				items.add(new CommonBottomMenuItem(2, "��������", R.drawable.race_func_buildrace));
				items.add(new CommonBottomMenuItem(0, "��������", R.drawable.race_func_searchrace));
				items.add(new CommonBottomMenuItem(1, "����������", R.drawable.race_func_searchracebuilder));
				menuDialog.showBottomMenuDialog(mActivity, items);
			}
		});
		menuDialog.setOnClickedItemPosition(new onClickedItemPosition() {
			@Override
			public void onClickedPosition(int position) {
				menuDialog.dismiss();
				CommonBottomMenuItem bmi = items.get(position);
				Intent intent = null;
				switch (bmi.getId()) {
				case 0:
				case 1:
					intent = new Intent(mActivity, RaceSearchActivity.class);
					intent.putExtra("sampletitle", bmi.getMenuName());
					intent.putExtra("searchtype", bmi.getId());
					startActivity(intent);
					mActivity.overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
					break;
				case 2:
					intent = new Intent(mActivity, RaceCreateActivity.class);
					intent.putExtra("sampletitle", bmi.getMenuName());
					intent.putExtra(SharedPreferredKey.PASSWORD, mPassword);
					intent.putExtra(SharedPreferredKey.PHONENUM, mPhoneNum);
					startActivityForResult(intent, NET_RELOAD);
					mActivity.overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
					break;
				}
			}
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == NET_RELOAD){
			if(resultCode == Activity.RESULT_OK){
				mViewPagerRace.setCurrentItem(0);
				mRadioButtonRace0.setChecked(true);
				RaceChildFragment rcf = (RaceChildFragment) mViewPagerRace.getAdapter().instantiateItem(mViewPagerRace, 0);
				rcf.loadDate();
			}
		}
	}

	// baseFragment��ʼ��֮�������ô˷���
	@Override
	public void loadLogic() {
		super.loadLogic();
	}
	

	// ��ʼ��ViewPager
	private void initViewPager() {
		ArrayList<Fragment> fragmentsList = new ArrayList<Fragment>();
		fragmentsList.add(RaceChildFragment.newInstance(mPhoneNum, mPassword, 0, 10));
		fragmentsList.add(RaceChildFragment.newInstance(mPhoneNum, mPassword, 1, 10));
		fragmentsList.add(RaceChildFragment.newInstance(mPhoneNum, mPassword, 2, 10));
		mViewPagerRace = findView(R.id.race_listcount);
		mViewPagerRace.setAdapter(new CommonFragmentPagerAdapter(((MainCenterActivity) mActivity).getSupportFragmentManager(), fragmentsList));
		mViewPagerRace.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				switch (arg0) {
				case 0:
					mRadioButtonRace0.setChecked(true);
					break;
				case 1:
					mRadioButtonRace1.setChecked(true);
					break;
				case 2:
					mRadioButtonRace2.setChecked(true);
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	// ��ʼ��Radioѡ����
	private void initRadioButton() {
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int newIndex = 0;
				switch (checkedId) {
				case R.id.race_radio0:
					newIndex = 0;
					break;
				case R.id.race_radio1:
					newIndex = 1;
					break;
				case R.id.race_radio2:
					newIndex = 2;
					break;
				}
				mViewPagerRace.setCurrentItem(newIndex, true);
			}
		});
		mRadioButtonRace0.setChecked(true);
	}

	@Override
	public void onDestroy() {
		try{
			destroyAllItem();
		}catch(Exception e){
			
		}
		super.onDestroy();
	}

	public void destroyAllItem() {
		for (int i = 0; i < 3; i++) {
			Object objectobject = (RaceChildFragment) mViewPagerRace.getAdapter().instantiateItem(mViewPagerRace, i);
			if (objectobject != null)
				destroyItem(mViewPagerRace, i, objectobject);
		}
	}

	public void destroyItem(ViewGroup container, int position, Object object) {
		FragmentManager manager = ((Fragment) object).getFragmentManager();
		FragmentTransaction trans = manager.beginTransaction();
		trans.remove((Fragment) object);
		trans.commit();
	}
}
