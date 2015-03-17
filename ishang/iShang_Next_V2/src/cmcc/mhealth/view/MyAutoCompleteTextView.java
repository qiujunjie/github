package cmcc.mhealth.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import cmcc.mhealth.R;
import cmcc.mhealth.adapter.CommonAutoCompleteAdapter;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.slidingcontrol.RankFragment;

public class MyAutoCompleteTextView extends RelativeLayout {
//	private final String TAG = "MyAutoCompleteTextView";
	private Context context;
	private AutoCompleteTextView tv;
	private CommonAutoCompleteAdapter adapter;
	private OnNameReturnedListener listener;// 监听器

	public MyAutoCompleteTextView(Context context) {
		super(context);
		this.context = context;
	}

	public MyAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		initViews();
	}

	@SuppressLint("NewApi")
	private void initViews() {
		@SuppressWarnings("deprecation")
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
		tv = new AutoCompleteTextView(context);
		tv.setSingleLine(true);
		tv.setLayoutParams(params);
		tv.setBackgroundResource(R.drawable.umeng_fb_list_item);
		tv.setPadding(10, 0, 40, 0);
		switch (RankFragment.mCurrIndex) {
		case 0:
			tv.setHint("输入拼音、姓名或手机号");
			break;
		case 1:
			tv.setHint("输入班组名称、全拼、简拼");
			break;
		}
		tv.setBackgroundColor(Color.rgb(255, 255, 255));
		tv.setDropDownVerticalOffset(1);
		tv.setDropDownHeight(Common.dip2px(context, 200));
		tv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				tv.dismissDropDown();
				listener.onReturned("" + adapter.getItem(arg2), "" + adapter.getItemId(arg2));
			}
		});
		this.addView(tv);
	}

	public void setAdapter(CommonAutoCompleteAdapter adapter) {
		tv.setAdapter(adapter);
		this.adapter = adapter;
	}

	public void setThreshold(int threshold) {
		tv.setThreshold(threshold);
	}

	public AutoCompleteTextView getAutoCompleteTextView() {
		return tv;
	}

	public interface OnNameReturnedListener {
		abstract void onReturned(String name, String phonenum);
	}

	public void setOnNameReturnedListener(OnNameReturnedListener listener) {
		this.listener = listener;
	}
}
