package cmcc.mhealth.slidingcontrol;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import cmcc.mhealth.R;
import cmcc.mhealth.activity.Time2Activity;
import cmcc.mhealth.adapter.CommonFragmentPagerAdapter;
import cmcc.mhealth.basic.SampleFragment;
import cmcc.mhealth.view.MyWheelView;
import cmcc.mhealth.view.UnScrollViewPager;

/**
 * 通用SampleFragment的竞赛Fragment实例<br>
 * 
 * @author zy
 * 
 */
public class MedicineFragment extends SampleFragment {
	private RadioGroup mRadioGroup;
	private RadioButton mRadioButtonVital0, mRadioButtonVital1, mRadioButtonVital2;
	private UnScrollViewPager mViewPagerVG;
	private Button addVitalSign;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.sample_fragment_vitalsign, container, false);
		view.setTag("inflated");
		super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		return view;
	}

	// baseFragment初始化之后首先调用此方法
	@Override
	public void findViews() {
		super.findViews();
		mTextViewTitle.setText("用药提醒");
		addVitalSign = (Button) findView(R.id.button_add);
		addVitalSign.setBackgroundResource(R.drawable.set_state_ok);
		addVitalSign.setVisibility(View.VISIBLE);
		mRadioGroup = findView(R.id.medicine_group);
		mRadioButtonVital0 = findView(R.id.medicine_radio0);
		mRadioButtonVital1 = findView(R.id.medicine_radio1);
		mRadioButtonVital2 = findView(R.id.medicine_radio2);
	}

	// baseFragment初始化之后其次调用此方法
	@Override
	public void clickListner() {
		super.clickListner();

		initRadioBuittons();
		initViewPager();
		
		addVitalSign.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				startActivity(new Intent().setClass(mActivity, Time2Activity.class));
				final Dialog mMenuDialog = new Dialog(mActivity, R.style.dialog_fullscreen);
				mMenuDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				mMenuDialog.setCanceledOnTouchOutside(true);
				final MyWheelView myWheelView = new MyWheelView();
				View viewDialog = myWheelView.getview(mActivity);
				Button buttonOK = (Button) viewDialog.findViewById(R.id.btn_add_time);
				buttonOK.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						//添加
						String str = myWheelView.getSelectDate();
						BaseToast(str);
					}
				});
				Button buttonCancel = (Button) viewDialog.findViewById(R.id.btn_cancel);
				buttonCancel.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mMenuDialog.dismiss();
					}
				});
				Window window = mMenuDialog.getWindow();
				LayoutParams lay = window.getAttributes();  
				lay.width = LayoutParams.MATCH_PARENT;
				lay.height = LayoutParams.WRAP_CONTENT;
				window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示00 的位置
				window.setWindowAnimations(R.style.mystylebottom); // 添加动画
				viewDialog.setBackgroundResource(R.drawable.layout_bg);
				mMenuDialog.setContentView(viewDialog);
				mMenuDialog.show();
			}
		});
	}

	private void initViewPager() {
		ArrayList<Fragment> fragmentsList = new ArrayList<Fragment>();
			

		fragmentsList.add(SampleWebViewFragment.newInstance("http://phr.cmri.cn/datav2/account.do?action=medicine&flag=1"));
		fragmentsList.add(SampleWebViewFragment.newInstance("http://phr.cmri.cn/datav2/account.do?action=medicine&flag=2"));
		fragmentsList.add(SampleWebViewFragment.newInstance("http://phr.cmri.cn/datav2/account.do?action=medicine&flag=3"));
		mViewPagerVG = findView(R.id.medicine_parent);
		mViewPagerVG.setOffscreenPageLimit(3);
		
		
		
		mViewPagerVG.setAdapter(new CommonFragmentPagerAdapter(((MainCenterActivity) mActivity).getSupportFragmentManager(), fragmentsList));
		mViewPagerVG.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				switch (arg0) {
				case 0:
					mRadioButtonVital0.setChecked(true);
					break;
				case 1:
					mRadioButtonVital1.setChecked(true);
					break;
				case 2:
					mRadioButtonVital2.setChecked(true);
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

	private void initRadioBuittons() {
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int newIndex = 0;
				switch (checkedId) {
				case R.id.medicine_radio0:
					newIndex = 0;
					break;
				case R.id.medicine_radio1:
					newIndex = 1;
					break;
				case R.id.medicine_radio2:
					newIndex = 2;
					break;
				}
				mViewPagerVG.setCurrentItem(newIndex, true);
			}
		});
		mRadioButtonVital0.setChecked(true);
	}

	// baseFragment初始化之后最后调用此方法
	@Override
	public void loadLogic() {
		super.loadLogic();

	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onStop() {
		super.onStop();

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
			Object objectobject = mViewPagerVG.getAdapter().instantiateItem(mViewPagerVG, i);
			if (objectobject != null)
				destroyItem(mViewPagerVG, i, objectobject);
		}
	}

	public void destroyItem(ViewGroup container, int position, Object object) {
		FragmentManager manager = ((Fragment) object).getFragmentManager();
		FragmentTransaction trans = manager.beginTransaction();
		trans.remove((Fragment) object);
		trans.commit();
	}

}
